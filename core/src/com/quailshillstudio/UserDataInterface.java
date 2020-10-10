package com.quailshillstudio;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class UserDataInterface extends Image {
    public UserData userData = null;
    public Body body;

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
