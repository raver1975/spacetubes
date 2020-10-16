package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.quailshillstudio.CollisionGeometry;
import com.quailshillstudio.PolygonBox2DShape;
import com.quailshillstudio.UserData;

import java.util.HashSet;

/**
 * Created by julienvillegas on 07/12/2017.
 */

public class B2dContactListener implements ContactListener {
    public float circRadius = 6f;
    public int segments = 16;

    private final Spacetubes spacetubes;
    private HashSet<String> debugSet = new HashSet<>();
    private static final boolean debug = true;

    public B2dContactListener(Spacetubes spacetubes) {
        super();
        this.spacetubes = spacetubes;

    }

    @Override
    public void beginContact(Contact contact) {
        String classA = contact.getFixtureA().getBody().getUserData().getClass().getName();
        String classB = contact.getFixtureB().getBody().getUserData().getClass().getName();

        if (debug) {
            String collis = classA + "\tcollide with:" + classB;
            if (!debugSet.contains(collis)) {
                System.out.println(collis);
                debugSet.add(collis);
            }
        }
        clippingGround(contact);
        try {
            BallActor ball = (BallActor) (contact.getFixtureA().getBody().getUserData());
            BallGenerator.getInstance().explode(ball);
//            ball.userData.type=UserData.BALL;
        } catch (Exception e) {
        }
        clippingGround(contact);
        try {
            BallActor ball = (BallActor) (contact.getFixtureB().getBody().getUserData());
            BallGenerator.getInstance().explode(ball);
//            ball.userData.type=UserData.BALL;
        } catch (Exception e) {
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private void clippingGround(Contact contact) {
        Body a1 = contact.getFixtureA().getBody();
        Body b1 = contact.getFixtureB().getBody();
        Body ground = null;
        Body bomb = null;
        if (((UserDataInterface) a1.getUserData()).getUserData().getType() == UserData.BOMB) {
            bomb = a1;
        }
        if (((UserDataInterface) b1.getUserData()).getUserData().getType() == UserData.BOMB) {
            bomb = b1;
        }
        if (((UserDataInterface) a1.getUserData()).getUserData().getType() == UserData.GROUND) {
            ground = a1;
        }
        if (((UserDataInterface) b1.getUserData()).getUserData().getType() == UserData.GROUND) {
            ground = b1;
        }
        if (ground == null || bomb == null) {
            return;
        }
        System.out.println("collision:" + ground.getUserData().getClass().getName() + "\t" + bomb.getUserData().getClass().getName());
        Array<float[]> totalRS = new Array<>();
        bomb.applyForceToCenter(new Vector2(0, 40000), true);

//        float[] circVerts = CollisionGeometry.approxCircle(b.getPosition().x, b.getPosition().y - a.getPosition().y, circRadius, segments);
        Vector2 v = new Vector2(bomb.getPosition().x - ground.getPosition().x - circRadius / 2, bomb.getPosition().y - ground.getPosition().y - circRadius / 2);
        v.rotateRad(-ground.getAngle());
        float[] circVerts = CollisionGeometry.approxCircle(v.x, v.y, circRadius, segments);
        ChainShape shape = new ChainShape();
        shape.createLoop(circVerts);
//            if (circVerts.length >= 6) {
//            } else {
//                shape.createChain(circVerts);
//            }

        PolygonBox2DShape circlePoly = new PolygonBox2DShape(shape);
        Body body = ground;

        Array<Fixture> fixtureList = body.getFixtureList();
        int fixCount = fixtureList.size;
        for (int i = 0; i < fixCount; i++) {
            PolygonBox2DShape polyClip = null;

//                if (fixtureList.get(i).getShape() instanceof PolygonShape) {
            try {
                polyClip = new PolygonBox2DShape((PolygonShape) fixtureList.get(i).getShape());
            } catch (Exception e) {
                polyClip = new PolygonBox2DShape((ChainShape) fixtureList.get(i).getShape());
            }


//                }
            Array<PolygonBox2DShape> rs = polyClip.differenceCS(circlePoly);

            for (int y = 0; y < rs.size; y++) {
                rs.get(y).circleContact(bomb.getPosition().cpy().sub(ground.getPosition()), circRadius);
//                    rs.get(y).ConstPolygonBox2DShape(shape);
                totalRS.add(rs.get(y).vertices());
            }
//                totalRS.add(rs.get(0).vertices());
        }
        spacetubes.switchGround(totalRS, (UserDataInterface) ground.getUserData());
//            ((UserDataInterface) body.getUserData()).getUserData().mustDestroy = true;
    }

}
