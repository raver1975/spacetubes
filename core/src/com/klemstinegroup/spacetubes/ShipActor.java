package com.klemstinegroup.spacetubes;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.quailshillstudio.DestructionData;

/**
 * Created by julienvillegas on 06/12/2017.
 */

public class ShipActor extends UserDataInterface {

    public enum TURNTYPE {MOUSE, LEFT, RIGHT, OFF}

    ;
    private final Vector2 v_offset;
    private final PointLight engineLight;
    private final PointLight shotLight;
    private final Texture whiteTexture;
    private World world;
    private boolean engineOn;
    private TURNTYPE turnType = TURNTYPE.OFF;
    private Vector3 testpoint = new Vector3();
    private BitmapFont font = new BitmapFont();
    private float ang;
    private SimplePID thrustController = new SimplePID(1.5f, .0001f, 0f, 128, 1f / 15f);

    public ShipActor(World aWorld, RayHandler rayHandler, float pos_x, float pos_y, float aWidth, float aHeight) {
        super(aWorld, rayHandler, new Texture("rocket.png"));
//        thrustController.setClamping(3000000, -3000000);
        thrustController.setOffset(0);
        this.rayHandler = rayHandler;
        Pixmap px = new Pixmap(1, 1, Format.RGBA8888);
        px.setColor(Color.WHITE);
        px.fill();
        TextureData texData = new PixmapTextureData(px, Format.RGBA8888, false, false, true);
        whiteTexture = new Texture(texData);
//        this.setTextureRegion(extractPixmapFromTextureRegion(new TextureRegion(new Texture("gfx/test01.png")), aWidth, aHeight));
        int scale1 = 16;
        Pixmap pixmap = new Pixmap((int) aWidth * scale1, (int) aHeight * scale1, Pixmap.Format.RGB888);
        final int MAX_COLOR = 6;
        final int MIN_COLOR = 0;
        double jump = (MAX_COLOR - MIN_COLOR) / (aWidth * scale1);
        for (int i = 0; i < aWidth * scale1; i++) {
            Color colors = GroundBoxActor.HSVtoRGB((float) ((jump * i)), 1.0f, 1.0f);
            pixmap.setColor(colors);
            pixmap.drawLine(i, 0, i, (int) aHeight * scale1);
        }
        this.setSize(aWidth, aHeight);
//        pixmap.drawPixmap(extractPixmapFromTextureRegion(new TextureRegion(new Texture("gfx/test01.png")), aWidth*scale, aHeight*scale),0,0);
//        setTextureRegion(pixmap);

//        setTextureRegion(new Texture("gfx/test01.png"));
        destr = new DestructionData(DestructionData.BALL);

        this.setPosition(pos_x, pos_y);
        world = aWorld;
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("rocket.json"));

        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.x = this.getX();
        bd.position.y = this.getY();
        bd.angle = 45 * MathUtils.degRad;
        float scale = this.getWidth();
        body = world.createBody(bd);
        body.setUserData(this);

        // 2. Create a FixtureDef, as usual.
        FixtureDef fd = new FixtureDef();
        fd.density = 10f;
        fd.friction = .9f;
        fd.restitution = .1f;
        fd.filter.groupIndex = 1;

        // 3. Create a Body, as usual.


        loader.attachFixture(body, "ship", fd, scale);
        v_offset = loader.getOrigin("ship", scale);
        System.out.println("loadr x,y:" + v_offset);
        this.setOrigin(v_offset.x, v_offset.y);
//        this.setCenter(v_offset.x/2, v_offset.y/2);
//        this.setCenter(this.getWidth()/2f,this.getHeight()/2f);
//        this.setCenter(0, 0);
//        this.setCenter(this.getWidth() / 2, this.getHeight() / 2);
        body.setUserData(this);
//        createVertex();
        shotLight = new PointLight(rayHandler, 32, new Color(1f, 1f, 1f, .7f), 20f, bd.position.x, bd.position.y);
        shotLight.attachToBody(body, 0, 0f);
        shotLight.setSoft(true);
        shotLight.setIgnoreAttachedBody(true);

        engineLight = new PointLight(rayHandler, 32, new Color(1f, .2f, .1f, .0f), 10f, bd.position.x, bd.position.y);
        engineLight.attachToBody(body, -.3f, -.3f);
        engineLight.setSoft(true);
        engineLight.setIgnoreAttachedBody(false);


