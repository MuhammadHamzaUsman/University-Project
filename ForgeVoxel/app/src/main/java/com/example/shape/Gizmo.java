package com.example.shape;

import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;

public class Gizmo{
    private VoxelGrid grid;
    private InputManager inputManager;
    private Camera camera;
    private GizmoState sharedState;
    
    private int xSelctorLowerLimit;
    private int xSelctorUpperLimit;
    private float xBarLength;
    private float currentX;
    private Spatial coneX;

    private int ySelctorLowerLimit;
    private int ySelctorUpperLimit;
    private float yBarLength;
    private float currentY;
    private Spatial coneY;

    private int zSelctorLowerLimit;
    private int zSelctorUpperLimit;
    private float zBarLength;
    private float currentZ;
    private Spatial coneZ;

    private Geometry selector = null;
    private Vector3f selectedAxis = null;
    private boolean isCTRLPrressed = false;

    private final String ACTION_NAME = "selectorDrag";
    private final String X_SELECTOR = "XSelector";
    private final String Y_SELECTOR = "YSelector";
    private final String Z_SELECTOR = "ZSelector";
    private final String SELECTOR = "Selector";

    private Node gizmoNode;

    public Gizmo(VoxelGrid grid, InputManager inputManager, Camera camera, GizmoState sharedState){
        this.grid = grid;
        this.inputManager = inputManager;
        this.camera = camera;
        this.sharedState = sharedState;
        sharedState.addGizmo(this);

        currentX = sharedState.getX();
        currentY = sharedState.getY();
        currentZ = sharedState.getZ();

        xBarLength = (float)(grid.getWidth() * Voxel.UNIT_SIZE + 2 * (Voxel.UNIT_SIZE / 2));
        yBarLength = (float)(grid.getHeight() * Voxel.UNIT_SIZE + 2 * (Voxel.UNIT_SIZE / 2));
        zBarLength = (float)(grid.getDepth() * Voxel.UNIT_SIZE + 2 * (Voxel.UNIT_SIZE / 2));

        xSelctorLowerLimit = -grid.getxLimit();
        xSelctorUpperLimit = -xSelctorLowerLimit + 1;

        ySelctorLowerLimit = -grid.getyLimit();
        ySelctorUpperLimit = -ySelctorLowerLimit + 1;

        zSelctorLowerLimit = -grid.getzLimit();
        zSelctorUpperLimit = -zSelctorLowerLimit + 1;

        if(!inputManager.hasMapping(ACTION_NAME))inputManager.addMapping(ACTION_NAME, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(getDragActionListner(), ACTION_NAME);
            
        if(!inputManager.hasMapping("CTRL"))inputManager.addMapping("CTRL", new KeyTrigger(KeyInput.KEY_LCONTROL));
        inputManager.addListener(
            new ActionListener() {
                @Override
                public void onAction(String name, boolean isPressed, float tpf) {
                    if(name.equals("CTRL")){
                        if(isPressed){ isCTRLPrressed = true;}
                        else{ isCTRLPrressed = false;}
                    }
                }
            }, 
        "CTRL"
        );
    }

    public void intializeAxisBars(AssetManager assetManager){
        gizmoNode = new Node();
        gizmoNode.attachChild(
            getBar(
                X_SELECTOR, currentX, Vector3f.UNIT_X, 
                ColorRGBA.fromRGBA255(63, 145, 119, 255), 
                xBarLength, xSelctorUpperLimit, coneX, assetManager
            )
        );
        gizmoNode.attachChild(
            getBar(
                Y_SELECTOR, currentY, Vector3f.UNIT_Y, 
                ColorRGBA.fromRGBA255(248, 128, 112, 255), 
                yBarLength, ySelctorUpperLimit, coneY, assetManager
            )
        );
        gizmoNode.attachChild(
            getBar(
                Z_SELECTOR, currentZ, Vector3f.UNIT_Z, 
                ColorRGBA.fromRGBA255(38, 125, 195, 255), 
                zBarLength, zSelctorUpperLimit, coneZ, assetManager
            )
        );
    }

    private Node getBar(String coneName, float location, Vector3f axis, ColorRGBA color, float length, int lengthLimit, Spatial storeCone, AssetManager assetManager){
        
        axis.normalizeLocal();

        Quaternion rotation = new Quaternion();
        rotation.lookAt(axis, Vector3f.UNIT_Y);
        
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", color);

        Node barNode = new Node(coneName + "BarAxis");
        
        Spatial cone = Voxel.getCone(assetManager);
        cone.setName(coneName);
        cone.depthFirstTraversal(s -> {
            if (s instanceof Geometry) {
                s.setName(coneName);
            }
        });
        cone.scale(0.5f);
        cone.setLocalTranslation(axis.mult(location));
        cone.setLocalRotation(rotation);
        cone.setMaterial(material);
        barNode.attachChild(cone);

        Geometry bar = new Geometry(
            "bar",
            new Cylinder(10, 10, 0.05f, length, true)
        );
        bar.setMaterial(material);
        bar.setLocalRotation(rotation);
        barNode.attachChild(bar);

        Sphere blob = new Sphere(10, 10, 0.13f);
        for (int i = -lengthLimit; i <= lengthLimit; i++) {
            Geometry blobGeometry = new Geometry("blob", blob);
            blobGeometry.setMaterial(material);
            blobGeometry.setLocalTranslation(axis.mult(i));
            barNode.attachChild(blobGeometry);
        }

        if(coneName.equals(X_SELECTOR)) this.coneX = cone;
        else if(coneName.equals(Y_SELECTOR)) this.coneY = cone;
        else if(coneName.equals(Z_SELECTOR)) this.coneZ = cone;

        return barNode;
    }

    private ActionListener getDragActionListner() {
        return new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if(name.equals(ACTION_NAME)){
                    if(isPressed && isCTRLPrressed) {
                        selector = getSelector();
                        if(selector != null){ selectedAxis = getAxis(selector);}
                    } 
                    else {
                        selectedAxis = null;
                        selector = null;
                    }
                }
            }
        };
    }

    private Geometry getSelector() {
        Vector2f mousePos = inputManager.getCursorPosition();
        Vector3f origin = camera.getWorldCoordinates(mousePos, 0f);
        Vector3f destination = camera.getWorldCoordinates(mousePos, 1f).subtract(origin).normalizeLocal();
        Ray ray = new Ray(origin, destination);

        CollisionResults collisions = new CollisionResults();
        grid.getGridNode().collideWith(ray, collisions);

        if(collisions.size() > 0){
            Geometry object = collisions.getClosestCollision().getGeometry();

            if(object.getName().contains(SELECTOR)){
                return object;
            }
        }

        return null;
    }

    private Vector3f getAxis(Geometry selector){
        switch (selector.getName()) {
            case X_SELECTOR: return Vector3f.UNIT_X;
            case Y_SELECTOR: return Vector3f.UNIT_Y;
            case Z_SELECTOR: return Vector3f.UNIT_Z;        
            default: return null;
        }
    }
    
    public void updateSelector(){
        if(selectedAxis == null || selector == null) return;
        Vector2f mousePos = inputManager.getCursorPosition();
        Vector3f origin = camera.getWorldCoordinates(mousePos, 0f);
        Vector3f directon = camera.getWorldCoordinates(mousePos, 1f).subtract(origin).normalizeLocal();
        Ray ray = new Ray(origin, directon);

        Vector3f normalPlaneVector = null;
        float smallestValue = Float.MAX_VALUE;
        float temp = 0;
        for (Vector3f vec : List.of(Vector3f.UNIT_X, Vector3f.UNIT_Y, Vector3f.UNIT_Z, Vector3f.UNIT_X.mult(-1), Vector3f.UNIT_Y.mult(-1), Vector3f.UNIT_Z.mult(-1))) {
            temp = vec.dot(directon);
            if(temp < smallestValue && !(Math.abs(selectedAxis.dot(vec)) == 1)){
                smallestValue = temp;
                normalPlaneVector = vec.clone();
            }
        }

        Plane perpendicularPlane = new Plane(normalPlaneVector, selector.getWorldTranslation());
        
        Vector3f planeHit = intersectRayPlane(ray, perpendicularPlane);
        if(planeHit != null){
            float distance = clampAxis(planeHit.dot(selectedAxis), selectedAxis);
            if (selectedAxis.equals(Vector3f.UNIT_X)){
                sharedState.setX(distance);
            }
            else if(selectedAxis.equals(Vector3f.UNIT_Y)){
                sharedState.setY(distance);
            }
            else if(selectedAxis.equals(Vector3f.UNIT_Z)){
                sharedState.setZ(distance);
            }
        }
    }
    
    private static Vector3f intersectRayPlane(Ray ray, Plane plane) {
        float numerator = plane.getNormal().dot(ray.getOrigin()) + plane.getConstant();
        float denominator = plane.getNormal().dot(ray.getDirection());

        if(Math.abs(denominator) < 1e-6f){
            return null;
        }

        float intersectionParameter = -(numerator / denominator);

        if(intersectionParameter < 0){
            return null;
        }

        return ray.getOrigin().add(ray.getDirection().mult(intersectionParameter)); 
    }

    private float clampAxis(float value, Vector3f axis){
        if(Vector3f.UNIT_X.equals(axis)){
            currentX = (float)(Math.clamp(value, xSelctorLowerLimit, xSelctorUpperLimit));
            return currentX;
        }
        else if(Vector3f.UNIT_Y.equals(axis)){
            currentY = (float)(Math.clamp(value, ySelctorLowerLimit, ySelctorUpperLimit));
            return currentY;
        }
        else if(Vector3f.UNIT_Z.equals(axis)){
            currentZ = (float)(Math.clamp(value, zSelctorLowerLimit, zSelctorUpperLimit));
            return currentZ;
        }

        return value;
    }

    public void setSelectorPosistions(float xOffset, float yOffset, float zOffset) {
        setSelectorXPosition(xOffset);
        setSelectorYPosition(yOffset);
        setSelectorZPosition(zOffset);
    }
    
    public void setSelectorXPosition(float xOffset){
        currentX = (float)(Math.clamp(xOffset, xSelctorLowerLimit, xSelctorUpperLimit));
        Vector3f localPosX = coneX.getParent().worldToLocal(Vector3f.UNIT_X.mult(currentX), null);
        coneX.setLocalTranslation(localPosX);
        grid.setXDrawLimit(Math.round(currentX));
    }
    
    public void setSelectorYPosition(float yOffset){
        currentY = (float)(Math.clamp(yOffset, ySelctorLowerLimit, ySelctorUpperLimit));    
        Vector3f localPosY = coneY.getParent().worldToLocal(Vector3f.UNIT_Y.mult(currentY), null);
        coneY.setLocalTranslation(localPosY);
        grid.setYDrawLimit(Math.round(currentY));
    }
    
    public void setSelectorZPosition(float zOffset){
        currentZ = (float)(Math.clamp(zOffset, zSelctorLowerLimit, zSelctorUpperLimit));
        Vector3f localPosZ = coneZ.getParent().worldToLocal(Vector3f.UNIT_Z.mult(currentZ), null);
        coneZ.setLocalTranslation(localPosZ);
        grid.setZDrawLimit(Math.round(currentZ));
    }

    public Node getGizmoNode() {
        return gizmoNode;
    }
} 