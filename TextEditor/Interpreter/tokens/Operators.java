package TextEditor.Interpreter.tokens;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public enum Operators {
    ASSIGNMENT("=", 2, 0),// 0
    PLUS_ASSIGNMENT("+=", 2, 0), // 1
    MINUS_ASSIGNMENT("-=", 2, 0), // 2
    MULTIPLY_ASSIGNMENT("*=", 2, 0), // 3
    DIVIDE_ASSIGNMENT("/=", 2, 0), // 4
    MODULUS_ASSIGNMENT("%=", 2, 0), // 5
    GREATER(">", 2, 2), // 6
    LESS("<", 2, 2), // 7
    GREATER_OR_EQUAL(">=", 2, 2), // 8
    LESS_OR_EQUAL("<=", 2, 2), // 9
    EQUALS("==", 2, 3), // 10
    NOT_EQUALS("!=", 2, 3), // 11
    PLUS("+", 2, 4), // 12
    MINUS("-", 2, 4), // 13
    MULTIPLY("*", 2, 5), // 14
    DIVIDE("/", 2, 5), // 15
    MODULUS("%", 2, 5), // 16
    POWER("^", 2, 6), //  17
    AND("&&", 2, 1), // 18
    OR("||", 2, 1), // 19
    NOT("!", 1, 8), // 20
    L_PARANTHESES("(", 0, 7), // 21 
    R_PARATHESES(")", 0, 7); // 22

    private String symbol;
    private int requiredOperands;
    private int precedence;
    private static Map<String, Operators> symbols;
    private static List<String> orderedSymbols;

    static{
        symbols = new HashMap<>();

        for (Operators operators : Operators.values()) {
            symbols.put(operators.symbol, operators);
        }

        orderedSymbols = new LinkedList<>(symbols.keySet());
        orderedSymbols.sort(Comparator.comparingInt(String::length).reversed());
    }

    private Operators(String symbol, int requiredOperands, int precedence) {
        this.symbol = symbol;
        this.requiredOperands = requiredOperands;
        this.precedence = precedence;
    }

    public static boolean hasSymbol(String symbol){
        return symbols.containsKey(symbol);
    }

    public static boolean hasSymbol(char symbol){
        return symbols.containsKey("" + symbol);
    }

    public static Operators getOperator(String symbol){
        return symbols.get(symbol) ;
    }

    public static Operators getOperator(char symbol){
        return symbols.get("" + symbol);
    }

    public String symbol() {
        return symbol;
    }

    public int requiredOperands() {
        return requiredOperands;
    }

    public int precedence() {
        return precedence;
    }

    public static List<String> sortedSymbols(){
        return orderedSymbols;
    }

    public static boolean isAssignment(Operators operator){
        if(operator == null){return false;}
        return operator.ordinal() == 0;
    }
    public static boolean isCompoundAssignment(Operators operators){
        if(operators == null){return false;}
        return operators.ordinal() >= 1 && operators.ordinal() <= 5;
    }

    public static boolean isRelational(Operators operators){
        if(operators == null){return false;}
        return operators.ordinal() >= 6 && operators.ordinal() <= 9;
    }
    
    public static boolean isEquality(Operators operators){
        if(operators == null){return false;}
        return operators.ordinal() >= 10 && operators.ordinal() <= 11;
    }

    public static boolean isMathematical(Operators operators){
        if(operators == null){return false;}
        return operators.ordinal() >= 12 && operators.ordinal() <= 17;
    }

    public static boolean isLogical(Operators operators){
        if(operators == null){return false;}
        return operators.ordinal() >= 18 && operators.ordinal() <= 20;
    }

    public static Operators tokenToOperator(String token){
        return getOperator(token.replace(" ", "").split(":")[1]);
    }

    public static boolean isBinary(Operators operator){
        return operator.ordinal() < 20;
    }

    public static boolean isUnary(Operators operator){
        return operator.ordinal() == 20;
    }
}
