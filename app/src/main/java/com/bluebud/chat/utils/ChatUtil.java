package com.bluebud.chat.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bluebud.chat.listener.MyConversationClickListener;
import com.bluebud.chat.listener.MyReceiveMessageListener;
import com.bluebud.chat.listener.MySendMessageListener;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation.ConversationType;

@SuppressWarnings("WrongConstant")
public class ChatUtil {

    /**
     * 状态判断
     */
    public static String userPhoto;//用户信息头像
    public static String userNickname;//用户信息昵称
    public static String token;//融云连接token
    public static boolean isLoginOut;//表示是否是自己退出微聊群

//    public static String initChatImage = "http://static.yingyonghui.com/screenshots/1657/1657011_4.jpg";

    /**
     * 获得当前进程的名字
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid)
                return appProcess.processName;
        }
        return null;
    }

    /**
     * 监听初始化
     */
    public static void initerListener() {
        RongIM.setOnReceiveMessageListener(new MyReceiveMessageListener());// 消息接收监听
        RongIM.getInstance().setSendMessageListener(new MySendMessageListener());// 消息发送监听
        RongIM.getInstance().setConversationClickListener(new MyConversationClickListener());
    }

    /**
     * 建立与融云服务器的连接
     */
    public static void connect(ApplicationInfo applicationInfo, Context context) {
        if (ChatUtil.token == null || TextUtils.isEmpty(ChatUtil.token))
            return;
        if (applicationInfo.packageName.equals(getCurProcessName(context))) { // IMKit SDK调用第二步,建立与服务器的连接
            RongIM.connect(ChatUtil.token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                    LogUtil.e("连接失败");
                }

                @Override
                public void onSuccess(String userid) {
                    LogUtil.e("连接成功=" + userid);
                    RongIM.getInstance().enableUnreadMessageIcon(true); // 设置会话界面历史消息是否展示
//                    RongIM.getInstance().setMaxVoiceDurationg(15);// 设置语音最大时长
                    RongIM.getInstance().enableNewComingMessageIcon(true);// 显示新消息提醒
                    RongIM.getInstance().enableUnreadMessageIcon(true);// 显示未读消息数目
                }

                @Override
                public void onError(ErrorCode errorCode) {  //失败
                    LogUtil.e("--onError" + errorCode);
                }
            });
        }
    }


    /**
     * 清除聊天信息
     */
    public static void clearChatMessage(String trackerNo) {
        RongIM.getInstance().clearMessages(ConversationType.GROUP, trackerNo, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean arg0) {
                LogUtil.e("清除消息成功==" + arg0);
            }

            @Override
            public void onError(ErrorCode arg0) {
                LogUtil.e("清除消息失败==" + arg0);
            }
        });
    }

    /**
     * 清除草稿
     */
    public static void clearMessageDrag(String device_sn) {
        RongIM.getInstance().clearTextMessageDraft(ConversationType.GROUP, device_sn, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean arg0) {
            }

            @Override
            public void onError(ErrorCode arg0) {
            }
        });
    }

    /**
     * 显示对话框
     *
     * @param context
     * @param msg
     * @param isShowCancel
     * @param callback
     */
    public void chatShowDialog(Context context, int msg, boolean isShowCancel,
                               final ChatCallbackResult callback) {//是否是application还是activity

        final AlertDialog mDialog = new AlertDialog.Builder(context).create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        Window window = mDialog.getWindow();
        int width;
        int height;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        mDialog.show();
        if (context instanceof Activity) {//activity窗体
            width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
            height = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
        } else {//系统窗体
            WindowManager a = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            width = a.getDefaultDisplay().getWidth();
            height = a.getDefaultDisplay().getHeight();
        }

        if (width < height) {
            width = width * 8 / 10;
        } else {
            width = height * 6 / 10;
        }
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = width;
        window.setAttributes(params);
        window.setContentView(R.layout.layout_dialog);

        TextView tvContent = (TextView) window
                .findViewById(R.id.tv_dialog_content);
        Button btnOk = (Button) window.findViewById(R.id.btn_dialog_ok);
        Button btnCancel = (Button) window.findViewById(R.id.btn_dialog_cancel);
        View vLine = window.findViewById(R.id.in_line);
        tvContent.setText(msg);
        btnOk.setText(R.string.confirm);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.callOkDilaog(mDialog);
            }
        });

        if (isShowCancel) {//显示取消按钮
            btnCancel.setText(R.string.cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.callCanceDilaog(mDialog);
                }
            });
        } else {
            btnCancel.setVisibility(View.GONE);
            vLine.setVisibility(View.GONE);
        }
    }

    /**
     * 将分钟转为小时,保留两位小数
     */
    public static String getHour(int time) {
//        float H = time / 60;
//        float m = time % 60f / 60f;
//        float t = H + m;
//        String format = new DecimalFormat("#.00").format(t);
//        Log.e("TAG","format=="+format);
        int h = time / 60;
        int m = time % 60;
        String H = h < 10 ? "0" + h : String.valueOf(h);
        String M = m < 10 ? "0" + m : String.valueOf(m);
        StringBuilder HM = new StringBuilder(H);
        HM.append(":").append(M);
        return HM.toString();
    }

    /**
     * 获取现在时间
     * yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getCurrYear() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        String dateYear = formatter.format(currentTime);
        return dateYear;
    }

    /**
     * 计算最近12个月，年月
     *
     * @return
     */
    public static List<String> getLast12Months() {
        String time = getCurrYear();
        if (time.length() == 7) {//处理月份输入条件
            time = time + "-01 00:00:00";
        } else if (time.length() == 110) {
            time = time.substring(0, 7) + "-01 00:00:00";
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            return null;
        }
        List<String> result = new ArrayList<String>();
        Calendar cal = Calendar.getInstance();
        //设置输入条件时间
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1); //要先+1,才能把本月的算进去
        for (int i = 0; i < 12; i++) {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1); //逐次往前推1个月
            String ym = cal.get(Calendar.YEAR) + "." + addZeroForNum(String.valueOf(cal.get(Calendar.MONTH) + 1), 2);
            result.add(ym);
        }

        return result;
    }

    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);// 左补0
                str = sb.toString();
                strLen = str.length();
            }
        }
        return str;
    }


    /**
     * 判断手机格式是否正确
     * @return 返回true说明字符串匹配成功
     */
    // Pattern类的作用在于编译正则表达式后创建一个匹配模式. Matcher类使用Pattern实例提供的模式信息对正则表达式进行匹配
