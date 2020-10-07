package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by julienvillegas on 07/12/2017.
 */

public class B2dContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        String classA = contact.getFixtureA().getBody().getUserData().getClass().getName();
        String classB = contact.getFixtureB().getBody().getUserData().getClass().getName();

//        Gdx.app.debug("begin Contact","between: "+classA+" and "+ classB);
        if(classA.equalsIgnoreCase("com.klemstinegroup.spacetubes.WindowsFrame") && classB.equalsIgnoreCase("com.klemstinegroup.spacetubes.Ball")){
            Ball ball = (Ball)(contact.getFixtureB().getBody().getUserData());
            BallGenerator.getInstance().explode(ball);

        }
        else if(classB.equalsIgnoreCase("com.klemstinegroup.spacetubes.WindowsFrame") && classA.equalsIgnoreCase("com.klemstinegroup.spacetubes.Ball")){
            Ball ball = (Ball)(contact.getFixtureA().getBody().getUserData());
            BallGenerator.getInstance().explode(ball);
        }
        else if(!(classA.equalsIgnoreCase("com.klemstinegroup.spacetubes.Ball") && classB.equalsIgnoreCase("com.klemstinegroup.spacetubes.Ball"))&&(classA.equalsIgnoreCase("com.klemstinegroup.spacetubes.Ball")||classB.equalsIgnoreCase("com.klemstinegroup.spacetubes.Ball"))){
            try {
                Ball ball = (Ball) (contact.getFixtureA().getBody().getUserData());
                BallGenerator.getInstance().explode(ball);
            }
            catch (Exception e){}
            try {
                Ball ball = (Ball) (contact.getFixtureB().getBody().getUserData());
                BallGenerator.getInstance().explode(ball);
            }
            catch (Exception e){}

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
}
