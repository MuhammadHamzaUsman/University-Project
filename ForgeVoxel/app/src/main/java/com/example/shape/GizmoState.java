package com.example.shape;

import java.util.ArrayList;
import java.util.List;

public class GizmoState {
    private float xOffset;
    private float yOffset;
    private float zOffset;

    private List<Gizmo> gizmos;
    
    public GizmoState(float startX, float startY, float startZ){
        gizmos = new ArrayList<>();

        xOffset = startX;
        yOffset = startY;
        zOffset = startZ;
    }

    public void addGizmo(Gizmo gizmo){
        gizmos.add(gizmo);
    }

    public void syncGizmos(){
        for (Gizmo gizmo : gizmos) {
            gizmo.setSelectorPosistions(xOffset, yOffset, zOffset);
        }
    }

    public void setX(float xOffset) {
        this.xOffset = xOffset;
        for (Gizmo gizmo : gizmos) {
            gizmo.setSelectorXPosition(xOffset);
        }
    }

    public void setY(float yOffset) {
        this.yOffset = yOffset;
        for (Gizmo gizmo : gizmos) {
            gizmo.setSelectorYPosition(yOffset);
        }
    }

    public void setZ(float zOffset) {
        this.zOffset = zOffset;
        for (Gizmo gizmo : gizmos) {
            gizmo.setSelectorZPosition(zOffset);
        }
    }

    public float getX() {
        return xOffset;
    }

    public float getY() {
        return yOffset;
    }

    public float getZ() {
        return zOffset;
    }    
}
