package com.example.TextEditor.Interpreter.statements;

import com.example.TextEditor.Interpreter.tokens.Operators;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AssignmentExpression extends ExpressionStatement {
    
    @JsonProperty("assignee")
    private ExpressionStatement assignee;
    
    @JsonProperty("value")
    private ExpressionStatement value;
    
    @JsonProperty("operator")
    private Operators operator;

    public AssignmentExpression(){}

    public AssignmentExpression(ExpressionStatement assignee, ExpressionStatement value, Operators operator) {
        this.assignee = assignee;
        this.value = value;
        this.operator = operator;
        super.type = StatementType.ASSIGNMENT_EXPRESSION;
    }

    public ExpressionStatement getAssignee() {
        return assignee;
    }

    public void setAssignee(ExpressionStatement assignee) {
        this.assignee = assignee;
    }

    public ExpressionStatement getValue() {
        return value;
    }

    public void setValue(ExpressionStatement value) {
        this.value = value;
    }

    public Operators getOperator() {
        return operator;
    }

    public void setOperator(Operators operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "AssignmentExpression [\n\tassignee=" + 
            assignee.toString().replace("\n", "\n\t") + 
            "\n\tvalue=" + value.toString().replace("\n", "\n\t") +
            "\n\toperator=" + operator + "\n]";
    }

}
