package com.klemstinegroup.spacetubes;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.quailshillstudio.DestructionData;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Spacetubes extends ApplicationAdapter implements InputProcessor {
    PolygonSpriteBatch batch;
    private World world;
    private Stage stage;
    private static Box2DDebugRenderer debugRenderer;

//    public ObjectSet<UserDataInterface> polyVerts = new ObjectSet<>();
//    public ObjectSet<BodyDef> polyVertsBodyDef = new ObjectSet<>();

    private RayHandler rayHandler;
    private Texture whiteTexture;
    private ShapeDrawer drawer;
    private GroundBoxActor windowFrame;
    private BallActor tempDraggedBallAcor;
    private Vector3 testpoint=new Vector3();
    private long touchDownTime;
    private Vector3 touchDownPoint;
    private ShipActor shipActor;

    @Override
    public void create() {
//        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        this.whiteTexture = new Texture(pixmap); //remember to dispose of later
//        pixmap.dispose();
        TextureRegion region = new TextureRegion(whiteTexture, 0, 0, 1, 1);
        drawer = new ShapeDrawer(batch, region);

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        world = new World(new Vector2(0, -5), true);
        world.setContactListener(new B2dContactListener(this));
        batch = new PolygonSpriteBatch();
        Gdx.input.setInputProcessor(stage);
        float ratio = (float) (Gdx.graphics.getWidth()) / (float) (Gdx.graphics.getHeight());

        stage = new Stage(new ScreenViewport(), batch);
        InputMultiplexer multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
        multiplexer.addProcessor(this);
        multiplexer.addProcessor(stage);
        stage.getCamera().position.set(0, 0, 10);
        stage.getCamera().lookAt(0, 0, 0);
        stage.getCamera().viewportWidth = 300;
        stage.getCamera().viewportHeight = 300 / ratio;
        debugRenderer = new Box2DDebugRenderer();
        GearActor gearActor2 = new GearActor(world, -50, 20.0f, 32f, 32f, 1.6f);
        GearActor gearActor4 = new GearActor(world, 50, 20.00f, 32f, 32f, -1.6f);
        stage.addActor(gearActor2);
        stage.addActor(gearActor4);

        windowFrame = new GroundBoxActor(world, -0, -32, 64, 16);
        stage.addActor(windowFrame);

        rayHandler = new RayHandler(world, 1024, 1024);
        rayHandler.setAmbientLight(0.4f, 0.2f, 0.2f, .5f);
        rayHandler.setBlurNum(3);
        rayHandler.setCulling(false);
        rayHandler.setLightMapRendering(true);
        JarActor jarActor = new JarActor(world,rayHandler, 0f, 40.0f, 32f, 32f);
        stage.addActor(jarActor);
        shipActor = new ShipActor(world,rayHandler, 0f, 20.0f, 16f, 16f);
        stage.addActor(shipActor);


        PointLight pl = new PointLight(rayHandler, 512, new Color(0f, .4f, .4f, 1f), 150, -80f, 10f);
        pl.setIgnoreAttachedBody(true);

        PointLight pl2 = new PointLight(rayHandler, 512, new Color(.4f, 0, .4f, 1f), 150, 80f, 10f);
        pl2.setIgnoreAttachedBody(true);

        PointLight pl3 = new PointLight(rayHandler, 512, new Color(.4f, .4f, 0f, 1f), 150, 0f, 10f);
//        pl3.attachToBody(windowFrame.body, 0, 50);
        pl3.setIgnoreAttachedBody(true);


        rayHandler.setShadows(true);
        pl.setStaticLight(false);
        pl.setSoft(true);
        pl2.setStaticLight(false);
        pl2.setSoft(true);
        pl3.setStaticLight(false);
        pl3.setSoft(true);
//        PointLight.setGlobalContactFilter((short)2,(short)-1,(short)-1);
//        pl.setContactFilter((short)1,(short)1,(short)1);
//        pl2.setContactFilter((short)1,(short)1,(short)1);
        pl.setContactFilter((short)1,(short)1,(short)2);
        pl2.setContactFilter((short)1,(short)1,(short)2);
        pl3.setContactFilter((short)1,(short)1,(short)2);
        BallGenerator.getInstance().setup(stage, world, rayHandler);
        stage.draw();
    }

    @Override
    public void render() {
        stage.getCamera().position.set(shipActor.getX(), shipActor.getY(), 0);
        stage.getCamera().update();
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
        stage.act();


        //debugRenderer.render(world, stage.getCamera().combined);

        BallGenerator.getInstance().emit();

        rayHandler.setCombinedMatrix(stage.getCamera().combined, 0, 0, 1, 1);
        rayHandler.updateAndRender();
        stage.draw();
//        debugRenderer.render(world, stage.getCamera().combined);

        Array<Body> bodies = new Array<>();

        world.getBodies(bodies);

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
        if (keycode== Input.Keys.UP){
//            shipActor.body.applyLinearImpulse(new Vector2(0,1000),shipActor.body.getLocalCenter(),true);
shipActor.thrust(true);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {

        if (keycode== Input.Keys.UP){
//            shipActor.body.applyLinearImpulse(new Vector2(0,1000),shipActor.body.getLocalCenter(),true);
            shipActor.thrust(false);
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
//        touchDownTime= TimeUtils.millis();
//        touchDownPoint=testpoint.cpy();
//        tempDraggedBallAcor=new BallActor(world, rayHandler, testpoint.x, testpoint.y);
//        stage.addActor(tempDraggedBallAcor);
        float ang=new Vector2(testpoint.x,testpoint.y).sub(shipActor.body.getPosition()).angleRad()-shipActor.body.getAngle();
        ang-=45*MathUtils.degRad;
        while ( ang > MathUtils.PI){ang -= MathUtils.PI2;}
        while ( ang < -MathUtils.PI){ang += MathUtils.PI2;}
        System.out.println("ang:"+ang*MathUtils.radDeg);
        shipActor.thrust(true);
        shipActor.turn(true);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//        long time=TimeUtils.timeSinceMillis(touchDownTime);
//        stage.getCamera().unproject(testpoint.set(screenX, screenY, 0));
//        testpoint.sub(touchDownPoint).scl(1000000f/time);
//        Gdx.app.log("debug:","force:"+testpoint);
//        tempDraggedBallAcor.body.applyForceToCenter(testpoint.x,testpoint.y,true);
        shipActor.thrust(false);
        shipActor.turn(false);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        stage.getCamera().unproject(testpoint.set(screenX,screenY,0));
//        tempDraggedBallAcor.body.setTransform(testpoint.x, testpoint.y, 0);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled (float x,  float y) {
        OrthographicCamera camera = (OrthographicCamera)stage.getCamera();
        camera.unproject(testpoint.set(Gdx.input.getX(), Gdx.input.getY(), 0 ));
        float px = testpoint.x;
        float py = testpoint.y;
        camera.zoom += y * camera.zoom * 0.1f;
        camera.update();

        camera.unproject(testpoint.set(Gdx.input.getX(), Gdx.input.getY(), 0 ));
        camera.position.add(px - testpoint.x, py- testpoint.y, 0);
        camera.update();
        return true;
    }

}