package com.klemstinegroup.spacetubes;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.quailshillstudio.DestructionData;

/**
 * Created by julienvillegas on 06/12/2017.
 */

public class JarActor extends UserDataInterface {

    private final Vector2 v_offset;
    private final PointLight pl2;
    private World world;

    public JarActor(World aWorld, RayHandler rayHandler,float pos_x, float pos_y, float aWidth, float aHeight) {
        super(aWorld,rayHandler,new Texture("gfx/test01.png"));
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
        setTextureRegion(pixmap);

//        setTextureRegion(new Texture("gfx/test01.png"));
        destr = new DestructionData(DestructionData.GROUND);

        this.setPosition(pos_x, pos_y);
        world = aWorld;
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("test.json"));

        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.x = this.getX();
        bd.position.y = this.getY();
        float scale = this.getWidth();
        body = world.createBody(bd);
        body.setUserData(this);

        // 2. Create a FixtureDef, as usual.
        FixtureDef fd = new FixtureDef();
        fd.density = 1f;
        fd.friction = 1f;
        fd.restitution = .1f;
        fd.filter.groupIndex=1;

        // 3. Create a Body, as usual.


        loader.attachFixture(body, "test01", fd, scale);
        v_offset = loader.getOrigin("test01", scale);
        System.out.println("loadr x,y:" + v_offset);
        this.setOrigin(v_offset.x,v_offset.y);
//        this.setCenter(v_offset.x/2, v_offset.y/2);
//        this.setCenter(this.getWidth()/2f,this.getHeight()/2f);
//        this.setCenter(0, 0);
//        this.setCenter(this.getWidth() / 2, this.getHeight() / 2);
        body.setUserData(this);
//        createVertex();
        pl2 = new PointLight(rayHandler, 65, new Color(1, 0, 0, 1f), 100f, bd.position.x, bd.position.y);
        pl2.attachToBody(body,0,this.getHeight()/2f-5);
        pl2.setSoft(true);
        pl2.setIgnoreAttachedBody(false);
        create();
    }


}
