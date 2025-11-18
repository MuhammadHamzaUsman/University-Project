package TextEditor.Interpreter.tokens;

import java.util.List;

public class TokenStream {
    private List<Token> tokens;
    private int index;

    public TokenStream(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Token peek(){
        try {
            return tokens.get(index);
        } catch (Exception e) {
            return null;
        }
    }

    public Token next(){
        try {
            return tokens.get(index++);
        } catch (Exception e) {
            return null;
        }
    }

    public Token expectType(Tokens token, String errorMessage){
        Token type = this.next();

        if(type == null || type.getTokenType() != token){
            //  Error
            System.err.println("Parse Error: " + errorMessage + " Got" + type + " Expecting: " + token);
            System.exit(1);
            return null;
        }
        return type;
    }

    public boolean isEnd(){
        return index >= tokens.size();
    }
}
