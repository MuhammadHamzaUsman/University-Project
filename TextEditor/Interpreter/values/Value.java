package TextEditor.Interpreter.values;

import java.util.Arrays;
import java.util.List;

import TextEditor.Interpreter.interpreter.Variables;
import TextEditor.Interpreter.statements.FunctionDecalarationStatement;
import TextEditor.Interpreter.tokens.Token;
import TextEditor.Interpreter.tokens.Tokens;

public class Value {
    private ValueType valueType;
    private Double doubleValue;
    private Boolean boolValue;
    private FunctionCall function;
    private UserDefinedFunction userFunction;
    private String[] tuple;

    private Value(){}

    public static Value of(double value){
        Value value_ = new Value();
        value_.valueType = ValueType.DOUBLE;
        value_.doubleValue = value;
        return value_;
    }

    public static Value of(boolean value){
        Value value_ = new Value();
        value_.valueType = ValueType.BOOLEAN;
        value_.boolValue = value;
        return value_;
    }

    public static Value ofNull(){
        Value value_ = new Value();
        value_.valueType = ValueType.NULL;
        return value_;
    }

    public static Value ofFunction(FunctionCall functionCall){
        Value value_ = new Value();
        value_.valueType = ValueType.FUNCTION;
        value_.function = functionCall;
        return value_;
    }

    public static Value ofUserFunction(FunctionDecalarationStatement funDecstmt, Variables variables){
        Value value_ = new Value();
        UserDefinedFunction userFunction = new UserDefinedFunction(
            funDecstmt.getName(), funDecstmt.getParameters(), variables, funDecstmt.getBody());
        value_.valueType = ValueType.USER_FUNCTION;
        value_.userFunction = userFunction;
        return value_;
    }

    public static Value ofTuple(List<String> values){
        Value value_ = new Value();
        value_.valueType = ValueType.TUPLE;
        value_.tuple = values.toArray(new String[values.size()]);
        return value_;
    }

    public String[] getTuple() {
        return tuple;
    }

    public void setTuple(String[] tuple) {
        this.tuple = tuple;
    }

    public UserDefinedFunction getUserFunction() {
        return userFunction;
    }

    public FunctionCall getFunction(){
        return function;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Boolean getBoolValue() {
        return boolValue;
    }

    public void setBoolValue(Boolean boolValue) {
        this.boolValue = boolValue;
    }

    public Token toToken(){
        return new Token(
            valueType == ValueType.DOUBLE ? Tokens.NUMBER : Tokens.BOOLEAN,
            valueType == ValueType.DOUBLE ? doubleValue.toString() : boolValue.toString()
        );
    }

    public String getString(){
        switch (valueType) {
            case ValueType.BOOLEAN: return boolValue.toString();
            case ValueType.DOUBLE: return doubleValue.toString();
            case ValueType.NULL: return "null";
            case ValueType.TUPLE: return Arrays.toString(tuple);
            case ValueType.USER_FUNCTION: return userFunction.getName(); 
            default: return "Native Function";
        }
    }

    @Override
    public String toString() {
        return "Value [\n\tvalueType=" + valueType + ", doubleValue=" + doubleValue + ", boolValue=" + boolValue +
            "\n\tFunction: " + function + 
            "\n\tUserFunction: " + (userFunction == null ? "null" : userFunction.toString().replace("\n", "\n\t")) + 
            "\n\tTuple: " + (tuple == null ? "null" : Arrays.toString(tuple)) + "\n]";
    }
}
