package com.bluebud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.view.ProgressWebView;

import java.lang.ref.WeakReference;


public class ShoppingActivity extends BaseActivity implements OnClickListener, ProgressDialogUtil.OnProgressDialogClickListener {
    private ProgressWebView mWebView;
    //        private Handler mHandler = new Handler();
    private String url;
//    private int serverAndMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_shopping);
        WeakReference<ShoppingActivity> weakReference = new WeakReference<ShoppingActivity>(this);
        ProgressDialogUtil.show(weakReference.get());
        init();
//        if (Constants.isQrcode)
//            serverAndMap = UserSP.getInstance().getServerAndMap(weakReference.get());
    }

    private void init() {
        setBaseTitleText(R.string.shopping);
        setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);
        if (null != getIntent()) {
            url = getIntent().getStringExtra("url");
        }
        LogUtil.i("url=" + url);
        mWebView = (ProgressWebView) findViewById(R.id.webView);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); // 开启 DOM storage API 功能
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
                ProgressDialogUtil.dismiss();
            }

        });
        mWebView.loadUrl(url);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                if (Constants.isQrcode) {
                    Intent intent = new Intent(ShoppingActivity.this, HomePageActivity.class);
//                    if (serverAndMap == 0)
//                        intent = new Intent(ShoppingActivity.this, BaiduHomePageActivity.class);
//                    else
//                        intent = new Intent(ShoppingActivity.this, GoogleHomePageActivity.class);
                    startActivity(intent);
                    Constants.isQrcode = false;
                }
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
        if (Constants.isQrcode) {
            Intent intent = new Intent(ShoppingActivity.this, HomePageActivity.class);
//            if (serverAndMap == 0)
//                intent = new Intent(ShoppingActivity.this, BaiduHomePageActivity.class);
//            else
//                intent = new Intent(ShoppingActivity.this, GoogleHomePageActivity.class);
            startActivity(intent);
            Constants.isQrcode = false;
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        mWebView.removeJavascriptInterface("demo");
        mWebView.removeAllViews();
        mWebView.destroy();
        super.onDestroy();
        Constants.isQrcode = false;//是否是扫码过来的
        Constants.qrcodeUrl = null;
    }

    @Override
    public void onProgressDialogBack() {
        ProgressDialogUtil.dismiss();
    }

//    class JsObject extends Object {
//        @JavascriptInterface
//        public String toString() {
//            return "demo";
//        }
//    }
}
