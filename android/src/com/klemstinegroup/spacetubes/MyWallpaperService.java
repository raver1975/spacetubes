package com.klemstinegroup.spacetubes;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaperService;
import com.badlogic.gdx.backends.android.AndroidWallpaperListener;

public class MyWallpaperService extends AndroidLiveWallpaperService{
    public static float pixelOffset = 0;

    @Override
    public void onCreateApplication () {
        super.onCreateApplication();

        final AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
//        config.useGL20 = false;
        config.useCompass = false;
        config.useWakelock = false;
        config.useAccelerometer = false;
        config.getTouchEventsForLiveWallpaper = true;

        final ApplicationListener listener = (ApplicationListener) new WallpaperListener();
        initialize(listener, config);
    }

    public static class WallpaperListener extends Spacetubes implements AndroidWallpaperListener {

        /*
         * never use xOffset/yOffset and xOffsetStep/yOffsetStep, because custom launchers will mess with your 
         * brain and this problem can't be fixed! Use only xPixelOffset/yPixelOffset (who used yPixelOffset???)))
         */

        @Override
        public void offsetChange (float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            pixelOffset = xPixelOffset;
        }

        @Override
        public void previewStateChange (boolean isPreview) {
        }

        @Override
        public void iconDropped(int x, int y) {

        }
    }
}