package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ShortArray;
import com.quailshillstudio.UserData;
import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * Created by julienvillegas on 07/12/2017.
 */

public class GroundBoxActor extends UserDataInterface {

    private final ShapeDrawer sd;
    private final TextureRegion tr;

    public GroundBoxActor(ShapeDrawer sd, World aWorld, float x, float y, float width, float height) {
//        super(new Texture("gfx/test02.png"));
        super(new Texture(new Pixmap((int) width, (int) height, Pixmap.Format.RGB888)));
        this.sd=sd;
        Pixmap pixmap = new Pixmap((int) width, (int) height, Pixmap.Format.RGB888);
        pixmap.setColor(1, 1, 1, 0f);
        pixmap.fill();
//        setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))));
        tr = new TextureRegion(new Texture(pixmap));
        this.setSize(width,height);
        this.setPosition(x, y);
        userData = new UserData(UserData.GROUND);
        world = aWorld;
        BodyDef bd = new BodyDef();
        bd.position.set(x, y);
        bd.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bd);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(width, height);
        body.setUserData(this);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.shape = groundBox;
        body.createFixture(fixtureDef);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (sd!=null &&verts!=null) {
            for (float[] f : verts) {
//                if (f!=null)sd.filledPolygon(f);
                float[] f1 = new float[f.length + 2];
                int u=0;
                for (u=0;u<f.length;u++){
                    f1[u]=f[u];
                }
                f1[u++]=f[0];
                f1[u++]=f[1];
                EarClippingTriangulator triangulator = new EarClippingTriangulator();
                ShortArray triangleIndices = triangulator.computeTriangles(f);

                PolygonRegion pR = new PolygonRegion(tr,f,triangleIndices.toArray());
                PolygonSprite pS = new PolygonSprite(pR);
//                pS.scale(1);
                pS.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
                pS.setPosition(body.getPosition().x, body.getPosition().y );
                pS.draw((PolygonSpriteBatch) batch);
            }
        }
//        super.draw(batch, parentAlpha);

    }
    @Override
    public void act(float delta) {
        super.act(delta);
//        this.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
//        this.setPosition(body.getPosition().x-this.getWidth()/2 , body.getPosition().y -this.getHeight()/2);
    }
}
