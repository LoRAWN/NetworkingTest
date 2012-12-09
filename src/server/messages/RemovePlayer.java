package server.messages;

import messages.MyMessage;
import com.jme3.asset.AssetManager;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Node;

/**
 *
 * @author Laurent
 */
//<editor-fold defaultstate="collapsed" desc="comment">
@Serializable
//</editor-fold>
public class RemovePlayer extends RemoveEntity {
    
    private String name;
    
    public RemovePlayer() {
        this.name = MyMessage.DEFAULT_NAME;
    }
    
    public RemovePlayer(String name) {
        this.name = name;
    }
    
   @Override
   public String toString() {
       return "["+timestamp+"]["+name+"][Left the Server]";
   }

    @Override
    public void update(Node players,AssetManager assetManager) {
        players.detachChildNamed(name);
    }
    
}
