package TextEditor.Interpreter.tokens;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum Tokens {
    NUMBER,
    BOOLEAN,
    IDENTIFIER,
    LEFT_PARANTHESES,
    RIGHT_PARANTHESES,
    COMMA,
    OPERATOR,
    IF,
    ELSE,
    LEFT_BRACES,
    RIGHT_BRACES,
    RETURN,
    VAR,
    WHILE,
    DEF,
    NULL,
    COLON;

    public String prefix(){
        return this.name() + ": ";
    }

    private final static Map<String, Tokens> KEYWORDS = Map.ofEntries(
        Map.entry("if", IF),
        Map.entry("else", ELSE),
        Map.entry("return", RETURN),
        Map.entry("var",  VAR),
        Map.entry("while", WHILE),
        Map.entry("def", DEF),
        Map.entry("null", NULL)
    );

    private final static Set<String> RESERVE_WORDS;

    static{
        RESERVE_WORDS = new HashSet<>();
        RESERVE_WORDS.addAll(KEYWORDS.keySet());
        RESERVE_WORDS.addAll(Functions.nativeFunctions.keySet());
        RESERVE_WORDS.add("true");
        RESERVE_WORDS.add("false");
    }

    public static boolean isKeyWord(String token){
        return KEYWORDS.get(token) != null;
    }

    public static boolean isReservedWord(String token){
        return RESERVE_WORDS.contains(token);
    }

    public static Tokens getKeyWord(String token){
        return KEYWORDS.get(token);
    }
}