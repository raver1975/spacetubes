package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ShortArray;
import com.quailshillstudio.DestructionData;

/**
 * Created by julienvillegas on 07/12/2017.
 */

public class GroundBoxActor extends UserDataInterface {

    public GroundBoxActor( World aWorld, float x, float y, float width, float height) {
//        super(new Texture("gfx/test02.png"));
        super();
        Pixmap pixmap = new Pixmap((int) width, (int) height, Pixmap.Format.RGB888);
        pixmap.setColor(1, 1, 1, .1f);
        pixmap.fill();
        pixmap.setColor(1, 0, 0, .1f);
        pixmap.fillCircle((int)width/2,(int)height/2,(int)Math.min(width/2,height/2));
//        setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))));
        tr = new TextureRegion(new Texture(pixmap));
        this.setSize(width,height);
        this.setPosition(x, y);
        userData = new DestructionData(DestructionData.GROUND);
        world = aWorld;
        BodyDef bd = new BodyDef();
        bd.position.set(x, y);
        bd.type = BodyDef.BodyType.StaticBody;
        body=world.createBody(bd);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(width, height);
        body.setUserData(this);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.shape = groundBox;
        body.createFixture(fixtureDef);
        createVertex();
    }

}
