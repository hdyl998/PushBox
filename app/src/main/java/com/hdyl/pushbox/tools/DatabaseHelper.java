package com.hdyl.pushbox.tools;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hdyl.pushbox.base.ConstData;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "mydata.db"; // 数据库名称
	private static final int version = 2; // 数据库版本
	private static final String TABLE = "level_tb";
	private Context context;

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, version);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TABLE + "(level integer default 0 , levelstring varchar(220) not null,beststep integer default 0,ispass integer default 0);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// SQLiteDatabase db1 = getWritableDatabase();
		// 查之前的数据
		Cursor cursor = db.query(TABLE, new String[] { "level", "levelstring", "beststep" }, null, null, null, null, null);
		List<LevelInfo> list = new ArrayList<LevelInfo>();
		while (cursor.moveToNext()) {
			LevelInfo userInfo = new LevelInfo();
			userInfo.level = cursor.getInt(0);
			userInfo.levelString = cursor.getString(1);
			userInfo.bestStep = cursor.getInt(2);
			list.add(userInfo);
		}
		cursor.close();

		db.execSQL("DROP TABLE IF EXISTS " + TABLE);// 删掉
		onCreate(db);// 重新创表
		// 新插入
		for (int i = 0; i < list.size(); i++) {
			ContentValues cv = new ContentValues();// 实例化ContentValues
			LevelInfo info = list.get(i);
			cv.put("level", info.level);
			cv.put("levelstring", info.levelString);
			cv.put("beststep", info.bestStep);
			cv.put("ispass", 0);
			db.insert(TABLE, null, cv);
		}
		// 更新数据
		int count = ConstData.getCurrentLevel(context);
		for (int i = 1; i < count; i++) {
			ContentValues cv = new ContentValues();// 实例化ContentValues
			cv.put("ispass", 1);
			db.update(TABLE, cv, "level=" + i, null);
//			Log.e("aa", "更新关卡为玩过这关" + i);
		}
		// db.close();
	}

	public LevelInfo selectInfos(int level) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE, new String[] { "level", "levelstring", "beststep", "ispass" }, "level=" + level, null, null, null, null);
		LevelInfo userInfo = null;
		while (cursor.moveToNext()) {
			userInfo = new LevelInfo();
			userInfo.level = cursor.getInt(0);
			userInfo.levelString = cursor.getString(1);
			userInfo.bestStep = cursor.getInt(2);
			userInfo.isPass = cursor.getInt(3) == 1;
			break;
		}
		cursor.close();
		db.close();
		return userInfo;
	}

	public boolean updateInfo(LevelInfo info) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();// 实例化ContentValues
		cv.put("level", info.level);
		cv.put("beststep", info.bestStep);
		cv.put("ispass", info.isPass ? 1 : 0);
		db.update(TABLE, cv, "level=" + info.level, null);
		db.close();
		return true;
	}

	/***
	 * 查询关卡
	 * 
	 * @param page
	 *            0 1-60 page 1 101-200
	 * @return
	 */
	public List<LevelInfo> selectAllInfos(int page) {

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE, new String[] { "level", "levelstring", "beststep", "ispass" }, page == 0 ? "level<=60" : "level>100 and level<=200", null, null, null, null);
		List<LevelInfo> list = new ArrayList<LevelInfo>();
		while (cursor.moveToNext()) {
			LevelInfo userInfo = new LevelInfo();
			userInfo.level = cursor.getInt(0);
			userInfo.levelString = cursor.getString(1);
			userInfo.bestStep = cursor.getInt(2);
			userInfo.isPass = cursor.getInt(3) == 1;
			list.add(userInfo);
		}
		cursor.close();
		db.close();
		return list;
	}

	public boolean insert(LevelInfo info) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();// 实例化ContentValues
		cv.put("level", info.level);
		cv.put("levelstring", info.levelString);
		cv.put("beststep", info.bestStep);
		cv.put("ispass", 0);
		long a = db.insert(TABLE, null, cv);
		db.close();
		return a >= 1;
	}

	public int deleteAll(int jdType) {
		SQLiteDatabase db = getWritableDatabase();
		int len = db.delete(TABLE, null, null);
		db.close();
		return len;
	}

}
