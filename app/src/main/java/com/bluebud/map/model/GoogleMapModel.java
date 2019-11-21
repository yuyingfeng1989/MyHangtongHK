package com.bluebud.map.model;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.bluebud.app.App;
import com.bluebud.constant.TrackerConstant;
import com.bluebud.http.HttpClientGoogleGeocode;
import com.bluebud.info.CurrentGPS;
import com.bluebud.info.PeripherDetail;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.bean.MyGeocodeCallback;
import com.bluebud.map.bean.MyLatLng;
import com.bluebud.map.poi.MyGetPoiSearchResultListener;
import com.bluebud.utils.Constants;
import com.bluebud.utils.Utils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class GoogleMapModel extends AbstractMapModel implements GoogleApiClient.ConnectionCallbacks, LocationListener {

    private GoogleMap mGmap;
    private Marker mLocationMarker;
    private Marker mMyLocation;
    private LatLng mLocation;
    private boolean mHasInitInfoWindow;
    private GoogleApiClient mClient;
    private boolean mNeedLocation;
    private boolean mNeedShowLocation;

    GoogleMapModel(Context context) {
        initLocationClient(context);
    }


    @Override
    public View getMapView(Context context) {
        return null;
    }

    @Override
    public void initMapView(final Activity activity, int contentId, final MyMapReadyCallback callback) {
        MapFragment mapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                activity.getFragmentManager().beginTransaction();
        fragmentTransaction.add(contentId, mapFragment);
        fragmentTransaction.commit();

        mapFragment.getMapAsync(new OnMapReadyCallback() {/*异步加载google地图*/
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                mGmap = googleMap;
                mGmap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        marker.hideInfoWindow();
                    }
                });
                mGmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        changeLocation(MyLatLng.from(marker.getPosition()));
                        marker.showInfoWindow();
                        return true;
                    }
                });

                if (mLocation != null) {
                    if (mNeedLocation) {
                        mGmap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, Constants.GOOGLE_ZOOM));
                        mNeedLocation = false;
                    }
                    if (mNeedShowLocation) {
                        addLocationMarker(MyLatLng.from(mLocation));
                        mNeedShowLocation = false;
                    }
                }

                if (callback != null) {
                    callback.onMapReady();
                }
            }
        });
    }

    @Override
    void locateMyWay() {
        mNeedLocation = true;
        if (!hasInitialized()) {
            return;
        }

        if (mLocation != null) {
            mGmap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, Constants.GOOGLE_ZOOM));
        }
    }

    private void initLocationClient(Context context) {
        if (mClient == null) {
            mClient = new GoogleApiClient
                    .Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .build();
            mClient.connect();
        } else {
            mClient.reconnect();
        }
    }

    @Override
    public void showMyLocation(boolean showMyLocation) {
        mNeedShowLocation = showMyLocation;
        if (showMyLocation) {
            if (hasInitialized() && mLocation != null) {
                addLocationMarker(MyLatLng.from(mLocation));
                mGmap.animateCamera(CameraUpdateFactory.zoomTo(Constants.GOOGLE_ZOOM));
            }
        } else {
            if (mMyLocation != null) {
                mMyLocation.remove();
            }
        }
    }


    @Override
    void setGeoSearchCallback(double lat, double lng, MyGeocodeCallback callback) {
        LatLng ll = MyLatLng.from(lat, lng).toGLatLng();
        HttpClientGoogleGeocode httpClientGoogleGeocode = new HttpClientGoogleGeocode();
        httpClientGoogleGeocode.getFromLocation(ll, callback);
    }

    @Override
    boolean hasInitialized() {
        return mGmap != null;
    }

    @Override
    protected void onInitMap(Context context) {

    }

    @Override
    void mapClearOverlay() {
        if (mGmap != null) {
            mGmap.clear();
        }
        mLocationMarker = null;
    }

    @Override
    void changeLocation(MyLatLng myLatLng) {
        if (mGmap != null) {
            mGmap.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(getOBDCorrectPos(myLatLng.toGLatLng()), Constants.GOOGLE_ZOOM));
        }
    }

    @Override
    void changeLocationByBound(MyLatLng myLatLng) {

    }

    @Override
    boolean changeMapType(boolean isNormalType) {
        if (isNormalType) {
            mGmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            mGmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        return !isNormalType;
    }

    @Override
    void addCircleOverlay(MyLatLng myLatLng, int radius) {
        CircleOptions options = new CircleOptions()
                .fillColor(Constants.LOCATION_FILL_COLOR)
                .center(myLatLng.toGLatLng(true))
                .strokeWidth(1)
                .strokeColor(Constants.LOCATION_STROKE_COLOR)
                .radius(radius);
        mGmap.addCircle(options);
    }

    /**
     * 把视图布局转换成Bitmap
     * 定位点
     */
    private Bitmap viewToBitmap(View addViewContent) {
        if (addViewContent == null) {
            return null;
        }
        DisplayMetrics displayMetrics = App.getContext().getResources().getDisplayMetrics();
        addViewContent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addViewContent.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        addViewContent.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        addViewContent.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(addViewContent.getMeasuredWidth(), addViewContent.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        addViewContent.draw(canvas);
        return bitmap;
    }

    @Override
    void setMarker(MyLatLng myLatLng, View fromView) {
        if (myLatLng == null) {
            return;
        }

        if (mLocationMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .infoWindowAnchor(0.5f, -0.15f)
                    .position(myLatLng.toGLatLng())
                    .icon(BitmapDescriptorFactory.fromBitmap(viewToBitmap(fromView)))
                    .draggable(false);

            mLocationMarker = mGmap.addMarker(markerOptions);
        } else {
            mLocationMarker.setPosition(myLatLng.toGLatLng());
        }
    }

    /**
     * 头像定位点
     */
    @Override
    String addMarker(PeripherDetail detail, View fromView, OnInfoWindowChangedListener listener) {
        if (detail == null) {
            return "";
        }
        BitmapDescriptor descriptor =
                fromView == null ?
                        BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding) :
                        BitmapDescriptorFactory.fromBitmap(viewToBitmap(fromView));
        Marker marker = addMarker(detail, descriptor);
        if (marker != null) {
            setInfoWindowMovedListener(marker);
            return marker.getId();
        }
        return "";
    }


    private Marker addMarker(PeripherDetail detail, BitmapDescriptor bitmapDescriptor) {
        if (detail == null || bitmapDescriptor == null) {
            return null;
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .position(detail.toLatLng().toGLatLng())
                .infoWindowAnchor(0.5f, -0.15f)
                .title(detail.name)
                .snippet(detail.address)
                .icon(bitmapDescriptor)
                .draggable(false);
        return mGmap.addMarker(markerOptions);
    }

    @Override
    void addMarker(MyLatLng myLatLng, float anchor) {
        if (myLatLng == null || anchor < 0 || anchor > 1) {
            return;
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .position(myLatLng.toGLatLng(true))
                .anchor(0.5f, anchor)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_dot_banyuan_google))
                .draggable(false);
        mGmap.addMarker(markerOptions);
    }

    @Override
    void setBoundByLatlngs(List<MyLatLng> latlngs, int bound) {
        if (latlngs == null || latlngs.size() == 0) {
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MyLatLng latLng : latlngs) {
            builder.include(latLng.toGLatLng());
        }

        mGmap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), App.getScreenWidth(), App.getScreenHeigh(), bound));
    }

    @Override
    void addMarkers(List<PeripherDetail> detailList, View fromView, OnInfoWindowChangedListener listener) {

    }


    @Override
    void removeMarker() {
        if (mLocationMarker != null) {
            mLocationMarker.remove();
        }
    }

    @Override
    void showInfoWindow(View view, final MyLatLng myLatLng) {
        if (!mHasInitInfoWindow) {
            initInfoWindowAdapter(view);
            mHasInitInfoWindow = true;
        }
        if (mLocationMarker != null) {
            // 正常展现infoWindow的话在地图上会出现一闪而过的动画，设置监听器让镜头移到给定坐标的地方再进行展示
            setInfoWindowMovedListener(mLocationMarker);
        }
    }

    private void setInfoWindowMovedListener(final Marker marker) {
        if (marker == null) {
            return;
        }
        mGmap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if ((int) (cameraPosition.target.latitude * 10000) == (int) (marker.getPosition().latitude * 10000)) {
                    marker.showInfoWindow();
                }
            }
        });
    }

    private void initInfoWindowAdapter(final View view) {
        mGmap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return view;
            }
        });
    }

    @Override
    void hideInfoWindow() {
        if (mLocationMarker != null) {
            mLocationMarker.hideInfoWindow();
        }
    }

    private LatLng getOBDCorrectPos(LatLng myLatLng) {
//        if (myLatLng != null && isNeedOffset && App.getTrackerType() == TrackerConstant.VALUE_RANGE_OBD) {
//            return new LatLng(myLatLng.latitude - 0.006, myLatLng.longitude);
//        }
        return myLatLng;
    }


    @Override
    MyLatLng mapPointConvert2Wgs84(MyLatLng myLatLng) {
        return myLatLng;
    }

    @Override
    double getDistance(MyLatLng src, MyLatLng dst) {
        return Utils.getDistance(src.longitude, src.latitude, dst.longitude, dst.latitude);
    }

    @Override
    void searchNearby(String keyWord, MyLatLng location) {

    }

    @Override
    void setPoiSearchListener(Context context, MyGetPoiSearchResultListener listener) {

    }

    @Override
    void setOnMapClickListener(final MyMapClickListener listener) {
        mGmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                listener.onClick(MyLatLng.from(latLng));
            }
        });

    }

    @Override
    void setOnMarkerClickListener(final MyMarkerClickListener listener) {
        mGmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (listener != null) {
                    listener.onMarkClick(marker.getId());
                }
                return true;
            }
        });
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

            LatLng latLng = MyLatLng.from(currentGPS.lat, currentGPS.lng).toGLatLng();
            if (latLng == null) {
                continue;
            }
            if (addLine) {
                latLngs.add(latLng);
            }
            builder.include(latLng);

            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                    .rotation(360 - currentGPS.direction).draggable(false);
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
                markerOptions.title(currentGPS.collect_datetime + " "
                        + App.getContext().getString(R.string.speed_unit, String.valueOf(currentGPS.speed)));
            }
            mGmap.addMarker(markerOptions);
            index++;
        }

        if (addLine) {
            PolylineOptions options = new PolylineOptions()
                    .addAll(latLngs).color(0xAAFF0000).width(6);
            mGmap.addPolyline(options);
        }
        final LatLngBounds build = builder.build();
        mGmap.animateCamera(CameraUpdateFactory.newLatLngBounds(build, 20));

        if (!addLine) {
            mGmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
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
        CurrentGPS currentEnd = trackList.get(trackList.size()-1);
        MarkerOptions markerOptionsStart = new MarkerOptions().position(MyLatLng.from(currentStart.lat, currentStart.lng).toGLatLng()).draggable(false);
        MarkerOptions markerOptionsEnd = new MarkerOptions().position(MyLatLng.from(currentEnd.lat, currentEnd.lng).toGLatLng()).draggable(false);
        markerOptionsStart.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_track_start));
        markerOptionsEnd.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_track_end));
        mGmap.addMarker(markerOptionsStart);
        mGmap.addMarker(markerOptionsEnd);

        int lastOverSpeed = 0;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng curLatLng;
        LatLng lastLatLng = null;
        int index = 0;
        PolylineOptions options;
        for (CurrentGPS currentGPS : trackList) {
            curLatLng = MyLatLng.from(currentGPS.lat, currentGPS.lng).toGLatLng();
            if (curLatLng == null) {
                continue;
            }
            if (index == 0) {
                builder.include(curLatLng);
            }


            if (index > 0) {
                options = new PolylineOptions()
                        .add(lastLatLng, curLatLng);
                if (currentGPS.overSpeedFlag == 1 && lastOverSpeed == 1) {
                    options.color(Color.RED);
                } else {
                    options.color(Constants.LOCATION_STROKE_COLOR);
                }
                mGmap.addPolyline(options);
            }
            lastOverSpeed = currentGPS.overSpeedFlag;

            lastLatLng = curLatLng;
            index++;
        }

        // 只取开头和结尾两个坐标即可，全部都取会有大量的GC
        if (lastLatLng != null) {
            builder.include(lastLatLng);
        }

        LatLngBounds build = builder.build();
        mGmap.animateCamera(CameraUpdateFactory.newLatLngBounds(build, 200));

    }

    @Override
    MyLatLng gpsConvert2MapPoint(MyLatLng myLatLng) {
        if (myLatLng != null) {
            return MyLatLng.from(myLatLng.toGLatLng());
        }
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest REQUEST = LocationRequest.create().setInterval(5000) // 5 seconds
                .setFastestInterval(16) // 16ms = 60fps
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, REQUEST, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void addLocationMarker(MyLatLng latLng) {
        if (mMyLocation != null) {
            mMyLocation.remove();
        }
//        MarkerOptions options = new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location))
//                .position(latLng.toGLatLng());
//        mMyLocation =  mGmap.addMarker(options);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = MyLatLng.from(location).toGLatLng();
        mClient.disconnect();

        if (!hasInitialized()) {
            return;
        }
        if (mNeedLocation) {
            mGmap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, Constants.GOOGLE_ZOOM));
            mNeedLocation = false;
        }
        if (mNeedShowLocation) {
            addLocationMarker(MyLatLng.from(mLocation));
            mNeedShowLocation = false;
        }
    }

}
