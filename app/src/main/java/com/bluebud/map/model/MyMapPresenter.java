package com.bluebud.map.model;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.bluebud.app.App;
import com.bluebud.info.CurrentGPS;
import com.bluebud.info.PeripherDetail;
import com.bluebud.map.MapLifecycleListener;
import com.bluebud.map.bean.MyGeocodeCallback;
import com.bluebud.map.bean.MyLatLng;
import com.bluebud.map.poi.MyGetPoiSearchResultListener;

import java.util.List;

public class MyMapPresenter implements MapLifecycleListener {

    public static final int MAP_TYPE_GOOGLE = App.MAP_TYPE_GMAP; // 谷歌地图
    public static final int MAP_TYPE_AMAP = App.MAP_TYPE_AMAP; // 高德地图
    public static final int MAP_TYPE_BMAP = App.MAP_TYPE_BMAP; // 百度地图


    private AbstractMapModel mMapModel;
    private int mMapType;

    public MyMapPresenter(Context context, int mapType) {
        this.mMapType = mapType;
        initMap(context);
    }

    public boolean hasInitialized() {
        return mMapModel.hasInitialized();
    }

    private void initMap(Context context) {
        switch (mMapType) {
            case MAP_TYPE_AMAP:
                mMapModel = new AMapModel(context);
                break;
            case MAP_TYPE_GOOGLE:
            default:
                mMapModel = new GoogleMapModel(context);
                break;
        }

    }

    public void initMapView(Activity activity, int contentId, AbstractMapModel.MyMapReadyCallback callback) {
        mMapModel.initMapView(activity, contentId, callback);
    }

    public void showMyLocation(boolean showMyLocation) {
        mMapModel.showMyLocation(showMyLocation);
    }

    public void mapLocation() {
        mMapModel.locateMyWay();
    }


    public AbstractMapModel getMapModel() {
        return mMapModel;
    }

    public View getMapView(Context context) {
        if (mMapModel != null) {
            return mMapModel.getMapView(context);
        }
        return null;
    }

//    public View getMapView(Context activity, int contentId) {
//        if (mMapModel != null) {
//            return mMapModel.getMapView(activity);
//        }
//        return null;
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (mMapModel != null) {
            mMapModel.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        if (mMapModel != null) {
            mMapModel.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mMapModel != null) {
            mMapModel.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (mMapModel != null) {
            mMapModel.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMapModel != null) {
            mMapModel.onSaveInstanceState(outState);
        }
    }

    public void mapClearOverlay() {
        mMapModel.mapClearOverlay();
    }

    public void changeLocation(MyLatLng latLng) {
        mMapModel.changeLocation(latLng);
    }

    public void needOBDOffset(boolean needOffset) {
        mMapModel.setNeedOffset(needOffset);
    }

//    public MyLatLng getOBDCorrectPos(MyLatLng latlng) {
//        return mMapModel.getOBDCorrectPos(latlng);
//    }

    public void setBoundByLatlngs(List<MyLatLng> latLngs, int bound) {
        mMapModel.setBoundByLatlngs(latLngs, bound);
    }

    public void changeLocationByBound(MyLatLng latLng) {
        mMapModel.changeLocationByBound(latLng);
    }

    public void setMarker(MyLatLng myLatLng, View fromView) {
        mMapModel.setMarker(myLatLng, fromView);
    }

    public String addMarker(PeripherDetail detail, View fromView, AbstractMapModel.OnInfoWindowChangedListener listener) {
        return mMapModel.addMarker(detail, fromView, listener);
    }

    public void addCenterMarker(MyLatLng myLatLng) {
        mMapModel.addMarker(myLatLng, 0.5f);
    }

    public void addMarkers(List<PeripherDetail> detailList, View fromView, AbstractMapModel.OnInfoWindowChangedListener listener) {
        mMapModel.addMarkers(detailList, fromView, listener);
    }

//    public void setMarker(MyLatLng latLng, int resId) {
//        mMapModel.setMarker(latLng, resId);
//    }
public void addCircleOverlay(MyLatLng myLatLng) {
    mMapModel.addCircleOverlay(mMapModel.gpsConvert2MapPoint(myLatLng), 5);
}


    public void addCircleOverlay(MyLatLng myLatLng, int radius) {
        mMapModel.addCircleOverlay(myLatLng, radius);
    }

    public void setOnMapClickListener(AbstractMapModel.MyMapClickListener listener) {
        mMapModel.setOnMapClickListener(listener);
    }

    public void setOnMarkerClickListener(AbstractMapModel.MyMarkerClickListener listener) {
        mMapModel.setOnMarkerClickListener(listener);
    }

    public void setGeoSearchCallback(double lat, double lng, MyGeocodeCallback callback) {
        mMapModel.setGeoSearchCallback(lat, lng, callback);
    }


    /**
     * 改变地图显示类型。可以看到参数和返回值描述一样，这里说指将当前地图状态传进去
     * 内部根据该状态选择改变的地图类型，然后返回新的状态结果
     * @param isNormalType 是否是普通地图类型
     * @return 是否是普通地图类型
     */
    public boolean changeMapType(boolean isNormalType) {
        return mMapModel.changeMapType(isNormalType);
    }

    public void removeMarker() {
        mMapModel.removeMarker();
    }

    public void showInfoWindow(View view, MyLatLng myLatLng) {
        mMapModel.showInfoWindow(view, myLatLng);
    }

    public void hideInfoWindow() {
        mMapModel.hideInfoWindow();
    }

//    public MyLatLng gpsConvert2MapPoint(MyLatLng myLatLng) {
//        return mMapModel.gpsConvert2MapPoint(myLatLng);
//    }

    public MyLatLng mapPointConvert2Wgs84(MyLatLng myLatLng) {
        return mMapModel.mapPointConvert2Wgs84(myLatLng);
    }

    public double getDistance(MyLatLng src, MyLatLng dst) {
        return mMapModel.getDistance(src, dst);
    }

    public MyLatLng gpsConvert2MapPoint(MyLatLng myLatLng) {
        return mMapModel.gpsConvert2MapPoint(myLatLng);
    }

    public void setPoiSearchListener(Context context, MyGetPoiSearchResultListener listener) {
        mMapModel.setPoiSearchListener(context, listener);
    }

    public void searchNearby(String keyWord, MyLatLng location) {
        mMapModel.searchNearby(keyWord, location);
    }

    public void mapAddRouteOverlay(int range, CurrentGPS... routeGPSList) {
        mMapModel.mapAddRouteOverlay(range, routeGPSList);
    }

    public void mapAddRouteOverlayWithLine(int range, boolean addLine, CurrentGPS... routeGPSList) {
        mMapModel.mapAddRouteOverlayWithLine(range, addLine, routeGPSList);
    }

    public void drawTrack(List<CurrentGPS> currentGPSList) {
        mMapModel.drawTracker(currentGPSList);
    }

}
