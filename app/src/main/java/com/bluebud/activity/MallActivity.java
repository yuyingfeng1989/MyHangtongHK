package com.bluebud.activity;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.AndroidBug5497Workaround;
import com.bluebud.utils.Constants;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.view.ProgressWebView;

import java.lang.ref.WeakReference;

import io.rong.eventbus.EventBus;

/**
 * Created by Administrator on 2019/1/3.
 */

public class MallActivity extends BaseActivity implements View.OnClickListener {

    private ProgressWebView webView;
    private boolean isBind;
    private ImageView litemall_back;
    private boolean isChat;
    private MallActivity mallActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
//        EventBus.getDefault().register(this);
        WeakReference<MallActivity> weakReference = new WeakReference<>(this);
        mallActivity = weakReference.get();
        AndroidBug5497Workaround.assistActivity(mallActivity);
        isBind = getIntent().getBooleanExtra("isBind", false);
        initView();

    }

    private void initView() {
        findViewById(R.id.rl_mall).setVisibility(View.VISIBLE);
        webView = (ProgressWebView) findViewById(R.id.webView);
        litemall_back = findViewById(R.id.litemall_back);
        findViewById(R.id.iv_back).setOnClickListener(this);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);//设置适应Html5

        litemall_back.setOnClickListener(this);
        if (!isBind)
            litemall_back.setVisibility(View.VISIBLE);
        webView.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(Constants.MALL_SHOP);
//        goBack();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(isChat) {
            webView.removeAllViews();
            webView.loadUrl(Constants.MALL_SHOP);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                if (webView.canGoBack())
                    webView.goBack();
                else {
                    finish();
                }
                break;
            case R.id.litemall_back:
                finish();
                break;
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e("TAG", "litemall==" + url);
            if (url.contains("chat&a=chatlist") || url.contains("m=chat&ru_id") || url.contains("m=chat&goods_id")) {
                litemall_back.setVisibility(View.GONE);
                isChat = true;
            }
            else litemall_back.setVisibility(View.VISIBLE);
            ProgressDialogUtil.dismiss();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
    }

//    private void goBack() {
//        webView.setOnKeyListener(new View.OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if(keyCode == KeyEvent.KEYCODE_BACK) {
//                    if (webView.canGoBack())
//                        webView.goBack();
//                    else {
//                        finish();
//                    }
//                }
//                return false;
//            }
//        });
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack())
                webView.goBack();
            else {
                finish();
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
        if (webView != null) {
            webView.destroy();
            webView.removeAllViews();
            webView = null;
        }
    }
}
