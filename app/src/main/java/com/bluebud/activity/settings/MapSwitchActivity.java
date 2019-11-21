package com.bluebud.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.bluebud.activity.BaseActivity;
import com.bluebud.activity.MainActivity;
import com.bluebud.app.App;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ToastUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

//地图切换
public class MapSwitchActivity extends BaseActivity implements
        OnClickListener, OnCheckedChangeListener {
    private RadioGroup radioGroup;
    private int[] iRbtnIds = {R.id.rbtn_amap, R.id.rbtn_google_map};
    private int mCurrentMap;
    private int mLastMap;
    private final static int TIMING = 1 * 700;
    private long mLastClickTime = System.currentTimeMillis();
    private ProgressBar bar;
    private Handler timingHandler = new Handler();
    private Runnable timingRunnable = new Runnable() {
        @Override
        public void run() {
            bar.setVisibility(View.GONE);
            startActivity(new Intent(MapSwitchActivity.this, MainActivity.class));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_map_swatch);
        init();
    }

    public void init() {
        setBaseTitleText(R.string.map_switch);
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleRightText(R.string.submit1);
        setBaseTitleRightTextVisible(View.VISIBLE);
        getBaseTitleRightText().setOnClickListener(this);
        getBaseTitleLeftBack().setOnClickListener(this);
        bar = findViewById(R.id.pb_loading);
        radioGroup = findViewById(R.id.group);
        radioGroup.setOnCheckedChangeListener(this);

        radioGroup.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtil.i("V" + v.getId());

            }
        });

        int mapType = UserSP.getInstance().getServerAndMap(this);
        mCurrentMap = iRbtnIds[mapType];
        mLastMap = mCurrentMap;
        radioGroup.check(mCurrentMap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timingHandler.removeCallbacks(timingRunnable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.rl_title_right_text://提交
                mapSwitch();
                break;
        }
    }

    int i = 0;

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        LogUtil.i("checkedId=" + checkedId);
        LogUtil.i("checkedId1====" + getIndexFromId(checkedId));
        mLastMap = checkedId;
    }

    private int getIndexFromId(int checkedId) {
        int len = iRbtnIds.length;
        for (int i = 0; i < len; i++) {
            if (checkedId == iRbtnIds[i])
                return i;
        }
        return -1;
    }


    private void mapSwitch() {

        if (mCurrentMap == mLastMap) {
            return;
        }

        if (isFastDoubleClick()) {
            LogUtil.i("太快了，别点了");
            return;
        }
        bar.setVisibility(View.VISIBLE);

        int mapType = getIndexFromId(mLastMap);
        if (mapType == App.MAP_TYPE_GMAP && ConnectionResult.SUCCESS
                != GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext())) {
            bar.setVisibility(View.GONE);
            ToastUtil.show(this, R.string.no_suppert_google);
            return;
        }

        sendBroadcast(new Intent(Constants.ACTION_MAP_SWITCH));
        UserSP.getInstance().saveServerAndMap(MapSwitchActivity.this, getIndexFromId(mLastMap));
        timingHandler.postDelayed(timingRunnable, TIMING);//一秒后再进去主页面，是为了防止地图没有完全销毁
        bar.setVisibility(View.GONE);
    }


    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long slotT = time - mLastClickTime;
        LogUtil.i("间隔时间：" + slotT);
        mLastClickTime = time;
        if (0 < slotT && slotT < 1200) {
            return true;
        }
        return false;
    }

}
