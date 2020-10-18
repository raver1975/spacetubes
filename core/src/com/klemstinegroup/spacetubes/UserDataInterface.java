package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;
import com.quailshillstudio.CollisionGeometry;
import com.quailshillstudio.DestructionData;
import com.quailshillstudio.PolygonBox2DShape;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

import java.util.Objects;
import java.util.UUID;

public class UserDataInterface extends Image {
    public static final float circRadius = 6f;
    public static final int segments = 16;

    public DestructionData destr = null;
    public Body body;
    public World world;
    public BodyDef tempBodyDef;
    public Vector2 center = new Vector2();
    public Vector2 scale = new Vector2(1, 1);
    public Array<float[]> verts = new Array<>();
    String uuid = UUID.randomUUID().toString();

    public Vector2 getCenter() {
        return center;
    }

    public void setCenter(Vector2 center) {
        this.center = center.cpy();
    }

    public void setCenter(float x, float y) {
        this.center = new Vector2(x, y);
    }

    public UserDataInterface(Texture texture) {
        super(texture);
        setTextureRegion(texture);
    }

    public UserDataInterface(float w, float h) {
        super(new Texture(new Pixmap(MathUtils.ceilPositive(w), MathUtils.ceilPositive(h), Pixmap.Format.RGB888)));
    }

    public TextureRegion getTextureRegion() {
        return ((TextureRegionDrawable) getDrawable()).getRegion();
    }

    public void setTextureRegion(TextureRegion tr) {
        setDrawable(new TextureRegionDrawable(tr));
        scale = new Vector2(tr.getRegionWidth() / getWidth(), tr.getRegionHeight() / getHeight());
        center.x = tr.getRegionWidth() / 2f;
        center.y = tr.getRegionHeight() / 2f;
    }

    public void setTextureRegion(Texture texture) {
        setTextureRegion(new TextureRegion(texture));
    }

    public void setTextureRegion(Pixmap pixmap) {
        setTextureRegion(new Texture(pixmap));
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

    public UserDataInterface(DestructionData userData) {
        this.destr = userData;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void collide(UserDataInterface bomb, Vector2[] points) {
        tempBodyDef = Box2DUtils.createDef(body);
        Array<float[]> totalRS = new Array<>();
        bomb.body.applyForceToCenter(new Vector2(0, 40000), true);

        Array<PolygonBox2DShape> shapes =new Array<>();
        for (Vector2 vv : points) {
            Vector2 v = new Vector2(vv.x - this.body.getPosition().x - circRadius / 2f, vv.y - this.body.getPosition().y - circRadius / 2f);
            v.rotateRad(-this.body.getAngle());
            float[] circVerts = CollisionGeometry.approxCircle(v.x, v.y, circRadius, segments);
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
            Array<PolygonBox2DShape> rstot=new Array<>();
            rstot.add(polyClip);
            for (PolygonBox2DShape po:shapes){
                for(PolygonBox2DShape p2:rstot){
                    Array<PolygonBox2DShape> rs = p2.differenceCS(po);
                    rstot.removeValue(p2,true);
                    rstot.addAll(rs);
                }

            }
            for (int y = 0; y < rstot.size; y++) {
                for (int e=0;e<shapes.size;e++) {
                    rstot.get(y).circleContact(points[e].cpy().sub(this.body.getPosition()), circRadius);
                }
                totalRS.add(rstot.get(y).vertices());
            }
        }
        verts = totalRS;
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
//        if (tr == null) {
        //super.draw(batch, parentAlpha);
//        if (true) return;
//        }
//        else
//        if (verts != null) {
//        if (verts==null||verts.size==0){
//            verts=getVerts();

//        }

        for (float[] f : verts) {
//                if (f!=null)sd.filledPolygon(f);
            float[] f1 = new float[f.length + 2];
            int u = 0;

            for (u = 0; u < f.length; u += 2) {
                f1[u] = (f[u] / (scale.x) + center.x);// + this.getWidth() / 2f;
                f1[u + 1] = (f[u + 1] / (scale.y) + center.y);// + this.getHeight() / 2f;
            }
            f1[u++] = f1[0];
            f1[u++] = f1[1];
            EarClippingTriangulator triangulator = new EarClippingTriangulator();
            ShortArray triangleIndices = triangulator.computeTriangles(f1);
            PolygonRegion pR = new PolygonRegion(((TextureRegionDrawable) this.getDrawable()).getRegion(), f1, triangleIndices.toArray());
            pR.getRegion().getTexture().setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
            PolygonSprite pS = new PolygonSprite(pR);
            pS.setScale(scale.x, scale.y);
            pS.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
            pS.setPosition(body.getPosition().x - center.x, body.getPosition().y - center.y);
            pS.draw((PolygonSpriteBatch) batch);
        }
//        }


    }

    @Override
    public void act(float delta) {
        super.act(delta);
        this.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        this.setPosition(body.getPosition().x - getOriginX(), body.getPosition().y - getOriginY());

//        new FireEmitter(world);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDataInterface)) return false;
        UserDataInterface that = (UserDataInterface) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    void create() {
        this.tempBodyDef = Box2DUtils.createDef(body);
        verts = getVerts();
        this.destr.mustDestroy = true;
    }

    protected void createGround() {

        //Iterator<BodyDef> uditbd=polyVertsBodyDef.iterator();
        System.out.println("creating polyvert:" + getClass().toString());
//            System.out.println(groundDef.toString());
//            groundDef.type = ud.body.getType();
//            groundDef.active = true;
        this.destr.mustDestroy = false;
        this.destr.destroyed = false;
        body = world.createBody(tempBodyDef);
        body.setUserData(this);
//            Array<Fixture> fixtures = new Array<>();
//            ud.verts=ud.getVerts();
//        verts=getVerts();
        for (int y = 0; y < verts.size; y++) {
            System.out.println("verts size" + verts.get(y).length);
            if (verts.get(y).length >= 6) {
                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.density = 10f;//ud.body.getFixtureList().get(0).getDensity();
                fixtureDef.friction = .50f;//ud.body.getFixtureList().get(0).getFriction();
                fixtureDef.restitution = 1f;//ud.body.getFixtureList().get(0).getRestitution();
                ChainShape shape = new ChainShape();
                float[] f = verts.get(y);
                Polygon p = new Polygon(f);
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
                fixtureDef.shape = shape;
                body.createFixture(fixtureDef);
            }
        }
//            polyVerts.get(i).setFixtures(fixtures);

    }


    private float b2SquaredDistance(float x1, float y1, float x2, float y2) {
        Vector2 vec = new Vector2(x1, y1);
        return vec.dst2(x2, y2);
    }
}
