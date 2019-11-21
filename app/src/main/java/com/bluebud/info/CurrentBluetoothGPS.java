package com.bluebud.info;

/*
 response:{"code":"0","ret":{"did":849,"collect_datetime":"2016-03-22 10:49:03","lat":22.5471417,"lng":113.9359817,"speed":0.86,"direction":173.6,"battery":"66","steps":0,"LBS_WIFI_Range":0,"onlinestatus":0,"totalMileage":49050,"mileage":0,"step":0,"calorie":0},"what":null}
 */

public class CurrentBluetoothGPS {
	public String collect_datetime;
	public double lat = 0;
	public double lng = 0;
	public float speed;
	public float direction;
	public String starnum;
	public int battery;
	public int steps;
	public int LBS_WIFI_Range = 0;//  定位点半径
}
