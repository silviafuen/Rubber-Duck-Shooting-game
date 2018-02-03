package mygame;

import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.cinematic.Cinematic;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.cinematic.events.SoundEvent;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;
import com.jme3.util.SkyFactory;
import static com.jme3.util.SkyFactory.EnvMapType.CubeMap;
import static com.jme3.util.SkyFactory.EnvMapType.EquirectMap;
import static com.jme3.util.SkyFactory.EnvMapType.SphereMap;
import com.jme3.water.SimpleWaterProcessor;

public class GameAppState extends AbstractAppState {

    /*GLOBAL VARIABLES*/
    public SimpleApplication app;
    public Node rootNode = new Node("Base");
    public Node objects;
    public Node rotateNode;
    public Node spinNode; //duck spinning around
    public Node alienship;
    public AudioNode bg;
    public AudioNode hit;
    public Geometry mark;
    BitmapText count;
    public Camera cam;
    public Camera cam2;
    int k = 1;
    int j = 0;
    Geometry sgeom;
    Spatial spatial;
    //ViewPort viewPort;
    RenderManager rendermanager;
    /*KEY TRIGGERS*/
    public final static Trigger f1_Trigger = new KeyTrigger(KeyInput.KEY_F1);
    public final static Trigger f2_Trigger = new KeyTrigger(KeyInput.KEY_F2);
    public final static Trigger f3_Trigger = new KeyTrigger(KeyInput.KEY_F3);

    /*GET COUNT TO RETURN MAIN for HUD*/
    public BitmapText getCount() {
        return count;
    }

    public void setCount(BitmapText count) {
        this.count = count;
    }

    @Override
    public void initialize(AppStateManager stateManager, //INITIALIZE APP STATE
            Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.app.getRootNode().attachChild(attachItems());
        this.app.getRootNode().attachChild(rotateSphere());
        this.app.getRootNode().attachChild(duckModel());
    }

    @Override
    public void update(float tpf) { //UPDATE NODES IN APP STATE
        rotateNode.rotate(0, .6f * FastMath.DEG_TO_RAD, 0);
    }

    /*ATTACHING METHODS TO BASENODE*/
    public Node attachItems() {
        geoObjects();
        geoMonster();
        worldFloor();
        worldSky();
        worldWater();
        audioEffects();
        listenerMapping();

        return rootNode;
    }

    /**
     * ************OBJECTS IN WORLD********************
     */
    public void geoObjects() {
        Box box = new Box(1, 1, 1);
        Geometry geombox = new Geometry("Box", box);
        geombox.setLocalTranslation(new Vector3f(2, 1, 2));
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        geombox.setMaterial(mat);
        mat.setColor("Diffuse", ColorRGBA.DarkGray);
        geombox.setShadowMode(ShadowMode.CastAndReceive);

        Dome dome = new Dome(2, 4, 2);
        Geometry geomdome = new Geometry("Dome", dome);
        geomdome.setLocalTranslation(new Vector3f(-7, 0, 4));
        Texture domeTexture = app.getAssetManager().loadTexture("/Textures/gradient.jpg"); //texture
        Material domemat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        geomdome.setMaterial(domemat);
        domemat.setTexture("ColorMap", domeTexture);
        domemat.setColor("Color", ColorRGBA.White);
        geomdome.setShadowMode(ShadowMode.CastAndReceive);

        Box wirebox = new Box(1, 1, 1);
        Geometry boxwire = new Geometry("Box", wirebox);
        boxwire.setLocalTranslation(new Vector3f(7, 1, 9));
        Texture boxTexture = app.getAssetManager().loadTexture("/Textures/paint.jpg"); //texture
        Material wiremat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        boxwire.setMaterial(wiremat);
        wiremat.setTexture("ColorMap", boxTexture);
        wiremat.setColor("Color", ColorRGBA.Pink);
        boxwire.setShadowMode(ShadowMode.CastAndReceive);

        objects = new Node("objects");  //attaching nodes
        objects.attachChild(geombox);
        objects.attachChild(geomdome);
        objects.attachChild(boxwire);
        rootNode.attachChild(objects);
    }

