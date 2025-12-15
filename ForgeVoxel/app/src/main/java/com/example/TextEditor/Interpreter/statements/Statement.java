package com.example.TextEditor.Interpreter.statements;

import java.io.Serializable;

public abstract class Statement implements Serializable{
    protected StatementType type;

    public Statement(){}

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