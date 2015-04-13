package com.example.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	/**
	 * @param context
	 *            静态上下文
	 */
	public DBHelper(Context context) {
		this(context, DBInfo.DB.DB_NAME, null, DBInfo.DB.VERSION);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            上下文
	 * @param name
	 *            数据库名字
	 * @param factory
	 *            游标工厂
	 * @param version
	 *            数据库版本
	 */

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DBInfo.Table.CREATE_USER_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DBInfo.Table.DROP_USER_TABLE);
		onCreate(db);
	}

}