    public Node rotateSphere() {
        Sphere sphere = new Sphere(16, 16, .03f);
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        sgeom = new Geometry("Box", sphere);
        Material smat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        smat.setBoolean("UseMaterialColors", true); //shine
        smat.setColor("Diffuse", ColorRGBA.Pink);
        smat.setColor("Specular", ColorRGBA.White);
        smat.setFloat("Shininess", 100f);
        sgeom.setMaterial(smat);
        sgeom.setShadowMode(ShadowMode.CastAndReceive);

        MotionPath path = new MotionPath();
        path.addWayPoint(new Vector3f(-8, 1, 13));
        path.addWayPoint(new Vector3f(-8, 0, 13));
        path.addWayPoint(new Vector3f(-7, 0, 13));
        path.addWayPoint(new Vector3f(-7, 1, 13));
        path.setCycle(true);
        path.setCurveTension(1.0f);
        rotateNode = new Node("rotate");
        rotateNode.attachChild(sgeom);
        rotateNode.rotate(0, FastMath.DEG_TO_RAD * 45, 0);

        //attaching nodes
        MotionEvent mEvent = new MotionEvent(rotateNode, path); //MOTion EVENT
        mEvent.setLoopMode(LoopMode.Loop);
        Cinematic c = new Cinematic(rotateNode, 0); //CINEMATIC EVENT 2
        app.getStateManager().attach(c);
        c.addCinematicEvent(0, mEvent);
        c.setLoopMode(LoopMode.Loop);
        c.play();

        rootNode.attachChild(rotateNode);
        return rootNode;
    }

