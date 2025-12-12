package com.example.TextEditor.Interpreter.statements;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BooleanLiteral extends ExpressionStatement {
    @JsonProperty("value")
    private boolean value;

    public BooleanLiteral() {
    }

    public BooleanLiteral(boolean value) {
        this.value = value;
        super.type = StatementType.BOOLEAN_LITERAL;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BooleanLiteral [value=" + value + "]";
    }
}