/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.messages;

import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Laurent
 */
@Serializable
public abstract class AddEntity extends ServerMessage {
    
   @Override
   public String toString() {
       return "["+timestamp+"][AddEntity]";
   }
    
}
