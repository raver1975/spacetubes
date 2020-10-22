package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.utils.Array;

public class GravityHandler {
    Array<DataAndForce> gravity = new Array<>();

    public GravityHandler() {
    }

    public void add(DataAndForce daf) {
        gravity.add(daf);
    }

    public Array<DataAndForce> getGravity() {
        return gravity;
    }

    static class DataAndForce {
        UserDataInterface ud;
        float force = 0;

        public DataAndForce(UserDataInterface ud, float force) {
            this.ud = ud;
            this.force = force;
        }
    }
}
