package TextEditor.Interpreter.interpreter;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import TextEditor.Interpreter.statements.*;
import TextEditor.Interpreter.tokens.Operators;
import TextEditor.Interpreter.tokens.Token;
import TextEditor.Interpreter.tokens.TokenStream;
import TextEditor.Interpreter.tokens.Tokens;
import TextEditor.Interpreter.values.Value;

public class Parser {
    private Variables variables = new Variables(null);
    private TokenStream stream;

    public Parser(Variables variables, List<Token> program){
        this.variables = variables;
        this.stream = new TokenStream(program);
    }

    public BlockStatement parseProgram(){
        BlockStatement block = new BlockStatement();

        while (!stream.isEnd()) {
            block.addStatment(parseStatement());
        }

        return block;
    }

    private Statement parseStatement() {
        switch (stream.peek().getTokenType()) {
            case Tokens.VAR: return parseVarDeclaration();
            case Tokens.IF: return parseIfStatement();
            case Tokens.WHILE : return parseWhileStatement();
            case Tokens.RETURN: return parseReturnStatment();
            case Tokens.DEF: return parseFunctionDeclarationStatement();
            default: return parseExpression();
        }
    }

    private Statement parseFunctionDeclarationStatement() {
        stream.next();

        String name = stream.expectType(
            Tokens.IDENTIFIER, 
            "Parse Error: Expecting identifier after def in function header"
        ).getValue();
        
        ExpressionStatement[] args = parseCallArgs();
        List<String> parameters = new LinkedList<>();

        for (ExpressionStatement arg : args) {
            if(arg.getType() != StatementType.IDENTIFIER){
                System.err.println("Expecting identifer for parameter name inside function header Got: " + arg);
                System.exit(1);
            }

            parameters.add(((Identifer)arg).getName());
        }

        stream.expectType(Tokens.LEFT_BRACES, "Expecting a opening brace for start of function declaration");
        List<Statement> body = new LinkedList<>();

        while(!stream.isEnd() && stream.peek().getTokenType() != Tokens.RIGHT_BRACES){
            body.add(parseStatement());
        }

        stream.expectType(Tokens.RIGHT_BRACES, "Expecting a opening brace for start of function declaration");


        return new FunctionDecalarationStatement(
            name, 
            parameters.toArray(new String[parameters.size()]), 
            body.toArray(new Statement[body.size()]));
    }

    private Statement parseReturnStatment() {
        stream.next();

        ExpressionStatement value;
        if(stream.peek().getTokenType() == Tokens.LEFT_BRACES){
            value = parseTuple();
        }
        else{
            value = parseExpression();
        }
        
        return new ReturnStatement(value);
    }

    private ExpressionStatement parseTuple(){
        stream.expectType(Tokens.LEFT_BRACES, "Expected Left Brace at openening of Tuple");
        ExpressionStatement[] tuple;

        if(stream.peek().getTokenType() == Tokens.RIGHT_BRACES){
            tuple = new ExpressionStatement[0];
        }
        else{
            tuple = parseCallArguments();
        }

        stream.expectType(Tokens.RIGHT_BRACES, "Expected right brace at end of tuple");        
        return new TupleLiteral(tuple);
    }

    private Statement parseIfStatement() {
        stream.next();

        stream.expectType(Tokens.LEFT_PARANTHESES, "Parse Error: Expecting a Opening Parantheses after if");
        ExpressionStatement condition = parseExpression();
        stream.expectType(Tokens.RIGHT_PARANTHESES, "Parse Error: Expecting a Opening Parantheses after if condition");
        
        stream.expectType(Tokens.LEFT_BRACES, "Parse Error: Expecting a Opening Brace at start of if body");
        Statement[] ifBody = parseBlock().getStatementsArray();
        stream.expectType(Tokens.RIGHT_BRACES, "Parse Error: Expecting a Opening Brace at end of if body");
        
        Statement[] elseBody = null;
        if(stream.peek().getTokenType() == Tokens.ELSE){
            stream.next();

            if(stream.peek().getTokenType() == Tokens.IF){
                elseBody = new Statement[]{parseIfStatement()};
            }
            else{
                stream.expectType(Tokens.LEFT_BRACES, "Parse Error: Expecting a Opening Brace at start of else body");
                elseBody = parseBlock().getStatementsArray();
                stream.expectType(Tokens.RIGHT_BRACES, "Parse Error: Expecting a Opening Brace at end of else body");
            }
            
        }

        return new IfStatement(condition, ifBody, elseBody);
    }

    private BlockStatement parseBlock() {
        BlockStatement block = new BlockStatement();

        while (!stream.isEnd() && (stream.peek().getTokenType() != Tokens.RIGHT_BRACES)) {
            block.addStatment(parseStatement());
        }

        return block;
    }

