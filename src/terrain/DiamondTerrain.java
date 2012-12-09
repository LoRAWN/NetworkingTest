package terrain;

import com.jme3.asset.AssetManager;
import java.nio.ByteBuffer;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;
import terrain.HeightMap.DiamondSquareHeightMap;
import terrain.HeightMap.HeightMapTools;

public class DiamondTerrain {

    private TerrainQuad terrain;
    private Material mat_terrain;

    public Node setUpTerrain(Node node, AssetManager ass) {

        // Note: for light terrain must be illuminated in different node than objects. Perhaps
        //       a bug
        Node groundNode = new Node("groundNode");
        node.attachChild(groundNode);

        Node terrainNode = new Node("terrainNode");
        terrainNode = createTerrain(ass);
        node.attachChild(terrainNode);
/*
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(5f));
        terrainNode.addLight(al);

        Vector3f lightRay = new Vector3f(0.8f, -3f, 0.5f);

        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White.mult(0.8f));
        sun.setDirection(lightRay.normalizeLocal());
        groundNode.addLight(sun);

        DirectionalLight dirLight1 = new DirectionalLight();
        dirLight1.setColor(ColorRGBA.White.mult(0.45f));
        dirLight1.setDirection(new Vector3f(0, 0, 1));
        groundNode.addLight(dirLight1);

        DirectionalLight dirLight2 = new DirectionalLight();
        dirLight2.setColor(ColorRGBA.White.mult(0.45f));
        dirLight2.setDirection(new Vector3f(0, 0, -1));
        groundNode.addLight(dirLight2);

        DirectionalLight dirLight3 = new DirectionalLight();
        dirLight3.setColor(ColorRGBA.White.mult(0.45f));
        dirLight3.setDirection(new Vector3f(1, 0, 0));
        groundNode.addLight(dirLight3);

        DirectionalLight dirLight4 = new DirectionalLight();
        dirLight4.setColor(ColorRGBA.White.mult(0.45f));
        dirLight4.setDirection(new Vector3f(-1, 0, 0));
        groundNode.addLight(dirLight4);*/

        return node;

    }

    public Node createTerrain(AssetManager assetManager) {

        DiamondSquareHeightMap hMap = null;

        try {
            hMap = new DiamondSquareHeightMap(World.terrainMapSize, World.hillPercentage, World.waterPercentage,World.seed);
        } catch (Exception e) {
            System.out.println(e);
        }

        float levelHeight = hMap.getLevelHeight();
        float low = hMap.heightGetAbsolute(0.00f);
        float high = hMap.heightGetAbsolute(1.00f);
        float beachLine = hMap.heightGetRelative(0.03f);
        float rockLine = hMap.heightGetRelative(0.03f);

        ByteBuffer waterChannel = hMap.createAlphaChannel(
                low,
                low,
                levelHeight - beachLine,
                levelHeight - beachLine);

        ByteBuffer sandChannel = hMap.createAlphaChannel(
                levelHeight - beachLine - hMap.heightGetRelative(0.03f),
                levelHeight - beachLine,
                levelHeight,
                levelHeight);

        ByteBuffer grassChannel = hMap.createAlphaChannel(
                levelHeight - hMap.heightGetRelative(0.04f),
                levelHeight - hMap.heightGetRelative(0.02f),
                levelHeight + hMap.heightGetRelative(0.04f),
                levelHeight + hMap.heightGetRelative(0.04f));

        ByteBuffer rockChannel = hMap.createAlphaChannel(
                levelHeight + hMap.heightGetRelative(0.02f),
                levelHeight + hMap.heightGetRelative(0.04f),
                levelHeight + rockLine + hMap.heightGetRelative(0.04f),
                levelHeight + rockLine + hMap.heightGetRelative(0.04f));

        ByteBuffer snowChannel = hMap.createAlphaChannel(
                levelHeight + rockLine + hMap.heightGetRelative(0.02f),
                levelHeight + rockLine + hMap.heightGetRelative(0.04f),
                high,
                high);

        ByteBuffer texChannels1 = HeightMapTools.combineChannels(waterChannel, sandChannel, grassChannel, rockChannel);

        ByteBuffer texChannels2 = HeightMapTools.combineChannels(snowChannel, null, null, null);

        Image alphaImg1 = new Image(Image.Format.RGBA8,
                hMap.getSize(), hMap.getSize(), texChannels1);

        Image alphaImg2 = new Image(Image.Format.RGBA8,
                hMap.getSize(), hMap.getSize(), texChannels2);

        mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        mat_terrain.setReceivesShadows(true);

        mat_terrain.setTexture("AlphaMap", new Texture2D(alphaImg1));
        mat_terrain.setTexture("AlphaMap_1", new Texture2D(alphaImg2));

        Texture water = assetManager.loadTexture("graphics/water512.png");
        water.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("DiffuseMap", water);
        mat_terrain.setFloat("DiffuseMap_0_scale", 32f);

        Texture sand = assetManager.loadTexture("graphics/sand512.png");
        sand.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("DiffuseMap_1", sand);
        mat_terrain.setFloat("DiffuseMap_1_scale", 64f);

        Texture grass = assetManager.loadTexture("graphics/synthgrass2.dds");
        grass.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("DiffuseMap_2", grass);
        mat_terrain.setFloat("DiffuseMap_2_scale", 128f);

        Texture rock = assetManager.loadTexture("graphics/rock512.png");
        rock.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("DiffuseMap_3", rock);
        mat_terrain.setFloat("DiffuseMap_3_scale", 32f);

        Texture snow = assetManager.loadTexture("graphics/snow2_512.png");
        snow.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("DiffuseMap_4", snow);
        mat_terrain.setFloat("DiffuseMap_4_scale", 32f);

        hMap.normalize(255);
        hMap.reduce(4);

        terrain = new TerrainQuad("my terrain", World.blockSize + 1, hMap.getSize(), hMap.getHeightMap());
        terrain.setShadowMode(ShadowMode.Receive);

        terrain.setMaterial(mat_terrain);
        terrain.setLocalTranslation(0, -hMap.getNormalizedLevelHeight(255) * World.mapZScale, 0);
        Vector3f terrainScale = new Vector3f((float) World.mapXYScale, (float) World.mapZScale, (float) World.mapXYScale);
        terrain.setLocalScale(terrainScale);

        return terrain;
    }
}
