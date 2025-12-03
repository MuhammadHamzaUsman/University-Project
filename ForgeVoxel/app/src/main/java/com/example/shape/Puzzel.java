package com.example.shape;

import com.example.TextEditor.Interpreter.interpreter.Interpreter;
import com.example.TextEditor.Interpreter.interpreter.Lexer;
import com.example.TextEditor.Interpreter.interpreter.Parser;
import com.example.TextEditor.Interpreter.interpreter.Variables;
import com.example.TextEditor.Interpreter.statements.Statement;
import com.example.TextEditor.Interpreter.tokens.Functions;
import com.example.TextEditor.Interpreter.values.Value;
import com.example.shape.Voxel.PropertyInfoHolder;
import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;

public class Puzzel {
    private VoxelGridInterface targetGridFunction;
    private VoxelGrid targetGrid;
    private VoxelGrid userGrid;

    private double matchPercentage = 0.0;

    private int width;
    private int height;
    private int depth;

    private String name;

    private String code = "";

    private boolean isComplete = false;

    // interperteer setup
    private Variables variables = new Variables(null);
    private Parser parser = new Parser(variables, null);
    private Interpreter interpreter;

    private SimpleApplication app;

    public Puzzel(VoxelGrid targetGrid, VoxelGridInterface targetGridFunction, VoxelGrid userGrid, String name, SimpleApplication app) {
        
        if(!userGrid.isSizeSame(targetGrid)){
            throw new IllegalArgumentException("Grids does not have same size");
        }

        this.targetGridFunction = targetGridFunction;
        this.app = app;
        this.targetGrid = targetGrid;
        this.userGrid = userGrid;
        this.name = name;

        this.width = targetGrid.getWidth();
        this.height = targetGrid.getHeight();
        this.depth = targetGrid.getDepth();

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

        variables.declareVariable("null", Value.ofNull());
        variables.declareVariable("true", Value.of(true));
        variables.declareVariable("false", Value.of(false));
        Functions.loadFunctions(variables);

        interpreter = new Interpreter(variables);
    }

    public void intializeTargetGrid(Node targetNode){
        targetGrid.placeVoxels(targetGridFunction);
        targetGrid.draw();
        targetNode.attachChild(targetGrid.getGridNode());
    }

    public void intializeUserGrid(Node userNode){
        userNode.attachChild(userGrid.getGridNode());
    }

    public void updateUserGrid(String code){
        this.code = code;
        parser.setTokens(Lexer.lexer(this.code));
        Statement program = parser.parseProgram();

        interpreter.resetVaraibles();

        interpreter.getVariables().declareVariable("x", Value.of(0.0));
        interpreter.getVariables().declareVariable("y", Value.of(0.0));
        interpreter.getVariables().declareVariable("z", Value.of(0.0));
        Value xArg = interpreter.getVariables().getValue("x");
        Value yArg = interpreter.getVariables().getValue("y");
        Value zArg = interpreter.getVariables().getValue("z");

        userGrid.updateGrid( 
            (x, y, z, frame) -> {
                
                xArg.setDoubleValue((double)x);
                yArg.setDoubleValue((double)y);
                zArg.setDoubleValue((double)z);

                Value value = interpreter.executeProgram(program);

                return getVoxelFromValue(x, y, z, value);
            }, app
        );

        updateCompletion();
    }

    private Voxel getVoxelFromValue(int x, int y, int z, Value value){
        PropertyInfoHolder info = value.getVoxelProperties();

        if(info == null) {
            return null;
        }

        switch (info.shape) {
            case CUBE: return new Cube(x, y, z, MaterialEnum.MATTE, info.color, info.size, app.getAssetManager());
            case SPHERE: return new Sphere(x, y, z, MaterialEnum.MATTE, info.color, info.size, app.getAssetManager());
            case CYLINDER: return new Cylinder(x, y, z, MaterialEnum.MATTE, info.color, info.size, app.getAssetManager());
        }

        return null;
    }

    private void updateCompletion(){
        matchPercentage = targetGrid.matchPercentage(userGrid);
        if(Double.compare(matchPercentage, 1.0) == 0){
            isComplete = true;
        }
    }

    public void resetPuzzel(){
        matchPercentage = 0;
        isComplete = false;
        code = "";
        userGrid.getGridNode().detachAllChildren();
        userGrid.placeVoxels((x,y,z,frame) -> null);
    }

    public VoxelGridInterface getTargetGridFunction() {
        return targetGridFunction;
    }

    public void setTargetGridFunction(VoxelGridInterface targetGridFunction) {
        this.targetGridFunction = targetGridFunction;
    }

    public VoxelGrid getTargetGrid() {
        return targetGrid;
    }

    public void setTargetGrid(VoxelGrid targetGrid) {
        this.targetGrid = targetGrid;
    }

    public VoxelGrid getUserGrid() {
        return userGrid;
    }

    public void setUserGrid(VoxelGrid userGrid) {
        this.userGrid = userGrid;
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public Variables getVariables() {
        return variables;
    }

    public void setVariables(Variables variables) {
        this.variables = variables;
    }

    public Parser getParser() {
        return parser;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }

    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public SimpleApplication getApp() {
        return app;
    }

    public void setApp(SimpleApplication app) {
        this.app = app;
    }

    
}
