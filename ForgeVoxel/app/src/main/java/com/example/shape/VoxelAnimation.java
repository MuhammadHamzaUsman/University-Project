package com.example.shape;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class VoxelAnimation extends AbstractControl{
    private static final float SCALING_FACTOR = 1.2f;
    
    private static final float DURATION = 0.175f;
    public static final float MAX_RADIAL_DELAY = 1f;
    public static final float RADIAL_DELAY_EXPONENT = 1f;

    private float timePassed;
    private boolean isIntialized = false;
    private float startingDelay = 0f;
    private float maxSize = 1f;

    public VoxelAnimation(float startingDelay, float maxSize){
        this.startingDelay = startingDelay;
        this.maxSize = maxSize;
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

        // if(spatial.getName().contains("Sphere")){
        //     System.out.println("completionPercentage: " + completionPercentage + ", scale: " + scale + ", timePassed " + timePassed + ", maxSize: " + maxSize);
        // }

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
