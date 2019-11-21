package com.bluebud.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.LogUtil;
import com.bluebud.view.ProgressWebView;


public class ShoppingActivity1 extends BaseActivity implements OnClickListener {
    private ProgressWebView mWebView;
    //    private Handler mHandler = new Handler();
    private String type = Constants.URL_SHOPPING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_shopping);
        init();
    }

    private void init() {
        setBaseTitleText(R.string.shopping);
        setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);
        if (null != getIntent()) {
            type = getIntent().getStringExtra("type");
        }
        String url = Constants.URL_SHOPPING1;
        LogUtil.i("type:" + type);
        if (null != type) {
            if (type.equals(Constants.PET_EQUIPMENT)) {// 宠物
                url = Constants.URL_SHOPPINGPET;
            } else if (type.equals(Constants.WATCH_EQUIPMENT)) {// 手表
                url = Constants.URL_SHOPPINGWATCH;
            } else if (type.equals(Constants.CAR_EQUIPMENT)) {// 汽车
                url = Constants.URL_SHOPPINGCAR;
            } else if (type.equals(Constants.MOTO_EQUIPMENT)) {// 摩托车
                url = Constants.URL_SHOPPINGMOTORCYCLE;
            } else if (type.equals(Constants.PERSON_EQUIPMENT)) {
                url = Constants.URL_SHOPPING1;
            }
        }

        mWebView = (ProgressWebView) findViewById(R.id.webView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        mWebView.addJavascriptInterface(new JsObject() {
//            public void clickOnAndroid() {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mWebView.loadUrl("javascript:wave()");
//                    }
//                });
//            }
//        }, "demo");
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.setVisibility(View.VISIBLE);
                super.onPageFinished(view, url);
            }

        });
        mWebView.loadUrl(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        mWebView.removeJavascriptInterface("demo");
        mWebView.removeAllViews();
        mWebView.destroy();
        super.onDestroy();
    }

//    class JsObject extends Object{
//        @JavascriptInterface
//        public String toString() { return "demo"; }
//    }
}
