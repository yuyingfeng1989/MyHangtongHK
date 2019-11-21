package com.bluebud.info;

import java.io.Serializable;

public class Tracker implements Serializable {
	public String device_sn;
	public String super_user;
	public String tracker_sim;
	public int ranges = 1;
	public int around_ranges=1;
	public String product_type;
	public String head_portrait;
	public int gps_interval;
	public int defensive;// 1代表布防、0代表撤防
	public String expired_time;
	public String expired_time_de;//设备到期时间
	public boolean expired;
	public int disable;//0 正常，1：停用
	public boolean expiredTime;//企业到期 true,false.
	public boolean expiredTimeDe;//设备到期
	public boolean one_month_expired;//一个月内到期
	public int timezone;
	public int protocol_type;//5为770手表

	public String isExistGroup;//判断是否在微聊群里面

	public int bt_enable = 0;// 开关机状态值
	public String cdt_enable;// 上课关机状态值
	
	public int onlinestatus;// 0:不在线 1：在线 3:休眠

	public String nickname;

	public DeviceInfo mDeviceInfo = new DeviceInfo();
	
	public String conn_name;
	public String conn_port;
	public int is_gps=0;//是否开启定位//0表示开启，1表示不开启

	@Override
	public String toString() {
		return "Tracker{" +
				"device_sn='" + device_sn + '\'' +
				", super_user='" + super_user + '\'' +
				", product_type='" + product_type + '\'' +
				", protocol_type=" + protocol_type +
				", isExistGroup='" + isExistGroup + '\'' +
				", nickname='" + nickname + '\'' +
				'}';
	}
}
