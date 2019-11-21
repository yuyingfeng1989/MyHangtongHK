package com.bluebud.info;

import java.io.Serializable;

public class LostCard2People implements Serializable {
	public String head_portrait;

	public String mobile1;
	public String simNo;//sim手表卡号
//	public String mobile2;
//	public String mobile3;
	public String id;
	public String human_height;
	public String human_weight;
	public String nickname;
	public String human_age;
	public String human_sex;
	public String human_step;
	public String human_feature;
	public String human_addr;
	public String human_lost_addr;
	
	public String lat;
	public String lng;
	public String shareUrl;
	//add by 
	public String human_birthday;
	
	@Override
	public String toString() {
		return "LostCard2People [head_portrait=" + head_portrait + ", mobile1="
				+ mobile1 + ", id=" + id + ", human_height=" + human_height
				+ ", human_weight=" + human_weight + ", nickname=" + nickname
				+ ", human_age=" + human_age + ", human_sex=" + human_sex
				+ ", human_step=" + human_step + ", human_feature="
				+ human_feature + ", human_addr=" + human_addr
				+ ", human_lost_addr=" + human_lost_addr + ", lat=" + lat
				+ ", lng=" + lng + ", shareUrl=" + shareUrl
				+ ", human_birthday=" + human_birthday
				+ ", simNo=" + simNo + "]";
	}

}
