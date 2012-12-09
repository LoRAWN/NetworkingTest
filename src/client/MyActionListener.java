/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import enums.Keys;

/**
 *
 * @author Laurent
 */
public class MyActionListener implements ActionListener {
    
    private int keys;
    
    private MyClient client;
    
    public MyActionListener(MyClient c) {
        this.client = c;
        this.keys = 0;
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equalsIgnoreCase("moveForward")) {
            setKey(Keys.FORWARD, isPressed);
        }
        if (name.equalsIgnoreCase("moveBackward")) {
            setKey(Keys.BACKWARD, isPressed);
        }
        if (name.equalsIgnoreCase("moveLeft")) {
            setKey(Keys.LEFT, isPressed);
        }
        if (name.equalsIgnoreCase("moveRight")) {
            setKey(Keys.RIGHT, isPressed);
        }
        if (name.equalsIgnoreCase("moveUp")) {
            setKey(Keys.UP, isPressed);
        }
        if (name.equalsIgnoreCase("moveDown")) {
            setKey(Keys.DOWN, isPressed);
        }
        if (name.equalsIgnoreCase("mouseLMB")) {
            setKey(Keys.LMB, isPressed);
        }
        if (name.equalsIgnoreCase("mouseMMB")) {
            setKey(Keys.MMB, isPressed);
        }
        if (name.equalsIgnoreCase("mouseRMB")) {
            setKey(Keys.RMB, isPressed);
        }
        if (name.equalsIgnoreCase("disconnect")) {
            client.sendDisconnect();
        }
        //TODO: calculate movement locally ?
    }
    
    private void setKey(Keys keyEnum, boolean isPressed) {
        if (isPressed) {
            int i = 1;
            i = i << keyEnum.ordinal();
            keys |= i;
        } else {
            int i = 1;
            i = i << keyEnum.ordinal();
            i = ~i;
            keys &= i;
        }
    }
    
    public int getKeys() {
        return keys;
    }
    
    public void registerInputs(InputManager inputManager) {
        inputManager.addMapping("moveForward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("moveBackward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("moveLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("moveRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("moveUp", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("moveDown", new KeyTrigger(KeyInput.KEY_LCONTROL));
        inputManager.addMapping("mouseLMB", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("mouseMMB", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        inputManager.addMapping("mouseRMB", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("disconnect", new KeyTrigger(KeyInput.KEY_F12));
        inputManager.addListener(this, "mouseLMB");
        inputManager.addListener(this, "mouseMMB");
        inputManager.addListener(this, "mouseRMB");
        inputManager.addListener(this, "moveForward");
        inputManager.addListener(this, "moveBackward");
        inputManager.addListener(this, "moveLeft");
        inputManager.addListener(this, "moveRight");
        inputManager.addListener(this, "moveUp");
        inputManager.addListener(this, "moveDown");
        inputManager.addListener(this, "disconnect");
    }
    
}
