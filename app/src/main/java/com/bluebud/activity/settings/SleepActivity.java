package com.bluebud.activity.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.PopupWindowListPositionUtils;
import com.bluebud.utils.PopupWindowListPositionUtils.ListPositon;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.view.CalendarView;
import com.bluebud.view.HomeArcView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class SleepActivity extends BaseActivity implements OnClickListener, ListPositon {
    //	private RequestHandle requestHandle;
    private TextView tvSleepQuality;
    private TextView tvSleepduration;

    private LinearLayout llDownUp;
    private TextView tvData;
    //    private TextView tvDay;
//    private ImageView ivDownUp;
    private CalendarView cvCalendar;
    private View vDateSelect;// 日历界面
    private Context mContext;
    //	private RelativeLayout rlToplay;
//	private boolean isVisible=false;
    private ImageView ivLastDay;
    private ImageView ivNextDay;
    private PopupWindowListPositionUtils positionUtils;
    private List<String> listString;
    private TextView tvNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_sleep2);
        mContext = this;
        init();
        positionUtils = new PopupWindowListPositionUtils(mContext, this);
    }

    private void init() {
        setBaseTitleText(R.string.sleep);
        super.setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);
        super.setBaseTitleRightText(R.string.submit1);
        super.setBaseTitleRightTextVisible(View.GONE);
        getBaseTitleRightText().setOnClickListener(this);
        LinearLayout doughnutView = (LinearLayout) findViewById(R.id.doughnutView);

        tvNum = (TextView) findViewById(R.id.tv_num);
        TextView tvTargetStep = (TextView) findViewById(R.id.tv_target_step);
        doughnutView.removeAllViews();
        doughnutView.addView(new HomeArcView(mContext, 83, 1));

        tvSleepQuality = (TextView) findViewById(R.id.tv_light_sleep);//浅睡
        tvSleepduration = (TextView) findViewById(R.id.tv_deep_sleep);//深睡

        ivLastDay = (ImageView) findViewById(R.id.iv_last_day);
        ivNextDay = (ImageView) findViewById(R.id.iv_next_day);

        ivLastDay.setOnClickListener(this);
        ivNextDay.setOnClickListener(this);

        llDownUp = (LinearLayout) findViewById(R.id.ll_down_up);
        tvData = (TextView) findViewById(R.id.tv_data);
//        tvDay = (TextView) findViewById(R.id.tv_day);
//        ivDownUp = (ImageView) findViewById(R.id.iv_down_up);
        llDownUp.setOnClickListener(this);
        //rlToplay = (RelativeLayout) findViewById(R.id.rl_toplay);
        vDateSelect = findViewById(R.id.ll_calender);
        //vDateSelect = createDateSelectView();
        //rlToplay.addView(vDateSelect);
        vDateSelect.setVisibility(View.GONE);
        String curDay = Utils.curDate2Day(this);
        showCalenderDay(curDay);
        cvCalendar = (CalendarView) findViewById(R.id.cv_calender);
        cvCalendar.setSelectMore(false); // 单选
        // 设置控件监听，可以监听到点击的每一天（大家也可以在控件中根据需求设定）
        cvCalendar
                .setOnItemClickListener(new CalendarView.OnItemClickListener() {

                    @Override
                    public void OnItemClick(Date selectedStartDate,
                                            Date selectedEndDate, Date downDate) {
                        if (cvCalendar.isSelectMore()) {
                        } else {
                            // 日期点击
                            vDateSelect.setVisibility(View.GONE);
                            SimpleDateFormat mday = new SimpleDateFormat(
                                    "yyyy-MM-dd");
                            String day = mday.format(downDate);
                            showCalenderDay(day);
                            if (Utils.curDate2Day(mContext).equals(day)) {
                                //getOnedayTrack(Utils.getCurTime(mContext));
                            } else {
                                //getOnedayTrack(day + " 23:59:59");
                            }
                        }
                    }

                });


//        SleepBluetoothList sleepBluetoothList = UserUtil.getCurrentSleepData(mContext);
//        if (sleepBluetoothList != null) {
//            if (sleepBluetoothList.list != null && sleepBluetoothList.list.size() > 0) {
//                for (int i = 0; i < sleepBluetoothList.list.size(); i++) {
//                    listString = new ArrayList<String>();
//                    String sportday = sleepBluetoothList.list.get(i).getSleepBin();
//                    listString.add(sportday);
//                }
//                String quality = sleepBluetoothList.list.get(sleepBluetoothList.list.size() - 1).getSleepQuality();
//                String times = sleepBluetoothList.list.get(sleepBluetoothList.list.size() - 1).getSleepTimes();
//                LogUtil.i("quality:" + quality + "times:" + times);
//                if ("2".equals(quality)) {//良好
//                    tvSleepQuality.setText(R.string.good);
//                    tvNum.setText(R.string.good);
//
//                } else if ("1".equals(quality)) {//一般
//                    tvSleepQuality.setText(R.string.general);
//                    tvNum.setText(R.string.general);
//                } else {//差
//                    tvSleepQuality.setText(R.string.difference);
//                    tvNum.setText(R.string.difference);
//                }
//                showCalenderDay(sleepBluetoothList.list.get(sleepBluetoothList.list.size() - 1).getSleepBin());
//                if (times != null) {
//                    tvSleepduration.setText(times + getString(R.string.sleep_H));
//                } else {
//                    tvSleepduration.setText("--" + getString(R.string.sleep_H));
//                }
//
//            } else {
//                String curDay1 = Utils.curDate2Day(this);
//                showCalenderDay(curDay1);
//            }
//        }
//        else {
        tvSleepQuality.setText("--");
        tvSleepduration.setText("--" + getString(R.string.sleep_H));
        String curDay1 = Utils.curDate2Day(this);
        showCalenderDay(curDay1);
