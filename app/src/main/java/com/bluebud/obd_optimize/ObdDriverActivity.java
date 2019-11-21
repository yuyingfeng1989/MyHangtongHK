package com.bluebud.obd_optimize;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.info.DriverDate;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.obd_optimize.fragment.FragmentFactory;
import com.bluebud.obd_optimize.minterface.IDriverInterface;
import com.bluebud.obd_optimize.view.ObdPageIndicator;
import com.bluebud.obd_optimize.view.ObdViewPager;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.utils.request.ObdRequestUtil;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

import io.rong.eventbus.EventBus;

/**
 * Created by Administrator on 2019/3/19.
 */

public class ObdDriverActivity extends FragmentActivity implements View.OnClickListener, IDriverInterface {
    private ObdPageIndicator indicator;
    private ObdViewPager viewPager;
    private RelativeLayout obd_rl_economic;
    private RelativeLayout obd_rl_safe;
    private TextView obd_text_economic;
    private TextView obd_text_safe;
    private TextView obd_scord;
    private TextView obd_tv_state;
    private TextView obd_mileage_value;//行程总公里数
    private TextView obd_oil_value;//行程总油耗
    private TextView obd_average_oil;//平均油耗
    private TextView obd_average_speed;//平均车速
    private TextView obd_travel_time;//行驶时间
    private TextView obd_rapidly_accelerate;//急加速
    private TextView obd_sharp_slowdown;//急减速
    private TextView obd_speed_limit;//超速
    private TextView obd_sharp_turn;//急转弯

    private boolean isSafeDriver;
    private boolean isOldSafeDriver;
    private ObdDriverActivity mContext;
    private Tracker mCurTracker;
    public ObdRequestUtil obdRequestUtil;
    private int oldPosition;
    private BasePagerAdapter adapter;
    private IDriverInterface iDayback;
    private IDriverInterface iWeekback;
    private IDriverInterface iMonthback;
    private DriverDate obdDriverData;//驾驶数据
    private IbackFragment ibackFragment;
    private TextView obd_driver_text;

    /**
     * 返回和点击Fragment其他地方回调接口
     */
    public interface IbackFragment {
        void backFragment();
    }

    @Override
    public void onclickPosition(int position) {
        if (oldPosition != position) {
            if (position == 0) iDayback.onclickPosition(position);
            else if (position == 1) iWeekback.onclickPosition(position);
            else iMonthback.onclickPosition(position);
        }
        oldPosition = position;
    }

    public void setBackFragmentListener(IbackFragment ibackFragment) {
        this.ibackFragment = ibackFragment;
    }

