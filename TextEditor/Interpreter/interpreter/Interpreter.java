package TextEditor.Interpreter.interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import TextEditor.Interpreter.statements.*;
import TextEditor.Interpreter.tokens.Functions;
import TextEditor.Interpreter.tokens.Operation;
import TextEditor.Interpreter.tokens.Operators;
import TextEditor.Interpreter.tokens.Token;
import TextEditor.Interpreter.tokens.Tokens;
import TextEditor.Interpreter.values.FunctionCall;
import TextEditor.Interpreter.values.UserDefinedFunction;
import TextEditor.Interpreter.values.Value;
import TextEditor.Interpreter.values.ValueType;

public class Interpreter{
    private Operation operation;
    private Variables variables;

    public Interpreter(Variables variables) {
        this.variables = variables;
        this.operation = new Operation(this.variables);
        
        variables.declareVariable("null", Value.ofNull());
        variables.declareVariable("true", Value.of(true));
        variables.declareVariable("false", Value.of(false));
        Functions.loadFunctions(variables);
    }

    public Value executeProgram(Statement ast){
        try{
            return this.interpret(ast, this.variables);
        }
        catch(ReturnExcpetion returnExcpetion){
            return returnExcpetion.getReturnValue();
        }
    }

    public static Value executeProgramInteractively(String filePath) throws FileNotFoundException{
        Scanner input = new Scanner(System.in);
        Scanner fileInput;
        StringBuilder strbldr = new StringBuilder();
        List<Token> tokens;
        Value lastValue = null;
        Parser parser;
        Variables variables;
        Interpreter interpreter;
        String inputS;

        System.out.println();
        while(true){
            inputS = input.next();
            if(inputS.equals("run")){
                fileInput = new Scanner(new File(filePath));
                while(fileInput.hasNext()){
                    strbldr.append("\n").append(fileInput.nextLine());
                }

                tokens = Lexer.lexer(strbldr.toString());
                strbldr.setLength(0);
                variables = new Variables(null);
                parser = new Parser(variables, tokens);
                interpreter = new Interpreter(variables);
                lastValue = interpreter.executeProgram(parser.parseProgram());
                System.out.println(lastValue);
                fileInput.close();
            }
            else if(inputS.equals("Exit")){
                break;
            }
        }

        input.close();

        return lastValue;
    }

    private Value interpret(Statement ast, Variables variables){
        switch (ast.getType()) {
            case StatementType.NUMERIC_LITERAL: 
                NumericLiteral numericStmt = (NumericLiteral)ast;
                return Value.of(numericStmt.getValue());
            case StatementType.BOOLEAN_LITERAL: 
                BooleanLiteral boolStmt = (BooleanLiteral)ast;
                return Value.of(boolStmt.getValue());
            case StatementType.NULL_LITERAL:
                return Value.ofNull();
            case StatementType.TUPLE_LITERAL:
                return evaluateTupleLiteral((TupleLiteral)ast, variables);
            case StatementType.BINARY_EXPRESSION:
                return evaluateBinaryExpression((BinaryExpression)ast, variables);
            case StatementType.IDENTIFIER:
                return evaluteIdentifier((Identifer)ast, variables);
            case StatementType.BLOCK_STATEMENT:
                return evaluteBlockStatement((BlockStatement)ast, variables);
            case StatementType.VAR_DECLARATION:
                return evaluateVariableDeclaration((VarDeclaration)ast, variables);
            case StatementType.ASSIGNMENT_EXPRESSION:
                return evaluateAssignmentExpression((AssignmentExpression)ast, variables);
            case StatementType.CALL_EXPRESSION:
                return evaluateCallExpression((CallExpression)ast, variables);
            case StatementType.UNARY_EXPRESSION:
                return evaluateUnaryExpression((UnaryExpression)ast, variables);
            case StatementType.IF_STATEMENT:
                return evaluateIfStatement((IfStatement)ast, variables);
            case StatementType.WHILE_STATEMENT:
                return evaluateWhileStatement((WhileStatement)ast, variables);
            case StatementType.RETURN_STATEMENT:
                return evaluateReturnStatement((ReturnStatement)ast, variables);
            case StatementType.FUNCTION_DECLARATION:
                return evaluateFunctionDeclarationStatement((FunctionDecalarationStatement)ast, variables);
            default: 
                System.err.println("This statement is not supported by interpreter: " + ast + ": " + ast.getType());
                System.exit(1);
                return Value.ofNull();
        }
    }

