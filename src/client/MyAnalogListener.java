/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author Laurent
 */
class MyAnalogListener implements AnalogListener {
    
    private MyClient client;
    private Quaternion tmpQuat;
    private float rotationSpeed;
    private Vector3f initialUpVec;

    public MyAnalogListener(MyClient client) {
        this.client = client;
        this.tmpQuat = new Quaternion();
        this.rotationSpeed  = 1f;
        this.initialUpVec = client.getCamera().getUp().clone();
    }

    public void onAnalog(String name, float value, float tpf) {
        Camera cam = client.getCamera();
        if(name.equalsIgnoreCase("mouseLeft")) {
             rotateCamera(cam,-value,initialUpVec);
        }
        if(name.equalsIgnoreCase("mouseRight")) {
             rotateCamera(cam,+value,initialUpVec);
        }
        if(name.equalsIgnoreCase("mouseUp")) {
             rotateCamera(cam,value,cam.getLeft());
        }
        if(name.equalsIgnoreCase("mouseDown")) {
             rotateCamera(cam,-value,cam.getLeft());
        }
        fixZRot();
    }
    
    private void fixZRot() {
        //this will return the actual rotation angles in radians
        float[] angles=new float[3];
        Camera cam = client.getCamera();
        cam.getRotation().toAngles(angles);
        //check the x rotation
        int i = 0;
        if(angles[i]>FastMath.HALF_PI){
                angles[i]=FastMath.HALF_PI;
                cam.setRotation(tmpQuat.fromAngles(angles));
        }else if(angles[i]<-FastMath.HALF_PI){
                angles[i]=-FastMath.HALF_PI;
                cam.setRotation(tmpQuat.fromAngles(angles));
        }
    }

    private void rotateCamera(Camera cam, float value, Vector3f axis) {
        Matrix3f mat = new Matrix3f();
        mat.fromAngleNormalAxis(rotationSpeed * value, axis);

        Vector3f up = cam.getUp();
        Vector3f left = cam.getLeft();
        Vector3f dir = cam.getDirection();

        mat.mult(up, up);
        mat.mult(left, left);
        mat.mult(dir, dir);
        tmpQuat = tmpQuat.fromAxes(left, up, dir);
        tmpQuat.normalizeLocal();

        cam.setAxes(tmpQuat);

    }
    
    public void registerInputs(InputManager inputManager) {
        inputManager.addMapping("mouseLeft", new MouseAxisTrigger(MouseInput.AXIS_X,false));
        inputManager.addMapping("mouseRight", new MouseAxisTrigger(MouseInput.AXIS_X,true));
        inputManager.addMapping("mouseUp", new MouseAxisTrigger(MouseInput.AXIS_Y,true));
        inputManager.addMapping("mouseDown", new MouseAxisTrigger(MouseInput.AXIS_Y,false));
        inputManager.addListener(this, "mouseLeft");
        inputManager.addListener(this, "mouseRight");
        inputManager.addListener(this, "mouseUp");
        inputManager.addListener(this, "mouseDown");
    }
    
}
