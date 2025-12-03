package com.example.shape;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;

public class GridRenderer {

    private VoxelGrid grid;
    private Node rendererNode;  // child of global root
    private ViewPort viewPort;
    private static int counter = 0;

    public GridRenderer(ViewPort viewPort, VoxelGrid grid, AssetManager assetManager) {
        this.grid = grid;
        this.viewPort = viewPort;

        rendererNode = new Node("GridRenderer-" + (counter++));

        // lights for this grid
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -0.33f, -1).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(0.7f));

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.4f));

        rendererNode.addLight(sun);
        rendererNode.addLight(ambient);

        // shadow renderer
        DirectionalLightShadowRenderer shadowRenderer =
            new DirectionalLightShadowRenderer(assetManager, 2048, 3);

        shadowRenderer.setShadowZExtend(50f);
        shadowRenderer.setLight(sun);
        shadowRenderer.setShadowIntensity(0.7f);
        shadowRenderer.setEdgeFilteringMode(EdgeFilteringMode.Bilinear);

        viewPort.addProcessor(shadowRenderer);

        viewPort.setBackgroundColor(ColorRGBA.fromRGBA255(246, 242, 232, 255));
    }

    public VoxelGrid getGrid() { return grid; }
    public Node getRootNode() { return rendererNode; }
    public ViewPort getViewPort() { return viewPort; }
}
