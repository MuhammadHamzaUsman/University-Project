package com.example.shape;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class VoxelAnimation extends AbstractControl{
    private static float duration = 0.25f;
    private static float scalingFactor = 1.2f;
    public static float delayDuaration = 1f;
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
        }

        timePassed += tpf;
        float completionPercentage = Math.min(timePassed / duration, 1f);

        float scale = easeOutBack(completionPercentage) * maxSize;
        spatial.setLocalScale(scale);

        if (completionPercentage >= 1f) {
            spatial.setLocalScale(maxSize);
            spatial.removeControl(this);
        }
    }

    private float easeOutBack(float time) {
        time -= 1f;
        return (time * time * ((scalingFactor + 1f) * time + scalingFactor) + 1f);
    }

    @Override
    protected void controlRender(RenderManager arg0, ViewPort arg1) {}
}
