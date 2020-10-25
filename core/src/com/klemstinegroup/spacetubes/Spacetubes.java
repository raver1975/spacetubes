package com.klemstinegroup.spacetubes;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.quailshillstudio.DestructionData;
import space.earlygrey.shapedrawer.ShapeDrawer;
import zk.planet_generator.Scene;

public class Spacetubes extends ApplicationAdapter implements InputProcessor {
    PolygonSpriteBatch batch;
    private World world;
    private Stage stage;
    private static Box2DDebugRenderer debugRenderer;
    public static boolean debug = false;

    //    public ObjectSet<UserDataInterface> polyVerts = new ObjectSet<>();
//    public ObjectSet<BodyDef> polyVertsBodyDef = new ObjectSet<>();
//    private Scene scene;

    private RayHandler rayHandler;
    private Texture whiteTexture;
    private ShapeDrawer drawer;
    private GroundBoxActor windowFrame;
    private BallActor tempDraggedBallAcor;
    private Vector3 testpoint = new Vector3();
    private long touchDownTime;
    private Vector3 touchDownPoint;
    private ShipActor shipActor;
    private Stage starStage;
    private GravityHandler gravityHandler;
    private float scaleFactor=2f;
    private float intendedZoom=1f;
    private CarActor carActor;
    private UserDataInterface cameraActor=null;
//    private Vector2 intendedPosition=new Vector2();


    @Override
    public void create() {
//        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
//        scene = new Scene();
        gravityHandler = new GravityHandler();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        this.whiteTexture = new Texture(pixmap); //remember to dispose of later
//        pixmap.dispose();
        TextureRegion region = new TextureRegion(whiteTexture, 0, 0, 1, 1);
        drawer = new ShapeDrawer(batch, region);

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        world = new World(new Vector2(0,-2), true);
        world.setContactListener(new B2dContactListener(this));
        batch = new PolygonSpriteBatch();
        Gdx.input.setInputProcessor(stage);
        float ratio = (float) (Gdx.graphics.getWidth()) / (float) (Gdx.graphics.getHeight());

        stage = new Stage(new ScreenViewport(), batch);

        GestureDetector gd = new GestureDetector(new GestureDetector.GestureAdapter() {
            private Vector2 oldInitialFirstPointer = null, oldInitialSecondPointer = null;
            private float oldScale;

            @Override
            public void pinchStop() {
                super.pinchStop();
            }

            @Override
            public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
                if (!(initialPointer1.equals(oldInitialFirstPointer) && initialPointer2.equals(oldInitialSecondPointer))) {
                    oldInitialFirstPointer = initialPointer1.cpy();
                    oldInitialSecondPointer = initialPointer2.cpy();
                    OrthographicCamera camera = (OrthographicCamera) stage.getCamera();
                    oldScale = camera.zoom;
                }
                Vector3 center = new Vector3(
                        (pointer1.x + initialPointer2.x) / 2,
                        (pointer1.y + initialPointer2.y) / 2,
                        0
                );
                float newScale=oldScale * initialPointer1.dst(initialPointer2) / pointer1.dst(pointer2);
                zoomCamera(center,newScale );
                scaleFactor=newScale;
                return true;
//                return super.pinch(initialPointer1, initialPointer2, pointer1, pointer2);

            }

            private void zoomCamera(Vector3 origin, float scale) {
                OrthographicCamera camera = (OrthographicCamera) stage.getCamera();
                camera.update();
                Vector3 oldUnprojection = camera.unproject(origin.cpy()).cpy();
                camera.zoom = scale; //Larger value of zoom = small images, border view
                camera.zoom = Math.max(camera.zoom, 0.05f);
                camera.update();
                Vector3 newUnprojection = camera.unproject(origin.cpy()).cpy();
                camera.position.add(oldUnprojection.cpy().add(newUnprojection.cpy().scl(-1f)));
            }
        });
        starStage = new Stage(new ScreenViewport(), batch);
        InputMultiplexer multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
        multiplexer.addProcessor(this);
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(starStage);
        multiplexer.addProcessor(gd);
        stage.getCamera().position.set(0, 0, 10);
        stage.getCamera().lookAt(0, 0, 0);
        stage.getCamera().viewportWidth = 20;
        stage.getCamera().viewportHeight = 20 / ratio;
        starStage.getCamera().position.set(0, 0, 10);
        starStage.getCamera().lookAt(0, 0, 0);
        starStage.getCamera().viewportWidth = 20;
        starStage.getCamera().viewportHeight = 20 / ratio;
        createStars(1000);
        debugRenderer = new Box2DDebugRenderer();

