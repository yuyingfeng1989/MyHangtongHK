package com.bluebud.map;

import com.liteguardian.wheelview.NumericWheelAdapter;
import com.liteguardian.wheelview.OnWheelChangedListener;
import com.liteguardian.wheelview.WheelView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class WheelViewManager {

    private static final int VALUE_START_YEAR = 1990;
    private static final int VALUE_END_YEAR = 2100;

    private static final int MONTH_DAY_START = 1;
    private static final int MONTH_END = 12;
    private static final int HOUR_MIN_START = 0;
    private static final int HOUR_END = 23;
    private static final int MIN_END = 59;
    private static final int COUNT_DEFAULT_DAY = 30;



    private int mYear, mMonth, mDay, mStartHour, mStartMinute, mEndHour, mEndMinute;
    private int tYear, tMonth, tDay, tStartHour, tStartMinute, tEndHour, tEndMinute;
    private DecimalFormat decimal = new DecimalFormat("00");
//    private Tracker mCurTracker;
//    private HistoricalTrackUtils mTrackUtils;


    public static WheelViewManager getNewInstance() {
        return new WheelViewManager();
    }

    private WheelViewManager() {
        Calendar mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DATE);
        mStartHour = 0;
        mStartMinute = 0;
        mEndHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mEndMinute = mCalendar.get(Calendar.MINUTE);
        resetTempTime();
//        mCurTracker = UserUtil.getCurrentTracker(App.getContext());
//        mTrackUtils = new HistoricalTrackUtils(App.getContext(), mCurTracker.device_sn);
    }

    public void resetTempTime() {
        tYear = mYear;
        tMonth = mMonth;
        tDay = mDay;
        tStartHour = mStartHour;
        tStartMinute = mStartMinute;
        tEndHour = mEndHour;
        tEndMinute = mEndMinute;
    }

    public void saveTempTime() {
        mYear = tYear;
        mMonth = tMonth;
        mDay = tDay;
        mStartHour = tStartHour;
        mStartMinute = tStartMinute;
        mEndHour = tEndHour;
        mEndMinute = tEndMinute;
    }

    /**
     * 初始化年月日相关的转轮组件
     */
    public void initYYMMDDWheel(final WheelView wheel1, final WheelView wheel2, final WheelView wheel3, final MyWheelChangedListener listener) {
        OnWheelChangedListener wheelYYMMDDListener = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // 由于newValue使用的是索引，所以要加上开头的值
                if (wheel == wheel1) {
                    tYear = newValue + VALUE_START_YEAR;
                } else if (wheel == wheel2) {
                    // 月份发生变化，需要变更日转轮的容量
                    tMonth = newValue + MONTH_DAY_START;
                    resizeDayAdapter(wheel3);
                } else if (wheel == wheel3) {
                    tDay = newValue + MONTH_DAY_START;
                }
                listener.onChanged(onCountDate(), onCountStartTime(), onCountEndTime());
            }
        };

        initYearWheel(wheel1, wheelYYMMDDListener);
        initMonthWheel(wheel2, wheelYYMMDDListener);
        initDayWheel(wheel3, wheelYYMMDDListener);
    }

    private void initYearWheel(WheelView wheelView, final OnWheelChangedListener listener) {
        initWheelView(wheelView, VALUE_START_YEAR, VALUE_END_YEAR, tYear, listener);
    }

    private void initMonthWheel(WheelView wheelView, final OnWheelChangedListener listener) {
        initWheelView(wheelView, MONTH_DAY_START, MONTH_END, tMonth, listener);
    }

    private void initDayWheel(WheelView wheelView, final OnWheelChangedListener listener) {
        initWheelView(wheelView, MONTH_DAY_START, getDayOfMonth(), tDay, listener);
    }

    /**
     * 初始化时分相关的转轮组件
     * * @param isStart 是否是开始时间相关的转轮
     */
    public void initTimeWheel(final WheelView wheel1, WheelView wheel2, final boolean isStart, final MyWheelChangedListener listener) {
        if (wheel1 == null || wheel2 == null) {
            return;
        }
        OnWheelChangedListener wheelHHMMListener = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (wheel == wheel1) {
                    if (isStart) {
                        tStartHour = newValue;
                    } else {
                        tEndHour = newValue;
                    }
                } else {
                    if (isStart) {
                        tStartMinute = newValue;
                    } else {
                        tEndMinute = newValue;
                    }
                }
                listener.onChanged(onCountDate(), onCountTime(tStartHour, tStartMinute), onCountTime(tEndHour, tEndMinute));
            }
        };

        initHourWheel(wheel1, isStart, wheelHHMMListener);
        initMinWheel(wheel2, isStart, wheelHHMMListener);
    }

    private void initHourWheel(WheelView wheelView, final boolean isStart, final OnWheelChangedListener listener) {
        initWheelView(wheelView, HOUR_MIN_START, HOUR_END, isStart ? tStartHour : tEndHour, listener);
    }

    private void initMinWheel(WheelView wheelView, final boolean isStart, final OnWheelChangedListener listener) {
        initWheelView(wheelView, HOUR_MIN_START, MIN_END, isStart ? tStartMinute : tEndMinute, listener);
    }

    private void initWheelView(WheelView wheelView, int start, int end, int current, final OnWheelChangedListener listener) {
        if (current > end || current < start) {
            return;
        }

        if (wheelView == null) {
            return;
        }
        wheelView.clearChangingListenr();
        wheelView.setAdapter(new NumericWheelAdapter(start, end));
        wheelView.setCyclic(true);
        wheelView.setCurrentItem(current - start);
        wheelView.addChangingListener(listener);
    }


    private String onCountDate() {
        return onCountDate(tYear, tMonth, tDay);
    }

    //统计转轮当前得到的时间，由于取的值是基于下标的，需要进行加一
    private String onCountDate(int year, int month, int day) {
     return year + "-" + decimal.format(month) + "-" + decimal.format(day);
    }

    private String onCountTime(int hour, int minute) {
        return decimal.format(hour) + ":" + decimal.format(minute);
    }

    private String onCountStartTime() {
        return onCountTime(tStartHour, tStartMinute);
    }

    private String onCountEndTime() {
        return onCountTime(tEndHour, tEndMinute);
    }


    private void resizeDayAdapter(WheelView wheelView) {
        doResizeWheelView(wheelView, 1, getDayOfMonth());
    }

    private int getDayOfMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(onCountDate(tYear, tMonth, MONTH_DAY_START)));
            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return COUNT_DEFAULT_DAY;
    }

    private void doResizeWheelView(WheelView wheelView, int min, int max) {
        if (wheelView == null || min > max) {
            return;
        }
        int currentIndex = wheelView.getCurrentItem();
        if (currentIndex > max - min) {
            currentIndex = max - min;
        }
        wheelView.setCurrentItem(currentIndex);
        wheelView.setAdapter(new NumericWheelAdapter(min, max));
    }


    public interface MyWheelChangedListener {
        void onChanged(String date, String startTime, String endTime);
    }
}
