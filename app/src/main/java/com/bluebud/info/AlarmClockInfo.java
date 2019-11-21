package com.bluebud.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlarmClockInfo implements Serializable {
	public static final String ID = "_id";
	public static final String CLOCK_ID = "id";
	public static final String USER_NAME = "user_name";
	public static final String TITLE = "title";
	public static final String DAY = "day";
	public static final String TIME = "time";
	public static final String WEEK = "week";
	public static final String TYPE = "type";
	public static final String LAST_DAY = "last_day";

	public static final String TABLE_NAME = "alarm_clock_table";
	public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ CLOCK_ID + " VARCHAR(30)," + USER_NAME + " VARCHAR(30)," + TITLE
			+ " VARCHAR(30)," + DAY + " VARCHAR(30)," + TIME + " VARCHAR(30),"
			+ WEEK + " VARCHAR(30)," + TYPE + " VARCHAR(30)," + LAST_DAY
			+ " VARCHAR(30))";

	public int id;
	public String sUserName;
	public String title;
	public int iType;// 0:紀念日 1:还款日 2:每周重复
	public List<String> times = new ArrayList<String>();
	public String sDay;
	public String[] arrWeeks;
	
	public boolean isEnd;
	public boolean weekly;
	public boolean monthly;
	public boolean yearly;
	public boolean monday;
	public boolean tuesday;
	public boolean wednesday;
	public boolean thursday;
	public boolean friday;
	public boolean saturday;
	public boolean sunday;
	public String repeat_year;
	public String repeat_month;
	public String repeat_day;
	public List<AlarmClockTimeInfo> diabolo = new ArrayList<AlarmClockTimeInfo>();

}
