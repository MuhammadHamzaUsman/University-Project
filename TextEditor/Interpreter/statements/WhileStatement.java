package TextEditor.Interpreter.statements;

public class WhileStatement extends Statement {
    private ExpressionStatement condition;
    private BlockStatement body;
    
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
