package com.bluebud.data.dao;

import java.util.ArrayList;
import java.util.List;

import com.bluebud.data.SQLiteDDL;
import com.bluebud.info.AlarmClockHistoryInfo;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class AlarmClockHistoryDao {
	private SQLiteDDL ddl;

	public AlarmClockHistoryDao(Context context) {
		ddl = new SQLiteDDL(context);
	}

	public void insert(AlarmClockHistoryInfo alarmClockHistoryInfo) {
		ContentValues values = new ContentValues();
		values.put(AlarmClockHistoryInfo.USER_NAME,
				alarmClockHistoryInfo.user_name);
		values.put(AlarmClockHistoryInfo.TITLE, alarmClockHistoryInfo.title);
		values.put(AlarmClockHistoryInfo.DAY, alarmClockHistoryInfo.day);
		values.put(AlarmClockHistoryInfo.TIME, alarmClockHistoryInfo.time);
		values.put(AlarmClockHistoryInfo.WEEK, alarmClockHistoryInfo.week);
		values.put(AlarmClockHistoryInfo.TYPE, alarmClockHistoryInfo.type);
		values.put(AlarmClockHistoryInfo.STATUS, 0);

		ddl.insert(AlarmClockHistoryInfo.TABLE_NAME, values);
	}

	public List<AlarmClockHistoryInfo> query(String sUserName) {
		if (Utils.isEmpty(sUserName))
			return null;

		String selection = AlarmClockHistoryInfo.USER_NAME + " = ?";
		Cursor mCursor = ddl.query(AlarmClockHistoryInfo.TABLE_NAME, null,
				selection, new String[] { sUserName }, null, null,
				AlarmClockHistoryInfo.ID + " DESC");
		if (mCursor != null) {
			List<AlarmClockHistoryInfo> alarmClockHistoryInfos = new ArrayList<AlarmClockHistoryInfo>();
			while (mCursor.moveToNext()) {
				AlarmClockHistoryInfo alarmClockHistoryInfo = new AlarmClockHistoryInfo();
				alarmClockHistoryInfo.user_name = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockHistoryInfo.USER_NAME));
				alarmClockHistoryInfo.title = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockHistoryInfo.TITLE));
				alarmClockHistoryInfo.day = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockHistoryInfo.DAY));
				alarmClockHistoryInfo.time = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockHistoryInfo.TIME));
				alarmClockHistoryInfo.week = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockHistoryInfo.WEEK));
				alarmClockHistoryInfo.type = mCursor.getInt(mCursor
						.getColumnIndex(AlarmClockHistoryInfo.TYPE));

				alarmClockHistoryInfos.add(alarmClockHistoryInfo);
			}
			mCursor.close();

			updateNoRead(sUserName);

			return alarmClockHistoryInfos;
		} else {
			return null;
		}
	}

	public int queryNoRead(String sUserName) {
		String sqlString = "SELECT * FROM " + AlarmClockHistoryInfo.TABLE_NAME
				+ " WHERE " + AlarmClockHistoryInfo.USER_NAME + " = ? and "
				+ AlarmClockHistoryInfo.STATUS + " = 0";
		Cursor mCursor = ddl.queryBySQL(sqlString, new String[] { sUserName });
		if (null != mCursor) {
			List<AlarmClockHistoryInfo> alarmClockHistoryInfos = new ArrayList<AlarmClockHistoryInfo>();
			while (mCursor.moveToNext()) {
				AlarmClockHistoryInfo alarmClockHistoryInfo = new AlarmClockHistoryInfo();
				alarmClockHistoryInfo.user_name = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockHistoryInfo.USER_NAME));
				alarmClockHistoryInfo.title = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockHistoryInfo.TITLE));
				alarmClockHistoryInfo.day = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockHistoryInfo.DAY));
				alarmClockHistoryInfo.time = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockHistoryInfo.TIME));
				alarmClockHistoryInfo.week = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockHistoryInfo.WEEK));
				alarmClockHistoryInfo.type = mCursor.getInt(mCursor
						.getColumnIndex(AlarmClockHistoryInfo.TYPE));

				alarmClockHistoryInfos.add(alarmClockHistoryInfo);
			}
			LogUtil.i("count:" + alarmClockHistoryInfos.size());
			return alarmClockHistoryInfos.size();
		} else {
			return 0;
		}
	}

	public void updateNoRead(String sUserName) {
		String sqlString = "UPDATE " + AlarmClockHistoryInfo.TABLE_NAME
				+ " SET " + AlarmClockHistoryInfo.STATUS + " = 1 WHERE "
				+ AlarmClockHistoryInfo.USER_NAME + " = ? and "
				+ AlarmClockHistoryInfo.STATUS + " = 0";
		ddl.updateBySQL(sqlString, new String[] { sUserName });
	}

	public int delete(String sUserName) {
		String whereClause = AlarmClockHistoryInfo.USER_NAME + " = ? ";
		return ddl.delete(AlarmClockHistoryInfo.TABLE_NAME, whereClause,
				new String[] { sUserName });
	}

	public void close() {
		ddl.close();
	}
}
