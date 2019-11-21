package com.bluebud.info;

import java.io.Serializable;

public class TimeSwitchCourseInfo implements Serializable{

	public String enable = "0";
	public String amstarttime = "07:30";
	public String amendtime = "12:00";
	public String tmstarttime = "14:00";
	public String tmendtime = "16:00";
	public String starttime3 = "00:00";
	public String endtime3 = "00:00";
	public String repeatday = "1,2,3,4,5";

	@Override
	public String toString() {
		return "TimeSwitchCourseInfo{" +
				"enable=" + enable +
				", amstarttime='" + amstarttime + '\'' +
				", amendtime='" + amendtime + '\'' +
				", tmstarttime='" + tmstarttime + '\'' +
				", tmendtime='" + tmendtime + '\'' +
				", starttime3='" + starttime3 + '\'' +
				", endtime3='" + endtime3 + '\'' +
				", repeatday='" + repeatday + '\'' +
				'}';
	}
}
