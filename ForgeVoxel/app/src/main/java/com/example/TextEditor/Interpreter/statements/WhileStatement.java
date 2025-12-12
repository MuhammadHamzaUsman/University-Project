package com.example.TextEditor.Interpreter.statements;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WhileStatement extends Statement {
    @JsonProperty("condition")
    private ExpressionStatement condition;
    @JsonProperty("body")
    private BlockStatement body;
    
    public WhileStatement() {
    }

    public WhileStatement(ExpressionStatement condition, BlockStatement body) {
        this.condition = condition;
        this.body = body;
        super.type = StatementType.WHILE_STATEMENT;
    }

    public ExpressionStatement getCondition() {
        return condition;
    }

    public void setCondition(ExpressionStatement condition) {
        this.condition = condition;
    }

    public BlockStatement getBody() {
        return body;
    }

    public void setBody(BlockStatement body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "WhileStatement [\n\tcondition=" + condition.toString().replace("\n", "\n\t") + 
            "\n\tbody=" + body.toString().replace("\n", "\n\t") + "\n]";
    }
}
