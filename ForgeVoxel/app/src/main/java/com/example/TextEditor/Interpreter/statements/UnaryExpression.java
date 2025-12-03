package com.example.TextEditor.Interpreter.statements;

import com.example.TextEditor.Interpreter.tokens.Operators;

public class UnaryExpression extends ExpressionStatement {
    private ExpressionStatement operand;
    private Operators operator;

    public UnaryExpression(ExpressionStatement operand, Operators operator) {
        this.operand = operand;
        this.operator = operator;
        super.type = StatementType.UNARY_EXPRESSION;
    }

    public ExpressionStatement getOperand() {
        return operand;
    }

    public void setOperand(ExpressionStatement operand) {
        this.operand = operand;
    }

    public Operators getOperator() {
        return operator;
    }

    public void setOperator(Operators operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "BinaryExpression [\n\toperand=" + operand.toString().replace("\n", "\n\t") + "\n\toperator=" + operator + "\n]";
    }
}