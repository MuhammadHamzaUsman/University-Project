package com.example.shape;

import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Sphere extends Voxel{
    private Spatial sphere;
    private static Spatial sphereModel;

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

        this.sphere = getModel(String.format("Voxel-%d|Sphere|%c|%d|%d|%d", color.ordinal(), size.name().charAt(0), x, y, z));
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

        float normalisedDistnace = distFromZero / gridRadius;
        float delay = FastMath.pow(normalisedDistnace, VoxelAnimation.RADIAL_DELAY_EXPONENT) * VoxelAnimation.MAX_RADIAL_DELAY;
        
        sphere.addControl(new VoxelAnimation(delay, (float)(dimension * 2)));
        sphere.setLocalTranslation((float)groupX, (float)groupY, (float)groupZ);
        node.attachChild(sphere);
    }

    public Spatial getSphere() {
        return sphere;
    }

    public void setSphere(Spatial sphere) {
        this.sphere = sphere;
    }

    private Spatial getModel(String name){
        if(sphereModel == null){
            sphereModel = assetManager.loadModel("Models/sphere.gltf");
        }

        Spatial clone = sphereModel.clone();
        clone.setName(name);
        clone.depthFirstTraversal(spatial -> {
                if (spatial instanceof Geometry) {
                    spatial.setName(name);
                }
            }
        );

        return clone;
    }
}
