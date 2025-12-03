package com.example.TextEditor.Interpreter.statements;

import java.util.Map;

public class MapLiteral extends ExpressionStatement {
    private Map<Identifer, ExpressionStatement> map;

    public MapLiteral(Map<Identifer, ExpressionStatement> map) {
        this.map = map;
        super.type = StatementType.MAP_LITERAL;
    }

    public Map<Identifer, ExpressionStatement> getMap() {
        return map;
    }

    public void setMap(Map<Identifer, ExpressionStatement> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "MapLiteral [\n\tmap=" + map.toString().replace("\n", "\n\t") + "\n]";
    }
}
