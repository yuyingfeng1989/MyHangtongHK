package com.bluebud.map;

import android.os.Bundle;

/**
 * 地图生命周期接口
 */

public interface MapLifecycleListener {

    void onCreate(Bundle savedInstanceState);

    void onResume();

    void onPause();

    void onDestroy();

    void onSaveInstanceState(Bundle outState);

}
