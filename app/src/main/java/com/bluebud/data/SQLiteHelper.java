package com.bluebud.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bluebud.info.Alarm;
import com.bluebud.info.AlarmClockHistoryInfo;
import com.bluebud.info.AlarmClockInfo;
import com.bluebud.info.User;

public class SQLiteHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "hangtong";
	private static final int DATABASE_VERSION = 1;

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(User.TABLE_CREATE);
//		db.execSQL(Alarm.TABLE_CREATE);//创建警情数据库表
		//db.execSQL(Alarm.TABLE_CREATE1);
		db.execSQL(AlarmClockInfo.TABLE_CREATE);
		db.execSQL(AlarmClockHistoryInfo.TABLE_CREATE);
//		db.execSQL(PayPalInfo.TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
