package com.klemstinegroup.spacetubes;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.graphics.ParticleEmitterBox2D;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.quailshillstudio.CollisionGeometry;
import com.quailshillstudio.DestructionData;

/**
 * Created by julienvillegas on 07/12/2017.
 */

public class BallActor extends UserDataInterface {
    private final PointLight pl2;
    private boolean delete;
    static private final Texture texture = new Texture("bubble.png");
    Array<ParticleEffect> explosionEffect = new Array<>();
    private boolean exploding;
    private boolean dead = false;
    private float ballScale = 30f;
    private float explosionScale = .3f;
    private int r;
    private int g;
    private int b;
    private float lightShrinkDist = 150;

    float size = 1f;

    public BallActor(World aWorld, RayHandler rayHandler, float pos_x, float pos_y) {
        super(aWorld,rayHandler,texture);
        while (r + g + b == 0) {
            r = MathUtils.randomBoolean() ? 1 : 0;
            g = MathUtils.randomBoolean() ? 1 : 0;
            b = MathUtils.randomBoolean() ? 1 : 0;
        }
        this.setSize(size / 2f, size / 2f);
        Pixmap pixmap = new Pixmap((int) size, (int) size, Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixmap(extractPixmapFromTextureRegion(new TextureRegion(texture), size, size), 0, 0);
//        setTextureRegion(pixmap);

        this.setPosition(pos_x, pos_y);
        destr = new DestructionData(DestructionData.BOMB);
        world = aWorld;
        BodyDef bd = new BodyDef();
        bd.position.set(this.getX(), this.getY());
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.bullet=true;
        bd.allowSleep=true;
        body = world.createBody(bd);
        body.setUserData(this);
        PolygonShape circle = new PolygonShape();
        float[] circVerts = CollisionGeometry.approxCircle(0, 0, getWidth() / 2f, 8);
//        Polygon p=new Polygon(circVerts);
        circle.setRadius(getWidth() / 2f);
//        float[] c1=new float[circVerts.length+2];
//        int c=0;
//        for (float f:circVerts){
//            c1[c]=circVerts[c++];
//        }
//        c1[c++]=c1[0];
//        c1[c++]=c1[1];
//        circVerts=p.getTransformedVertices();
        circle.set(circVerts);
        exploding = false;


        // 2. Create a FixtureDef, as usual.
        FixtureDef fd = new FixtureDef();
        fd.density = 2f;
        fd.friction = 0.0f;
        fd.restitution = 0f;
        fd.shape = circle;


        Fixture fixture = body.createFixture(fd);
//        this.setOrigin(getWidth()/2, getHeight()/2);
//        circle.dispose();
        Vector2 v = body.getPosition();
        pl2 = new PointLight(rayHandler, 128, new Color(r, g, b, 1f), size*10, v.x, v.y);
        pl2.setSoft(true);
        pl2.setSoftnessLength(100);
        pl2.setXray(true);
        pl2.attachToBody(body);
        pl2.setIgnoreAttachedBody(true);

//        create();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (exploding) {
            for (ParticleEffect explosionEffect : explosionEffect) {
                explosionEffect.draw(batch);
            }
        }

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        Vector2 v = body.getPosition();

        if (exploding) {
//            BallGenerator.getInstance().explode(this);
            body.applyForceToCenter(body.getLinearVelocity().cpy().scl(.05f + MathUtils.random(.01f) - .005f, .05f + MathUtils.random(.01f) - .005f).scl(explosionScale / 6f), true);
//            if (MathUtils.random() > .8f) {
            BallGenerator.getInstance().explode(this);
//            }
            for (ParticleEffect explosionEffect : explosionEffect) {
                FireEmitter.setAngle(explosionEffect, body.getAngle() * MathUtils.radiansToDegrees + 180 * (explosionScale > 0 ? 1 : 0));
                explosionEffect.setPosition(v.x, v.y);
                explosionEffect.setPosition(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2);
//                explosionEffect.scaleEffect(0.02f);
                explosionEffect.update(delta);
            }
//            Array<Body> bodies = new Array<>();
            /*orld.getBodies(bodies);
            if (bodies.contains(body,true))world.destroyBody(body);*/


            CircleShape circle = new CircleShape();
            ballScale += .1f;
            circle.setRadius(this.getWidth() / ballScale);
            body.getFixtureList().first().getShape().setRadius(this.getWidth() / ballScale);
            this.setScale(2f / ballScale);
            /*body.destroyFixture(body.getFixtureList().first());
            // 2. Create a FixtureDef, as usual.
            FixtureDef fd = new FixtureDef();
            fd.density = 10;
            fd.friction = 0.5f;
            fd.restitution = 0.6f;
            fd.shape = circle;
            Fixture fixture = body.createFixture(fd);*/

            boolean com = true;
            for (ParticleEffect explosionEffect : explosionEffect) {
                if (!explosionEffect.isComplete()) {
                    com = false;
                    break;
                }
            }
            if (explosionEffect.size > 200) {
                dead = true;
            }
            ;
            if (com) {
                world.destroyBody(body);
                for (ParticleEffect explosionEffect : explosionEffect) {
                    explosionEffect.dispose();
                }
                this.delete = true;
            }
        }
//        pl2.setPosition(v);

        setRotation(body.getAngle() * MathUtils.radiansToDegrees);

        this.setPosition(body.getPosition().x - this.getWidth() / 2, body.getPosition().y - this.getHeight() / 2);
        if (delete) {
            this.remove();
            pl2.remove();

        }

    }

    public void explode(ParticleEffect explosionEffect) {
//        if (MathUtils.random()<.03f&&getStage().getActors().size<BallGenerator.MAX_NBR*50) {
//            Ball b = new Ball(this.world, this.getX(), this.getY());
//            getStage().addActor(b);
//            b.setScale(this.getScaleX(), this.getScaleY());
//        }


        if (!dead) {
            explosionEffect.getEmitters().add(new ParticleEmitterBox2D(world, explosionEffect.getEmitters().first()));
            explosionEffect.getEmitters().removeIndex(0);
            explosionEffect.setPosition(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2);
//            explosionEffect.scaleEffect(0.02f);

            explosionEffect.scaleEffect(explosionScale + MathUtils.random(.01f));
            explosionScale -= .002f;
//            setTextureRegion(new Pixmap(1, 1, Format.RGBA8888));
            ParticleEmitter pe = explosionEffect.getEmitters().get(explosionEffect.getEmitters().size - 1);
            FloatArray fa = new FloatArray();
            int n = pe.getTint().getTimeline().length;
            for (int i = 0; i < n; i++) {
                fa.addAll(r + MathUtils.random(-.4f, .4f), g + MathUtils.random(-.4f, .4f), b + MathUtils.random(-.4f, .4f));
            }
            pe.getTint().setColors(fa.toArray());
            explosionEffect.start();
            this.explosionEffect.add(explosionEffect);
            pl2.setDistance(lightShrinkDist -= .1f);
            pl2.setContactFilter((short) 1, (short) 1, (short) 2);
            pl2.setColor(r, g, b, 1);
            pl2.setSoft(true);
            body.setLinearDamping(2f);

            exploding = true;
        }
    }

}
