package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.quailshillstudio.UserData;

public class UserDataInterface extends Image {
    public UserData userData = null;
    public Body body;
    public Array<float[]> verts;

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

    public World world;

    public UserDataInterface(Texture texture) {
        super(texture);
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public UserDataInterface(UserData userData) {
        this.userData = userData;
    }


    public Body getBody(){return body;}

    public void setBody(Body body) {this.body=body;
    }

}
