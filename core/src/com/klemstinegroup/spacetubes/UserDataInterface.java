package com.klemstinegroup.spacetubes;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;
import com.quailshillstudio.CollisionGeometry;
import com.quailshillstudio.DestructionData;
import com.quailshillstudio.PolygonBox2DShape;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

public class UserDataInterface extends Image {
    public static final float circRadius = 10f;
    public static final int segments = 16;
    public static final EarClippingTriangulator triangulator = new EarClippingTriangulator();

    public DestructionData destr = null;
    public Body body;
    public World world;
    public BodyDef tempBodyDef;
    public Array<FixtureDef> tempFixtureDefs = new Array<>();
    public Array<float[]> verts = new Array<>();
    protected RayHandler rayHandler;
    protected Vector2 linearVelocity;
    private PixmapTextureData texData;
    private Array<PolygonRegion> pS = new Array<PolygonRegion>();
    private Vector2 offset = new Vector2();
//    private Array<Vector2> linearVelocities=new Array<>();

    public UserDataInterface(World world, RayHandler rayHandler, Texture texture) {
        super(texture);
        setTextureRegion(extractPixmapFromTextureRegion(new TextureRegion(texture), texture.getWidth(), texture.getHeight()));
        this.world = world;
        this.rayHandler = rayHandler;
    }

    public Pixmap getPixmap() {
        TextureRegion temp = ((TextureRegionDrawable) getDrawable()).getRegion();
        return extractPixmapFromTextureRegion(temp, temp.getRegionWidth(), temp.getRegionHeight());
    }

    public Pixmap extractPixmapFromTextureRegion(TextureRegion textureRegion, float aWidth, float aHeight) {
        TextureData textureData = textureRegion.getTexture().getTextureData();
        if (!textureData.isPrepared()) {
            textureData.prepare();
        }
        Pixmap pixmap = new Pixmap((int) aWidth, (int) aHeight, textureData.getFormat());
        pixmap.drawPixmap(
                textureData.consumePixmap(), // The other Pixmap
                textureRegion.getRegionX(), // The source x-coordinate (top left corner)
                textureRegion.getRegionY(), // The source y-coordinate (top left corner)
                textureRegion.getRegionWidth(), // The width of the area from the other Pixmap in pixels
                textureRegion.getRegionHeight(), // The height of the area from the other Pixmap in pixels
                0, // The target x-coordinate (top left corner)
                0, // The target y-coordinate (top left corner)
                (int) aWidth,
                (int) aHeight
        );
        return pixmap;
    }

    public UserDataInterface(World world, RayHandler rayHandler) {
        this.world = world;
        this.rayHandler = rayHandler;
    }

    public void setTextureRegion(TextureRegion tr) {
        setDrawable(new TextureRegionDrawable(new TextureRegion(tr)));
        setOrigin(getWidth() / 2, getHeight() / 2);
    }

