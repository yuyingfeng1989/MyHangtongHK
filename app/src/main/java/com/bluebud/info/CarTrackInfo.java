package com.bluebud.info;

import java.util.List;

//06-14 15:30:12.965: E/gf(21919): ����response:
//{"code":"0","ret":{"deviceSn":"213GD0000001",
//	"driveTrailData":[{"start_time":"2016-06-14 09:22:34","end_time":"2016-06-14 14:46:34",
//		"spendtime":19440,"mileage":84000,
//		"fuel_consumption":0.17,
//		"start_latlon":"",
//		"end_latlon":"","start_addr":"","end_addr":"",
//		"speed":0.0720164609053498}]},"what":null}

public class CarTrackInfo {
	public String deviceSn;
	public List<DriveTrailData> driveTrailData;
	/*"sumSpendtime": 142,
	"sumMileage": 4301,
			"sumFuelConsumption": 859.8*/

	public static class DriveTrailData {
		public String start_time;
		public String end_time;
		public int spendtime;
		public float mileage;
		public float fuel_consumption;
		public String start_latlon;
		public String end_latlon;
		public String start_addr;
		public String end_addr;
		public float speed;
		public String sumSpendtime;
		public String sumMileage;
		public String sumFuelConsumption;


	
	}
}
