package com.bluebud.chat.listener;

import android.support.annotation.RequiresApi;
import com.bluebud.app.AppApplication;
import com.bluebud.chat.utils.PushNotificationUtil;
import com.bluebud.info.PushAlarmInfo;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;

import io.rong.eventbus.EventBus;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.ProfileNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * Created by Administrator on 2017/5/18 0018.
 */
public class MyReceiveMessageListener implements RongIMClient.OnReceiveMessageListener {
    //  String operation = pro.getOperation();//判断是警情还是位置  12289 位置 ，12290 警情,12291是否在线
    private PushNotificationUtil util = new PushNotificationUtil(AppApplication.getInstance());

    /**
     * 返回true接收消息不震动，返回false后台接收消息震动
     */
    @RequiresApi(api = 26)
    @Override
    public boolean onReceived(Message message, int i) {
        if (i > 1)//离线推送消息去掉
            return true;

        MessageContent messageContent = message.getContent();
        if (messageContent instanceof TextMessage) {// 文本消息
            if (i > 0)
                return true;
            return false;
        } else if (messageContent instanceof VoiceMessage) {// 语音消息
            if (i > 0)
                return true;
            return false;
        } else if(messageContent instanceof ImageMessage){//图片消息
            if (i > 0)
                return true;
            return  false;
        }
        else if (messageContent instanceof ProfileNotificationMessage) {//推送定位,包含警情
            ProfileNotificationMessage pro = (ProfileNotificationMessage) messageContent;
            LogUtil.e("接收推送消息i==" + i);
            if (pro == null)
                return true;
            String textpush = pro.getExtra();
            if (textpush != null) {//推送的消息
                PushAlarmInfo pushAlarmInfo = GsonParse.pushAlarmInfoParse(textpush);
                util.notify(pushAlarmInfo);
            }
            return true;
        } else if (message != null && message.getConversationType().getName().equals("system")) { // 系统消息，增加群成员和退出群聊天人员
            LogUtil.e("系统消息");
            if (messageContent instanceof CommandNotificationMessage)
                EventBus.getDefault().post(message);
        } else if (message != null && message.getConversationType().getName().equals("group")) {//小灰条信息，0退群、1加群
            InformationNotificationMessage information = (InformationNotificationMessage) messageContent;
            EventBus.getDefault().post(information);
            LogUtil.e("小灰条=" + information.getMessage());
        }
        return true;
    }
}
