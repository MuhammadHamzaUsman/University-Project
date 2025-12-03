package com.example.TextEditor.Interpreter.interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.example.TextEditor.Interpreter.statements.*;
// import com.example.TextEditor.Interpreter.tokens.Functions;
import com.example.TextEditor.Interpreter.tokens.Operation;
import com.example.TextEditor.Interpreter.tokens.Operators;
import com.example.TextEditor.Interpreter.tokens.Token;
import com.example.TextEditor.Interpreter.tokens.Tokens;
import com.example.TextEditor.Interpreter.values.FunctionCall;
import com.example.TextEditor.Interpreter.values.UserDefinedFunction;
import com.example.TextEditor.Interpreter.values.Value;
import com.example.TextEditor.Interpreter.values.ValueType;

public class Interpreter{
    private Operation operation;
    private Variables variables;

    public Interpreter(Variables variables) {
        this.variables = new Variables(variables);
        this.operation = new Operation(this.variables);
        
        // variables.declareVariable("null", Value.ofNull());
        // variables.declareVariable("true", Value.of(true));
        // variables.declareVariable("false", Value.of(false));
        // Functions.loadFunctions(variables);
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
            else if(inputS.equals("exit")){
                break;
            }
        }

        input.close();

        return lastValue;
    }

    private Value interpret(Statement ast, Variables variables){
        switch (ast.getType()) {
            case NUMERIC_LITERAL: 
                NumericLiteral numericStmt = (NumericLiteral)ast;
                return Value.of(numericStmt.getValue());
            case BOOLEAN_LITERAL: 
                BooleanLiteral boolStmt = (BooleanLiteral)ast;
                return Value.of(boolStmt.getValue());
            case NULL_LITERAL:
                return Value.ofNull();
            case MAP_LITERAL:
                return evaluateMapLiteral((MapLiteral)ast, variables);
            case BINARY_EXPRESSION:
                return evaluateBinaryExpression((BinaryExpression)ast, variables);
            case IDENTIFIER:
                return evaluteIdentifier((Identifer)ast, variables);
            case BLOCK_STATEMENT:
                return evaluteBlockStatement((BlockStatement)ast, variables);
            case VAR_DECLARATION:
                return evaluateVariableDeclaration((VarDeclaration)ast, variables);
            case ASSIGNMENT_EXPRESSION:
                return evaluateAssignmentExpression((AssignmentExpression)ast, variables);
            case CALL_EXPRESSION:
                return evaluateCallExpression((CallExpression)ast, variables);
            case UNARY_EXPRESSION:
                return evaluateUnaryExpression((UnaryExpression)ast, variables);
            case IF_STATEMENT:
                return evaluateIfStatement((IfStatement)ast, variables);
            case WHILE_STATEMENT:
                return evaluateWhileStatement((WhileStatement)ast, variables);
            case RETURN_STATEMENT:
                return evaluateReturnStatement((ReturnStatement)ast, variables);
            case FUNCTION_DECLARATION:
                return evaluateFunctionDeclarationStatement((FunctionDecalarationStatement)ast, variables);
            default: 
                throw new RuntimeError("Interpreter Error: This statement is not supported by interpreter: " + ast + ": " + ast.getType());
        }
    }

    private Value evaluateMapLiteral(MapLiteral mapLiteral, Variables variables) {
        Map<String, Value> mapValue = new HashMap<>();
        Map<Identifer, ExpressionStatement> map = mapLiteral.getMap();

        for(Identifer identifer : map.keySet()){
            mapValue.put(identifer.getName(), interpret(map.get(identifer), variables));
        } 

        return Value.ofMap(mapValue);
    }

    private Value evaluateFunctionDeclarationStatement(FunctionDecalarationStatement funDecStmt, Variables variables) {
        String funName = ((FunctionDecalarationStatement)funDecStmt).getName();

        if(Tokens.isReservedWord(funName)){
            throw new RuntimeError("Interpreter Error: Reserved Functions can not be declared: " + funName);
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

    private Value evaluateWhileStatement(WhileStatement whileStatement, Variables variables) throws RuntimeError {
        Value conditionValue = interpret(whileStatement.getCondition(), variables);

        if(conditionValue.getValueType() != ValueType.BOOLEAN){
            throw new RuntimeError("Interpreter Error: Expecting Boolean Value in while condition got: " + conditionValue.getValueType());
        }

        Value lastValue = Value.ofNull();
        Variables whileScope = new Variables(variables);
        while (conditionValue.getBoolValue()) {
            lastValue = interpret(whileStatement.getBody(), whileScope);
            conditionValue = interpret(whileStatement.getCondition(), variables);
        }

        return lastValue;
    }

    private Value evaluateIfStatement(IfStatement ifStatement, Variables variables) throws RuntimeError {
        Value conditionValue = interpret(ifStatement.getCondition(), variables);

        if(conditionValue.getValueType() != ValueType.BOOLEAN){
            throw new RuntimeError("Interpreter Error: Expecting Boolean Value in if condition got: " + conditionValue.getValueType());
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

    private Value evaluateUnaryExpression(UnaryExpression expr, Variables variables) throws RuntimeError {
        Value operand = interpret(expr.getOperand(), variables);
        Operators operator = expr.getOperator();

        if(operator == Operators.NOT){
            if (operand.getValueType() != ValueType.BOOLEAN) {
                throw new RuntimeError("Interprter Error: Expecting Boolean got: " + operand.getValueType() + " for Operation " + operator);
            }

            return operation.performLogicalOperation(operator, List.of(operand));
        }
        else if(operator == Operators.MINUS){
            if (operand.getValueType() != ValueType.DOUBLE) {
                throw new RuntimeError("Interprter Error: Expecting Number/Double got: " + operand.getValueType() + " for Operation " + operator);
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
                throw new RuntimeError("Interperter Error: Expecting Number of arguments " + funcCall.requiredArgs() + 
                    " got " + callExpr.getArgs().length + " in function: " + caller);
            }

            Value[] args = new Value[callExpr.getArgs().length];

            for (int i = 0; i < args.length; i++) {
                args[i] = interpret(callExpr.getArgs()[i], variables);
                if(args[i].getValueType() != ValueType.DOUBLE){
                   throw new RuntimeError("Interperter Error: Expecting Number/Double in function: " + caller + " got " + args[i].getValueType());
                }
            }

            try {
                return funcCall.execute(args);
            } catch (ReturnExcpetion e) {
                return e.getReturnValue();
            } catch (Exception e){
                throw new RuntimeError("Interperter Error: " + e.getMessage());
            }
        }
        else if(value.getValueType() == ValueType.USER_FUNCTION){
            UserDefinedFunction userFunc = value.getUserFunction();

            if(callExpr.getArgs().length != userFunc.getParameters().length){
                throw new RuntimeError("Interperter Error: Expecting Number of arguments to be " + userFunc.getParameters().length + 
                    " got " + callExpr.getArgs().length + " in function: " + caller);
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
                    if(e.getReturnValue().getValueType() == ValueType.MAP){
                        throw new RuntimeError("Interperter Error: Function can not return Map");
                    }

                    return  e.getReturnValue();
                }
            }

            return result;
        }
        else{
            throw new RuntimeError("Interperter Error: Function not Declared: " + caller);
        }
    }

    private Value evaluateAssignmentExpression(AssignmentExpression expr, Variables variables) {
        if(expr.getAssignee().getType() != StatementType.IDENTIFIER){
            throw new RuntimeError("Interperter Error: Invalid Left Hand Side for expression: " + expr);
        }

        String varName = ((Identifer)expr.getAssignee()).getName();

        if(Tokens.isReservedWord(varName)){
            throw new RuntimeError("Interperter Error: Reserved Word can not be reassigned: " + varName);
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
                throw new RuntimeError("Interperter Error: Expected Double Got " + value.getValueType() + " Invalid Operation for Operator " + operator);
            }

            d1 = value.getDoubleValue();
            d2 = variables.getValue(varName).getDoubleValue();
        }

        switch (operator) {
            case ASSIGNMENT: 
                variables.assignValue(varName, value);
                break;
            case PLUS_ASSIGNMENT: 
                value.setDoubleValue(d1 + d2);
                variables.assignValue(varName, value);
                break;
            case MINUS_ASSIGNMENT: 
                value.setDoubleValue(d1 - d2);
                variables.assignValue(varName, value);
                break;
            case MULTIPLY_ASSIGNMENT: 
                value.setDoubleValue(d1 * d2);
                variables.assignValue(varName, value);
                break;
            case DIVIDE_ASSIGNMENT: 
                value.setDoubleValue(d1 / d2);
                variables.assignValue(varName, value);
                break;
            case MODULUS_ASSIGNMENT: 
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
           throw new RuntimeError("Interpreter Error: Reserved Word can not be redeclared: " + varName);
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

    private Value evaluateBinaryExpression(BinaryExpression expr, Variables variables) throws RuntimeError {

        Value left = interpret(expr.getLeft(), variables);
        Value right = interpret(expr.getRight(), variables);
        Operators operator = expr.getOperator();

        if(Operators.isMathematical(operator)){
            if (left.getValueType() != ValueType.DOUBLE || right.getValueType() != ValueType.DOUBLE ) {
                throw new RuntimeError("Interprter Error: Expecting Number/Double got: " + 
                    left.getValueType() + " and " + right.getValueType() +
                    " for Operation " + operator);
            }

            return operation.performMathematicalOperation(operator, List.of(left, right));
        }
        else if(Operators.isEquality(operator)){
            return operation.performEqualityOperation(operator, List.of(left, right));
        }
        else if(Operators.isRelational(operator)){
            if (left.getValueType() != ValueType.DOUBLE || right.getValueType() != ValueType.DOUBLE ) {
                throw new RuntimeError("Interprter Error: Expecting Number/Double " + 
                    left.getValueType() +  " and " + right.getValueType()  +
                    " for Operation " + operator);
            }

            return operation.performRelationalOperation(operator, List.of(left, right));
        }else if(Operators.isLogical(operator)){
            if (left.getValueType() != ValueType.BOOLEAN || right.getValueType() != ValueType.BOOLEAN ) {
                throw new RuntimeError("Interprter Error: Expecting Boolean got " + 
                    left.getValueType() +  " and " + right.getValueType()  +
                    " for Operation " + operator);
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

    public void resetVaraibles() {
        variables.getVaraibles().clear();
    }

    public Variables getVariables() {
        return variables;
    }
}