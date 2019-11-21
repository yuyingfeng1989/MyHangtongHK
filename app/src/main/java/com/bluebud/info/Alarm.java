package com.bluebud.info;

public class Alarm {
//	public static final String ID = "_id";
//	public static final String USER_NAME = "user_name";
//	public static final String SERIAL_NUMBER = "serial_number";
//	public static final String TIME = "time";
//	public static final String LAT = "lat";
//	public static final String LNG = "lng";
//	public static final String SPEED = "speed";
//	public static final String TYPE = "type";
//	public static final String STATUS = "status";
//	public static final String NAMEID = "nameid";
//
//	public static final String TABLE_NAME = "alarm_table";
//	public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
//			+ TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//			+ USER_NAME + " VARCHAR(30)," + LAT + " VARCHAR(30)," + LNG
//			+ " VARCHAR(30)," + TIME + " VARCHAR(30)," + TYPE + " VARCHAR(30),"
//			+ SERIAL_NUMBER + " VARCHAR(30)," + SPEED + " VARCHAR(30),"
//			+ STATUS + " VARCHAR(10))";
//	public static final String TABLE_CREATE1 = "CREATE TABLE IF NOT EXISTS "
//			+ TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//			+ USER_NAME + " VARCHAR(30)," + LAT + " VARCHAR(30)," + LNG
//			+ " VARCHAR(30)," + TIME + " VARCHAR(30),"+ NAMEID + " VARCHAR(30)," + TYPE + " VARCHAR(30),"
//			+ SERIAL_NUMBER + " VARCHAR(30)," + SPEED + " VARCHAR(30),"
//			+ STATUS + " VARCHAR(10))";

	public int type;
	public int data;
	public String dtime;
	public double lat;
	public double lng;
	public double speed;
	public String address;
	public int status;
	public int id;//id±àºÅ
	public String serialNumber;
	public int readstatus;//0表示未读，1 表示已读
	
}
