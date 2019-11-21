package com.bluebud.info;

import java.io.Serializable;

public class LostCard2Pet implements Serializable {

	public String head_portrait;
	public String nickname;
	public String pet_sex;
	public String pet_breed;
	public String pet_weight;
	public String pet_age;
	public String pet_feature;
	public String pet_addr;
	public String mobile1;
	public String mobile2;
	public String mobile3;
	public String simNo;//sim手表卡号
	public String pet_lost_addr;
	
	public String lat;
	public String lng;
	public String shareUrl;
	public String pet_birthday;
	public int insur_code;
	
	@Override
	public String toString() {
		return "LostCard2Pet [head_portrait=" + head_portrait + ", nickname="
				+ nickname + ", pet_sex=" + pet_sex + ", pet_breed="
				+ pet_breed + ", pet_weight=" + pet_weight + ", pet_age="
				+ pet_age + ", pet_feature=" + pet_feature + ", pet_addr="
				+ pet_addr + ", mobile1=" + mobile1 + ", mobile2=" + mobile2
				+ ", mobile3=" + mobile3 + ", pet_lost_addr=" + pet_lost_addr
				+ ", lat=" + lat + ", lng=" + lng + ", shareUrl=" + shareUrl
				+ ", pet_birthday=" + pet_birthday
				+ ", simNo=" + simNo + "]";
	}
}
