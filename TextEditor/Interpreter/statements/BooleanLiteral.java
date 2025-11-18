package TextEditor.Interpreter.statements;

public class BooleanLiteral extends ExpressionStatement {
    private boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
        super.type = StatementType.BOOLEAN_LITERAL;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BooleanLiteral [value=" + value + "]";
    }
}