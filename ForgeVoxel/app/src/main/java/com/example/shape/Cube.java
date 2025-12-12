package com.example.shape;

import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;

public class Cube extends Voxel{
    private Geometry cube;

    public Cube(int x, int y, int z, MaterialEnum materialEnum, Colors color, Size size) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.material = MaterialEnum.createMaterial(assetManager, color, materialEnum);
        this.shape = Shape.CUBE;
        this.size = size;
        this.dimension = shape.getDimension() * size.getFactor() * Voxel.UNIT_SIZE;

        this.material.getAdditionalRenderState().setDepthTest(true);
        this.material.getAdditionalRenderState().setDepthWrite(true);
        this.material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);

        Box cubeShape = new Box((float)(dimension / 2), (float)(dimension / 2), (float)(dimension / 2));
        this.cube = new Geometry(
            String.format("Voxel-%d|Cube|%c|%d|%d|%d", color.ordinal(), size.name().charAt(0), x, y, z),
            cubeShape
        );
        this.cube.setMaterial(this.material);
        this.cube.setShadowMode(ShadowMode.CastAndReceive);
        this.cube.setQueueBucket(RenderQueue.Bucket.Opaque);
    }

    @Override
    public void draw(Node node) {
        double groupX = x * Voxel.UNIT_SIZE;
        double groupZ = z * Voxel.UNIT_SIZE; 
        double groupY = y * Voxel.UNIT_SIZE;

        cube.setLocalTranslation((float)groupX, (float)groupY, (float)groupZ);
        cube.addControl(new VoxelAnimation());
        node.attachChild(cube);
    }

    public Geometry getCube() {
        return cube;
    }

    public void setCube(Geometry cube) {
        this.cube = cube;
    }

    @Override
    public void startAnimation() {
        VoxelAnimation animation = cube.getControl(VoxelAnimation.class);
        if(animation != null){animation.setEnabled(true);}
    }
}
