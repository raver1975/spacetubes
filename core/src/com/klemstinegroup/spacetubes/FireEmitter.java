package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.graphics.ParticleEmitterBox2D;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by julienvillegas on 09/12/2017.
 */

public class FireEmitter extends Actor {

    ParticleEffect fireEmitter;

    public FireEmitter(World aWorld) {
        TextureAtlas textureAtlas = new TextureAtlas();
        textureAtlas.addRegion("particle", new TextureRegion(new Texture("particle.png")));
        fireEmitter = new ParticleEffect();
        fireEmitter.load(Gdx.files.internal("continous.p"), textureAtlas);
        fireEmitter.getEmitters().add(new ParticleEmitterBox2D(aWorld, fireEmitter.getEmitters().first()));
        fireEmitter.getEmitters().removeIndex(0);
        fireEmitter.setPosition(5, 2);
        fireEmitter.scaleEffect(.05f);
        fireEmitter.start();

    }

    float x = 5;
    float y = 2;

    @Override
    public void act(float delta) {
        super.act(delta);
        x = x - .05f;
        y = y - .05f;
        if (x<-5)x=5;
        if (y<-5)y=5;
        fireEmitter.setPosition(x, y);
        this.setRotation(this.getRotation()+10);
        fireEmitter.update(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        fireEmitter.draw(batch);
    }


}
