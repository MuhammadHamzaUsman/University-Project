package TextEditor.Interpreter.statements;

public class IfStatement extends Statement{
    private ExpressionStatement condition;
    private Statement[] ifBlock;
    private Statement[] elseBlock;

    public IfStatement(ExpressionStatement condition, Statement[] ifBlock, Statement[] elseBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
        super.type = StatementType.IF_STATEMENT;
    }

    public boolean hasElseBlock(){
        return elseBlock != null;
    }

    public ExpressionStatement getCondition() {
        return condition;
    }

    public void setCondition(ExpressionStatement condition) {
        this.condition = condition;
    }

    public Statement[] getIfBlock() {
        return ifBlock;
    }

    public void setIfBlock(Statement[] ifBlock) {
        this.ifBlock = ifBlock;
    }

    public Statement[] getElseBlock() {
        return elseBlock;
    }

    public void setElseBlock(Statement[] elseBlock) {
        this.elseBlock = elseBlock;
    }

    @Override
    public String toString() {
        return "IfStatement [\n\tcondition=" + condition.toString().replace("\n", "\n\t") + 
            ", ifBlock=" + ifBlock.toString().replace("\n", "\n\t") + 
            ", elseBlock=" + elseBlock.toString().replace("\n", "\n\t") + "\n]";
    }
}