        create();
    }


    public void thrust(boolean engineOn) {
        this.engineOn = engineOn;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        body.setAngularDamping(.5f);
        body.setLinearDamping(.2f);
//        body.setAngularDamping(.7f);
//        body.setLinearDamping(.2f);
        Vector2 f = new Vector2(new Vector2(-MathUtils.sin(body.getAngle() - 45 * MathUtils.degRad), MathUtils.cos(body.getAngle() - 45 * MathUtils.degRad)).scl(1));
//            Vector2 f1=new Vector2(testpoint.x,testpoint.y).sub(body.getPosition());
        Vector2 f1 = body.getPosition().sub(new Vector2(testpoint.x, testpoint.y));
        ang = MathUtils.PI - f1.angleRad(f);
        while (ang > MathUtils.PI) {
            ang -= MathUtils.PI2;
        }
        while (ang < -MathUtils.PI) {
            ang += MathUtils.PI2;
        }
        if (engineOn && (turnType != TURNTYPE.MOUSE || Math.abs(ang) < 1f)) {
            engineLight.setColor(MathUtils.clamp(engineLight.getColor().r + MathUtils.random(-.07f, .07f), 0, 1), MathUtils.clamp(engineLight.getColor().g + MathUtils.random(-.07f, .07f), 0, 1), MathUtils.clamp(engineLight.getColor().b + MathUtils.random(-.07f, .07f), 0, 1), MathUtils.clamp(engineLight.getColor().a + MathUtils.random(-.07f, .07f), .95f, 1));
            body.applyForceToCenter(new Vector2(0, 100).rotateRad(body.getAngle() - 45 * MathUtils.degRad), true);
        } else {
            engineLight.setColor(engineLight.getColor().r, engineLight.getColor().g, engineLight.getColor().b, .0f);
        }
        if (turnType != TURNTYPE.OFF) {
            switch (turnType) {
                case MOUSE:
                    getStage().getCamera().unproject(testpoint.set((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f));
                    break;
                case LEFT:
                    testpoint.set(body.getWorldCenter().x + MathUtils.cos(body.getAngle() + 135 * MathUtils.degRad) * 17, body.getWorldCenter().y + MathUtils.sin(body.getAngle() + 135 * MathUtils.degRad) * 17, 0);
//                    getStage().getCamera().unproject(testpoint.set((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f));
                    break;
                case RIGHT:
                    testpoint.set(body.getWorldCenter().x + MathUtils.cos(body.getAngle() - 45 * MathUtils.degRad) * 17, body.getWorldCenter().y + MathUtils.sin(body.getAngle() - 45 * MathUtils.degRad) * 17, 0);
                    break;
            }
//            Vector2 f = new Vector2(new Vector2(-MathUtils.sin(body.getAngle() - 45 * MathUtils.degRad), MathUtils.cos(body.getAngle() - 45 * MathUtils.degRad)).scl(1));
//            Vector2 f1 = body.getPosition().sub(new Vector2(testpoint.x, testpoint.y));

//            if (turnType == TURNTYPE.MOUSE && (ang < .002f && ang > -.002f && body.getAngularVelocity() < .002f && body.getAngularVelocity() > -.002f)) {
//                turnType = TURNTYPE.OFF;
//            }
//            ang = MathUtils.clamp(ang, -MathUtils.HALF_PI, MathUtils.HALF_PI);
            thrustController.update(ang + body.getAngularVelocity() / 10f, 0, Gdx.graphics.getDeltaTime());
//            body.applyForce(new Vector2(0, thrustController.getOutput()*200).rotateRad(ang-45*MathUtils.degRad), body.getLocalCenter().cpy().add(4, 4), true);
            body.applyTorque(thrustController.getOutput() * 10f, true);
        }
    }

    public void turn(TURNTYPE turn) {
        turnType = turn;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
//        getStage().getCamera().project(testpoint.set(body.getPosition().x, body.getPosition().y, 0f));
//        float ang=new Vector2(testpoint.x,testpoint.y).sub(body.getPosition()).angleRad()-body.getAngle();
//        ang+=-45* MathUtils.degRad;
//        while ( ang > MathUtils.PI){ang -= MathUtils.PI2;}
//        while ( ang < -MathUtils.PI){ang += MathUtils.PI2;}
//        font.getData().setScale(100);

//        if (Spacetubes.debug) {

//            font.draw(batch, ang * MathUtils.radDeg + "", testpoint.x + 10, testpoint.y + 10);
//            drawLine(batch, new Vector2(testpoint.x, testpoint.y), body.getPosition(), 1, whiteTexture);
            Vector2 f = body.getWorldCenter().cpy().add(body.getLinearVelocity().cpy().scl(.7f));
            drawLine(batch, f, body.getWorldCenter(), body.getLinearVelocity().len()/209, whiteTexture);
//        }
    }

    public static void drawLine(Batch batch, Vector2 v1, Vector2 v2, float thickness, Texture tex) {
        float length = v1.dst(v2); // get distance between those 2 points
        float dx = v1.x;
        ;
        float dy = v1.y;
        dx = dx - v2.x;
        dy = dy - v2.y;
        float angle = MathUtils.radiansToDegrees * MathUtils.atan2(dy, dx);
        angle = angle - 180;
        batch.draw(tex, v1.x, v1.y, 0f, thickness * 0.5f, length, thickness, 1f, 1f, angle, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
    }

    public void fire() {
        Vector2 tip=tipVector(1.3f);
        BallActor b = new BallActor(world, rayHandler, tip.x, tip.y);
        tip.set(-MathUtils.sin(body.getAngle() - 45 * MathUtils.degRad), MathUtils.cos(body.getAngle() - 45 * MathUtils.degRad));
        getStage().addActor(b);
        b.body.setLinearVelocity(tip.scl(1000000000).add(body.getLinearVelocity().cpy()));
//
//        b.body.setLinearVelocity(tip.scl(100000000));
    }

    public Vector2 tipVector(float scl){
        return new Vector2(body.getWorldCenter().x - MathUtils.sin(body.getAngle() - 45 * MathUtils.degRad)*scl, body.getWorldCenter().y + MathUtils.cos(body.getAngle() - 45 * MathUtils.degRad)*scl);
    }


}
