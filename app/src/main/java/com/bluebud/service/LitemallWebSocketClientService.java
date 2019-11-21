//package com.bluebud.service;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Handler;
//import android.os.IBinder;
//import android.support.annotation.RequiresApi;
//import android.util.Log;
//
//import com.bluebud.data.sharedprefs.UserSP;
//import com.bluebud.info.Alarm;
//import com.bluebud.info.LOGINIM;
//import com.bluebud.info.LitemallInfo;
//import com.bluebud.utils.Constants;
//
//import org.java_websocket.enums.ReadyState;
//import org.java_websocket.handshake.ServerHandshake;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.net.URI;
//
//import io.rong.eventbus.EventBus;
//
//public class LitemallWebSocketClientService extends Service {
//    public LitemallWebSocketClient client;
//    //    private final static int GRAY_SERVICE_ID = 1001;
////    private String photoUrl;//头像
////    private SharedPreferences shared;
//    private String uid;//用户客服id
//    private static final long HEART_BEAT_RATE = 30 * 1000;//每隔10秒进行一次对长连接的心跳检测
//    private Handler mHandler = new Handler();
//    private int reConnect = 0;//最多联系能够初始化IM三次
//    public boolean isCreateService;//服务是否创建
//    private ServiceIMUtil serviceIMUtil;
//    private LOGINIM message;//登录数据对象
//    private boolean isTimeChange;//网络每隔一分钟检测IM服务一次
//    private int initLogin = 0;//每隔两分钟初始化一次IM登录是否畅通
//    private LitemallInfo liteMall;//litemall核心信息
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
////        shared = getSharedPreferences("userInfo", 0);//文件缓存类
////        serviceIMUtil = new ServiceIMUtil(this);
//        isCreateService = true;
//    }
//
//    /**
//     * 连接为标socket
//     * 设置service为前台服务，提高优先级
//     */
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null)
//            isTimeChange = intent.getBooleanExtra("START_IM", false);
//        Log.e("TAG", "onStartCommand=" + isTimeChange);
//        startIM();
//        if (!isCreateService)//服务没有被回收
//            return START_STICKY;
//        isCreateService = false;
//        serviceIMUtil.startForegroundNF();
//        return START_STICKY;
//    }
//
//    /**
//     * 是否重启管道连接
//     */
//    private void startIM() {
//        if (isTimeChange) {
//            initLogin++;
//            reConnect = 0;
//            if (client != null && initLogin % 2 == 0&&client.isOpen()) {//每间隔2分钟初始化登录一次
//                reLoginIM();
//                initLogin = 0;
//            } else if (client == null||client.isClosed()) {
//                connectWebSocket();
//            }
//        } else {
//            if (client != null&&client.isOpen()) {
//                reLoginIM();
//            } else {
//                connectWebSocket();
//            }
//        }
//    }
//
//    /**
//     * 初始化websocket连接
//     */
//    private void initSocketClient() {
//        mHandler.removeCallbacks(heartBeatRunnable);
//        URI uri = URI.create(Constants.WEBSOCKET_IM);
//        client = new LitemallWebSocketClient(uri) {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onMessage(String message) {
//                Log.e("TAG", "收到的消息：" + message);
//                try {
//                    JSONObject jb = new JSONObject(message);
//                    if (!jb.isNull("type")) //心跳包检测
//                        return;
//                    if (!jb.isNull("msg")) {//登录成功检测
//                        String msg = jb.optString("msg");
//                        String message_type = jb.optString("message_type");
//                        if (msg.equals("yes") && message_type.equals("init")) {//登录IM成功
//                            mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测
//                        }
//                        return;
//                    }
//                    if (jb.isNull("message_type"))
//                        return;
//                    messageIM(jb);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onOpen(ServerHandshake handshakedata) {
//                super.onOpen(handshakedata);
//                Log.e("TAG", "websocket连接成功");
//                reLoginIM();
//            }
//
//            @Override
//            public void onClose(int code, String reason, boolean remote) {
//                super.onClose(code, reason, remote);
//                Log.e("TAG", "onClose");
//                if(initLogin>4)
//                    return;
//                initLogin++;
//                client = null;
//                connectWebSocket();
//            }
//        };
//        connect();
//    }
//
//    /**
//     * 获取消息处理
//     */
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void messageIM(JSONObject jb) {
//        String message_type = jb.optString("message_type");
//        if (message_type.equals("leave") || message_type.equals("others_login")) {
//            String comeId = jb.optString("uid");
//            if (uid.equals(comeId) && reConnect < 4) {
//                if (client != null && client.isOpen()) reLoginIM();
//                else connectWebSocket();
//                reConnect++;
//            }
//            return;
//        }
//        if ((message_type.equals("come_msg") || message_type.equals("come_wait")) && !Constants.ISSERVICE_IM) {
//            String backMessage = jb.optString("message");
//            String msg = backMessage.replaceAll("&nbsp;", "");
//            Alarm alarm = new Alarm();
//            alarm.type = 1;//表示litemall消息
//            EventBus.getDefault().post(alarm);
//            String from_id = jb.optString("from_id");
//            String goods_id = jb.optString("goods_id");
//            String store_id = jb.optString("store_id");
//            String name = jb.optString("name");
////            serviceIMUtil.showNotification(msg, from_id, goods_id, store_id, message_type, name);
//        }
//    }
//
//    /**
//     * websocket心跳检测
//     */
//    private Runnable heartBeatRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (client.getReadyState().equals(ReadyState.OPEN)) {//websocket管道联通才能发
//                client.send("{\"type\":\"pong\"}");//发送一个心跳
//            }
//            mHandler.postDelayed(this, HEART_BEAT_RATE); //每隔一定的时间，对长连接进行一次心跳检测
//        }
//    };
//
//    /**
//     * 重新登录IM
//     */
//    private void reLoginIM() {
//        if (mHandler != null)
//            mHandler.removeCallbacks(heartBeatRunnable);
//        if (message == null)
//            message = new LOGINIM();
////        uid = shared.getString("kefu_id", "0");//新添加客服id
//        uid = liteMall.dscMallUserId;//新添加客服id
//        message.avatar = "/mobile/public/assets/chat/images/avatar.png";
//        message.name = "LiteGuardian";
////        message.store_id = "4511";
//        message.uid = uid;
//        String loginMessage = message.toJson(message).toString();
//        Log.e("TAG", "login=" + loginMessage);
//        if (client.isOpen())
//            client.send(loginMessage);
//    }
//
//    /**
//     * 初始花连接或者重连
//     */
//    private void connectWebSocket() {
//        if (client == null || client.isClosing()) {//如果client已为空，重新初始化连接
//            liteMall = UserSP.getInstance().getLiteMall(this);
//            initSocketClient();
//        }
//    }
//
//    /**
//     * 连接websocket
//     * connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
//     */
//    private void connect() {
//        try {
//            client.connectBlocking();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 断开连接
//     */
//    private void closeConnect() {
//        if (mHandler != null && heartBeatRunnable != null)
//            mHandler.removeCallbacks(heartBeatRunnable);
//        try {
//            if (null != client) client.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
////    /**
////     * 灰色保活
////     */
////    public static class GrayInnerService extends Service {
////        @Override
////        public void onCreate() {
////            super.onCreate();
////        }
////
////        @Override
////        public int onStartCommand(Intent intent, int flags, int startId) {
////            startForeground(GRAY_SERVICE_ID, new Notification());
////            stopForeground(true);
////            stopSelf();
////            return super.onStartCommand(intent, flags, startId);
////        }
////
////        @Override
////        public IBinder onBind(Intent intent) {
////            return null;
////        }
////    }
//
//
//    @Override
//    public void onDestroy() {
//        mHandler.removeCallbacks(heartBeatRunnable);
//        closeConnect();
//        super.onDestroy();
//    }
//}