    private Value evaluateTupleLiteral(TupleLiteral tupleLiteral, Variables variables) {
        List<String> tuple = new LinkedList<>();
        ExpressionStatement[] exprs = tupleLiteral.getExprs();
        Value value;
        String name;
        Variables currentScope = variables;
        boolean match = false;

        for (ExpressionStatement expr : exprs) {
            if(expr.getType() == StatementType.IDENTIFIER){
                name = ((Identifer)expr).getName();
                
                while (!currentScope.isVaraibleDeclared(name)) {
                    if(currentScope.getParent() == null){
                        tuple.add(name);
                        match = true;
                        break;
                    }

                    currentScope = currentScope.getParent();
                }
            }

            if(match){continue;}

            value = interpret(expr, variables);
            
            switch (value.getValueType()) {
                case ValueType.BOOLEAN: tuple.add(value.getBoolValue().toString());
                    break;
                case ValueType.DOUBLE: tuple.add(value.getDoubleValue().toString());
                    break;
                case ValueType.NULL: tuple.add("null");
                    break;
                case ValueType.TUPLE: tuple.add(value.getTuple().toString());
                    break;
                default:
                    System.err.println("Invalid Datatype being inserted in tuple: " + value.getValueType());
                    System.exit(1);
                    break;
            }
        }

        return Value.ofTuple(tuple);
    }

    private Value evaluateFunctionDeclarationStatement(FunctionDecalarationStatement funDecStmt, Variables variables) {
        String funName = ((FunctionDecalarationStatement)funDecStmt).getName();

        if(Tokens.isReservedWord(funName)){
            System.err.println("Reserved Word can not be declared: " + funName);
            System.exit(1);
        }

        Value fun = Value.ofUserFunction(funDecStmt, variables);
        variables.declareVariable(funName, fun);
        return fun;
    }

    private Value evaluateReturnStatement(ReturnStatement returnStatement, Variables variables)
    throws ReturnExcpetion {
        Value value = interpret(returnStatement.getValueToReturn(), variables);
        throw new ReturnExcpetion(value);
    }

    private Value evaluateWhileStatement(WhileStatement whileStatement, Variables variables) {
        Value conditionValue = interpret(whileStatement.getCondition(), variables);

        if(conditionValue.getValueType() != ValueType.BOOLEAN){
            System.err.println("Interpreter Error: Expecting Boolean Value in while condition got: " + conditionValue.getValueType());
            System.exit(1);
        }

        Value lastValue = Value.ofNull();
        Variables whileScope = new Variables(variables);
        while (conditionValue.getBoolValue()) {
            lastValue = interpret(whileStatement.getBody(), whileScope);
            conditionValue = interpret(whileStatement.getCondition(), variables);
        }

        return lastValue;
    }

    private Value evaluateIfStatement(IfStatement ifStatement, Variables variables) {
        Value conditionValue = interpret(ifStatement.getCondition(), variables);

        if(conditionValue.getValueType() != ValueType.BOOLEAN){
            System.err.println("Interpreter Error: Expecting Boolean Value in if condition got: " + conditionValue.getValueType());
            System.exit(1);
        }

        if(conditionValue.getBoolValue() == true){
            Variables ifScope = new Variables(variables);
            Value lastValue = Value.ofNull();

            for(Statement statement : ifStatement.getIfBlock()){
                lastValue = interpret(statement, ifScope);
            }

            return lastValue;
        }
        else if(ifStatement.hasElseBlock()){
            Variables elseScope = new Variables(variables);
            Value lastValue = Value.ofNull();

            for(Statement statement : ifStatement.getElseBlock()){
                lastValue = interpret(statement, elseScope);
            }

            return lastValue;
        }
        
        return Value.ofNull();
    }