        windowFrame = new GroundBoxActor(world, rayHandler, -32, -100, 50, 40);
        stage.addActor(windowFrame);
        rayHandler = new RayHandler(world, 1024, 1024);
        rayHandler.setAmbientLight(0.4f, 0.2f, 0.2f, .5f);
        rayHandler.setBlurNum(3);
        rayHandler.setCulling(false);
        rayHandler.setLightMapRendering(true);
        JarActor jarActor = new JarActor(world, rayHandler, 0f, -10.0f, 32f, 32f);
        stage.addActor(jarActor);
        for (int i = 0; i < 10; i++) {
            PlanetActor planetActor = new PlanetActor(world, rayHandler, new Vector2(MathUtils.random(200, 3000), MathUtils.random(-1500, 1500)), 40);
            stage.addActor(planetActor);
            PointLight pl2 = new PointLight(rayHandler, 128, new Color(1, 1f, 0f, 1f), planetActor.getWidth(), planetActor.getX(), planetActor.getY());
            PointLight pl3 = new PointLight(rayHandler, 128, new Color(1, 0f, 0f, 1f), planetActor.getWidth(), planetActor.getX(), planetActor.getY());
            pl3.setSoft(false);
            pl2.setXray(true);
            pl2.setSoft(true);
            gravityHandler.add(new GravityHandler.DataAndForce(planetActor, planetActor.getWidth()*600));
            System.out.println("processed planet #" + i);
        }
        shipActor = new ShipActor(world, rayHandler, 0f, 40.0f, 1.2f, 1.2f);
        stage.addActor(shipActor);
cameraActor=shipActor;
        carActor = new CarActor(world, rayHandler, 0f, 25.0f);
        stage.addActor(carActor);

//        shipActor.addListener(new ActorGestureListener(){
//            @Override
//            public void tap(InputEvent event, float x, float y, int count, int button) {
//                super.tap(event, x, y, count, button);
//                shipActor.fire();
//            }
//        });
        GearActor gearActor2 = new GearActor(world, rayHandler, -50, 20.0f, 16f, 16f, 1.6f);
        stage.addActor(gearActor2);
        GearActor gearActor4 = new GearActor(world, rayHandler, 50, 20.00f, 16f, 16f, -1.6f);
        stage.addActor(gearActor4);

        PointLight pl = new PointLight(rayHandler, 512, new Color(0f, .4f, .4f, 1f), 150, -80f, 10f);
        pl.setIgnoreAttachedBody(true);

        PointLight pl2 = new PointLight(rayHandler, 512, new Color(.4f, 0, .4f, 1f), 150, 80f, 10f);
        pl2.setIgnoreAttachedBody(true);

        PointLight pl3 = new PointLight(rayHandler, 512, new Color(.4f, .4f, 0f, 1f), 150, 0f, 10f);
//        pl3.attachToBody(windowFrame.body, 0, 50);
        pl3.setIgnoreAttachedBody(true);


        rayHandler.setShadows(true);
        pl.setStaticLight(false);
        pl.setSoft(false);
        pl2.setStaticLight(false);
        pl2.setSoft(false);
        pl3.setStaticLight(false);
        pl3.setSoft(false);
//        PointLight.setGlobalContactFilter((short)2,(short)-1,(short)-1);
//        pl.setContactFilter((short)1,(short)1,(short)1);
//        pl2.setContactFilter((short)1,(short)1,(short)1);
        pl.setContactFilter((short) 1, (short) 1, (short) 2);
        pl2.setContactFilter((short) 1, (short) 1, (short) 2);
        pl3.setContactFilter((short) 1, (short) 1, (short) 2);
        BallGenerator.getInstance().setup(stage, world, rayHandler);

