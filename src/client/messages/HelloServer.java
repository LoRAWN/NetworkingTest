package client.messages;

import com.jme3.asset.AssetManager;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Node;

/**
 *
 * @author Laurent
 */
@Serializable
public class HelloServer extends ClientMessage {
    
    private String name;
    
    public HelloServer(String name) {
        this.name = name;
    }
    
    public HelloServer() {
        this.name = DEFAULT_NAME;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public void update(Node players, AssetManager assetManager) {
        //DO NOTHING
    }
    
}
