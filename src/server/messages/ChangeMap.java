/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.messages;

import messages.MyMessage;
import com.jme3.asset.AssetManager;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Node;

/**
 *
 * @author Laurent
 */
@Serializable
public class ChangeMap extends ServerMessage {
    
    String mapPath;
    
    public ChangeMap() {
        mapPath = "";
    }
    
    public ChangeMap(String mapPath) {
        this.mapPath = mapPath;
    }
    
    public String getMapPath() {
        return this.mapPath;
    }

    @Override
    public void update(Node players, AssetManager assetManager) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
