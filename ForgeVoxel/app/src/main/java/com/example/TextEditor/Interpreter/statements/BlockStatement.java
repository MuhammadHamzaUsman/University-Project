package com.example.TextEditor.Interpreter.statements;

import java.util.LinkedList;
import java.util.List;

public class BlockStatement extends Statement {
    private List<Statement> statements;

    public BlockStatement() {
        this.statements = new LinkedList<>();
        super.type = StatementType.BLOCK_STATEMENT;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void addStatment(Statement stmt){
        statements.add(stmt);
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    public Statement[] getStatementsArray(){
        return statements.toArray(new Statement[statements.size()]);
    }

    @Override
    public String toString() {
        return "BlockStatement [\n\tstatements=" + statements.toString().replace("\n", "\n\t") + "\n]";
    }
}
