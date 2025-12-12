package com.example.io;

import com.example.TextEditor.Interpreter.statements.Statement;
import com.example.shape.Puzzel;
import com.example.shape.VoxelGrid;
import com.jme3.app.SimpleApplication;

import javafx.scene.image.Image;

public class PuzzelLevel {
    public String levelName;
    public int width;
    public int height;
    public int depth;
    public Image image;
    public String imagePath;
    public Statement targetFunction;
    public String userFunction;
    public String filePath;
    public boolean completed;

    public PuzzelLevel(){}

    public PuzzelLevel(String levelName, int width, int height, int depth, Image image, Statement targetFunction,
            String userFunction, String filePath, boolean completed) {
        this.levelName = levelName;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.image = image;
        this.targetFunction = targetFunction;
        this.userFunction = userFunction;
        this.filePath = filePath;
        this.completed = completed;
    }

    public PuzzelLevel(String levelName, int width, int height, int depth, String imagePath, Statement targetFunction,
            String userFunction, String filePath, boolean completed) {
        this.levelName = levelName;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.imagePath = imagePath;
        this.targetFunction = targetFunction;
        this.userFunction = userFunction;
        this.filePath = filePath;
        this.completed = completed;
    }

    public Puzzel toPuzzel(SimpleApplication app){
        VoxelGrid targetGrid = new VoxelGrid(app, width, height, depth, "target-" + levelName);
        targetGrid.intializeBorder(app.getAssetManager());
        VoxelGrid userGrid = new VoxelGrid(app, width, height, depth, "user-" + levelName);
        userGrid.intializeBorder(app.getAssetManager());
        Puzzel puzzel = new Puzzel(targetGrid, targetFunction, userGrid, levelName);
        puzzel.setComplete(completed);
        return puzzel;
    }

    @Override
    public String toString() {
        return "PuzzelLevel [levelName=" + levelName + ", width=" + width + ", height=" + height + ", depth=" + depth
                + ", image=" + image + ", imagePath=" + imagePath + ", targetFunction=" + targetFunction
                + ", userFunction=" + userFunction + ", filePath=" + filePath + ", completed=" + completed + "]";
    }
}