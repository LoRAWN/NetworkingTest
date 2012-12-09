package server;

import client.messages.GoodbyeServer;
import client.messages.HelloServer;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import client.messages.UpdateInput;
import java.util.LinkedList;
import server.messages.AddPlayer;
import server.messages.ChangeMap;
import server.messages.MessageCollection;
import server.messages.RemovePlayer;

/**
 *
 * @author Laurent
 */
class MyServerListener implements MessageListener<HostedConnection> {

    private final MyServer server;

    MyServerListener(MyServer server) {
        this.server = server;
    }

    public void messageReceived(HostedConnection source, Message m) {
        if (m instanceof HelloServer) {
            //
            HelloServer hs = (HelloServer) m;
            // Initialize Player
            source.setAttribute("name", hs.getName());
            source.setAttribute("position", Vector3f.ZERO.clone());
            source.setAttribute("viewDirection", Vector3f.UNIT_X.clone());
            // Create Physics for Player
            CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
            CharacterControl player = new CharacterControl(capsuleShape, 0.05f);
            player.setJumpSpeed(20f);
            player.setFallSpeed(40f);
            player.setGravity(40f);
            player.setPhysicsLocation(new Vector3f(0, 300, 0));
            source.setAttribute("characterControl", player);
            server.addCharacterControl(player);
            // Send AddPlayers to new Clients
            AddPlayer ap = new AddPlayer(hs.getName());
            // Send AddPlayer to the other Clients (also send AddPlayer to new Client)
            server.getServer().broadcast(ap);
            sendAddPlayersTo(source);
            // Send ChangeMap to Player, to load the Map
            source.send(new ChangeMap(server.getMapPath()));
        }
        if (m instanceof GoodbyeServer) {
            String name = source.getAttribute("name");
            RemovePlayer rp = new RemovePlayer(name);
            server.getServer().broadcast(rp);
            //source.close("Player Left");
        }
        if (m instanceof UpdateInput) {
            UpdateInput ci = (UpdateInput) m;
            //System.out.println(ci.toString());
            source.setAttribute("clientInput", ci);
        }
    }

    private void sendAddPlayersTo(HostedConnection source) {
        LinkedList<AddPlayer> a = new LinkedList<AddPlayer>();
        for (HostedConnection each : this.server.getServer().getConnections()) {
            if (each.getId() == source.getId()) {
                continue;
            } else {
                String name = (String) each.getAttribute("name");
                if (name != null) {
                    AddPlayer p = new AddPlayer(name);
                    a.add(p);
                }
            }
        }
        MessageCollection<AddPlayer> apc = new MessageCollection<AddPlayer>(a);
        source.send(apc);
    }
}
