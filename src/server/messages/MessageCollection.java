/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.messages;

import com.jme3.asset.AssetManager;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Node;
import java.util.LinkedList;

/**
 *
 * @author Laurent
 */
@Serializable
public class MessageCollection<T extends ServerMessage> extends ServerMessage {
    
    private LinkedList<T> messages;
    
    public MessageCollection() {
        this.messages = new LinkedList<T>();
    }
    
    public MessageCollection(LinkedList<T> m) {
        this.messages = m;
    }

    @Override
    public void update(Node players, AssetManager assetManager) {
        for(T each : messages) {
            each.update(players, assetManager);
        }
    }
    
}
