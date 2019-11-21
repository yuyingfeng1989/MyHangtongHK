package com.bluebud.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.http.HttpClientGoogleGeocode;
import com.bluebud.info.User;
import com.bluebud.utils.Constants;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.UserUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PeripherDetailGoogleActivity extends BaseFragmentActivity implements
        OnClickListener, InfoWindowAdapter {
    private ImageView ivBack;
//    private String m_Dtime;
//    private double speed;
//    private int type = 1;

    private GoogleMap mMap;
    private Marker markerLocation;
    private View mInfoWindowContent;
    private String address;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_periper_detail_google);

        init();
    }

    private void init() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);

        double lat = getIntent().getExtras().getDouble("lat");
        double lng = getIntent().getExtras().getDouble("lng");
        address = getIntent().getStringExtra("address");
        name = getIntent().getStringExtra("name");
        LatLng ll = new LatLng(lat, lng);

        initMap();

        if (null != mMap) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, Constants.GOOGLE_ZOOM));

            MarkerOptions markerOptions = new MarkerOptions().position(ll)
                   .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_gcoding));
//			markerOptions = Utils.setMarkerOptions2Google(0,type, 0,
//					markerOptions);
            markerLocation = mMap.addMarker(markerOptions);
            markerLocation.showInfoWindow();

            getCodeFromLatLng(ll);
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

    private void setUpMap() {
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setInfoWindowAdapter(this);

        User user = UserUtil.getUserInfo(this);
        if (0 != user.lat && 0 != user.lng) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    user.lat, user.lng), Constants.GOOGLE_ZOOM));
        }
    }

    private void getCodeFromLatLng(LatLng latLng) {
        HttpClientGoogleGeocode httpClientGoogleGeocode = new HttpClientGoogleGeocode();
        httpClientGoogleGeocode.getFromLocation(
                HttpClientGoogleGeocode.MODE_LOCATION, latLng,
                mHandlerGoogleMap);
    }

    private Handler mHandlerGoogleMap = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HttpClientGoogleGeocode.SUCCESS_LOCATION:
                    markerLocation.setSnippet(msg.obj.toString());
                    markerLocation.showInfoWindow();
                    break;
            }
            super.handleMessage(msg);
        }
    };

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
        TextView infoTitle = (TextView) mInfoWindowContent
                .findViewById(R.id.map_info_title);
        infoTitle.setText(name);
        TextView infoSnippet = (TextView) mInfoWindowContent
                .findViewById(R.id.map_info_snippet);
        infoSnippet.setText(address);
        return mInfoWindowContent;
    }

}
