package com.bluebud.listener;

import android.os.Handler;

import com.bluebud.activity.settings.WifiSettingActivity;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ParseValueUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SendandRece implements Runnable {
    private WifiSettingActivity listener;
    private String address;
    private int aport;
    private byte[] data;
    public static boolean isDestroy = true;
    private Socket s = null;
    private OutputStream out = null;
    private InputStream in = null;
    private boolean iscommit;
    private String result = "";
    private Handler handler;

    public SendandRece(WifiSettingActivity listener, byte[] data,
                       String address, int aport, boolean iscommit) {
        this.listener = listener;
        this.address = address;
        this.aport = aport;
        this.data = data;
        this.iscommit = iscommit;
        if (handler == null)
            handler = new Handler();
        totalTimer();
    }

    @Override
    public void run() {
        isDestroy = false;
        try {
            s = new Socket(address, aport);
            if (s.isConnected()) {
                s.setSoTimeout(5000);
                out = s.getOutputStream();
                if (out != null) {// 写数据
                    out.write(data);
                    out.flush();
                }

                in = s.getInputStream();// 读数据
//				s.shutdownOutput();
                if (in == null)
                    return;

                LogUtil.e("isInterrupted()==" + isDestroy);
                while (!isDestroy) {
                    byte[] buffer = new byte[1024];
                    int size = in.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                }
            }
        } catch (Exception e) {
            if (iscommit)
                listener.onRequestFailure(R.string.pay_network_error, 1);
            else
                listener.onRequestFailure(R.string.pay_network_error, 0);
            e.printStackTrace();
        } finally {
            closeData();
        }

    }

    /**
     * 接收Socket返回数据字节，解析为字符串
     */
    private void onDataReceived(byte[] buffer, int size) {
        try {
            result += new String(buffer, 0, size, "utf-8");
            LogUtil.e("返回d读取数据=" + result);
            int index = result.indexOf("0D0A");
            if (index == -1)
                return;
            closeData();
            String crc = result.substring(0, index + 4);
            if (iscommit)
                ParseValueUtil.interceptParseSetting(listener, crc);
            else
                ParseValueUtil.interceptParseQuery(listener, crc);

        } catch (Exception e) {
            closeData();
            e.printStackTrace();

        }
    }


    private void closeData() {
        try {
            isDestroy = true;
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (s != null && s.isConnected())
                s.close();
            if (handler != null) {
                handler.removeCallbacks(run);
                handler.removeCallbacksAndMessages(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 计时十秒超时
     */
    private void totalTimer() {
        handler.postDelayed(run, 5000);
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            LogUtil.e("执行了5秒超时");
            closeData();
            if (iscommit)
                listener.onRequestFailure(R.string.pay_network_error, 1);
            else
                listener.onRequestFailure(R.string.pay_network_error, 0);
        }
    };
}
