package com.bluebud.map.model;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.bluebud.constant.TrackerConstant;
import com.bluebud.info.CurrentGPS;
import com.bluebud.info.PeripherDetail;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.MapLifecycleListener;
import com.bluebud.map.bean.MyGeocodeCallback;
import com.bluebud.map.bean.MyLatLng;
import com.bluebud.map.poi.MyGetPoiSearchResultListener;

import java.util.List;

public abstract class AbstractMapModel implements MapLifecycleListener {

    boolean isNeedOffset; //汽车图标是否需要偏移，默认不需要

    /**
     * 返回当前地图的view
     */
    public abstract View getMapView(Context context);

    /**
     * 展示当前定位的图标
     */
    public abstract void showMyLocation(boolean showMyLocation);

    abstract boolean hasInitialized();

    /**
     * 定位到当前位置
     */
    abstract void locateMyWay();
    /**
     * 地图初始化操作
     */
    protected abstract void onInitMap(Context context);

    /**
     * 清空地图上的各种marker
     */
    abstract void mapClearOverlay();

    /**
     * 切换当前地图中心店
     */
    abstract void changeLocation(MyLatLng myLatLng);

    // 根据周边搜索范围来调整缩放等级，目前搜索范围是固定的，所以暂时是固定一个值
    abstract void changeLocationByBound(MyLatLng myLatLng);

    /**
     * 切换地图类型
     * @param isNormalType 当前是否是普通地图类型
     * @return 返回修改后的状态，是否是普通类型
     */
    abstract boolean changeMapType(boolean isNormalType);

    /**
     * 添加圆形覆盖物
     */
    abstract void addCircleOverlay(MyLatLng myLatLng, int radius);

    /**
     * 设置主marker，只有一个
     */
    abstract void setMarker(MyLatLng myLatLng, View fromView);

    /**
     * 添加Marker，可以添加多个
     */
    abstract String addMarker(PeripherDetail detailList, View fromView, OnInfoWindowChangedListener listener);

    /**
     * 添加marker，只不过是调整了锚点
     */
    abstract void addMarker(MyLatLng myLatLng, float anchor);

    /**
     * 根据一组坐标放大缩小地图并居中
     */
    abstract void setBoundByLatlngs(List<MyLatLng> latlngs, int bouns);

    /**
     * 复数添加marker
     */
    abstract void addMarkers(List<PeripherDetail> detailList, View fromView, OnInfoWindowChangedListener listener);

    /**
     * 清除主要marker
     */
    abstract void removeMarker();

    /**
     * 展示和隐藏marker
     */
    abstract void showInfoWindow(View view, MyLatLng myLatLng);

    abstract void hideInfoWindow();

    /**
     * 计算OBD设备在首页的偏移后的坐标
     */
//    abstract MyLatLng getOBDCorrectPos(MyLatLng myLatLng);

    public void setNeedOffset(boolean needOffset) {
        this.isNeedOffset = needOffset;
    }

    /**
     * GPS坐标转换成当前地图用的坐标
     */
    abstract MyLatLng gpsConvert2MapPoint(MyLatLng myLatLng);

    /**
     * GPS坐标转换成当前地图的wgs84坐标
     */
    abstract MyLatLng mapPointConvert2Wgs84(MyLatLng myLatLng);

    /**
     * 计算两个坐标的距离
     */
    abstract double getDistance(MyLatLng src, MyLatLng dst);

    /**
     * 搜索附近的兴趣点
     * @param keyWord 关键词
     * @param location 中心范围坐标
     */
    abstract void searchNearby(String keyWord, MyLatLng location);

    /**
     * 设置兴趣点搜索监听器，配合searchNearby使用
     */
    abstract void setPoiSearchListener(Context context, MyGetPoiSearchResultListener listener);

    public void initMapView(Activity activity, int contentId, MyMapReadyCallback callback) {

    }

    /**
     * 地图点击监听器
     */
    abstract void setOnMapClickListener(MyMapClickListener listener);

    abstract void setOnMarkerClickListener(MyMarkerClickListener listener);

    /**
     * 根据一组坐标添加地图兴趣点
     */
    abstract void mapAddRouteOverlay(int range, CurrentGPS... routeGPSList);

    abstract void mapAddRouteOverlayWithLine(int range, boolean addLine, CurrentGPS... routeGPSList);

    abstract void setGeoSearchCallback(double lat, double lng, MyGeocodeCallback callback);

    /**
     * 画轨迹
     */
    abstract void drawTracker(List<CurrentGPS> trackList);

    /**
     * 根据设备类型获取图标
     */
    protected int getTrackerIconByRange(int range) {
        switch (range) {
            case TrackerConstant.VALUE_RANGE_CAR:
            case TrackerConstant.VALUE_RANGE_OBD:
                return R.drawable.carslow;
            case TrackerConstant.VALUE_RANGE_PET:
                return R.drawable.petslow;
            case TrackerConstant.VALUE_RANGE_MOTO:
                return R.drawable.motoslow;
            default:
                return R.drawable.peopleslow;
        }

    }

    public interface MyMapClickListener {
        void onClick(MyLatLng myLatLng);
    }

    public interface MyMarkerClickListener {
        void onMarkClick(String id);
    }

    public interface MyMapReadyCallback {
        void onMapReady();
    }

    public interface OnInfoWindowChangedListener {
        View OnInfoWindowChanged(PeripherDetail detail);
    }
}