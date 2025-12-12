package com.example.TextEditor.Interpreter.statements;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "statementType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AssignmentExpression.class, name = "assignmentExpression"),
    @JsonSubTypes.Type(value = BinaryExpression.class, name = "binaryExpression"),
    @JsonSubTypes.Type(value = BlockStatement.class, name = "blockStatement"),
    @JsonSubTypes.Type(value = BooleanLiteral.class, name = "booleanLiteral"),
    @JsonSubTypes.Type(value = CallExpression.class, name = "callExpression"),
    @JsonSubTypes.Type(value = ExpressionStatement.class, name = "expressionStatement"),
    @JsonSubTypes.Type(value = FunctionDecalarationStatement.class, name = "functionDecalarationStatement"),
    @JsonSubTypes.Type(value = Identifer.class, name = "identifer"),
    @JsonSubTypes.Type(value = IfStatement.class, name = "ifStatement"),
    @JsonSubTypes.Type(value = MapLiteral.class, name = "mapLiteral"),
    @JsonSubTypes.Type(value = NullLiteral.class, name = "nullLiteral"),
    @JsonSubTypes.Type(value = NumericLiteral.class, name = "numericLiteral"),
    @JsonSubTypes.Type(value = ReturnStatement.class, name = "returnStatement"),
    @JsonSubTypes.Type(value = UnaryExpression.class, name = "unaryExpression"),
    @JsonSubTypes.Type(value = VarDeclaration.class, name = "varDeclaration"),
    @JsonSubTypes.Type(value = WhileStatement.class, name = "whileStatement")
})
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