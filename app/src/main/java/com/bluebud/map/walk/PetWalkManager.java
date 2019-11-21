package com.bluebud.map.walk;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bluebud.app.App;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.bean.MyLatLng;
import com.bluebud.map.model.AbstractMapModel;
import com.bluebud.map.model.MyMapPresenter;

public class PetWalkManager{

    private static final int LAYOUT_INDEX_MAP_CONTENT = 1;

    private MyMapPresenter mPresenter;

    private MyLatLng mLocation;

    private PetWalkManager(Context context) {
        mPresenter = new MyMapPresenter(context, App.getMapType());
    }

    public static PetWalkManager newInstance(Context context) {
        return new PetWalkManager(context);
    }

    public void addMapView(Activity activity, int viewId, AbstractMapModel.MyMapReadyCallback callback) {
        if (activity == null || activity.isDestroyed() || activity.isFinishing() || viewId == 0) {
            return;
        }

        if (App.getMapType() == App.MAP_TYPE_GMAP) {
            mPresenter.initMapView(activity, viewId, callback);
            return;
        }

        ViewGroup rootView = activity.findViewById(viewId);
        View view = mPresenter.getMapView(null);
        if (rootView == null || view == null) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
//        params.addRule(RelativeLayout.BELOW, R.id.rl_date_setting);
        rootView.addView(view, params);
        if (callback != null) {
            callback.onMapReady();
        }

    }


    public MyMapPresenter getPresenter() {
        return mPresenter;
    }

}
