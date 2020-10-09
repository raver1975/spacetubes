package com.klemstinegroup.spacetubes;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.quailshillstudio.UserDataInterface;
import com.quailshillstudio.polygonClippingUtils.GroundFixture;
import com.quailshillstudio.polygonClippingUtils.PolygonBox2DShape;
import com.quailshillstudio.polygonClippingUtils.UserData;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.List;

public class Spacetubes extends ApplicationAdapter {
    PolygonSpriteBatch batch;
    private World world;
    private Stage stage;
    private Box2DDebugRenderer debugRenderer;
    private Array<GroundFixture> polyVerts = new Array<GroundFixture>();

    private RayHandler rayHandler;
    private Texture whiteTexture;
    private ShapeDrawer drawer;
    private boolean mustCreate;

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
        world = new World(new Vector2(0, -3), true);
        world.setContactListener(new B2dContactListener(this));
        batch = new PolygonSpriteBatch();
        Gdx.input.setInputProcessor(stage);
        float ratio = (float) (Gdx.graphics.getWidth()) / (float) (Gdx.graphics.getHeight());

        stage = new Stage(new ScreenViewport());
        stage.getCamera().position.set(0, 0, 10);
        stage.getCamera().lookAt(0, 0, 0);
        stage.getCamera().viewportWidth = 100;
        stage.getCamera().viewportHeight = 100 / ratio;
        debugRenderer = new Box2DDebugRenderer();

        GearActor gearActor1 = new GearActor(world, -20, -15.0f, 23.5f, 23.5f, false);
        GearActor gearActor3 = new GearActor(world, 20, -15.00f, 23.5f, 23.5f, false);
        GearActor gearActor2 = new GearActor(world, 0, -25.0f, 23.5f, 23.5f, true);
//        GroundActor groundActor = new GroundActor(world, 30f, -20.0f, 43.35f, 43.5f);
        stage.addActor(gearActor1);
        stage.addActor(gearActor2);
        stage.addActor(gearActor3);
//        stage.addActor(groundActor);


//        new WindowsFrame(world, stage.getCamera().viewportWidth, stage.getCamera().viewportHeight);

        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0.3f, 0.2f, 0.2f, .5f);
        rayHandler.setBlurNum(3);


        PointLight pl = new PointLight(rayHandler, 1280, new Color(0.2f, 1, 1, 1f), 100, -30f, 10f);
        pl.setIgnoreAttachedBody(true);

        PointLight pl2 = new PointLight(rayHandler, 1280, new Color(1, 0, 1, 1f), 100, 30f, 10f);
        pl2.setIgnoreAttachedBody(true);

        rayHandler.setShadows(true);
        pl.setStaticLight(false);
        pl.setSoft(true);
        pl2.setStaticLight(false);
        pl2.setSoft(true);
        BallGenerator.getInstance().setup(stage, world, rayHandler);
//        stage.addActor(new FireEmitter(world));

        try {

            float[] points = {-50, 0, -50, -10f, 50, -10f, 50, 0};
            Array<float[]> verts = new Array<>();
            verts.add(points);
            GroundFixture grFix = new GroundFixture(verts);
            polyVerts.add(grFix);

//            float[] points1 = {-50, 20, -50, 10f, 0, 10f, 0, 20};
//            Array<float[]> verts1 = new Array<>();
//            verts1.add(points1);
//            GroundFixture grFix1 = new GroundFixture(verts1);
//            polyVerts.add(grFix1);
//            gearActor2.body.getFixtureList().clear();
//            for (int ii = 0; ii < gearActor2.body.getFixtureList().size; ii++) {
//                System.out.println("ii:" + ii);
//                verts.clear();
//                PolygonShape s = (PolygonShape) gearActor2.body.getFixtureList().get(ii).getShape();
//                float[] p = new float[s.getVertexCount()];
//                Vector2 v = new Vector2();
//                for (int i = 0; i < s.getVertexCount(); i++) {
//                    s.getVertex(i, v);
//                    p[i * 2] = v.x*gearActor2.getWidth();
//                    p[i * 2 + 1] = v.y*gearActor2.getHeight();
//                    verts.add(p);
//                    System.out.println("+++"+v);
//                }
//                System.out.println();
//                grFix = new GroundFixture(verts);
//                polyVerts.add(grFix);
//            }
            mustCreate = true;
        } catch (
                Exception e) {
        }


    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);

        stage.draw();
        debugRenderer.render(world, stage.getCamera().combined);

        //debugRenderer.render(world, stage.getCamera().combined);

        BallGenerator.getInstance().emit();

        rayHandler.setCombinedMatrix(stage.getCamera().combined, 0, 0, 1, 1);
        rayHandler.updateAndRender();


        for (int i = 0; i < world.getBodyCount(); i++) {
            Array<Body> bodies = new Array<Body>();
            world.getBodies(bodies);
            try {
                UserDataInterface datai = ((UserDataInterface) bodies.get(i).getUserData());
                UserData data = datai.getUserData();
                if (data != null && data.getType() == UserData.GROUND) {
                    if ((data.mustDestroy || mustCreate) && !data.destroyed) {
                        world.destroyBody(bodies.get(i));
                        bodies.removeIndex(i);
                    }
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }

        }

        if (mustCreate)
            createGround();

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


    public void switchGround(Array<PolygonBox2DShape> rs) {
        mustCreate = true;
        Array<float[]> verts = new Array<float[]>();
        for (int i = 0; i < rs.size; i++) {
            verts.add(rs.get(i).verticesToLoop());
        }
        GroundFixture grFix = new GroundFixture(verts);
        polyVerts.add(grFix);
    }

    protected void createGround() {
        BodyDef groundDef = new BodyDef();
        groundDef.type = BodyDef.BodyType.KinematicBody;
        groundDef.position.set(0, 0);

        for (int i = 0; i < polyVerts.size; i++) {
            Body nground = world.createBody(groundDef);
            nground.setUserData(new UserDataInterface(new UserData(UserData.GROUND)));

            Array<Fixture> fixtures = new Array<>();
            for (int y = 0; y < this.polyVerts.get(i).getVerts().size; y++) {
                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.density = 1;
                fixtureDef.friction = .8f;
                float[] f = this.polyVerts.get(i).getVerts().get(y);
//                if (f.length >= 4) {
//                FloatArray a = new FloatArray();
//                a.addAll(this.polyVerts.get(i).getVerts().get(y));
//                if (a.size>0) {
//                    while (a.size < 9) {
//                        a.add(a.get(0) + .1f);
//                        a.add(a.get(1) + .1f);
//                        a.add(a.pop());
//                        a.add(a.pop());
//                    }
                ChainShape shape = new ChainShape();
                if (f.length >= 6) {
                    shape.createLoop(f);
                    fixtureDef.shape = shape;
                    fixtures.add(nground.createFixture(fixtureDef));
                } else if (f.length >= 4) {
                    shape.createChain(f);
                    fixtureDef.shape = shape;
                    fixtures.add(nground.createFixture(fixtureDef));
                }

//                }
            }
            polyVerts.get(i).setFixtures(fixtures);
        }
        this.mustCreate = false;
        polyVerts.clear();
    }


}