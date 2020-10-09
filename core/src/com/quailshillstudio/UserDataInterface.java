package com.quailshillstudio;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.quailshillstudio.polygonClippingUtils.UserData;

public class UserDataInterface extends Image {
    public UserData userData = null;

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


}
