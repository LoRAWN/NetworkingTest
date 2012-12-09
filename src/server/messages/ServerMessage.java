package server.messages;

import messages.MyMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Laurent
 */
//<editor-fold defaultstate="collapsed" desc="comment">
@Serializable
//</editor-fold>
public abstract class ServerMessage extends MyMessage {
   
    @Override
   public String toString() {
       return "["+timestamp+"][ServerMessage]";
   }
    
}
