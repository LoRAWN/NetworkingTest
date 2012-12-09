package server.messages;

import messages.MyMessage;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Node;
import server.messages.UpdateEntity;

/**
 *
 * @author Laurent
 */
@Serializable
public class UpdatePlayer extends UpdateEntity {
    
    private String name;
    
    private float posX;
    private float posY;
    private float posZ;
    
    private float vdX;
    private float vdY;
    private float vdZ;
    
    public UpdatePlayer() {
        name = MyMessage.DEFAULT_NAME;
        posX = 0;
        posX = 0;
        posX = 0;
        vdX = 0;
        vdY = 0;
        vdZ = 0;
    }
    
    public UpdatePlayer(String name,Vector3f position,Vector3f viewDirection) {
        this.name = name;
        posX = position.getX();
        posY = position.getY();
        posZ = position.getZ();
        vdX = viewDirection.getX();
        vdY = viewDirection.getY();
        vdZ = viewDirection.getZ();
    }

    public Vector3f getPosition() {
        return new Vector3f(posX,posY,posZ);
    }

    public Vector3f getViewDirection() {
        return new Vector3f(vdX,vdY,vdZ);
    }
    
   @Override
   public String toString() {
       return "["+timestamp+"]["+name+"][Updated]";
   }

    @Override
    public void update(Node players, AssetManager assetManager) {
        Node player = (Node)players.getChild(name);
        if(player!=null) {
            player.setLocalTranslation(posX, posY, posZ);
            Vector3f w = player.getLocalTranslation().add(vdX,vdY,vdZ);
            player.lookAt(w, Vector3f.UNIT_Y);
        }
    }
    
}
