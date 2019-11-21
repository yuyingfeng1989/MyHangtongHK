package com.bluebud.info;

public class LatLng {
	public String gpstime;
	public double lat;
	public double lng;
	public LatLng(String gpstime, double lat, double lng) {
		super();
		this.gpstime = gpstime;
		this.lat = lat;
		this.lng = lng;
	}
	@Override
	public String toString() {
		return gpstime + " " + lat + "," + lng;
	}
	
	
}
