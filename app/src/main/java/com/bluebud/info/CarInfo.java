package com.bluebud.info;

import java.util.List;

/*E/gf      (24129): response:{"code":"0","ret":{"carInspectionData":[{"score":93,
 "datetime":"2016-06-13 15:39:15","cts_id":6,"item_type":1,"item_status":true},{"
 score":93,"datetime":"2016-06-13 15:39:15","cts_id":6,"item_type":2,"item_status
 ":false},{"score":93,"datetime":"2016-06-13 15:39:15","cts_id":6,"item_type":3,"
 item_status":false},{"score":93,"datetime":"2016-06-13 15:39:15","cts_id":6,"ite
 m_type":4,"item_status":false},{"score":93,"datetime":"2016-06-13 15:39:15","cts
 _id":6,"item_type":5,"item_status":false},{"score":93,"datetime":"2016-06-13 15:
 39:15","cts_id":6,"item_type":6,"item_status":false}],"deviceSn":"213GD0000001",
 "mileageAndFuel":{"totalmileage":193.16999998688698,"totalfuel":185,"mileage":0,
 "fuel":0}},"what":null}
 */
/**
 * @ClassName CarDetectInfo
 * @PackageName com.bluebud.info
 * @Description 从服务器返回的汽车检测数据bean
 * @author Andyzhao
 * @Date 2016-6-13 上午9:47:35
 * @Version V1.0
 */
public class CarInfo {
	public String deviceSn;
	public MileageAndFuel mileageAndFuel;
	public List<CarInspectionData> carInspectionData;
	public static class MileageAndFuel {
		public float totalmileage;//车辆总里程
		public float totalfuel;
		public float speed;//车辆实时速度
		public float rotationRate;//车辆转速
		public float mileage;//当日行驶里程
		public float fuel;//当日油耗
		public int carStatus;//车辆行驶状态，0:熄火1：运行 2：怠速
		public int voltage;//电量0：消除报警/未报警1：低电压报警 ,默认：0
		public int water;//水温0：消除报警/未报警1：水温报警 ,默认：0
		public int oil;//0：消除报警/未报警1：油量报警 ,默认：0



		@Override
		public String toString() {
			return "MileageAndFuel{" +
					"totalmileage=" + totalmileage +
					", totalfuel=" + totalfuel +
					", speed=" + speed +
					", rotationRate=" + rotationRate +
					", mileage=" + mileage +
					", fuel=" + fuel +
					", carStatus=" + carStatus +
					'}';
		}
	}
}