    private Statement parseWhileStatement() {
        stream.next();

        stream.expectType(Tokens.LEFT_PARANTHESES, "Parse Error: Expecting a Opening Parantheses after while");
        ExpressionStatement condition = parseExpression();
        stream.expectType(Tokens.RIGHT_PARANTHESES, "Parse Error: Expecting a Opening Parantheses after while condition");
        
        stream.expectType(Tokens.LEFT_BRACES, "Parse Error: Expecting a Opening Brace at start of while body");
        BlockStatement whileBody = parseBlock();
        stream.expectType(Tokens.RIGHT_BRACES, "Parse Error: Expecting a Opening Brace at end of while body");

        return new WhileStatement(condition, whileBody);
    }

    private Statement parseVarDeclaration() {
        stream.next();
        
        String name = null;
        if(!stream.isEnd()){
            name = stream.expectType(Tokens.IDENTIFIER, "Expecting identifier").getValue();
        }
        else{
            System.err.println("Expecting identifier");
            System.exit(1);
        }

        Token token = null;
        if(!stream.isEnd()){
            token = stream.peek();
        }

        if(token != null && token.getTokenType() == Tokens.OPERATOR && Operators.isAssignment(token.toOperator())){
            stream.next();
            return new VarDeclaration(name, parseExpression());
        }
        else{
            return new VarDeclaration(name, null);
        }
    }

    private ExpressionStatement parseExpression() {
        return parseAssigmentExpression();
    }


    private ExpressionStatement parseAssigmentExpression() {
        ExpressionStatement left = parseCompoundAssigmentExpression();
        
        if(!stream.isEnd()){
            Token token = stream.peek();
            if(Operators.isAssignment(token.toOperator())){
                stream.next();

                ExpressionStatement value = parseAssigmentExpression();
                return new AssignmentExpression(left, value, token.toOperator());
            }
        }

        return left;
    }

    private ExpressionStatement parseCompoundAssigmentExpression() {
        ExpressionStatement left = parseOROperator();
        
        if(!stream.isEnd()){
            Token token = stream.peek();
            if(Operators.isCompoundAssignment(token.toOperator())){
                stream.next();

                ExpressionStatement value = parseAssigmentExpression();
                return new AssignmentExpression(left, value, token.toOperator());
            }
        }

        return left;
    }

    private ExpressionStatement parseRelationalExpression(){
        ExpressionStatement left = parseAdditiveExpression();
        ExpressionStatement right;
        Operators operator;

        while(!stream.isEnd() && (Operators.isRelational(stream.peek().toOperator()))){
            operator = stream.next().toOperator();
            right = parseAdditiveExpression();
            left = new BinaryExpression(left, right, operator);
        }

        return left;
    }

    private ExpressionStatement parseRelationalEqualityExpression(){
        ExpressionStatement left = parseRelationalExpression();
        ExpressionStatement right;
        Operators operator;

        while(!stream.isEnd() && Operators.isEquality(stream.peek().toOperator())){
            operator = stream.next().toOperator();
            right = parseRelationalExpression();
            left = new BinaryExpression(left, right, operator);
        }

        return left;
    }

    private ExpressionStatement parseAndOperator(){
        ExpressionStatement left = parseRelationalEqualityExpression();
        ExpressionStatement right;
        Operators operator;

        while(!stream.isEnd() && (stream.peek().toOperator() == Operators.AND)){
            operator = stream.next().toOperator();
            right = parseRelationalEqualityExpression();
            left = new BinaryExpression(left, right, operator);
        }

        return left;
    }

    private ExpressionStatement parseOROperator(){
        ExpressionStatement left = parseAndOperator();
        ExpressionStatement right;
        Operators operator;

        while(!stream.isEnd() && (stream.peek().toOperator() == Operators.OR)){
            operator = stream.next().toOperator();
            right = parseAndOperator();
            left = new BinaryExpression(left, right, operator);
        }

        return left;
    }

    private ExpressionStatement parseExponentialExpression(){
        ExpressionStatement left = parseUnaryExpression();
        ExpressionStatement right;
        Operators operator;

        if(!stream.isEnd() && (stream.peek().toOperator() == Operators.POWER)){
            operator = stream.next().toOperator();
            right = parseExponentialExpression();
            left = new BinaryExpression(left, right, operator);
        }

        return left;
    }

    private ExpressionStatement parseMutiplicativeExpression(){
        ExpressionStatement left = parseExponentialExpression();
        ExpressionStatement right;
        Operators operator;

        while(
            !stream.isEnd() && 
            (stream.peek().toOperator() == Operators.MULTIPLY || 
            stream.peek().toOperator() == Operators.DIVIDE ||
            stream.peek().toOperator() == Operators.MODULUS)
        ){
            operator = stream.next().toOperator();
            right = parseExponentialExpression();
            left = new BinaryExpression(left, right, operator);
        }

        return left;
    }

    private ExpressionStatement parseAdditiveExpression(){
        ExpressionStatement left = parseMutiplicativeExpression();
        ExpressionStatement right;
        Operators operator;

        while(!stream.isEnd() && (stream.peek().toOperator() == Operators.PLUS || stream.peek().toOperator() == Operators.MINUS)){
            operator = stream.next().toOperator();
            right = parseMutiplicativeExpression();
            left = new BinaryExpression(left, right, operator);
        }

        return left;
    }

