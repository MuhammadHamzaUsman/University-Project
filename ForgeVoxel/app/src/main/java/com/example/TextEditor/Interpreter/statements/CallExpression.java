package com.example.TextEditor.Interpreter.statements;

import java.util.Arrays;

public class CallExpression extends ExpressionStatement{
    private ExpressionStatement[] args;
    private ExpressionStatement calle;

    public CallExpression(ExpressionStatement calle, ExpressionStatement[] args) {
        this.args = args;
        this.calle = calle;
        super.type = StatementType.CALL_EXPRESSION;
    }

    public ExpressionStatement[] getArgs() {
        return args;
    }

    public void setArgs(ExpressionStatement[] args) {
        this.args = args;
    }

    public ExpressionStatement getCalle() {
        return calle;
    }

    public void setCalle(ExpressionStatement calle) {
        this.calle = calle;
    }

    @Override
    public String toString() {
        return "CallExpression [\n\targs=" + 
            Arrays.toString(args).replace("\n", "\n\t") + 
            "\n\tcalle=" + calle.toString().replace("\n", "\n\t") + "\n]";
    }
}