    public void setCallbackFragmentListener(IDriverInterface iDayback, IDriverInterface iWeekback, IDriverInterface iMonthback) {
        if (iDayback != null)
            this.iDayback = iDayback;
        if (iWeekback != null)
            this.iWeekback = iWeekback;
        if (iMonthback != null)
            this.iMonthback = iMonthback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obddriver_activity);
        WeakReference<ObdDriverActivity> weakReference = new WeakReference<ObdDriverActivity>(this);
        mContext = weakReference.get();
        EventBus.getDefault().register(this);
        mCurTracker = UserUtil.getCurrentTracker(mContext);
        obdRequestUtil = new ObdRequestUtil(mContext, mCurTracker.device_sn);
        initView();
        initValue();
    }

    /**
     * 获取不同时间的驾驶数据
     */
    public void onEventMainThread(DriverDate event) {
        this.obdDriverData = event;
        showDriverValue();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        indicator = (ObdPageIndicator) findViewById(R.id.indicator);//导航栏
        viewPager = (ObdViewPager) findViewById(R.id.viewPager);//时间控件
        obd_rl_economic = findViewById(R.id.obd_rl_economic);//经济驾驶背景
        obd_rl_safe = findViewById(R.id.obd_rl_safe);//安全驾驶背景
        obd_text_economic = findViewById(R.id.obd_text_economic);//经济驾驶文字
        obd_text_safe = findViewById(R.id.obd_text_safe);//安全驾驶文字
        obd_scord = findViewById(R.id.obd_scord); //驾驶评估得分
        obd_driver_text = findViewById(R.id.obd_driver_text);//驾驶类型
        obd_tv_state = findViewById(R.id.obd_tv_state); //评估车辆状态显示
        obd_mileage_value = findViewById(R.id.obd_mileage_value);//行程总公里数
        obd_oil_value = findViewById(R.id.obd_oil_value);//行程总油耗
        obd_average_oil = findViewById(R.id.obd_average_oil);//平均油耗
        obd_average_speed = findViewById(R.id.obd_average_speed);//平均车速
        obd_travel_time = findViewById(R.id.obd_travel_time);//行驶时间
        obd_rapidly_accelerate = findViewById(R.id.obd_rapidly_accelerate);//急加速
        obd_sharp_slowdown = findViewById(R.id.obd_sharp_slowdown);//急减速
        obd_speed_limit = findViewById(R.id.obd_speed_limit);//超速
        obd_sharp_turn = findViewById(R.id.obd_sharp_turn);//急转弯
        findViewById(R.id.rl_bg_image).setOnClickListener(this);
        findViewById(R.id.obd_back_image).setOnClickListener(this);
        obd_rl_economic.setOnClickListener(this);
        obd_rl_safe.setOnClickListener(this);
        indicator.setListener(this);//导航栏点击事件
    }

    /**
     * 初始化数据
     */
    private void initValue() {
        adapter = new BasePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.obd_back_image:
                ibackFragment.backFragment();
                finish();
                break;
            case R.id.obd_rl_economic://经济驾驶
                obd_rl_economic.setBackgroundResource(R.drawable.obd_small_activation);
                obd_rl_safe.setBackgroundResource(R.drawable.un_obd_small_activation);
                obd_text_economic.setTextColor(getResources().getColor(R.color.white));
                obd_text_safe.setTextColor(getResources().getColor(R.color.obd_acacac));
                isSafeDriver = false;
                obd_driver_text.setText(R.string.ecological_drive);
                if (isOldSafeDriver != isSafeDriver)
                    showDriverValue();
                isOldSafeDriver = isSafeDriver;
                break;
            case R.id.obd_rl_safe://安全驾驶
                obd_rl_safe.setBackgroundResource(R.drawable.obd_small_activation);
                obd_rl_economic.setBackgroundResource(R.drawable.un_obd_small_activation);
                obd_text_safe.setTextColor(getResources().getColor(R.color.white));
                obd_text_economic.setTextColor(getResources().getColor(R.color.obd_acacac));
                isSafeDriver = true;
                obd_driver_text.setText(R.string.safe_driver);
                if (isOldSafeDriver != isSafeDriver)
                    showDriverValue();
                isOldSafeDriver = isSafeDriver;
                break;
            case R.id.rl_bg_image:
                ibackFragment.backFragment();
                break;
        }
    }

    /**
     * 设置数据
     */
    private void showDriverValue() {
        if (obdDriverData == null)
            return;
        driverLevelData(isSafeDriver ? obdDriverData.safeDriveData.score : obdDriverData.economicalDriveData.score);
        obd_scord.setText(isSafeDriver ? String.valueOf(obdDriverData.safeDriveData.score) : String.valueOf(obdDriverData.economicalDriveData.score));
        obd_mileage_value.setText(isSafeDriver ? Utils.format1(obdDriverData.safeDriveData.mileage) : Utils.format1(obdDriverData.economicalDriveData.mileage));//行程总公里数
        obd_oil_value.setText(isSafeDriver ? Utils.format1(obdDriverData.safeDriveData.fuel) : Utils.format1(obdDriverData.economicalDriveData.fuel));//总油耗
        obd_average_oil.setText(isSafeDriver ? Utils.format1(obdDriverData.safeDriveData.kmfule) :Utils.format1(obdDriverData.economicalDriveData.kmfule));//平均油耗
        obd_average_speed.setText(isSafeDriver ? Utils.format1(obdDriverData.safeDriveData.avgspeed) : Utils.format1(obdDriverData.economicalDriveData.avgspeed));//平均车速
        obd_travel_time.setText(isSafeDriver ? ""+Utils.getDecimal(obdDriverData.safeDriveData.drivetime/60f,"0.0") : ""+Utils.getDecimal(obdDriverData.economicalDriveData.drivetime/60f,"0.0"));//行驶时间
        obd_rapidly_accelerate.setText(isSafeDriver ? String.valueOf(obdDriverData.safeDriveData.p4) : String.valueOf(obdDriverData.economicalDriveData.p4));//急加速
        obd_sharp_slowdown.setText(isSafeDriver ? String.valueOf(obdDriverData.safeDriveData.p5) : String.valueOf(obdDriverData.economicalDriveData.p5));//急减速
        obd_speed_limit.setText(isSafeDriver ? String.valueOf(obdDriverData.safeDriveData.p2) : String.valueOf(obdDriverData.economicalDriveData.p2));//超速
        obd_sharp_turn.setText(isSafeDriver ? String.valueOf(obdDriverData.safeDriveData.p14) : String.valueOf(obdDriverData.economicalDriveData.p14));//急转弯
    }

    /**
     * 驾驶数据
     */
    private void driverLevelData(int point) {
        if (isSafeDriver) {
            if (point >= 90) {
                obd_tv_state.setText(getResources().getString(R.string.safe_drive_a_level_m));
            } else if (point < 90 && point >= 65) {
                obd_tv_state.setText(getResources().getString(R.string.safe_drive_b_level_m));
            } else if (point < 65 && point >= 36) {
                obd_tv_state.setText(getResources().getString(R.string.safe_drive_c_level_m));
            } else {
                obd_tv_state.setText(getResources().getString(R.string.safe_drive_d_level_m));
            }
        } else {
            if (point >= 90) {
                obd_tv_state.setText(getResources().getString(R.string.ecnomic_drive_a_level_m));
            } else if (point < 90 && point >= 65) {
                obd_tv_state.setText(getResources().getString(R.string.ecnomic_drive_b_level_m));
            } else if (point < 65 && point >= 36) {
                obd_tv_state.setText(getResources().getString(R.string.ecnomic_drive_c_level_m));
            } else {
                obd_tv_state.setText(getResources().getString(R.string.ecnomic_drive_d_level_m));
            }
        }
    }

    /**
     * Viewpager适配器
     */
    class BasePagerAdapter extends FragmentPagerAdapter {
        String[] titles;

        public BasePagerAdapter(FragmentManager fm) {
            super(fm);
            this.titles = getResources().getStringArray(R.array.obd_driver_titles);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentFactory.createForNoExpand(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mContext);
        indicator = null;
        viewPager = null;
        obd_rl_economic = null;
        obd_rl_safe = null;
        obd_text_economic = null;
        obd_text_safe = null;
        obd_scord = null;
        obd_tv_state = null;
        mContext = null;
        obdRequestUtil = null;
        mCurTracker = null;
    }
}
