package com.bluebud.info;

public class AlarmClockHistoryInfo {
	public static final String ID = "_id";
	public static final String USER_NAME = "user_name";
	public static final String TITLE = "title";
	public static final String DAY = "day";
	public static final String TIME = "time";
	public static final String WEEK = "week";
	public static final String TYPE = "type";
	public static final String STATUS = "status";

	public static final String TABLE_NAME = "alarm_clock_history_table";
	public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ USER_NAME + " VARCHAR(30)," + TITLE + " VARCHAR(30)," + DAY
			+ " VARCHAR(30)," + TIME + " VARCHAR(30)," + WEEK + " VARCHAR(30),"
			+ TYPE + " VARCHAR(30)," + STATUS + " VARCHAR(10))";

	public int type;
	public String user_name;
	public String title;
	public String day;
	public String time;
	public String week;
	public int status;//0：未读 1：已读


}
