package com.bluebud.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.PeripherDetail;
import com.bluebud.info.User;
import com.bluebud.utils.Constants;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.UserUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class PeripherDetailGoogleTotalActivity extends BaseFragmentActivity implements
        OnClickListener, InfoWindowAdapter, OnMarkerClickListener {
    private ImageView ivBack;
//    private String m_Dtime;
//    private double speed;
//    private int type = 1;

    private GoogleMap mMap;
    //    private Marker markerLocation;
    private View mInfoWindowContent;
    //    private String address;
//    private String name;
    private List<Marker> routeMarkers = new ArrayList<Marker>();
    private List<PeripherDetail> peripherDetailList;

    private LatLng ll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_periper_detail_google);
        getData();
        init();
    }

    @SuppressWarnings("unchecked")
    private void getData() {
        peripherDetailList = (List<PeripherDetail>) getIntent().getSerializableExtra("peripherDetailList");
        if (peripherDetailList != null) {
            LogUtil.i("peripherDetailList size:" + peripherDetailList.size());
        } else {
            LogUtil.i("peripherDetailList is null");
        }

    }

    private void init() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        if (peripherDetailList != null) {
            int size = peripherDetailList.size();
            LogUtil.i("peripherDetailList size =" + size + "size/2=" + size / 2);
            ll = new LatLng(peripherDetailList.get(size / 2).latitude, peripherDetailList.get(size / 2).longitude);
        }
        initMap();
        if (null != mMap) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, Constants.GOOGLE_ZOOM));
        }
    }

    /**
     * 清除地图图层
     */
    public void mapClearOverlay() {
        if (null != mMap) {
            mMap.clear();
        }
    }

    public void mapAddRouteOverlay() {
        mapClearOverlay();
        routeMarkers.clear();
        int size = peripherDetailList.size();
        for (int i = 0; i < size; i++) {
            LatLng markerLocation = new LatLng(peripherDetailList.get(i).latitude, peripherDetailList.get(i).longitude);
            MarkerOptions markerOptions = new MarkerOptions().position(markerLocation)
                    .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_gcoding));
            Marker marker = mMap.addMarker(markerOptions);
            routeMarkers.add(marker);

        }
    }


    private void initMap() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        for (int i = 0; i < routeMarkers.size(); i++) {
            if (marker.equals(routeMarkers.get(i))) {
                marker.showInfoWindow();
            }
        }

        return false;
    }


    private void setUpMap() {
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setInfoWindowAdapter(this);
        mMap.setOnMarkerClickListener(this);

        User user = UserUtil.getUserInfo(this);
        if (0 != user.lat && 0 != user.lng) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    user.lat, user.lng), Constants.GOOGLE_ZOOM));
        }
        mapAddRouteOverlay();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DialogUtil.dismiss();
    }

    @Override
    protected void onResume() {
        initMap();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        if (mInfoWindowContent == null) {
            mInfoWindowContent = LayoutInflater.from(this).inflate(
                    R.layout.map_pop_info1, null);
        }
        if (peripherDetailList != null) {
            for (int i = 0; i < peripherDetailList.size(); i++) {
                if (marker.equals(routeMarkers.get(i))) {
                    TextView infoTitle = (TextView) mInfoWindowContent
                            .findViewById(R.id.map_info_title);
                    infoTitle.setText(peripherDetailList.get(i).name);
                    TextView infoSnippet = (TextView) mInfoWindowContent
                            .findViewById(R.id.map_info_snippet);
                    infoSnippet.setText(peripherDetailList.get(i).address);

                }
            }
        }
        return mInfoWindowContent;
    }

}
