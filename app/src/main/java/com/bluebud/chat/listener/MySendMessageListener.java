package com.bluebud.chat.listener;

import com.bluebud.app.AppApplication;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.utils.Constants;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.UserUtil;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * Created by Administrator on 2017/5/18 0018.
 */
public class MySendMessageListener implements RongIM.OnSendMessageListener {

    @Override
    public Message onSend(Message message) {
//		if(message!=null) {
//			String id = message.getTargetId();
//			Message.SentStatus sentStatus = message.getSentStatus();
//			LogUtil.e("id=="+id+"status=="+sentStatus+"content=="+ message.getContent() );
//		}
        return message;
    }

    /**
     * 消息发送成功后回调
     */
    @Override
    public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
        if (Constants.chatProductType.equals("29"))
            return false;

        if (message == null)
            return true;
        Message.SentStatus sentStatus = message.getSentStatus();
        LogUtil.e("id==" + message.getTargetId() + "status==" + sentStatus);
        if (sentStatus == null || sentStatus.getValue() != 30)//不等于30为消息不正常，不发送到手表
            return true;
        MessageContent messageContent = message.getContent();
        String device_sn = UserUtil.getCurrentTracker(AppApplication.getInstance()).device_sn;
        if (messageContent instanceof TextMessage) {// 文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            sendChatMessage("1", textMessage.getContent(), device_sn);// URLEncoder.encode(textMessage.getContent(),"utf-8")
        } else if (messageContent instanceof VoiceMessage) {
            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
            sendChatVoidce("0", voiceMessage.getUri().getPath(), device_sn);
        }
        return true;
    }

    /**
     * 发送文字
     *
     * @param msgType
     * @param msg
     * @param device_sn
     */
    private void sendChatMessage(String msgType, String msg, String device_sn) {
        ChatHttpParams.getInstallSigle(AppApplication.getInstance())
                .chatHttpRequest(5, null, null, null, device_sn, msg,
                        "0", msgType, null, new ChatCallbackResult() {
                            @Override
                            public void callBackResult(String result) {
                                LogUtil.e("result1=" + result);
                                if (result == null)
                                    return;
                                LogUtil.e("result2=" + result);
                            }

                            @Override
                            public void callBackFailResult(String result) {
                                LogUtil.e("callBackFailResult=" + result);
                            }
                        });
    }

    /**
     * 发送语音
     */
    private void sendChatVoidce(String msgType, String msg, String device_sn) {
        ChatHttpParams.getInstallSigle(AppApplication.getInstance())
                .chatHttpRequestFile(5, null, null, device_sn, msg, "0",
                        msgType, new ChatCallbackResult() {
                            @Override
                            public void callBackResult(String result) {
                                if (result == null)
                                    return;
                                LogUtil.e("result=" + result);
                            }

                            @Override
                            public void callBackFailResult(String result) {
                                LogUtil.e("callBackFailResult=" + result);
                            }
                        });
    }
}
