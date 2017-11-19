package com.hdyl.pushbox.base;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.TextUtils;

import com.hdyl.pushbox.R;
import com.hdyl.pushbox.setting.SettingConfig;
import com.hdyl.pushbox.tools.LogUtils;
import com.hdyl.pushbox.tools.MySharepreferences;
import com.hdyl.pushbox.tools.ToastUtils;
import com.umeng.analytics.MobclickAgent;

public class ConstData {

	public final static int MAX_LEVEL = 60;
	public final static int MAX_LEVEL2 = 200;
	public static int currentLevel = 1;

	public static boolean isDefaultBitmaps = true;;

	public static Bitmap bitmaps[];
	public static Bitmap bitmapsWall[];

	public static Bitmap[] getBitmaps() {
		return bitmaps;
	}

	static {
		loadBitmapsWithConfig();
		if (bitmaps == null) {
			bitmaps = getDefaultBitmaps();
		}
	}

	public static void loadBitmapsWithConfig() {
		SettingConfig config = SettingConfig.getInstence();
		if (config.skinStyle == 0) {
			if (!ConstData.isDefaultBitmaps) {
				ConstData.setBitmaps(ConstData.getDefaultBitmaps());
			}
		} else {
			if (ConstData.isDefaultBitmaps) {
				Bitmap bitmaps[] = ConstData.getUserSkinFromFile(config.skinPath);
				if (bitmaps != null) {
					ConstData.setBitmaps(bitmaps);
				}
			}
		}
	}

	private static void recycleBitmap(Bitmap[] bitmaps) {
		if (bitmaps != null) {
			for (Bitmap bitmap : bitmaps) {
				if (bitmap != null) {
					bitmap.recycle();
				}
			}
		}
	}

	public static void setBitmaps(Bitmap[] bitmaps) {
		recycleBitmap(ConstData.bitmaps);
		ConstData.bitmaps = bitmaps;
	}

	private static Bitmap[] getWallsSkinFromFile(String stringPath) {
		// 顺序要与下面的getWallBitmap数组保持一致
		String strings[] = { "wall_l", "wall_u", "wall_r", "wall_d", "wall_ul", "wall_lr", "wall_dl", "wall_ur", "wall_ud", "wall_dr", "wall_ulr", "wall_udl", "wall_dlr", "wall_udr", "wall_udlr" };
		Bitmap bitmaps[] = getPathBitmaps(strings, stringPath);
		LogUtils.Print("wall初始化成功" + bitmaps.length);
		return bitmaps;

	}

	/***
	 * 获得需要的图片
	 * 
	 * @param arr
	 * @param row
	 * @param col
	 * @return
	 */
	public static Bitmap getWallBitmap(int[][] arr, int row, int col) {

		if (bitmapsWall == null) {
			return ConstData.bitmaps[1];
		}

		int aa[] = { row, row - 1, row, row + 1 };
		int bb[] = { col - 1, col, col + 1, col };
		// 左，上，右，下
		boolean isBox[] = { false, false, false, false };// 对应 下 ，上，左

		for (int cc = 0; cc < 4; cc++) {
			if (inArray(arr, aa[cc], bb[cc])) {
				if (arr[aa[cc]][bb[cc]] == Consts.ID_WALL) {
					isBox[cc] = true;
				}
			}
		}

		int datas[][] = { { 0 /* 左 */}, { 1 /* 上 */}, { 2 /* 右 */}, { 3 /* 下 */}, { 0, 1 }, { 0, 2 }, { 0, 3 }, { 1, 2 }, { 1, 3 }, { 2, 3 }, { 0, 1, 2 }, { 0, 1, 3 }, { 0, 2, 3 }, { 1, 2, 3 }, { 0, 1, 2, 3 } };

		if (isFalse(isBox)) {// 全假
			LogUtils.Print("获得图片 全假");
			return ConstData.bitmaps[1];
		}
		for (int i = 0; i < datas.length; i++) {
			boolean istrue = isFalse(isBox, datas[i]);
			if (istrue) {
				LogUtils.Print("获得图片" + i);
				return bitmapsWall[i];
			}
		}

		return ConstData.bitmaps[1];
	}

