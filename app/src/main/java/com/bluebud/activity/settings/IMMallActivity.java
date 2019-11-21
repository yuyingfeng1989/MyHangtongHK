package com.bluebud.activity.settings;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bluebud.activity.BaseActivity;
import com.bluebud.activity.MallActivity;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.AndroidBug5497Workaround;
import com.bluebud.utils.Constants;
import com.bluebud.view.ProgressWebView;

import java.lang.ref.WeakReference;

import me.leolin.shortcutbadger.ShortcutBadger;


public class IMMallActivity extends BaseActivity implements View.OnClickListener {

    private ProgressWebView webView;
    private String store_id;
    private String loadurl;
    private String imList = "http://www.litemall.hk/mobile/index.php?m=chat&a=chatlist";
    private int indexWeb;
    private IMMallActivity imMallActivity;

    /**
     * 拼接连接
     */
    private void imChatPararm() {
        loadurl = "http://www.litemall.hk/mobile/index.php?m=chat&ru_id=" + store_id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        WeakReference<IMMallActivity> weakReference = new WeakReference<>(this);
        imMallActivity = weakReference.get();
        AndroidBug5497Workaround.assistActivity(imMallActivity);
        Intent intent = getIntent();
        store_id = intent.getStringExtra("store_id");
        imChatPararm();
        initView();
        Constants.imCountMessage = 0;
        ShortcutBadger.removeCount(this.getApplicationContext());
    }

    /**
     * 初始化控件
     */
    private void initView() {
        findViewById(R.id.rl_mall).setVisibility(View.VISIBLE);
        webView = (ProgressWebView) findViewById(R.id.webView);
        findViewById(R.id.litemall_back).setVisibility(View.GONE);
        findViewById(R.id.iv_back).setOnClickListener(this);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);//设置适应Html5
        webView.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new MyWebViewClient());
        if (loadurl != null)
            webView.loadUrl(loadurl);
//        goBack();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                if (indexWeb == 1) {
                    startActivity(new Intent(IMMallActivity.this, MallActivity.class));
                    finish();
                } else if (indexWeb == 2) {
                    webView.loadUrl(imList);
                } else {
                    webView.goBack();
                }
                break;
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e("TAG", "finished=" + url);
            if (url.contains("a=chatlist")) {
                indexWeb = 1;
            } else if (url.contains("m=chat&ru_id=")) {
                indexWeb = 2;
            } else indexWeb = 0;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

//        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            Log.e("TAG", "URL=" + request.getUrl());
//            return super.shouldOverrideUrlLoading(view, request);
//        }
    }

    /**
     * 回退
     */
//    private void goBack() {
//        webView.setOnKeyListener(new View.OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    if (indexWeb == 1) {
//                        startActivity(new Intent(IMMallActivity.this, MallActivity.class));
//                        finish();
//                    }else if(indexWeb == 2){
//                        webView.loadUrl(imList);
//                    }else {
//                        webView.goBack();
//                    }
//                }
//                return false;
//            }
//        });
//    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (indexWeb == 1) {
                startActivity(new Intent(IMMallActivity.this, MallActivity.class));
                finish();
            } else if (indexWeb == 2) {
                webView.loadUrl(imList);
            } else {
                webView.goBack();
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
            webView.removeAllViews();
            webView = null;
        }
    }
}
