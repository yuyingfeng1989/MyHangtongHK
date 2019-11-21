package com.bluebud.info;

import java.util.List;

public class User {
	public String userid;
	public int is_email_verify;
	public List<Tracker> device_list;
	public int alert_mode;
	public long timezone_check;
	public int timezone_id;
	public List<Advertisement> advertising;
	public String username;
//微聊
	public String chat_token;
	public String portrait;
	public String nickname;
	public String chatProductType;//判断是否包含在群里面微聊
	public String dscMallLogin;//商城链接
	public String dscMallUserId;//大商创的user_id
	public String dscMallOpenId;//大商创openId
	public String dscMallToken;//大商创token
	
	public double lat;
	public double lng;

	public static final String USER_NAME = "user_name";
	public static final String USER_PWD = "user_pwd";

	public static final String TABLE_NAME = "user_table";
	public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME
			+ "(_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ USER_NAME + " VARCHAR(30)," + USER_PWD + " VARCHAR(30))";

	public static final String USER_QUERY_DISTINCT = "select distinct * from "
			+ TABLE_NAME;
}
