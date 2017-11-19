package com.hdyl.pushbox.tools;

import com.alibaba.fastjson.JSON;

import android.util.Log;

public class LogUtils {

	static boolean isDebug = true;

	public static void Print(Object str) {
		if (isDebug) {
			Print("lgdx", str);
		}
	}

	public static void Print(String tag, Object str) {
		if (isDebug) {
			if (str instanceof String) {
				Log.e(tag, str + "");
			} else {
				Log.e(tag, JSON.toJSONString(str));
			}
		}
	}
}
