package com.example.TextEditor.Interpreter.values;

import java.util.Arrays;

import com.example.TextEditor.Interpreter.interpreter.Variables;
import com.example.TextEditor.Interpreter.statements.Statement;

public class UserDefinedFunction {
    private String name;
    private String[] parameters;
    private Variables variables;
    private Statement[] body;
    
    public UserDefinedFunction(String name, String[] parameters, Variables variables, Statement[] body) {
        this.name = name;
        this.parameters = parameters;
        this.variables = variables;
        this.body = body;
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

    public Variables getVariables() {
        return variables;
    }

    public void setVariables(Variables variables) {
        this.variables = variables;
    }

    public Statement[] getBody() {
        return body;
    }

    public void setBody(Statement[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "UserDefinedFunction [\n\tname=" + name + 
            "\n\tparameters=" + Arrays.toString(parameters).replace("\n", "\n\t") + 
            "\n\tvariables=" + variables.toString().replace("\n", "\n\t") + 
            "\n\tbody=" + Arrays.toString(body).replace("\n", "\n\t") + 
            "\n\t]";
    }
}
