package TextEditor.Interpreter.tokens;

import TextEditor.Interpreter.values.Value;

public class Token {
    private Tokens tokenType;
    private String value;
    
    public Token(Tokens tokenType, String value) {
        this.tokenType = tokenType;
        this.value = value;
    }

    public Tokens getTokenType() {
        return tokenType;
    }

    public void setTokenType(Tokens tokenType) {
        this.tokenType = tokenType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Value toValue(){
        if(value.equals("true")){
            return Value.of(true);
        }
        else if(value.equals("false")){
            return Value.of(false);
        }
        else{
            try {
                return Value.of(Double.parseDouble(value));
            } catch (Exception e) {
                // TODO: handle exception
                return Value.of(0.0);
            }
        }
    }

    public Operators toOperator(){
        return Operators.getOperator(value);
    }

    @Override
    public String toString() {
        return "Token [tokenType=" + tokenType + ", value=" + value + "]";
    }
}
