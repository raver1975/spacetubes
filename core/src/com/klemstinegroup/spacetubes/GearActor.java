package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.quailshillstudio.DestructionData;

/**
 * Created by julienvillegas on 06/12/2017.
 */

public class GearActor extends UserDataInterface {

    public GearActor(World aWorld, float pos_x, float pos_y, float aWidth, float aHeight, float clockwise) {
        super(new Texture("gear.png"));
//        super();
//        setTextureRegion(new Texture("gear.png"));
        Pixmap pixmap = new Pixmap((int) aWidth, (int) aHeight, Pixmap.Format.RGB888);
        final int MAX_COLOR = 6;
        final int MIN_COLOR = 0;
        double jump = (MAX_COLOR - MIN_COLOR) / (aWidth * 1.0);
        for (int i = 0; i < aWidth; i++) {
            Color colors = GroundBoxActor.HSVtoRGB((float) ((jump * i)), 1.0f, 1.0f);
            pixmap.setColor(colors);
            pixmap.drawLine(i, 0, i, (int) aHeight);
        }
        pixmap.drawPixmap(extractPixmapFromTextureRegion(new TextureRegion(new Texture("gear.png")), aWidth, aHeight),0,0);
        this.setTextureRegion(pixmap);
        destr = new DestructionData(DestructionData.GROUND);
        this.setSize(aWidth, aHeight);
        this.setPosition(pos_x, pos_y);

        world = aWorld;
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("box2d_scene.json"));

        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.KinematicBody;
        bd.position.x = this.getX();
        bd.position.y = this.getY();
        body = world.createBody(bd);
        body.setUserData(this);


        // 2. Create a FixtureDef, as usual.
        FixtureDef fd = new FixtureDef();
        fd.density = 1;
        fd.friction = 0.1f;
        fd.restitution = 0f;

        // 3. Create a Body, as usual.

        float scale = this.getWidth();
        loader.attachFixture(body, "gear", fd, scale);
        System.out.println("ddd:" + body.getFixtureList().get(0).getFilterData().groupIndex);
        System.out.println("ddd:" + body.getFixtureList().get(0).getFilterData().maskBits);
        System.out.println("ddd:" + body.getFixtureList().get(0).getFilterData().categoryBits);
        Vector2 v = loader.getOrigin("gear", scale);
//        setTextureRegion(pixmap);
//        setTextureRegion(new Texture("gear.png"));
//        this.setOrigin(v.x,v.y);
//        setCenter(v.x,v.y);
//        setDrawable(new TextureRegionDrawable(new Texture("gear.png")));
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
