package com.example.shape;

import com.jme3.material.Material;
import com.jme3.scene.Node;

public abstract class Voxel {
    protected int x; 
    protected int y;
    protected int z;
    protected Material material;
    protected Colors color;
    protected Size size;
    protected Shape shape;
    protected double dimension;

    public final static double UNIT_SIZE = 1.0;

    abstract public void draw(Node group); 

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
