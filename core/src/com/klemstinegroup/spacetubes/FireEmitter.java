package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.graphics.ParticleEmitterBox2D;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

/**
 * Created by julienvillegas on 09/12/2017.
 */

public class FireEmitter extends Actor {

    ParticleEffect fireEmitter;
    float x = 0f;
    float y = 1f;
    float ang = 0;
    int lifetime=100;
    public FireEmitter(World aWorld, Vector3 vv) {
        TextureAtlas textureAtlas = new TextureAtlas();
        textureAtlas.addRegion("particle", new TextureRegion(new Texture("particle.png")));
        fireEmitter = new ParticleEffect();
        fireEmitter.load(Gdx.files.internal("continous.p"), textureAtlas);
        fireEmitter.getEmitters().add(new ParticleEmitterBox2D(aWorld, fireEmitter.getEmitters().first()));
        fireEmitter.getEmitters().removeIndex(0);
        fireEmitter.setPosition(vv.x,vv.y);
        x=vv.x;
        y=vv.y;
//        ang=vv.z;

        ang = new Vector2(-vv.x,Math.abs(vv.y)).angleDeg();
        fireEmitter.scaleEffect(.1f);
        fireEmitter.start();

    }



    @Override
    public void act(float delta) {
        super.act(delta);
        if (lifetime<0){
            fireEmitter.getEmitters().clear();
            return;
        }
        else{lifetime--;}
//        x = x - .01f;
//        y = y - .01f;
//        if (x < -2.5) x = 2.5f;
//        if (y < -2.5) y = 2.5f;
//        ang -= 2;

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
angle.setHighMax(180);
angle.setHighMin(0);
angle.setLowMax(180);
angle.setLowMin(0);
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
