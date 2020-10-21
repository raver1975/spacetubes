package com.klemstinegroup.spacetubes;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.quailshillstudio.CollisionGeometry;
import com.quailshillstudio.DestructionData;

public class PlanetActor extends UserDataInterface{
    public PlanetActor(World world, RayHandler rayHandler, Vector2 position, float radius){
        super(world,rayHandler);
        Pixmap p=new Pixmap((int)radius*2, (int)radius*2, Format.RGBA8888);
        p.setColor(Color.GREEN);
        p.fillCircle((int)radius,(int)radius,(int)radius);
        setTextureRegion(p);

        this.setPosition(position.x,position.y);
        this.setSize(radius*2,radius*2);
        this.setOrigin(radius , radius);
        this.setOffset(radius*2,radius*2);
        this.setScale(2,2);
        destr = new DestructionData(DestructionData.GROUND);
        BodyDef bd = new BodyDef();
        bd.position.set(position.cpy());
        bd.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bd);
        body.setUserData(this);
        PolygonShape circle = new PolygonShape();
        float[] circVerts = CollisionGeometry.approxCircle(0, 0, radius, 8);
        circle.setRadius(getWidth() / 2f);
        circle.set(circVerts);
        FixtureDef fd = new FixtureDef();
        fd.density = 2f;
        fd.friction = 0.0f;
        fd.restitution = 1f;
        fd.shape = circle;
        Fixture fixture = body.createFixture(fd);
        Vector2 v = body.getPosition();
        PointLight pl2 = new PointLight(rayHandler, 128, new Color(0, 1, 0, 1f), 400f, v.x, v.y);
        pl2.attachToBody(body);
        pl2.setIgnoreAttachedBody(true);
    }
}