    private Value evaluateUnaryExpression(UnaryExpression expr, Variables variables) {
        Value operand = interpret(expr.getOperand(), variables);
        Operators operator = expr.getOperator();

        if(operator == Operators.NOT){
            if (operand.getValueType() != ValueType.BOOLEAN) {
                System.err.println("Interprter Error: Invalid Data Type " + 
                    operand.getValueType() +  " and " + " for Operation " + operator);
                System.exit(1);
            }

            return operation.performLogicalOperation(operator, List.of(operand));
        }
        else if(operator == Operators.MINUS){
            if (operand.getValueType() != ValueType.DOUBLE) {
                System.err.println("Interprter Error: Invalid Data Type " + 
                    operand.getValueType() +  " and " + " for Operation " + operator);
                System.exit(1);
            }

            return Value.of(-operand.getDoubleValue());
        }

        return operand;
    }

    private Value evaluateCallExpression(CallExpression callExpr, Variables variables) {
        String caller = ((Identifer)(callExpr.getCalle())).getName();
        Value value = variables.getValue(caller);

        if(value.getValueType() == ValueType.FUNCTION){
            FunctionCall funcCall = value.getFunction();

            if(funcCall.toString().equals("print")){
                List<String> args = new LinkedList<>();
                
                for (ExpressionStatement arg : callExpr.getArgs()) {
                    args.add(interpret(arg, variables).getString());
                }

                System.out.println(args.toString());
                return Value.ofNull();
            }

            if(callExpr.getArgs().length != funcCall.requiredArgs()){
                System.err.println("Number of arguments " + callExpr.getArgs().length + 
                    " not equal to number of paramteres " + funcCall.requiredArgs() + " in function: " + caller);
                System.exit(1);
            }

            Value[] args = new Value[callExpr.getArgs().length];

            for (int i = 0; i < args.length; i++) {
                args[i] = interpret(callExpr.getArgs()[i], variables);
                if(args[i].getValueType() != ValueType.DOUBLE){
                    System.err.println("Datatype of arguments must be double in function: " + caller + " One was: " + args[i].getValueType());
                    System.exit(1);
                }
            }

            try {
                return funcCall.execute(args);
            } catch (ReturnExcpetion e) {
                return e.getReturnValue();
            } catch (Exception e){
                System.err.println(e.getMessage());
                System.exit(1);
                return Value.ofNull();
            }
        }
        else if(value.getValueType() == ValueType.USER_FUNCTION){
            UserDefinedFunction userFunc = value.getUserFunction();

            if(callExpr.getArgs().length != userFunc.getParameters().length){
                System.err.println("Number of arguments " + callExpr.getArgs().length + 
                    " not equal to number of paramteres " + userFunc.getParameters().length + " in function: " + caller);
                System.exit(1);
            }

            Variables funcScope = new Variables(variables);
            String[] parametres = userFunc.getParameters();
            ExpressionStatement[] args = callExpr.getArgs();

            for (int i = 0; i < parametres.length; i++) {
                funcScope.declareVariable(parametres[i], interpret(args[i], variables));
            }

            Value result = Value.ofNull();
            for (Statement stmt : userFunc.getBody()) {
                try {
                    result = interpret(stmt, funcScope);
                } catch (ReturnExcpetion e) {
                    if(e.getReturnValue().getValueType() == ValueType.TUPLE){
                        System.err.println("Function can not return Tuple");
                        System.exit(1);
                    }

                    return  e.getReturnValue();
                }
            }

            return result;
        }
        else{
            System.err.println("Unkown Function: " + caller);
            System.exit(1);
            return Value.ofNull();
        }
    }

