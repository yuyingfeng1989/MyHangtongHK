package com.bluebud.info;

/*
 response:{"code":"0","ret":{"did":849,"collect_datetime":"2016-03-22 10:49:03","lat":22.5471417,"lng":113.9359817,"speed":0.86,"direction":173.6,"battery":"66","steps":0,"LBS_WIFI_Range":0,"onlinestatus":0,"totalMileage":49050,"mileage":0,"step":0,"calorie":0},"what":null}
 */

import android.text.TextUtils;

import java.io.Serializable;

public class CurrentGPS implements Serializable {
    public String collect_datetime;
    public double lat = 0;
    public double lng = 0;
    public float speed;
    public float direction;
    public String starnum;

    public double totalMileage;
    public double mileage;
    public int step;
    public double calorie;
    public int battery;

    public int onlinestatus;//0:不在线 1：在线
    public int car_status;//0：熄火，1：运行，2:怠速

    public int LBS_WIFI_Range = 0;//  定位点半径
    public int gps_flag;//0：未定位，1：2D定位，2:LBS定位，3：3D定位 10:wifi定位
    public int overSpeedFlag;//当前gps点是否超速0否，1是

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentGPS that = (CurrentGPS) o;
        return Double.compare(that.lat, lat) == 0 &&
                Double.compare(that.lng, lng) == 0 &&
                Float.compare(that.speed, speed) == 0 &&
                Float.compare(that.direction, direction) == 0 &&
                Double.compare(that.totalMileage, totalMileage) == 0 &&
                Double.compare(that.mileage, mileage) == 0 &&
                step == that.step &&
                Double.compare(that.calorie, calorie) == 0 &&
                battery == that.battery &&
                onlinestatus == that.onlinestatus &&
                car_status == that.car_status &&
                LBS_WIFI_Range == that.LBS_WIFI_Range &&
                gps_flag == that.gps_flag &&
                overSpeedFlag == that.overSpeedFlag &&

                TextUtils.equals(collect_datetime, that.collect_datetime) &&
                TextUtils.equals(starnum, that.starnum);
    }

    @Override
    public String toString() {
        return "CurrentGPS{" +
                "collect_datetime='" + collect_datetime + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", speed=" + speed +
                ", direction=" + direction +
                ", starnum='" + starnum + '\'' +
                ", totalMileage=" + totalMileage +
                ", mileage=" + mileage +
                ", step=" + step +
                ", calorie=" + calorie +
                ", battery=" + battery +
                ", onlinestatus=" + onlinestatus +
                ", car_status=" + car_status +
                ", LBS_WIFI_Range=" + LBS_WIFI_Range +
                ", gps_flag=" + gps_flag +
                ", overSpeedFlag=" + overSpeedFlag +
                '}';
    }

    //	@Override
//	public int hashCode() {
//		return Objects.hash(collect_datetime, lat, lng, speed, direction, starnum, totalMileage, mileage, step, calorie, battery, onlinestatus, car_status, LBS_WIFI_Range, gps_flag, overSpeedFlag);
//	}
//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof CurrentGPS) {
//			CurrentGPS currentGPS = (CurrentGPS)obj;
//
//		}
//		return super.equals(obj);
//	}


}
