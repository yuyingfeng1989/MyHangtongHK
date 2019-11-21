package com.bluebud.data;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteDDL {
	private SQLiteHelper helper;
	private SQLiteDatabase db;
	private Cursor cursor;

	public SQLiteDDL(Context context) {
		helper = new SQLiteHelper(context);
	}

	public long insert(String tableName, ContentValues values) {
		db = helper.getWritableDatabase();
		long rt = db.insert(tableName, null, values);
		return rt;
	}

	public void insertMulti(String tableName, List<ContentValues> lists) {
		db = helper.getWritableDatabase();
		db.beginTransaction();
		for (int i = 0; i < lists.size(); i++) {
			db.insert(tableName, null, lists.get(i));
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public void insertBySQL(String sql) {
		db = helper.getWritableDatabase();
		db.execSQL(sql);
	}

	public void insertBySQL(String sql, String values[]) {
		db = helper.getWritableDatabase();
		db.execSQL(sql, values);
	}

	public int update(String tableName, ContentValues values,
			String whereClause, String[] whereArgs) {
		db = helper.getWritableDatabase();
		int row = db.update(tableName, values, whereClause, whereArgs);
		return row;
	}

	public void updateBySQL(String sql) {
		db = helper.getWritableDatabase();
		db.execSQL(sql);
	}

	public void updateBySQL(String sql, Object bindArgs[]) {
		db = helper.getWritableDatabase();
		db.execSQL(sql, bindArgs);
	}

	public Cursor query(String tableName, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		db = helper.getReadableDatabase();
		cursor = db.query(tableName, columns, selection, selectionArgs,
				groupBy, having, orderBy);
		return cursor;
	}

	public Cursor queryBySQL(String sql, String[] selectionArgs) {
		db = helper.getReadableDatabase();
		cursor = db.rawQuery(sql, selectionArgs);
		return cursor;
	}

	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String orderBy,
			String having, int limit, int pageNo) {
		db = helper.getReadableDatabase();
		cursor = db.query(table, columns, selection, selectionArgs, groupBy,
				null, orderBy, limit * (pageNo - 1) + "," + limit);
		return cursor;
	}

	public int delete(String tableName, String where, String[] whereArgs) {
		db = helper.getWritableDatabase();
		int rt = db.delete(tableName, where, whereArgs);
		return rt;
	}

	public void deleteBySQL(String sql) {
		db = helper.getWritableDatabase();
		db.execSQL(sql);
	}

	public void deleteBySQL(String sql, String[] bindArgs) {
		db = helper.getWritableDatabase();
		db.execSQL(sql, bindArgs);
	}

	public void close() {
		helper.close();
		helper = null;
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		if (db != null) {
			db.close();
			db = null;
		}
	}

}
