package com.hdyl.pushbox.tools;

import android.content.Context;

/**
 * sharepre缓存工具
 * 
 * @author liugd
 * 
 */
public class ShareCacheUtil {
	public final static String DEFAULT_FILE_NAME = "cache";

	/**
	 * 放入缓存
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putString(Context context, String key, String value) {
		MySharepreferences.putString(context, DEFAULT_FILE_NAME, key, value);
	}

	/**
	 * 得到缓存
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static String getString(Context context, String key) {
		return MySharepreferences.getString(context, DEFAULT_FILE_NAME, key, null);
	}

}
