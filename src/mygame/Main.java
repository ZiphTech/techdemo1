package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
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
    
    AudioNode ballHit;
    AudioNode ballDeath;
    
    BitmapText txtScore;
    
    float aiMove = 1;
    
    int playerScore = 0;
    int compScore = 0;
    
    String scoreInfo = "Player: " + playerScore + "\t\t\t Computer: " + compScore;
    
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
        aiLogic(tpf);
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
        
        ballHit = new AudioNode(assetManager, "Sounds/BallHit.ogg");
        ballDeath = new AudioNode(assetManager, "Sounds/BallDeath.ogg");
        
        txtScore = new BitmapText(guiFont, false);
        txtScore.setSize(guiFont.getCharSet().getRenderedSize());
        txtScore.setColor(ColorRGBA.White);
        txtScore.setText(scoreInfo);
        txtScore.setLocalTranslation(150, txtScore.getLineHeight(), -1);
        
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
        
        guiNode.attachChild(txtScore);

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
                 
                 playerPad.setLocalTranslation(v.x, v.y + 5f * value, v.z);
            }
            if (name.equals("Down"))
            {
                Vector3f v = playerPad.getLocalTranslation();
                
                // Don't let the pad off the screen
                if (v.y < -3.5)
                {
                    v.y = -3.5f;
                }
                
                playerPad.setLocalTranslation(v.x, v.y  - 5f * value, v.z);
            }
        }
    };
    
    private void aiLogic(float tpf)
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
            //v.set(v.x, v.y + aiMove * tpf, v.z);
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
            increaseSpeed();
        }
        
        if (aiPad.collideWith(ballBound, results) > 0)
        {
            deltaX = -1;
            increaseSpeed();
            aiMove += 0.25f;
        }
        
        if (v.x > 5.5)
        {
            //PLAYER WIN; Computer LOOSE!
            playerScore++;
            resetBall();
        }
        else if (v.x < -5.5)
        {
            //PLAYER LOOSE; Computer WIN!
            compScore++;
            resetBall();
        }
        
        if (v.y > 4)
        {
            deltaY = -1;
            ballHit.setPitch(2f);
            ballHit.play();
        }
        else if (v.y < -4)
        {
            deltaY = 1;
            ballHit.setPitch(2f);
            ballHit.play();
        }
        ball.setLocalTranslation(v);
        txtScore.setText("Player: " + playerScore + "\t\t\t Computer: " + compScore);
    }
    
    private void increaseSpeed()
    {
        ballHit.setPitch(1);
        ballHit.play();
        ballXSpeed += 0.10f;
        ballYSpeed += 0.10f;
    }
    
    private void resetBall()
    {
        ballDeath.play();
        ball.setLocalTranslation(0, 0, 0);
        ballXSpeed = 3f;
        ballYSpeed = 1.25f;
        aiMove = 1;
    }
}