    private ExpressionStatement parseUnaryExpression(){
        Operators operator = stream.peek().toOperator();

        if(!stream.isEnd() && (operator == Operators.NOT || operator == Operators.MINUS)){
            operator = stream.next().toOperator();
            return new UnaryExpression(parseCallExpression(), operator);
        }

        return parseCallExpression();
    }

    private ExpressionStatement parseCallExpression(){
        ExpressionStatement calle = parsePrimaryExpression();

        if(!stream.isEnd() && stream.peek().getTokenType() == Tokens.LEFT_PARANTHESES){
            calle = parseCallExpressionHelper(calle);
        }

        return calle;
    }

    private ExpressionStatement parseCallExpressionHelper(ExpressionStatement caller){
        ExpressionStatement callExpr = new CallExpression(caller, parseCallArgs());

        while(!stream.isEnd() && stream.peek().getTokenType() == Tokens.LEFT_PARANTHESES){
            callExpr = new CallExpression(callExpr, parseCallArgs());
        }

        return callExpr;
    }

    private ExpressionStatement[] parseCallArgs(){
        stream.expectType(Tokens.LEFT_PARANTHESES, "Expecting a Opening Paranthesis");
        ExpressionStatement[] args;

        if(stream.peek().getTokenType() == Tokens.RIGHT_PARANTHESES){
            args = new ExpressionStatement[0];
        }
        else{
            args = parseCallArguments();
        }

        stream.expectType(Tokens.RIGHT_PARANTHESES, "Expecting a closing parantheses");
        return args;
    }

    private ExpressionStatement[] parseCallArguments(){
        List<ExpressionStatement> args = new LinkedList<>();
        args.add(parseAssigmentExpression());

        while(!stream.isEnd() && stream.peek().getTokenType() == Tokens.COMMA){

            stream.next();
            args.add(parseAssigmentExpression());
        }

        return args.toArray(new ExpressionStatement[args.size()]);
    }

    private ExpressionStatement parsePrimaryExpression(){
        Token token = stream.peek();

        switch(token.getTokenType()){
            case Tokens.IDENTIFIER: return new Identifer(stream.next().getValue());
            case Tokens.NUMBER: return new NumericLiteral(Double.parseDouble(stream.next().getValue()));
            case Tokens.BOOLEAN: return new BooleanLiteral(Boolean.parseBoolean(stream.next().getValue()));
            case Tokens.NULL : stream.next(); return new NullLiteral();
            case Tokens.LEFT_PARANTHESES:
                stream.next();
                ExpressionStatement expression = parseExpression();
                stream.expectType(Tokens.RIGHT_PARANTHESES, "No Closing Parantheses");

                return expression;
            case Tokens.ELSE:
                System.err.println("else not assciated with any if statement");
                System.exit(1);
                return null;
            default: 
                System.err.println("Unexpected Token at location: " + token);
                System.exit(1);
                return null;
        }
    }

    public List<Token> infixToPostfix(List<Token> infixNotation){
        List<Token> postFixNotation = new LinkedList<>();
        Stack<Operators> operatorStack = new Stack<>();

        for (Token token : infixNotation) {
            if (token.getTokenType() == Tokens.NUMBER) {
                postFixNotation.add(token);
            }
            else if(token.getTokenType() == Tokens.BOOLEAN){
                postFixNotation.add(token);
            }
            else if(token.getTokenType() == Tokens.IDENTIFIER){
                variables.declareVariable(token.getValue(), Value.ofNull());
                postFixNotation.add(token);
            }
            else if(token.getTokenType() == Tokens.LEFT_PARANTHESES){
                operatorStack.push(Operators.L_PARANTHESES);
            }
            else if(token.getTokenType() == Tokens.RIGHT_PARANTHESES){
                while(!operatorStack.isEmpty() && operatorStack.peek() != Operators.L_PARANTHESES){
                    postFixNotation.add(new Token(Tokens.OPERATOR, operatorStack.pop().symbol()));
                }

                if(!operatorStack.isEmpty() && operatorStack.peek() == Operators.L_PARANTHESES){
                    operatorStack.pop();
                }
            }
            else if(token.getTokenType() == Tokens.COMMA){}
            else if(token.getTokenType() == Tokens.OPERATOR){
                Operators currentOperator = Operators.getOperator(token.getValue());

                if(operatorStack.isEmpty()){
                    operatorStack.push(currentOperator);
                }
                else if(currentOperator.precedence() > operatorStack.peek().precedence()){
                    operatorStack.push(currentOperator);
                }else{
                    
                    while(!operatorStack.isEmpty() && currentOperator.precedence() < operatorStack.peek().precedence()) {
                        postFixNotation.add(new Token(Tokens.OPERATOR, operatorStack.pop().symbol()));
                    }

                    operatorStack.push(currentOperator);
                }
            }
        }

        while(!operatorStack.isEmpty()) {
            postFixNotation.add(new Token(Tokens.OPERATOR, operatorStack.pop().symbol()));
        }

        return postFixNotation;
    } 
}
