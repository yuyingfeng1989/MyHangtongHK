package com.bluebud.info;

import java.io.Serializable;

public class LostCard2Car implements Serializable {
	
	public String head_portrait;

	public String nickname;// 摩托车昵称
	
	public String motor_cc;// 摩托车排量
	public String motor_trademark;// 摩托车品牌
	public String motor_set;// 摩托车车系
	public String motor_year;// 摩托车年款
	public String car_no;// 汽车牌号
	public String car_vin;// 汽车VIN
	public String car_engine;// 汽车发动机号
	public String car_set;// 汽车车系
	public String car_brand;// 汽车品牌
	public String car_year;// 汽车年款
	public String car_type;// 汽车车型
	public String car_oil_type;// 汽车燃油类型
	public String car_mileage;// 当前里程
	public String car_check_time;// 年审时间
	
	
	public String mobile1;
	public String mobile2;
	public String mobile3;
	public String simNo;//sim卡号
	public String motor_no;// 摩托车牌号
	public String moto_type;// 摩托车型号
	public String motor_buytime;
	public String shareUrl;
	public String car_buytime;
	
	public String obd_no;
	public String obd_type;
	public String obd_buytime;
	@Override
	public String toString() {
		return "LostCard2Car [head_portrait=" + head_portrait + ", nickname="
				+ nickname + ", motor_cc=" + motor_cc + ", motor_trademark="
				+ motor_trademark + ", motor_set=" + motor_set
				+ ", motor_year=" + motor_year + ", car_no=" + car_no
				+ ", car_vin=" + car_vin + ", car_engine=" + car_engine
				+ ", car_set=" + car_set + ", car_brand=" + car_brand
				+ ", car_year=" + car_year + ", car_type=" + car_type
				+ ", car_oil_type=" + car_oil_type + ", car_mileage="
				+ car_mileage + ", car_check_time=" + car_check_time
				+ ", mobile1=" + mobile1 + ", mobile2=" + mobile2
				+ ", mobile3=" + mobile3 + ", motor_no=" + motor_no
				+ ", moto_type=" + moto_type + ", motor_buytime="
				+ motor_buytime + ", shareUrl=" + shareUrl + ", car_buytime="
				+ car_buytime + ", obd_no=" + obd_no + ", obd_type=" + obd_type
				+ ", obd_buytime=" + obd_buytime
				+ ", simNo=" + simNo+ "]";
	}
	

	

}
