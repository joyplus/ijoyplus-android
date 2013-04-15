package com.joyplus.Video;

import io.vov.vitamio.Vitamio;
import android.app.Activity;
import android.content.Intent;

public final class LibsChecker {
	public static final String FROM_ME = "fromVitamioInitActivity";

	public static final boolean checkVitamioLibs(Activity ctx, int msgID) {
		if ((!Vitamio.isInitialized(ctx)) && (!ctx.getIntent().getBooleanExtra("fromVitamioInitActivity", false))) {
			Intent i = new Intent();
			i.setClassName(ctx.getPackageName(), "com.joyplus.Video.InitActivity");
			i.putExtras(ctx.getIntent());
			i.setData(ctx.getIntent().getData());
			i.putExtra("package", ctx.getPackageName());
			i.putExtra("className", ctx.getClass().getName());
			i.putExtra("EXTRA_MSG", msgID);
			ctx.startActivity(i);
			ctx.finish();
			return false;
		}
		return true;
	}
}
