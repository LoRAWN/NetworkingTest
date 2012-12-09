package server;

import client.messages.*;
import server.messages.*;
import enums.Keys;
import terrain.DiamondTerrain;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Laurent
 */
public class MyServer extends SimpleApplication {

    private Server server;
    private final int port;
    private MyServerListener serverlistener;
    private Spatial sceneModel;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private String mapPath;

    private MyServer(int i, String map) {
        super();
        port = i;
        mapPath = map;
    }

    @Override
    public void simpleInitApp() {
        setUpPhysics();
        setUpScene();
        try {
            setUpConnections();
        } catch (IOException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        //simulate players
        for (HostedConnection player : server.getConnections()) {
            simulate(player);
        }
        //calculate updates
        LinkedList<UpdatePlayer> updates = new LinkedList<UpdatePlayer>();
        for (HostedConnection player : server.getConnections()) {
            String name = player.getAttribute("name");
            Vector3f pos = player.getAttribute("position");
            Vector3f rot = player.getAttribute("viewDirection");
            if (pos != null && rot != null && name != null) {
                UpdatePlayer update = new UpdatePlayer(name, pos, rot);
                updates.add(update);
            }
        }
        //transmit updates
        MessageCollection uc = new MessageCollection<UpdatePlayer>(updates);
        for (HostedConnection player : server.getConnections()) {
            player.send(uc);
        }
    }

    private void simulate(HostedConnection player) {
        UpdateInput ci = player.getAttribute("clientInput");
        CharacterControl cc = player.getAttribute("characterControl");
        if (ci == null || cc == null) {
            return;
        }
        cc.setViewDirection(ci.getViewDirection());
        Vector3f wd = Vector3f.ZERO.clone();
        Vector3f left = ci.getLeft();
        for (Keys key : ci.getKeys()) {
            switch (key) {
                case UP:
                    if(cc.onGround()) {
                        cc.jump();
                    }
                    break;
                case DOWN:
                    break;
                case FORWARD:
                    wd.addLocal(ci.getViewDirection().mult(speed));
                    break;
                case BACKWARD:
                    wd.addLocal(ci.getViewDirection().mult(-speed));
                    break;
                case LEFT:
                    wd.addLocal(left.mult(speed));
                    break;
                case RIGHT:
                    wd.addLocal(left.mult(-speed));
                    break;
                case LMB:
                    shoot(cc);
                default:
                    break;
            }
        }
        // TODO: figure out a good way to add air-movement control
        if(!cc.onGround()) {
            wd = cc.getWalkDirection();
        }
        //
        wd.setY(0f);
        cc.setWalkDirection(wd);
        player.setAttribute("position", cc.getPhysicsLocation());
        player.setAttribute("viewDirection", cc.getViewDirection());

    }

    private void setUpPhysics() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
    }

    private void unloadMap() {
        rootNode.detachChildNamed("Scene");
    }
    
    private void loadMap(String mapPath) {
        /*
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        Node scene = new Node("Scene");
        DiamondTerrain dt = new DiamondTerrain();
        scene.attachChild(dt.setUpTerrain(scene,assetManager));
        sceneModel = scene;
        rootNode.attachChild(sceneModel);
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
    
    private void setUpScene() {
        unloadMap();
        loadMap(mapPath);
        
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) sceneModel);
        landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);
        bulletAppState.getPhysicsSpace().add(landscape);
    }

    private void setUpConnections() throws IOException {
        // REMARK!!!!!: 
        // ORDER OF REGISTRATIONS MUST BE THE SAME 
        // ON CLIENTS & SERVER OLOLOLOLOLOLOLOLOLO - WHAT IS THIS SHIT ?
        //
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
        // Create Server & Message Listener
        server = Network.createServer(port);
        serverlistener = new MyServerListener(this);
        // ClientMessages
        server.addMessageListener(serverlistener, HelloServer.class);
        server.addMessageListener(serverlistener, GoodbyeServer.class);
        server.addMessageListener(serverlistener, UpdateInput.class);
        // Start Server
        server.start();
    }

    public void addCharacterControl(CharacterControl player) {
        bulletAppState.getPhysicsSpace().add(player);
    }

    public Server getServer() {
        return server;
    }

    @Override
    public void destroy() {
        // custom code
        server.close();
        super.destroy();
    }

    public static void main(String[] args) {
        int port = 1337;
        if (args != null && args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        String map = "Scenes/flatgrass/flatgrass.j3o";
        MyServer app = new MyServer(port,map);
        AppSettings s = new AppSettings(true);
        s.setFrameRate(120);
        app.setSettings(s);
        app.start(JmeContext.Type.Headless);
    }

    private void shoot(CharacterControl cc) {
        Vector3f vd = cc.getViewDirection().clone();
        Vector3f pos = cc.getPhysicsLocation().clone();
        vd.subtractLocal(pos).normalizeLocal();

        Ray ray = new Ray(pos, vd);
        CollisionResults results = new CollisionResults();
        sceneModel.collideWith(ray, results);
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            //TODO
        }
    }
    
    public String getMapPath() {
        return mapPath;
    }

}
