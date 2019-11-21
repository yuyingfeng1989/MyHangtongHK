package com.castel.obd;

public class OBD {
	static {
		System.loadLibrary("CRC");
	}

	public static native int CRC(String msg);
	public static native String SMSEncrypt(String msg);
	public static native String SMSAPNEncrypt(String apn, String userName,String pwd);
	public static native String SMSDecrypt(String msg);
}
