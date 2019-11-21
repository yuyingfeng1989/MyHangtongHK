package com.bluebud.info;

import java.io.Serializable;

public class PetInsur implements Serializable {

	private static final long serialVersionUID = 1L;
	public String no;
	public String deviceSn;
	public String user_name;
	public String real_name;
	public String mobile;
	public String dog_name;
	public String type;
	public String colour;
	public int tail_shape;
	public String age;
	public int sex;
	public String start_time;
	public String end_time;
	public String create_time;
	
	
	
	@Override
	public String toString() {
		return "PetInsur [no=" + no + ", deviceSn=" + deviceSn + ", user_name="
				+ user_name + ", real_name=" + real_name + ", mobile=" + mobile
				+ ", dog_name=" + dog_name + ", type=" + type + ", colour="
				+ colour + ", tail_shape=" + tail_shape + ", age=" + age
				+ ", sex=" + sex + ", start_time=" + start_time + ", end_time="
				+ end_time + ", create_time=" + create_time + "]";
	}
	
	
}
