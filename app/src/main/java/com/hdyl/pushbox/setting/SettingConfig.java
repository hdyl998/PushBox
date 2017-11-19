package com.hdyl.pushbox.setting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.hdyl.pushbox.R;
import com.hdyl.pushbox.base.ConstData;
import com.hdyl.pushbox.base.MyApplication;
import com.hdyl.pushbox.tools.LogUtils;
import com.hdyl.pushbox.tools.MySharepreferences;
import com.hdyl.pushbox.tools.ToastUtils;

public class SettingConfig {

	public int sleepTime;// 单位毫秒
	public int progressVar;

	public int bgStyle = 0;// 背景样式

	public String userImg;
	public boolean isPosition;// 是否显示坐标
	public boolean isSkinCover;//皮肤是否覆盖

	public static SettingConfig instence;

	public int skinStyle = 0;// 皮肤样式

	public String skinPath;// 皮肤路径

	public int colorRed;
	public int colorBlue;
	public int colorGreen;

	public static SettingConfig getInstence() {
		if (instence == null) {
			return instence = getSettingConfig();
		}
		return instence;
	}

	final static String fileName = "setting";
	final static String fileKey = "datas";

	public static void saveConfig() {
		MySharepreferences.putString(MyApplication.getInstance(), fileName, fileKey, JSON.toJSONString(getInstence()));
	}

	public SettingConfig() {
		setPgVar(9);// 默认值 是9,
	}

	public static SettingConfig getSettingConfig() {
		String sssString = MySharepreferences.getString(MyApplication.getInstance(), fileName, fileKey);
		if (sssString != null) {
			try {
				SettingConfig config = JSON.parseObject(sssString, SettingConfig.class);
				return config;
			} catch (Exception e) {
			}
		}
		return new SettingConfig();
	}

	public String getSkinName() {
		String ss[] = { "经典皮肤", "自定义皮肤" };
		return ss[skinStyle];
	}

	// 交换皮肤
	public void exchangeSkin() {
		skinStyle++;
		skinStyle = skinStyle % SKIN_INT;
		ConstData.loadBitmapsWithConfig();
	}

	public String getBgName() {
		String sss[] = { "黯淡忧伤", "砖石迷阵", "自定义图片背景", "自定义颜色背景" };
		return sss[bgStyle];
	}

	public int getBgResource() {
		int res[] = { R.drawable.ic_bg2, R.drawable.ic_bg3, 0, 0 };
		return res[bgStyle];
	}

	public int getBgStyle() {
		return bgStyle;
	}

	public int getSkinStyle() {
		return skinStyle;
	}

	public String getSkinPath() {
		return skinPath;
	}

	public void setSkinPath(String skinPath) {
		this.skinPath = skinPath;
	}

	public Bitmap getSkinExampleBitmap() {
		if (skinPath == null) {
			return null;
		}
		return ConstData.bitmaps[4];
	}

	public void setSkinStyle(int skinStyle) {
		this.skinStyle = skinStyle;
	}

	public void setBgStyle(int bgStyle) {
		this.bgStyle = bgStyle;
	}

	final static int BG_INT = 4;
	final static int SKIN_INT = 2;

	// 交换主题
	public void exchangeTheme() {
		bgStyle++;
		bgStyle = bgStyle % BG_INT;
	}

	public void setPgVar(int progressVar) {
		this.progressVar = progressVar;
		progressVar = progressVar + 1;// 1秒多少步,5-20步
		sleepTime = 1000 / progressVar;
	}

	public void exchangePosition() {
		isPosition = !isPosition;
	}

	public void exchangeCover(){
		isSkinCover=!isSkinCover;
	}
	
	public String getUserImg() {
		return userImg;
	}

	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}

	public int getBgColor() {
		return Color.argb(255, this.colorRed, this.colorGreen, this.colorBlue);
	}

	// 设置view的类型
	public void setBgWithType(View view) {
		switch (this.getBgStyle()) {
		case 0:
		case 1:
			view.setBackgroundResource(this.getBgResource());
			break;
		case 2:// 图片背景
			Bitmap bm = BitmapFactory.decodeFile(this.getUserImg());
			BitmapDrawable bd = new BitmapDrawable(bm);
			view.setBackgroundDrawable(bd);
			break;
		case 3:
			view.setBackgroundDrawable(new ColorDrawable(this.getBgColor()));
			break;
		}
	}

	public int getProgressVar() {
		return progressVar;
	}
}
