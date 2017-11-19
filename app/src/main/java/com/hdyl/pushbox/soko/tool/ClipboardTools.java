package com.hdyl.pushbox.soko.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.os.Build;
import android.text.TextUtils;

import com.hdyl.pushbox.tools.LogUtils;
import com.hdyl.pushbox.tools.ToastUtils;

public class ClipboardTools {

	@SuppressLint("NewApi")
	public static void copy2Clipboard(Activity activity, String text) {
		if(TextUtils.isEmpty(text)){
			ToastUtils.makeTextAndShow("复制失败!复制数据为空！");
			return;
		}
		if (Build.VERSION.SDK_INT >= 11) {
			ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Activity.CLIPBOARD_SERVICE);
			LogUtils.Print(text);
			cmb.setText(text);
			ToastUtils.makeTextAndShow("复制成功!");
		} else {
			ToastUtils.makeTextAndShow("复制失败!手机版本太低！");
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static CharSequence getStringFromClipboard(Activity activity) {

		if (Build.VERSION.SDK_INT >= 11) {
			ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Activity.CLIPBOARD_SERVICE);
			return cmb.getText();
		}
		return null;
	}
}
