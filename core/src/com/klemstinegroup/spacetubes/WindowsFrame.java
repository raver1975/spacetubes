package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.quailshillstudio.UserData;
import com.quailshillstudio.UserDataInterface;

/**
 * Created by julienvillegas on 07/12/2017.
 */

public class WindowsFrame extends UserDataInterface {

    public WindowsFrame(World aWorld, float x, float y,float width, float height) {
        super(new UserData(UserData.GROUND));
        world = aWorld;
        BodyDef bd = new BodyDef();
        bd.position.set(x,y);
        bd.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bd);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(width, height);
        body.setUserData(this);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.shape = groundBox;
        body.createFixture(fixtureDef);


    }


}