        stage.draw();
    }

    @Override
    public void render() {
        stage.getCamera().unproject(testpoint.set((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f));
        Vector2 f = new Vector2(new Vector2(-MathUtils.sin(shipActor.body.getAngle() - 45 * MathUtils.degRad), MathUtils.cos(shipActor.body.getAngle() - 45 * MathUtils.degRad)).scl(1));
//            Vector2 f1=new Vector2(testpoint.x,testpoint.y).sub(body.getPosition());
        Vector2 f1 = shipActor.body.getPosition().sub(new Vector2(testpoint.x, testpoint.y));
//        float ang = MathUtils.PI - f1.angleRad(f);
//        while (ang > MathUtils.PI) {
//            ang -= MathUtils.PI2;
//        }
//        while (ang < -MathUtils.PI) {
//            ang += MathUtils.PI2;
//        }
//        ang = MathUtils.clamp(ang, -MathUtils.HALF_PI, MathUtils.HALF_PI);
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        //gravity
        for (GravityHandler.DataAndForce daf : gravityHandler.getGravity()) {
            for (Body body : bodies) {
                Vector2 planetCenter = daf.ud.body.getWorldCenter();
                Vector2 bodyCenter = body.getWorldCenter();
//                bodyCenter.sub(planetCenter);
                Vector2 dist = planetCenter.cpy().sub(bodyCenter);
                Vector2 dd=dist.cpy().nor().scl((float) (daf.force/(dist.len()*dist.len())));
                body.applyForceToCenter(dd,true);

            }
        }
//        f = shipActor.tipVector(shipActor.body.getLinearVelocity().len()*1f);
        f = cameraActor.body.getWorldCenter().cpy().add(cameraActor.body.getLinearVelocity().cpy().scl(((OrthographicCamera)stage.getCamera()).zoom*(1f/scaleFactor)/2.5f));
        stage.getCamera().position.set(f, 0);
        intendedZoom=Math.max(1f,cameraActor.body.getLinearVelocity().len()/2.5f)*scaleFactor;
        if (((OrthographicCamera)stage.getCamera()).zoom<intendedZoom-.02f){
            ((OrthographicCamera)stage.getCamera()).zoom+=.02f;
        }
        if (((OrthographicCamera)stage.getCamera()).zoom>intendedZoom+.02f){
            ((OrthographicCamera)stage.getCamera()).zoom-=.02f;
        }
        stage.getCamera().update();
        starStage.getCamera().update();
        starStage.draw();
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        Vector2 g = new Vector2();
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
//        for (Body b : bodies) {
//            Array<Vector2> temp = ((UserDataInterface) b.getUserData()).getLinearVelocities();
//            if (temp.size > 0) {
//                g.set(0, 0);
//                for (Vector2 v : temp) {
//                    b.applyForceToCenter(v,true);
//                }
//                ((UserDataInterface) b.getUserData()).clearLinearVelocities();
//            }
//        }

        stage.act();
        rayHandler.setCombinedMatrix(stage.getCamera().combined, 0, 0, 1, 1);
        rayHandler.updateAndRender();
        stage.draw();
        if (debug) debugRenderer.render(world, stage.getCamera().combined);

        Array<UserDataInterface> createBody = new Array<>();
        for (int i = 0; i < world.getBodyCount(); i++) {
            try {
                UserDataInterface datai = ((UserDataInterface) bodies.get(i).getUserData());
                DestructionData data = datai.getDestr();
                if (data != null && data.getType() == DestructionData.GROUND) {
                    if ((data.mustDestroy) && !data.destroyed) {
                        world.destroyBody(bodies.get(i));
                        createBody.add(datai);
//                        bodies.removeIndex(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        for (UserDataInterface ud : createBody) {
            ud.createGround();
        }

//        if (mustCreate)
//            createGround();
//        scene.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        whiteTexture.dispose();
        batch.dispose();
        rayHandler.dispose();
    }

//    public void switchGround(List<PolygonBox2DShape> rs) {
//        mustCreate = true;
//        List<float[]> verts = new ArrayList<float[]>();
//        for (int i = 0; i < rs.size(); i++) {
//            verts.add(rs.get(i).verticesToLoop());
//        }
//        GroundFixture grFix = new GroundFixture(verts);
//        polyVerts.add(grFix);
//    }


//    public void switchGround(Array<float[]> rs, UserDataInterface ud1) {
//        polyVerts.clear();
//
//    }


    @Override
    public boolean keyDown(int keycode) {

        if (keycode == Input.Keys.NUM_1) {
            cameraActor=carActor;
        }

        if (keycode == Input.Keys.NUM_2) {
            cameraActor=shipActor;
        }

        if (keycode == Input.Keys.SPACE) {
            shipActor.fire();
        }

        if (keycode == Input.Keys.UP) {
//            shipActor.body.applyLinearImpulse(new Vector2(0,1000),shipActor.body.getLocalCenter(),true);
            shipActor.thrust(true);
//            shipActor.turn(ShipActor.TURNTYPE.RIGHT);
        }
        if (keycode == Input.Keys.LEFT) {
//            shipActor.body.applyLinearImpulse(new Vector2(0,1000),shipActor.body.getLocalCenter(),true);
            shipActor.turn(ShipActor.TURNTYPE.LEFT);
        }
        if (keycode == Input.Keys.RIGHT) {
//            shipActor.body.applyLinearImpulse(new Vector2(0,1000),shipActor.body.getLocalCenter(),true);
            shipActor.turn(ShipActor.TURNTYPE.RIGHT);
        }

        if (keycode == Input.Keys.D) {
            debug = !debug;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {


        if (keycode == Input.Keys.UP) {
//            shipActor.body.applyLinearImpulse(new Vector2(0,1000),shipActor.body.getLocalCenter(),true);
            shipActor.thrust(false);
        } else {
            shipActor.turn(ShipActor.TURNTYPE.OFF);
        }
        return true;

    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        stage.getCamera().unproject(testpoint.set(screenX, screenY, 0));
        if ((button==0&&shipActor.body.getWorldCenter().dst(new Vector2(testpoint.x,testpoint.y))<3)||button==1){
            shipActor.fire();
            return false;
        }       if (pointer == 0) {

//        touchDownTime= TimeUtils.millis();
//        touchDownPoint=testpoint.cpy();
//        tempDraggedBallAcor=new BallActor(world, rayHandler, testpoint.x, testpoint.y);
//        stage.addActor(tempDraggedBallAcor);
//        float ang = new Vector2(testpoint.x, testpoint.y).sub(shipActor.body.getPosition()).angleRad() - shipActor.body.getAngle();
//        ang -= 45 * MathUtils.degRad;
//        while (ang > MathUtils.PI) {
//            ang -= MathUtils.PI2;
//        }
//        while (ang < -MathUtils.PI) {
//            ang += MathUtils.PI2;
//        }
//        System.out.println("ang:" + ang * MathUtils.radDeg);

            shipActor.thrust(true);
//        stage.getCamera().unproject(testpoint.set((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f));
            shipActor.turn(ShipActor.TURNTYPE.MOUSE);
        } else {
            shipActor.fire();
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//        long time=TimeUtils.timeSinceMillis(touchDownTime);
//        stage.getCamera().unproject(testpoint.set(screenX, screenY, 0));
//        testpoint.sub(touchDownPoint).scl(1000000f/time);
//        Gdx.app.log("debug:","force:"+testpoint);
//        tempDraggedBallAcor.body.applyForceToCenter(testpoint.x,testpoint.y,true);
        if (pointer == 0) {
            shipActor.thrust(false);
            shipActor.turn(ShipActor.TURNTYPE.OFF);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        stage.getCamera().unproject(testpoint.set(screenX,screenY,0));
//        tempDraggedBallAcor.body.setTransform(testpoint.x, testpoint.y, 0);
//        if (pointer == 0) {
//            stage.getCamera().unproject(testpoint.set(screenX, screenY, 0));
//            if (shipActor.body.getWorldCenter().dst(new Vector2(testpoint.x, testpoint.y)) < 5) {
//                shipActor.fire();
//                return false;
//            }
//        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float x, float y) {
        OrthographicCamera camera = (OrthographicCamera) stage.getCamera();
        camera.unproject(testpoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        float px = testpoint.x;
        float py = testpoint.y;
        camera.zoom += y * camera.zoom * 0.1f;
        scaleFactor+=y*.1f;
        camera.update();

        camera.unproject(testpoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        camera.position.add(px - testpoint.x, py - testpoint.y, 0);
        camera.update();
        return true;
    }

    public void createStars(int starAmount) {
        for (int i = 0; i < starAmount; i++) {
            Image star = new Image(Scene.pixelTexture);
            star.setPosition(MathUtils.random(0, Scene.BUFFER_WIDTH + Scene.EDITOR_OFFSET), MathUtils.random(0, Scene.BUFFER_HEIGHT));
            star.setColor(Color.WHITE);

            if (MathUtils.randomBoolean(0.1f)) {
                star.setSize(MathUtils.random.nextFloat(), MathUtils.random.nextFloat());
            }

            starStage.addActor(star);
        }
    }
}