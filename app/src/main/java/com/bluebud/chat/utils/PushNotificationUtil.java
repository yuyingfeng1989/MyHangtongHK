package com.bluebud.chat.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.bluebud.activity.MyAlarmDetailActivity;
import com.bluebud.info.Alarm;
import com.bluebud.info.PushAlarmInfo;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;

import java.util.Random;

import io.rong.eventbus.EventBus;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Administrator on 2017/8/14 0014.
 */

public class PushNotificationUtil {

    private final Random random = new Random(System.currentTimeMillis());
    private Context context;
    private NotificationManager notificationManager;
//    private List<Alarm> alarms;

    /**
     * 8.0以上手机需要构建通知渠道，才能够打开通知栏
     * <p>
     * channelId   通知栏id
     * channelName 通知栏名
     * importance  通知栏级别 例如NotificationManager.IMPORTANCE_HIGH;
     */
    public PushNotificationUtil(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "警情", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

    }

    @RequiresApi(api = 26)
    public void notify(PushAlarmInfo pushAlarmInfo) {
//		message = "{\"msgType\":\"1\",\"equipId\":\"2676718HX2014002676\",\"alarmtype\":\"1\","
//				+ "\"localDateTime\":\"2010-01-12 13:44:52\",\"lng\":\"113.936247\","
//				+ "\"lat\":\"22.547038\",\"speed\":\"0.00\",\"direction\":\"0.00\","
//				+ "\"ranges\":\"1\",\"timezone\":\"288000\"}";
//        PushAlarmInfo pushAlarmInfo = GsonParse.pushAlarmInfoParse(message);
        if (null == pushAlarmInfo) {
            return;
        }
        LogUtil.e(pushAlarmInfo.toString());
        if (3 == pushAlarmInfo.msgType) {
            Intent intent = new Intent();
            intent.setAction(com.bluebud.utils.Constants.ACTION_ACC_STATUS);
            intent.putExtra("PUSH_ALARM_INFO", pushAlarmInfo);
            context.sendBroadcast(intent);
            return;
        } else if (4 == pushAlarmInfo.msgType) {//litefamily闹钟通知
            lifamilyAlarm(pushAlarmInfo);
        } else if (1 == pushAlarmInfo.msgType) {//警情
            showNotification(context, pushAlarmInfo);
        }
    }

    /**
     * 显示警情通知栏
     */

    @RequiresApi(api = 26)
    private void showNotification(Context context, PushAlarmInfo pushAlarmInfo) {
//        if (alarms == null)
//            alarms = new ArrayList<>();
//        alarms.clear();
        Alarm alarm = new Alarm();
        alarm.lat = pushAlarmInfo.lat;
        alarm.lng = pushAlarmInfo.lng;
        alarm.dtime = pushAlarmInfo.localDateTime;
        alarm.type = pushAlarmInfo.alarmtype;
        alarm.serialNumber = pushAlarmInfo.equipId;
        alarm.speed = pushAlarmInfo.speed;
//        alarms.add(alarm);

//        AlarmDao alarmDao = new AlarmDao(context);
//        alarmDao.insert(alarms, UserSP.getInstance().getUserName(context));
        EventBus.getDefault().post(alarm);//更新警情列表
        String sNotifiTitle = Utils.getAlarmType(context, pushAlarmInfo.alarmtype);
        String sNotifiMessage = context.getResources().getString(R.string.tracker_no) + ":" + alarm.serialNumber;

        Intent intent = new Intent(context, MyAlarmDetailActivity.class);
//        if (0 == UserSP.getInstance().getServerAndMap(context)) {//百度地图
//            intent.setClass(context, AlarmReadActivity.class);
//        } else {
//            intent.setClass(context, AlarmGoogleActivity.class);
//        }

        LogUtil.e("纬度=" + pushAlarmInfo.lat + " 经度=" + pushAlarmInfo.lng);
        intent.putExtra("lat", pushAlarmInfo.lat);
        intent.putExtra("lng", pushAlarmInfo.lng);
        intent.putExtra("dtime", pushAlarmInfo.localDateTime);
        intent.putExtra("speed", pushAlarmInfo.speed);
//        intent.putExtra("type", pushAlarmInfo.ranges);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context);
        } else {
            builder = new NotificationCompat.Builder(context, "1");
        }
        /**设置通知左边的大图标**/
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        /**设置通知右边的小图标**/
        builder.setSmallIcon(R.drawable.ic_launcher);
        /**通知首次出现在通知栏，带上升动画效果的**/
//        builder.setTicker(sNotifiTitle);
        /**设置通知的标题**/
        builder.setContentTitle(sNotifiTitle);
        /**设置通知的内容**/
        builder.setContentText(sNotifiMessage);
        /**通知产生的时间，会在通知信息里显示**/
        builder.setWhen(System.currentTimeMillis());
        /**设置该通知优先级**/
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        /**设置这个标志当用户单击面板就可以让通知将自动取消**/
        builder.setAutoCancel(true);
        /**设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)**/
        builder.setOngoing(false);
        /**向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：**/
        int ringing = UserUtil.getUserInfo(context).alert_mode;
        if (1 == ringing) {
            builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
        } else if (3 == ringing) {
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
        } else if (4 == ringing) {
            builder.setDefaults(Notification.DEFAULT_SOUND);
        }
        builder.setContentIntent(PendingIntent.getActivity(context, random.nextInt(), intent, PendingIntent.FLAG_CANCEL_CURRENT));
        Notification build = builder.build();
        /**发起通知**/
        notificationManager.notify(random.nextInt(), build);
//        context.sendBroadcast(new Intent(com.bluebud.utils.Constants.ACTION_NEW_MESSAGE));

    }

    /**
     * litefamily通知栏
     */
    private void lifamilyAlarm(PushAlarmInfo pushAlarmInfo) {
        String sNotifiTitle = pushAlarmInfo.title;
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context);
        } else {
            builder = new NotificationCompat.Builder(context, "1");
        }
        /**设置通知右边的小图标**/
        builder.setSmallIcon(R.drawable.ic_launcher);
        /**设置通知左边的大图标**/
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        /**通知首次出现在通知栏，带上升动画效果的**/
//        builder.setTicker(sNotifiTitle);
        /**设置通知的标题**/
//        builder.setContentTitle(title);
        /**设置通知的内容**/
        builder.setContentText(sNotifiTitle);
        /**通知产生的时间，会在通知信息里显示**/
        builder.setWhen(System.currentTimeMillis());
        /**设置该通知优先级**/
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        /**设置这个标志当用户单击面板就可以让通知将自动取消**/
        builder.setAutoCancel(true);
        /**设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)**/
        builder.setOngoing(false);
        Intent intent = new Intent();
//        intent.setFlags(Notification.FLAG_AUTO_CANCEL);
        builder.setContentIntent(PendingIntent.getActivity(context, random.nextInt(), intent, PendingIntent.FLAG_CANCEL_CURRENT));
        Notification build = builder.build();
        /**发起通知**/
        notificationManager.notify(random.nextInt(), build);
    }
}
