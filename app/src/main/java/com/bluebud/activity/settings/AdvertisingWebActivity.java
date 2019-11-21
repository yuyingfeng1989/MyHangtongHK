package com.bluebud.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bluebud.activity.BaseActivity;
import com.bluebud.activity.BindListActivity;
import com.bluebud.activity.HomePageActivity;
import com.bluebud.activity.MainActivity;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.view.ProgressWebView;

import java.lang.ref.WeakReference;

/**
 * Created by user on 2018/4/8.
 */

public class AdvertisingWebActivity extends BaseActivity implements ProgressDialogUtil.OnProgressDialogClickListener, View.OnClickListener {
    private ProgressWebView webView;
//    private int mapIndex;
    private int size;
    private String webUrl;
    private AdvertisingWebActivity mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertisingweb_activity);
        WeakReference<AdvertisingWebActivity> reference = new WeakReference(this);
        mContext = reference.get();
        Intent intent = getIntent();
//        mapIndex = intent.getIntExtra("mapIndex", 0);
        size = intent.getIntExtra("size", 0);
        webUrl = intent.getStringExtra("webUrl");
        ProgressDialogUtil.show(mContext);
        init();
    }

    /**
     * 初始化加载网页
     */
    private void init() {
        webView = (ProgressWebView) findViewById(R.id.webView);
        findViewById(R.id.back_image).setOnClickListener(this);
        LogUtil.e("webUrl==" + webUrl);
        WebSettings settings = webView.getSettings();//启用支持Javascript
        settings.setJavaScriptEnabled(true);//java和JavaScript交互
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                webView.setVisibility(View.VISIBLE);
                super.onPageFinished(view, url);
                ProgressDialogUtil.dismiss();
            }

        });
        webView.loadUrl(webUrl);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_image) {
            intentClass();//跳转界面
        }
    }

    //改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();   //返回上一页面
                return true;
            } else {
                intentClass();//跳转界面
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 跳转界面
     */
    private void intentClass() {
        Intent intent;
        if (size > 1) {//大于两个设备
            intent = new Intent(mContext, HomePageActivity.class);
        } else if (size < 1) {//没有设备
            intent = new Intent(mContext, BindListActivity.class);
            intent.putExtra("formpage", Constants.REGISTRATION_COMPLETED);
        } else {//只有一个设备
            intent = new Intent(mContext, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //清除记录
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearFormData();
        webView.destroy();
        mContext = null;
        webUrl = null;
    }

    @Override
    public void onProgressDialogBack() {
        ProgressDialogUtil.dismiss();
    }


}
