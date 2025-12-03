package com.example.TextEditor.Interpreter.statements;

public abstract class Statement {
    protected StatementType type;

    public StatementType getType() {
        return type;
    }

    public void setType(StatementType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Statement [type=" + type + "]";
    }
}