package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.quailshillstudio.CollisionGeometry;
import com.quailshillstudio.PolygonBox2DShape;
import com.quailshillstudio.UserData;
import com.quailshillstudio.UserDataInterface;

import java.util.HashSet;

/**
 * Created by julienvillegas on 07/12/2017.
 */

public class B2dContactListener implements ContactListener {
    public float circRadius = 4f;
    public int segments = 8;

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
        try {
            BallActor ball = (BallActor) (contact.getFixtureA().getBody().getUserData());
            BallGenerator.getInstance().explode(ball);
            clippingGround(contact.getFixtureA().getBody(), contact.getFixtureB().getBody());
            ball.userData.type=UserData.BALL;
        } catch (Exception e) {
        }
        try {
            BallActor ball = (BallActor) (contact.getFixtureB().getBody().getUserData());
            BallGenerator.getInstance().explode(ball);
            clippingGround(contact.getFixtureB().getBody(), contact.getFixtureA().getBody());
            ball.userData.type=UserData.BALL;
        } catch (Exception e) {
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
        if (a == null || b == null) {
            return;
        }
        System.out.println("collision:" + a.getUserData().getClass().getName() + "\t" + b.getUserData().getClass().getName());
        Array<float[]> totalRS = new Array<>();
        b.applyForceToCenter(new Vector2(0, 200000), true);

        float[] circVerts = CollisionGeometry.approxCircle(b.getPosition().x - a.getPosition().x, b.getPosition().y - a.getPosition().y, circRadius, segments);
//        float[] circVerts = CollisionGeometry.approxCircle(b.getPosition().x-a.getPosition().x, b.getPosition().y-a.getPosition().x, circRadius, segments);
            ChainShape shape = new ChainShape();
            shape.createLoop(circVerts);
//            if (circVerts.length >= 6) {
//            } else {
//                shape.createChain(circVerts);
//            }

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
                    totalRS.add(rs.get(y).verticesToLoop());
                }
            }
            spacetubes.switchGround(totalRS, (UserDataInterface) a.getUserData());
//            ((UserDataInterface) body.getUserData()).getUserData().mustDestroy = true;
        }

}
