package com.bluebud.utils;

import android.content.Context;

import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.info.HomePageInfo;

import java.util.List;

import io.rong.eventbus.EventBus;

/**
 * Created by user on 2018/3/27.
 */

public class RequestHomePageUtil implements ProgressDialogUtil.OnProgressDialogClickListener {


    public static void requestHomePageData(final Context mContext) {
        ChatHttpParams.getInstallSigle(mContext).chatHttpRequest(19, UserSP.getInstance().getUserName(mContext), null, null, null, null, null, null, null, new ChatCallbackResult() {
            @Override
            public void callBackStart() {
                super.callBackStart();
                ProgressDialogUtil.show(mContext);
            }

            @Override
            public void callBackResult(String result) {
                List<HomePageInfo> listInfo = (List<HomePageInfo>) ChatHttpParams.getInstallSigle(mContext).getParseResult(19, result);
                EventBus.getDefault().post(listInfo);
                ProgressDialogUtil.dismiss();
            }

            @Override
            public void callBackFailResult(String result) {
                ProgressDialogUtil.dismiss();
            }
        });
    }


    @Override
    public void onProgressDialogBack() {
        ProgressDialogUtil.dismiss();
    }
}
