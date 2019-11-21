package com.bluebud.chat.listener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.bluebud.utils.LogUtil;

import io.rong.push.PushType;
import io.rong.push.RongPushClient.ConversationType;
import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * 当您的应用处于后台运行或者和融云服务器 disconnect() 的时候，如果收到消息，融云 SDK 会以通知形式提醒您。 所以您还需要自定义一个继承融云
 * PushMessageReceiver 的广播接收器，用来接收提醒通知
 *
 * @author Administrator
 */
public class SealNotificationReceiver extends PushMessageReceiver {

//    @Override
//    public boolean onNotificationMessageArrived(Context mContext, PushNotificationMessage message) {
////        SealNotificationReceiver.isPush = true;
////        Log.e("TAG","推送内容=" + message.getPushData());
////        if(message!=null&&!message.getConversationType().getName().equals("group")) //不是群消息点击不跳转
////            return true;
//        return true;
//
//    }

//    @Override
//    public boolean onNotificationMessageClicked(Context mContext, PushNotificationMessage message) {
//        if(message!=null&&!message.getConversationType().getName().equals("group")) //不是群消息点击不跳转
//            return true;
//
//        LogUtil.e("message=="+message);
//        Intent intent = new Intent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri.Builder builder = Uri.parse("rong://" + mContext.getPackageName()).buildUpon();
//        builder.appendPath("conversation").appendPath(ConversationType.GROUP.getName())
//                .appendQueryParameter("targetId", message.getTargetId())
//                .appendQueryParameter("title", "微聊");
//        Uri uri = builder.build();
//        intent.setData(uri);
//        mContext.startActivity(intent);
//        return true;
//    }

    @Override
    public boolean onNotificationMessageArrived(Context context, PushType pushType, PushNotificationMessage pushNotificationMessage) {
        return false;
    }

    @Override
    public boolean onNotificationMessageClicked(Context mContext, PushType pushType, PushNotificationMessage message) {
        if (message != null && !message.getConversationType().getName().equals("group")) //不是群消息点击不跳转
            return true;

        LogUtil.e("message==" + message);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri.Builder builder = Uri.parse("rong://" + mContext.getPackageName()).buildUpon();
        builder.appendPath("conversation").appendPath(ConversationType.GROUP.getName())
                .appendQueryParameter("targetId", message.getTargetId())
                .appendQueryParameter("title", "微聊");
        Uri uri = builder.build();
        intent.setData(uri);
        mContext.startActivity(intent);
        return true;
    }
}
