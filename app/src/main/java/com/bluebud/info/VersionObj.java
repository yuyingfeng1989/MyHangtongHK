package com.bluebud.info;

public class VersionObj {
	public String appOS;
	public String appVersion;
	public String appUrl;
	public String currentFirmwareVersion;
	public String lastFirmwareVersion;
	public String firmwareUrl;
	public int isForceUpdate; //  0:不强制升级 1:强制升级
	public String description;// 升级描述
	public String phoneUrlAPK; //  APK下载地址

	@Override
	public String toString() {
		return "VersionObj{" +
				"appOS='" + appOS + '\'' +
				", appVersion='" + appVersion + '\'' +
				", appUrl='" + appUrl + '\'' +
				", currentFirmwareVersion='" + currentFirmwareVersion + '\'' +
				", lastFirmwareVersion='" + lastFirmwareVersion + '\'' +
				", firmwareUrl='" + firmwareUrl + '\'' +
				", isForceUpdate=" + isForceUpdate +
				", description='" + description + '\'' +
				", phoneUrlAPK='" + phoneUrlAPK + '\'' +
				'}';
	}
}
