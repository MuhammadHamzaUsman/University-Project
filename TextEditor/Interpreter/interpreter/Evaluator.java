package TextEditor.Interpreter.interpreter;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import TextEditor.Interpreter.tokens.Operation;
import TextEditor.Interpreter.tokens.Operators;
import TextEditor.Interpreter.tokens.Token;
import TextEditor.Interpreter.tokens.Tokens;
import TextEditor.Interpreter.values.Value;
import TextEditor.Interpreter.values.ValueType;

public class Evaluator {
    private Variables variables;
    private Operation operation;

    public Evaluator(Variables variables) {
        this.variables = variables;
        this.operation = new Operation(variables);
    }

    public Value evalute(List<Token> postFixNotation){
        Stack<Token> outputStack = new Stack<>();
        Tokens tokenType;
        Operators operator;
        Value value;
        String name;
        List<Value> inputValues = new LinkedList<>();
                    
        for (Token token : postFixNotation) {
            tokenType = token.getTokenType();

            if(tokenType == Tokens.IDENTIFIER || tokenType == Tokens.BOOLEAN || tokenType == Tokens.NUMBER){
                outputStack.push(token);
            }
            else if(tokenType == Tokens.OPERATOR){
                operator = token.toOperator();

                if(Operators.isAssignment(operator)){
                    value = outputStack.pop().toValue();
                    name = outputStack.pop().getValue();

                    value = operation.performAssignmentOperation(value, name);
                    outputStack.push(value.toToken());
                }
                else if(Operators.isCompoundAssignment(operator)){
                    value = outputStack.pop().toValue();
                    name = outputStack.pop().getValue();

                    value = operation.performCompoundAssignmentOperation(operator, value, name);
                    outputStack.push(value.toToken());
                }
                else if(Operators.isMathematical(operator)){
                    inputValues.clear();

                    for (int i = 0; i < operator.requiredOperands(); i++) {
                        value = outputStack.pop().toValue();

                        if(value.getValueType() != ValueType.DOUBLE){
                            // Error
                        }
                        else{inputValues.addFirst(value);}
                    }

                    outputStack.add(operation.performMathematicalOperation(operator, inputValues).toToken());
                }
                else if(Operators.isRelational(operator)){
                    inputValues.clear();

                    for (int i = 0; i < operator.requiredOperands(); i++) {
                        value = outputStack.pop().toValue();

                        if(value.getValueType() != ValueType.DOUBLE){
                            // Error
                        }
                        else{inputValues.addFirst(value);}
                    }

                    outputStack.add(operation.performRelationalOperation(operator, inputValues).toToken());
                }
                else if(Operators.isLogical(operator)){
                    inputValues.clear();

                    for (int i = 0; i < operator.requiredOperands(); i++) {
                        value = outputStack.pop().toValue();

                        if(value.getValueType() != ValueType.BOOLEAN){
                            // Error
                        }
                        else{inputValues.addFirst(value);}
                    }

                    outputStack.add(operation.performLogicalOperation(operator, inputValues).toToken());
                }
            }
        }

        return outputStack.pop().toValue();
    }

    public List<Token> subsituteVaraiblesValue(List<Token> postfixNotation){
        int start = 0;

        Operators operators = Operators.getOperator(postfixNotation.getLast().getValue());

        if(operators == null){
            // Error
            return null;
        }

        if(!Operators.isAssignment(operators)){
            start = 1;
        }

        Token token;
        for (int i = start; i < postfixNotation.size(); i++) {
            token = postfixNotation.get(i);

            if(token.getTokenType() == Tokens.IDENTIFIER){
                if(variables.isVaraibleDeclared(token.getValue()) && variables.isVaraibleIntialized(token.getValue())){
                    Value value = variables.getValue(token.getValue());

                    postfixNotation.set(i, value.toToken());
                }
                else{
                    // Error
                }
            }
        }

        return postfixNotation;
    }

    public Variables getVariables() {
        return variables;
    }

    public void setVariables(Variables variables) {
        this.variables = variables;
    }
}
