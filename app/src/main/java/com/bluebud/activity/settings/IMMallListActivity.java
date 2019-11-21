//package com.bluebud.activity.settings;
//
//import android.content.Intent;
//import android.net.http.SslError;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.RequiresApi;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.webkit.SslErrorHandler;
//import android.webkit.WebResourceRequest;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//
//import com.bluebud.activity.BaseActivity;
//import com.bluebud.activity.MallActivity;
//import com.bluebud.liteguardian_hk.R;
//import com.bluebud.utils.Constants;
//import com.bluebud.view.ProgressWebView;
//
//public class IMMallListActivity extends BaseActivity implements View.OnClickListener {
//    private ProgressWebView webView;
//    private String webUrl = "https://www.litemall.hk/mobile/index.php?m=chat&a=chatlist";
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_shopping);
//        initView();
//    }
//
//    /**
//     * 初始化控件
//     */
//    private void initView() {
//        findViewById(R.id.rl_mall).setVisibility(View.VISIBLE);
//        webView = (ProgressWebView) findViewById(R.id.webView);
//        findViewById(R.id.litemall_back).setVisibility(View.GONE);
//        findViewById(R.id.iv_back).setOnClickListener(this);
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setDomStorageEnabled(true);//设置适应Html5
//        webView.setVisibility(View.VISIBLE);
//        webView.setWebViewClient(new MyWebViewClient());
//        webView.loadUrl(webUrl);
////        goBack();
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.iv_back:
//                if (webView.canGoBack())
//                    webView.goBack();
//                else {
//                    startActivity(new Intent(IMMallListActivity.this, MallActivity.class));
//                    finish();
//                }
//                break;
//        }
//    }
//
//    private class MyWebViewClient extends WebViewClient {
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
//            Log.e("TAG", "onPageFinished=" + url);
//        }
//
//        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            Log.e("TAG", "URL=" + request.getUrl());
//            return super.shouldOverrideUrlLoading(view, request);
//        }
//        @Override
//        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
////                super.onReceivedSslError(view, handler, error);
//            handler.proceed();
//        }
//    }
//
////    /**
////     * 回退
////     */
////    private void goBack() {
////        webView.setOnKeyListener(new View.OnKeyListener() {
////            public boolean onKey(View v, int keyCode, KeyEvent event) {
////                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
////                    webView.goBack();
////                    return true;
////                } else {
////                    startActivity(new Intent(IMMallListActivity.this, MallActivity.class));
////                    finish();
////                    return false;
////                }
////            }
////        });
////    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            webView.goBack();
//            return true;
//        } else {
//            startActivity(new Intent(IMMallListActivity.this, MallActivity.class));
//            finish();
//            return false;
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (webView != null) {
//            webView.destroy();
//            webView.removeAllViews();
//            webView = null;
//        }
//    }
//}
