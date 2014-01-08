package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication 
{
    Spatial ball;
    Spatial aiPad;
    Spatial playerPad;
    
    int height = cam.getHeight();
    int width = cam.getWidth();

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() 
    {
        initAssets();
        initCamera();
        initKeys();
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        aiLogic();
        ballLogic(tpf);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    private void initAssets()
    {
        ball = assetManager.loadModel("Models/ball/ball.j3o");
        aiPad = assetManager.loadModel("Models/paddle1/paddle1.j3o");
        playerPad = assetManager.loadModel("Models/paddle2/paddle2.j3o");
        
        ball.move(0, 0, 0);
        aiPad.move(-5, 0, 0);
        playerPad.move(5, 0, 0);
        
        ball.scale(0.10f);
        aiPad.scale(0.10f);
        playerPad.scale(0.10f);

        rootNode.attachChild(ball);
        rootNode.attachChild(aiPad);
        rootNode.attachChild(playerPad);
    }
    
    private void initCamera()
    {
        cam.setLocation(new Vector3f(0, 10, 0));
        
        Quaternion topDown = new Quaternion();
        topDown.fromAngleAxis(FastMath.PI / 2, new Vector3f(1,0,0));
        
        cam.setRotation(topDown);
        
        flyCam.setEnabled(false);
    }
    
    private void initKeys()
    {
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        
        inputManager.addListener(analogListener, "Up", "Down");
    }
    
    private AnalogListener analogListener = new AnalogListener()
    {
        public void onAnalog(String name, float value, float tpf) 
        {
            if (name.equals("Up"))
            {
                 Vector3f v = playerPad.getLocalTranslation();
                 playerPad.setLocalTranslation(v.x, v.y, v.z + 0.005f + value * speed);
            }
            if (name.equals("Down"))
            {
                Vector3f v = playerPad.getLocalTranslation();
                 playerPad.setLocalTranslation(v.x, v.y, v.z - 0.005f - value * speed);
            }
        }
    };
    
    private void aiLogic()
    {
        
    }
    
    private void ballLogic(float tpf)
    {
        Vector3f v = ball.getLocalTranslation();
        
        
        v.set(v.x + 2 * tpf, v.y, v.z + 2 * tpf);
        
        
        
        ball.setLocalTranslation(v);
    }
}