    private Value evaluateAssignmentExpression(AssignmentExpression expr, Variables variables) {
        if(expr.getAssignee().getType() != StatementType.IDENTIFIER){
            System.err.println("Invalid Left Hand Side for expression: " + expr);
            System.exit(1);
        }

        String varName = ((Identifer)expr.getAssignee()).getName();

        if(Tokens.isReservedWord(varName)){
            System.err.println("Reserved Word can not be assigned: " + varName);
            System.exit(1);
        }

        Value value = interpret(expr.getValue(), variables);
        Operators operator = expr.getOperator();
        double d1 = 0, d2 = 0;

        if(
            operator == Operators.PLUS_ASSIGNMENT ||
            operator == Operators.MINUS_ASSIGNMENT ||
            operator == Operators.MULTIPLY_ASSIGNMENT ||
            operator == Operators.DIVIDE_ASSIGNMENT ||
            operator == Operators.MODULUS_ASSIGNMENT 
        ){
            if(value.getValueType() != ValueType.DOUBLE){
                System.err.println("Invalid Operation for Operator " + operator + " Expected Double Got " + value.getValueType());
            }

            d1 = value.getDoubleValue();
            d2 = variables.getValue(varName).getDoubleValue();
        }

        switch (operator) {
            case Operators.ASSIGNMENT: 
                variables.assignValue(varName, value);
                break;
            case Operators.PLUS_ASSIGNMENT: 
                value.setDoubleValue(d1 + d2);
                variables.assignValue(varName, value);
                break;
            case Operators.MINUS_ASSIGNMENT: 
                value.setDoubleValue(d1 - d2);
                variables.assignValue(varName, value);
                break;
            case Operators.MULTIPLY_ASSIGNMENT: 
                value.setDoubleValue(d1 * d2);
                variables.assignValue(varName, value);
                break;
            case Operators.DIVIDE_ASSIGNMENT: 
                value.setDoubleValue(d1 / d2);
                variables.assignValue(varName, value);
                break;
            case Operators.MODULUS_ASSIGNMENT: 
                value.setDoubleValue(d1 * d2);
                variables.assignValue(varName, value);
                break;
            default:
                variables.assignValue(varName, value);
                break;
        }

        return value;
    }

    private Value evaluateVariableDeclaration(VarDeclaration varDeclaration, Variables variables) {
        Value value = Value.ofNull();

        String varName = ((VarDeclaration)varDeclaration).getName();

        if(Tokens.isReservedWord(varName)){
            System.err.println("Reserved Word can not be declared: " + varName);
            System.exit(1);
        }
        
        if(varDeclaration.getExpression() != null){
            value = interpret(varDeclaration.getExpression(), variables);
        }

        variables.declareVariable(varDeclaration.getName(), value);
        return value;
    }

    private Value evaluteIdentifier(Identifer identifer, Variables variables) {
        return variables.getValue(identifer.getName());
    }

    private Value evaluateBinaryExpression(BinaryExpression expr, Variables variables){

        Value left = interpret(expr.getLeft(), variables);
        Value right = interpret(expr.getRight(), variables);
        Operators operator = expr.getOperator();

        if(Operators.isMathematical(operator)){
            if (left.getValueType() != ValueType.DOUBLE || right.getValueType() != ValueType.DOUBLE ) {
                System.err.println("Interprter Error: Invalid Data Types " + 
                    left.getValueType() +  " and " + right.getValueType()  +
                    " for Operation " + operator);
                System.exit(1);
            }

            return operation.performMathematicalOperation(operator, List.of(left, right));
        }
        else if(Operators.isEquality(operator)){
            return operation.performEqualityOperation(operator, List.of(left, right));
        }
        else if(Operators.isRelational(operator)){
            if (left.getValueType() != ValueType.DOUBLE || right.getValueType() != ValueType.DOUBLE ) {
                System.err.println("Interprter Error: Invalid Data Types " + 
                    left.getValueType() +  " and " + right.getValueType()  +
                    " for Operation " + operator);
                System.exit(1);
            }

            return operation.performRelationalOperation(operator, List.of(left, right));
        }else if(Operators.isLogical(operator)){
            if (left.getValueType() != ValueType.BOOLEAN || right.getValueType() != ValueType.BOOLEAN ) {
                System.err.println("Interprter Error: Invalid Data Types " + 
                    left.getValueType() +  " and " + right.getValueType()  +
                    " for Operation " + operator);
                System.exit(1);
            }

            return operation.performLogicalOperation(operator, List.of(left, right));
        }

        return Value.ofNull();
    }

    private Value evaluteBlockStatement(BlockStatement stmt, Variables variables){
        Value lastValue = Value.ofNull();

        for (Statement statement : stmt.getStatements()) {
            lastValue = interpret(statement, variables);
        }

        return lastValue;
    }
}