package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;

/**
 * Pong clone focused on a simple, first learning enviornment.
 * @author Dan Shelley
 */
public class Main extends SimpleApplication 
{
    Spatial ball;
    Spatial aiPad;
    Spatial playerPad;
    
    public static void main(String[] args) {
        
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Pong V0.1 {BETA} || Drew Brown | Dan Shelley");
        
        Main app = new Main();
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() 
    {
        
        initAssets();
        initCamera();
        initKeys();
        setDisplayStatView(false);
        setDisplayFps(false);
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
        
        aiPad.setLocalTranslation(5, 0, 0);
        playerPad.setLocalTranslation(-5, 0, 0);
        
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(FastMath.PI / 2, new Vector3f(1, 0, 0));
        
        aiPad.rotate(rot);
        playerPad.rotate(rot);
        ball.rotate(rot);
        
        ball.scale(0.10f);
        aiPad.scale(0.10f);
        playerPad.scale(0.10f);

        rootNode.attachChild(ball);
        rootNode.attachChild(aiPad);
        rootNode.attachChild(playerPad);
    }
    
    private void initCamera()
    {
        cam.setLocation(new Vector3f(0, 0 , 10));
        
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
                 
                 // Don't let the pad off the screen
                 if (v.y > 3.5)
                 {
                     v.y = 3.5f;
                 }
                 
                 playerPad.setLocalTranslation(v.x, v.y + 4f * value, v.z);
            }
            if (name.equals("Down"))
            {
                Vector3f v = playerPad.getLocalTranslation();
                
                // Don't let the pad off the screen
                if (v.y < -3.5)
                {
                    v.y = -3.5f;
                }
                
                playerPad.setLocalTranslation(v.x, v.y  - 4f * value, v.z);
            }
        }
    };
    
    private void aiLogic()
    {
        Vector3f v = aiPad.getLocalTranslation();
        Vector3f b = ball.getLocalTranslation();
        
        if (b.y > 3.5)
        {
            v.y = 3.5f;
        }
        else if (b.y < -3.5)
        {
            v.y = -3.5f;
        }
        else
        {
            v.y = b.y;
        }
        aiPad.setLocalTranslation(v);
    }
    
    float deltaX = 1;
    float deltaY = 1;
    
    float ballXSpeed = 3f;
    float ballYSpeed = 1.25f;
    
    private void ballLogic(float tpf)
    {
        Vector3f v = ball.getLocalTranslation();
        CollisionResults results = new CollisionResults();
        BoundingVolume ballBound = ball.getWorldBound();
        
        v.set(v.x + ballXSpeed * tpf * deltaX, v.y + ballYSpeed * tpf * deltaY, v.z);
        
        
        if (playerPad.collideWith(ballBound, results) > 0)
        {
            deltaX = 1;
        }
        
        if (aiPad.collideWith(ballBound, results) > 0)
        {
            deltaX = -1;
        }
        
        
        
        if (v.x > 5.5)
        {
            //deltaX = -1;
            //PLAYER WIN; Computer LOOSE!
        }
        else if (v.x < -5.5)
        {
            //deltaX = 1;
            //PLAYER LOOSE; Computer WIN!
        }
        
        if (v.y > 4)
        {
            deltaY = -1;
        }
        else if (v.y < -4)
        {
            deltaY = 1;
        }
        
        ball.setLocalTranslation(v);
    }
}
