package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.quailshillstudio.DestructionData;


/**
 * Created by julienvillegas on 07/12/2017.
 */

public class GroundBoxActor extends UserDataInterface {



    public GroundBoxActor(World aWorld, float x, float y, float width, float height) {
//        super(new Texture("gfx/test02.png"));
        super(width,height);
        int px=2;
        int py=2;
        while(px<width){
            px*=2;
        }
        while(py<height){
            py*=2;
        }
//        px*=2;
//        py*=2;
        Pixmap pixmap = new Pixmap(px,  py, Pixmap.Format.RGB888);
        pixmap.setColor(1, 1, 1, .1f);
        pixmap.fill();
//        pixmap.setColor(1, 0, 0, .1f);
//        pixmap.fillCircle((int)width/2,(int)height/2,(int)Math.min(width/2,height/2));
        final int MAX_COLOR = 6;
        final int MIN_COLOR = 0;
        double jump = (MAX_COLOR-MIN_COLOR) / (px*1.0);
//        Color[] colors = new colors[ARRAY_SIZE];
        for (int i = 0; i < px; i++) {
//            colors[i] = Color.HSVToColor(new float[]{(float) (MIN_COLOR + (jump*i)), 1.0f, 1.0f});
//            colors[i] = Color.HSVToColor(new float[]{(float) (MIN_COLOR + (jump*i)), 1.0f, 1.0f});
            Color colors =HSVtoRGB((float) ((jump * i)), 1.0f, 1.0f);
            pixmap.setColor(colors);
            pixmap.drawLine(i,0,i, py);
        }
        pixmap.setColor(Color.WHITE);
        pixmap.drawLine(0,0,0,py);
        pixmap.drawLine(px-1,0,px-1,py);
        scale=new Vector2(px/width,py/height);
        setTextureRegion(pixmap);
//tr.setRegionWidth((int) width);
//tr.setRegionHeight((int) height);
        this.setSize(width,height);
        this.setPosition(x, y);
        destr = new DestructionData(DestructionData.GROUND);
        world = aWorld;
        BodyDef bd = new BodyDef();
        bd.position.set(x, y);
        bd.type = BodyDef.BodyType.StaticBody;
        body=world.createBody(bd);
        body.setUserData(this);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(width, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.shape = groundBox;
        body.createFixture(fixtureDef);
        this.setOrigin(width/2f , height/2f );
        this.setScale(2,2);
        setCenter(px/2f,py/2f);
        create();
//        createVertex();
    }



    public static Color HSVtoRGB(float h, float s, float v)
    {
        // H is given on [0->6] or -1. S and V are given on [0->1].
        // RGB are each returned on [0->1].
        float m, n, f;
        int i;

        float[] hsv = new float[3];
        float[] rgb = new float[3];

        hsv[0] = h;
        hsv[1] = s;
        hsv[2] = v;

        if (hsv[0] == -1)
        {
            rgb[0] = rgb[1] = rgb[2] = hsv[2];
            return new Color(rgb[0],rgb[1],rgb[2],1f);
        }
        i = (int) (Math.floor(hsv[0]));
        f = hsv[0] - i;
        if (i % 2 == 0)
        {
            f = 1 - f; // if i is even
        }
        m = hsv[2] * (1 - hsv[1]);
        n = hsv[2] * (1 - hsv[1] * f);
        switch (i)
        {
            case 6:
            case 0:
                rgb[0] = hsv[2];
                rgb[1] = n;
                rgb[2] = m;
                break;
            case 1:
                rgb[0] = n;
                rgb[1] = hsv[2];
                rgb[2] = m;
                break;
            case 2:
                rgb[0] = m;
                rgb[1] = hsv[2];
                rgb[2] = n;
                break;
            case 3:
                rgb[0] = m;
                rgb[1] = n;
                rgb[2] = hsv[2];
                break;
            case 4:
                rgb[0] = n;
                rgb[1] = m;
                rgb[2] = hsv[2];
                break;
            case 5:
                rgb[0] = hsv[2];
                rgb[1] = m;
                rgb[2] = n;
                break;
        }

        return new Color(rgb[0],rgb[1],rgb[2],1f);

    }
}