    public void setTextureRegion(Pixmap pixmap) {
        texData = new PixmapTextureData(pixmap, Pixmap.Format.RGBA8888, false, false, true);
        Texture texture = new Texture(texData);
        setTextureRegion(new TextureRegion(texture));
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public DestructionData getDestr() {
        return destr;
    }

    public void setDestr(DestructionData destr) {
        this.destr = destr;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void collide(UserDataInterface bomb, Vector2[] points) {
//        if(true)return;
        tempBodyDef = Box2DUtils.createDef(body);
        for (Fixture f : body.getFixtureList()) {
            tempFixtureDefs.add(Box2DUtils.createDef(f));
        }
        Array<float[]> totalRS = new Array<>();
        Array<PolygonBox2DShape> shapes = new Array<>();
        for (Vector2 vv : points) {
            Vector2 t = new Vector2(body.getWorldCenter().cpy().sub(vv)).nor().scl(-1000);
            bomb.body.applyForceToCenter(t, true);
            t = new Vector2(bomb.body.getWorldCenter().cpy().sub(vv)).nor().scl(-1000);
            body.applyForceToCenter(t, true);
            this.getStage().addActor(new FireEmitter(world, new Vector3(vv.x, vv.y, this.body.getAngle())));
            Vector2 v = new Vector2(vv.x - this.body.getWorldCenter().x, vv.y - this.body.getWorldCenter().y);
            v.rotateRad(-this.body.getAngle());
            float[] circVerts = CollisionGeometry.approxCircle(v.x, v.y, circRadius / 2, segments);
            ChainShape shape = new ChainShape();
            shape.createLoop(circVerts);
            PolygonBox2DShape circlePoly = new PolygonBox2DShape(shape);
            shapes.add(circlePoly);
        }
        Array<Fixture> fixtureList = body.getFixtureList();
        int fixCount = fixtureList.size;
        for (int i = 0; i < fixCount; i++) {
            PolygonBox2DShape polyClip;
            try {
                polyClip = new PolygonBox2DShape((PolygonShape) fixtureList.get(i).getShape());
            } catch (Exception e) {
                polyClip = new PolygonBox2DShape((ChainShape) fixtureList.get(i).getShape());
            }
            Array<PolygonBox2DShape> rstot = new Array<>();
            rstot.add(polyClip);
            for (PolygonBox2DShape po : shapes) {
                for (PolygonBox2DShape p2 : rstot) {
                    Array<PolygonBox2DShape> rs = p2.differenceCS(po);
                    rstot.removeValue(p2, true);
                    rstot.addAll(rs);
                }

            }
            for (int y = 0; y < rstot.size; y++) {
//                for (int e = 0; e < shapes.size; e++) {
//                    rstot.get(y).circleContact(points[e].cpy().sub(this.body.getPosition()), circRadius);
//                }
                totalRS.add(rstot.get(y).vertices());
            }
        }
        verts = totalRS;
        createPolgyonShapes();
//        uuid = bomb.uuid;
        this.destr.mustDestroy = true;
    }

    public Array<float[]> getVerts(Body body) {
        Array<float[]> totalRS = new Array<>();
        Array<Fixture> fixtureList = body.getFixtureList();
        int fixCount = fixtureList.size;
        for (int i = 0; i < fixCount; i++) {
            PolygonBox2DShape polyClip;
            try {
                polyClip = new PolygonBox2DShape((PolygonShape) fixtureList.get(i).getShape());
            } catch (Exception e) {
                polyClip = new PolygonBox2DShape((ChainShape) fixtureList.get(i).getShape());

            }
            totalRS.add(polyClip.vertices());
        }
        return totalRS;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (PolygonRegion psa : pS) {
            ((PolygonSpriteBatch) batch).draw(psa, body.getPosition().x - getOriginX() * getScaleX() + offset.x, body.getPosition().y - getOriginY() * getScaleY() + offset.y, getOriginX(), getOriginY(), getWidth(), getHeight(),
                    getScaleX(), getScaleY(), body.getAngle() * MathUtils.radiansToDegrees);
//            psa.draw((PolygonSpriteBatch) batch);
        }
        if (Spacetubes.debug && MathUtils.random() < .05f) super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        this.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        this.setPosition(body.getPosition().x - getOriginX(), body.getPosition().y - getOriginY());
        if (linearVelocity != null) {
            this.body.applyForceToCenter(linearVelocity.cpy(), true);
            linearVelocity = null;
        }
    }

    void create(Array<Body> bodies) {
        verts = new Array<>();
        for (Body body : bodies) {
            this.tempBodyDef = Box2DUtils.createDef(body);
            for (Fixture f : body.getFixtureList()) {
                tempFixtureDefs.add(Box2DUtils.createDef(f));
            }
            verts.addAll(getVerts(body));
        }

        createPolgyonShapes();
        this.destr.mustDestroy = true;
    }

    void create() {
        this.tempBodyDef = Box2DUtils.createDef(body);
        for (Fixture f : body.getFixtureList()) {
            tempFixtureDefs.add(Box2DUtils.createDef(f));
        }
        verts = getVerts(body);

        createPolgyonShapes();
        this.destr.mustDestroy = true;
    }

    private void createPolgyonShapes() {
        pS.clear();
        for (float[] f : verts) {
            float[] f1 = new float[f.length + 2];
            int u = 0;
            float scx = texData.getWidth() / getWidth();
            float scy = texData.getHeight() / getHeight();
            scx = 1f / scx;
            scy = 1f / scy;
            for (u = 0; u < f.length; u += 2) {
                f1[u] = (f[u] + getOriginX() * getScaleX()) / (scx * getScaleX());// + this.getWidth() / 2f;
                f1[u + 1] = (f[u + 1] + getOriginY() * getScaleY()) / (scy * getScaleY());// + this.getHeight() / 2f;
            }
            f1[u++] = f1[0];
            f1[u++] = f1[1];
            ShortArray triangleIndices = triangulator.computeTriangles(f1);
            PolygonRegion pR = new PolygonRegion(new TextureRegion(new Texture(texData)), f1, triangleIndices.toArray());
//this.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(texData))));
            pR.getRegion().getTexture().setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
            pS.add(pR);
        }
    }

    protected void createGround() {
        if (world == null) return;
        //Iterator<BodyDef> uditbd=polyVertsBodyDef.iterator();
        Gdx.app.log("debug:", "creating polyvert:" + getClass().toString());
//            System.out.println(groundDef.toString());
//            groundDef.type = ud.body.getType();
//            groundDef.active = true;
        this.destr.mustDestroy = false;
        this.destr.destroyed = false;
        Body newbody = world.createBody(tempBodyDef);
        newbody.setUserData(this);
//            Array<Fixture> fixtures = new Array<>();
//            ud.verts=ud.getVerts();
//        verts=getVerts();
        for (int y = 0; y < verts.size; y++) {
            if (verts.get(y).length >= 6) {

                FixtureDef fixtureDef = new FixtureDef();
                FixtureDef fixtureDef1 = tempFixtureDefs.get(y);
                fixtureDef.density = fixtureDef1.density;//ud.body.getFixtureList().get(0).getDensity();
                fixtureDef.friction = fixtureDef1.friction;//ud.body.getFixtureList().get(0).getFriction();
                fixtureDef.restitution = fixtureDef1.restitution;//ud.body.getFixtureList().get(0).getRestitution();
                fixtureDef.filter.categoryBits = 1;
                fixtureDef.filter.maskBits = -1;
                fixtureDef.filter.groupIndex = 0;
                ChainShape shape = new ChainShape();
                float[] f = verts.get(y);
                Polygon p = new Polygon(f);
                float scx = texData.getWidth() / getWidth();
                float scy = texData.getHeight() / getHeight();
//                p.setScale(scx,scy);
                f = p.getTransformedVertices();
                int hh = 0;
//                Array<Fixture> fixtures = new Array<>();
                while (true) {
                    boolean flag = true;
                    for (hh = 0; hh < f.length - 3; hh += 2) {
                        while (b2SquaredDistance(f[hh], f[hh + 1], f[hh + 2], f[hh + 3]) < (0.000025f)) {
                            f[hh + 2] += MathUtils.random(.0001f);
                            f[hh + 3] += MathUtils.random(.0001f);
                            flag = false;
                        }
                    }
                    while (b2SquaredDistance(f[hh], f[hh + 1], f[0], f[1]) < (0.000025f)) {
                        f[0] += MathUtils.random(.0001f);
                        f[1] += MathUtils.random(.0001f);
                        flag = false;
                    }
                    if (flag) break;
                }
                shape.createLoop(f);
//                FixtureDef tt = tempFixtureDefs.get(y);
                fixtureDef.shape = shape;
                Fixture f1 = newbody.createFixture(fixtureDef);


            }
        }
        body = newbody;
//            polyVerts.get(i).setFixtures(fixtures);


    }


    private float b2SquaredDistance(float x1, float y1, float x2, float y2) {
        Vector2 vec = new Vector2(x1, y1);
        return vec.dst2(x2, y2);
    }

    protected void setOffset(float x, float y) {
        this.offset.set(x, y);
    }

//    public void addLinearVelocity(Vector2 add) {
//        linearVelocities.add(add);
//    }
//    public void clearLinearVelocities(){
//        linearVelocities.clear();
//    }
//    public Array<Vector2> getLinearVelocities(){
//        return linearVelocities;
//    }
}
