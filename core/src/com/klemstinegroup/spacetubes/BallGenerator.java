package com.klemstinegroup.spacetubes;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.Random;

/**
 * Created by julienvillegas on 08/12/2017.
 */

public class BallGenerator {


    public static final int MAX_NBR = 5;
    Random rand = new Random();
    private World world;
    private Stage stage;

    private ParticleEffectPool ballExplosionPool;


    private static BallGenerator ballGenerator;
    private RayHandler rayHandler;

    static public BallGenerator getInstance() {
        if (ballGenerator == null) {
            ballGenerator = new BallGenerator();
        }
        return ballGenerator;
    }

    private BallGenerator() {
        TextureAtlas textureAtlas = new TextureAtlas();
        textureAtlas.addRegion("particle", new TextureRegion(new Texture("particle.png")));
        ParticleEffect explosionEffect = new ParticleEffect();
        explosionEffect.load(Gdx.files.internal("particles.p"), textureAtlas);
        ballExplosionPool = new ParticleEffectPool(explosionEffect, MAX_NBR * 20, MAX_NBR * 20);
    }

    public void setup(Stage aStage, World aWorld, RayHandler rayHandler) {
        stage = aStage;
        world = aWorld;
        this.rayHandler=rayHandler;
    }


    public void emit() {
        while (stage.getActors().size < MAX_NBR) {
            BallActor ball = new BallActor(world, rayHandler,rand.nextInt(100) - 50, (rand.nextInt(100) +50));
            stage.addActor(ball);

//            Ball ball2 = new Ball(world, rayHandler,(float) ((rand.nextInt(1000) - 500)) / 10, (((rand.nextInt(1000) - 500)) / 10)+70);
//            stage.addActor(ball2);
//            Gdx.app.debug("generatBalls", "Balls:" + ballNbr);
        }
    }

    public void explode(BallActor aBall) {
        ParticleEffectPool.PooledEffect effect = ballExplosionPool.obtain();
        aBall.explode(effect);
    }
}
