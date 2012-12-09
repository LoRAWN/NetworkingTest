package client.messages;

import com.jme3.network.serializing.Serializable;
import messages.MyMessage;

/**
 *
 * @author Laurent
 */
//<editor-fold defaultstate="collapsed" desc="comment">
@Serializable
//</editor-fold>
public abstract class ClientMessage extends MyMessage {
   
    @Override
   public String toString() {
       return "["+timestamp+"][ClientMessage]";
   }
    
}
