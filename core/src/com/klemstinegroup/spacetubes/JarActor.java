package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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
//        super();
        Pixmap pixmap = new Pixmap((int) aWidth, (int) aHeight, Pixmap.Format.RGB888);
        pixmap.setColor(1, 0, 1, 1f);
        pixmap.fill();
//        pixmap.setColor(1, 0, 0, 1f);
//        pixmap.fillCircle((int)aWidth/2,(int)aHeight/2,(int)Math.min(aWidth/2,aHeight/2));
//        setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))));
        tr = new TextureRegion(new Texture(pixmap));
        userData = new DestructionData(DestructionData.BOMB);
        this.setSize(aWidth, aHeight);
        this.setPosition(pos_x, pos_y);

        world = aWorld;
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("test.json"));

        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.x = this.getX();
        bd.position.y = this.getY();
        float scale = (this.getWidth() + this.getHeight()) / 2;
        body = world.createBody(bd);


        // 2. Create a FixtureDef, as usual.
        FixtureDef fd = new FixtureDef();
        fd.density = 10f;
        fd.friction = 0f;
        fd.restitution = 0f;

        // 3. Create a Body, as usual.


        loader.attachFixture(body, "test01", fd, scale);
        v_offset = loader.getOrigin("test01", scale);
        System.out.println("loadr x,y:" + v_offset);
        this.setOrigin(v_offset.x, v_offset.y);
        body.setUserData(this);
        createVertex();
    }


}
