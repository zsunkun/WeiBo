package com.example.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.api.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * �û���Ϣ������
 * 
 * 
 */
public class UserHandler {
	// User����Ҫ��ѯ���ֶ�
	/*
	 * String[] columns = { DBInfo.Table._ID, DBInfo.Table.USER_ID,
	 * DBInfo.Table.USER_NAME, DBInfo.Table.TOKEN, DBInfo.Table.TOKEN_SECRET,
	 * DBInfo.Table.DESCRIPTION, DBInfo.Table.USER_HEAD };
	 */
	String[] columns = { DBInfo.Table._ID, DBInfo.Table.USER_ID,
			DBInfo.Table.USER_NAME, DBInfo.Table.USER_GENDER,
			DBInfo.Table.DESCRIPTION, DBInfo.Table.USER_HEAD,
			DBInfo.Table.STATUSES_COUNT, DBInfo.Table.FOLLOWERS_COUNT,
			DBInfo.Table.FRIENDS_COUNT };

	DBHelper dbHelper = null;
	SQLiteDatabase db = null;

	public UserHandler(Context context) {
		dbHelper = new DBHelper(context);
	}

	public long insertUser(User user) {
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(DBInfo.Table.USER_TABLE, columns, "user_id ="
				+ user.getUser_id(), null, null, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {

			ContentValues cv = prepareContentValues(user);

			long rowId = db.insert(DBInfo.Table.USER_TABLE,
					DBInfo.Table.USER_NAME, cv);
			db.close();
			return rowId;
		} else {
			if (!db.isOpen()) {
				db.close();
			}
			update(user);
			return -1;
		}

	}

	public int update(User user) {
		db = dbHelper.getReadableDatabase();
		ContentValues cv = prepareContentValues(user);
		int rowId = db.update(DBInfo.Table.USER_TABLE, cv,
				"user_id =" + user.getUser_id(), null);
		return rowId;
	}

	private ContentValues prepareContentValues(User user) {
		ContentValues cv = new ContentValues();
		cv.put(DBInfo.Table.USER_NAME, user.getUser_name());
		cv.put(DBInfo.Table.USER_ID, user.getUser_id());
		cv.put(DBInfo.Table.USER_GENDER, user.getUser_gender());

		/*
		 * cv.put(DBInfo.Table.TOKEN, user.getToken());
		 * cv.put(DBInfo.Table.TOKEN_SECRET, user.getToken_secret());
		 */
		cv.put(DBInfo.Table.DESCRIPTION, user.getDescription());

		cv.put(DBInfo.Table.STATUSES_COUNT, user.getStatuses_count());
		cv.put(DBInfo.Table.FOLLOWERS_COUNT, user.getFollowers_count());
		cv.put(DBInfo.Table.FRIENDS_COUNT, user.getFriends_count());

		// ��ͼƬ���͵����ݽ��д洢��ʱ����Ҫ����ת�����ܴ洢��BLOB������
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// Ϊ��ʵ�����ݴ洢����Ҫ���������ͽ���ת��
		BitmapDrawable newHead = (BitmapDrawable) user.getUser_head();
		// �����ݽ���ѹ����PNG�������ݣ��洢����100%
		newHead.getBitmap().compress(CompressFormat.PNG, 100, os);
		// �洢ͼƬ��������
		cv.put(DBInfo.Table.USER_HEAD, os.toByteArray());
		return cv;
	}

	public int deleteUser(String user_id) {
		return 1;
	}

	public User findUserByUserID(String user_id) {
		db = dbHelper.getReadableDatabase();
		User user = null;

		Cursor cursor = db.query(DBInfo.Table.USER_TABLE, columns, "user_id ="
				+ user_id, null, null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {

			user = new User();
			cursor.moveToFirst();
			// cursor.moveToNext();
			getUserFromDB(user, cursor);
		}
		cursor.close();
		db.close();
		return user;
	}

	public List<User> findAllUsers() {
		db = dbHelper.getReadableDatabase();

		List<User> list_users = null;
		User user = null;

		Cursor cursor = db.query(DBInfo.Table.USER_TABLE, columns, null, null,
				null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {

			Log.i("Userxx", "cursor��Ϊ��");
			list_users = new ArrayList<User>(cursor.getCount());
			while (cursor.moveToNext()) {
				user = new User();

				getUserFromDB(user, cursor);

				list_users.add(user);
			}
		}

		cursor.close();
		db.close();

		return list_users;
	}

	private void getUserFromDB(User user, Cursor cursor) {
		user.setId(cursor.getLong(cursor.getColumnIndex(DBInfo.Table._ID)));
		user.setUser_id(cursor.getString(cursor
				.getColumnIndex(DBInfo.Table.USER_ID)));
		user.setUser_name(cursor.getString(cursor
				.getColumnIndex(DBInfo.Table.USER_NAME)));
		user.setUser_gender(cursor.getString(cursor
				.getColumnIndex(DBInfo.Table.USER_GENDER)));
		/*
		 * user.setToken(cursor.getString(cursor
		 * .getColumnIndex(DBInfo.Table.TOKEN)));
		 * user.setToken_secret(cursor.getString(cursor
		 * .getColumnIndex(DBInfo.Table.TOKEN_SECRET)));
		 */
		user.setDescription(cursor.getString(cursor
				.getColumnIndex(DBInfo.Table.DESCRIPTION)));
		user.setStatuses_count(cursor.getInt(cursor
				.getColumnIndex(DBInfo.Table.STATUSES_COUNT)));
		user.setFollowers_count(cursor.getInt(cursor
				.getColumnIndex(DBInfo.Table.FOLLOWERS_COUNT)));
		user.setFriends_count(cursor.getInt(cursor
				.getColumnIndex(DBInfo.Table.FRIENDS_COUNT)));

		byte[] byteHead = cursor.getBlob(cursor
				.getColumnIndex(DBInfo.Table.USER_HEAD));

		ByteArrayInputStream is = new ByteArrayInputStream(byteHead);
		Drawable userHead = Drawable.createFromStream(is, "srcName");
		user.setUser_head(userHead);
	}
}