    public void geoMonster() {
        //HEAD
        Sphere sphere = new Sphere(15, 15, .4f);
        Geometry geomSphere = new Geometry("Sphere", sphere);
        Material matSphere = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        geomSphere.setMaterial(matSphere);
        matSphere.setColor("Color", ColorRGBA.LightGray);
        geomSphere.setLocalTranslation(new Vector3f(-5, 4, 0));
        //BLUE SPHERES
        Sphere sphere1 = new Sphere(15, 15, .2f);
        Geometry geomSphere1 = new Geometry("Sphere", sphere1);
        setMaterial(geomSphere1);
        geomSphere1.setLocalTranslation(new Vector3f(-6, 4, -1));

        Sphere sphere2 = new Sphere(15, 15, .2f);
        Geometry geomSphere2 = new Geometry("Sphere", sphere1);
        setMaterial(geomSphere2);
        geomSphere2.setLocalTranslation(new Vector3f(-6, 4, 0));

        Sphere sphere3 = new Sphere(15, 15, .2f);
        Geometry geomSphere3 = new Geometry("Sphere", sphere1);
        setMaterial(geomSphere3);
        geomSphere3.setLocalTranslation(new Vector3f(-5, 4, -1));

        Sphere sphere4 = new Sphere(15, 15, .2f);
        Geometry geomSphere4 = new Geometry("Sphere", sphere1);
        setMaterial(geomSphere4);
        geomSphere4.setLocalTranslation(new Vector3f(-5, 4, 1));
        //END OF SPHERES 

        //DOME 1
        Dome dome = new Dome(new Vector3f(1, 1, .1f), 15, 10, .3f);
        Geometry geomDome = new Geometry("Dome", dome);
        Material matDome = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        geomDome.setMaterial(matDome);
        matDome.setColor("Color", ColorRGBA.Pink);
        geomDome.setLocalTranslation(new Vector3f(-6, 3, 1));

        //DOME 1
        Dome dome2 = new Dome(new Vector3f(1, 1, .1f), 15, 10, .3f);
        Geometry geomDome2 = new Geometry("Dome", dome2);
        Material matDome2 = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        geomDome2.setMaterial(matDome2);
        matDome2.setColor("Color", ColorRGBA.Pink);
        geomDome2.setLocalTranslation(new Vector3f(-6, 3, -1));

        //BODY
        Cylinder cylinder = new Cylinder(15, 25, 2, .2f, true);
        Geometry geomCylin = new Geometry("Cylinder", cylinder);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        geomCylin.setMaterial(mat);
        mat.setColor("Color", ColorRGBA.White);
        geomCylin.setLocalTranslation(new Vector3f(-5, 4, 0));
        Quaternion roll090 = new Quaternion(); // create the quaternion
        roll090.fromAngleAxis(90 * FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
        geomCylin.setLocalRotation(roll090);
        geomCylin.setShadowMode(ShadowMode.CastAndReceive);

        alienship = new Node("AlienShip");
        alienship.attachChild(geomDome);
        alienship.attachChild(geomDome2);
        alienship.attachChild(geomSphere);
        alienship.attachChild(geomSphere1);
        alienship.attachChild(geomSphere2);
        alienship.attachChild(geomSphere3);
        alienship.attachChild(geomSphere4);
        alienship.attachChild(geomCylin);

        MotionPath pat = new MotionPath(); // 6 WAY POINTS
        pat.addWayPoint(new Vector3f(-10, 4, 0));
        pat.addWayPoint(new Vector3f(0, 4, -10));
        pat.addWayPoint(new Vector3f(10, 4, -4));
        pat.addWayPoint(new Vector3f(10, 4, 0));
        pat.addWayPoint(new Vector3f(0, 4, 10));
        pat.addWayPoint(new Vector3f(-10, 4, 4));
        pat.setCycle(true);
        pat.setCurveTension(1.0f);

        alienship.rotate(0, FastMath.DEG_TO_RAD * 45, 0);
        objects.attachChild(alienship);
        rootNode.attachChild(objects);
        MotionEvent met = new MotionEvent(alienship, pat); //MOTION EVENT
        met.setLoopMode(LoopMode.Cycle);

        SoundEvent sEvent = new SoundEvent("Sounds/warp.ogg"); //SOUND EVENT
        Cinematic cin = new Cinematic(alienship, 3); //CINEMATIC EVENT 2
        app.getStateManager().attach(cin);
        cin.addCinematicEvent(0, met);
        cin.addCinematicEvent(0, sEvent);
        cin.play();
    }

    public void setMaterial(Geometry geom) { ///method for blue color material
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        geom.setMaterial(mat);
        mat.setColor("Color", ColorRGBA.Blue);
    }

    public Node duckModel() {
        spatial = app.getAssetManager().loadModel("Models/Duck/RubberDuck.j3o");
        spatial.setLocalTranslation(5, 0, 14);
        Material spatMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        Texture duckTexture = app.getAssetManager().loadTexture("Textures/yellow.png");
        spatMaterial.setTexture("ColorMap", duckTexture);
        spatial.setShadowMode(ShadowMode.CastAndReceive);

        MotionPath path = new MotionPath();
        /**/
        path.addWayPoint(new Vector3f(-26, 0, 0));
        path.addWayPoint(new Vector3f(0, 0, -26));
        path.addWayPoint(new Vector3f(16, 0, -14));
        path.addWayPoint(new Vector3f(16, 0, 0));
        path.addWayPoint(new Vector3f(0, 0, 26));
        path.addWayPoint(new Vector3f(-16, 0, 14));
        path.setCycle(true);
        path.setCurveTension(1.0f);

        spinNode = new Node("Spinning");
        spinNode.attachChild(spatial); //attaching the duck to the node
        spinNode.rotate(0, FastMath.DEG_TO_RAD * 45, 0);
        rootNode.attachChild(spinNode); // attaching the spin node with the duck to the main node
        MotionEvent me = new MotionEvent(spinNode, path);
        me.setLoopMode(LoopMode.Cycle);
        Cinematic cin1 = new Cinematic(spinNode, 0); //CINMATIC EVENT 3
        app.getStateManager().attach(cin1);
        cin1.addCinematicEvent(0, me);
        cin1.play();
        cin1.setLoopMode(LoopMode.Cycle);
        return rootNode;
    }

    /**
     * ************SURROUNDING ELEMENTS**************
     */
    public void worldFloor() {
        Spatial terrain = app.getAssetManager().loadModel("Textures/rain.j3o");
        /*  Box f = new Box(25, 0, 25);
        Geometry floor = new Geometry("Box", f);
        floor.setLocalTranslation(0, 0, 0);
        Texture floorTexture = app.getAssetManager().loadTexture("Textures/water.jpg");
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", floorTexture);
        floor.setMaterial(mat);
        floor.setShadowMode(ShadowMode.Receive);
        rootNode.attachChild(floor);*/
        rootNode.attachChild(terrain);//attaching 
    }

    public void worldWater() {
        SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(app.getAssetManager());
        waterProcessor.setReflectionScene(rootNode);
        waterProcessor.setLightPosition(new Vector3f(0.55f, -0.82f, 0.15f));
        Vector3f waterLocation = new Vector3f(0, 1, 0);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
        app.getViewPort().addProcessor(waterProcessor);
        waterProcessor.setWaterDepth(10);
        waterProcessor.setDistortionScale(0.15f);
        waterProcessor.setWaveSpeed(0.05f);

        Quad quad = new Quad(400, 400);
        quad.scaleTextureCoordinates(new Vector2f(6f, 6f));
        
        Geometry water = new Geometry("water", quad);
        water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        water.setLocalTranslation(-200, 1, 250);
        water.setShadowMode(RenderQueue.ShadowMode.Receive);
        water.setMaterial(waterProcessor.getMaterial());
        rootNode.attachChild(water);
    }

    public void worldSky() {
        /**
         * ** SKY ***
         */
        Spatial sky = SkyFactory.createSky(app.getAssetManager(), "Textures/BrightSky.dds", CubeMap);
        rootNode.attachChild(sky);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0, 0, -2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);

        rootNode.addLight(sun);
        objects.setShadowMode(ShadowMode.CastAndReceive);
        addShadow(sun);
    }

