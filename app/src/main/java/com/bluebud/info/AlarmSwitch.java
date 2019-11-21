package com.bluebud.info;

public class AlarmSwitch {
	
	public int id;
	public int sos = 1;
	public int boundary = 1;
	public int voltage = 1;
	public int tow = 1;
	public int takeOff = 1;
	public int vibration = 1;
	public int clipping = 1;
	public int speed = 1;
	public int outage = 1;//断电告警
	public int water = 1;//水温报警
	public int speedValue = 100;
	public int speedTime = 3;

	@Override
	public String toString() {
		return "AlarmSwitch{" +
				"id=" + id +
				", sos=" + sos +
				", boundary=" + boundary +
				", voltage=" + voltage +
				", tow=" + tow +
				", takeOff=" + takeOff +
				", vibration=" + vibration +
				", clipping=" + clipping +
				", speed=" + speed +
				", outage=" + outage +
				", water=" + water +
				", speedValue=" + speedValue +
				", speedTime=" + speedTime +
				'}';
	}
}
