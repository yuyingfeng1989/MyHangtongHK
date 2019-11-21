package com.bluebud.app;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.bluebud.constant.TrackerConstant;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.liteguardian_hk.BuildConfig;
import com.bluebud.utils.UserUtil;

public class App {

    public static final int MAP_TYPE_AMAP = 0; // 高德地图
    public static final int MAP_TYPE_GMAP = 1; // 谷歌地图
    public static final int MAP_TYPE_BMAP = 2; // 百度地图


    public static Context getContext() {
        return AppApplication.getContext();
    }

    public static boolean isDebug() {
//        return BuildConfig.DEBUG;
        return true;
    }

    public static int getMapType() {
        return UserSP.getInstance().getServerAndMap(getContext());
    }

    public static int getTrackerType() {
        if (UserUtil.getCurrentTracker() != null) {
            return UserUtil.getCurrentTracker().ranges;
        }
        return TrackerConstant.VALUE_RANGE_PERSON;
    }


    public static int getScreenWidth() {
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (manager != null) {
            Point size = new Point();
            Display display = manager.getDefaultDisplay();
            display.getSize(size);
            return size.x;
        }
        return 0;
    }

    public static int getScreenHeigh() {
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (manager != null) {
            Point size = new Point();
            Display display = manager.getDefaultDisplay();
            display.getSize(size);
            return size.y;
        }
        return 0;
    }


}
