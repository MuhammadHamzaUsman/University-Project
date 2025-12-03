package com.example.TextEditor.Interpreter.statements;

public class NullLiteral extends ExpressionStatement {
    public NullLiteral() {
        super.type = StatementType.NULL_LITERAL;
    }

    @Override
    public String toString() {
        return "NullLiteral [null]";
    }
}