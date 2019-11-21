package com.bluebud.map;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bluebud.app.App;
import com.bluebud.info.Tracker;
import com.bluebud.map.model.AbstractMapModel;
import com.bluebud.map.model.MyMapPresenter;

public class TrackManager{

    private MyMapPresenter mPresenter;

    public static TrackManager newInstance(Context context) {
        return new TrackManager(context);
    }

    private TrackManager(Context context) {
        mPresenter = new MyMapPresenter(context, App.getMapType());
    }

    public void addView(Activity activity, int viewId, AbstractMapModel.MyMapReadyCallback callback) {
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

        rootView.addView(view, params);
    }

    public MyMapPresenter getMapPresenter() {
        return mPresenter;
    }
}
