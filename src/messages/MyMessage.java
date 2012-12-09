package messages;

import com.jme3.asset.AssetManager;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Node;

/**
 *
 * @author Laurent
 */
//<editor-fold defaultstate="collapsed" desc="comment">
@Serializable
//</editor-fold>
public abstract class MyMessage extends AbstractMessage {
    
    public static final String DEFAULT_NAME = "UNKNOWN_PLAYER";
    
    protected final long timestamp;
    
    public MyMessage() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public MyMessage(String sender) {
        this.timestamp = System.currentTimeMillis();
    }
    
   public long getTimestamp() {
        return this.timestamp;
    }
   
    @Override
   public String toString() {
       return "["+timestamp+"][MyMessage]";
   }

    public abstract void update(Node players, AssetManager assetManager);
    
}
