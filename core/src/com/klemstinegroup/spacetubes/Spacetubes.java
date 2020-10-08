package com.klemstinegroup.spacetubes;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Spacetubes extends ApplicationAdapter  {
    SpriteBatch batch;
    private World world;
    private Stage stage;
    private Box2DDebugRenderer debugRenderer;

    private RayHandler rayHandler;

    @Override
    public void create () {
//        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        world = new World(new Vector2(0, -3), true);
        world.setContactListener(new B2dContactListener());
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);
        float ratio = (float)(Gdx.graphics.getWidth()) / (float)(Gdx.graphics.getHeight());

        stage = new Stage(new ScreenViewport());
        stage.getCamera().position.set(0,0,10);
        stage.getCamera().lookAt(0,0,0);
        stage.getCamera().viewportWidth = 10;
        stage.getCamera().viewportHeight = 10/ratio;
        debugRenderer = new Box2DDebugRenderer();

        GearActor gearActor1 = new GearActor(world,-2,-1.5f,2.35f,2.35f,false);
        GearActor gearActor3 = new GearActor(world,2,-1.5f,2.35f,2.35f,false);
        GearActor gearActor2 = new GearActor(world,0,-2.5f,2.35f,2.35f,true);
        stage.addActor(gearActor1);
        stage.addActor(gearActor2);
        stage.addActor(gearActor3);


        new WindowsFrame(world,stage.getCamera().viewportWidth,stage.getCamera().viewportHeight);

        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0.1f, 0.0f, 0.0f, 0.1f);
        rayHandler.setBlurNum(3);


        PointLight pl = new PointLight(rayHandler, 128, new Color(0.2f,1,1,1f), 10,-2f,-1.5f);
        pl.setIgnoreAttachedBody(true);

        PointLight pl2 = new PointLight(rayHandler, 128, new Color(1,0,1,1f), 10,2f,-1.5f);
        pl2.setIgnoreAttachedBody(true);

        rayHandler.setShadows(true);
        pl.setStaticLight(false);
        pl.setSoft(true);
        pl2.setStaticLight(false);
        pl2.setSoft(true);
        BallGenerator.getInstance().setup(stage,world,rayHandler);
        stage.addActor(new FireEmitter(world));

    }

    @Override
    public void render () {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
        stage.draw();

        //debugRenderer.render(world, stage.getCamera().combined);

        BallGenerator.getInstance().emit();

        rayHandler.setCombinedMatrix(stage.getCamera().combined,0,0,1,1);
        rayHandler.updateAndRender();

    }

    @Override
    public void dispose () {
        batch.dispose();
        rayHandler.dispose();
    }

}