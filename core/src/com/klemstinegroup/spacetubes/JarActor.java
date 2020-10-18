package com.klemstinegroup.spacetubes;

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
    private World world;

    public JarActor(World aWorld, float pos_x, float pos_y, float aWidth, float aHeight) {
        super(new Texture("gfx/test01.png"));
//        this.setTextureRegion(extractPixmapFromTextureRegion(new TextureRegion(new Texture("gfx/test01.png")), aWidth, aHeight));
        Pixmap pixmap = new Pixmap((int) aWidth*10, (int) aHeight*10, Pixmap.Format.RGB888);
        final int MAX_COLOR = 6;
        final int MIN_COLOR = 0;
        double jump = (MAX_COLOR - MIN_COLOR) / (aWidth * 10.0);
        for (int i = 0; i < aWidth*10; i++) {
            Color colors = GroundBoxActor.HSVtoRGB((float) ((jump * i)), 1.0f, 1.0f);
            pixmap.setColor(colors);
            pixmap.drawLine(i, 0, i, (int) aHeight*10);
        }
        this.setSize(aWidth, aHeight);
//        pixmap.drawPixmap(extractPixmapFromTextureRegion(new TextureRegion(new Texture("gfx/test01.png")), aWidth*10, aHeight*10),0,0);
        setTextureRegion(pixmap);
//        setTextureRegion(new Texture("gfx/test01.png"));
        destr = new DestructionData(DestructionData.BOMB);

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
        create();
    }


}
