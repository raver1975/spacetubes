package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.quailshillstudio.UserData;
import com.quailshillstudio.UserDataInterface;

/**
 * Created by julienvillegas on 06/12/2017.
 */

public class GearActor extends UserDataInterface {

    public GearActor(World aWorld, float pos_x, float pos_y, float aWidth, float aHeight, float clockwise) {
        super(new Texture("gear.png"));
        userData = new UserData(UserData.BALL);
        this.setSize(aWidth, aHeight);
        this.setPosition(pos_x, pos_y);

        world = aWorld;
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("box2d_scene.json"));

        BodyDef bd = new BodyDef();
        bd.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        bd.type = BodyDef.BodyType.KinematicBody;
        bd.position.x = this.getX();
        bd.position.y = this.getY();
        body = world.createBody(bd);


        // 2. Create a FixtureDef, as usual.
        FixtureDef fd = new FixtureDef();
        fd.density = 1;
        fd.friction = 0.1f;
        fd.restitution = 0f;

        // 3. Create a Body, as usual.

        float scale = this.getWidth();
        loader.attachFixture(body, "gear", fd, scale);
        this.setOrigin(this.getWidth() / 2, this.getHeight() / 2);
        body.setAngularVelocity(clockwise);
        body.setUserData(this);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);


    }

    @Override
    public void act(float delta) {
        super.act(delta);
        this.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        this.setPosition(body.getPosition().x - this.getWidth() / 2, body.getPosition().y - this.getHeight() / 2);

    }
}
