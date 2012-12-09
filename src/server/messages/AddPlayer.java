package server.messages;

import messages.MyMessage;
import com.jme3.asset.AssetManager;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Laurent
 */
//<editor-fold defaultstate="collapsed" desc="comment">
@Serializable
//</editor-fold>
public class AddPlayer extends AddEntity {
    
    private String name;
    
    public AddPlayer() {
        this.name = MyMessage.DEFAULT_NAME;
    }
    
   public AddPlayer(String name) {
        this.name = name;
    }
    
   @Override
   public String toString() {
       return "["+timestamp+"][Joined the Server]";
   }

    @Override
    public void update(Node players,AssetManager assetManager) {
        
        Node player = (Node)players.getChild(name);
        if(player==null) {
            player = new Node(name);
            /*
            Box b = new Box(Vector3f.ZERO, 2, 10, 2);
            Geometry geom = new Geometry("Box", b);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Blue);
            geom.setMaterial(mat);
             *
             */
            Spatial geom = assetManager.loadModel("Models/T-Rex.j3o");
            geom.setLocalScale(2f);
            player.attachChild(geom);
            players.attachChild(player);
        }
        
    }
    
}
