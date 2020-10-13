package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.quailshillstudio.UserDataInterface;
import com.quailshillstudio.UserData;

/**
 * Created by julienvillegas on 06/12/2017.
 */

public class GroundActor extends UserDataInterface {

    private final Vector2 position;
    private final Vector2 v_offset;
    private World world;
    private float angle;

    public GroundActor(World aWorld, float pos_x, float pos_y, float aWidth, float aHeight) {
        super(new Texture("gfx/test02.png"));
        userData=new UserData(UserData.GROUND);
        this.setSize(aWidth,aHeight);
        this.setPosition(pos_x, pos_y);

        world = aWorld;
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("test.json"));

        BodyDef bd = new BodyDef();
//        bd.position.set(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.x = this.getX();
        bd.position.y = this.getY();
        float scale = this.getWidth();
        body = world.createBody(bd);


        // 2. Create a FixtureDef, as usual.
        FixtureDef fd = new FixtureDef();
        fd.density = 1f;
        fd.friction = 0f;
        fd.restitution = 1f;

        // 3. Create a Body, as usual.


        loader.attachFixture(body, "test02", fd, scale);
        v_offset=loader.getOrigin("test02",scale);
        System.out.println("loadr x,y:"+v_offset);
        this.setOrigin(v_offset.x,v_offset.y);
//        body.setAngularVelocity(clockwise?1:-1);
        body.setUserData(this);
        position=body.getPosition().cpy();

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
//        body.setTransform(position.cpy(),body.getAngle());
//        body.setAwake(true);


    }

    @Override
    public void act(float delta) {
        super.act(delta);
        this.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
//        body.setTransform(position.cpy(),body.getAngle());
//        body.setAwake(true);
        this.setPosition(body.getPosition().x-this.getWidth()/2 , body.getPosition().y -this.getHeight()/2);

    }
}
