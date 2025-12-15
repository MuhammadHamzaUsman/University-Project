package com.example.TextEditor.Interpreter.statements;

public class Identifer extends ExpressionStatement {
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
