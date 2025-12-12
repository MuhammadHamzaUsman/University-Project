package com.example.shape;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class VoxelAnimation extends AbstractControl{
    private float timePassed;
    private float duration = 0.25f;
    private boolean isIntialized = false;
    private static float scalingFactor = 1.2f;

    public VoxelAnimation(){
        enabled = false;
    };

    @Override
    protected void controlUpdate(float tpf){
        if(!isIntialized){
            isIntialized = true;
            spatial.setLocalScale(0f);
        }

        timePassed += tpf;
        float completionPercentage = Math.min(timePassed / duration, 1f);

        float scale = easeOutBack(completionPercentage);
        spatial.setLocalScale(scale);

        if (completionPercentage >= 1f) {
            spatial.setLocalScale(1f);
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
