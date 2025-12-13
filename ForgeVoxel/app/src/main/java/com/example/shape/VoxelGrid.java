package com.example.shape;

import com.example.TextEditor.Interpreter.interpreter.RuntimeError;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;

public class VoxelGrid{
    private SimpleApplication app;
    private Gizmo gizmo;

    private int height;
    private int width;
    private int depth;
    private Voxel[][][] grid;
    private Node gridNode;
    private String name;

    private int xLimit;
    private int yLimit;
    private int zLimit;

    private int xDrawLimit;
    private int yDrawLimit;
    private int zDrawLimit;
    
    private Node border;

    private float radius;

    public VoxelGrid(int width, int height, int depth, String name) {
        this.height = height;
        this.width = width;
        this.depth = depth;
        this.grid = new Voxel[depth][height][width];
        this.name = (name == null) ? String.format("User-%d-%d-%d", width, height, depth) : name;
        this.gridNode = new Node(this.name);
        this.gridNode.setLocalTranslation(0, 0, 0);

        xLimit = width / 2;
        yLimit = height / 2;
        zLimit = depth / 2;

        radius = FastMath.sqrt(xLimit * xLimit + yLimit * yLimit + zLimit * zLimit);

        xDrawLimit = width;
        yDrawLimit = height;
        zDrawLimit = depth;
    }

    public VoxelGrid(SimpleApplication app, int height, int width, int depth, String name) {
        this(width, height, depth, name);
        this.app = app;
    }

    public void setApp(SimpleApplication app){
        this.app = app;
    }

    public void intializeBorder(AssetManager assetManager) {
        float borderWidth = (float)((width * Voxel.UNIT_SIZE) * 1.02);
        float borderHeight = (float)((height * Voxel.UNIT_SIZE) * 1.02);
        float borderDepth = (float)((depth * Voxel.UNIT_SIZE) * 1.02);

        border = Voxel.getborder("GridBorder", borderWidth, borderHeight, borderDepth, 0.08f, ColorRGBA.fromRGBA255(68, 75, 78, 255), assetManager);
        
        gridNode.attachChild(border);
    }

    public void draw(){
        Voxel voxel;

        for (int z = 0; z < zDrawLimit; z++) {
            for (int y = 0; y < yDrawLimit; y++) {
                for (int x = 0; x < xDrawLimit; x++) {
                    voxel = grid[z][y][x];
                    if(voxel != null){voxel.draw(gridNode, radius);}
                }
            }
        }

        gridNode.attachChild(border);
        if(gizmo != null) {gridNode.attachChild(gizmo.getGizmoNode());}
    }

    public void placeVoxels(VoxelGridInterface function){
        for (int z = 0; z < depth; z++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    grid[z][y][x] = function.determineVoxel(x - xLimit, y - yLimit, z - zLimit, 0);
                }
            }
        }
    }

    public void updateGrid(VoxelGridInterface function){
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

    public int getxLimit() {
        return xLimit;
    }

    public void setxLimit(int xLimit) {
        this.xLimit = xLimit;
    }

    public int getyLimit() {
        return yLimit;
    }

    public void setyLimit(int yLimit) {
        this.yLimit = yLimit;
    }

    public int getzLimit() {
        return zLimit;
    }

    public void setzLimit(int zLimit) {
        this.zLimit = zLimit;
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

    public void setXDrawLimit(int x){
        x += 2;
        
        xDrawLimit = Math.clamp(x, 0, width);
        // visual update
        app.enqueue(
            () -> {
                gridNode.detachAllChildren();
                draw();
                return null;
            }
        );
    }

    public void setYDrawLimit(int y){
        y += 2;
        
        yDrawLimit = Math.clamp(y, 0, height);
        // visual update
        app.enqueue(
            () -> {
                gridNode.detachAllChildren();
                draw();
                return null;
            }
        );
    }

    public void setZDrawLimit(int z){
        z += 2;
        
        zDrawLimit = Math.clamp(z, 0, depth);
        // visual update
        app.enqueue(
            () -> {
                gridNode.detachAllChildren();
                draw();
                return null;
            }
        );
    }

    public void addGizmo(Gizmo gizmo) {
        this.gizmo = gizmo;
        this.gridNode.attachChild(gizmo.getGizmoNode());
    }
}
