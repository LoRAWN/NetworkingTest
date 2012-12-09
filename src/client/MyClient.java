package client;

import terrain.DiamondTerrain;
import client.messages.*;
import server.messages.*;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeContext;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.lwjgl.opengl.Display;

/**
 *
 * @author Laurent
 */
public class MyClient extends SimpleApplication {

    private final String name;
    private final String hostname;
    private final int port;
    private Client client;
    private MyClientListener clientlistener;
    private LinkedList<ServerMessage> messages;
    private MyActionListener actionlistener;
    private MyAnalogListener analoglistener;
    private Node players;
    private Node player;
    private Spatial sceneModel;
    private String mapPath;

    public MyClient(String hn, int i, String n) {
        super();
        hostname = hn;
        port = i;
        name = n;
        messages = new LinkedList<ServerMessage>();
        mapPath = "Scenes/flatgrass/flatgrass.j3o";
    }

    @Override
    public void simpleInitApp() {
        setUpPlayer();
        setUpLight();
        setUpFog();
        setUpScene();
        setUpInputs();
        setUpGui();
        try {
            setUpConnection();
        } catch (IOException ex) {
            Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        // Apply Updates
        synchronized (messages) {
            for (ServerMessage each : messages) {
                each.update(players, assetManager);
                if (each instanceof ChangeMap) {
                    this.mapPath = ((ChangeMap) each).getMapPath();
                    this.unloadMap();
                    this.loadMap(mapPath);
                }
            }
            messages.clear();
        }
        cam.setLocation(player.getLocalTranslation());
        // Retrieve User Inputs
        Vector3f vd = cam.getDirection();
        Vector3f left = cam.getLeft();
        // Send Update to Server
        UpdateInput ci = new UpdateInput(vd, left, actionlistener.getKeys());
        this.client.send(ci);
    }

    public void addMessage(ServerMessage mm) {
        synchronized (messages) {
            messages.add(mm);
        }
    }

    private void setUpInputs() {
        // Actions
        actionlistener = new MyActionListener(this);
        actionlistener.registerInputs(inputManager);
        // Analogs
        analoglistener = new MyAnalogListener(this);
        analoglistener.registerInputs(inputManager);
    }

    private void setUpConnection() throws IOException {
        // Client Messages
        com.jme3.network.serializing.Serializer.registerClass(HelloServer.class);
        com.jme3.network.serializing.Serializer.registerClass(GoodbyeServer.class);
        com.jme3.network.serializing.Serializer.registerClass(UpdateInput.class);
        // Server Messages
        com.jme3.network.serializing.Serializer.registerClass(AddPlayer.class);
        com.jme3.network.serializing.Serializer.registerClass(RemovePlayer.class);
        com.jme3.network.serializing.Serializer.registerClass(UpdatePlayer.class);
        com.jme3.network.serializing.Serializer.registerClass(ChangeMap.class);
        com.jme3.network.serializing.Serializer.registerClass(MessageCollection.class);
        // Start Client & Message Listener
        client = Network.connectToServer(hostname, port);
        clientlistener = new MyClientListener(this);
        // Server Messages
        client.addMessageListener(clientlistener, AddPlayer.class);
        client.addMessageListener(clientlistener, RemovePlayer.class);
        client.addMessageListener(clientlistener, UpdatePlayer.class);
        client.addMessageListener(clientlistener, MessageCollection.class);
        client.addMessageListener(clientlistener, ChangeMap.class);
        client.start();
        // Send HelloServer on connect
        client.send(new HelloServer(name));
    }

    private void setUpPlayer() {
        flyCam.setMoveSpeed(100);
        players = new Node("Players");
        player = new Node(name);
        //
        //Spatial model = assetManager.loadModel("Models/T-Rex.j3o");
        /*Box b = new Box(Vector3f.ZERO, 2, 10, 2);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);*/
        //
        //player.attachChild(model);
        players.attachChild(player);
        rootNode.attachChild(players);
    }

    private void setUpLight() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
    }

    private void setUpScene() {
        unloadMap();
        loadMap(mapPath);
    }

    private void unloadMap() {
        this.rootNode.detachChildNamed("Scene");
    }

    private void loadMap(String mapPath) {
        /*
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        Node scene = new Node("Scene");
        DiamondTerrain dt = new DiamondTerrain();
        Node t = dt.setUpTerrain(scene, assetManager);
        //
        TerrainQuad tq = (TerrainQuad) t.getChild("my terrain");
        List<Camera> cameras = new ArrayList<Camera>();
        cameras.add(getCamera());
        TerrainLodControl control = new TerrainLodControl(tq, cameras);
        tq.addControl(control);
        //
        scene.attachChild(t);
        sceneModel = scene;
        this.rootNode.attachChild(sceneModel);
        */
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        Spatial map = assetManager.loadModel(mapPath);
        map.setLocalTranslation(0, -5.2f, 0);
        map.setLocalScale(2);
        Node scene = new Node("Scene");
        scene.attachChild(map);
        sceneModel = scene;
        this.rootNode.attachChild(sceneModel);
    }

    @Override
    public void destroy() {
        client.close();
        super.destroy();
    }

    private void setUpGui() {
        this.guiNode.detachAllChildren();
        BitmapText hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize());       // font size
        hudText.setColor(ColorRGBA.Green);                             // font color
        hudText.setText("+");                                          // the text
        hudText.setLocalTranslation(this.settings.getWidth() / 2, this.settings.getHeight() / 2, 0); // position
        this.guiNode.attachChild(hudText);
        Spatial weapon = assetManager.loadModel("Models/weapon/weapon.j3o");
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Textures/weapon.png"));
        //mat.setColor("Color", ColorRGBA.Gray);
        weapon.setMaterial(mat);
        weapon.setLocalScale(40f);
        weapon.rotate(25f, 25f, 0f);
        weapon.setLocalTranslation(this.settings.getWidth() * 7 / 10, this.settings.getHeight() * 1 / 10, 0f);
        this.guiNode.attachChild(weapon);
    }

    private void setUpFog() {
        // May be outdated, need to check
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        FogFilter fog = new FogFilter();
        fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
        fog.setFogDistance(625);
        fog.setFogDensity(1.65f);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);
    }

    void sendDisconnect() {
        client.send(new RemovePlayer(name));
    }

    public static void main(String[] args) {
        String hn = "localhost";
        String n = "UnknownPlayer2";
        int p = 1337;
        int w = 1280;
        int h = 720;
        boolean fs = false;
        boolean vs = true;
        if (args != null && args.length == 7) {
            hn = args[0];
            n = args[2];
            p = Integer.parseInt(args[1]);
            h = Integer.parseInt(args[3]);
            w = Integer.parseInt(args[4]);
            fs = Boolean.parseBoolean(args[5]);
            vs = Boolean.parseBoolean(args[6]);
        }
        MyClient app = new MyClient(hn, p, n);
        AppSettings s = new AppSettings(true);
        s.setFrameRate(60);
        s.setTitle("Networking Test");
        if (fs) {
            w = Display.getDesktopDisplayMode().getWidth();
            h = Display.getDesktopDisplayMode().getHeight();
        }
        s.setResolution(w, h);
        s.setFullscreen(fs);
        s.setVSync(vs);
        app.setSettings(s);
        app.start(JmeContext.Type.Display);
    }
    
}