package com.example.TextEditor.Interpreter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import com.example.TextEditor.Interpreter.interpreter.Lexer;
import com.example.TextEditor.Interpreter.interpreter.Parser;
import com.example.TextEditor.Interpreter.statements.Statement;
import com.example.TextEditor.Interpreter.tokens.Token;

public class CodeParser {
    public static Statement compileProgram(File function) throws FileNotFoundException {
        StringBuilder input = new StringBuilder();
        Scanner inputS = new Scanner(function);

        while(inputS.hasNext()){
            input.append("\n").append(inputS.nextLine());
        }

        List<Token> tokens = Lexer.lexer(input.toString());

        Parser parse = new Parser(null, tokens);
        Statement program = parse.parseProgram();
        
        inputS.close();

        return program;
    }
}