    private void addShadow(DirectionalLight light) {
        DirectionalLightShadowRenderer shadow = new DirectionalLightShadowRenderer(app.getAssetManager(), 200, 2);
        shadow.setLight(light);
        app.getViewPort().addProcessor(shadow);
    }

    public void audioEffects() {
        //Background Audio
        bg = new AudioNode(app.getAssetManager(), "/Sounds/wind.ogg", AudioData.DataType.Stream);
        bg.setLooping(true);  // activate continuous playing
        bg.setPositional(true);
        bg.setVolume(2);
        //bg.play();
        //Situation Audio

        hit = new AudioNode(app.getAssetManager(), "/Sounds/chips.ogg", AudioData.DataType.Buffer);
        hit.setPositional(false);
        hit.setLooping(false);
        hit.setVolume(3);

        //rootNode.attachChild(bg); //attaching 
        rootNode.attachChild(hit);
    }

    /**
     * **************ACTION LISTENERS & LISTENING MAPS***************
     */
    private void listenerMapping() {
        app.getInputManager().addMapping("Dot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));  //DOT ACTION
        app.getInputManager().addListener(dotListener, "Dot");
        //RED listener
        app.getInputManager().addMapping("Set Red", new KeyTrigger(KeyInput.KEY_F1));
        app.getInputManager().addListener(actionListener, "Set Red");
        //PINK listener
        app.getInputManager().addMapping("Set Blue", new KeyTrigger(KeyInput.KEY_F2));
        app.getInputManager().addListener(actionListener, "Set Blue");
        //BLUE listener
        app.getInputManager().addMapping("Set Pink", new KeyTrigger(KeyInput.KEY_F3));
        app.getInputManager().addListener(actionListener, "Set Pink");

        app.getInputManager().addMapping("Hit", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addListener(hitListener, "Hit");

        app.getInputManager().addMapping("Duck", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addListener(duckListener, "Duck");
    }

    private ActionListener dotListener = new ActionListener() {  //setting color method
        @Override
        public void onAction(String name, boolean pressed, float tpf) {
            if (name.equals("Dot") && pressed) {
                dotAction();
            }
        }
    };
    private ActionListener hitListener = new ActionListener() {     //listening for hit
        @Override
        public void onAction(String name, boolean pressed, float tpf) {
            if (name.equals("Hit") && pressed) {
                hitAction(count);
            }
        }
    };
    private ActionListener duckListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean pressed, float tpf) {
            if (name.equals("Duck") && pressed) {
                duckAction();
            }
        }
    };
    private ActionListener actionListener = new ActionListener() {  //setting color method
        @Override
        public void onAction(String name, boolean pressed, float tpf) {
            setColor(name);
        }
    };

    /*ACTIONS SET BY LISTENERS*/
    private void setColor(String name) { //SET NEW COLOR OF OBJECTS
        Box box = new Box(1, 1, 1);
        Geometry newbox = new Geometry("Box", box);
        newbox.setLocalTranslation(new Vector3f(2, 1, 2));
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        switch (name) {
            case "Set Red": {
                mat.setColor("Color", ColorRGBA.Red);
                newbox.setMaterial(mat);
                break;
            }
            case "Set Blue": {
                mat.setColor("Color", ColorRGBA.Blue);
                newbox.setMaterial(mat);
                break;
            }
            case "Set Pink": {
                mat.setColor("Color", ColorRGBA.Pink);
                newbox.setMaterial(mat);
                break;
            }
            case "Set Yellow": {
                mat.setColor("Color", ColorRGBA.Yellow);
                newbox.setMaterial(mat);
                break;
            }
            case "Set Green": {
                mat.setColor("Color", ColorRGBA.Green);
                newbox.setMaterial(mat);
                break;
            }
            default:
                break;
        }
        objects.attachChild(newbox);
    }

    private void hitAction(BitmapText count) { //ACTIONS TAKING UPON A HIT
        CollisionResults collide = new CollisionResults();
        Ray ray = new Ray(app.getCamera().getLocation(), app.getCamera().getDirection());
        objects.collideWith(ray, collide);
        hitMark(collide);
    }

    private void dotAction() { // COMPLEX OBJECT ACTION - AUTO WIN
        CollisionResults collide = new CollisionResults();
        Ray ray = new Ray(app.getCamera().getLocation(), app.getCamera().getDirection());
        rotateNode.collideWith(ray, collide);

        System.out.println("sphere hit " + j);
        if (collide.size() > 0) {
            CollisionResult result = collide.getClosestCollision();
            Vector3f vector = new Vector3f();
            result.getGeometry().getParent().worldToLocal(result.getContactPoint(), vector);
            Sphere sphere = new Sphere(10, 10, 0.08f); //HITMARK
            mark = new Geometry("Duckhit", sphere);
            Material smat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            smat.setColor("Color", ColorRGBA.Orange);
            mark.setMaterial(smat);
            mark.setLocalTranslation(vector);
            result.getGeometry().getParent().attachChild(mark);
            hit.playInstance();
            count.setText(" " + k);
            j = 4;
            k = 5;
            System.out.println("sphere hits " + j);
            if (k == 5) {
                gameResults();
            }
        } else {
            hit.playInstance();
        }
    }

    public void hitMark(CollisionResults collide) {
        if (collide.size() > 0) {
            CollisionResult result = collide.getClosestCollision();
            Vector3f vector = new Vector3f();
            result.getGeometry().getParent().worldToLocal(result.getContactPoint(), vector);
            Sphere sphere = new Sphere(10, 10, 0.08f); //HITMARK
            mark = new Geometry("Hit", sphere);
            Material smat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            smat.setColor("Color", ColorRGBA.Orange);
            mark.setMaterial(smat);
            mark.setLocalTranslation(vector);
            result.getGeometry().getParent().attachChild(mark);
            hit.playInstance();
            count.setText(" " + k);
            k++;
            if (k == 5) {
                gameResults();
            } else if (j == 4) {
                gameResults();
            }
        } else {
            hit.playInstance();
        }
    }

    private void duckAction() { //ACTIONS TAKING UPON HITTING DUCK
        CollisionResults collide = new CollisionResults();
        Ray ray = new Ray(app.getCamera().getLocation(), app.getCamera().getDirection());
        spinNode.collideWith(ray, collide);
        if (collide.size() > 0) {
            CollisionResult result = collide.getClosestCollision();
            Vector3f vector = new Vector3f();
            result.getGeometry().getParent().worldToLocal(result.getContactPoint(), vector);
            Sphere sphere = new Sphere(10, 10, 0.08f); //HITMARK
            mark = new Geometry("Duckhit", sphere);
            Material smat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            smat.setColor("Color", ColorRGBA.Orange);
            mark.setMaterial(smat);
            mark.setLocalTranslation(vector);
            result.getGeometry().getParent().attachChild(mark);
            hit.playInstance();
            count.setText(" " + k);
            j++;
            k++;
            System.out.println("duck hit" + j);
            if (k == 5) {
                gameResults();
            }
        } else {
            hit.playInstance();
        }
    }

    private void gameResults() {
        if (j == 4) {
            System.out.println("User wins");
            Picture pic = new Picture("You Win!");
            pic.setImage(app.getAssetManager(), "Textures/win.png", true);
            pic.setWidth(300);
            pic.setHeight(200);
            pic.setPosition(175, 125);
            rootNode.attachChild(pic);

        } else {
            System.out.println("User loses");
            Picture pic = new Picture("You Lose!");
            pic.setImage(app.getAssetManager(), "Textures/loss.png", true);
            pic.setWidth(300);
            pic.setHeight(200);
            pic.setPosition(175, 125);
            rootNode.attachChild(pic);
        }
    }
}
