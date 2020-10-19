package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.graphics.ParticleEmitterBox2D;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

/**
 * Created by julienvillegas on 09/12/2017.
 */

public class FireEmitter extends Actor {

    ParticleEffect fireEmitter;

    public FireEmitter(World aWorld, Vector2 vv) {
        TextureAtlas textureAtlas = new TextureAtlas();
        textureAtlas.addRegion("particle", new TextureRegion(new Texture("particle.png")));
        fireEmitter = new ParticleEffect();
        fireEmitter.load(Gdx.files.internal("continous.p"), textureAtlas);
        fireEmitter.getEmitters().add(new ParticleEmitterBox2D(aWorld, fireEmitter.getEmitters().first()));
        fireEmitter.getEmitters().removeIndex(0);
        fireEmitter.setPosition(vv.x,vv.y);
        fireEmitter.scaleEffect(.021f);
        fireEmitter.start();

    }

    float x = 0f;
    float y = 1f;
    float ang = 0;

    @Override
    public void act(float delta) {
        super.act(delta);
//        x = x - .01f;
//        y = y - .01f;
//        if (x < -2.5) x = 2.5f;
//        if (y < -2.5) y = 2.5f;
        ang -= 2;

        fireEmitter.setPosition(x, y);
//        this.setRotation(this.getRotation()+10);
        setAngle(fireEmitter, ang);
        fireEmitter.update(delta);
    }

    public static void setAngle(ParticleEffect pe, float targetAngle) {
        if (pe == null) {
            return;
        }
        Array<ParticleEmitter> g = pe.getEmitters();
        if (g != null) {
            for (ParticleEmitter emitter : g) {
//        ParticleEmitter emitter = fireEmitter.getEmitters().first(); // find the emitter you want to     rotate here
//        float targetAngle = /* your target angle */

                ParticleEmitter.ScaledNumericValue angle = emitter.getAngle();

                /* find angle property and adjust that by letting the min, max of low and high span their current size around your angle */

                float angleHighMin = angle.getHighMin();
                float angleHighMax = angle.getHighMax();
                float spanHigh = angleHighMax - angleHighMin;
                angle.setHigh(targetAngle - spanHigh / 2.0f, targetAngle + spanHigh / 2.0f);

                float angleLowMin = angle.getLowMin();
                float angleLowMax = angle.getLowMax();
                float spanLow = angleLowMax - angleLowMin;
                angle.setLow(targetAngle - spanLow / 2.0f, targetAngle + spanLow / 2.0f);
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        fireEmitter.draw(batch);
    }


}
