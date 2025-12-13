package com.example.shape;

import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;

public class Cube extends Voxel{
    private Spatial cube;
    private static Spatial cubeModel;

    public Cube(int x, int y, int z, MaterialEnum materialEnum, Colors color, Size size) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.material = MaterialEnum.createMaterial(assetManager, color, materialEnum);
        this.shape = Shape.CUBE;
        this.size = size;
        this.dimension = shape.getDimension() * size.getFactor() * Voxel.UNIT_SIZE;
        this.distFromZero = FastMath.sqrt(x * x + y * y + z * z);

        this.material.getAdditionalRenderState().setDepthTest(true);
        this.material.getAdditionalRenderState().setDepthWrite(true);
        this.material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);

        this.cube = getModel(String.format("Voxel-%d|Cube|%c|%d|%d|%d", color.ordinal(), size.name().charAt(0), x, y, z));
        this.cube.setMaterial(this.material);
        this.cube.setShadowMode(ShadowMode.CastAndReceive);
        this.cube.setQueueBucket(RenderQueue.Bucket.Opaque);
        
        this.cube.setLocalScale(0);
    }

    @Override
    public void draw(Node node, float gridRadius) {
        double groupX = x * Voxel.UNIT_SIZE;
        double groupZ = z * Voxel.UNIT_SIZE; 
        double groupY = y * Voxel.UNIT_SIZE;

        cube.setLocalTranslation((float)groupX, (float)groupY, (float)groupZ);
        cube.addControl(new VoxelAnimation((distFromZero / gridRadius) * VoxelAnimation.delayDuaration, (float)dimension));
        node.attachChild(cube);
    }

    public Spatial getCube() {
        return cube;
    }

    public void setCube(Spatial cube) {
        this.cube = cube;
    }

    private Spatial getModel(String name){
        if(cubeModel == null){
            cubeModel = assetManager.loadModel("Models/cube.gltf");
        }

        Spatial clone = cubeModel.clone();
        clone.setName(name);

        return clone;
    }
}
