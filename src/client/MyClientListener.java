package client;

import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import server.messages.ServerMessage;

/**
 *
 * @author Laurent
 */
public class MyClientListener implements MessageListener<Client> {
    
    private final MyClient myclient;
    
    public MyClientListener(MyClient myclient) {
        this.myclient = myclient;
    }

    public void messageReceived(Client source, Message m) {
        myclient.addMessage((ServerMessage)m);
    }
    
}
