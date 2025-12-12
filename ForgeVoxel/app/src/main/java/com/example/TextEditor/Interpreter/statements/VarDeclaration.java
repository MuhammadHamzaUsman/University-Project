package com.example.TextEditor.Interpreter.statements;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VarDeclaration extends Statement {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("expr")
    private ExpressionStatement expr;

    public VarDeclaration() {
    }

    public VarDeclaration(String name, ExpressionStatement expr) {
        this.name = name;
        this.expr = expr;
        super.type = StatementType.VAR_DECLARATION;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExpressionStatement getExpression() {
        return expr;
    }

    public void setExpression(ExpressionStatement expr) {
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "VarDeclaration [\n\tname=" + name + "\n\texpr=" +
            (expr == null ? "null" : expr.toString().replace("\n", "\n\t")) + 
            "\n]";
    }
}
