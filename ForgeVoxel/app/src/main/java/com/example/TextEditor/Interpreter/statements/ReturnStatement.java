package com.example.TextEditor.Interpreter.statements;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReturnStatement extends Statement {
    @JsonProperty("valueToReturn")
    private ExpressionStatement valueToReturn;

    public ReturnStatement() {
    }

    public ReturnStatement(ExpressionStatement valueToReturn) {
        this.valueToReturn = valueToReturn;
        super.type = StatementType.RETURN_STATEMENT;
    }

    public ExpressionStatement getValueToReturn() {
        return valueToReturn;
    }

    public void setValueToReturn(ExpressionStatement valueToReturn) {
        this.valueToReturn = valueToReturn;
    }

    @Override
    public String toString() {
        return "ReturnStatement [\n\tvalueToReturn=" + valueToReturn.toString().replace("\n", "\n\t") + "]";
    }
}
