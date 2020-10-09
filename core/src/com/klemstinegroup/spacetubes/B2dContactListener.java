package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.quailshillstudio.UserDataInterface;
import com.quailshillstudio.polygonClippingUtils.CollisionGeometry;
import com.quailshillstudio.polygonClippingUtils.PolygonBox2DShape;
import com.quailshillstudio.polygonClippingUtils.UserData;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by julienvillegas on 07/12/2017.
 */

public class B2dContactListener implements ContactListener {
    public float circRadius = 4f;
    public int segments = 10;

    private final Spacetubes spacetubes;
    private HashSet<String> debugSet = new HashSet<>();
    private static final boolean debug = true;

    public B2dContactListener(Spacetubes spacetubes) {
        super();
        this.spacetubes = spacetubes;

    }

    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        String classA = contact.getFixtureA().getBody().getUserData().getClass().getName();
        String classB = contact.getFixtureB().getBody().getUserData().getClass().getName();

        if (debug) {
            String collis = classA + "\tcollide with:" + classB;
            if (!debugSet.contains(collis)) {
                System.out.println(collis);
                debugSet.add(collis);
            }
        }

//        Gdx.app.debug("begin Contact","between: "+classA+" and "+ classB);
        if (classA.equalsIgnoreCase("com.klemstinegroup.spacetubes.WindowsFrame") && classB.equalsIgnoreCase("com.klemstinegroup.spacetubes.Ball")) {
            Ball ball = (Ball) (contact.getFixtureB().getBody().getUserData());
            BallGenerator.getInstance().explode(ball);

        } else if (classB.equalsIgnoreCase("com.klemstinegroup.spacetubes.WindowsFrame") && classA.equalsIgnoreCase("com.klemstinegroup.spacetubes.Ball")) {
            Ball ball = (Ball) (contact.getFixtureA().getBody().getUserData());
            BallGenerator.getInstance().explode(ball);
        }
//        else if(!(classA.equalsIgnoreCase("com.klemstinegroup.spacetubes.Ball") && classB.equalsIgnoreCase("com.klemstinegroup.spacetubes.Ball"))&&(classA.equalsIgnoreCase("com.klemstinegroup.spacetubes.Ball")||classB.equalsIgnoreCase("com.klemstinegroup.spacetubes.Ball"))){
        else if ((classA.equalsIgnoreCase("com.klemstinegroup.spacetubes.Ball") || classB.equalsIgnoreCase("com.klemstinegroup.spacetubes.Ball"))) {
//            System.out.println(classA+"\t"+classB);
            try {
                Ball ball = (Ball) (contact.getFixtureA().getBody().getUserData());
                BallGenerator.getInstance().explode(ball);
                clippingGround(contact.getFixtureA().getBody(), contact.getFixtureB().getBody());
            } catch (Exception e) {
            }
            try {
                Ball ball = (Ball) (contact.getFixtureB().getBody().getUserData());
                BallGenerator.getInstance().explode(ball);
                clippingGround(contact.getFixtureB().getBody(), contact.getFixtureA().getBody());
            } catch (Exception e) {
            }

        }

    }


    private void clippingGround(Body a1, Body b1) {
        Body a = null;
        Body b = null;
        if (((UserDataInterface) a1.getUserData()).getUserData().getType() == UserData.BOMB) {
            b = a1;
        }
        if (((UserDataInterface) b1.getUserData()).getUserData().getType() == UserData.BOMB) {
            b = b1;
        }
        if (((UserDataInterface) a1.getUserData()).getUserData().getType() == UserData.GROUND) {
            a = a1;
        }
        if (((UserDataInterface) b1.getUserData()).getUserData().getType() == UserData.GROUND) {
            a = b1;
        }
        System.out.println("collision:" + a.getUserData().getClass().getName() + "\t" + b.getUserData().getClass().getName());
        if (a == null || b == null) {
            return;
        }
        Array<PolygonBox2DShape> totalRS = new Array<PolygonBox2DShape>();

        float[] circVerts = CollisionGeometry.approxCircle(b.getPosition().x, b.getPosition().y, circRadius, segments);
        if (circVerts.length >= 4) {
            ChainShape shape = new ChainShape();
            if (circVerts.length >= 6) {
                shape.createLoop(circVerts);
            } else if (circVerts.length >= 4) {
                shape.createChain(circVerts);
            }

            PolygonBox2DShape circlePoly = new PolygonBox2DShape(shape);
            Body body = a;

            Array<Fixture> fixtureList = body.getFixtureList();
            int fixCount = fixtureList.size;
            for (int i = 0; i < fixCount; i++) {
                PolygonBox2DShape polyClip = null;
                if (fixtureList.get(i).getShape() instanceof PolygonShape) {
                    polyClip = new PolygonBox2DShape((PolygonShape) fixtureList.get(i).getShape());
                } else if (fixtureList.get(i).getShape() instanceof ChainShape) {
                    polyClip = new PolygonBox2DShape((ChainShape) fixtureList.get(i).getShape());
                }
                Array<PolygonBox2DShape> rs = polyClip.differenceCS(circlePoly);
                for (int y = 0; y < rs.size; y++) {
                    rs.get(y).circleContact(b.getPosition(), circRadius);
                    totalRS.add(rs.get(y));
                }
            }
            spacetubes.switchGround(totalRS);
            ((UserData) body.getUserData()).mustDestroy = true;
        }
    }

}
