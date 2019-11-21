package com.bluebud.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.bluebud.activity.settings.IMMallActivity;
import com.bluebud.info.IMMessage;
import com.bluebud.liteguardian_hk.R;

import java.util.Random;

public class ServiceIMUtil {
    private final Random random = new Random(System.currentTimeMillis());
    private Service mContext;
    private final static int GRAY_SERVICE_ID = 1001;
    public ServiceIMUtil(Service context) {
        this.mContext = context;
    }

    /**
     * 显示警情通知栏
     * @param imBean
     */
    @RequiresApi(api = 26)
    public void showNotification(IMMessage.ImBean imBean) {
        NotificationManager notifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("2", "提醒", NotificationManager.IMPORTANCE_HIGH);
            notifyManager.createNotificationChannel(channel);
        }
        String sNotifiTitle = mContext.getString(R.string.im_new_messag);
        Intent intent = new Intent();
//        intent.putExtra("from_id", imBean.from_user_id);
//        intent.putExtra("goods_id", goods_id);
        intent.putExtra("store_id", imBean.store_id);
//        intent.putExtra("message_type", imBean.message_type);
//        intent.putExtra("goods_id",imBean.goods_id);
        intent.setClass(mContext, IMMallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(mContext);
        } else {
            builder = new NotificationCompat.Builder(mContext, "2");
        }
        /**设置通知左边的大图标**/
//        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ecmoban_logo));
        /**设置通知右边的小图标**/
        builder.setSmallIcon(R.drawable.ic_launcher);
        /**设置通知的标题**/
        builder.setContentTitle(sNotifiTitle);
        /**设置通知的内容**/
        builder.setContentText(imBean.name+":"+imBean.message);
        /**通知产生的时间，会在通知信息里显示**/
        builder.setWhen(System.currentTimeMillis());
        /**设置该通知优先级**/
        builder.setPriority(Notification.PRIORITY_MAX);//最高级
        /**设置这个标志当用户单击面板就可以让通知将自动取消**/
        builder.setAutoCancel(true);
        /**设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)**/
        builder.setOngoing(false);
        builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
        builder.setContentIntent(PendingIntent.getActivity(mContext, random.nextInt(), intent, PendingIntent.FLAG_CANCEL_CURRENT));
        Notification build = builder.build();
        /**发起通知**/
        notifyManager.notify(random.nextInt(), build);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startForegroundNF(){
        if (Build.VERSION.SDK_INT < 18) {
            mContext.startForeground(GRAY_SERVICE_ID, new Notification());//Android4.3以下 ，隐藏Notification上的图标
        } else if (Build.VERSION.SDK_INT > 18 && Build.VERSION.SDK_INT < 25) {//Android4.3 - Android7.0，隐藏Notification上的图标
//            Intent innerIntent = new Intent(mContext, JWebSocketClientService.GrayInnerService.class);
//            mContext.startService(innerIntent);
            mContext.startForeground(GRAY_SERVICE_ID, new Notification());
        } else {
            mContext.startForeground(GRAY_SERVICE_ID, showNotification());//Android7.0以上app启动后通知栏会出现一条"正在运行"的通知
        }
    }

    /**
     * 启动前台通知栏
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification showNotification() {
        NotificationManager notifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("3", "提醒", NotificationManager.IMPORTANCE_HIGH);
            notifyManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "3");
        /**设置通知右边的小图标**/
        builder.setSmallIcon(R.drawable.ic_launcher);
        /**设置这个标志当用户单击面板就可以让通知将自动取消**/
        builder.setAutoCancel(true);
        /**设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)**/
        builder.setOngoing(false);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        Notification build = builder.build();
        /**发起通知**/
        notifyManager.notify(random.nextInt(), build);
        return build;
    }
}
