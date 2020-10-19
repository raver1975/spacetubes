package com.klemstinegroup.spacetubes;

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
    public static final float circRadius = 6f;
    public static final int segments = 16;
    public static final EarClippingTriangulator triangulator = new EarClippingTriangulator();

    public DestructionData destr = null;
    public Body body;
    public World world;
    public BodyDef tempBodyDef;
    public Array<FixtureDef> tempFixtureDefs = new Array<>();
//    public Vector2 center = new Vector2();
    //    public Vector2 scale = new Vector2(1, 1);
    public Array<float[]> verts = new Array<>();
    private PixmapTextureData texData;
    private Array<PolygonRegion> pS = new Array<PolygonRegion>();


//    String uuid = UUID.randomUUID().toString();

//    public Vector2 getCenter() {
//        return center;
//    }
//
//    public void setCenter(Vector2 center) {
//        this.center = center.cpy();
//    }
//
//    public void setCenter(float x, float y) {
//        this.center = new Vector2(x, y);
//    }

    public UserDataInterface(Texture texture) {
        super(texture);
        setTextureRegion(extractPixmapFromTextureRegion(new TextureRegion(texture), texture.getWidth(), texture.getHeight()));
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

    public UserDataInterface(float w, float h) {
        super(new Texture(new Pixmap(MathUtils.ceilPositive(w), MathUtils.ceilPositive(h), Pixmap.Format.RGB888)));
    }

    public void setTextureRegion(TextureRegion tr) {
        setDrawable(new TextureRegionDrawable(new TextureRegion(tr)));
        setOrigin(getWidth() / 2, getHeight() / 2);
//        scale = new Vector2(getWidth() / tr.getRegionWidth(), getHeight() / tr.getRegionHeight());
//        center.x = tr.getRegionWidth() / 2f;
//        center.y = tr.getRegionHeight() / 2f;


    }

//    public void setTextureRegion(Texture texture) {
////        tr = texture;
//        setTextureRegion(new TextureRegion(texture));
//    }

    public void setTextureRegion(Pixmap pixmap) {
//        tr = new Texture(pixmap);
        texData = new PixmapTextureData(pixmap, Pixmap.Format.RGBA8888, false, false, true);
        Texture texture = new Texture(texData);
        setTextureRegion(new TextureRegion(texture));
    }

//    public Array<float[]> verts = new Array<>();


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
        bomb.body.applyForceToCenter(new Vector2(0, 40000), true);

        Array<PolygonBox2DShape> shapes = new Array<>();
        for (Vector2 vv : points) {
            if (MathUtils.random() < .2f)
                this.getStage().addActor(new FireEmitter(world, new Vector3(vv.x, vv.y, this.body.getAngle())));
            Vector2 v = new Vector2(vv.x - this.body.getPosition().x, vv.y - this.body.getPosition().y);
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

    public Array<float[]> getVerts() {
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
//        if (MathUtils.random() < .25f) super.draw(batch, parentAlpha);
//        createPolgyonShapes();
        for (PolygonRegion psa : pS) {
//            Gdx.app.log("debug:","Sc:"+scale);
//            psa.setScale(scale.x, scale.y);
//            psa.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
//            psa.setPosition(body.getPosition().x - center.x, body.getPosition().y - center.y);
//            ((PolygonSpriteBatch)batch).draw(psa,body.getPosition().x - center.x,body.getPosition().y - center.y);
            float scx=texData.getWidth()/getWidth();
            float scy=texData.getHeight()/getHeight();
            scx=1f/scx;
            scy=1f/scy;
            scx=1f;
            scy=1f;
            float bx=(body.getPosition().x-psa.getRegion().getRegionX())/scx;
            float by=(body.getPosition().y-psa.getRegion().getRegionY())/scy;
            ((PolygonSpriteBatch) batch).draw(psa, body.getPosition().x-getOriginX(), body.getPosition().y-getOriginY(), getOriginX(), getOriginY(), getWidth(), getHeight(),
                    scx, scy, body.getAngle() * MathUtils.radiansToDegrees);
//            psa.draw((PolygonSpriteBatch) batch);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        this.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        this.setPosition(body.getPosition().x - getOriginX(), body.getPosition().y - getOriginY());
    }


    void create() {
        this.tempBodyDef = Box2DUtils.createDef(body);
        for (Fixture f : body.getFixtureList()) {
            tempFixtureDefs.add(Box2DUtils.createDef(f));
        }
        verts = getVerts();

        createPolgyonShapes();
        this.destr.mustDestroy = true;
    }

    private void createPolgyonShapes() {
        pS.clear();
        for (float[] f : verts) {
            float[] f1 = new float[f.length + 2];
            int u = 0;
            float scx=texData.getWidth()/getWidth();
            float scy=texData.getHeight()/getHeight();
            scx=1f/scx;
            scy=1f/scy;
            Gdx.app.log("debug","scale::"+scx+","+scy);
//            float scx = 1f;
//            float scy = 1f;
            for (u = 0; u < f.length; u += 2) {
                f1[u] = ((f[u]+getOriginX()) / (scx) );// + this.getWidth() / 2f;
                f1[u + 1] = ((f[u + 1]+getOriginY()) / (scy) );// + this.getHeight() / 2f;
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
                float scx=texData.getWidth()/getWidth();
                float scy=texData.getHeight()/getHeight();
//                p.setScale(scx,scy);
                f = p.getTransformedVertices();
                int hh = 0;
//                Array<Fixture> fixtures = new Array<>();
                while (true) {
                    boolean flag = true;
                    for (hh = 0; hh < f.length - 3; hh += 2) {
                        while (b2SquaredDistance(f[hh], f[hh + 1], f[hh + 2], f[hh + 3]) < (0.000025f)) {
                            f[hh + 2] += .00001d;
                            f[hh + 3] += .00001d;
                            flag = false;
                        }
                    }
                    while (b2SquaredDistance(f[hh], f[hh + 1], f[0], f[1]) < (0.000025f)) {
                        f[0] += .00001d;
                        f[1] += .00001d;
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
}
