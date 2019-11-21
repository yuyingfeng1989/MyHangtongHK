package com.bluebud.activity.settings;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.view.ProgressWebView;

/**
 * Created by Administrator on 2019/6/21.
 */

public class WebActivity extends Activity implements View.OnClickListener {
    private final String WEB_URL = "http://www.castelbeidou.com";
    private ProgressWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        findViewById(R.id.rl_mall).setVisibility(View.VISIBLE);
        TextView text_mall = findViewById(R.id.text_mall);
        findViewById(R.id.iv_back).setOnClickListener(this);
        webView = findViewById(R.id.webView);
        webView.setVisibility(View.VISIBLE);
        text_mall.setText("Castel Beidou Hong Kong Limited");
        initWebView();
    }

    /**
     * 初始化进度条
     */
    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(WEB_URL);
//        goBack();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }

    }

    /**
     * 网页加载事件监听
     */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("TAG", "URL=" + url);
            view.loadUrl(url);
            return true;////返回true代表在当前webview中打开，返回false表示打开浏览器
        }
    }

//    /**
//     *回退事件处理
//     */
//    private void goBack() {
//        webView.setOnKeyListener(new View.OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
//                    webView.goBack();
//                    return true;
//                } else {
//                    finish();
//                }
//                return false;
//            }
//        });
//    }


    @Override
    protected void onDestroy() {
        webView.removeAllViews();
        super.onDestroy();
        webView.destroy();
        webView = null;
    }
}
