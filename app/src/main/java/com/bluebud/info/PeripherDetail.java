package com.bluebud.info;

import com.bluebud.map.bean.MyLatLng;

import java.io.Serializable;

public class PeripherDetail implements Serializable {
	public String name;
	public String address;
	public int distance;
	public Double latitude;
	public Double longitude;
	public boolean hasConvert;

	public PeripherDetail(MyLatLng myLatLng) {
		if (myLatLng != null) {
			latitude = myLatLng.latitude;
			longitude = myLatLng.longitude;
		}
	}

	public PeripherDetail() {}
	
	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public MyLatLng toLatLng() {
		return MyLatLng.from(latitude, longitude);
	}

	@Override
	public String toString() {
		return "PeripherDetail [name=" + name + ", address=" + address
				+ ", distance=" + distance + ", latitude=" + latitude
				+ ", longitude=" + longitude + "]";
	}
	
	
}
