package com.bluebud.map.model;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.bluebud.app.App;
import com.bluebud.constant.TrackerConstant;
import com.bluebud.info.CurrentGPS;
import com.bluebud.info.PeripherDetail;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.bean.MyGeocodeCallback;
import com.bluebud.map.bean.MyLatLng;
import com.bluebud.map.poi.MyGetPoiSearchResultListener;
import com.bluebud.utils.Constants;
import com.bluebud.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AMapModel extends AbstractMapModel {

    private TextureMapView mMapView;
    private AMap mAmap;
    private UiSettings mSettings;
    private Marker mLocationMarker;
    private boolean mHasInitInfoWindow;
    private PoiSearch mPoiSearch;
    private MyLocationStyle myLocationStyle;

    AMapModel(Context context) {
        if (context != null) {
            onInitMap(context);
        }
    }

    @Override
    public View getMapView(Context context) {
        if (mMapView == null) {
            onInitMap(context);
        }
        return mMapView;
    }

    @Override
    public void showMyLocation(boolean showMyLocation) {
        myLocationStyle.showMyLocation(showMyLocation);
        mAmap.setMyLocationStyle(myLocationStyle);
        mAmap.animateCamera(CameraUpdateFactory.zoomTo(Constants.AMAP_ZOOM));
    }

    @Override
    boolean hasInitialized() {
        return mAmap != null;
    }

    @Override
    void locateMyWay() {

    }

    @Override
    protected void onInitMap(Context context) {
        mMapView = new TextureMapView(context);
        mAmap = mMapView.getMap();
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
//                .myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location))
                .strokeColor(Color.argb(0, 0, 0, 0))
                .radiusFillColor(Color.argb(0, 0, 0, 0))
                .showMyLocation(false)
                .interval(2000);
        mAmap.setMyLocationStyle(myLocationStyle);
        mAmap.setMyLocationEnabled(true);
        mAmap.animateCamera(CameraUpdateFactory.zoomTo(Constants.AMAP_ZOOM));
        mAmap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                changeLocation(marker.getPosition());
                marker.showInfoWindow();
                return true;
            }
        });
        onUiSettings();
    }

    private void initInfoWindowAdapter(final View view) {
        mAmap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    @Override
    void mapClearOverlay() {
        if (mAmap != null) {
            mAmap.clear();
        }
    }

    @Override
    void changeLocation(MyLatLng myLatLng) {
        changeLocation(myLatLng.toALatLng());
    }

    @Override
    void changeLocationByBound(MyLatLng myLatLng) {
        changeLocation(myLatLng.toALatLng(), 14);
    }

    private void changeLocation(LatLng latLng) {
        changeLocation(latLng, Constants.AMAP_ZOOM);
    }

    private void changeLocation(LatLng latLng, int zoomType) {
        mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(getOBDCorrectPos(latLng), zoomType));
    }

    @Override
    boolean changeMapType(boolean isNormalType) {
        if (isNormalType) {
            mAmap.setMapType(AMap.MAP_TYPE_SATELLITE);
        } else {
            mAmap.setMapType(AMap.MAP_TYPE_NORMAL);
        }
        return !isNormalType;
    }

    @Override
    void addCircleOverlay(MyLatLng myLatLng, int radius) {
        if (myLatLng == null) {
            return;
        }
        CircleOptions options = new CircleOptions()
                .fillColor(Constants.LOCATION_FILL_COLOR)
                .center(myLatLng.toALatLng(true))
                .strokeWidth(1)
                .strokeColor(Constants.LOCATION_STROKE_COLOR)
                .radius(radius);
        mAmap.addCircle(options);
    }


    @Override
    void setMarker(MyLatLng myLatLng, View fromView) {

        setMarker(myLatLng, BitmapDescriptorFactory.fromView(fromView));
    }

    @Override
    String addMarker(PeripherDetail detail, View fromView, OnInfoWindowChangedListener listener) {
        if (detail == null) {
            return "";
        }
        BitmapDescriptor descriptor =
                fromView == null ?
                        BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding) :
                        BitmapDescriptorFactory.fromView(fromView);
        Marker marker = addMarker(detail, descriptor);
        if (marker != null) {
            if (listener != null) {
                initMarkersListener(listener);
            }
            marker.showInfoWindow();
            return marker.getId();
        }
        return "";

    }

    /**
     * 绘制中心圆圈marker
     */
    @Override
    void addMarker(MyLatLng myLatLng, float anchor) {
        if (myLatLng == null || anchor < 0 || anchor > 1) {
            return;
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .position(myLatLng.toALatLng(true))
                .anchor(0.5f, anchor)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_dot_banyuan_google))
                .draggable(false);
        mAmap.addMarker(markerOptions);
    }

    @Override
    void setBoundByLatlngs(List<MyLatLng> latlngs, int bound) {
        if (latlngs == null || latlngs.size() == 0) {
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MyLatLng latLng : latlngs) {
            builder.include(latLng.toALatLng());
        }

        mAmap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), bound));
    }

    private Marker addMarker(PeripherDetail detail, BitmapDescriptor bitmapDescriptor) {
        if (detail == null || bitmapDescriptor == null) {
            return null;
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .position(detail.toLatLng().toALatLng(detail.hasConvert))
                .setInfoWindowOffset(0, -15)
                .title(detail.name)
                .snippet(detail.address)
                .icon(bitmapDescriptor)
                .draggable(false);
        return mAmap.addMarker(markerOptions);
    }

    @Override
    void addMarkers(List<PeripherDetail> detailList, View fromView, OnInfoWindowChangedListener listener) {
        if (detailList == null || detailList.size() == 0 || fromView == null || listener == null) {
            return;
        }
        final BitmapDescriptor descriptor = BitmapDescriptorFactory.fromView(fromView);
        for (PeripherDetail detail : detailList) {
            addMarker(detail, descriptor);
        }
        initMarkersListener(listener);
    }


    private void initMarkersListener(final OnInfoWindowChangedListener listener) {
        mAmap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });

        mAmap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                PeripherDetail detail = new PeripherDetail();
                detail.name = marker.getTitle();
                detail.address = marker.getSnippet();
                return listener.OnInfoWindowChanged(detail);
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    private void setMarker(MyLatLng myLatLng, BitmapDescriptor descriptor) {
        if (myLatLng == null) {
            return;
        }
        if (descriptor == null) {
        }

        if (mLocationMarker == null || mLocationMarker.isRemoved()) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(myLatLng.toALatLng())
                    .setInfoWindowOffset(0, -15)
                    .draggable(false);
            markerOptions.icon(descriptor);

            mLocationMarker = mAmap.addMarker(markerOptions);
        } else {
            mLocationMarker.setPosition(mLocationMarker.getPosition());
        }
    }


    // FIXME: 2019/6/28 wait to check...
    @Override
    void removeMarker() {
        if (mLocationMarker != null) {
            mLocationMarker.remove();
        }
    }

    @Override
    void setGeoSearchCallback(double lat, double lng, final MyGeocodeCallback callback) {
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(lat, lng), 50, GeocodeSearch.GPS);
        GeocodeSearch search = new GeocodeSearch(App.getContext());
        search.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                if (callback != null && regeocodeResult != null && i == 1000) {
                    callback.onGetAddressSucceed(regeocodeResult.getRegeocodeAddress().getFormatAddress());
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
        search.getFromLocationAsyn(query);
    }


    @Override
    void showInfoWindow(View view, MyLatLng myLatLng) {
        if (!mHasInitInfoWindow) {
            initInfoWindowAdapter(view);
            mHasInitInfoWindow = true;
        }
        if (mLocationMarker != null) {
            mLocationMarker.showInfoWindow();
        }
    }

    @Override
    void hideInfoWindow() {
        if (mLocationMarker != null) {
            mLocationMarker.hideInfoWindow();
        }
    }

    private LatLng getOBDCorrectPos(LatLng latLng) {
//        if (latLng != null && isNeedOffset && App.getTrackerType() == TrackerConstant.VALUE_RANGE_OBD) {
//            return new LatLng(latLng.latitude - 0.0037, latLng.longitude);
//        }
        return latLng;
    }


    @Override
    MyLatLng mapPointConvert2Wgs84(MyLatLng myLatLng) {
        return myLatLng.toAmapWgs84Point();
    }

    @Override
    double getDistance(MyLatLng src, MyLatLng dst) {
        if (src == null || dst == null) {
            return 0;
        }
        return Utils.getDistance(src.longitude, src.latitude, dst.longitude, dst.latitude);
    }

    @Override
    void searchNearby(String keyWord, MyLatLng location) {
        PoiSearch.Query query = new PoiSearch.Query(keyWord, "", "");
        LatLng latLng = location.toALatLng();
        query.setPageSize(50);
        query.setPageNum(1);
        mPoiSearch.setQuery(query);
        mPoiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latLng.latitude, latLng.longitude), 4000));
        mPoiSearch.searchPOIAsyn();
    }

    @Override
    void setPoiSearchListener(Context context, final MyGetPoiSearchResultListener listener) {
        mPoiSearch = new PoiSearch(context, null);
        mPoiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {
                if (i != 1000) {
                    listener.onGetPoiSucceed(false, null);
                    return;
                }
                List<PoiItem> poiItemList = poiResult.getPois();
                if (poiItemList != null || poiItemList.size() != 0) {
                    List<PeripherDetail> detailList = new ArrayList<>();
                    for (PoiItem item : poiItemList) {
                        PeripherDetail detail = new PeripherDetail();
                        detail.address = item.getSnippet();
                        detail.name = item.getTitle();
                        detail.latitude = item.getLatLonPoint().getLatitude();
                        detail.longitude = item.getLatLonPoint().getLongitude();
                        detail.distance = item.getDistance();
                        detail.hasConvert = true;
                        detailList.add(detail);
                    }
                    listener.onGetPoiSucceed(true, detailList);
                    return;
                }
                listener.onGetPoiSucceed(false, null);
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });
    }

    @Override
    void setOnMapClickListener(final MyMapClickListener listener) {

        if (listener != null) {
            mAmap.setOnMapClickListener(new AMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    listener.onClick(MyLatLng.from(latLng));
                }
            });
        }
    }

    @Override
    void setOnMarkerClickListener(final MyMarkerClickListener listener) {
        if (listener != null) {
            mAmap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    listener.onMarkClick(marker.getId());
                    return true;
                }
            });
        }
    }

    @Override
    void mapAddRouteOverlay(int range, CurrentGPS... routeGPSList) {
        mapAddRouteOverlayWithLine(range, false, routeGPSList);
    }

    @Override
    void mapAddRouteOverlayWithLine(int range, boolean addLine, CurrentGPS... routeGPSList) {
        if (routeGPSList == null || routeGPSList.length == 0) {
            return;
        }
        mapClearOverlay();
        int size = routeGPSList.length;
        BitmapDescriptor bitmapDescriptorStart = BitmapDescriptorFactory.fromResource(R.drawable.icon_track_start);
        BitmapDescriptor bitmapDescriptorEnd = BitmapDescriptorFactory.fromResource(R.drawable.icon_track_end);
        BitmapDescriptor bitmapDescriptorTrack = BitmapDescriptorFactory.fromResource(getTrackerIconByRange(range));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<LatLng> latLngs = new ArrayList<>();
        int index = 0;
        for (CurrentGPS currentGPS : routeGPSList) {

            LatLng latLng = MyLatLng.from(currentGPS.lat, currentGPS.lng).toALatLng();
            if (latLng == null) {
                continue;
            }
            builder.include(latLng);
            if (addLine) {
                latLngs.add(latLng);
            }

            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                    .rotateAngle(360 - currentGPS.direction)
                    .setInfoWindowOffset(0, -15)
                    .draggable(false);
            // 只有一个坐标的话就用普通标志就好了
            if (size == 1) {
                markerOptions.icon(bitmapDescriptorTrack);
            } else {
                if (index == 0) {
                    markerOptions.icon(bitmapDescriptorStart);
                } else if (index == size - 1) {
                    markerOptions.icon(bitmapDescriptorEnd);
                } else {
                    markerOptions.icon(bitmapDescriptorTrack);
                }
            }
            if (range == TrackerConstant.VALUE_RANGE_PET) {
                markerOptions.title(currentGPS.collect_datetime + " " + App.getContext().getString(R.string.speed_unit, String.valueOf(currentGPS.speed)));
            }

            mAmap.addMarker(markerOptions);
            index++;
        }
        if (addLine) {
            PolylineOptions options = new PolylineOptions().addAll(latLngs).color(0xAAFF0000);
            mAmap.addPolyline(options);
        }
        final LatLngBounds build = builder.build();
        mAmap.animateCamera(CameraUpdateFactory.newLatLngBounds(build, 50));
        if (!addLine) {
            mAmap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    marker.showInfoWindow();
                    return true;
                }
            });
        }
    }

    @Override
    void drawTracker(List<CurrentGPS> trackList) {
        // 少于两个点画不成线
        if (trackList == null || trackList.size() < 2) {
            return;
        }
        mapClearOverlay();
        CurrentGPS currentStart = trackList.get(0);
        CurrentGPS currentEnd = trackList.get(trackList.size() - 1);
        MarkerOptions markerOptionsStart = new MarkerOptions().position(MyLatLng.from(currentStart.lat, currentStart.lng).toALatLng()).setInfoWindowOffset(0, -15).draggable(false);
        MarkerOptions markerOptionsEnd = new MarkerOptions().position(MyLatLng.from(currentEnd.lat, currentEnd.lng).toALatLng()).setInfoWindowOffset(0, -15).draggable(false);
        markerOptionsStart.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_track_start));
        markerOptionsEnd.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_track_end));
        mAmap.addMarker(markerOptionsStart);
        mAmap.addMarker(markerOptionsEnd);

        List<LatLng> latLngs = new ArrayList<>();
        List<Integer> colorList = new ArrayList<>();
        int lastOverSpeed = 0;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int index = 0;

        for (CurrentGPS currentGPS : trackList) {
            LatLng latLng = MyLatLng.from(currentGPS.lat, currentGPS.lng).toALatLng();
            if (latLng == null) {
                continue;
            }
            latLngs.add(latLng);
            builder.include(latLng);
            if (index > 0) {
                if (currentGPS.overSpeedFlag == 1 && lastOverSpeed == 1) {
                    colorList.add(Color.RED);
                } else {
                    colorList.add(Constants.LOCATION_STROKE_COLOR);
                }
            }

            lastOverSpeed = currentGPS.overSpeedFlag;
            index++;
        }
        final LatLngBounds build = builder.build();
        PolylineOptions options = new PolylineOptions().addAll(latLngs).colorValues(colorList);
        mAmap.addPolyline(options);
        mAmap.animateCamera(CameraUpdateFactory.newLatLngBounds(build, 50));
    }


    private void onUiSettings() {
        if (mSettings == null) {
            mSettings = mAmap.getUiSettings();
        }
        mSettings.setScaleControlsEnabled(true);
        mSettings.setZoomControlsEnabled(false);

    }

    @Override
    MyLatLng gpsConvert2MapPoint(MyLatLng myLatLng) {
        if (myLatLng != null) {
            return MyLatLng.from(myLatLng.toALatLng());
        }
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }
}
