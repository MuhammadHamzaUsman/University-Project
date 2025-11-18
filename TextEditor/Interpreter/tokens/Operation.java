package TextEditor.Interpreter.tokens;
import java.util.List;

import TextEditor.Interpreter.interpreter.Variables;
import TextEditor.Interpreter.values.Value;
import TextEditor.Interpreter.values.ValueType;

public class Operation {
    private Variables variables;

    public Operation(Variables variables){
        this.variables = variables;
    }

    public Value performEqualityOperation(Operators operator, List<Value> inputs){

        if (inputs.size() != 2 ) {
            // Error
        }

        Value v1 = inputs.get(0);
        Value v2 = inputs.get(1);

        if(v1.getValueType() != v2.getValueType()){
            if (operator == Operators.NOT_EQUALS) {
                return Value.of(true);
            }
            return Value.of(false);
        }
        
        boolean result = false;
        switch (v1.getValueType()) {
            case ValueType.DOUBLE: result = Double.compare(v1.getDoubleValue(), v2.getDoubleValue()) == 0; break;
            case ValueType.BOOLEAN: result = v1.getBoolValue().equals(v2.getBoolValue()); break;
            case ValueType.NULL: result = true; break;
            case ValueType.FUNCTION: result = v1.getFunction() == v2.getFunction(); break;
            case ValueType.USER_FUNCTION: result = v1.getUserFunction() == v2.getUserFunction(); break;
            default: result = false; break;
        }

        if(operator == Operators.NOT_EQUALS){
            result = !result;
        }

        return Value.of(result);
    }

    public Value performMathematicalOperation(Operators operator, List<Value> inputs){

        if (inputs.size() != 2 ) {
            // Error
        }

        for (Value value : inputs) {
            if(value.getValueType() != ValueType.DOUBLE){
                // Error
                return Value.of(0);
            }
        }

        double d1 = inputs.get(0).getDoubleValue();
        double d2 = inputs.get(1).getDoubleValue();

        switch (operator) {
            case PLUS: return Value.of(d1 + d2);
            case MINUS: return Value.of(d1 - d2);
            case MULTIPLY: return Value.of(d1 * d2);
            case MODULUS: return Value.of(d1 % d2);
            case DIVIDE: return Value.of(d1 / d2);
            case POWER: return Value.of(Math.pow(d1, d2));
            default: return Value.of(0.0);
        }
    }

    public Value performRelationalOperation(Operators operator, List<Value> inputs){

        if (inputs.size() != 2 ) {
            // Error
        }

        for (Value value : inputs) {
            if(value.getValueType() != ValueType.DOUBLE){
                // Error
                return Value.of(false);
            }
        }

        double d1 = inputs.get(0).getDoubleValue();
        double d2 = inputs.get(1).getDoubleValue();

        switch (operator) {
            case GREATER: return Value.of(Double.compare(d1, d2) > 0);
            case LESS: return Value.of(Double.compare(d1, d2) < 0);
            case GREATER_OR_EQUAL: return Value.of(Double.compare(d1, d2) >= 0);
            case LESS_OR_EQUAL: return Value.of(Double.compare(d1, d2) <= 0);
            case EQUALS: return Value.of(Double.compare(d1, d2) == 0);
            case NOT_EQUALS: return Value.of(Double.compare(d1, d2) != 0);
            default: return Value.of(false);
        }
    }

    public Value performLogicalOperation(Operators operator, List<Value> inputs){

        if (inputs.size() > 2 ) {
            // Error
        }

        for (Value value : inputs) {
            if(value.getValueType() != ValueType.BOOLEAN){
                // Error
                return Value.of(false);
            }
        }

        switch (operator) {
            case NOT: return Value.of(!inputs.get(0).getBoolValue());
            case AND: return Value.of(inputs.get(0).getBoolValue() && inputs.get(1).getBoolValue());
            case OR: return Value.of(inputs.get(0).getBoolValue() || inputs.get(1).getBoolValue());
            default: return Value.of(false);
        }
    }

    public Value performAssignmentOperation(Value value, String variable){
        variables.declareVariable(variable, Value.ofNull());
        variables.assignValue(variable, value);

        return value;
    }

    public Value performCompoundAssignmentOperation(Operators operator, Value value, String variable){
        if(value.getValueType() != ValueType.DOUBLE){
            // Error
        }

        if(!variables.isVaraibleDeclared(variable) || !variables.isVaraibleIntialized(variable)){
            // Error
        }

        Value variableValue = variables.getValue(variable);

        if(variableValue.getValueType() != ValueType.DOUBLE){
            // Error
        }

        double d1 = variableValue.getDoubleValue();
        double d2 = value.getDoubleValue();

        switch (operator) {
            case PLUS_ASSIGNMENT:
                d1 += d2;
                break;
            case MINUS_ASSIGNMENT:
                d1 -= d2;
                break;
            case MULTIPLY_ASSIGNMENT:
                d1 *= d2;
                break;
            case DIVIDE_ASSIGNMENT:
                d1 /= d2;
                break;
            case MODULUS_ASSIGNMENT:
                d1 %= d2;
                break;
            default:
                break;
        }

        variableValue.setDoubleValue(d1);

        return variableValue;
    }
}
