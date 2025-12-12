package com.example.TextEditor.Interpreter.statements;

import com.example.TextEditor.Interpreter.tokens.Operators;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BinaryExpression extends ExpressionStatement {
    
    @JsonProperty("left")
    private ExpressionStatement left;
    
    @JsonProperty("right")
    private ExpressionStatement right;
    
    @JsonProperty("operator")
    private Operators operator;

    public BinaryExpression(){}

    public BinaryExpression(ExpressionStatement left, ExpressionStatement right, Operators operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
        super.type = StatementType.BINARY_EXPRESSION;
    }

    public ExpressionStatement getLeft() {
        return left;
    }

    public void setLeft(ExpressionStatement left) {
        this.left = left;
    }

    public ExpressionStatement getRight() {
        return right;
    }

    public void setRight(ExpressionStatement right) {
        this.right = right;
    }

    public Operators getOperator() {
        return operator;
    }

    public void setOperator(Operators operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "BinaryExpression [\n\tleft=" + left.toString().replace("\n", "\n\t") + 
            "\n\tright=" + right.toString().replace("\n", "\n\t") + 
            "\n\toperator=" + operator + "\n]";
    }
}
