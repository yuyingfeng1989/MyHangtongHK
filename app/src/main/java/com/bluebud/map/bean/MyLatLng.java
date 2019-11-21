package com.bluebud.map.bean;

import android.location.Location;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;
import com.bluebud.app.App;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.ModifyOffset;
import com.bluebud.utils.PointDouble;

import java.io.Serializable;


public class MyLatLng implements Serializable {
    public final double latitude;
    public final double longitude;
    private boolean noConvert = false; // 不用进行坐标转换

    private MyLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private MyLatLng(double latitude, double longitude, boolean noConvert) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.noConvert = noConvert;
    }

    public static MyLatLng from(double latitude, double longitude) {
        return new MyLatLng(latitude, longitude);
    }

    public static MyLatLng from(Location location) {
        return new MyLatLng(location.getLatitude(), location.getLongitude());
    }

    public static MyLatLng from(com.google.android.gms.maps.model.LatLng latLng) {
        return new MyLatLng(latLng.latitude, latLng.longitude, true);
    }



    public static MyLatLng from(com.amap.api.maps.model.LatLng latLng) {
        return new MyLatLng(latLng.latitude, latLng.longitude, true);
    }



//    public MyLatLng(com.amap.api.maps.model.LatLng latLng) {
//        this.latitude = latLng.latitude;
//        this.longitude = latLng.longitude;
//    }
//
//    public MyLatLng(com.baidu.mapapi.model.LatLng latLng) {
//        this.latitude = latLng.latitude;
//        this.longitude = latLng.longitude;
//    }




    public com.amap.api.maps.model.LatLng toALatLng() {
        return toALatLng(noConvert);
    }

    public com.amap.api.maps.model.LatLng toALatLng(boolean noConvert) {
        if (noConvert) {
            return new com.amap.api.maps.model.LatLng(latitude, longitude);
        }
        return gpsConvert2AMapPoint(latitude, longitude);
    }



    public com.google.android.gms.maps.model.LatLng toGLatLng() {
        return toGLatLng(noConvert);
    }


    public com.google.android.gms.maps.model.LatLng toGLatLng(boolean noConvert) {
        if (noConvert) {
            return new com.google.android.gms.maps.model.LatLng(latitude, longitude);
        }
        return gpsConvert2GMapPoint(latitude, longitude);
    }

    /**
     * gps坐标转高德地图坐标
     * @return 转换后的数据
     */
    private LatLng gpsConvert2AMapPoint(double latitude, double longitude) {
        CoordinateConverter converter = new CoordinateConverter(App.getContext());
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(new LatLng(latitude, longitude));
        return converter.convert();
    }

    /**
     * 高德坐标转换为WGS84定位坐标
     */
    public MyLatLng toAmapWgs84Point() {
        LatLng tmpLL = gpsConvert2AMapPoint(latitude, longitude);
        double lat = 2 * latitude - tmpLL.latitude;
        double lng = 2 * longitude - tmpLL.longitude;
        return MyLatLng.from(lat, lng);
    }

    /**
     * google坐标转换为WGS84定位坐标
     */
    public MyLatLng toGmapWgs84Point() {
        com.google.android.gms.maps.model.LatLng tmpLL = gpsConvert2GMapPoint(latitude, longitude);
        double lat = 2 * latitude - tmpLL.latitude;
        double lng = 2 * longitude - tmpLL.longitude;
        return MyLatLng.from(lat, lng);
    }


    private com.google.android.gms.maps.model.LatLng gpsConvert2GMapPoint(double latitude, double longitude) {
        try {
            ModifyOffset offset = ModifyOffset.getInstance(App.getContext()
                    .getResources().openRawResource(R.raw.axisoffset));
            // FIXME: 2019/7/11 y6?,x6?傻傻分不清楚
            PointDouble point = new PointDouble(longitude, latitude);
            if (point.isInChina()) {
                PointDouble chPoint = offset.s2c(point);
                // 不要怀疑，就是这么写的
                latitude = chPoint.getY();
                longitude = chPoint.getX();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new com.google.android.gms.maps.model.LatLng(latitude, longitude);
    }

}
