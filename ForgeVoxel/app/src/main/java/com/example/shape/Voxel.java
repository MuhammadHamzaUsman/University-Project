package com.example.shape;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

public abstract class Voxel {
    protected int x; 
    protected int y;
    protected int z;
    protected Material material;
    protected Colors color;
    protected Size size;
    protected Shape shape;
    protected double dimension;
    protected static AssetManager assetManager;

    public final static double UNIT_SIZE = 1.0;

    public static final ColorRGBA borderColor = ColorRGBA.fromRGBA255(255, 237, 199, 255);

    public static final String HIGHLIGHT = "UNIT-HIGHLIGHT";
    private static final float borderSize = (float)(UNIT_SIZE * 1.2);
    private static final float borderThickness = 0.06f;
    private static Node border;

    private static Spatial cone = null;

    abstract public void draw(Node group); 

    public static void setAssetManager(AssetManager assetManager) {
        Voxel.assetManager = assetManager;
    }

    public static class PropertyInfoHolder{
        public Colors color;
        public Size size;
        public Shape shape;

        public PropertyInfoHolder(Colors color, Size size, Shape shape) {
            this.color = color;
            this.size = size;
            this.shape = shape;
        }

        @Override
        public String toString() {
            return "PropertyInfoHolder [color=" + color + ", size=" + size + ", shape=" + shape + "]";
        }
    }

    public boolean isSame(Voxel otherVoxel){
        return (color == otherVoxel.color) && (size == otherVoxel.size) && (shape == otherVoxel.shape);
    }

    public static Node getborder(String name, float width, float height, float depth, float lineWidth, ColorRGBA borderColor, AssetManager assetManager){
        Material borderMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        borderMaterial.setColor("Color", borderColor);

        Node borderNode = new Node(name);
        
        width /= 2;
        height /= 2;
        depth /= 2;
        lineWidth /= 2;

        // X edges
        borderNode.attachChild(boxEdge(width, lineWidth, lineWidth, 0,  height,  depth, borderMaterial));
        borderNode.attachChild(boxEdge(width, lineWidth, lineWidth, 0, -height,  depth, borderMaterial));
        borderNode.attachChild(boxEdge(width, lineWidth, lineWidth, 0,  height, -depth, borderMaterial));
        borderNode.attachChild(boxEdge(width, lineWidth, lineWidth, 0, -height, -depth, borderMaterial));

        // Y edges
        borderNode.attachChild(boxEdge(lineWidth, height, lineWidth,  width, 0,  depth, borderMaterial));
        borderNode.attachChild(boxEdge(lineWidth, height, lineWidth, -width, 0,  depth, borderMaterial));
        borderNode.attachChild(boxEdge(lineWidth, height, lineWidth,  width, 0, -depth, borderMaterial));
        borderNode.attachChild(boxEdge(lineWidth, height, lineWidth, -width, 0, -depth, borderMaterial));

        // Z edges
        borderNode.attachChild(boxEdge(lineWidth, lineWidth, depth,  width,  height, 0, borderMaterial));
        borderNode.attachChild(boxEdge(lineWidth, lineWidth, depth, -width,  height, 0, borderMaterial));
        borderNode.attachChild(boxEdge(lineWidth, lineWidth, depth,  width, -height, 0, borderMaterial));
        borderNode.attachChild(boxEdge(lineWidth, lineWidth, depth, -width, -height, 0, borderMaterial));

        return borderNode;
    }

    private static Geometry boxEdge(float width, float height, float depth, float posX, float posY, float posZ, Material material){
        Geometry edge = new Geometry("edge", new Box(width, height, depth));
        edge.setMaterial(material);
        edge.setLocalTranslation(posX, posY, posZ);
        return edge;
    }
    
    public static Node getVoxelUnitBorder(AssetManager assetManager){
        if(border == null){
            border  = Voxel.getborder(
                HIGHLIGHT, 
                Voxel.borderSize, Voxel.borderSize, Voxel.borderSize, 
                Voxel.borderThickness, ColorRGBA.White, assetManager
            );

            return (Node) border.clone();
        }

        return (Node) border.clone();
    }

    public static Spatial getCone(AssetManager assetManager) throws AssetNotFoundException {
        if(cone == null){
            Logger.getLogger("com.jme3.asset").setLevel(Level.FINE);
            cone = assetManager.loadModel("Models/cone.gltf");

            cone.depthFirstTraversal(spatial -> {
                if (spatial instanceof Geometry) {
                    spatial.setName("Cone");
                }
            });
        }

        return cone.clone();
    }

    abstract public void startAnimation();

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Colors getColor() {
        return color;
    }

    public void setColor(Colors color) {
        this.color = color;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    @Override
    public String toString() {
        return "Voxel [x=" + x + ", y=" + y + ", z=" + z + ", material=" + material + ", color=" + color + ", size="
                + size + ", shape=" + shape + ", dimension=" + dimension + "]";
    }
}
