package TextEditor.Interpreter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import TextEditor.Interpreter.interpreter.Interpreter;
import TextEditor.Interpreter.interpreter.Lexer;
import TextEditor.Interpreter.interpreter.Parser;
import TextEditor.Interpreter.interpreter.Variables;
import TextEditor.Interpreter.statements.Statement;
import TextEditor.Interpreter.tokens.Token;

public class CodeParser {
    public static void main(String[] args) throws FileNotFoundException {
        Interpreter.executeProgramInteractively("TextEditor\\Interpreter\\Test.txt");

        // StringBuilder input = new StringBuilder();
        // Scanner inputS = new Scanner(new File("Test.txt"));
        // Variables variables = new Variables(null);
        // while(inputS.hasNext()){
        //     input.append("\n").append(inputS.nextLine());
        // }
        // List<Token> tokens = Lexer.lexer(input.toString());
        // Parser parse = new Parser(variables, tokens);
        // Statement program = parse.parseProgram();
        // Interpreter interpreter = new Interpreter(variables);
        // inputS.close();
    }

    
}