//        }
    }


    /**
     * @param
     * @return void
     * @throws
     * @Title: showCurrentDay
     * @Description: 上方中间显示日期
     */
    private void showCalenderDay(String setDay) {
        // TODO Auto-generated method stub
        String curDay = Utils.curDate2Day(this);
        if (curDay.equals(setDay))
            tvData.setText(getResources().getString(R.string.today));
        else
            tvData.setText(setDay);
    }

//    /**
//     * @param @return
//     * @return View 控件界面
//     * @throws
//     * @Title: addDateSelectView
//     * @Description: 创建日历选择控件
//     */
//    private View createDateSelectView() {
//        // TODO 动态添加布局(xml方式)
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        LayoutInflater inflater3 = LayoutInflater.from(this);
//        View view = inflater3.inflate(R.layout.layout_calender1, null);
//        view.setLayoutParams(lp);
//        return view;
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.ll_down_up:
                if (listString != null && listString.size() > 0) {
                    positionUtils.ShowlistPosition(listString);
                }

                break;

            case R.id.iv_last_day:
                // 如果日历界面显示，则跳转到下一个月 ，如果日历控件没显示，则跳转到下一天
                if (vDateSelect.getVisibility() == View.VISIBLE) {
                    calenderGoLastMouth();
                } else {
                    //getLastDayCarTrack();
                }
                break;
            case R.id.iv_next_day:
                if (vDateSelect.getVisibility() == View.VISIBLE) {
                    calenderGoNextMouth();
                } else {
                    //getNextDayCarTrack();
                }
                break;
            case R.id.btn_submit:
                if (UserUtil.isGuest(this)) {
                    ToastUtil.show(this, R.string.guest_no_set);
                    return;
                }

                break;
            case R.id.rl_title_right_text:
                if (UserUtil.isGuest(this)) {
                    ToastUtil.show(this, R.string.guest_no_set);
                    return;
                }
                break;
        }
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: calenderGoLastMouth
     * @Description: 日历跳转到上一个月
     */
    private void calenderGoLastMouth() {
        // TODO Auto-generated method stub
        // 点击上一月 同样返回年月
        String leftYearAndmonth = cvCalendar.clickLeftMonth();
//        String[] ya = leftYearAndmonth.split("-");
        tvData.setText(leftYearAndmonth);
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: calenderGoNextMouth
     * @Description: 日历跳转到下一个月
     */
    private void calenderGoNextMouth() {
        // TODO Auto-generated method stub
        // 点击上一月 同样返回年月
        String carlenderday = getCalenderDay();
        String curday = Utils.curDate2Day(this);
        String[] carlenderdayArray = carlenderday.split("-");
        String[] curdayArray = curday.split("-");
        // 如果年和月都相同，那么说明当前日历是在当前月份，点击下一个月应该不能让它跳转
        if (carlenderdayArray[0].equals(curdayArray[0])
                && carlenderdayArray[1].equals(curdayArray[1])) {
            return;
        }

        String rightYearAndmonth = cvCalendar.clickRightMonth();
        tvData.setText(rightYearAndmonth);
    }


    /**
     * @param @return
     * @return String
     * @throws
     * @Title: getCalenderDay
     * @Description: 获取上方视图中间显示的日期
     */
    private String getCalenderDay() {
        // TODO Auto-generated method stub
        String setDay = tvData.getText().toString();
        if (Utils.getStrCount(setDay, "-") == 1) {
            setDay = setDay + "-01";
        }
        if (setDay.equals(getResources().getString(R.string.today))) {
            String curDay = Utils.curDate2Day(this);
            return curDay;
        } else {
            return setDay;
        }
    }

    @Override
    public void getListPositon(int position) {
        showCalenderDay(listString.get(position));
//        SleepBluetoothList sleepBluetoothList = UserUtil.getCurrentSleepData(mContext);
//        if (sleepBluetoothList != null) {
//            if (sleepBluetoothList.list != null && sleepBluetoothList.list.size() > 0) {
//                String quality = sleepBluetoothList.list.get(sleepBluetoothList.list.size() - 1).getSleepQuality();
//                String times = sleepBluetoothList.list.get(sleepBluetoothList.list.size() - 1).getSleepTimes();
//                LogUtil.i("quality:" + quality + "times:" + times);
//                if ("2".equals(quality)) {//良好
//                    tvSleepQuality.setText(R.string.good);
//                    tvNum.setText(R.string.good);
//                } else if ("1".equals(quality)) {//一般
//                    tvSleepQuality.setText(R.string.general);
//                    tvNum.setText(R.string.general);
//                } else {//差
//                    tvSleepQuality.setText(R.string.difference);
//                    tvNum.setText(R.string.difference);
//                }
//                if (times != null) {
//                    tvSleepduration.setText(times + getString(R.string.sleep_H));
//                } else {
//                    tvSleepduration.setText("--" + getString(R.string.sleep_H));
//                }
//
//            }
//        }

    }
}
