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
import com.badlogic.gdx.graphics.g3d.environment.AmbientCubemap;
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

    private final Vector2 v_offset;
    private final PointLight pl2;
    private final Texture whiteTexture;
    private World world;
    private boolean engineOn;
    private boolean turnOn;
    private Vector3 testpoint=new Vector3();
    private BitmapFont font = new BitmapFont();
    private float ang;

    public ShipActor(World aWorld, RayHandler rayHandler, float pos_x, float pos_y, float aWidth, float aHeight) {
        super(new Texture("rocket.png"));
        Pixmap.Format format;
        Pixmap px=new Pixmap(1, 1, Format.RGBA8888);
        px.setColor(Color.WHITE);
        px.fill();
        TextureData texData = new PixmapTextureData(px, Format.RGBA8888, false, false, true);
        whiteTexture = new Texture(texData);
//        this.setTextureRegion(extractPixmapFromTextureRegion(new TextureRegion(new Texture("gfx/test01.png")), aWidth, aHeight));
        int scale1=16;
        Pixmap pixmap = new Pixmap((int) aWidth*scale1, (int) aHeight*scale1, Pixmap.Format.RGB888);
        final int MAX_COLOR = 6;
        final int MIN_COLOR = 0;
        double jump = (MAX_COLOR - MIN_COLOR) / (aWidth * scale1);
        for (int i = 0; i < aWidth*scale1; i++) {
            Color colors = GroundBoxActor.HSVtoRGB((float) ((jump * i)), 1.0f, 1.0f);
            pixmap.setColor(colors);
            pixmap.drawLine(i, 0, i, (int) aHeight*scale1);
        }
        this.setSize(aWidth, aHeight);
//        pixmap.drawPixmap(extractPixmapFromTextureRegion(new TextureRegion(new Texture("gfx/test01.png")), aWidth*scale, aHeight*scale),0,0);
//        setTextureRegion(pixmap);

//        setTextureRegion(new Texture("gfx/test01.png"));
        destr = new DestructionData(DestructionData.BOMB);

        this.setPosition(pos_x, pos_y);
        world = aWorld;
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("rocket.json"));

        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.x = this.getX();
        bd.position.y = this.getY();
        float scale = this.getWidth();
        body = world.createBody(bd);
        body.setUserData(this);

        // 2. Create a FixtureDef, as usual.
        FixtureDef fd = new FixtureDef();
        fd.density = 10f;
        fd.friction = 1f;
        fd.restitution = .1f;
        fd.filter.groupIndex=1;

        // 3. Create a Body, as usual.


        loader.attachFixture(body, "ship", fd, scale);
        v_offset = loader.getOrigin("ship", scale);
        System.out.println("loadr x,y:" + v_offset);
        this.setOrigin(v_offset.x,v_offset.y);
//        this.setCenter(v_offset.x/2, v_offset.y/2);
//        this.setCenter(this.getWidth()/2f,this.getHeight()/2f);
//        this.setCenter(0, 0);
//        this.setCenter(this.getWidth() / 2, this.getHeight() / 2);
        body.setUserData(this);
//        createVertex();
        pl2 = new PointLight(rayHandler, 32, new Color(1f, .2f, .1f, .0f), 100f, bd.position.x, bd.position.y);
        pl2.attachToBody(body,-4.99f,-4.99f);
        pl2.setSoft(false);
        pl2.setIgnoreAttachedBody(false);
        create();
    }


    public void thrust(boolean engineOn) {
        this.engineOn =engineOn;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        body.setAngularDamping(.11f);
        body.setLinearDamping(.1f);
        if (engineOn){
            pl2.setColor(MathUtils.clamp(pl2.getColor().r+MathUtils.random(.1f),0,1),MathUtils.clamp(pl2.getColor().g+MathUtils.random(.1f),0,1),MathUtils.clamp(pl2.getColor().b+MathUtils.random(.1f),0,1),MathUtils.clamp(pl2.getColor().a+MathUtils.random(.1f),.8f,1));
            body.applyForceToCenter(new Vector2(0,30000).rotateRad(body.getAngle()),true);
        }
        else{
            pl2.setColor(pl2.getColor().r,pl2.getColor().g,pl2.getColor().b,.0f);
        }
        if (turnOn) {
            getStage().getCamera().unproject(testpoint.set((float)Gdx.input.getX(), (float)Gdx.input.getY(), 0f));
            Vector2 f=new Vector2(-MathUtils.sin(body.getAngle()-45*MathUtils.degRad),MathUtils.cos(body.getAngle()-45*MathUtils.degRad)).scl(1);
//            Vector2 f1=new Vector2(testpoint.x,testpoint.y).sub(body.getPosition());
            Vector2 f1=body.getPosition().sub(new Vector2(testpoint.x,testpoint.y));
            ang=MathUtils.PI-f1.angleRad(f);
            while ( ang > MathUtils.PI){ang -= MathUtils.PI2;}
            while ( ang < -MathUtils.PI){ang += MathUtils.PI2;}
            ang=MathUtils.clamp(ang,-MathUtils.HALF_PI,MathUtils.HALF_PI);
//            body.applyForce(new Vector2((ang>0?1:-1)*(ang*1000), 0).rotateRad(body.getAngle()-45*MathUtils.degRad), body.getLocalCenter().cpy().add(0, 4), true);
            body.applyTorque(-(MathUtils.sin(ang))*100000,true);
            System.out.println("DD:"+-(MathUtils.sin(ang)));
        }
    }

    public void turn(boolean turn) {
        turnOn=turn;
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
        font.draw(batch,ang*MathUtils.radDeg+"",testpoint.x+10,testpoint.y+10);
        drawLine(batch, new Vector2(testpoint.x,testpoint.y),body.getPosition(),1,whiteTexture);
        Vector2 f=new Vector2(-MathUtils.sin(body.getAngle()-45*MathUtils.degRad),MathUtils.cos(body.getAngle()-45*MathUtils.degRad)).scl(new Vector2(testpoint.x,testpoint.y).sub(body.getPosition()).len()).add(body.getPosition());
         drawLine(batch,f ,body.getPosition(),1,whiteTexture);
    }

    public static void drawLine(Batch batch, Vector2 v1,Vector2 v2, float thickness, Texture tex)
    {
        float length = v1.dst(v2); // get distance between those 2 points
        float dx = v1.x;;
        float dy = v1.y;
        dx = dx - v2.x;
        dy = dy - v2.y;
        float angle = MathUtils.radiansToDegrees*MathUtils.atan2(dy, dx);
        angle = angle-180;
        batch.draw(tex, v1.x, v1.y, 0f, thickness*0.5f, length, thickness, 1f, 1f, angle, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
    }
}
