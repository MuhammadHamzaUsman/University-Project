package com.example.shape;

import java.util.Map;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

public class OrbitCamera {
    private InputManager inputManager;

    private Node pivotHorz;
    private Node pivotVert;
    private Node cameraNode;
    private Camera camera;
    private Node rootNode;

    private double radius = 10.0;
    private double zoomInLimit = 5;
    private double zoomOutLimit = 100;
    private double zoomFactor = 1.1;

    private boolean isRotating = false;
    private double anchorX = 0;
    private double anchorY = 0;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private double rotationFactor = 0.01;
    private double horizontalAngle = 0;
    private double verticalAngle = FastMath.PI / 2;
    private Node highlight;

    private boolean isSHIFTPrressed;

    private static class InputHolder{
        int mouseInput;
        boolean bool;

        public InputHolder(int mouseInput, boolean bool) {
            this.mouseInput = mouseInput;
            this.bool = bool;
        }
    }

    private static Map<String, InputHolder> analogInput = Map.ofEntries(
        Map.entry("ZoomIn", new InputHolder( MouseInput.AXIS_WHEEL, false)),
        Map.entry("ZoomOut", new InputHolder( MouseInput.AXIS_WHEEL, true)),
        Map.entry("Horiz+", new InputHolder(MouseInput.AXIS_X, true)),
        Map.entry("Horiz-", new InputHolder(MouseInput.AXIS_X, false)),
        Map.entry("Vert+", new InputHolder(MouseInput.AXIS_Y, true)),
        Map.entry("Vert-", new InputHolder(MouseInput.AXIS_Y, false))
    );

    public OrbitCamera(Node rootNode, Camera cam, InputManager inputManager, AssetManager assetManager){
        this.inputManager = inputManager;
        this.camera = cam;
        this.rootNode = rootNode;
        this.highlight = Voxel.getVoxelUnitBorder(assetManager);

        camera.setLocation(new Vector3f(0f, 0f, (float)radius));
        camera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        pivotHorz = new Node("PivotHorz");
        pivotHorz.setLocalTranslation(Vector3f.ZERO);
        rootNode.attachChild(pivotHorz);

        pivotVert = new Node("PivotVert");
        pivotVert.setLocalTranslation(Vector3f.ZERO);
        pivotHorz.attachChild(pivotVert);

        cameraNode = new CameraNode("CameraNode", camera);
        cameraNode.setLocalTranslation(new Vector3f(0, 0, (float)radius));
        pivotVert.attachChild(cameraNode);
        addInputs();
    }

    public void addInputs(){
        InputHolder inputHolder;
        for (String inputName : analogInput.keySet()) {
            if(!inputManager.hasMapping(inputName)){
                inputHolder = analogInput.get(inputName);
                inputManager.addMapping(inputName, new MouseAxisTrigger( inputHolder.mouseInput, inputHolder.bool));
            }
        }
        
        if(!inputManager.hasMapping("LeftClick")){ inputManager.addMapping("LeftClick", new MouseButtonTrigger( MouseInput.BUTTON_LEFT));}
        if(!inputManager.hasMapping("SHIFT")){ inputManager.addMapping("SHIFT", new KeyTrigger(KeyInput.KEY_LSHIFT));}
        
        inputManager.addListener(
            new AnalogListener() {
                @Override
                public void onAnalog(String name, float value, float tpf) {
                    radius /= zoomFactor;
                    radius = FastMath.clamp((float)radius, (float)zoomInLimit, (float)zoomOutLimit);

                    cameraNode.setLocalTranslation(new Vector3f(0f, 0f, (float)radius));
                    cameraNode.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
                }
            },
            new String[]{"ZoomIn"}
        );

        inputManager.addListener(
            new AnalogListener() {
                @Override
                public void onAnalog(String name, float value, float tpf) {
                    radius *= zoomFactor;
                    radius = FastMath.clamp((float)radius, (float)zoomInLimit, (float)zoomOutLimit);

                    cameraNode.setLocalTranslation(new Vector3f(0f, 0f, (float)radius));
                    cameraNode.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
                }
            }, 
            new String[]{"ZoomOut"}
        );

        inputManager.addListener(
            new ActionListener() {
                @Override
                public void onAction(String name, boolean isPressed, float tpf) {
                    if(isPressed){
                        isRotating = true;

                        anchorX = inputManager.getCursorPosition().x;
                        anchorY = inputManager.getCursorPosition().y;

                        anchorAngleX = horizontalAngle;
                        anchorAngleY = verticalAngle;
                    }
                    else {isRotating = false;}
                }
            }, 
            new String[]{"LeftClick"}
        );

        inputManager.addListener(
            new AnalogListener() {
                @Override
                public void onAnalog(String name, float value, float tpf) {
                    if(!isRotating || !isSHIFTPrressed) return;

                    horizontalAngle = anchorAngleX + (inputManager.getCursorPosition().y - anchorY) * rotationFactor;
                    verticalAngle = anchorAngleY - (inputManager.getCursorPosition().x - anchorX) * rotationFactor;
                    
                    horizontalAngle = FastMath.clamp((float)horizontalAngle, -FastMath.HALF_PI, FastMath.HALF_PI);

                    pivotHorz.setLocalRotation(new Quaternion().fromAngleAxis((float)verticalAngle, Vector3f.UNIT_Y));
                    pivotVert.setLocalRotation(new Quaternion().fromAngleAxis((float)horizontalAngle, Vector3f.UNIT_X));
                }
            }, 
            new String[]{"Horiz+", "Horiz-", "Vert+", "Vert-"}
        );

        inputManager.addListener(
            new ActionListener() {
                @Override
                public void onAction(String name, boolean isPressed, float tpf) {
                    if(name.equals("SHIFT")){
                        if(isPressed){ isSHIFTPrressed = true;}
                        else{ isSHIFTPrressed = false;}
                    }
                }
            }, 
        "SHIFT"
        );
    }

    Geometry highlightedVoxel = null;
    public void mouseHoverCheck(){
        Vector2f mousePos = inputManager.getCursorPosition();
        Vector3f origin = camera.getWorldCoordinates(mousePos, 0f);
        Vector3f destination = camera.getWorldCoordinates(mousePos, 1f).subtract(origin).normalizeLocal();
        Ray ray = new Ray(origin, destination);

        CollisionResults collisions = new CollisionResults();
        rootNode.collideWith(ray, collisions);

        Geometry geom;
        if(collisions.size() >= 1){
            geom = collisions.getClosestCollision().getGeometry();

            if(geom.getName().contains("Voxel-")){
                if(geom != highlightedVoxel){
                    highlightedVoxel = geom;
                    
                    highlight.setLocalTranslation(highlightedVoxel.getLocalTranslation());
                    rootNode.attachChild(highlight);
                }
            }
            else{
                rootNode.detachChildNamed(Voxel.HIGHLIGHT);
                highlightedVoxel = null;
            }
        }
        else{
            rootNode.detachChildNamed(Voxel.HIGHLIGHT);
            highlightedVoxel = null;
        }
    }
}
