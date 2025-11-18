package TextEditor.Interpreter.statements;

public class TupleLiteral extends ExpressionStatement {
    private ExpressionStatement[] expr;

    public TupleLiteral(ExpressionStatement[] expr) {
        this.expr = expr;
        super.type = StatementType.TUPLE_LITERAL;
    }

    public ExpressionStatement[] getExprs() {
        return expr;
    }

    public void setExprs(ExpressionStatement[] expr) {
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "TupleLiteral [\n\texpr=" + expr.toString().replace("\n", "\n\t") + "\n]";
    }
}
