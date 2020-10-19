package com.klemstinegroup.spacetubes;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.quailshillstudio.DestructionData;
import jdk.internal.event.Event;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Spacetubes extends ApplicationAdapter implements InputProcessor {
    PolygonSpriteBatch batch;
    private World world;
    private Stage stage;
    private Box2DDebugRenderer debugRenderer;

    public ObjectSet<UserDataInterface> polyVerts = new ObjectSet<>();
//    public ObjectSet<BodyDef> polyVertsBodyDef = new ObjectSet<>();

    private RayHandler rayHandler;
    private Texture whiteTexture;
    private ShapeDrawer drawer;
    private GroundBoxActor windowFrame;
    private BallActor tempDraggedBallAcor;
    private Vector3 testpoint=new Vector3();
    private long touchDownTime;
    private Vector3 touchDownPoint;

    @Override
    public void create() {
//        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        this.whiteTexture = new Texture(pixmap); //remember to dispose of later
        pixmap.dispose();
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
        stage.getCamera().viewportWidth = 200;
        stage.getCamera().viewportHeight = 200 / ratio;
        debugRenderer = new Box2DDebugRenderer();
//debugRenderer.setDrawVelocities(true);
//debugRenderer.setDrawContacts(true);

        GearActor gearActor2 = new GearActor(world, -50, 20.0f, 32f, 32f, 1.6f);
        GearActor gearActor4 = new GearActor(world, 50, 20.00f, 32f, 32f, -1.6f);
        JarActor jarActor = new JarActor(world, 0f, 40.0f, 32f, 32f);
        stage.addActor(gearActor2);
        stage.addActor(gearActor4);
        stage.addActor(jarActor);

        windowFrame = new GroundBoxActor(world, -0, -50, 80, 20);
        stage.addActor(windowFrame);

        rayHandler = new RayHandler(world, 2048, 2048);
        rayHandler.setAmbientLight(0.4f, 0.2f, 0.2f, .5f);
        rayHandler.setBlurNum(3);
        rayHandler.setCulling(false);
        rayHandler.setLightMapRendering(true);


        PointLight pl = new PointLight(rayHandler, 512, new Color(0.2f, 1, 1, 1f), 150, -80f, 10f);
        pl.setIgnoreAttachedBody(true);

        PointLight pl2 = new PointLight(rayHandler, 512, new Color(1, 0, 1, 1f), 150, 80f, 10f);
        pl2.setIgnoreAttachedBody(true);

        PointLight pl3 = new PointLight(rayHandler, 512, new Color(1, 1, .2f, 1f), 150, 0f, 10f);
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
//        stage.addActor(new FireEmitter(world));

//        try {

//            float[] points = {-50, 0, -50, -50f, 50, -50f, 50, 0};
//            Array<float[]> verts = new Array<>();
//            verts.add(points);
//            GroundFixture grFix = new GroundFixture(verts);
//            polyVerts.add(grFix);

//            float[] points1 = {-50, 20, -50, 10f, 0, 10f, 0, 20};
//            Array<float[]> verts1 = new Array<>();
//            verts1.add(points1);
//            GroundFixture grFix1 = new GroundFixture(verts1);
//            polyVerts.add(grFix1);
//            gearActor2.body.getFixtureList().clear();
/*            for (int ii = 0; ii < groundActor.body.getFixtureList().size; ii++) {
                System.out.println("ii:" + ii);
                Array<float[]> verts = new Array<>();
                PolygonShape s = (PolygonShape) groundActor.body.getFixtureList().get(ii).getShape();
                float[] p = new float[s.getVertexCount()*2];
                Vector2 v = new Vector2();
                for (int i = 0; i < s.getVertexCount(); i++) {
                    s.getVertex(i, v);
                    p[i * 2] = v.x * groundActor.getWidth();
                    p[i * 2 + 1] = v.y * groundActor.getHeight();
                    verts.add(p);
                    System.out.println("+++" + v);
                }
                System.out.println();
                GroundFixture grFix = new GroundFixture(verts);
                polyVerts.add(grFix);
            }
//        this.ud=groundActor;
        mustCreate = true;*/
        //        this.mustCreate = false;

//        this.ud = windowFrame;

//        windowFrame.verts = totalRS;
//        UserDataInterface ud1=(UserDataInterface)ground.getUserData();
//        polyVerts.add(windowFrame);
//        windowFrame.tempBodyDef = Box2DUtils.createDef(windowFrame.body);
//        polyVerts.add(jarActor);
//        jarActor.tempBodyDef = Box2DUtils.createDef(jarActor.body);
//        polyVerts.add(gearActor2);
//        polyVerts.add(gearActor4);
//        ud.verts = totalRS;
//        switchGround(totalRS, groundActor);

//        } catch (
//                Exception e) {
//        }
        stage.draw();
    }

    @Override
    public void render() {

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
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        stage.getCamera().unproject(testpoint.set(screenX, screenY, 0));
        touchDownTime= TimeUtils.millis();
        touchDownPoint=testpoint.cpy();
        tempDraggedBallAcor=new BallActor(world, rayHandler, testpoint.x, testpoint.y);
        stage.addActor(tempDraggedBallAcor);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        long time=TimeUtils.timeSinceMillis(touchDownTime);
        stage.getCamera().unproject(testpoint.set(screenX, screenY, 0));
        testpoint.sub(touchDownPoint).scl(1000000f/time);
        System.out.println("force:"+testpoint);
        tempDraggedBallAcor.body.applyForceToCenter(testpoint.x,testpoint.y,true);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        stage.getCamera().unproject(testpoint.set(screenX,screenY,0));
        tempDraggedBallAcor.body.setTransform(testpoint.x, testpoint.y, 0);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}