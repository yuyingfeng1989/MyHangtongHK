package com.bluebud.obd_optimize.fragment;

import android.support.v4.app.Fragment;


/**
 * Created by shan_yao on 2016/6/17.
 */
public class FragmentFactory {
    public static Fragment createForNoExpand(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ObdDailyFragment();
                break;
            case 1:
                fragment = new ObdWeeksFragment();
                break;
            case 2:
                fragment = new ObdMonthFragment();
                break;
        }
        return fragment;
    }
}
