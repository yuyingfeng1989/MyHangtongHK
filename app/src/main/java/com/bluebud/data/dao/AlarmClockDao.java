package com.bluebud.data.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bluebud.data.SQLiteDDL;
import com.bluebud.info.AlarmClockInfo;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class AlarmClockDao {
	private SQLiteDDL ddl;

	public AlarmClockDao(Context context) {
		ddl = new SQLiteDDL(context);
	}

	public void insert(AlarmClockInfo alarmClockInfo) {
		ContentValues values = new ContentValues();
		values.put(AlarmClockInfo.CLOCK_ID, alarmClockInfo.id);
		values.put(AlarmClockInfo.USER_NAME, alarmClockInfo.sUserName);
		values.put(AlarmClockInfo.TITLE, alarmClockInfo.title);
		values.put(AlarmClockInfo.DAY, alarmClockInfo.sDay);
		values.put(AlarmClockInfo.TYPE, alarmClockInfo.iType);

		StringBuffer sbTtime = new StringBuffer();
		for (int i = 0; i < alarmClockInfo.times.size(); i++) {
			sbTtime.append(alarmClockInfo.times.get(i));
			if (i != alarmClockInfo.times.size() - 1) {
				sbTtime.append(",");
			}
		}
		values.put(AlarmClockInfo.TIME, sbTtime.toString());
		
		if (alarmClockInfo.iType == 1) {
			if (alarmClockInfo.isEnd) {
				values.put(AlarmClockInfo.LAST_DAY, 1);
			} else {
				values.put(AlarmClockInfo.LAST_DAY, 0);
			}
		}

		if (alarmClockInfo.iType == 2) {
			StringBuffer sbWeek = new StringBuffer();
			for (int i = 0; i < alarmClockInfo.arrWeeks.length; i++) {
				sbWeek.append(alarmClockInfo.arrWeeks[i]);
				if (i != alarmClockInfo.arrWeeks.length - 1) {
					sbWeek.append(",");
				}
			}
			LogUtil.i(sbWeek.toString());
			values.put(AlarmClockInfo.WEEK, sbWeek.toString());
		}
		ddl.insert(AlarmClockInfo.TABLE_NAME, values);
	}

	public void insert(List<AlarmClockInfo> alarmClockInfos) {
		delete(alarmClockInfos.get(0).sUserName);

		List<ContentValues> lists = new ArrayList<ContentValues>();
		for (AlarmClockInfo alarmClockInfo : alarmClockInfos) {
			ContentValues values = new ContentValues();
			values.put(AlarmClockInfo.CLOCK_ID, alarmClockInfo.id);
			values.put(AlarmClockInfo.USER_NAME, alarmClockInfo.sUserName);
			values.put(AlarmClockInfo.TITLE, alarmClockInfo.title);
			values.put(AlarmClockInfo.DAY, alarmClockInfo.sDay);
			values.put(AlarmClockInfo.TYPE, alarmClockInfo.iType);

			StringBuffer sbTtime = new StringBuffer();
			for (int i = 0; i < alarmClockInfo.times.size(); i++) {
				sbTtime.append(alarmClockInfo.times.get(i));
				if (i != alarmClockInfo.times.size() - 1) {
					sbTtime.append(",");
				}
			}
			values.put(AlarmClockInfo.TIME, sbTtime.toString());

			if (alarmClockInfo.iType == 1) {
				if (alarmClockInfo.isEnd) {
					values.put(AlarmClockInfo.LAST_DAY, 1);
				} else {
					values.put(AlarmClockInfo.LAST_DAY, 0);
				}
			}

			if (alarmClockInfo.iType == 2) {
				StringBuffer sbWeek = new StringBuffer();
				for (int i = 0; i < alarmClockInfo.arrWeeks.length; i++) {
					sbWeek.append(alarmClockInfo.arrWeeks[i]);
					if (i != alarmClockInfo.arrWeeks.length - 1) {
						sbWeek.append(",");
					}
				}
				LogUtil.i(sbWeek.toString());
				values.put(AlarmClockInfo.WEEK, sbWeek.toString());
			}
			lists.add(values);
		}

		ddl.insertMulti(AlarmClockInfo.TABLE_NAME, lists);
	}

	public void update(AlarmClockInfo alarmClockInfo) {
		ContentValues values = new ContentValues();
		values.put(AlarmClockInfo.USER_NAME, alarmClockInfo.sUserName);
		values.put(AlarmClockInfo.TITLE, alarmClockInfo.title);
		values.put(AlarmClockInfo.DAY, alarmClockInfo.sDay);
		values.put(AlarmClockInfo.TYPE, alarmClockInfo.iType);

		StringBuffer sbTtime = new StringBuffer();
		for (int i = 0; i < alarmClockInfo.times.size(); i++) {
			sbTtime.append(alarmClockInfo.times.get(i));
			if (i != alarmClockInfo.times.size() - 1) {
				sbTtime.append(",");
			}
		}
		values.put(AlarmClockInfo.TIME, sbTtime.toString());

		if (alarmClockInfo.iType == 2) {
			StringBuffer sbWeek = new StringBuffer();
			for (int i = 0; i < alarmClockInfo.arrWeeks.length; i++) {
				sbWeek.append(alarmClockInfo.arrWeeks[i]);
				if (i != alarmClockInfo.arrWeeks.length - 1) {
					sbWeek.append(",");
				}
			}
			values.put(AlarmClockInfo.WEEK, sbWeek.toString());
		}

		String selection = AlarmClockInfo.ID + " = ?";
		ddl.update(AlarmClockInfo.TABLE_NAME, values, selection,
				new String[] { String.valueOf(alarmClockInfo.id) });
	}

	public AlarmClockInfo query(int id) {
		AlarmClockInfo alarmClockInfo = new AlarmClockInfo();
		String selection = AlarmClockInfo.ID + " = ?";
		Cursor mCursor = ddl.query(AlarmClockInfo.TABLE_NAME, null, selection,
				new String[] { String.valueOf(id) }, null, null, null);
		if (mCursor != null) {
			if (mCursor.moveToNext()) {
				alarmClockInfo.sUserName = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockInfo.USER_NAME));
				alarmClockInfo.title = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockInfo.TITLE));
				alarmClockInfo.sDay = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockInfo.DAY));
				alarmClockInfo.iType = mCursor.getInt(mCursor
						.getColumnIndex(AlarmClockInfo.TYPE));
				alarmClockInfo.id = mCursor.getInt(mCursor
						.getColumnIndex(AlarmClockInfo.ID));

				String sTime = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockInfo.TIME));
				String[] arrTimes = sTime.split(",");
				alarmClockInfo.times = new ArrayList<String>(
						Arrays.asList(arrTimes));
				;

				if (2 == alarmClockInfo.iType) {
					String sWeek = mCursor.getString(mCursor
							.getColumnIndex(AlarmClockInfo.WEEK));
					String[] arrWeeks = sWeek.split(",");
					alarmClockInfo.arrWeeks = arrWeeks;
				}

			}
			mCursor.close();
			return alarmClockInfo;
		} else {
			return null;
		}
	}

	public List<AlarmClockInfo> query(String sUserName) {
		if (Utils.isEmpty(sUserName))
			return null;

		String selection = AlarmClockInfo.USER_NAME + " = ?";
		Cursor mCursor = ddl.query(AlarmClockInfo.TABLE_NAME, null, selection,
				new String[] { sUserName }, null, null, AlarmClockInfo.ID
						+ " DESC");
		if (mCursor != null) {
			List<AlarmClockInfo> alarmClockInfos = new ArrayList<AlarmClockInfo>();
			while (mCursor.moveToNext()) {
				AlarmClockInfo alarmClockInfo = new AlarmClockInfo();
				alarmClockInfo.sUserName = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockInfo.USER_NAME));
				alarmClockInfo.title = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockInfo.TITLE));
				alarmClockInfo.sDay = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockInfo.DAY));
				alarmClockInfo.iType = mCursor.getInt(mCursor
						.getColumnIndex(AlarmClockInfo.TYPE));
				alarmClockInfo.id = mCursor.getInt(mCursor
						.getColumnIndex(AlarmClockInfo.ID));

				String sTime = mCursor.getString(mCursor
						.getColumnIndex(AlarmClockInfo.TIME));
				String[] arrTimes = sTime.split(",");
				alarmClockInfo.times = new ArrayList<String>(
						Arrays.asList(arrTimes));

				if (2 == alarmClockInfo.iType) {
					String sWeek = mCursor.getString(mCursor
							.getColumnIndex(AlarmClockInfo.WEEK));
					LogUtil.i("week:" + sWeek);
					String[] arrWeeks = sWeek.split(",");
					alarmClockInfo.arrWeeks = arrWeeks;
				}

				alarmClockInfos.add(alarmClockInfo);
			}
			LogUtil.i("query " + alarmClockInfos.size());
			mCursor.close();
			return alarmClockInfos;
		} else {
			return null;
		}
	}

	public int delete(int id) {
		String whereClause = AlarmClockInfo.ID + " = ? ";
		return ddl.delete(AlarmClockInfo.TABLE_NAME, whereClause,
				new String[] { String.valueOf(id) });
	}

	public int delete(String sUserName) {
		String whereClause = AlarmClockInfo.USER_NAME + " = ? ";
		return ddl.delete(AlarmClockInfo.TABLE_NAME, whereClause,
				new String[] { sUserName });
	}

	public void close() {
		ddl.close();
	}
}
