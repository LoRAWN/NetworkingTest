/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.messages;

import com.jme3.asset.AssetManager;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Node;

/**
 *
 * @author Laurent
 */
@Serializable
public class GoodbyeServer extends ClientMessage {

    @Override
    public void update(Node players, AssetManager assetManager) {
        //DO NOTHING
    }
    
}
