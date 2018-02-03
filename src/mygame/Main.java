/*@author Silvia Fuentes
10/19/2017
Lab - 12 - 3330*/
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;

public class Main extends SimpleApplication {

    //GLOBAL VARIABLES
    GameAppState gamestate = new GameAppState();
    RenderManager rendermanager;

    public static void main(String[] args) {
        Main app = new Main();
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.put("Title", "Lab 14");
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        stateManager.attach(gamestate);
        setDisplayFps(false);
        setDisplayStatView(false);
        hudText();
        
      /***************CAMERA VIEWS*********************/
        viewPort.setBackgroundColor(ColorRGBA.Yellow);
        cam.setViewPort(0f, 1f, 0f, 1f);
        cam.setLocation(new Vector3f(0f, 1f,10f));
        cam.setRotation(new Quaternion(0f, 0.5f, 0f, 0f));

        //SECOND VIEW
         Camera cam2 = cam.clone();
        cam2.setViewPort(0.8f, .95f, .8f, .95f);
        cam2.setLocation(new Vector3f(0f,4f, -10f));
        cam2.setRotation(new Quaternion(0f, 0f, 0f, .25f));
        
        ViewPort view2 = renderManager.createMainView("Top Right", cam2);
        view2.setBackgroundColor(ColorRGBA.Black);
        view2.setClearFlags(true, true, true);
        view2.attachScene(rootNode);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private void hudText() { //Heads Up Display Information
        // Health info
        BitmapText health = new BitmapText(guiFont, false);
        health.setText("Hit the duck 4 times");
        health.setSize(guiFont.getCharSet().getRenderedSize());
        health.setColor(ColorRGBA.Brown);
        health.setLocalTranslation(health.getLineHeight(), 100, 0);
        //Inventory Info
        BitmapText inv = new BitmapText(guiFont, false);
        inv.setText("Inventory");
        inv.setSize(guiFont.getCharSet().getRenderedSize());
        inv.setColor(ColorRGBA.Black);
        inv.setLocalTranslation(inv.getLineHeight(), 75, 0);
        // Current Location
        BitmapText cl = new BitmapText(guiFont, false);
        cl.setText("Current location");
        cl.setSize(guiFont.getCharSet().getRenderedSize());
        cl.setColor(ColorRGBA.Black);
        cl.setLocalTranslation(cl.getLineHeight(), 50, 0);
        //Crosshairs
        BitmapText crosshairs = new BitmapText(guiFont, false);
        crosshairs.setText("+");
        crosshairs.setSize(guiFont.getCharSet().getRenderedSize());
        crosshairs.setColor(ColorRGBA.White);
        crosshairs.setLocalTranslation(settings.getWidth() / 2 - crosshairs.getLineWidth() / 2,
                settings.getHeight() / 2 + crosshairs.getLineHeight() / 2, 0);
        //Crosshairs hit
        BitmapText hitcount = new BitmapText(guiFont, false);
        hitcount.setText("Hit Count: ");
        hitcount.setSize(guiFont.getCharSet().getRenderedSize());
        hitcount.setColor(ColorRGBA.Black);
        hitcount.setLocalTranslation(hitcount.getLineHeight(), 25, 0);
        //Count #
        gamestate.count = new BitmapText(guiFont, false);
        gamestate.getCount().setSize(guiFont.getCharSet().getRenderedSize());
        gamestate.getCount().setColor(ColorRGBA.Black);
        gamestate.getCount().setLocalTranslation(88, 25, 0);
        
        
        //Attaching nodes
        guiNode.attachChild(hitcount);
        guiNode.attachChild(health);
        guiNode.attachChild(inv);
        guiNode.attachChild(cl);
        guiNode.attachChild(crosshairs);
        guiNode.attachChild(gamestate.getCount());
    }

}
