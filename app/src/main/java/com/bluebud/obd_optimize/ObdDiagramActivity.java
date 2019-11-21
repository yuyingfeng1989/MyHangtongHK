package com.bluebud.obd_optimize;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bluebud.activity.BaseActivity;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.chat.utils.ChatUtil;
import com.bluebud.chat.utils.TrackDriverBean;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.view.ChartView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2019/4/18.
 */

public class ObdDiagramActivity extends BaseActivity implements View.OnClickListener {
    private List<Integer> yValueKm = new ArrayList<>(); //y轴坐标对应的数据
    private List<Integer> yValueL = new ArrayList<>(); //y轴坐标对应的数据
    private List<Integer> yValueTime = new ArrayList<>(); //y轴坐标对应的数据
    private ChartView chartView;
    private ChartView chartView1;
    private ChartView chartView2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trackdiagram_activity);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        findViewById(R.id.obd_back_image).setOnClickListener(this);
        chartView = findViewById(R.id.chartview);//总里程
        chartView1 = findViewById(R.id.chartview1);//总油耗
        chartView2 = findViewById(R.id.chartview2); //总时长
        Tracker mCurTracker = UserUtil.getCurrentTracker(this);
        if (mCurTracker != null)
            requestValue(mCurTracker.device_sn);
    }

    /**
     * 初始化数据
     */
    private void initData(List<TrackDriverBean> list) {
        DateComparator d = new DateComparator();
        d.setIndex(0);
        int maxKm = (int) Collections.max(list, d).getKm() + 1;
        d.setIndex(1);
        int maxFuel = (int) Collections.max(list, d).getFuel() + 1;
        d.setIndex(2);
        int maxTime = Collections.max(list, d).getTime() / 60 + 1;
        int unitKm = (int) Math.ceil(maxKm / 3f);
        int unitL = (int) Math.ceil(maxFuel / 3f);
        int unitTime = (int) Math.ceil(maxTime / 3f);
        for (int i = 0; i < 5; i++) {
            yValueKm.add(i * unitKm);
            yValueL.add(i * unitL);
            yValueTime.add(i * unitTime);
        }
        Collections.reverse(list);//集合反序排列
        chartView.setValue(list, yValueKm, getString(R.string.total_dileage), "km", 1);
        chartView1.setValue(list, yValueL, getString(R.string.today_dileage), "L", 2);
        chartView2.setValue(list, yValueTime, getString(R.string.obd_total_time), "h", 3);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.obd_back_image:
                finish();
                break;
        }
    }

    /**
     * 比较对象中的值大小
     */
    public class DateComparator implements Comparator<TrackDriverBean> {
        private int index;

        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public int compare(TrackDriverBean lhs, TrackDriverBean rhs) {
            if (index == 0) {
                return (lhs.getKm() < rhs.getKm() ? -1 : (lhs.getKm() == rhs.getKm() ? 0 : 1));
            } else if (index == 1) {
                return (lhs.getFuel() < rhs.getFuel() ? -1 : (lhs.getFuel() == rhs.getFuel() ? 0 : 1));
            } else {
                return (lhs.getTime() < rhs.getTime() ? -1 : (lhs.getTime() == rhs.getTime() ? 0 : 1));
            }
        }
    }

    /**
     * 轨迹数据请求
     */
    private void requestValue(String device_sn) {
        ChatHttpParams.getInstallSigle(this).chatHttpRequest(
                13, ChatUtil.getCurrDate(), device_sn,
                null, null, null, null, null, null, new ChatCallbackResult() {
                    @Override
                    public void callBackStart() {
                        super.callBackStart();
                        ProgressDialogUtil.show(ObdDiagramActivity.this);
                    }

                    @Override
                    public void callBackResult(String result) {
                        ProgressDialogUtil.dismiss();
                        List<TrackDriverBean> list = (List<TrackDriverBean>) ChatHttpParams.getParseResult(13, result);
                        if (list == null) return;
                        initData(list);
                    }

                    @Override
                    public void callBackFailResult(String result) {
                        ProgressDialogUtil.dismiss();
                    }
                });
    }
}