//    public static boolean isPhone(String str) {
//        // 将给定的正则表达式编译并赋予给Pattern类
//        Pattern pattern = Pattern.compile("1[0-9]{10}");
//        // 对指定输入的字符串创建一个Matcher对象
//        Matcher matcher = pattern.matcher(str);
//        // 尝试对整个目标字符展开匹配检测,也就是只有整个目标字符串完全匹配时才返回真值.
//        if (matcher.matches()) {
//            return true;
//        } else {
//            return false;
//        }
//    }


    /**
     * 输入最大字符数
     */
    public void editInputLimit(final Context context, final EditText myEditText, final int max) {
        final Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        myEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                int mTextMaxlenght = 0;
                Editable editable = myEditText.getText();
                String str = editable.toString().trim();
                int selEndIndex = Selection.getSelectionEnd(editable); //得到最初字段的长度大小，用于光标位置的判断
                // 取出每个字符进行判断，如果是字母数字和标点符号则为一个字符加1，如果是汉字则为两个字符
                for (int i = 0; i < str.length(); i++) {
                    char charAt = str.charAt(i);
                    //32-122包含了空格，大小写字母，数字和一些常用的符号，如果在这个范围内则算一个字符，
                    if (p.matcher(String.valueOf(charAt)).matches())//正则表达式判断是否是中文字
                        mTextMaxlenght += 2;
                    else
                        mTextMaxlenght++;
                    if (mTextMaxlenght > max) {  // 当最大字符大于40时，进行字段的截取，并进行提示字段的大小
                        String newStr = str.substring(0, i); // 截取最大的字段
                        myEditText.setText(newStr);
                        editable = myEditText.getText(); // 得到新字段的长度值
                        int newLen = editable.length();
                        if (selEndIndex > newLen)
                            selEndIndex = editable.length();
                        Selection.setSelection(editable, selEndIndex); // 设置新光标所在的位置
//                        Toast.makeText(context, "最大长度不超过"+max+"个字符！", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
