package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.quailshillstudio.CollisionGeometry;
import com.quailshillstudio.PolygonBox2DShape;
import com.quailshillstudio.DestructionData;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

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
            String collis = classA + "\t: " + classB;
            if (!debugSet.contains(collis)) {
                debugSet.add(collis);
            }
            System.out.println(collis);
        }

        clippingGround(contact);
//        try {
//            BallActor ball = (BallActor) (contact.getFixtureA().getBody().getUserData());
//            BallGenerator.getInstance().explode(ball);
//        } catch (Exception e) {
//        }
//        try {
//            BallActor ball = (BallActor) (contact.getFixtureB().getBody().getUserData());
//            BallGenerator.getInstance().explode(ball);
//        } catch (Exception e) {
//        }

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
        UserDataInterface ground = null;
        UserDataInterface bomb = null;
        if (((UserDataInterface) a1.getUserData()).getDestr().getType() == DestructionData.BOMB) {
            bomb = (UserDataInterface) a1.getUserData();
        }
        if (((UserDataInterface) b1.getUserData()).getDestr().getType() == DestructionData.BOMB) {
            bomb = (UserDataInterface) b1.getUserData();;
        }
        if (((UserDataInterface) a1.getUserData()).getDestr().getType() == DestructionData.GROUND) {
            ground = (UserDataInterface) a1.getUserData();
        }
        if (((UserDataInterface) b1.getUserData()).getDestr().getType() == DestructionData.GROUND) {
            ground = (UserDataInterface) b1.getUserData();
        }

        if (ground == null || bomb == null) {
            System.out.println("---------------------------------");
            System.out.println(ground+"\t"+bomb);
            System.out.println(((UserDataInterface) a1.getUserData()).getDestr().getType()+"\t"+((UserDataInterface) b1.getUserData()).getDestr().getType());
            System.out.println(a1.getUserData().getClass().toString()+"\t"+b1.getUserData().getClass().toString());
            System.out.println("-------------------------------------------------");
//
            return;
        }
        Gdx.app.log("debug:","collision:" + ground.getClass().getName() + "\t" + bomb.getClass().getName());

        if (contact.getWorldManifold().getNumberOfContactPoints()==2){
            Vector2 point1 = contact.getWorldManifold().getPoints()[0];
            Vector2 point2 = contact.getWorldManifold().getPoints()[1];
            int n=5;
            Vector2[] t=new Vector2[n];
            for (int i=0;i<n;i++){
                t[i]=point1.lerp(point2,(float)i/(float)n).cpy();
            }
            ground.collide(bomb,t);
        }
        else{
            ground.collide(bomb,contact.getWorldManifold().getPoints());
        }

//        spacetubes.polyVerts.add(ground);

    }

}
