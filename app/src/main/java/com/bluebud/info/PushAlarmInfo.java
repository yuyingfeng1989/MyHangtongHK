package com.bluebud.info;

import java.io.Serializable;

public class PushAlarmInfo implements Serializable {

	public int msgType;// 0表示位置数据；1表示警情数据；2表示设备状态
	public String equipId;// 设备号
	public String localDateTime;// 时间
	public double lng;// 经度
	public double lat;// 纬度
	public double speed;// 速度
	public double direction;// 方向
	public int ranges;// 范围
	public int timezone;// 时区ID
	public int alarmtype;// 警情类型
	public int onlinestatus;// 0:不在线 1：在线 3:休眠
	public int accon = 0;
	public String title;//提醒内容
	public String datetime;//提醒时间
	
	// guoqz add 20160229.
	public int LBS_WIFI_Range = 0;
	@Override
	public String toString() {
		return "PushAlarmInfo [msgType=" + msgType + ", equipId=" + equipId
				+ ", localDateTime=" + localDateTime + ", lng=" + lng
				+ ", lat=" + lat + ", speed=" + speed + ", direction="
				+ direction + ", ranges=" + ranges + ", timezone=" + timezone
				+ ", alarmtype=" + alarmtype + ", onlinestatus=" + onlinestatus
				+ ", LBS_WIFI_Range=" + LBS_WIFI_Range
				+ ", accon=" + accon + "]";
	}

	

}