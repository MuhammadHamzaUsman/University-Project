package com.example.shape;

import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Cylinder extends Voxel{
    
    private Spatial cylinder;
    private static Spatial cylinderModel;

    public Cylinder(int x, int y, int z, MaterialEnum materialEnum, Colors color, Size size) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.material = MaterialEnum.createMaterial(assetManager, color, materialEnum);
        this.shape = Shape.CYLINDER;
        this.size = size;
        this.dimension = shape.getDimension() * size.getFactor() * Voxel.UNIT_SIZE;
        this.distFromZero = FastMath.sqrt(x * x + y * y + z * z);
        
        this.material.getAdditionalRenderState().setDepthTest(true);
        this.material.getAdditionalRenderState().setDepthWrite(true);
        this.material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);

        this.cylinder = getModel(String.format("Voxel-%d|Cylinder|%c|%d|%d|%d", color.ordinal(), size.name().charAt(0), x, y, z));
        this.cylinder.setMaterial(this.material);
        this.cylinder.setShadowMode(ShadowMode.CastAndReceive);
        this.cylinder.rotate(FastMath.PI / 2, 0f, 0f);
        this.cylinder.setQueueBucket(RenderQueue.Bucket.Opaque);

        this.cylinder.setLocalScale(0);
    }

    @Override
    public void draw(Node node, float gridRadius) {
        double groupX = x * Voxel.UNIT_SIZE;
        double groupZ = z * Voxel.UNIT_SIZE; 
        double groupY = y * Voxel.UNIT_SIZE;

        cylinder.setLocalTranslation((float)groupX, (float)groupY, (float)groupZ);
        cylinder.addControl(new VoxelAnimation((distFromZero / gridRadius) * VoxelAnimation.delayDuaration, (float)(dimension * 2)));
        node.attachChild(cylinder);
    }

    public Spatial getCylinder() {
        return cylinder;
    }

    public void setCylinder(Spatial cylinder) {
        this.cylinder = cylinder;
    }

    private Spatial getModel(String name){
        if(cylinderModel == null){
            cylinderModel = assetManager.loadModel("Models/cube.gltf");
        }

        Spatial clone = cylinderModel.clone();
        clone.setName(name);

        return clone;
    }
}
