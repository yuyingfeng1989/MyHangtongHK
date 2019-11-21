package com.bluebud.info;

//06-14 09:16:16.980: E/gf(30368): response:{"code":"0","ret":{"deviceSn":"213GD0000001","safeDriveData":
//{"score":100,"p2":1,"p11":0,"p4":0,"p5":0,"p8":1,"p9":0,"p12":1,"p13":0}},"what":null}

public class DriverDate {

    //	public String deviceSn;
    public SafeDriveData safeDriveData;
    public EconomicalDriveData economicalDriveData;

    public static class SafeDriveData {
        public int p2;//超速
        public int p4;//急加速
        public int p5;//急减速
        public int p6;//停车未熄火
        public int p8;//转速过高
        public int p9;//转速超标时长
        public int p10;//停车未熄火
        public int p11;//超速时长
        public int p12;//疲劳驾驶次数
        public int p13;//疲劳驾驶时长
        public int p14;//急拐弯
        public int p15;//急变道

        public String fuel;//油耗量
        public float drivetime;//行驶时间
        public String avgspeed;//平均车速
        public String kmfule;//平均油耗量
        public String mileage;//行驶公里数
        public int score;//分数
        public String testingtime;//检测时间
//		public int economicscore;//
//		public int safescore;//
//		public String tripEndtime;//
    }

    public static class EconomicalDriveData {
        //		public int economicscore;//
//		public int safescore;//
        public int p2;//超速
        public int p4;//急加速
        public int p5;//急减速
        public int p6;//停车未熄火
        public int p8;//转速过高
        public int p9;//转速超标时长
        public int p10;//停车未熄火
        public int p11;//超速时长
        public int p12;//疲劳驾驶次数
        public int p13;//疲劳驾驶时长
        public int p14;//急拐弯
        public int p15;//急变道
        public String fuel;//油耗量
        public float drivetime;//行驶时间
        public String avgspeed;//平均车速
        public String kmfule;//平均油耗量
        public String mileage;//行驶公里数
        public int score;//分数
        public String testingtime;//检测时间
//		public String tripEndtime;//
    }

    @Override
    public String toString() {
        return "DriverDate{" +
                ", safeDriveData=" + safeDriveData +
                ", economicalDriveData=" + economicalDriveData +
                '}';
    }
}
