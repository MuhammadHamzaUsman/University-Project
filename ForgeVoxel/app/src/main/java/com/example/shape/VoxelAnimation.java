package com.example.shape;

import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class VoxelAnimation extends AbstractControl{
    private static final float SCALING_FACTOR = 1.2f;
    
    private static final float DURATION = 0.2f;
    public static final float MAX_RADIAL_DELAY = 1f;
    public static final float RADIAL_DELAY_EXPONENT = 1f;

    private float timePassed;
    private boolean isIntialized = false;
    private float startingDelay = 0f;
    private float maxSize = 1f;
    private float audioDelay;
    private boolean audioPlayed = false;

    public VoxelAnimation(float startingDelay, float maxSize){
        this.startingDelay = startingDelay;
        this.maxSize = maxSize;
        audioDelay = FastMath.rand.nextFloat(0f, 0.1f);
    };

    public VoxelAnimation(){};

    @Override
    protected void controlUpdate(float tpf){
        
        if(timePassed < startingDelay){
            timePassed += tpf;
            return ;
        }
        
        if(!isIntialized){
            isIntialized = true;
            startingDelay = 0f;
            spatial.setLocalScale(0f);
            timePassed = 0f;
        }

        timePassed += tpf;
        float completionPercentage = Math.min(timePassed / DURATION, 1f);

        float scale = easeOutBack(completionPercentage) * maxSize;
        spatial.setLocalScale(scale);
        
        if(!audioPlayed && timePassed > audioDelay){
            Voxel.updatePopSoundPos(spatial.getWorldTranslation());
            Voxel.playPopSound();
            audioPlayed = true;
        }

        if (completionPercentage >= 1f) {
            spatial.setLocalScale(maxSize);
            spatial.removeControl(this);
        }
    }

    private float easeOutBack(float time) {
        time -= 1f;
        return (time * time * ((SCALING_FACTOR + 1f) * time + SCALING_FACTOR) + 1f);
    }

    @Override
    protected void controlRender(RenderManager arg0, ViewPort arg1) {}


}
