package com.example.TextEditor.Interpreter.statements;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FunctionDecalarationStatement extends Statement{
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("parameters")
    private String[] parameters;
    
    @JsonProperty("body")
    private Statement[] body;
    
    public FunctionDecalarationStatement() {
    }

    public FunctionDecalarationStatement(String name, String[] parameters, Statement[] body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        super.type = StatementType.FUNCTION_DECLARATION;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }

    public Statement[] getBody() {
        return body;
    }

    public void setBody(Statement[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "FunctionDecalarationStatement [\n\tname=" + name + "\n\tparameters=" + Arrays.toString(parameters) + 
            "\n\tbody=" + Arrays.toString(body).replace("\n", "\n\t") + "\n]";
    }

    
}
