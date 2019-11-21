package com.bluebud.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.adapter.ClassScheduleAdapter;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.SchoolTimetableInfo;
import com.bluebud.info.SchoolTimetableInfo.SchoolHourMapBean;
import com.bluebud.info.SchoolTimetableInfo.SchoolTimetableBean;
import com.bluebud.info.Tracker;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.request.RequestUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Administrator on 2018/10/9.
 */

public class ClassScheduleActivity extends BaseActivity implements View.OnClickListener, RequestUtil.ICallBack, AdapterView.OnItemClickListener {

    private Context mContext;
    private RequestUtil request;
    private String mDeviceSn;
    private SchoolTimetableInfo.SchoolHourMapBean schoolHourMap;
    private final int IResultOk = 1;
    private int week;//星期
    private int index;//课程位置

    private int[] colors = new int[8];
    private SchoolTimetableBean[] info1 = new SchoolTimetableBean[8];
    private SchoolTimetableBean[] info2 = new SchoolTimetableBean[8];
    private SchoolTimetableBean[] info3 = new SchoolTimetableBean[8];
    private SchoolTimetableBean[] info4 = new SchoolTimetableBean[8];
    private SchoolTimetableBean[] info5 = new SchoolTimetableBean[8];
    private ClassScheduleAdapter adapter1;
    private ClassScheduleAdapter adapter2;
    private ClassScheduleAdapter adapter3;
    private ClassScheduleAdapter adapter4;
    private ClassScheduleAdapter adapter5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classschedule_activity);
        WeakReference<ClassScheduleActivity> weakReference = new WeakReference<>(this);
        mContext = weakReference.get();
        //添加八种随机背景颜色
        colors[0] = R.color.color_fff7df;
        colors[1] = R.color.color_e7fffb;
        colors[2] = R.color.color_ecffee;
        colors[3] = R.color.color_eefbff;
        colors[4] = R.color.color_f8f5fe;
        colors[5] = R.color.color_fef2fb;
        colors[6] = R.color.color_f3f7fe;
        colors[7] = R.color.color_f0ffe2;
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        findViewById(R.id.ll_class_layout1).setBackground(getResources().getDrawable(R.drawable.ico_date1));
        findViewById(R.id.ll_class_layout2).setBackground(getResources().getDrawable(R.drawable.ico_date2));
        findViewById(R.id.ll_class_layout3).setBackground(getResources().getDrawable(R.drawable.ico_date3));
        findViewById(R.id.ll_class_layout4).setBackground(getResources().getDrawable(R.drawable.ico_date4));
        findViewById(R.id.ll_class_layout5).setBackground(getResources().getDrawable(R.drawable.ico_date5));
        findViewById(R.id.schedule_setting).setOnClickListener(this);
        findViewById(R.id.schedule_back).setOnClickListener(this);
        GridView gv_class1 = (GridView) findViewById(R.id.gv_class1);
        GridView gv_class2 = (GridView) findViewById(R.id.gv_class2);
        GridView gv_class3 = (GridView) findViewById(R.id.gv_class3);
        GridView gv_class4 = (GridView) findViewById(R.id.gv_class4);
        GridView gv_class5 = (GridView) findViewById(R.id.gv_class5);
        adapter1 = new ClassScheduleAdapter(mContext, info1, colors);
        adapter2 = new ClassScheduleAdapter(mContext, info2, colors);
        adapter3 = new ClassScheduleAdapter(mContext, info3, colors);
        adapter4 = new ClassScheduleAdapter(mContext, info4, colors);
        adapter5 = new ClassScheduleAdapter(mContext, info5, colors);
        gv_class1.setAdapter(adapter1);
        gv_class2.setAdapter(adapter2);
        gv_class3.setAdapter(adapter3);
        gv_class4.setAdapter(adapter4);
        gv_class5.setAdapter(adapter5);
        gv_class1.setOnItemClickListener(this);
        gv_class2.setOnItemClickListener(this);
        gv_class3.setOnItemClickListener(this);
        gv_class4.setOnItemClickListener(this);
        gv_class5.setOnItemClickListener(this);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Tracker mCurTracker = UserUtil.getCurrentTracker(mContext);
        if (mCurTracker != null)
            mDeviceSn = mCurTracker.device_sn;
        request = new RequestUtil(mContext, mDeviceSn, this);
        request.getDeviceSchoolHours();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        index = position;
        switch (parent.getId()) {
            case R.id.gv_class1:
                week = 1;
                showDialogControy(info1[position]);
                break;
            case R.id.gv_class2:
                week = 2;
                showDialogControy(info2[position]);
                break;
            case R.id.gv_class3:
                week = 3;
                showDialogControy(info3[position]);
                break;
            case R.id.gv_class4:
                week = 4;
                showDialogControy(info4[position]);
                break;
            case R.id.gv_class5:
                week = 5;
                showDialogControy(info5[position]);
                break;
        }
    }

    /**
     * 显示课程表设置弹框
     */
    private void showDialogControy(SchoolTimetableBean info) {
        String courseName = null;
        if (info != null) courseName = info.courseName;
        DialogUtil.showDialogs(mContext, courseName, (RequestUtil.ICallBack) mContext);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.schedule_setting:
                if (schoolHourMap == null)
                    return;
                Intent intent = new Intent(mContext, ClassScheduleTimerActivity.class);
                intent.putExtra("timeInfo", schoolHourMap);
                startActivityForResult(intent, IResultOk);
                break;
            case R.id.schedule_back:
                finish();
                break;
        }
    }


    /**
     * 结果操作
     */
    @Override
    public void callBackData(String data, int position) {
        if (position == 1) {//设置课程表
            request.setDeviceSchoolHours(0, week, index, data, null);
        } else if (position == 2) {//更新本地课程表
            SchoolTimetableBean info;
            switch (week) {
                case 1:
                    info = info1[index];
                    if (info == null) {
                        info = new SchoolTimetableBean();
                        info.dayOfWeek = week;
                        info.courseOfDay = index;
                        info.courseName = data;
                        info1[index] = info;
                    } else {
                        info1[index].courseName = data;
                    }
                    adapter1.refreshValue(info1);
                    break;
                case 2:
                    info = info2[index];
                    if (info == null) {
                        info = new SchoolTimetableBean();
                        info.dayOfWeek = week;
                        info.courseOfDay = index;
                        info.courseName = data;
                        info2[index] = info;
                    } else {
                        info2[index].courseName = data;
                    }
                    adapter2.refreshValue(info2);
                    break;
                case 3:
                    info = info3[index];
                    if (info == null) {
                        info = new SchoolTimetableBean();
                        info.dayOfWeek = week;
                        info.courseOfDay = index;
                        info.courseName = data;
                        info3[index] = info;
                    } else {
                        info3[index].courseName = data;
                    }
                    adapter3.refreshValue(info3);
                    break;
                case 4:
                    info = info4[index];
                    if (info == null) {
                        info = new SchoolTimetableBean();
                        info.dayOfWeek = week;
                        info.courseOfDay = index;
                        info.courseName = data;
                        info4[index] = info;
                    } else {
                        info4[index].courseName = data;
                    }
                    adapter4.refreshValue(info4);
                    break;
                case 5:
                    info = info5[index];
                    if (info == null) {
                        info = new SchoolTimetableBean();
                        info.dayOfWeek = week;
                        info.courseOfDay = index;
                        info.courseName = data;
                        info5[index] = info;
                    } else {
                        info5[index].courseName = data;
                    }
                    adapter5.refreshValue(info5);
                    break;
            }

        } else if (position == 3) {
            SchoolTimetableInfo classSchedule = GsonParse.getClassSchedule(data);//获取课程表
            if (classSchedule != null) {
                schoolHourMap = classSchedule.schoolHourMap;
                settingDateInfo(classSchedule.schoolTimetable);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 1 || data == null)
            return;
        SchoolHourMapBean infos = (SchoolHourMapBean) data.getSerializableExtra("timeInfo");
        if (infos != null && schoolHourMap != null)
            schoolHourMap = infos;
    }

    /**
     * 遍历数组,刷新数据
     */
    private void settingDateInfo(List<SchoolTimetableBean> schoolTimetable) {
        if (schoolTimetable == null)
            return;
        for (SchoolTimetableBean info : schoolTimetable) {
            if (info.dayOfWeek == 1) {
                switch (info.courseOfDay) {
                    case 1:
                        info1[0] = info;
                        break;
                    case 2:
                        info1[1] = info;
                        break;
                    case 3:
                        info1[2] = info;
                        break;
                    case 4:
                        info1[3] = info;
                        break;
                    case 5:
                        info1[4] = info;
                        break;
                    case 6:
                        info1[5] = info;
                        break;
                    case 7:
                        info1[6] = info;
                        break;
                    case 8:
                        info1[7] = info;
                        break;
                }
            }
            if (info.dayOfWeek == 2) {
                switch (info.courseOfDay) {
                    case 1:
                        info2[0] = info;
                        break;
                    case 2:
                        info2[1] = info;
                        break;
                    case 3:
                        info2[2] = info;
                        break;
                    case 4:
                        info2[3] = info;
                        break;
                    case 5:
                        info2[4] = info;
                        break;
                    case 6:
                        info2[5] = info;
                        break;
                    case 7:
                        info2[6] = info;
                        break;
                    case 8:
                        info2[7] = info;
                        break;
                }
            }
            if (info.dayOfWeek == 3) {
                switch (info.courseOfDay) {
                    case 1:
                        info3[0] = info;
                        break;
                    case 2:
                        info3[1] = info;
                        break;
                    case 3:
                        info3[2] = info;
                        break;
                    case 4:
                        info3[3] = info;
                        break;
                    case 5:
                        info3[4] = info;
                        break;
                    case 6:
                        info3[5] = info;
                        break;
                    case 7:
                        info3[6] = info;
                        break;
                    case 8:
                        info3[7] = info;
                        break;
                }
            }
            if (info.dayOfWeek == 4) {
                switch (info.courseOfDay) {
                    case 1:
                        info4[0] = info;
                        break;
                    case 2:
                        info4[1] = info;
                        break;
                    case 3:
                        info4[2] = info;
                        break;
                    case 4:
                        info4[3] = info;
                        break;
                    case 5:
                        info4[4] = info;
                        break;
                    case 6:
                        info4[5] = info;
                        break;
                    case 7:
                        info4[6] = info;
                        break;
                    case 8:
                        info4[7] = info;
                        break;
                }
            }
            if (info.dayOfWeek == 5) {
                switch (info.courseOfDay) {
                    case 1:
                        info5[0] = info;
                        break;
                    case 2:
                        info5[1] = info;
                        break;
                    case 3:
                        info5[2] = info;
                        break;
                    case 4:
                        info5[3] = info;
                        break;
                    case 5:
                        info5[4] = info;
                        break;
                    case 6:
                        info5[5] = info;
                        break;
                    case 7:
                        info5[6] = info;
                        break;
                    case 8:
                        info5[7] = info;
                        break;
                }
            }
            adapter1.refreshValue(info1);
            adapter2.refreshValue(info2);
            adapter3.refreshValue(info3);
            adapter4.refreshValue(info4);
            adapter5.refreshValue(info5);
        }
    }
}
