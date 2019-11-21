package com.bluebud.obd_optimize.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.obd_optimize.ObdDriverActivity;
import com.bluebud.obd_optimize.minterface.IDriverInterface;
import com.bluebud.utils.PopupWindowUtils;
import com.bluebud.utils.Utils;
import com.bluebud.view.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2019/3/19.
 */

public class ObdDailyFragment extends Fragment implements View.OnClickListener, IDriverInterface, ObdDriverActivity.IbackFragment {
    private View contentView;
    private TextView obd_time;
    private PopupWindowUtils popupWindowUtils;
    private CalendarView cvCalendar;
    private ObdDriverActivity mContext;
    private ImageView obd_time_right;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (ObdDriverActivity) getActivity();
        mContext.setBackFragmentListener(this);
        mContext.setCallbackFragmentListener(this, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == contentView) {
            contentView = inflater.inflate(R.layout.obd_fragment, container, false);
            popupWindowUtils = new PopupWindowUtils(getContext());
            cvCalendar = popupWindowUtils.popupWindCalender(getContext());
            initView();
            initData();
        }
        ViewGroup parent = (ViewGroup) contentView.getParent();
        if (parent != null) {
            parent.removeView(contentView);
        }
        return contentView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (cvCalendar != null && cvCalendar.isShown())
            popupWindowUtils.dismiss();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        contentView.findViewById(R.id.obd_time_left).setOnClickListener(this);
        obd_time_right = contentView.findViewById(R.id.obd_time_right);
        obd_time = contentView.findViewById(R.id.obd_time);
        obd_time.setOnClickListener(this);
        obd_time_right.setOnClickListener(this);
        showCalenderDay(Utils.curDate2Day(getContext()));
        obd_time_right.setVisibility(View.GONE);
    }

    private void initData() {
        // 设置控件监听，可以监听到点击的每一天（大家也可以在控件中根据需求设定）
        cvCalendar.setOnItemClickListener(new CalendarView.OnItemClickListener() {
            @Override
            public void OnItemClick(Date selectedStartDate, Date selectedEndDate, Date downDate) {
                popupWindowUtils.dismiss();
                if (!cvCalendar.isSelectMore()) {// 日期点击
                    SimpleDateFormat mday = new SimpleDateFormat("yyyy-MM-dd");
                    String day = mday.format(downDate);
                    showCalenderDay(day);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
//    if (DeviceExpiredUtil.advancedFeatures(mContext, mCurTracker, true))
//           return;
        switch (v.getId()) {
            case R.id.obd_time_left:
                // 如果日历界面显示，则跳转到下一个月 ，如果日历控件没显示，则跳转到下一天
                if (cvCalendar.isShown()) {
                    calenderGoLastMouth();
                } else {
                    getLastDayCarTrack();
                }
                if (!obd_time_right.isShown())
                    obd_time_right.setVisibility(View.VISIBLE);
                break;
            case R.id.obd_time_right:
                if (cvCalendar.isShown()) {
                    calenderGoNextMouth();
                } else {
                    getNextDayCarTrack();
                }
                break;
            case R.id.obd_time:
                if (!cvCalendar.isShown())
                    popupWindowUtils.showObdCalender();
                break;
        }
    }


    /**
     * @Description: 获取下一天的汽车轨迹数据，并显示在列表中，如果下一天无数据，则会继续获取后面第二天的数据，再不行就获取后面第三天的数据，
     * 仍然没数据则提示无轨迹数据
     */
    public void getNextDayCarTrack() {
        if (getCalenderDay().equals(Utils.curDate2Day(mContext))) {
            obd_time_right.setVisibility(View.GONE);
//            ToastUtil.show(mContext, mContext.getResources().getString(R.string.today_is_lastday));
            return;
        } else {
            obd_time_right.setVisibility(View.VISIBLE);
            showCalenderDay(Utils.getSpecifiedDayAfter(getCalenderDay()));
        }
    }

    public void getLastDayCarTrack() {
        showCalenderDay(Utils.getSpecifiedDayBefore(getCalenderDay()));
    }


    /**
     * @Description: 日历跳转到上一个月
     */
    public void calenderGoLastMouth() {
        // 点击上一月 同样返回年月
        String leftYearAndmonth = cvCalendar.clickLeftMonth();
        obd_time.setText(leftYearAndmonth);
    }

    /**
     * @Title: calenderGoNextMouth
     * @Description: 日历跳转到下一个月
     */
    public void calenderGoNextMouth() {
        // 点击上一月 同样返回年月
        String carlenderday = getCalenderDay();
        String curday = Utils.curDate2Day(mContext);
        String[] carlenderdayArray = carlenderday.split("-");
        String[] curdayArray = curday.split("-");
        // 如果年和月都相同，那么说明当前日历是在当前月份，点击下一个月应该不能让它跳转
        if (carlenderdayArray[0].equals(curdayArray[0]) && carlenderdayArray[1].equals(curdayArray[1])) {
            return;
        }
        String rightYearAndmonth = cvCalendar.clickRightMonth();
        obd_time.setText(rightYearAndmonth);
    }

    /**
     * @Description: 获取上方视图中间显示的日期
     */
    private String getCalenderDay() {
        String setDay = obd_time.getText().toString();
        if (Utils.getStrCount(setDay, "-") == 1) {
            setDay = setDay + "-01";
        }
        if (setDay.equals(getResources().getString(R.string.today))) {
            String curDay = Utils.curDate2Day(getContext());
            return curDay;
        } else {
            return setDay;
        }
    }

    /**
     * @Description: 上方中间显示日期
     */
    private void showCalenderDay(String setDay) {
        obd_time.setText(setDay);
        if (setDay.equals(Utils.curDate2Day(mContext))){
            obd_time_right.setVisibility(View.GONE);
        }else{
            obd_time_right.setVisibility(View.VISIBLE);
        }
        mContext.obdRequestUtil.obdReuqestCallback(setDay, setDay);
    }

    @Override
    public void onclickPosition(int position) {
        showCalenderDay(obd_time.getText().toString());
    }

    @Override
    public void backFragment() {
        if (popupWindowUtils != null)
            popupWindowUtils.dismiss();
    }
}
