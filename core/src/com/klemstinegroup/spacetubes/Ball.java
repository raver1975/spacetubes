package com.klemstinegroup.spacetubes;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.graphics.ParticleEmitterBox2D;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

/**
 * Created by julienvillegas on 07/12/2017.
 */

public class Ball extends Image {
    private final PointLight pl2;
    private Body body;
    private World world;
    private boolean delete;
    static private final Texture texture = new Texture("bubble.png");
    Array<ParticleEffect> explosionEffect = new Array<>();
    private boolean exploding;
    private boolean dead = false;
    private float scale = 2;

    public Ball(World aWorld, RayHandler rayHandler, float pos_x, float pos_y) {
        super(texture);
//        setDrawable(null);
        this.setSize(0.3f, 0.3f);
        this.setPosition(pos_x, pos_y);

        world = aWorld;
        BodyDef bd = new BodyDef();
        bd.position.set(this.getX(), this.getY());
        bd.type = BodyDef.BodyType.DynamicBody;


        body = world.createBody(bd);
        CircleShape circle = new CircleShape();
        circle.setRadius(this.getWidth() / 2);
        exploding = false;


        // 2. Create a FixtureDef, as usual.
        FixtureDef fd = new FixtureDef();
        fd.density = 10;
        fd.friction = 0.5f;
        fd.restitution = 0.6f;
        fd.shape = circle;

        Fixture fixture = body.createFixture(fd);
        body.setUserData(this);

        this.setOrigin(this.getWidth() / 2, this.getHeight() / 2);
        circle.dispose();
        Vector2 v = body.getPosition();
        pl2 = new PointLight(rayHandler, 128, new Color(1, 1, 1, 1f), 1f, v.x, v.y);
        pl2.setIgnoreAttachedBody(true);
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
            BallGenerator.getInstance().explode(this);
            for (ParticleEffect explosionEffect : explosionEffect) {
                FireEmitter.setAngle(explosionEffect, body.getAngle() * MathUtils.radiansToDegrees + 180);
                explosionEffect.setPosition(v.x, v.y);
                explosionEffect.update(delta);
            }
//            Array<Body> bodies = new Array<>();
            /*orld.getBodies(bodies);
            if (bodies.contains(body,true))world.destroyBody(body);*/


            CircleShape circle = new CircleShape();
            scale += .42f;
            circle.setRadius(this.getWidth() / scale);
            body.getFixtureList().first().getShape().setRadius(this.getWidth() / scale);
            this.setScale(2f / scale);
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
            if ( explosionEffect.size > 200){dead=true;};
            if (com ) {
                world.destroyBody(body);
                for (ParticleEffect explosionEffect : explosionEffect) {
                    explosionEffect.dispose();
                }
                this.delete = true;
            }
        }
        pl2.setPosition(v);

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
//        BallGenerator.getInstance().explode(b);
        if (!dead) {
            explosionEffect.getEmitters().add(new ParticleEmitterBox2D(world, explosionEffect.getEmitters().first()));
            explosionEffect.getEmitters().removeIndex(0);
            explosionEffect.setPosition(this.getX() + this.getWidth() / 2, this.getY());
            explosionEffect.scaleEffect(0.02f);

            explosionEffect.start();
            this.explosionEffect.add(explosionEffect);

            exploding = true;
        }
    }

}
