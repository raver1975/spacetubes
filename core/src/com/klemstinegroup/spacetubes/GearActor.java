package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.quailshillstudio.DestructionData;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

/**
 * Created by julienvillegas on 06/12/2017.
 */

public class GearActor extends UserDataInterface {

    public GearActor(World aWorld, float pos_x, float pos_y, float aWidth, float aHeight, float clockwise) {
        super(new Texture("gear.png"));
//        super();
        Pixmap pixmap = new Pixmap((int) aWidth, (int) aHeight, Pixmap.Format.RGB888);
        pixmap.setColor(1, 0, 1, 1f);
        pixmap.fill();
        pixmap.setColor(1, 1, 0, 1f);
        pixmap.fillCircle((int)aWidth/2,(int)aHeight/2,(int)Math.max(aWidth/2,aHeight/2));
//        setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))));
//        setTextureRegion(pixmap);
        destr = new DestructionData(DestructionData.BALL);
        this.setSize(aWidth, aHeight);
        this.setPosition(pos_x, pos_y);

        world = aWorld;
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("box2d_scene.json"));

        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.KinematicBody;
        bd.position.x = this.getX();
        bd.position.y = this.getY();
        body=world.createBody(bd);
        body.setUserData(this);


        // 2. Create a FixtureDef, as usual.
        FixtureDef fd = new FixtureDef();
        fd.density = 1;
        fd.friction = 0.1f;
        fd.restitution = 0f;

        // 3. Create a Body, as usual.

        float scale = this.getWidth();
        loader.attachFixture(body, "gear", fd, scale);

        Vector2 v=loader.getOrigin("gear",scale);
        this.setOrigin(v.x,v.y);
        setCenter(v.x,v.y);
        body.setAngularVelocity(clockwise);

        create();
//        createVertex();
    }




//    @Override
//    public void draw(Batch batch, float parentAlpha) {
//        super.draw(batch, parentAlpha);
//
//
//    }
//
//    @Override
//    public void act(float delta) {
//        super.act(delta);
//        this.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
//        this.setPosition(body.getPosition().x - this.getWidth() / 2, body.getPosition().y - this.getHeight() / 2);
//
//    }
}
