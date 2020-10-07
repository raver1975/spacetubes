package com.klemstinegroup.spacetubes;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		Intent intent = new Intent(
//				WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
//		intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
//				new ComponentName(this, MyWallpaperService.class));
//		startActivity(intent);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new Spacetubes(), config);
	}
}
