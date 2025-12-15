package com.example.TextEditor.Interpreter.statements;

public class NumericLiteral extends ExpressionStatement {
    private Double value;

    public NumericLiteral() {
    }

    public NumericLiteral(Double value) {
        this.value = value;
        super.type = StatementType.NUMERIC_LITERAL;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "NumericLiteral [value=" + value + "]";
    }
}
