package client.messages;

import enums.Keys;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Node;
import java.util.LinkedList;

/**
 *
 * @author Laurent
 */
@Serializable
public class UpdateInput extends ClientMessage {
    
    private int keys;
    private Vector3f vd;
    private Vector3f left;
    
    public UpdateInput() {
        this.keys = 0;
        this.vd = Vector3f.UNIT_X.clone();
        this.left = Vector3f.UNIT_Z.clone();
    }
    
    public UpdateInput(Vector3f vd,Vector3f left, int keys) {
        this.vd = vd;
        this.keys = keys;
        this.left = left;
    }

    public Vector3f getViewDirection() {
        return this.vd;
    }

    public LinkedList<Keys> getKeys() {
        LinkedList<Keys> k = new LinkedList<Keys>();
        int copy = keys;
        int count = 0;
        while(copy!=0) {
            int temp = 1;
            temp &= copy;
            if(temp==1) {
                Keys key = Keys.fromOrdinal(count);
                k.add(key);
            }
            count++;
            copy = copy >> 1;
        }
        return k;
    }
        
    public void setKeys(int keys) {
        this.keys = keys;
    }
    
    @Override
    public String toString() {
        return "[keys="+keys+"]";
    }

    @Override
    public void update(Node players,AssetManager assetManager) {
        //DO NOTHING
    }

    public Vector3f getLeft() {
        return this.left;
    }
    
}
