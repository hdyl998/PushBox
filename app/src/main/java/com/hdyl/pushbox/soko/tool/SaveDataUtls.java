package com.hdyl.pushbox.soko.tool;

import com.alibaba.fastjson.JSON;
import com.hdyl.pushbox.base.MyApplication;
import com.hdyl.pushbox.soko.LevelChooseItem;
import com.hdyl.pushbox.soko.LevelChooseItem.LevelInfo;
import com.hdyl.pushbox.tools.MySharepreferences;
//存数据用到的一具
public class SaveDataUtls {

	final static String GAME_SAVED = "game_save_info";

	public static void saveLevelInfoData(LevelInfo info) {
		MySharepreferences.putString(MyApplication.instance, GAME_SAVED, info.idMD5, JSON.toJSONString(info));
	}

	public static LevelInfo getLevelInfoData(String id) {
		String sss = MySharepreferences.getString(MyApplication.instance, GAME_SAVED, id, null);
		if (sss == null) {
			return null;
		}
		LevelInfo info=JSON.parseObject(sss, LevelInfo.class);
		info.idMD5=id;
		return info;
	}
}
