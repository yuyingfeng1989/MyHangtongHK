package com.bluebud.utils;

import com.bluebud.app.AppApplication;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.StepsInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/6/9 0009.
 */

public class DateUtils {
    private static DateUtils dateUtils;
    private static SimpleDateFormat ymdhms;
    private static SimpleDateFormat ymd;

    private DateUtils() {
    }

    public static DateUtils getInsingle() {
        if (dateUtils == null)
            dateUtils = new DateUtils();
        //当地时间格式
        ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ymd = new SimpleDateFormat("yyyy-MM-dd");
        return dateUtils;
    }

    /**
     * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     */
    public String getOldDate(int distanceDay) {
//        ymdhms.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
//        ymdhms.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = ymdhms.parse(ymdhms.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LogUtil.d("前7天==" + ymdhms.format(endDate));
        return ymdhms.format(endDate);
    }

    /**
     * 获取今天往后一周的日期（几月几号）
     */
    public List<String> getOldWeek7(List<StepsInfo> lastStepBeans) {
        if (lastStepBeans == null)
            return null;
        List<String> weeksList = new ArrayList<String>();
        for (StepsInfo s : lastStepBeans) {
            weeksList.add(getWeek(s.createDate));
        }
        return weeksList;
    }

    /**
     * 获取7天的日期
     */

    /**
     * 给定开始和结束时间，遍历之间的所有日期
     */
    public List<String> queryData(String startAt, String endAt, boolean isDay) {
        List<String> dates = new ArrayList<>();
        Calendar start = Calendar.getInstance();
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = ymd.parse(startAt);
            endDate = ymd.parse(endAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        SimpleDateFormat sim;
        if (isDay)
            sim = new SimpleDateFormat("dd");
        else
            sim = ymd;

        while (start.before(end) || start.equals(end)) {
            if (isDay)
                dates.add(sim.format(start.getTime()));
            else
                dates.add(getWeek(sim.format(start.getTime())));
            start.add(Calendar.DAY_OF_YEAR, 1);
        }
        return dates;
    }
//    public List<String> get7date() {
//        List<String> dates = new ArrayList<String>();
//        final Calendar c = Calendar.getInstance();
//        SimpleDateFormat sim;
//        sim = new SimpleDateFormat("dd");
//        String date = sim.format(c.getTime());
//        dates.add(date);
//        for (int i = 0; i < 6; i++) {
//            c.add(Calendar.DAY_OF_MONTH, 1);
//            date = sim.format(c.getTime());
//            dates.add(date);
//        }
//        return dates;
//    }

    /**
     * 根据当前日期获得是星期几
     */
    private String getWeek(String time) {
        String Week = "";
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(ymd.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int weekIndex = c.get(Calendar.DAY_OF_WEEK);
        switch (weekIndex) {
            case 1:
                Week += AppApplication.getInstance().getString(R.string.week_7);
                break;
            case 2:
                Week += AppApplication.getInstance().getString(R.string.week_1);
                break;
            case 3:
                Week += AppApplication.getInstance().getString(R.string.week_2);
                break;
            case 4:
                Week += AppApplication.getInstance().getString(R.string.week_3);
                break;
            case 5:
                Week += AppApplication.getInstance().getString(R.string.week_4);
                break;
            case 6:
                Week += AppApplication.getInstance().getString(R.string.week_5);
                break;
            case 7:
                Week += AppApplication.getInstance().getString(R.string.week_6);
                break;
        }
        return Week;
    }


    /**
     * 获取未来 第 past 天的日期
     *
     * @param past
     * @return
     */
    public String getFetureDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
        String timeInterval = getTimeInterval(today);
        return timeInterval;
    }

    private String getTimeInterval(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // System.out.println("要计算日期为:" + sdf.format(cal.getTime())); // 输出要计算日期
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        String imptimeBegin = sdf.format(cal.getTime());
        // System.out.println("所在周星期一的日期：" + imptimeBegin);
        cal.add(Calendar.DATE, 6);
        String imptimeEnd = sdf.format(cal.getTime());
        // System.out.println("所在周星期日的日期：" + imptimeEnd);
        return imptimeBegin + "-" + imptimeEnd;
    }

    public String getLast12Months(int i) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -i + 1);
//        Date m = c.getTime();
//        String month = sdf.format(m);
        c.set(Calendar.DAY_OF_MONTH, 0);
        String firstday = sdf.format(c.getTime());

        // 获取前月的最后一天
        c.set(Calendar.DAY_OF_MONTH, 1);
        String lastday = sdf.format(c.getTime());
        return lastday + "-" + firstday;
    }

    /**
     * 比较两个时间大小
     */
    public  int compare_date(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }
}
