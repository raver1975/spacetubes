package com.klemstinegroup.spacetubes;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import com.quailshillstudio.CollisionGeometry;
import com.quailshillstudio.DestructionData;

import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody;

public class CarActor extends UserDataInterface {
    //    private var world:b2World=new b2World(new b2Vec2(0,10),true);
//    private var worldScale:int=30;
    private Body car;
    private RevoluteJoint rearWheelRevoluteJoint;
    private RevoluteJoint frontWheelRevoluteJoint;
    private Boolean left = false;
    private Boolean right = true;
    private float motorSpeed = 1;
    private float worldScale = 20;
    private PrismaticJoint frontAxlePrismaticJoint;
    private PrismaticJoint rearAxlePrismaticJoint;

    public CarActor(World world, RayHandler rayHandler, float x, float y) {
        super(world, rayHandler);
        this.setSize(120/worldScale,20/worldScale);

        this.setScale(2,2);// fixture

        setDestr(new DestructionData(DestructionData.BALL));
        int scale1=16;
        Pixmap pixmap = new Pixmap((int) this.getWidth()*scale1, (int) getHeight()*scale1, Pixmap.Format.RGB888);
        final int MAX_COLOR = 6;
        final int MIN_COLOR = 0;
        double jump = (MAX_COLOR - MIN_COLOR) / (getWidth()*scale1);
        for (int i = 0; i < getWidth()*scale1; i++) {
            Color colors = GroundBoxActor.HSVtoRGB((float) ((jump * i)), 1.0f, 1.0f);
            pixmap.setColor(colors);
            pixmap.drawLine(i, 0, i, (int) getHeight()*scale1);
        }
        setTextureRegion(pixmap);
//        this.setOffset(60/worldScale,10/worldScale);
//        this.setOrigin(60/worldScale,10/worldScale);
//        this.setOrigin(0,0);
        this.setOffset(getWidth()/2,getHeight()/2);
//        this.setOrigin(0,0);
        this.setOrigin(getWidth()/2,getHeight()/2);
        //        debugDraw();
//        // ************************ THE FLOOR ************************ //
//        // shape
//        PolygonShape floorShape = new PolygonShape();
//        floorShape.setAsBox(640/worldScale,10/worldScale);
//        // fixture
//        FixtureDef floorFixture= new FixtureDef();
//        floorFixture.density=0;
//        floorFixture.friction=3;
//        floorFixture.restitution=0;
//        floorFixture.shape=floorShape;
//        // body definition
//        BodyDef floorBodyDef= new BodyDef();
//        floorBodyDef.position.set(320/worldScale,480/worldScale);
//        // the floor itself
//        Body floor=world.createBody(floorBodyDef);
//        floor.createFixture(floorFixture);
        // ************************ THE CAR ************************ //
        // shape
        PolygonShape carShape = new PolygonShape();
        carShape.setAsBox(120 / worldScale, 20 / worldScale);

//        this.setOffset((120/worldScale)/2,(20/worldScale)/2);
        this.setPosition(x,y);
        FixtureDef carFixture = new FixtureDef();
        carFixture.density = 50;
        carFixture.friction = 3;
        carFixture.restitution = 0.3f;
        carFixture.filter.groupIndex = -1;
        carFixture.shape = carShape;
        // body definition
        BodyDef carBodyDef = new BodyDef();
        carBodyDef.type = DynamicBody;
        carBodyDef.position.set(x,y);
        // ************************ THE TRUNK ************************ //
        // shape
        PolygonShape trunkShape = new PolygonShape();
        trunkShape.setAsBox(40 / worldScale, 40 / worldScale, new Vector2(-80 / worldScale, -60 / worldScale), worldScale);
        // fixture
        FixtureDef trunkFixture = new FixtureDef();
        trunkFixture.density = 1;
        trunkFixture.friction = 3;
        trunkFixture.restitution = 0.3f;
        trunkFixture.filter.groupIndex = -1;
        trunkFixture.shape = trunkShape;
        // ************************ THE HOOD ************************ //
        // shape
        PolygonShape hoodShape = new PolygonShape();
        Vector2[] carVector = new Vector2[3];
        carVector[0] = new Vector2(-40 / worldScale, -20 / worldScale);
        carVector[1] = new Vector2(-40 / worldScale, -100 / worldScale);
        carVector[2] = new Vector2(120 / worldScale, -20 / worldScale);
        hoodShape.set(carVector);
        // fixture
        FixtureDef hoodFixture = new FixtureDef();
        hoodFixture.density = 1;
        hoodFixture.friction = 3;
        hoodFixture.restitution = 0.3f;
        hoodFixture.filter.groupIndex = -1;
        hoodFixture.shape = hoodShape;
        // ************************ MERGING ALL TOGETHER ************************ //
        // the car itself
        car = world.createBody(carBodyDef);
        car.setUserData(this);
        this.body = car;
        car.createFixture(carFixture);
        car.createFixture(trunkFixture);
        car.createFixture(hoodFixture);
        // ************************ THE AXLES ************************ //
        // shape
        PolygonShape axleShape = new PolygonShape();
        axleShape.setAsBox(20 / worldScale, 20 / worldScale);
        // fixture
        FixtureDef axleFixture = new FixtureDef();
        axleFixture.density = 0.5f;
        axleFixture.friction = 3;
        axleFixture.restitution = 0.3f;
        axleFixture.shape = axleShape;
        axleFixture.filter.groupIndex = -1;
        // body definition
        BodyDef axleBodyDef = new BodyDef();
        axleBodyDef.type = DynamicBody;
        // the rear axle itself
        axleBodyDef.position.set(car.getWorldCenter().x - (60 / worldScale), car.getWorldCenter().y + (65 / worldScale));
        Body rearAxle = world.createBody(axleBodyDef);
        rearAxle.setUserData(this);
        rearAxle.createFixture(axleFixture);
        // the front axle itself
        axleBodyDef.position.set(car.getWorldCenter().x + (75 / worldScale), car.getWorldCenter().y + (65 / worldScale));
        Body frontAxle = world.createBody(axleBodyDef);
        frontAxle.setUserData(this);
        frontAxle.createFixture(axleFixture);
        // ************************ THE WHEELS ************************ //
        // shape

//        CircleShape wheelShape = new CircleShape();
//        wheelShape.setRadius(40 / worldScale);

        ChainShape wheelShapeRear = new ChainShape();
        float[] circVerts = CollisionGeometry.approxCircle(0,0, 40/worldScale, 6);
        wheelShapeRear.setRadius(40/worldScale);
        wheelShapeRear.createLoop(circVerts);

        // fixture
        FixtureDef wheelFixtureRear = new FixtureDef();
        wheelFixtureRear.density = 1;
        wheelFixtureRear.friction = 3;
        wheelFixtureRear.restitution = 0.1f;
        wheelFixtureRear.filter.groupIndex = -1;
        wheelFixtureRear.shape = wheelShapeRear;
        // body definition
        BodyDef wheelBodyDef = new BodyDef();
        wheelBodyDef.type = DynamicBody;
        // the real wheel itself
        wheelBodyDef.position.set(car.getWorldCenter().x - (60 / worldScale), car.getWorldCenter().y + (65 / worldScale));
        Body rearWheel = world.createBody(wheelBodyDef);
        rearWheel.setUserData(this);
        rearWheel.createFixture(wheelFixtureRear);
        // the front wheel itself
        ChainShape wheelShapeFront = new ChainShape();
        float[] circVerts2 = CollisionGeometry.approxCircle(0,0, 40/worldScale, 6);
        wheelShapeFront.setRadius(40/worldScale);
        wheelShapeFront.createLoop(circVerts2);

        // fixture
        FixtureDef wheelFixtureFront = new FixtureDef();
        wheelFixtureFront.density = 1;
        wheelFixtureFront.friction = 3;
        wheelFixtureFront.restitution = 0.1f;
        wheelFixtureFront.filter.groupIndex = -1;
        wheelFixtureFront.shape = wheelShapeFront;

        wheelBodyDef.position.set(car.getWorldCenter().x + (75 / worldScale), car.getWorldCenter().y + (65 / worldScale));
        Body frontWheel = world.createBody(wheelBodyDef);
        frontWheel.setUserData(this);
        frontWheel.createFixture(wheelFixtureFront);
        // ************************ REVOLUTE JOINTS ************************ //
        // rear joint
        RevoluteJointDef rearWheelRevoluteJointDef = new RevoluteJointDef();
        rearWheelRevoluteJointDef.initialize(rearWheel, rearAxle, rearWheel.getWorldCenter());
        rearWheelRevoluteJointDef.enableMotor = true;
        rearWheelRevoluteJointDef.maxMotorTorque = 10000;
        RevoluteJoint rearWheelRevoluteJoint = (RevoluteJoint) world.createJoint(rearWheelRevoluteJointDef);
        // front joint
        RevoluteJointDef frontWheelRevoluteJointDef = new RevoluteJointDef();
        frontWheelRevoluteJointDef.initialize(frontWheel, frontAxle, frontWheel.getWorldCenter());
        frontWheelRevoluteJointDef.enableMotor = true;
        frontWheelRevoluteJointDef.maxMotorTorque = 10000;
        RevoluteJoint frontWheelRevoluteJoint = (RevoluteJoint) world.createJoint(frontWheelRevoluteJointDef);
        // ************************ PRISMATIC JOINTS ************************ //
        //  definition
        PrismaticJointDef axlePrismaticJointDef = new PrismaticJointDef();
        axlePrismaticJointDef.lowerTranslation = -20 / worldScale;
        axlePrismaticJointDef.upperTranslation = 5 / worldScale;
        axlePrismaticJointDef.enableLimit = true;
        axlePrismaticJointDef.enableMotor = true;
        // front axle
        axlePrismaticJointDef.initialize(car, frontAxle, frontAxle.getWorldCenter(), new Vector2(0, 1));
        PrismaticJoint frontAxlePrismaticJoint = (PrismaticJoint) world.createJoint(axlePrismaticJointDef);
        // rear axle
        axlePrismaticJointDef.initialize(car, rearAxle, rearAxle.getWorldCenter(), new Vector2(0, 1));
        PrismaticJoint rearAxlePrismaticJoint = (PrismaticJoint) world.createJoint(axlePrismaticJointDef);

//        addEventListener(Event.ENTER_FRAME,updateWorld);
//        stage.addEventListener(KeyboardEvent.KEY_DOWN,keyPressed);
//        stage.addEventListener(KeyboardEvent.KEY_UP,keyReleased);
//    }
/*    private function keyPressed(e:KeyboardEvent):void {
        switch (e.keyCode) {
            case 37 :
                left=true;
                break;
            case 39 :
                right=true;
                break;
        }
    }
    private function keyReleased(e:KeyboardEvent):void {
        switch (e.keyCode) {
            case 37 :
                left=false;
                break;
            case 39 :
                right=false;
                break;
        }
    }
    private function updateWorld(e:Event):void {
        if (left) {
            motorSpeed+=0.5;
        }
        if (right) {
            motorSpeed-=0.5;
        }
        motorSpeed*=0.99;
        if (motorSpeed>100) {
            motorSpeed=100;
        }

    }
}*/

        rearWheelRevoluteJoint.setMotorSpeed(motorSpeed);
        frontWheelRevoluteJoint.setMotorSpeed(motorSpeed);
        frontAxlePrismaticJoint.setMaxMotorForce(Math.abs(600 * frontAxlePrismaticJoint.getJointTranslation()));
        frontAxlePrismaticJoint.setMotorSpeed((frontAxlePrismaticJoint.getMotorSpeed() - 2 * frontAxlePrismaticJoint.getJointTranslation()));
        rearAxlePrismaticJoint.setMaxMotorForce(Math.abs(600 * rearAxlePrismaticJoint.getJointTranslation()));
        rearAxlePrismaticJoint.setMotorSpeed((rearAxlePrismaticJoint.getMotorSpeed() - 2 * rearAxlePrismaticJoint.getJointTranslation()));
//        world.step(1 / 30, 10, 10);
//        world.clearForces();
        Array<Body> temp = new Array<Body>();
        temp.add(body);
        temp.add(frontWheel);
        temp.add(rearWheel);
        create();
    }
}