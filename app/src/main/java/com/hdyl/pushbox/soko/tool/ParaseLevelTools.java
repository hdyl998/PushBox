package com.hdyl.pushbox.soko.tool;

import java.io.InputStream;

import com.alibaba.fastjson.JSON;
import com.hdyl.pushbox.paraser.ParserManager;
import com.hdyl.pushbox.soko.LevelChooseItem;

public class ParaseLevelTools {

	public static String paraseFile2String(String otherName, String keyString, InputStream inputStream) throws Exception {
		LevelChooseItem object = new ParserManager(keyString).parse2Item(inputStream);
		if (object != null && object.title == null) {
			object.title = otherName;
		}
		inputStream.close();
		if (object == null) {
			return null;
		}
		return JSON.toJSONString(object);
	}

	public final static String exts = "slc,xsb,lp0,sok,box,txt";

	public static boolean isFileExtRight(String fileName, String[] arrExts) {
		String sssString = fileName.toLowerCase();

		for (String temp : arrExts) {
			if (sssString.endsWith(temp)) {
				return true;
			}
		}
		return false;
	}

	// 获取所支持文件格式
	public static String getSupportExtString(String exts) {
		StringBuilder sb = new StringBuilder();
		boolean isFist = true;
		String[] arrString = exts.split(",");
		for (String ss : arrString) {
			if (isFist) {
				isFist = false;
			} else {
				sb.append("|");
			}
			sb.append("*" + ss);
		}
		return sb.substring(0);
	}

}
