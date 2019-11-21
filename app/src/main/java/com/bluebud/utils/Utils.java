package com.bluebud.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bluebud.app.App;
import com.bluebud.app.AppApplication;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.HttpParams;
import com.bluebud.info.Tracker;
import com.bluebud.info.User;
import com.bluebud.liteguardian_hk.R;
import com.loopj.android.http.RequestParams;

import org.apache.http.util.EncodingUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Character.UnicodeBlock;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String getLanguage2Url() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry();

        LogUtil.i("language:" + language);

        // 除了繁体中文其余系统语言均显示为英文
        if ("zh".equals(language)) {
            if ("HK".equals(country) || "TW".equals(country)) {
                language = "zh-hk";
            } else {
                language = "zh-cn";
            }
        } else if ("tr".equals(language)) {
            language = "tr-tr";
        } else if ("es".equals(language)) {
            language = "es-es";
        } else if ("ru".equals(language)) {
            language = "ru-ru";
        } else if ("de".equals(language)) {
            language = "de-de";
        } else {
            language = "en-us";
        }

        return language;
    }

    public static boolean isChineseMainland() {
        boolean isChineseMainland = true;
        if ("zh".equals(SystemUtil.getSystemLanguage())) {
            if ("HK".equals(SystemUtil.getSystemCountry()) || "TW".equals(SystemUtil.getSystemCountry())) {
                isChineseMainland = false;
            } else {
                isChineseMainland = true;
            }
        } else {
            isChineseMainland = false;
        }
        return isChineseMainland;
    }

    public static boolean isChina() {
        boolean isChineseMainland = true;
//        Locale l = Locale.getDefault();
        String language = SystemUtil.getSystemLanguage();
        if ("zh".equals(language)) {
            isChineseMainland = true;
        } else {
            isChineseMainland = false;
        }
        return isChineseMainland;
    }

    public static String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str) || "null".equals(str);
    }

    public static void hiddenKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // 判断输入是否6-80个字符
    public static boolean isCorrectUserName(String userName) {
        // Pattern p = Pattern
        // .compile("^{4,20}$");//^[a-zA-Z0-9\u4e00-\u9fa5]{4,20}$只由数字、字母和汉字组成的4-20个字符
        // Matcher m = p.matcher(mobiles);
        int len = userName.length();
        if (len >= 6 && len <= 80) {
            return true;
        } else {
            return false;
        }
    }

    // 判断输入是否6-80个字符
    public static boolean isCorrectName(String userName) {
        Pattern p = Pattern.compile("^[A-Za-z0-9\\u4e00-\u9fa5]+$");//^[a-zA-Z0-9\u4e00-\u9fa5]{4,20}$只由数字、字母和汉字组成的4-20个字符
        Matcher m = p.matcher(userName);
        return m.matches();

    }

    // 判断输入是否8个字符（字母和数字）以上
    public static boolean isCorrectPwd(String mobiles) {
        Pattern p = Pattern.compile("^[a-zA-Z0-9]{8,20}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    //判断是否包含有中文
    public static boolean isCorrectWifi(String string) {
        try {
            byte[] bytes = string.getBytes("UTF-8");
            if (bytes.length == string.length())
                return false;
            else
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 判断输入是否邮箱
    public static boolean isCorrectEmail(String email) {
        Pattern p = Pattern
                .compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    // 判断输入3-20个字符trackerName
    public static boolean isCorrectTrackerName(String trackerName) {
        Pattern p = Pattern.compile("^.{0,30}$");
        Matcher m = p.matcher(trackerName);
        return m.matches();
    }

    // 判断输入100个字符以内mark
    public static boolean isCorrectMark(String mark) {
        Pattern p = Pattern.compile("^.{1,500}$");
        Matcher m = p.matcher(mark);
        return m.matches();
    }

    // 判断输入11位数字以内mobile
    public static boolean isCorrectMobile(String mobile) {
        if (Utils.isEmpty(mobile)) {
            return true;
        }
//		Pattern p = Pattern
//				.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Pattern p = Pattern.compile("^\\d{6,20}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    // 判断输入20位数字以内mobile
    public static boolean isCorrectPhone(String mobile) {
        Pattern p = Pattern.compile("^\\d{0,20}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    // 判断输入30个字符以内apn
    public static boolean isCorrectApn(String apn) {
        Pattern p = Pattern.compile("^.{1,30}$");
        Matcher m = p.matcher(apn);
        return m.matches();
    }

    // 判断输入30个字符以内apn用户名
    public static boolean isCorrectApnUserName(String username) {
        Pattern p = Pattern.compile("^.{0,30}$");
        Matcher m = p.matcher(username);
        return m.matches();
    }

    // 判断输入30个字符以内apn密码
    public static boolean isCorrectApnPasswd(String passwd) {
        Pattern p = Pattern.compile("^.{0,20}$");
        Matcher m = p.matcher(passwd);
        return m.matches();
    }

    // 判断输入4位汉字，8位字符以上
    public static int getAddressLen(String address) {
        int chinese = 0;
        int len = address.length();
        for (int i = 0; i < len; i++) {
            int a = address.charAt(i);
            if (a >= 0x4e00 && a <= 0x9fff)
                chinese++;
        }
        int length = len + chinese;
        return length;
    }

    public static String getParamsStr(List<String> params) {
        StringBuilder sb = new StringBuilder();
        for (String param : params) {
            if (params.indexOf(param) == params.size() - 1) {
                sb.append("\"" + param + "\"");
                continue;
            }
            sb.append("\"" + param + "\"|");
        }
        return sb.toString();
    }

    public static String getUrl(String connIP, int connPort) {//注册服务器拼接地址
        if (TextUtils.isEmpty(connIP))
            return null;
        String url = "http://" + connIP + ":" + connPort
                + HttpParams.URL_ACTION;
        return url;
    }


//    public static String getUrIP(String url) {
//        String ip = url.substring(url.indexOf("http://") + 7,
//                url.lastIndexOf(":"));
//        return ip;
//    }
//
//    public static String getUrlPort(String url) {
//        String[] url1 = url.split(":");
//        String[] url2 = url1[2].split("/");
//        return url2[0];
//    }

    public static String getImageUrl(Context context) {
        Tracker currentTracker = UserUtil.getCurrentTracker(context);
        // add by zengms 2016-4-27
        String url = null;
        if (currentTracker != null) {
            url = "http://" + currentTracker.conn_name + ":" + currentTracker.conn_port;
        }
        return url;
    }

    /**
     * 获取点击设备的头像
     */
    public static String getImageUrl(Tracker tracker) {
        String url = null;
        if (tracker != null) {
            url = "http://" + tracker.conn_name + ":" + tracker.conn_port;
        }
        return url;
    }


    public static int getMatchAddressIndex(String emailSuffix) {
        String[] addresses = Constants.EMAIL_ADDRESSES;
        int len = addresses.length;
        for (int i = 0; i < len; i++) {
            if (emailSuffix.equals(addresses[i])) {
                if (i != len - 1)
                    return i;
                return 3;
            }
        }
        return -1;
    }

    public static void openUrl(Context context, String email) {
        String[] emailStrs = email.split("@");
        // int i = Utils.getMatchAddressIndex(emailStrs[1]);
        // if (i != -1) {
        // Uri uri = Uri.parse(Constants.EMAIL_URL[i]);
        // Intent netIntent = new Intent(Intent.ACTION_VIEW, uri);
        // context.startActivity(netIntent);
        // return;
        // }
        // Uri uri = Uri.parse(Constants.EMAIL_URL[0]);
        Uri uri = Uri.parse("http://mail." + emailStrs[1]);
        Intent netIntent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(netIntent);
    }

    /**
     * 比较两个时间大小
     *
     * @param time1
     * @param time2
     * @return 小于0时time1<time2, 等于0时相等 ， 大于0时time1>time2
     */
    public static int compareTime(String time1, String time2) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse(time1));
            c2.setTime(sdf.parse(time2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c1.compareTo(c2);
    }

    public static int compareDate(String sDay1, String sDay2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse(sDay1));
            c2.setTime(sdf.parse(sDay2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c1.compareTo(c2);
    }

    public static boolean compareDateTo(String sDay1, String sDay2) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            long startTimer = sdf.parse(sDay1).getTime();
            long endTimer = sdf.parse(sDay2).getTime();
            if (startTimer < endTimer)
                return true;
            else
                return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
//        return c1.compareTo(c2);
    }


    public static String getDate(Calendar calendar) {
        Date d = calendar.getTime();
        SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd");
        return sDate.format(d);
    }

    public static String getTime(Calendar calendar) {
        Date d = calendar.getTime();
        SimpleDateFormat sDate = new SimpleDateFormat("HH:mm");
        return sDate.format(d);
    }

    public static String getTimeFromStr(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar c1 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getTime(c1);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        LogUtil.d("density:" + scale);
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取追踪器列表
     *
     * @param trackerList
     * @param type        为0表示普通用户 为1表示超级用户
     * @return
     */
    public static List<Tracker> getTrackers(String user,
                                            List<Tracker> trackerList, int type) {
        int size = trackerList.size();
        List<Tracker> trackers = new ArrayList<Tracker>();
        if (type == 1) {
            for (int i = 0; i < size; i++) {
                Tracker tracker = trackerList.get(i);
                if (tracker.super_user != null) {
                    if (user.equals(tracker.super_user)) {
                        trackers.add(tracker);
                    }
                }
            }
            return trackers;
        }
        for (int i = 0; i < size; i++) {
            Tracker tracker = trackerList.get(i);
            if (tracker.super_user != null) {
                if (!user.equals(tracker.super_user)) {
                    trackers.add(tracker);
                }
            }
        }
        return trackers;
    }

    public static String getVersionName(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "0.0.0";
        }
        String version = packInfo.versionName;
        return version;
    }

    // 保存照片
    public static void saveImage(Bitmap bitmap, File file) {
        File fileDir = new File(Constants.CACHE_SAVE_PATH);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
        try {
            baos.flush();
            baos.close();
            @SuppressWarnings("resource")
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int compareTo(Calendar c1, Calendar c2) {
        String time1 = Utils.getDate(c1);
        String time2 = Utils.getDate(c2);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = sdf.parse(time1);
            Date d2 = sdf.parse(time2);
            if (d1.getTime() > d2.getTime())
                return 1;
            if (d1.getTime() == d2.getTime())
                return 0;
            return -1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Calendar getBeforeDate(Calendar cal) {
        int year_s = cal.get(Calendar.YEAR);
        int month_s = cal.get(Calendar.MONTH);
        int day_s = cal.get(Calendar.DAY_OF_MONTH) - 30;
        Calendar newCal = Calendar.getInstance();
        newCal.set(Calendar.YEAR, year_s);
        newCal.set(Calendar.MONTH, month_s);
        newCal.set(Calendar.DAY_OF_MONTH, day_s);
        return newCal;
    }

    public static String getBeforeDate2String(Calendar cal) {
        int year_s = cal.get(Calendar.YEAR);
        int month_s = cal.get(Calendar.MONTH);
        int day_s = cal.get(Calendar.DAY_OF_MONTH) - 2;
        Calendar newCal = Calendar.getInstance();
        newCal.set(Calendar.YEAR, year_s);
        newCal.set(Calendar.MONTH, month_s);
        newCal.set(Calendar.DAY_OF_MONTH, day_s);
        return Utils.getDate(newCal);
    }

    public static String throwableToString(Throwable t) {
        if (t == null)
            return "";

        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static String getFileName(String dataFormat) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat(dataFormat);
        return format.format(date);
    }

    public static boolean checkSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean saveFile(String filePath, String fileContent,
                                   String fileName) throws IOException {
        LogUtil.i("saveFile()");
        if (!checkSDCard()) {
            LogUtil.i("no SDCard");
            return false;
        }
        File folder = new File(Environment.getExternalStorageDirectory(),
                filePath);

        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(fileContent.getBytes());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断当前应用程序处于前台还是后台
     *
     * @param context
     * @return
     */
//	public static boolean isApplicationBroughtToBackground(final Context context) {
//		ActivityManager am = (ActivityManager) context
//				.getSystemService(Context.ACTIVITY_SERVICE);
//		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
//		if (!tasks.isEmpty()) {
//			ComponentName topActivity = tasks.get(0).topActivity;
//			if (!topActivity.getPackageName().equals(context.getPackageName())) {
//				return true;
//			}
//		}
//		return false;
//
//	}

//    /**
//     * GPS坐标转为百度坐标
//     *
//     * @param ll
//     * @return
//     * @paramCoordType.COMMON:google地图、soso地图、aliyun地图、mapabc地图和amap地图所用坐标 GPS
//     */
//    public static LatLng gpsConvert2BaiduPoint(CoordType coordType, LatLng ll) {
//        CoordinateConverter converter = new CoordinateConverter();
//        converter.from(coordType);
//        converter.coord(ll);
//        return converter.convert();
//    }
//
//
//    public static MyLatLng gpsConvert2BaiduPoint(MyLatLng myLatLng) {
//        if (myLatLng == null) {
//            return null;
//        }
//        return MyLatLng.from(gpsConvert2BaiduPoint(CoordType.GPS, myLatLng.toBLatLng()));
//    }

//    /**
//     * gps坐标转高德地图坐标
//     * @param ll 偏转数据源
//     * @return 转换后的数据
//     */
//    public static MyLatLng gpsConvert2AMapPoint(
//            MyLatLng ll) {
//        com.amap.api.maps.CoordinateConverter converter = new com.amap.api.maps.CoordinateConverter(App.getContext());
//        converter.from(com.amap.api.maps.CoordinateConverter.CoordType.GPS);
//        converter.coord(ll.toALatLng());
//        return MyLatLng.from(converter.convert());
//    }

//    /**
//     * 百度坐标转换为WGS84定位坐标
//     */
//    public static MyLatLng AmapPointConvert2Wgs84(MyLatLng srcLL) {
//        LogUtil.d("高德坐标：" + srcLL.latitude + "," + srcLL.longitude);
//        MyLatLng tmpLL = gpsConvert2AMapPoint(srcLL);
//        double lat = 2 * srcLL.latitude - tmpLL.latitude;
//        double lng = 2 * srcLL.longitude - tmpLL.longitude;
//        LogUtil.d("WGS坐标：" + lat + "," + lng);
//        return MyLatLng.from(lat, lng);
//    }

//    /**
//     * 百度坐标转换为WGS84定位坐标
//     *
//     * @param srcLL
//     * @return LatLng
//     */
//    public static LatLng baiduPointConvert2Wgs84(LatLng srcLL) {
//        LogUtil.d("百度坐标：" + srcLL.latitude + "," + srcLL.longitude);
//        LatLng tmpLL = gpsConvert2BaiduPoint(CoordType.GPS, srcLL);
//        double lat = 2 * srcLL.latitude - tmpLL.latitude;
//        double lng = 2 * srcLL.longitude - tmpLL.longitude;
//        LogUtil.d("WGS坐标：" + lat + "," + lng);
//        return new LatLng(lat, lng);
//    }
//
//    public static MyLatLng baiduPointConvert2Wgs84(MyLatLng srcLL) {
//        return MyLatLng.from(baiduPointConvert2Wgs84(srcLL.toBLatLng()));
//    }
    public static String getCurTime(Context context) {
        return getCurTime();
    }

    public static String getCurTime() {
        String sFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sDate = new SimpleDateFormat(sFormat);
        Date date = new Date();
        return sDate.format(date);
    }

    public static String getDiffTime(Context context, String d1, String d2) {
        try {
            String sFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sDate = new SimpleDateFormat(sFormat);
            long diff = sDate.parse(d1).getTime() - sDate.parse(d2).getTime();//这样得到的差值是微秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);

            long second = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000;
            String second1 = "00";
            String hours1 = "00";
            String minutes1 = "00";
            if (second < 10) {
                second1 = "0" + second;
            } else {
                second1 = "" + second;
            }
            if (hours < 10) {
                hours1 = "0" + hours;
            } else {
                hours1 = "" + hours;
            }
            if (minutes < 10) {
                minutes1 = "0" + minutes;
            } else {
                minutes1 = "" + minutes;
            }

            return hours1 + ":" + minutes1 + ":" + second1;

        } catch (Exception e) {
            return null;
        }
    }


    public static String getTimeHour(Context context, String d1) {


        try {
            String sFormat = "yyyy-MM-dd HH:mm:ss";
            d1 = d1 + "000";
            LogUtil.i("d1" + d1);
            SimpleDateFormat sDate = new SimpleDateFormat(sFormat);
            long diff = sDate.parse(d1).getTime();//这样得到的差值是微秒级别
            LogUtil.i("diff:" + diff);
            long hours = diff / (1000 * 60 * 60);

            return hours + "";

        } catch (Exception e) {
            return null;
        }
    }


    public static String curDate2Day(Context context) {
        StringBuffer sb = new StringBuffer();
        String sFormat = "yyyy-MM-dd";
        SimpleDateFormat sDate = new SimpleDateFormat(sFormat);
        Date date = new Date();
        sb.append(sDate.format(date));

        return sb.toString();
    }

    public static String curDate2Day() {
        return curDate2Day(null);
    }

    public static String curDate2BeforeTowDay(Context context) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH) - 2;
        calendar.set(year, monthOfYear, dayOfMonth);

        return Utils.getDate(calendar);
    }

    public static String curDate2Week(Context context) {
        StringBuffer sb = new StringBuffer();
        // String sFormat = "yyyy"
        // + context.getResources().getString(R.string.year) + "MM"
        // + context.getResources().getString(R.string.month) + "dd"
        // + context.getResources().getString(R.string.day);
        String sFormat = "yyyy-MM-dd";
        SimpleDateFormat sf = new SimpleDateFormat(sFormat);
        Date date = new Date();
        sb.append(sf.format(date));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String week = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(week)) {
            week = context.getResources().getString(R.string.week_7);
        } else if ("2".equals(week)) {
            week = context.getResources().getString(R.string.week_1);
        } else if ("3".equals(week)) {
            week = context.getResources().getString(R.string.week_2);
        } else if ("4".equals(week)) {
            week = context.getResources().getString(R.string.week_3);
        } else if ("5".equals(week)) {
            week = context.getResources().getString(R.string.week_4);
        } else if ("6".equals(week)) {
            week = context.getResources().getString(R.string.week_5);
        } else if ("7".equals(week)) {
            week = context.getResources().getString(R.string.week_6);
        }
        sb.append(" " + week);

        return sb.toString();
    }

    public static String string2Week(Context context, String week) {
        if ("1".equals(week)) {
            week = context.getResources().getString(R.string.week_7);
        } else if ("2".equals(week)) {
            week = context.getResources().getString(R.string.week_1);
        } else if ("3".equals(week)) {
            week = context.getResources().getString(R.string.week_2);
        } else if ("4".equals(week)) {
            week = context.getResources().getString(R.string.week_3);
        } else if ("5".equals(week)) {
            week = context.getResources().getString(R.string.week_4);
        } else if ("6".equals(week)) {
            week = context.getResources().getString(R.string.week_5);
        } else if ("7".equals(week)) {
            week = context.getResources().getString(R.string.week_6);
        }

        return week;
    }

    public static String dateString2Week(Context context, String sDate) {
        StringBuffer sb = new StringBuffer();
        String sFormat = "yyyy-MM-dd";
        // String sFormat1 = "yyyy"
        // + context.getResources().getString(R.string.year) + "MM"
        // + context.getResources().getString(R.string.month) + "dd"
        // + context.getResources().getString(R.string.day);
        String sFormat1 = "yyyy-MM-dd";
        SimpleDateFormat sf = new SimpleDateFormat(sFormat);
        SimpleDateFormat sf1 = new SimpleDateFormat(sFormat1);
        Date date = new Date();
        try {
            date = sf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sb.append(sf1.format(date));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String week = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(week)) {
            week = context.getResources().getString(R.string.week_7);
        } else if ("2".equals(week)) {
            week = context.getResources().getString(R.string.week_1);
        } else if ("3".equals(week)) {
            week = context.getResources().getString(R.string.week_2);
        } else if ("4".equals(week)) {
            week = context.getResources().getString(R.string.week_3);
        } else if ("5".equals(week)) {
            week = context.getResources().getString(R.string.week_4);
        } else if ("6".equals(week)) {
            week = context.getResources().getString(R.string.week_5);
        } else if ("7".equals(week)) {
            week = context.getResources().getString(R.string.week_6);
        }
        sb.append(" " + week);

        return sb.toString();
    }

    public static String curDate2CharDays(Context context) {
        StringBuffer sb = new StringBuffer();
        // String sFormat = "yyyy"
        // + context.getResources().getString(R.string.year) + "MM"
        // + context.getResources().getString(R.string.month) + "dd"
        // + context.getResources().getString(R.string.day);
        String sFormat = "yyyy-MM-dd";
        SimpleDateFormat sDate = new SimpleDateFormat(sFormat);
        Date date = new Date();
        sb.append(sDate.format(date));

        return sb.toString();
    }

    public static String curDate2CharDays1(Context context) {
        StringBuffer sb = new StringBuffer();
        String sFormat = "yyyy-MM-dd";
        SimpleDateFormat sDate = new SimpleDateFormat(sFormat);
        Date date = new Date();
        sb.append(sDate.format(date));

        return sb.toString();
    }

    public static String curDate2CharDay(Context context) {
        StringBuffer sb = new StringBuffer();
        String sFormat = "dd";
        SimpleDateFormat sDate = new SimpleDateFormat(sFormat);
        Date date = new Date();
        sb.append(sDate.format(date));

        return sb.toString();
    }

    public static String curDate2CharMonth(Context context) {
        StringBuffer sb = new StringBuffer();
        String sFormat = "MM";
        SimpleDateFormat sDate = new SimpleDateFormat(sFormat);
        Date date = new Date();
        sb.append(sDate.format(date));

        return sb.toString();
    }

    public static String curDate2CharWeek(Context context) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String week = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));

        return week;
    }

    public static String dateString2Week1(Context context, String sDate) {
        StringBuffer sb = new StringBuffer();
        String sFormat = "yyyy-MM-dd";
        SimpleDateFormat sf = new SimpleDateFormat(sFormat);
        Date date = new Date();
        try {
            date = sf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String week = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));

        return week;
    }

    public static String dateString2Days(Context context, String sDate) {
        StringBuffer sb = new StringBuffer();
        String sFormat = "yyyy-MM-dd";
        // String sFormat1 = "yyyy"
        // + context.getResources().getString(R.string.year) + "MM"
        // + context.getResources().getString(R.string.month) + "dd"
        // + context.getResources().getString(R.string.day);
        String sFormat1 = "yyyy-MM-dd";
        SimpleDateFormat sf = new SimpleDateFormat(sFormat);
        SimpleDateFormat sf1 = new SimpleDateFormat(sFormat1);
        Date date = new Date();
        try {
            date = sf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sb.append(sf1.format(date));

        return sb.toString();
    }

    public static String dateString2DaysSS(Context context, String sDate) {
        StringBuffer sb = new StringBuffer();
        String sFormat = "yyyy-MM-dd HH:mm";
        // String sFormat1 = "yyyy"
        // + context.getResources().getString(R.string.year) + "MM"
        // + context.getResources().getString(R.string.month) + "dd"
        // + context.getResources().getString(R.string.day) + " HH:mm";
        String sFormat1 = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sf = new SimpleDateFormat(sFormat);
        SimpleDateFormat sf1 = new SimpleDateFormat(sFormat1);
        Date date = new Date();
        try {
            date = sf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sb.append(sf1.format(date));

        return sb.toString();
    }

    public static String dateString2Day(String sDate) {
        StringBuffer sb = new StringBuffer();
        String sFormat = "yyyy-MM-dd";
        String sFormat1 = "dd";
        SimpleDateFormat sf = new SimpleDateFormat(sFormat);
        SimpleDateFormat sf1 = new SimpleDateFormat(sFormat1);
        Date date = new Date();
        try {
            date = sf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sb.append(sf1.format(date));

        return sb.toString();
    }

    public static String dateString2Month(String sDate) {
        StringBuffer sb = new StringBuffer();
        String sFormat = "yyyy-MM-dd";
        String sFormat1 = "MM";
        SimpleDateFormat sf = new SimpleDateFormat(sFormat);
        SimpleDateFormat sf1 = new SimpleDateFormat(sFormat1);
        Date date = new Date();
        try {
            date = sf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sb.append(sf1.format(date));

        return sb.toString();
    }

    public static String dateString2Year(String sDate) {
        StringBuffer sb = new StringBuffer();
        String sFormat = "yyyy-MM-dd";
        String sFormat1 = "yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(sFormat);
        SimpleDateFormat sf1 = new SimpleDateFormat(sFormat1);
        Date date = new Date();
        try {
            date = sf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sb.append(sf1.format(date));

        return sb.toString();
    }

    public static Date dateString2Date(Context context, String sDate) {
        String sFormat = "yyyy-MM-dd";
        SimpleDateFormat sf = new SimpleDateFormat(sFormat);
        Date date = new Date();
        try {
            date = sf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String curDate2Hour(Context context) {
        StringBuffer sb = new StringBuffer();
        String sFormat = "HH:mm";
        SimpleDateFormat sDate = new SimpleDateFormat(sFormat);
        Date date = new Date();
        sb.append(sDate.format(date));

        return sb.toString();
    }

    public static Date hourString2Date(String sDate) {
        String sFormat = "HH:mm";
        SimpleDateFormat sf = new SimpleDateFormat(sFormat);
        Date date = new Date();
        try {
            date = sf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String dateString2Hour(String sDate) {
        StringBuffer sb = new StringBuffer();
        String sFormat = "yyyy-MM-dd HH:mm";
        String sFormat1 = "HH:mm";
        SimpleDateFormat sf = new SimpleDateFormat(sFormat);
        SimpleDateFormat sf1 = new SimpleDateFormat(sFormat1);
        Date date = new Date();
        try {
            date = sf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sb.append(sf1.format(date));

        return sb.toString();
    }

    public static String dateString2YearDay(String sDate) {
        StringBuffer sb = new StringBuffer();
        String sFormat = "yyyy-MM-dd HH:mm";
        String sFormat1 = "yyyy-MM-dd";
        SimpleDateFormat sf = new SimpleDateFormat(sFormat);
        SimpleDateFormat sf1 = new SimpleDateFormat(sFormat1);
        Date date = new Date();
        try {
            date = sf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sb.append(sf1.format(date));

        return sb.toString();
    }

    public static long getLongTime(String sDate) {
        String sFormat = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sf = new SimpleDateFormat(sFormat);
        Date date = new Date();
        try {
            date = sf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date.getTime();
    }

    public static String getWeeks(Context context, String[] arrWeeks) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arrWeeks.length; i++) {
            if ("1".equals(arrWeeks[i])) {
                sb.append(getWeek(context, i) + " ");
            }
        }
        return sb.toString();
    }

    public static String getWeek(Context context, int week) {
        String msg = "";
        if (0 == week) {
            msg = context.getResources().getString(R.string.week_1);
        } else if (1 == week) {
            msg = context.getResources().getString(R.string.week_2);
        } else if (2 == week) {
            msg = context.getResources().getString(R.string.week_3);
        } else if (3 == week) {
            msg = context.getResources().getString(R.string.week_4);
        } else if (4 == week) {
            msg = context.getResources().getString(R.string.week_5);
        } else if (5 == week) {
            msg = context.getResources().getString(R.string.week_6);
        } else if (6 == week) {
            msg = context.getResources().getString(R.string.week_7);
        }
        return msg;
    }

    public static boolean isDateLastDay(String sDate) {
        StringBuffer sb = new StringBuffer();
        String sFormat = "yyyy-MM-dd";
        SimpleDateFormat sf = new SimpleDateFormat(sFormat);
        Date date = new Date();
        try {
            date = sf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);

        int iLastDay = calendar.get(Calendar.DAY_OF_MONTH);
        int iDay = Integer.parseInt(dateString2Day(sDate));

        if (iLastDay == iDay) {
            return true;
        }

        return false;
    }

    public static String getUrl(String url, RequestParams params) {
        StringBuffer sb = new StringBuffer();
        sb.append(url);
        sb.append(params.toString());

        return sb.toString();
    }

    public static boolean isNum(String str) {
        if (null == str) {
            return false;
        }
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    public static int verCompare(String sVer1, String sVer2) {
        if (Utils.isEmpty(sVer1) || Utils.isEmpty(sVer2)) {
            return -1;
        }
        int result = 0;
        String[] arrVer1 = sVer1.replace(".", ",").split(",");
        String[] arrVer2 = sVer2.replace(".", ",").split(",");
        int size1 = arrVer1.length;
        int size2 = arrVer2.length;
        if (size1 > size2) {
            for (int i = 0; i < size2; i++) {
                int iVer1 = Integer.parseInt(arrVer1[i]);
                int iVer2 = Integer.parseInt(arrVer2[i]);
                if (iVer1 > iVer2) {
                    result = 1;
                    break;
                } else if (iVer1 < iVer2) {
                    result = -1;
                    break;
                }
                if (i == size2 - 1) {
                    result = 1;
                }
            }
        } else if (size1 < size2) {
            for (int i = 0; i < size1; i++) {
                int iVer1 = Integer.parseInt(arrVer1[i]);
                int iVer2 = Integer.parseInt(arrVer2[i]);
                if (iVer1 > iVer2) {
                    result = 1;
                    break;
                } else if (iVer1 < iVer2) {
                    result = -1;
                    break;
                }
                if (i == size1 - 1) {
                    result = -1;
                }
            }
        } else {
            for (int i = 0; i < size1; i++) {
                int iVer1 = Integer.parseInt(arrVer1[i]);
                int iVer2 = Integer.parseInt(arrVer2[i]);
                if (iVer1 > iVer2) {
                    result = 1;
                    break;
                } else if (iVer1 < iVer2) {
                    result = -1;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * 序列号类型：1.人 2.宠物 3.汽车 4.摩托车 5.无规则的
     *
     * @param sSn
     * @return
     */
    public static int serialNumberRange(String sSn) {
        int iType = 0;

        if (sSn.length() == 12) {
            if ("6".equals(sSn.substring(0, 1))
                    || "8".equals(sSn.substring(0, 1))) {
                String sType = sSn.substring(1, 3);
                if ("01".equals(sType) || "02".equals(sType)
                        || "06".equals(sType) || "07".equals(sType)
                        || "08".equals(sType) || "09".equals(sType)
                        || "10".equals(sType) || "14".equals(sType)
                        || "15".equals(sType) || "16".equals(sType)
                        || "17".equals(sType) || "18".equals(sType)
                        || "19".equals(sType) || "20".equals(sType)) {
                    iType = 1;
                } else if ("05".equals(sType) || "11".equals(sType)
                        || "13".equals(sType)) {
                    iType = 2;
                } else if ("04".equals(sType)) {
                    iType = 3;
                } else if ("03".equals(sType) || "12".equals(sType)) {
                    iType = 4;
                }
            }
        } else if (sSn.length() == 19) {
            String sType = sSn.substring(4, 9);
            if ("718SX".equals(sType) || "718AX".equals(sType)
                    || "718HX".equals(sType)) {
                iType = 1;
            }
        } else if (sSn.length() == 14) {
            iType = 5;
        } else if (13 == sSn.length()) {
            if (sSn.startsWith("718") || sSn.startsWith("719")) {
                iType = 1; // 个人
            } else if (sSn.startsWith("690")) {
                iType = 2; // 宠物
            } else if (sSn.startsWith("213")) {
                iType = 3; // 汽车
            } else if (sSn.startsWith("620")) {
                iType = 4; // 摩托车
            }
        }

        /**
         * if (13 == sSn.length()) { if(sSn.startsWith("718") ||
         * sSn.startsWith("719")) { iType = 1; // 个人 } else
         * if(sSn.startsWith("690")) { iType = 2; // 宠物 } else
         * if(sSn.startsWith("213")) { iType = 3; // 汽车 } else
         * if(sSn.startsWith("620")) { iType = 4; // 摩托车 } } if (14 ==
         * sSn.length()) { iType = 5; }
         */

        return iType;
    }

    public static boolean serialNumberRange719(int ranges, String sSn) {
        if (1 != ranges) {
            return false;
        }

        if (sSn.length() == 12) {
            if ("6".equals(sSn.substring(0, 1))
                    || "8".equals(sSn.substring(0, 1))) {
                String sType = sSn.substring(1, 3);
                if ("07".equals(sType) || "08".equals(sType)
                        || "19".equals(sType) || "20".equals(sType)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean serialNumberRange718(int ranges, String sSn) {
        if (1 != ranges) {
            return false;
        }
        if (sSn.length() == 12) {
            if ("6".equals(sSn.substring(0, 1))
                    || "8".equals(sSn.substring(0, 1))) {
                String sType = sSn.substring(1, 3);
                if ("01".equals(sType) || "02".equals(sType)
                        || "06".equals(sType) || "14".equals(sType)
                        || "15".equals(sType) || "16".equals(sType)
                        || "17".equals(sType) || "18".equals(sType)) {
                    return true;
                } else {
                    return false;
                }
            }
        } else if (sSn.length() == 19) {
            String sType = sSn.substring(4, 9);
            if ("718SX".equals(sType) || "718AX".equals(sType)
                    || "718HX".equals(sType)) {
                return true;
            }
        } else if (13 == sSn.length()) {
            if (sSn.startsWith("718")) {
                return true;
            }
        }
        return false;
    }

    public static boolean serialNumberRange7181(int ranges, String product_type) {
        if (1 != ranges) {
            return false;
        }
        if ("1".equals(product_type) || "2".equals(product_type)
                || "6".equals(product_type) || "14".equals(product_type)
                || "15".equals(product_type) || "16".equals(product_type)
                || "17".equals(product_type) || "18".equals(product_type)) {
            return true;
        } else {
            return false;
        }
    }

    public static String serialNumberType(String sSn) {
        String sType = "PT-718H";

        if (sSn.length() == 12) {
            if ("6".equals(sSn.substring(0, 1))
                    || "8".equals(sSn.substring(0, 1))) {
                int iType = Integer.parseInt(sSn.substring(1, 3));
                switch (iType) {
                    case 1:
                        sType = "PT-718H";
                        break;
                    case 2:
                        sType = "PT-718S";
                        break;
                    case 3:
                        sType = "MPIP-620S";
                        break;
                    case 4:
                        sType = "IDD-231TH";
                        break;
                    case 5:
                        sType = "PT-690G";
                        break;
                    case 6:
                        sType = "PT-718G";
                        break;
                    case 7:
                        sType = "PT-719G";
                        break;
                    case 8:
                        sType = "PT-719S";
                        break;
                    case 9:
                        sType = "PT-720G";
                        break;
                    case 10:
                        sType = "PT-720S";
                        break;
                    case 11:
                        sType = "PT-690S";
                        break;
                    case 12:
                        sType = "MPIP-620G";
                        break;
                    case 13:
                        sType = "PT-690C";
                        break;
                    default:
                        break;
                }
            }
        }

        return sType;
    }

//    static BitmapDescriptor bdLowLocation = BitmapDescriptorFactory
//            .fromResource(R.drawable.peopleslow_location);
//    static BitmapDescriptor bdNormalLocation = BitmapDescriptorFactory
//            .fromResource(R.drawable.peoplenormal_location);
//    static BitmapDescriptor bdFastLocation = BitmapDescriptorFactory
//            .fromResource(R.drawable.peoplefast_location);
//
//    static BitmapDescriptor bdLow = BitmapDescriptorFactory
//            .fromResource(R.drawable.peopleslow);
//    static BitmapDescriptor bdNormal = BitmapDescriptorFactory
//            .fromResource(R.drawable.peoplenormal);
//    static BitmapDescriptor bdFast = BitmapDescriptorFactory
//            .fromResource(R.drawable.peoplefast);
//
//    static BitmapDescriptor bdPetLowLocation = BitmapDescriptorFactory
//            .fromResource(R.drawable.petslow_location);
//    static BitmapDescriptor bdPetNormalLocation = BitmapDescriptorFactory
//            .fromResource(R.drawable.petnormal_location);
//    static BitmapDescriptor bdPetFastLocation = BitmapDescriptorFactory
//            .fromResource(R.drawable.petfast_location);
//
//    static BitmapDescriptor bdPetLow = BitmapDescriptorFactory
//            .fromResource(R.drawable.petslow);
//    static BitmapDescriptor bdPetNormal = BitmapDescriptorFactory
//            .fromResource(R.drawable.petnormal);
//    static BitmapDescriptor bdPetFast = BitmapDescriptorFactory
//            .fromResource(R.drawable.petfast);
//
//    static BitmapDescriptor bdCarLowLocation = BitmapDescriptorFactory
//            .fromResource(R.drawable.carslow_location);
//    static BitmapDescriptor bdCarNormalLocation = BitmapDescriptorFactory
//            .fromResource(R.drawable.carnormal_location);
//    static BitmapDescriptor bdCarFastLocation = BitmapDescriptorFactory
//            .fromResource(R.drawable.carfast_location);
//
//    static BitmapDescriptor bdCarLow = BitmapDescriptorFactory
//            .fromResource(R.drawable.carslow);
//    static BitmapDescriptor bdCarNormal = BitmapDescriptorFactory
//            .fromResource(R.drawable.carnormal);
//    static BitmapDescriptor bdCarFast = BitmapDescriptorFactory
//            .fromResource(R.drawable.carfast);
//
//    static BitmapDescriptor bdMotoLowLocation = BitmapDescriptorFactory
//            .fromResource(R.drawable.motoslow_location);
//    static BitmapDescriptor bdMotoNormalLocation = BitmapDescriptorFactory
//            .fromResource(R.drawable.motonormal_location);
//    static BitmapDescriptor bdMotoFastLocation = BitmapDescriptorFactory
//            .fromResource(R.drawable.motofast_location);
//
//    static BitmapDescriptor bdMotoLow = BitmapDescriptorFactory
//            .fromResource(R.drawable.motoslow);
//    static BitmapDescriptor bdMotoNormal = BitmapDescriptorFactory
//            .fromResource(R.drawable.motonormal);
//    static BitmapDescriptor bdMotoFast = BitmapDescriptorFactory
//            .fromResource(R.drawable.motofast);

//    public static MarkerOptions setMarkerOptions2Baidu(int type, int ranges,
//                                                       float speed, MarkerOptions markerOptions) {
//        if (1 == ranges || 5 == ranges || 7 == ranges) {
//            markerOptions.icon(bdLow);
//
//        } else if (2 == ranges) {
//            markerOptions.icon(bdPetLow);
//
//        } else if (3 == ranges || 6 == ranges) {
//            markerOptions.icon(bdCarLow);
//
//        } else if (4 == ranges) {
//            markerOptions.icon(bdMotoLow);
//        }
//        return markerOptions;
//    }


    public static com.google.android.gms.maps.model.MarkerOptions setMarkerOptions2Google(
            int type, int ranges, float speed, com.google.android.gms.maps.model.MarkerOptions markerOptions) {
        if (1 == ranges || 5 == ranges || 7 == ranges) {
            markerOptions
                    .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
                            .fromResource(R.drawable.peopleslow));
//			if (speed < 8) {
//				if (0 == type) {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.peopleslow_location));
//				} else {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.peopleslow));
//				}
//			} else if (speed >= 8 && speed <= 15) {
//				if (0 == type) {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.peoplenormal_location));
//				} else {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.peoplenormal));
//				}
//			} else {
//				if (0 == type) {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.peoplefast_location));
//				} else {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.peoplefast));
//				}
//			}
        } else if (2 == ranges) {
            markerOptions
                    .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
                            .fromResource(R.drawable.petslow));
//			if (speed < 20) {
//				if (0 == type) {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.petslow_location));
//				} else {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.petslow));
//				}
//			} else if (speed >= 20 && speed <= 35) {
//				if (0 == type) {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.petfast_location));
//				} else {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.petfast));
//				}
//			} else {
//				if (0 == type) {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.petfast_location));
//				} else {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.petfast));
//				}
//			}
        } else if (4 == ranges) {
            markerOptions
                    .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
                            .fromResource(R.drawable.motoslow));
//			if (speed < 30) {
//				if (0 == type) {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.motoslow_location));
//				} else {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.motoslow));
//				}
//			} else if (speed >= 30 && speed <= 50) {
//				if (0 == type) {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.motonormal_location));
//				} else {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.motonormal));
//				}
//			} else {
//				if (0 == type) {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.motofast_location));
//				} else {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.motofast));
//				}
//			}
        } else {
            markerOptions
                    .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
                            .fromResource(R.drawable.carslow));
//			if (speed < 60) {
//				if (0 == type) {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.carslow_location));
//				} else {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.carslow));
//				}
//			} else if (speed >= 60 && speed <= 100) {
//				if (0 == type) {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.carnormal_location));
//				} else {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.carnormal));
//				}
//			} else {
//				if (0 == type) {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.carfast_location));
//				} else {
//					markerOptions
//							.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
//									.fromResource(R.drawable.carfast));
//				}
//			}
        }
        return markerOptions;
    }


//    public static BitmapDescriptor setRouteStartMarkerOptionsIcon2Baidu(
//            int ranges) {
//        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_track_start);
//
//        return bitmapDescriptor;
//    }

//    public static BitmapDescriptor setRouteEndMarkerOptionsIcon2Baidu(int ranges) {
//        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_track_end);
//
//        return bitmapDescriptor;
//    }

    public static String getTimeForTimeZone(int timeZoneId, String[] arrTimeZone) {
        String sTimeZone = arrTimeZone[timeZoneId];
        String sTimeZoneId = sTimeZone.substring(1, sTimeZone.indexOf(")"));
        LogUtil.i(sTimeZoneId);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone(sTimeZoneId));

        return sdf.format(calendar.getTime());
    }

    public static String getTimeForTimeZone(int timeZoneId,
                                            String[] arrTimeZone, long lCheckTime) {
        String sTimeZone = arrTimeZone[timeZoneId];
        String sTimeZoneId = sTimeZone.substring(1, sTimeZone.indexOf(")"));
        LogUtil.i(sTimeZoneId);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone(sTimeZoneId));

        Date date = calendar.getTime();
        LogUtil.i("time1:" + date.getTime());
        LogUtil.i("time2:" + sdf.format(date));
        date.setTime(date.getTime() + lCheckTime);
        LogUtil.i("time3:" + date.getTime());
        LogUtil.i("time4:" + sdf.format(date));

        return sdf.format(date);
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static String getAlarmType(Context context, int typeId) {
        String[] infos = context.getResources().getStringArray(R.array.alarm_type);
        String sInfo = infos[0];
        switch (typeId) {
            case 2:
                sInfo = infos[0];// ACC关
                break;
            case 6:
                sInfo = infos[1];// sos报警
                break;
            case 8:
                sInfo = infos[2];// GPS故障报警
                break;

            case 14:
                sInfo = infos[3];// 超速报警
                break;
            case 16:
                sInfo = infos[4];// 主电剪线报警
                break;
            case 18:
                sInfo = infos[5];// 电压低报警
                break;
            case 24:
                sInfo = infos[6];// 断电/油
                break;
            case 28:
                sInfo = infos[7];// 拖吊报警
                break;
            case 29:
                sInfo = infos[8];// 越界报警
                break;
            case 30:
                sInfo = infos[9];// 碰撞报警
                break;
            case 31:
                sInfo = infos[10];// 盗车报警
                break;
            case 81:
                sInfo = infos[11];// 超速告警
                break;
            case 82:
                sInfo = infos[12];// 低电压告警
                break;

            case 83:
                sInfo = infos[13];// 水温告警

                break;
            case 84:
                sInfo = infos[14];// 急加速告警

                break;
            case 85:
                sInfo = infos[15];// 急减速告警

                break;
            case 86:
                sInfo = infos[16];// 停车未熄火告警

                break;
            case 87:
                sInfo = infos[17];// 拖吊告警

                break;

            case 88:
                sInfo = infos[18];// 转速高告警

                break;
            case 89:
                sInfo = infos[19];// 上电告警

                break;
            case 90:
                sInfo = infos[20];// 尾气超标

                break;
            case 91:
                sInfo = infos[21];// 急变道告警

                break;

            case 92:
                sInfo = infos[22];// 急转弯告警

                break;
            case 93:
                sInfo = infos[23];// 疲劳驾驶告警
                break;
            case 94:
                sInfo = infos[24];// 断电告警

                break;
            case 95:
                sInfo = infos[25];// 区域告警

                break;
            case 96:
                sInfo = infos[26];// 紧急告警

                break;

            case 97:
                sInfo = infos[27];// 碰撞告警

                break;
            case 98:
                sInfo = infos[28];// 防拆告警

                break;
            case 99:
                sInfo = infos[29];// 非法进入告警

                break;
            case 100:
                sInfo = infos[30];// 非法点火告警

                break;
            case 101:
                sInfo = infos[31];// OBD剪线告警

                break;
            case 102:
                sInfo = infos[32];// 点火告警

                break;
            case 103:
                sInfo = infos[33];// 熄火告警

                break;
            case 104:
                sInfo = infos[34];// MIL故障告警
                break;
            case 105:
                sInfo = infos[35];// 未锁车告警

                break;
            case 106:
                sInfo = infos[36];// 未刷卡告警

                break;
            case 107:
                sInfo = infos[37];// 危险驾驶告警

                break;
            case 108:
                sInfo = infos[38];// 震动告警

                break;
            case 3:
                sInfo = infos[39];// 震动报警
                break;

            case 35://手表脱落报警
                sInfo = infos[40];
                break;
            default:
                break;
        }
        return sInfo;
    }

    public static int getAlarmType2(Context context, int typeId) {
        // String[] infos = context.getResources().getStringArray(
        // R.array.alarm_type);
        // String sInfo = infos[0];
        int info = 1;
        switch (typeId) {
            case 6:
                // sInfo = infos[0];//sos报警
                info = 0;
                break;
            case 29:
                // sInfo = infos[1];//越界报警
                info = 1;
                break;
            case 18:
                // sInfo = infos[2];//低电报警
                info = 2;
                break;
            case 28:
                // sInfo = infos[8];//拖吊报警
                info = 3;
                break;
            case 16:
                // sInfo = infos[6];//主电剪线报警
                info = 4;
                break;
            case 14:
                // sInfo = infos[3];//超速报警
                info = 5;
                break;
            case 8:
                // sInfo = infos[4];//GPS故障报警
                info = 6;
                break;
            case 24:
                // sInfo = infos[5];//断电/油
                info = 6;
                break;

            case 31:
                // sInfo = infos[7];//盗车报警
                info = 6;
                break;

            default:
                break;
        }
        return info;
    }

    public static long timezone(String sGMT) {
        long time = 0;
        String str1 = sGMT.substring(sGMT.indexOf("(GMT") + 4,
                sGMT.indexOf(")"));
        if (isEmpty(str1)) {
            time = 0;
        } else {
            String str2;
            String strUnit = "";
            if ("-".equals(str1.substring(0, 1))) {
                strUnit = "-";
                str2 = str1.substring(1, str1.length());
            } else {
                str2 = str1.substring(1, str1.length());
            }
            String[] str = str2.split(":");
            LogUtil.i(str[0] + " " + str[1]);
            long time1 = Integer.parseInt(str[0]) * 60 * 60
                    + Integer.parseInt(str[1]) * 60;
            String str3 = strUnit + time1;
            LogUtil.i(str3);
            time = Integer.parseInt(str3.trim());
        }
        return time;
    }

    public static int setBateryImage(Context context, int batery) {
        int resId = R.drawable.batery_0;
        if (0 >= batery) {
            resId = R.drawable.batery_0;
        } else if (0 < batery && 10 >= batery) {
            resId = R.drawable.batery_10;
        } else if (10 < batery && 20 >= batery) {
            resId = R.drawable.batery_20;
        } else if (20 < batery && 30 >= batery) {
            resId = R.drawable.batery_30;
        } else if (30 < batery && 40 >= batery) {
            resId = R.drawable.batery_40;
        } else if (40 < batery && 50 >= batery) {
            resId = R.drawable.batery_50;
        } else if (50 < batery && 60 >= batery) {
            resId = R.drawable.batery_60;
        } else if (60 < batery && 70 >= batery) {
            resId = R.drawable.batery_70;
        } else if (70 < batery && 80 >= batery) {
            resId = R.drawable.batery_80;
        } else if (80 < batery && 90 >= batery) {
            resId = R.drawable.batery_90;
        } else if (90 < batery) {
            resId = R.drawable.batery_100;
        }

        return resId;
    }


    // google地图坐标偏移修正方法
    public static com.google.android.gms.maps.model.LatLng getCorrectedLatLng(
            Context context, double x6, double y6) {

        com.google.android.gms.maps.model.LatLng latLng = null;
        try {
            ModifyOffset offset = ModifyOffset.getInstance(context.getResources().openRawResource(R.raw.axisoffset));
            PointDouble point = new PointDouble(y6, x6);
            if (point.isInChina()) {
                PointDouble newdouble1 = offset.s2c(point);
                double x3 = newdouble1.getX();
                double y3 = newdouble1.getY();
                latLng = new com.google.android.gms.maps.model.LatLng(y3, x3);
                LogUtil.i("坐标偏移修正前:latitude=" + x6 + ",longitude=" + y6);
                LogUtil.i("坐标偏移修正后:latitude=" + latLng.latitude + ",longitude="
                        + latLng.longitude);
            } else {
                latLng = new com.google.android.gms.maps.model.LatLng(x6, y6);
                LogUtil.i("不是中国地区不用修正:latitude=" + latLng.latitude
                        + ",longitude=" + latLng.longitude);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return latLng;

    }

//    public static int getPixelsFromDp(Context context, float dp) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dp * scale + 0.5f);
//    }

    public static String arrDayToString(String[] strs) {
        if (strs == null)
            return "";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strs.length; i++) {
            if ("1".equals(strs[i])) {
                sb.append((i + 1) + ",");
            }
        }

        String msg = sb.toString();

        if (!"".equals(msg)) {
            msg = msg.substring(0, msg.length() - 1);
        }

        return msg;
    }

    public static String[] strDayToArr(String str) {
        if (TextUtils.isEmpty(str))
            return null;

        String[] strs = {"0", "0", "0", "0", "0", "0", "0"};

        if ("".equals(str)) {
            return strs;
        }

        String[] msgs = str.split(",");
        for (int i = 0; i < msgs.length; i++) {
            int position = Integer.parseInt(msgs[i]) - 1;
            strs[position] = "1";
        }

        return strs;
    }

    public static String strDayToWeek(Context context, String str) {
        if (TextUtils.isEmpty(str))
            return "";

        StringBuffer sb = new StringBuffer();

        String[] msgs = str.split(",");

        for (int i = 0; i < msgs.length; i++) {
            int week = Integer.parseInt(msgs[i]);

            if (1 == week) {
                sb.append(" "
                        + context.getResources().getString(R.string.week_1));
            } else if (2 == week) {
                sb.append(" "
                        + context.getResources().getString(R.string.week_2));
            } else if (3 == week) {
                sb.append(" "
                        + context.getResources().getString(R.string.week_3));
            } else if (4 == week) {
                sb.append(" "
                        + context.getResources().getString(R.string.week_4));
            } else if (5 == week) {
                sb.append(" "
                        + context.getResources().getString(R.string.week_5));
            } else if (6 == week) {
                sb.append(" "
                        + context.getResources().getString(R.string.week_6));
            } else if (7 == week) {
                sb.append(" "
                        + context.getResources().getString(R.string.week_7));
            }
        }

        LogUtil.d("week:" + sb.toString());

        return sb.toString();

    }

    public static String strDaylongToWeek(Context context, String str) {
        if (Utils.isEmpty(str)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        char[] msgs = str.toCharArray();
        //String[] msgs = str.split(",");

        for (int i = 0; i < msgs.length; i++) {
            int week = Integer.parseInt(msgs[i] + "");
            //LogUtil.i(msgs[i]+"");
            if (1 == week) {
                if (i == 0) {
                    sb.append(context.getResources().getString(R.string.week_7));
                } else if (i == 1) {
                    sb.append(" " + context.getResources().getString(R.string.week_1));
                } else if (i == 2) {
                    sb.append(" " + context.getResources().getString(R.string.week_2));
                } else if (i == 3) {
                    sb.append(" " + context.getResources().getString(R.string.week_3));
                } else if (i == 4) {
                    sb.append(" " + context.getResources().getString(R.string.week_4));
                } else if (i == 5) {
                    sb.append(" " + context.getResources().getString(R.string.week_5));
                } else if (i == 6) {
                    sb.append(" " + context.getResources().getString(R.string.week_6));
                }
            }
        }

        LogUtil.d("week:" + sb.toString());

        return sb.toString();

    }

    public static String strDaylongToString(String str) {
        if (TextUtils.isEmpty(str))
            return null;
        StringBuffer sb = new StringBuffer();
        char[] msgs = str.toCharArray();
        for (int i = 0; i < msgs.length; i++) {
            int week = Integer.parseInt(msgs[i] + "");
            //LogUtil.i(msgs[i]+"");
            if (1 == week) {
                if (i == 0) {
                    sb.append("7" + ",");
                } else if (i == 1) {
                    sb.append("1" + ",");
                } else if (i == 2) {
                    sb.append("2" + ",");
                } else if (i == 3) {
                    sb.append("3" + ",");
                } else if (i == 4) {
                    sb.append("4" + ",");
                } else if (i == 5) {
                    sb.append("5" + ",");
                } else if (i == 6) {
                    sb.append("6");
                }
            }
        }

        LogUtil.d("week:" + sb.toString());

        return sb.toString();

    }


    public static String strDayToWeek1(Context context, String str) {
        if (TextUtils.isEmpty(str))
            return null;
        String[] arrWeeks = {"0", "0", "0", "0", "0", "0", "0"};
        StringBuffer sb = new StringBuffer();

        String[] msgs = str.split(",");
        for (int i = 0; i < msgs.length; i++) {
            int week = Integer.parseInt(msgs[i]);
            if (1 == week) {
                arrWeeks[1] = "1";
            } else if (2 == week) {
                arrWeeks[2] = "1";
            } else if (3 == week) {
                arrWeeks[3] = "1";
            } else if (4 == week) {
                arrWeeks[4] = "1";
            } else if (5 == week) {
                arrWeeks[5] = "1";
            } else if (6 == week) {
                arrWeeks[6] = "1";
            } else if (7 == week) {
                arrWeeks[0] = "1";
            }
        }

        for (int i = 0; i < arrWeeks.length; i++) {
            sb.append(arrWeeks[i]);
        }
        LogUtil.d("week:" + sb.toString());

        return sb.toString();

    }


    public static boolean isOperate(Context context, Tracker tracker) {
        if (null == tracker) {
            DialogUtil.showAddDevice(context);
            return false;
        }

        if (UserUtil.isGuest(context)) {
            ToastUtil.show(context, R.string.guest_no_set);
            return false;
        }

        LogUtil.d("上传信息卡" + tracker.super_user + ","
                + UserSP.getInstance().getUserName(context));
        if (tracker.super_user != null
                && UserSP.getInstance().getUserName(context) != null) {
            if (!tracker.super_user.equalsIgnoreCase(UserSP.getInstance()
                    .getUserName(context))) {
                ToastUtil.show(context, R.string.no_super_user);
                return false;
            }
        }

        if (Utils.serialNumberRange719(tracker.ranges, tracker.device_sn)) {
            tracker = UserUtil.getCurrentTracker(context);
            if (4 == tracker.onlinestatus) {
                ToastUtil.show(context, R.string.dormancy_ing);
                return false;
            }
        }

        return true;
    }

    public static double getDistance(double longt1, double lat1, double longt2,
                                     double lat2) {
        double PI = 3.14159265358979323; // 圆周率
        double R = 6371229; // 地球的半径

        double x, y, distance;
        x = (longt2 - longt1) * PI * R
                * Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;
        y = (lat2 - lat1) * PI * R / 180;
        distance = Math.hypot(x, y);
        return distance;
    }

    /**
     * 获得指定日期的前一天
     *
     * @param specifiedDay
     * @return
     */
    public static String getSpecifiedDayBefore(String specifiedDay) {// 可以用new
        // Date().toLocalString()传递参数
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);

        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c
                .getTime());
        return dayBefore;
    }

    /**
     * 获得指定日期的后一天
     *
     * @param specifiedDay
     * @return
     */
    public static String getSpecifiedDayAfter(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + 1);

        String dayAfter = new SimpleDateFormat("yyyy-MM-dd")
                .format(c.getTime());
        return dayAfter;
    }

    /**
     * 两个时间之间相差距离多少天
     *
     * @return 相差天数
     * @paramone 时间参数 1：
     * @paramtwo 时间参数 2：
     */
    public static long getDistanceDays(String str1, String str2)
            throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date one;
        Date two;
        long days = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            days = diff / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    // 得到设备的昵称
    // deviceNo为设备号
    public static String getNickName(Context context, String deviceNo) {
        User user = UserUtil.getUserInfo(context);
        List<Tracker> trackers = user.device_list;
        String nickname = "";
        for (int i = 0; i < trackers.size(); i++) {
            if (trackers.get(i).device_sn.equalsIgnoreCase(deviceNo)) {
                nickname = trackers.get(i).nickname;
            }
        }
        return nickname;
    }

    public static int getStrCount(String src, String find) {
        int o = 0;
        int index = -1;
        while ((index = src.indexOf(find, index)) > -1) {
            ++index;
            ++o;
        }
        return o;
    }

    public static float getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static float getDisplayMetrics() {
        return App.getContext().getResources().getDisplayMetrics().density;
    }


    //是否超级用户
    public static boolean isSuperUser(Tracker mTracker, Context context) {
        if (null != mTracker) {
            if (mTracker.super_user != null
                    && UserSP.getInstance().getUserName(context) != null) {
                if (!mTracker.super_user.equalsIgnoreCase(UserSP.getInstance()
                        .getUserName(context))) {
                    ToastUtil.show(context, R.string.no_super_user);
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    //是否超级用户
    public static boolean isSuperUserNotprompt(Tracker mTracker, Context context) {

        if (null != mTracker) {
            LogUtil.i("超级用户q：" + mTracker.super_user);
            if (mTracker.super_user != null
                    && UserSP.getInstance().getUserName(context) != null) {
                if (!mTracker.super_user.equalsIgnoreCase(UserSP.getInstance()
                        .getUserName(context))) {

                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }


    public static com.google.android.gms.maps.model.BitmapDescriptor setRouteStartMarkerOptionsIcon2Google(
            int ranges) {
        com.google.android.gms.maps.model.BitmapDescriptor bitmapDescriptor = com.google.android.gms.maps.model.BitmapDescriptorFactory
                .fromResource(R.drawable.icon_track_start);
//		if (1 == ranges || 5 == ranges) {
//			bitmapDescriptor = com.google.android.gms.maps.model.BitmapDescriptorFactory
//					.fromResource(R.drawable.peopleslows);
//		} else if (2 == ranges) {
//			bitmapDescriptor = com.google.android.gms.maps.model.BitmapDescriptorFactory
//					.fromResource(R.drawable.petslows);
//		} else if (4 == ranges) {
//			bitmapDescriptor = com.google.android.gms.maps.model.BitmapDescriptorFactory
//					.fromResource(R.drawable.motoslows);
//		} else {
//			bitmapDescriptor = com.google.android.gms.maps.model.BitmapDescriptorFactory
//					.fromResource(R.drawable.carslows);
//		}
        return bitmapDescriptor;
    }

    public static com.google.android.gms.maps.model.BitmapDescriptor setRouteEndMarkerOptionsIcon2Google(
            int ranges) {
        com.google.android.gms.maps.model.BitmapDescriptor bitmapDescriptor = com.google.android.gms.maps.model.BitmapDescriptorFactory
                .fromResource(R.drawable.icon_track_end);
//		if (1 == ranges || 5 == ranges) {
//			bitmapDescriptor = com.google.android.gms.maps.model.BitmapDescriptorFactory
//					.fromResource(R.drawable.peopleslowe);
//		} else if (2 == ranges) {
//			bitmapDescriptor = com.google.android.gms.maps.model.BitmapDescriptorFactory
//					.fromResource(R.drawable.petslowe);
//		} else if (4 == ranges) {
//			bitmapDescriptor = com.google.android.gms.maps.model.BitmapDescriptorFactory
//					.fromResource(R.drawable.motoslowe);
//		} else {
//			bitmapDescriptor = com.google.android.gms.maps.model.BitmapDescriptorFactory
//					.fromResource(R.drawable.carslowe);
//		}
        return bitmapDescriptor;
    }


    // 读SD中的文件
    public static String readFileSdcardFile(String fileName) throws IOException {
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);

            int length = fin.available();

            byte[] buffer = new byte[length];
            fin.read(buffer);

            res = EncodingUtils.getString(buffer, "UTF-8");

            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


//    //发送数据
//    public static void sendBooBluetoothBroadcast(Context context, String data) {
//        LogUtil.i("data:" + data);
//        LogUtil.i("发送蓝牙广播数据");
//        Intent intent = new Intent();
//        intent.setAction(MainService.ACTION_BLUETOOTH_SEND_EXCD_CMD);
//        intent.putExtra(MainService.EXTRA_DATA, data.getBytes());
//        context.sendBroadcast(intent);
//    }


    /*
     * 将字符转为Unicode码表示
     */
    public static String string2unicode(String s) {
        int in;
        String st = "";
        for (int i = 0; i < s.length(); i++) {
            in = s.codePointAt(i);
            st = st + "\\u" + Integer.toHexString(in).toUpperCase();
        }
        return st;
    }


    /**
     * 字符串转换unicode
     */
    public static String string2Unicode(String string) {

        StringBuffer unicode = new StringBuffer();
        byte[] mm = new byte[1024];
        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append(Integer.toHexString(c));
            String hexString = Integer.toHexString(c);
            String substring = hexString.substring(0, 2);


            String substring1 = hexString.substring(2, 4);

            LogUtil.i("substring:" + substring + ",substring1:" + substring1);
        }

        return unicode.toString();
    }

    /**
     * 字符串转换unicode
     */
    public static String string2Unicode1(String string) {

        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            char c = string.charAt(i);

            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }


    /**
     * utf-8 转换成 unicode
     *
     * @param inStr
     * @return
     * @author fanhui
     * 2007-3-15
     */
    public static String utf8ToUnicode(String inStr) {
        char[] myBuffer = inStr.toCharArray();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inStr.length(); i++) {
            UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
            if (ub == UnicodeBlock.BASIC_LATIN) {
                //英文及数字等
                sb.append(myBuffer[i]);
            } else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                //全角半角字符
                int j = (int) myBuffer[i] - 65248;
                sb.append((char) j);
            } else {
                //汉字
                short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u" + hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * utf-8 转换成 unicode
     *
     * @param inStr
     * @return
     * @author fanhui
     * 2007-3-15
     */
    public static String utf8ToUnicode1(String inStr) {
        char[] myBuffer = inStr.toCharArray();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inStr.length(); i++) {
            UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
            if (ub == UnicodeBlock.BASIC_LATIN) {
                //英文及数字等
                sb.append(myBuffer[i]);
            } else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                //全角半角字符
                int j = (int) myBuffer[i] - 65248;
                sb.append((char) j);
            } else {
                //汉字
                short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u" + hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString();
    }


    //把二个字节数组组合成一个字节数组
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public static byte[] compositebyte(List<byte[]> list) {
        byte[] totalByte = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            totalByte = byteMerger(totalByte, list.get(i));
        }
        return totalByte;
    }


    /**
     * 不足20个字节的数组补全为20个字节数组
     *
     * @return
     */
    public static byte[] completionByte(int size, String data) {
        if (TextUtils.isEmpty(data)) // 判断是否有数据
            return null;

        byte[] totalByte = data.getBytes();// 获取字节数组

        int length = totalByte.length;
        if (length < size) {// 不足20字节后面补0x00到二十位
            byte[] completion = {0x00};
            for (int i = 0; i < size - length; i++) {
                totalByte = byteMerger(totalByte, completion);
            }
        }
        return totalByte;
    }

    public static String binary(byte[] bytes, int radix) {
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }


    /*
     * 获取时间差
     */
    public static long getDifferTime(String dateold, String datenew) {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long between = 0;
        try {
            Date begin = dfs.parse(dateold);
            Date end = dfs.parse(datenew);
            between = (end.getTime() - begin.getTime());// 得到两者的毫秒数
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return between;
    }


    public static float getHourData(float value) {
        float valueData = 0;
        BigDecimal b = new BigDecimal(value);
        float f = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return f;
    }

    public static Bitmap createBitmap(Bitmap bitmap, int onLine) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap faceIconGreyBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(faceIconGreyBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        if (onLine == 0)
            colorMatrix.setSaturation(0.1f);
        else colorMatrix.setSaturation(1.0f);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return faceIconGreyBitmap;
    }

    public static String getPhotoString(String data, int position, int photoPostion) {
        LogUtil.i("data:" + data + "&&&&&&&&posion:" + position + ",photoPosition:" + photoPostion);
        if (data == null) {
            return ",,,,,,,,,";
        } else {
            String[] split = data.split(",", 10);
            if (split.length > position) {
                split[position] = photoPostion + "";
            }
            LogUtil.i("split size:" + split.length);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 10; i++) {
                if (i == split.length - 1) {
                    sb.append(split[i]);
                } else {
                    sb.append(split[i] + ",");
                }
            }
            LogUtil.i("sb:" + sb.toString());
            return sb.toString();
        }
    }


    public static int getPhotoSubscript(String data, int position) {
        LogUtil.i("data:" + data + "&&&&&&&&posion:" + position);
        int subscript = 0;
        if (data == null) {
            return 0;
        } else {
            String[] split = data.split(",", 10);

            if (split.length > position) {
                String subscript1 = split[position];
                if (isEmpty(subscript1)) {
                    subscript = 0;

                } else if (Integer.parseInt(subscript1) == 10) {
                    subscript = 0;
                } else {
                    subscript = Integer.parseInt(subscript1);
                }
            }
        }
        return subscript;
    }


    public static short byte2short(byte[] b) {
        return (short) (b[1] & 0xFF | (b[0] & 0xFF) << 8);
    }

    public static int byte2int(byte[] b) {
        return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
    }


    public static byte[] hexToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }

        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] bytes = new byte[length];
        String hexDigits = "0123456789abcdef";
        for (int i = 0; i < length; i++) {
            int pos = i * 2; // 两个字符对应一个byte
            int h = hexDigits.indexOf(hexChars[pos]) << 4; // 注1
            int l = hexDigits.indexOf(hexChars[pos + 1]); // 注2
            if (h == -1 || l == -1) { // 非16进制字符
                return null;
            }
            bytes[i] = (byte) (h | l);
        }
        return bytes;
    }


    public static short LowToShort(short a) {
        return (short) (((a & 0xFF) << 8) | ((a >> 8) & 0xFF));
    }

    public static int LowToInt(int a) {
        return (((a & 0xFF) << 24) | (((a >> 8) & 0xFF) << 16) | (((a >> 16) & 0xFF) << 8) | ((a >> 24) & 0xFF));
    }


    public static byte[] intToByte(int i, int len) {
        byte[] abyte = null;
        if (len == 1) {
            abyte = new byte[len];
            abyte[0] = (byte) (0xff & i);
        } else {
            abyte = new byte[len];
            abyte[0] = (byte) (0xff & i);
            abyte[1] = (byte) ((0xff00 & i) >> 8);
            //abyte[2] = (byte) ((0xff0000 & i) >> 16);
            // abyte[3] = (byte) ((0xff000000 & i) >> 24);
        }
        return abyte;
    }


    public static short get2Bytes(byte[] from, int fromIndex) {
        int high = from[fromIndex] & 0xff;
        int low = from[fromIndex + 1] & 0xff;
        return (short) (high << 8 + low);
    }


    public static byte[] short2byte(int n) {
        byte[] b = new byte[2];
        b[1] = ((byte) (n >> 8));
        b[0] = ((byte) n);
        return b;
    }

    public static byte[] Reversalbyte(byte[] datas) {
        byte[] b = new byte[2];
        b[1] = datas[0];
        b[0] = datas[1];
        return b;
    }


    public static byte[] intToByte(int n) {
        byte[] b = new byte[4];
        b[0] = ((byte) (n >> 24));
        b[1] = ((byte) (n >> 16));
        b[2] = ((byte) (n >> 8));
        b[3] = ((byte) n);
        return b;
    }

    public static byte[] long2byte(long n) {
        byte[] b = new byte[8];
        b[0] = ((byte) (int) (n >> 56));
        b[1] = ((byte) (int) (n >> 48));
        b[2] = ((byte) (int) (n >> 40));
        b[3] = ((byte) (int) (n >> 32));
        b[4] = ((byte) (int) (n >> 24));
        b[5] = ((byte) (int) (n >> 16));
        b[6] = ((byte) (int) (n >> 8));
        b[7] = ((byte) (int) n);
        return b;
    }


    private static byte[] getByte(int data) {
        byte[] b = new byte[2];
        byte data1 = (byte) ((data & 0xff00) >> 8);
        byte data2 = (byte) (data & 0xff);
        b[1] = data1;
        b[0] = data2;
        return b;
    }

    private static int Checksum(byte[] datas) {
        int i, sum = 0;
        for (i = 0; i < datas.length; i++) {
            sum += datas[i];//将每个数相加
            if (sum > 0xff) {
                sum = ~sum;
                sum += 1;
            }
        }
        return sum & 0xff;

    }

    private static byte[] SumCheck(byte[] msg, int length) {
        long mSum = 0;
        byte[] mByte = new byte[length];

        /** 逐Byte添加位数和 */
        for (byte byteMsg : msg) {
            long mNum = ((long) byteMsg >= 0) ? (long) byteMsg : ((long) byteMsg + 256);
            mSum += mNum;
        } /** end of for (byte byteMsg : msg) */

        /** 位数和转化为Byte数组 */
        for (int liv_Count = 0; liv_Count < length; liv_Count++) {
            mByte[length - liv_Count - 1] = (byte) (mSum >> (liv_Count * 8) & 0xff);
        } /** end of for (int liv_Count = 0; liv_Count < length; liv_Count++) */

        return mByte;
    }

    /**
     * 当地时间 ---> UTC时间
     *
     * @return
     */
    public static String Local2UTC() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
        String gmtTime = sdf.format(new Date());
        return gmtTime;
    }

    /**
     * 获取当前时间年月日
     *
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String gmtTime = sdf.format(new Date());
        return gmtTime;
    }

    /**
     * UTC时间 ---> 当地时间
     *
     * @param utcTime UTC时间
     * @return
     */
    public static String utc2Local(String utcTime) {
        SimpleDateFormat utcFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//UTC时间格式
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormater.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat localFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//当地时间格式
        localFormater.setTimeZone(TimeZone.getDefault());
        String localTime = localFormater.format(gpsUTCDate.getTime());
        return localTime;
    }


    public static String StringtoTime(String time) {
        if (Utils.isEmpty(time)) {
            return "00:00";
        }
        int intTime = Integer.parseInt(time);
        if (intTime <= 9) {
            return "00:0" + intTime;
        } else if (intTime <= 60 && intTime > 9) {
            return "00:" + intTime;
        } else {
            int hour = intTime / 60;
            int minute = intTime % 60;
            String finallyHour = hour < 10 ? ("0" + hour) : hour + "";
            String finallyMinute = minute < 10 ? ("0" + minute) : minute + "";
            return finallyHour + ":" + finallyMinute;
        }
    }

    public static String format1(String value) {
        if (isEmpty(value)) {
            return "--";
        }
        double value1 = Double.parseDouble(value);

        BigDecimal bd = new BigDecimal(value1);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.toString();
    }

    private static Map<String, String> sa;

    public static Map<String, String> getHashMap() {
        if (sa == null)
            sa = new HashMap();
        return sa;
    }

    /**
     * 计算dip单位值
     */
    public static int dipToPx(float dip) {
        float scale = AppApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 时区
     */
    public static int getTimeZone(Context mContext) {
        String[] arrTimeZone = mContext.getApplicationContext().getResources().getStringArray(R.array.time_zone);
        TimeZone tz = TimeZone.getDefault();
        String s = "TimeZone   " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID();
        LogUtil.i(s);
        String sTimeZone = tz.getDisplayName(false, TimeZone.SHORT);
        for (int i = 1; i < arrTimeZone.length; i++) {
            String str = arrTimeZone[i].substring(arrTimeZone[i].indexOf("(") + 1, arrTimeZone[i].indexOf(")"));
            if (str.equals(sTimeZone)) {
                return i;
            }
        }
        return 31; // (GMT) 格林威治标准时间: 都柏林, 爱丁堡, 伦敦, 里斯本
    }

    /**
     * 分钟转小时
     * value 保留的数据
     * format 保留小数格式
     *
     * @return
     */
    public static float getDecimal(float value, String format) {
        DecimalFormat df = new DecimalFormat(format);
        return Float.valueOf(df.format(value));
    }
}
