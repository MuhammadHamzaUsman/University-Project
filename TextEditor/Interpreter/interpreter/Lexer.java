package TextEditor.Interpreter.interpreter;

import java.util.LinkedList;
import java.util.List;

import TextEditor.Interpreter.tokens.Operators;
import TextEditor.Interpreter.tokens.Token;
import TextEditor.Interpreter.tokens.Tokens;

public class Lexer {
    public static List<Token> lexer(String input){
        int i = 0;
        char character;
        List<Token> tokens =  new LinkedList<>();

        while (i < input.length()) {
            character = input.charAt(i);

            if(character == ' ' || character == '\n' || character == '\t'){ 
                i++;
                continue;
            }

            if(Character.isDigit(character)){
                int start = i;
                i++;
                
                while(i < input.length() && Character.isDigit(input.charAt(i))){i++;}
                if(i < input.length() && input.charAt(i) == '.'){
                    i++;
                    while(i < input.length() && Character.isDigit(input.charAt(i))){i++;}
                }

                tokens.add(new Token(Tokens.NUMBER, input.substring(start, i)));
                continue;
            }
            
            if(Character.isAlphabetic(character) || character == '_'){
                int start = i;
                i++;

                while(i < input.length() && (Character.isAlphabetic(input.charAt(i)) || Character.isDigit(input.charAt(i)) || input.charAt(i) == '_')){
                    i++;
                }

                String identifer = input.substring(start, i);
                
                if(identifer.equals("true") || identifer.equals("false")){
                    tokens.add(new Token(Tokens.BOOLEAN, identifer));
                }
                else if(Tokens.isKeyWord(identifer)){
                    tokens.add(new Token(Tokens.getKeyWord(identifer), identifer));
                }
                else{tokens.add(new Token(Tokens.IDENTIFIER, identifer));}

                continue;
            }
            
            if(character == '('){
                tokens.add(new Token(Tokens.LEFT_PARANTHESES, "("));
                i++;
                continue;
            }

            if(character == ')'){
                tokens.add(new Token(Tokens.RIGHT_PARANTHESES, ")"));
                i++;
                continue;
            }

            if(character == '{'){
                tokens.add(new Token(Tokens.LEFT_BRACES, "{"));
                i++;
                continue;
            }

            if(character == '}'){
                tokens.add(new Token(Tokens.RIGHT_BRACES, "}"));
                i++;
                continue;
            }

            if(character == ','){
                tokens.add(new Token(Tokens.COMMA, ","));
                i++;
                continue;
            }

            if(character == ':'){
                tokens.add(new Token(Tokens.COLON, ":"));
                i++;
                continue;
            }

            int start = i;
            boolean found = false;
            for(String operator : Operators.sortedSymbols()){
                if(input.startsWith(operator, i)){
                    i += operator.length();
                    tokens.add(new Token(Tokens.OPERATOR, input.substring(start, i)));
                    found = true;
                    break;
                }
            }
            if(found){
                continue;
            }

            System.err.println("Lexer Error: Unrecongnized Token: " + character + "|");
            System.exit(1);
        }

        return tokens;
    }
}
