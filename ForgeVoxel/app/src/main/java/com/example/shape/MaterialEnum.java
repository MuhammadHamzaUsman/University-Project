package com.example.shape;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;

public enum MaterialEnum{
    MATTE,
    SHINY,
    TRANSPARENT;

    public static Material createMaterial(AssetManager assetManager, Colors color, MaterialEnum materialEnum){
        ColorRGBA primaryColor = color.getColor().clone();
        Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

        switch (materialEnum) {
            case MATTE : {
                material.setBoolean("UseMaterialColors", true);
                material.setColor("Ambient", primaryColor);
                material.setColor("Diffuse", primaryColor.interpolateLocal(ColorRGBA.Black, 0.05f));
                break;
            }
            case SHINY : {
                material.setBoolean("UseMaterialColors", true);
                material.setColor("Ambient", primaryColor);
                material.setColor("Diffuse", primaryColor.interpolateLocal(ColorRGBA.Black, 0.05f));
                material.setColor("Specular", ColorRGBA.Yellow.interpolateLocal(ColorRGBA.White, 0.5f));
                material.setFloat("Shininess", 64f);
                break;
            }
            case TRANSPARENT : {
                ColorRGBA transparent = new ColorRGBA(primaryColor.getRed(), primaryColor.getGreen(), primaryColor.getBlue(), 0.5f);
                material.setColor("Diffuse", transparent);
                material.setColor("Specular", ColorRGBA.White);
                material.setFloat("Shininess", 20f);
                material.setBoolean("UseMaterialColors", true);
                material.setColor("Ambient", primaryColor);
                material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                material.setTransparent(true);
                break;
            }
        }
        return material;
    }

}