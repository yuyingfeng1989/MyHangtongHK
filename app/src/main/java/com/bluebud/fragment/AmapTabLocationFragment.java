package com.bluebud.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.bluebud.app.App;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.bean.MyLatLng;
import com.bluebud.map.model.MyMapPresenter;
import com.bluebud.utils.ToastUtil;


/**
 * 高德地图fragment
 */
public class AmapTabLocationFragment extends AbstractTabLocationMapFragment {


    @Override
    protected MyMapPresenter onCreateMapPresenter(Context context) {
        return new MyMapPresenter(context, MyMapPresenter.MAP_TYPE_AMAP);
    }


    @Override
    protected void resetMarkerView() {
        vTrackerLocationDot = null;
        initLocationMapInfoPoint();
    }

    @Override
    protected MyLatLng getCorrectCarLatLng(double lat, double lng) {
        return MyLatLng.from(lat, lng);
    }

    private boolean isAMapsInstalled() {
        try {
            App.getContext().getPackageManager().getApplicationInfo("com.autonavi.minimap", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void toNavigator() {
        if (isAMapsInstalled()) {// 是否安装了高德
            Intent intents = new Intent();
            double lat = curPointLocation.toALatLng().latitude;
            double lng = curPointLocation.toALatLng().longitude;
            intents.setData(Uri.parse("androidamap://navi?sourceApplication=nyx_super&lat=" + lat + "&lon=" + lng + "&dev=0&style=2"));
            startActivity(intents); // 启动调用
        } else {
            ToastUtil.show(getActivity(), R.string.install_gaode_map);
        }
    }
}
