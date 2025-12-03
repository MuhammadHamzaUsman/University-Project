package com.example.TextEditor.Interpreter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import com.example.TextEditor.Interpreter.interpreter.Interpreter;
import com.example.TextEditor.Interpreter.interpreter.Lexer;
import com.example.TextEditor.Interpreter.interpreter.Parser;
import com.example.TextEditor.Interpreter.interpreter.Variables;
import com.example.TextEditor.Interpreter.statements.Statement;
import com.example.TextEditor.Interpreter.tokens.Token;
import com.example.TextEditor.Interpreter.values.Value;
import com.example.shape.Colors;
import com.example.shape.Shape;
import com.example.shape.Size;

public class CodeParser {
    public static void main(String[] args) throws FileNotFoundException {
        // Interpreter.executeProgramInteractively("app\\src\\main\\java\\com\\example\\TextEditor\\Interpreter\\Test.txt");

        // reading code
        StringBuilder input = new StringBuilder();
        Scanner inputS = new Scanner(new File("C:\\Users\\Dell\\Documents\\ForgeVoxel\\app\\src\\main\\java\\com\\example\\TextEditor\\Interpreter\\Test.txt"));
        while(inputS.hasNext()){
            input.append("\n").append(inputS.nextLine());
        }

        List<Token> tokens = Lexer.lexer(input.toString());

        Variables variables = new Variables(null);
        
        // Colors
        Colors[] colors = Colors.values();
        for (int  i = 0; i < colors.length; i++) {
            variables.declareVariable(colors[i].name(), Value.of(i));
        }

        // Size
        Size[] sizes = Size.values();
        for (int  i = 0; i < sizes.length; i++) {
            variables.declareVariable(sizes[i].name(), Value.of(i));
        }

        // Shape
        Shape[] shapes = Shape.values();
        for (int  i = 0; i < shapes.length; i++) {
            variables.declareVariable(shapes[i].name(), Value.of(i));
        }

        Parser parse = new Parser(variables, tokens);
        Statement program = parse.parseProgram();

        Interpreter interpreter = new Interpreter(variables);
        Value value = interpreter.executeProgram(program);
        
        
        inputS.close();
    }
}
