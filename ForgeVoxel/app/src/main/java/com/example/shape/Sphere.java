package com.example.shape;

import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

public class Sphere extends Voxel{
    private Geometry sphere;

    public Sphere(int x, int y, int z, MaterialEnum materialEnum, Colors color, Size size) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.material = MaterialEnum.createMaterial(assetManager, color, materialEnum);
        this.shape = Shape.SPHERE;
        this.size = size;
        this.dimension = shape.getDimension() * size.getFactor() * Voxel.UNIT_SIZE;
        this.distFromZero = FastMath.sqrt(x * x + y * y + z * z);

        this.material.getAdditionalRenderState().setDepthTest(true);
        this.material.getAdditionalRenderState().setDepthWrite(true);
        this.material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);        

        com.jme3.scene.shape.Sphere sphereShape = new com.jme3.scene.shape.Sphere(50, 50, (float)(dimension));
        this.sphere = new Geometry(
            String.format("Voxel-%d|Sphere|%c|%d|%d|%d", color.ordinal(), size.name().charAt(0), x, y, z),
            sphereShape
        );
        this.sphere.setMaterial(this.material);
        this.sphere.setShadowMode(ShadowMode.CastAndReceive);
        this.sphere.setQueueBucket(RenderQueue.Bucket.Opaque);

        this.sphere.setLocalScale(0);
    }

    @Override
    public void draw(Node node, float gridRadius) {
        double groupX = x * Voxel.UNIT_SIZE;
        double groupZ = z * Voxel.UNIT_SIZE; 
        double groupY = y * Voxel.UNIT_SIZE;

        sphere.setLocalTranslation((float)groupX, (float)groupY, (float)groupZ);
        sphere.addControl(new VoxelAnimation((distFromZero / gridRadius) * VoxelAnimation.delayDuaration, (float)(dimension * 2)));
        node.attachChild(sphere);
    }

    public Geometry getSphere() {
        return sphere;
    }

    public void setSphere(Geometry sphere) {
        this.sphere = sphere;
    }
}
