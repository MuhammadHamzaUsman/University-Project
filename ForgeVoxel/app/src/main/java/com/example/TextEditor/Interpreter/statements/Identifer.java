package com.example.TextEditor.Interpreter.statements;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Identifer extends ExpressionStatement {
    @JsonProperty("name")
    private String name;

    public Identifer() {
    }

    public Identifer(String name) {
        this.name = name;
        super.type = StatementType.IDENTIFIER;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Identifer [name=" + name + "]";
    }
}
