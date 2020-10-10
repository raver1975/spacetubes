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
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.quailshillstudio.GroundFixture;
import com.quailshillstudio.PolygonBox2DShape;
import com.quailshillstudio.UserData;
import com.quailshillstudio.UserDataInterface;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Spacetubes extends ApplicationAdapter {
    PolygonSpriteBatch batch;
    private World world;
    private Stage stage;
    private Box2DDebugRenderer debugRenderer;
    public Array<GroundFixture> polyVerts = new Array<GroundFixture>();

    private RayHandler rayHandler;
    private Texture whiteTexture;
    private ShapeDrawer drawer;
    private boolean mustCreate;
    private UserDataInterface ud;

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
        world = new World(new Vector2(0, -10), true);
        world.setContactListener(new B2dContactListener(this));
        batch = new PolygonSpriteBatch();
        Gdx.input.setInputProcessor(stage);
        float ratio = (float) (Gdx.graphics.getWidth()) / (float) (Gdx.graphics.getHeight());

        stage = new Stage(new ScreenViewport());
        stage.getCamera().position.set(0, 0, 10);
        stage.getCamera().lookAt(0, 0, 0);
        stage.getCamera().viewportWidth = 200;
        stage.getCamera().viewportHeight = 200 / ratio;
        debugRenderer = new Box2DDebugRenderer();

//        GearActor gearActor1 = new GearActor(world, -20, -15.0f, 23.5f, 23.5f, false);
        GearActor gearActor2 = new GearActor(world, 0, -25.0f, 23.5f, 23.5f, true);
//        GearActor gearActor3 = new GearActor(world, 20, -15.00f, 23.5f, 23.5f, false);
        GroundActor groundActor = new GroundActor(world, 30f, -20.0f, 150.35f, 150.5f);
//        stage.addActor(gearActor1);
        stage.addActor(gearActor2);
//        stage.addActor(gearActor3);
        stage.addActor(groundActor);


//        new WindowsFrame(world, stage.getCamera().viewportWidth, stage.getCamera().viewportHeight);

        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0.3f, 0.2f, 0.2f, .5f);
        rayHandler.setBlurNum(3);


        PointLight pl = new PointLight(rayHandler, 1280, new Color(0.2f, 1, 1, 1f), 150, -80f, 10f);
        pl.setIgnoreAttachedBody(true);

        PointLight pl2 = new PointLight(rayHandler, 1280, new Color(1, 0, 1, 1f), 150, 80f, 10f);
        pl2.setIgnoreAttachedBody(true);

        PointLight pl3 = new PointLight(rayHandler, 1280, new Color(1, 1, .2f, 1f), 150, -30f, -70f);
        pl3.setIgnoreAttachedBody(true);


        rayHandler.setShadows(true);
        pl.setStaticLight(false);
        pl.setSoft(true);
        pl2.setStaticLight(false);
        pl2.setSoft(true);
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
        this.ud=groundActor;
            Array<float[]> totalRS = new Array<float[]>();
            Array<Fixture> fixtureList = ud.body.getFixtureList();
            int fixCount = fixtureList.size;
            for (int i = 0; i < fixCount; i++) {
                PolygonBox2DShape polyClip = null;
                if (fixtureList.get(i).getShape() instanceof PolygonShape) {
                    polyClip = new PolygonBox2DShape((PolygonShape) fixtureList.get(i).getShape());
                } else if (fixtureList.get(i).getShape() instanceof ChainShape) {
                    polyClip = new PolygonBox2DShape((ChainShape) fixtureList.get(i).getShape());
                }
                Array<PolygonBox2DShape> rs = polyClip.clipCS(polyClip, false, true);
                for (int y = 0; y < rs.size; y++) {
//                rs.get(y).circleContact(b.getPosition(), circRadius);
                    totalRS.add(rs.get(y).verticesToLoop());
                }
            }
//            polyVerts.clear();
        this.ud=groundActor;
            switchGround(totalRS, groundActor);
//        } catch (
//                Exception e) {
//        }
mustCreate=false;
    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        stage.draw();
        debugRenderer.render(world, stage.getCamera().combined);

        //debugRenderer.render(world, stage.getCamera().combined);

        BallGenerator.getInstance().emit();

        rayHandler.setCombinedMatrix(stage.getCamera().combined, 0, 0, 1, 1);
        rayHandler.updateAndRender();


        for (int i = 0; i < world.getBodyCount(); i++) {
            Array<Body> bodies = new Array<>();
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
        stage.act();
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);

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


    public void switchGround(Array<float[]> rs, UserDataInterface ud1) {

        polyVerts.clear();
        mustCreate = true;
//        Array<float[]> verts = new Array<>();
//        for (int i = 0; i < rs.size; i++) {/*
//            float[] temp = rs.get(i).verticesToLoop();
//            for (int ii=0;ii<temp.length;ii+=2){
//                temp[ii]+=pos.x;
//                temp[ii+1]+=pos.y;
//            }
//            verts.add(temp);
//
//        }*/
        GroundFixture grFix = new GroundFixture(rs);
        polyVerts.add(grFix);
        this.ud=ud1;
    }

    protected void createGround() {
        BodyDef groundDef = new BodyDef();
        groundDef.type = ud.body.getType();
//        groundDef.position.set(pos.cpy());
        groundDef.position.set(ud.body.getPosition().cpy());

        Body nground = world.createBody(groundDef);
        nground.setUserData(ud);
        for (int i = 0; i < polyVerts.size; i++) {
//            Body nground = ud.getBody();
//            nground.setTransform(ud.getX(),ud.getY(),nground.getAngle());
//            nground.getFixtureList().clear();
            Array<Fixture> fixtures = new Array<>();
            for (int y = 0; y < this.polyVerts.get(i).getVerts().size; y++) {
                if (this.polyVerts.get(i).getVerts().get(y).length >= 6) {
                    FixtureDef fixtureDef = new FixtureDef();
                    fixtureDef.density = 10f;//ud.body.getFixtureList().get(0).getDensity();
                    fixtureDef.friction = .50f;//ud.body.getFixtureList().get(0).getFriction();
                    fixtureDef.restitution = .3f;//ud.body.getFixtureList().get(0).getRestitution();

//                    float[] f = ;
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
//                if (f.length >= 6) {
                    shape.createLoop(this.polyVerts.get(i).getVerts().get(y));
                    fixtureDef.shape = shape;
                    fixtures.add(nground.createFixture(fixtureDef));
//                } else if (f.length >= 4) {
//                    shape.createChain(f);
//                    fixtureDef.shape = shape;
//                    fixtures.add(nground.createFixture(fixtureDef));
//                }
//                nground.getFixtureList().add(fixtures.peek());
//                }
                }
            }
            polyVerts.get(i).setFixtures(fixtures);

        }
        this.mustCreate = false;
        polyVerts.clear();
    }


}