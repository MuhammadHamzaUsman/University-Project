package com.example.shape;

import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

public class Cylinder extends Voxel{
    
    private Geometry cylinder;

    public Cylinder(int x, int y, int z, MaterialEnum materialEnum, Colors color, Size size) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.material = MaterialEnum.createMaterial(assetManager, color, materialEnum);
        this.shape = Shape.CYLINDER;
        this.size = size;
        this.dimension = shape.getDimension() * size.getFactor() * Voxel.UNIT_SIZE;
        
        this.material.getAdditionalRenderState().setDepthTest(true);
        this.material.getAdditionalRenderState().setDepthWrite(true);
        this.material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);

        com.jme3.scene.shape.Cylinder cylinderShape = new com.jme3.scene.shape.Cylinder(50, 50, (float)(dimension), (float)(dimension * 2), true);
        this.cylinder = new Geometry(
            String.format("Voxel-%d|Cylinder|%c|%d|%d|%d", color.ordinal(), size.name().charAt(0), x, y, z),
            cylinderShape
        );
        this.cylinder.setMaterial(this.material);
        this.cylinder.setShadowMode(ShadowMode.CastAndReceive);
        this.cylinder.rotate(FastMath.PI / 2, 0f, 0f);
        this.cylinder.setQueueBucket(RenderQueue.Bucket.Opaque);
    }

    @Override
    public void draw(Node node) {
        double groupX = x * Voxel.UNIT_SIZE;
        double groupZ = z * Voxel.UNIT_SIZE; 
        double groupY = y * Voxel.UNIT_SIZE;

        cylinder.setLocalTranslation((float)groupX, (float)groupY, (float)groupZ);
        cylinder.addControl(new VoxelAnimation());
        node.attachChild(cylinder);
    }

    public Geometry getCylinder() {
        return cylinder;
    }

    public void setCylinder(Geometry cylinder) {
        this.cylinder = cylinder;
    }

    @Override
    public void startAnimation() {
        VoxelAnimation animation = cylinder.getControl(VoxelAnimation.class);
        if(animation != null){animation.setEnabled(true);}
    }
}
