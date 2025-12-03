package com.example.shape;

import com.example.TextEditor.Interpreter.interpreter.RuntimeError;
import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;

public class VoxelGrid {
    private int height;
    private int width;
    private int depth;
    private Voxel[][][] grid;
    private Node gridNode;
    private String name;

    public VoxelGrid(int width, int height, int depth, String name) {
        this.height = height;
        this.width = width;
        this.depth = depth;
        this.grid = new Voxel[depth][height][width];
        this.name = (name == null) ? String.format("User-%d-%d-%d", width, height, depth) : name;
        this.gridNode = new Node(this.name);
        this.gridNode.setLocalTranslation(
            0 - (float)(width * Voxel.UNIT_SIZE) / 2, 
            0 - (float)(height * Voxel.UNIT_SIZE) / 2, 
            0 - (float)(depth * Voxel.UNIT_SIZE) / 2
        );
    }

    public void draw(){
        for (Voxel[][] voxelsPlane : grid) {
            for (Voxel[] voxelsArray : voxelsPlane) {
                for (Voxel voxel : voxelsArray) {
                    if(voxel != null){voxel.draw(gridNode);}
                }
            }
        }
        
        this.gridNode.setLocalTranslation(
            0 - (float)(width * Voxel.UNIT_SIZE) / 2, 
            0 - (float)(height * Voxel.UNIT_SIZE) / 2, 
            0 - (float)(depth * Voxel.UNIT_SIZE) / 2
        );
    }

    public void placeVoxels(VoxelGridInterface function){
        for (int z = 0; z < depth; z++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    grid[z][y][x] = function.determineVoxel(x, y, z, 0);
                }
            }
        }
    }

    public void updateGrid(VoxelGridInterface function, SimpleApplication app){
        // data update
        grid = new Voxel[depth][height][width];
        placeVoxels(function);

        // visual update
        app.enqueue(
            () -> {
                gridNode.detachAllChildren();
                draw();
                return null;
            }
        );
    } 

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Node getGridNode() {
        return gridNode;
    }

    public void setGridNode(Node gridNode) {
        this.gridNode = gridNode;
    }

    public boolean isSizeSame(VoxelGrid otherGrid){
        return (width == otherGrid.width) && (height == otherGrid.height) && (depth == otherGrid.depth);
    }

    public double matchPercentage(VoxelGrid othVoxelGrid){
        if (!isSizeSame(othVoxelGrid)) {
            throw new RuntimeError(this.name + " is not of same size as " + othVoxelGrid.name + ": " +
                width + "x" + height + "x" + depth + " to " + othVoxelGrid.width + "x" + othVoxelGrid.height + "x" + othVoxelGrid.depth
            );
        }

        int matchingCube = 0;
        Voxel voxel;
        Voxel otherVoxel;

        for (int z = 0; z < depth; z++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    voxel = grid[z][y][x];
                    otherVoxel = othVoxelGrid.grid[z][y][x];

                    if(voxel == null && otherVoxel == null){
                        matchingCube++;
                    }
                    else if(voxel != null && otherVoxel != null){
                        matchingCube += voxel.isSame(otherVoxel) ? 1 : 0;
                    }
                }
            }
        }

        return ((double) matchingCube) / (width * depth * height);
    }
}
