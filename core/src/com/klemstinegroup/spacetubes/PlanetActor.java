package com.klemstinegroup.spacetubes;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.quailshillstudio.CollisionGeometry;
import com.quailshillstudio.DestructionData;
import zk.planet_generator.generators.NoiseGenerator;

public class PlanetActor extends UserDataInterface{

    public static final int BUFFER_WIDTH = 640;
    public static final int BUFFER_HEIGHT = 480;
    public static final int CENTER_X = BUFFER_WIDTH / 2;
    public static final int CENTER_Y = BUFFER_HEIGHT / 2;
//    private Planet planet;
    private ShaderProgram planetShader;
    private float time;
    private float rotationSpeed;
    private float direction=1;

    public PlanetActor(World world, RayHandler rayHandler, Vector2 position, float radius){
        super(world,rayHandler);
        Pixmap p=new Pixmap((int)radius*2, (int)radius*2, Format.RGBA8888);
        p.setColor(Color.GREEN);
        p.fillCircle((int)radius,(int)radius,(int)radius);
        setTextureRegion(p);

        this.setPosition(position.x,position.y);
        this.setSize(radius*2,radius*2);
        this.setOrigin(radius , radius);
        this.setOffset(radius*2,radius*2);
        this.setScale(2,2);
        destr = new DestructionData(DestructionData.GROUND);
        BodyDef bd = new BodyDef();
        bd.position.set(position.cpy());
        bd.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bd);
        body.setUserData(this);
        ChainShape circle = new ChainShape();
        float[] circVerts = CollisionGeometry.approxCircle(0, 0, radius, 360);
        circle.setRadius(getWidth() / 2f);
        circle.createLoop(circVerts);
        FixtureDef fd = new FixtureDef();
        fd.density = 2f;
        fd.friction = 0.0f;
        fd.restitution = 1f;
        fd.shape = circle;
        Fixture fixture = body.createFixture(fd);
        Vector2 v = body.getPosition();

        createPlanet();
        rotationSpeed = 1/50f;
        planetShader = new ShaderProgram(Gdx.files.internal("shaders/planet.vsh"), Gdx.files.internal("shaders/planet.fsh"));
        if(!planetShader.isCompiled()) {
            Gdx.app.error("Planet Shader Error", "\n" + planetShader.getLog());
        }
        create();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setShader(planetShader);
//        batch.setProjectionMatrix(getStage().getCamera().projection);
//        batch.setTransformMatrix(getStage().getCamera().combined);
//        planetShader.bind();
//        planetShader.setUniformMatrix("u_projTrans",getStage().getCamera().projection);
        super.draw(batch, parentAlpha);
        batch.setShader(null);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        time += rotationSpeed * delta;

        // If the code below this comment is moved to update, it causes graphic issues with orbiting objects
        planetShader.begin();
        planetShader.setUniformf("time", direction * time);
        planetShader.end();
    }

    private void createPlanet() {
        setTextureRegion(generatePlanetPixmap(1024));

//        int size = MathUtils.random(100, 148);
//        planet.setSize(size, size);
//        planet.setPosition(CENTER_X - planet.getWidth() / 2, CENTER_Y - planet.getHeight() / 2);
//        this.planet = new Planet(planet, getPixmap(), velDir);
    }

    private Pixmap generatePlanetPixmap(int size) {
        float[][] generated = NoiseGenerator.GenerateWhiteNoise(size, size);
        generated = NoiseGenerator.GeneratePerlinNoise(generated, 8);

        Pixmap pixmap = new Pixmap(generated.length, generated.length, Pixmap.Format.RGBA8888);
        for (int x = 0; x < generated.length; x++) {
            for (int y = 0; y < generated.length; y++) {
                double value = generated[x][y];

                if(value < 0.40f) {
                    // Deep ocean
                    pixmap.drawPixel(x, y, Color.rgba8888(47f / 255f, 86f / 255f, 118f / 255f, 1f));
                } else if (value < 0.55f) {
                    // Ocean
                    pixmap.drawPixel(x, y, Color.rgba8888(62f / 255f, 120f / 255f, 160f / 255f, 1f));
                } else {
                    // Land
                    pixmap.drawPixel(x, y, Color.rgba8888(146f / 255f, 209f / 255f, 135f / 255f, 1f));
                }
            }
        }
        return pixmap;
    }
}
