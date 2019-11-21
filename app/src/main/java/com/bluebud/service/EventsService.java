package com.bluebud.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.bluebud.activity.settings.AlarmClockHistoryActivity;
import com.bluebud.data.dao.AlarmClockDao;
import com.bluebud.data.dao.AlarmClockHistoryDao;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.info.AlarmClockHistoryInfo;
import com.bluebud.info.AlarmClockInfo;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EventsService extends Service {
    private NotificationManager notificationManager;

    private String sUserName;

    private AlarmClockDao alarmClockDao;
    private List<AlarmClockInfo> alarmClockInfos;

    private static final Random random = new Random(System.currentTimeMillis());

//    private PayPalDao payPalDao;

    // 定时器
    private final static int TIME = 60 * 1000;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            getAlarmClock();

//			Notifier notifier = new Notifier(EventsService.this);
//			notifier.notify("100", "123", "警情推送", "超速告警", "castelecom");

            // getPayPalOrder();

            handler.postDelayed(this, TIME);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        sUserName = UserSP.getInstance().getUserName(EventsService.this);
        alarmClockDao = new AlarmClockDao(this);
        alarmClockInfos = new ArrayList<AlarmClockInfo>();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_TIME_TICK);//时间改变广播
        registerReceiver(mReceiver, mFilter);//注册广播
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler.postDelayed(runnable, TIME);// 计时器开始工作

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 显示通知栏
     *
     * @param title
     */
    @SuppressLint("WrongConstant")
    private void showNotification(String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        /**设置通知左边的大图标**/
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        /**设置通知右边的小图标**/
        builder.setSmallIcon(R.drawable.ic_launcher);
        /**通知首次出现在通知栏，带上升动画效果的**/
        builder.setTicker(title);
        /**设置通知的标题**/
//        builder.setContentTitle(title);
        /**设置通知的内容**/
        builder.setContentText(text);
        /**通知产生的时间，会在通知信息里显示**/
        builder.setWhen(System.currentTimeMillis());
        /**设置该通知优先级**/
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        /**设置这个标志当用户单击面板就可以让通知将自动取消**/
        builder.setAutoCancel(true);
        /**向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：**/
        int ringing = UserUtil.getUserInfo(EventsService.this).alert_mode;
        if (1 == ringing) {
            builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
        } else if (3 == ringing) {
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
        } else if (4 == ringing) {
            builder.setDefaults(Notification.DEFAULT_SOUND);
        }
        /**设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)**/
        builder.setOngoing(false);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(this, AlarmClockHistoryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setFlags(Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL);
        builder.setContentIntent(PendingIntent.getActivity(this, random.nextInt(), intent, PendingIntent.FLAG_CANCEL_CURRENT));
        Notification build = builder.build();
        /**发起通知**/
        notificationManager.notify(random.nextInt(), build);
    }


    private void getAlarmClock() {
        new Thread() {
            @Override
            public void run() {
                alarmClockInfos = alarmClockDao.query(sUserName);
                if (null == alarmClockInfos) {
                    return;
                }
                String sCurDays = Utils.curDate2Day(EventsService.this);
                String sCurMonth = Utils.curDate2CharMonth(EventsService.this);
                String sCurDay = Utils.curDate2CharDay(EventsService.this);
                String sCurTime = Utils.curDate2Hour(EventsService.this);
                String sCurWeek = Utils.curDate2CharWeek(EventsService.this);

                for (AlarmClockInfo alarmClockInfo : alarmClockInfos) {
                    boolean isYear = false;
                    boolean isMonth = false;
                    boolean isWeek = false;
                    if (0 == alarmClockInfo.iType) {
                        String sDays = alarmClockInfo.sDay;
                        String sMonth = Utils.dateString2Month(sDays);
                        String sDay = Utils.dateString2Day(sDays);
                        if (sCurMonth.equals(sMonth) && sCurDay.equals(sDay)) {
                            isYear = true;
                        }
                    } else if (1 == alarmClockInfo.iType) {
                        String sDays = alarmClockInfo.sDay;
                        String sMonth = Utils.dateString2Month(sDays);
                        String sDay = Utils.dateString2Day(sDays);
                        if (alarmClockInfo.isEnd
                                && Utils.isDateLastDay(sCurDays)) {
                            isMonth = true;
                        } else {
                            if (sCurDay.equals(sDay)) {
                                isMonth = true;
                            }
                        }
                    } else if (2 == alarmClockInfo.iType) {
                        for (int i = 0; i < alarmClockInfo.arrWeeks.length; i++) {
                            if (i == alarmClockInfo.arrWeeks.length - 1) {
                                if (sCurWeek.equals("1")
                                        && alarmClockInfo.arrWeeks[i]
                                        .equals("1")) {
                                    isWeek = true;
                                }
                            } else {
                                if (alarmClockInfo.arrWeeks[i].equals("1")
                                        && (Integer.parseInt(sCurWeek) == i + 2)) {
                                    isWeek = true;
                                }
                            }
                        }
                    }

                    if (!(isYear || isMonth || isWeek)) {
                        return;
                    }
                    for (String str : alarmClockInfo.times) {
                        if (str.equals(sCurTime)) {
                            AlarmClockHistoryInfo alarmClockHistoryInfo = new AlarmClockHistoryInfo();
                            alarmClockHistoryInfo.type = alarmClockInfo.iType;
                            alarmClockHistoryInfo.user_name = alarmClockInfo.sUserName;
                            alarmClockHistoryInfo.title = alarmClockInfo.title;
                            if (2 == alarmClockInfo.iType) {
                                alarmClockHistoryInfo.week = sCurWeek;
                            } else {
                                alarmClockHistoryInfo.day = alarmClockInfo.sDay;
                            }
                            alarmClockHistoryInfo.time = str;
                            AlarmClockHistoryDao alarmClockHistoryDao = new AlarmClockHistoryDao(
                                    EventsService.this);
                            alarmClockHistoryDao.insert(alarmClockHistoryInfo);

                            showNotification(
                                    getResources().getString(
                                            R.string.lift_helper),
                                    alarmClockInfo.title);

                            sendBroadcast(new Intent(Constants.ACTION_CLOCK));
                        }
                    }

                }
            }

            ;
        }.start();
    }

    /**
     * 时间广播
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.ACTION_TIME_TICK.equals(intent.getAction())) {//时间改变广播
                startIMService(context);//定时一分钟调用一次
            }
        }
    };

    /**
     * 启动客服消息服务
     *
     * @param context
     */
    private void startIMService(Context context) {
        //Android4.3 - Android7.0，隐藏Notification上的图标
        Intent serviceIM = new Intent(context, IMLiteGuardianService.class);
        serviceIM.putExtra("START_IM", true);
        startService(serviceIM);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        unregisterReceiver(mReceiver);
        // payPalDao.close();
        super.onDestroy();
    }

}
