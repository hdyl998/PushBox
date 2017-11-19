package com.hdyl.pushbox.soko.tool;

import android.content.Context;
import android.content.Intent;

import com.hdyl.pushbox.soko.LevelChooseItem;
import com.hdyl.pushbox.soko.LevelChooseItem.MyLevel;

public class ShareUtils {

	public static void shareInfo(Context context, MyLevel myLevel) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, LevelChooseItem.getLevelString(myLevel) + "\n\n" + myLevel.getLevelInfo());
		sendIntent.setType("text/plain");
		context.startActivity(Intent.createChooser(sendIntent, "分享关卡 【" + myLevel.ID+"】"));
	}
}
