package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;
import com.quailshillstudio.DestructionData;
import com.quailshillstudio.PolygonBox2DShape;

public class UserDataInterface extends Image {
    public DestructionData userData = null;
    public Body body;
    public World world;
    public TextureRegion tr;

    public UserDataInterface() {
        super(new Texture(new Pixmap((int) 1, (int) 1, Pixmap.Format.RGB888)));
    }

    public TextureRegion getTr() {
        return tr;
    }

    public void setTr(TextureRegion tr) {
        this.tr = tr;
    }

    public Array<float[]> verts = new Array<>();

    public Array<float[]> getVerts() {
        return verts;
    }

    public void setVerts(Array<float[]> verts) {
        this.verts = verts;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public UserDataInterface(Texture texture) {
        super(texture);
    }

    public DestructionData getUserData() {
        return userData;
    }

    public void setUserData(DestructionData userData) {
        this.userData = userData;
    }

    public UserDataInterface(DestructionData userData) {
        this.userData = userData;
    }

    public Body getBody() {
        return body;
    }

    public void createVertex() {
        Array<Fixture> fixtureList = body.getFixtureList();
        int fixCount = fixtureList.size;
        for (int i = 0; i < fixCount; i++) {
            PolygonBox2DShape polyClip = null;
//            if (fixtureList.get(i).getShape() instanceof PolygonShape) {
            try {
                polyClip = new PolygonBox2DShape((PolygonShape) fixtureList.get(i).getShape());
            } catch (Exception e) {
                polyClip = new PolygonBox2DShape((ChainShape) fixtureList.get(i).getShape());
            }
//            } else if (fixtureList.get(i).getShape() instanceof ChainShape) {
//            }
            Array<PolygonBox2DShape> rs = polyClip.clipCS(polyClip, false, true);
            for (int y = 0; y < rs.size; y++) {
                verts.add(rs.get(y).vertices());
            }
        }
    }

    public void setBody(Body body) {
        this.body = body;

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (tr==null) {
            super.draw(batch, parentAlpha);
        } else if (verts != null) {
            for (float[] f : verts) {
//                if (f!=null)sd.filledPolygon(f);
                float[] f1 = new float[f.length + 2];
                int u = 0;
                for (u = 0; u < f.length; u += 2) {
                    f1[u] = f[u] + this.getWidth() / 2f;
                    f1[u + 1] = f[u + 1] + this.getHeight() / 2f;
                }
                f1[u++] = f1[0];
                f1[u++] = f1[1];
                EarClippingTriangulator triangulator = new EarClippingTriangulator();
                ShortArray triangleIndices = triangulator.computeTriangles(f1);
                PolygonRegion pR = new PolygonRegion(tr, f1, triangleIndices.toArray());
                pR.getRegion().getTexture().setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.ClampToEdge);
                PolygonSprite pS = new PolygonSprite(pR);

//                pS.scale(1);
//                pS.setOrigin(this.getWidth()*this.getScaleX()/2f, this.getHeight()*this.getScaleY()/2f);
//                pS.translate(pS.getWidth()/2, pS.getHeight()/2);
//                pS.translate(pS.getHeight());
                pS.setPosition(body.getPosition().x - this.getWidth() / 2f, body.getPosition().y - this.getHeight() / 2f);
                pS.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
                pS.draw((PolygonSpriteBatch) batch);

            }
        }


    }

    @Override
    public void act(float delta) {
        super.act(delta);
        this.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        this.setPosition(body.getPosition().x - this.getWidth() / 2, body.getPosition().y - this.getHeight() / 2);
//        new FireEmitter(world);
    }

}
