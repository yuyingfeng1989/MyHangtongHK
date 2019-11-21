package com.bluebud.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.bluebud.activity.AdvertisingPageActivity;
import com.bluebud.activity.BindListActivity;
import com.bluebud.activity.HomePageActivity;
import com.bluebud.activity.MainActivity;
import com.bluebud.app.App;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.info.Advertisement;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

/**
 * Created by user on 2018/4/3.
 * 广告加载工具类
 */

public class AdvertisingPage {
    private Activity mContext;
    private String resultData;
    private int deviceSize;//设备个数
//    private int mapIndex;//显示那种地图

    public AdvertisingPage(Context context, int size) {
        this.mContext = (Activity) context;
        this.deviceSize = size;

    }

    /**
     * 获取广告信息
     */
    public void getAdvertisingPageInfo(String type, final int function) {
        ChatHttpParams.getInstallSingle().chatHttpRequest(function, null, null, null, null, null, type, null, null, new ChatCallbackResult() {
            @Override
            public void callBackResult(String result) {
                LogUtil.e("广告====" + result);
                resultData = result;
                List<Advertisement> parseResult = (List<Advertisement>) ChatHttpParams.getParseResult(function, result);
                showAdvertisingPage(parseResult);
            }

            @Override
            public void callBackFailResult(String result) {//请求失败
                intentActivity();
            }
        });
    }

    /**
     * 是否加载广告
     */
    private void showAdvertisingPage(List<Advertisement> lists) {
//        mapIndex = UserSP.getInstance().getServerAndMap(App.getContext());
        if (lists == null) {
            intentActivity();
            return;
        }

        int size = lists.size();
        if (size < 1) {
            intentActivity();
            return;
        }

        String image_url = lists.get(size - 1).image_url;
        if (TextUtils.isEmpty(image_url) || image_url.indexOf("http") == -1) {//判断是否有广告和是否正常图片链接
            intentActivity();
            return;
        }
        boolean isAd = AppSP.getInstance().getAdvertisingIsCache(image_url);
        if (isAd) {//跳转广告页
            intentClass(AdvertisingPageActivity.class, true);
            return;
        }
        addAdvertisingPage(image_url);//循环缓存加载图片
        intentActivity();//跳转界面
    }

    /**
     * 判断跳转到那个界面
     */
    private void intentActivity() {
        if (deviceSize > 1) {
            intentClass(HomePageActivity.class, false);
//            if (mapIndex == 0)
//                intentClass(BaiduHomePageActivity.class, false);
//            else
//                intentClass(GoogleHomePageActivity.class, false);
        } else if (deviceSize == 1) {
            intentClass(MainActivity.class, false);
        } else {
            intentClass(BindListActivity.class, false);
        }
    }

    /**
     * 跳转
     */
    private void intentClass(Class<?> clss, boolean isAdvertising) {
        // FIXME: 2019/7/10 mContext空指针异常！！！！
        if (mContext == null) {
            return;
        }
        Intent intent = new Intent(mContext, clss);
        if (isAdvertising) {//广告界面
            intent.putExtra("adverts", resultData);//广告数据
//            intent.putExtra("mapIndex", mapIndex);//哪个地图
            intent.putExtra("size", deviceSize);//设备数
        } else if (deviceSize < 1) {//绑定设备界面
            intent.putExtra("formpage", Constants.REGISTRATION_COMPLETED);
        }
        mContext.startActivity(intent);
        mContext.finish();
    }

    /**
     * 加载缓存图片
     */
    private void addAdvertisingPage(final String url) {
        LogUtil.e("fileurl==" + url);
        Glide.with(App.getContext()).load(url).asBitmap().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (resource != null)
                    AppSP.getInstance().saveAdvertising(url, true);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
            }
        });
    }
}
