package com.hdyl.pushbox.db;

/**
 * Created by liugd on 2016/12/23.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 比分数据库
 * 
 * @author liugd
 */
public class DBHelper<T> extends SQLiteOpenHelper {
	private final static int DATABASE_VERSION = 1; // 数据库版本
	private final static String DATABASE_NAME = "mydata.db";// 数据库名
	private final String TABLE_NAME;// 表名
	private DBManagerHelper managerHelper;
	private Class<T> clazz;

	public DBHelper(Context context, Class<T> clazz) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		TABLE_NAME = clazz.getSimpleName();
		this.clazz = clazz;
		// 创建一个助手对象
		managerHelper = new DBManagerHelper(clazz);
	}

	public static <T> DBHelper<T> createDbHelper(Context context, Class<T> clazz) {
		return new DBHelper<T>(context, clazz);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {// 创建表
		managerHelper.onCreate(db, TABLE_NAME);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);// 重新创建表
	}

	// /////////////////////插入数据////////////////////////

	/****
	 * 更新数据
	 * 
	 * @param item
	 *            更新的实体对象
	 */
	public synchronized void update(T item, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction(); // 手动设置开始事务
		int i = db.update(TABLE_NAME, managerHelper.object2ContentValues(item), whereClause, whereArgs);
		db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
		db.endTransaction(); // 处理完成
		db.close();
		// LogUitls.PrintObject("数据库", "更新单条数据" + (i == 1));
	}

	/***
	 * 插入所有数据
	 * 
	 * @param list
	 *            需要插入的数据
	 */
	public synchronized void insert(T item) {
		long count = 0;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.beginTransaction(); // 手动设置开始事务
			count = db.insert(TABLE_NAME, null, managerHelper.object2ContentValues(item));
			db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); // 处理完成
			db.close();
		} catch (Exception e) {
			// LogUitls.PrintObject("数据库", e + "");
		}
		// LogUitls.PrintObject("数据库", "插入所有数据" + count);// 插入多少条数据
	}

	/***
	 * 插入所有数据
	 * 
	 * @param list
	 *            需要插入的数据
	 */
	public synchronized void insert(List<T> list) {
		int count = 0;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.beginTransaction(); // 手动设置开始事务
			for (Object item : list) {// 把数据复制一到一个新的集合里面
				count += db.insert(TABLE_NAME, null, managerHelper.object2ContentValues(item));
			}
			db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); // 处理完成
			db.close();
		} catch (Exception e) {
			// LogUitls.PrintObject("数据库", e + "");
		}
		// LogUitls.PrintObject("数据库", "插入所有数据" + count);// 插入多少条数据
	}

	// //////////////////////////////////////////////////////////////
	// ////////////////////////删除数据////////////////////////////////
	// /////////////////////////////////////////////////////////////

	/**
	 * 删除指定表的所有数据
	 * 
	 * @return
	 */
	public synchronized boolean delete(String whereClause, String[] whereArgs) {
		// LogUitls.PrintObject("数据库", " 删除所有数据");
		int num = 0;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.beginTransaction(); // 手动设置开始事务
			num = db.delete(TABLE_NAME, whereClause, whereArgs);
			db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); // 处理完成
			db.close();
		} catch (Exception e) {
			// LogUitls.PrintObject("数据库", e + "");
			System.out.println(e + "");
		}
		return num >= 1;
	}

	// //////////////////////////////////////////////////////////////
	// ////////////////////////查询数据////////////////////////////////
	// /////////////////////////////////////////////////////////////

	public List<T> querry() {
		return querry(null, null, null, null, null);
	}

	public List<T> querry(String selection, String[] selectioinArgs) {
		return querry(selection, selectioinArgs, null, null, null);
	}

	public List<T> querry(String selection, String[] selectioinArgs, String orderBy) {
		return querry(selection, selectioinArgs, null, null, orderBy);
	}

	/***
	 * 查询数据
	 * 
	 * @return
	 */
	public synchronized List<T> querry(String selection, String[] selectioinArgs, String groupBy, String having, String orderBy) {
		List<T> list = new ArrayList<T>();
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.beginTransaction(); // 手动设置开始事务
			Cursor cursor = db.query(TABLE_NAME, managerHelper.getStringFields(), selection, selectioinArgs, groupBy, having, orderBy);
			while (cursor.moveToNext()) {
				T ball = clazz.newInstance();
				managerHelper.setObjectData(ball, cursor);
				list.add(ball);
			}
			cursor.close();
			db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); // 处理完成
			db.close();
		} catch (Exception e) {
			// LogUitls.PrintObject("数据库", e + "");
		}
		return list;
	}

}
