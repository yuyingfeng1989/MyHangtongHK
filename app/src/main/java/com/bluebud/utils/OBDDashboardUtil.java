package com.bluebud.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluebud.info.CarInfo;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.view.DashboardView4;

import java.text.DecimalFormat;

import io.rong.eventbus.EventBus;

/**
 * Created by Administrator on 2018/12/27.
 */

public class OBDDashboardUtil implements View.OnClickListener {
    private final DecimalFormat mFloatFormat1 = new DecimalFormat("0.0");
//    private final DecimalFormat mFloatFormat2 = new DecimalFormat("0.00");
    private TextView obd_daily_mileage;
    private TextView obd_daily_oil;
    private DashboardView4 dashboardView4;
    private ImageView obd_bg;
    private boolean isAnimFinished = true;
    private OnclickObdFunciton obdFunction;
    private float oldSpeed;
    private float oldRotate;
    private float oldTotaMileage;
    private TextView obd_total_mileage;
    private TextView obd_total_oil;
    private ImageView obd_temp_image;
    private ImageView obd_battery_image;
    private ImageView obd_oil_image;
//    private final Random rand;

    public interface OnclickObdFunciton {
        public void onclickObdCallback(int position);// 定位接口

    }

    public OBDDashboardUtil(OnclickObdFunciton obdFunciton) {
        EventBus.getDefault().register(this);
        this.obdFunction = obdFunciton;
    }

    public void addView(View view) {
        dashboardView4 = view.findViewById(R.id.dashboard_view_4); //仪表盘
        obd_daily_mileage = view.findViewById(R.id.obd_daily_mileage); //单日里程
        obd_daily_oil = view.findViewById(R.id.obd_daily_oil); //单日油耗
        obd_bg = view.findViewById(R.id.bg_obd_image);
        obd_total_mileage = view.findViewById(R.id.obd_total_mileage);
        obd_total_oil = view.findViewById(R.id.obd_total_oil);
        obd_temp_image = view.findViewById(R.id.obd_temp_image);
        obd_battery_image = view.findViewById(R.id.obd_battery_image);
        obd_oil_image = view.findViewById(R.id.obd_oil_image);
        view.findViewById(R.id.obd_detection_image).setOnClickListener(this);//车辆检测
        view.findViewById(R.id.obd_driving_image).setOnClickListener(this);//驾驶行为评分
        view.findViewById(R.id.obd_trajectory_image).setOnClickListener(this);//行程轨迹
        view.findViewById(R.id.obd_fence_image).setOnClickListener(this);//围栏
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.obd_detection_image://车辆检测
                obdFunction.onclickObdCallback(0);
                break;
            case R.id.obd_driving_image://驾驶行为评分
                obdFunction.onclickObdCallback(1);
                break;
            case R.id.obd_trajectory_image://行程轨迹
                obdFunction.onclickObdCallback(2);
                break;
            case R.id.obd_fence_image://围栏
                obdFunction.onclickObdCallback(3);
                break;
        }
    }

    /**
     * 轮询和点名返回obd数据在仪表盘上
     */
    public void onEventMainThread(CarInfo carInfo) {
        CarInfo.MileageAndFuel mileageAndFuel = carInfo.mileageAndFuel;
        LogUtil.e("obd仪表盘数据=" + mileageAndFuel.toString());
        if (mileageAndFuel == null)
            return;
        mileageAndFuel.rotationRate = mileageAndFuel.rotationRate / 1000f;
        refreshData(mileageAndFuel);
    }

    private void refreshData(CarInfo.MileageAndFuel mileageAndFuel) {
        if (isAnimFinished) {
            if (mileageAndFuel.rotationRate > 0&& mileageAndFuel.carStatus > 0)//
                obd_bg.setImageResource(R.drawable.obd_bg_start);
            else
                obd_bg.setImageResource(R.drawable.obd_bg_unstart);
            if (mileageAndFuel.voltage == 0||mileageAndFuel.carStatus<1)
                obd_battery_image.setImageResource(R.drawable.obd_unbattery);
            else obd_battery_image.setImageResource(R.drawable.obd_battery);
            if (mileageAndFuel.oil == 0||mileageAndFuel.carStatus<1)
                obd_oil_image.setImageResource(R.drawable.obd_unoil);
            else obd_oil_image.setImageResource(R.drawable.obd_oil);
            if (mileageAndFuel.water == 0||mileageAndFuel.carStatus<1)
                obd_temp_image.setImageResource(R.drawable.obd_untemp);
            else obd_temp_image.setImageResource(R.drawable.obd_temp);
            String zh = SystemUtil.getSystemLanguage();
            if(zh.equals("zh")){
                obd_daily_oil.setText(mFloatFormat1.format(mileageAndFuel.fuel) + "L");
                obd_daily_mileage.setText(mFloatFormat1.format(mileageAndFuel.mileage) + "km");
                obd_total_mileage.setText(mFloatFormat1.format(mileageAndFuel.totalmileage)+ "km");
                obd_total_oil.setText(mFloatFormat1.format(mileageAndFuel.totalfuel) + "L");
            }else {
                obd_daily_oil.setText(mFloatFormat1.format(mileageAndFuel.fuel));
                obd_daily_mileage.setText(mFloatFormat1.format(mileageAndFuel.mileage));
                obd_total_mileage.setText(mFloatFormat1.format(mileageAndFuel.totalmileage)+"");
                obd_total_oil.setText(mFloatFormat1.format(mileageAndFuel.totalfuel));
            }
            if (oldRotate == mileageAndFuel.rotationRate && oldSpeed == mileageAndFuel.speed && oldTotaMileage == mileageAndFuel.totalmileage)
                return;
            animatorOBD(mileageAndFuel);
        }
    }

    /**
     * 旋转动画
     */
    private void animatorOBD(final CarInfo.MileageAndFuel data) {
        if (isAnimFinished) {
            dashboardView4.setDashboardValue(data);
            ObjectAnimator animator = ObjectAnimator.ofFloat(dashboardView4, "rotationValue", dashboardView4.getVelocity(), data.rotationRate);
            animator.setDuration(1500).setInterpolator(new LinearInterpolator());
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isAnimFinished = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isAnimFinished = true;
                    oldRotate = data.rotationRate;
                    oldSpeed = data.speed;
                    oldTotaMileage = data.totalmileage;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    isAnimFinished = true;
                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    dashboardView4.setVelocity(value);
                }
            });
            animator.start();
        }
    }

    /**
     * 取消EventBus注册
     */
    public void unRegistEventBus() {
        EventBus.getDefault().unregister(this);
    }
}
