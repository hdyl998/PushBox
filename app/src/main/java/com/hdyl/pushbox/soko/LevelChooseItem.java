package com.hdyl.pushbox.soko;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.R.string;
import android.text.TextUtils;

import com.hdyl.pushbox.md5.Encryp;
import com.hdyl.pushbox.tools.Tools;

public class LevelChooseItem {

	public String title;

	public String auther;
	public String email;
	public List<MyLevel> listLev = new ArrayList<MyLevel>();
	public String url;
	public String description;

	public String getLevelsString() {
		String sss = "作者：" + notNull(this.auther) + "\n邮箱：" + notNull(this.email) + "\n描述：" + notNull(this.description);
		return sss;
	}

	// 算出来的
	public int levelCount;
	public int levelFished;

	public String getFinishedString() {
		levelFished = 0;
		for (MyLevel myLevel : listLev) {
			if (myLevel.isFinished) {
				levelFished++;
			}
		}
		return levelFished + "/" + listLev.size();
	}

	public static class MyLevel implements Serializable {
		public String ID;
		public List<String> levDatas = new ArrayList<String>();
		public String author;// 作者
		public String comment;
		public boolean isFinished;// 是否完成了
		public String passDate;// 通关时间
		public boolean isSolution;

		public String getLevelInfo() {
			return "关卡名：" + notNull(this.ID) + "\n作者：" + notNull(this.author) + "\n描述：" + notNull(this.comment);
		}
	}

	public static String notNull(String sss) {
		if (TextUtils.isEmpty(sss)) {
			return "暂无数据";
		}
		return sss;
	}

	public static class LevelInfo implements Serializable {
		public int bestStep;// 最佳步数
		public String solution;// 通关步数字
		public String idMD5;// MD5 ID 根据 关卡数据获得的
		public String passDate;// 通关时间
		public boolean isFinished=false;

		// 设置最新的解法，返回真表示需要存档
		public boolean setSolution(String sss) {

			// 之前没解法，或现在的解法最优
			if (TextUtils.isEmpty(solution) || solution.length() > sss.length()) {
				solution = sss;
				bestStep = sss.length();
				passDate = Tools.getNowTimeString();
				return true;
			}
			// 通关时间之前为空
			if (TextUtils.isEmpty(passDate)) {
				passDate = Tools.getNowTimeString();
				return true;
			}
			return false;
		}
	}

	// 获得levelString,用于md5的计算，及复制关卡
	public static String getLevelString(MyLevel levelInfo) {
		List<String> listStrings = levelInfo.levDatas;
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String ss : listStrings) {
			if (i != 0) {
				sb.append("\n");
			}
			i++;
			sb.append(ss);
		}
		return sb.substring(0);
	}

	// 计算MD5 ID
	public static String getLevelMD5(MyLevel levelInfo) {
		String sssString = LevelChooseItem.getLevelString(levelInfo);
		return Encryp.getInstance().md5(sssString);
	}
}