	// 判断index全是真
	private static boolean isFalse(boolean arr[], int... index) {// 1234,12必需是真，34是假

		// 索引为真
		for (int j = 0; j < index.length; j++) {
			if (arr[index[j]] == false) {
				return false;
			}
		}

		// 非索引为假
		for (int i = 0; i < arr.length; i++) {

			if (!contains(index, i)) {
				if (arr[i] == true) {
					return false;
				}
			}
		}
		return true;

	}

	private static boolean contains(int index[], int i) {
		for (int temp : index) {
			if (temp == i) {
				return true;
			}
		}
		return false;
	}

	public static boolean inArray(int[][] arr, int row, int col) {// 数组越界判断，越界为假，正常为真
		if (row >= arr.length || col >= arr[0].length || row < 0 || col < 0)
			return false;
		return true;
	}

	private static Bitmap[] getPathBitmaps(String[] strings, String stringPath) {
		Bitmap[] bitmaps = new Bitmap[strings.length];
		for (int i = 0; i < bitmaps.length; i++) {
			if (strings[i] != null) {
				String pathString = stringPath + "/" + strings[i] + ".png";
				if (!new File(pathString).exists()) {
					ToastUtils.makeTextAndShow("文件不存在" + pathString + "\n加载皮肤失败！");
					return null;
				} else {
					LogUtils.Print(pathString);
					bitmaps[i] = BitmapFactory.decodeFile(pathString);
				}
			}
		}
		return bitmaps;
	}

	public static Bitmap[] getUserSkinFromFile(String stringPath) {
		if (TextUtils.isEmpty(stringPath)) {
			return null;
		}
		String strings[] = { null, "wall", // 1wall
				"ground",// 2way
				"store",// 3point
				"object",// 4box
				"object_store",// 5boxin
				"mover",// 6man
				"mover_store" // 7manin
		};

		// 以下是处理图片，防止无地板背景
		Bitmap bitmaps[] = getPathBitmaps(strings, stringPath);

		Bitmap bitmapGround = bitmaps[Consts.ID_GROUND];

		int width = bitmapGround.getWidth();
		int height = bitmapGround.getHeight();

		RectF rectF = new RectF();
		rectF.left = 0;
		rectF.top = 0;
		rectF.right = width;
		rectF.bottom = height;

		for (int i = Consts.ID_GROUND + 1; i < strings.length; i++) {
			Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(bitmapGround, 0, 0, null);// 画背景
			Bitmap tempBitmap = bitmaps[i];
			canvas.drawBitmap(tempBitmap, null, rectF, null);// 画目标
			tempBitmap.recycle();
			bitmaps[i] = bitmap;
		}
		// 加载墙的图片
		recycleBitmap(bitmapsWall);
		bitmapsWall = getWallsSkinFromFile(stringPath);

		isDefaultBitmaps = false;
		return bitmaps;

	}

	// 获取默认bitmaps
	public static Bitmap[] getDefaultBitmaps() {

		int ids[] = { 0,// 0//black
				R.drawable.p1,// 1wall
				R.drawable.p2,// 2way
				R.drawable.p3,// 3point
				R.drawable.p4,// 4box
				R.drawable.p5,// 5boxin
				R.drawable.p6,// 6man
				R.drawable.p62 // 7manin
		};
		Bitmap[] bitmaps = new Bitmap[ids.length];
		for (int i = 0; i < bitmaps.length; i++) {
			if (ids[i] != 0)
				bitmaps[i] = BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), ids[i]);
		}
		isDefaultBitmaps = true;
		recycleBitmap(bitmapsWall);
		bitmapsWall = null;
		return bitmaps;
	}

	public final static int MAX_NOT_PLAY_STAGE = 5;// 最高不玩关数

	public static void saveCurrentLevel(Context context, int level) {
		currentLevel = level;
		MySharepreferences.putInt(context, "aa", "current_level", currentLevel);
	}

	public static int getCurrentLevel(Context context) {
		int a = MySharepreferences.getInt(context, "aa", "current_level");
		if (a == 0)
			a = 1;
		currentLevel = a;
		return currentLevel;
	}

	public static void showAbout(Context context) {
		MobclickAgent.onEvent(context, "about");
		String aa = context.getResources().getString(R.string.app_name);
		aa = "【" + aa + "】" + context.getResources().getString(R.string.action_settings);
		DialogCreator.create(context, "关于", aa);
	}
}
