package com.bluebud.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.LogUtil;

public class PetInsurActivity extends BaseActivity implements OnClickListener {
    private WebView webView;
    private LinearLayout llSubmit;
    private String urlIndex = "file:///android_asset/petter/index.html";
    private String currentUrl = "file:///android_asset/petter/index.html";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.layout_pet_insur);

        initWebView();
    }

    private void initWebView() {
        setBaseTitleText(R.string.pet_insurance_agreement);
        setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);
        llSubmit = (LinearLayout) findViewById(R.id.ll_submit);
        ((Button) findViewById(R.id.btn_submit)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_cancel)).setOnClickListener(this);
        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.clearCache(true);
        webView.loadUrl("file:///android_asset/petter/index.html");
        webView.setWebViewClient(new HelloWebViewClient());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                if (currentUrl.equals(urlIndex)) {//返回到上个页面
                    finish();
                } else {//加载主页面
                    webView.loadUrl("file:///android_asset/petter/index.html");
                }

                break;
            case R.id.btn_submit:
                startActivity(new Intent(PetInsurActivity.this, PetInsurEditActivity.class));

                break;
            case R.id.btn_cancel:
                finish();
                break;

            default:
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class HelloWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            currentUrl = url;
            if (urlIndex.equals(url)) {
                llSubmit.setVisibility(View.VISIBLE);
            } else {
                llSubmit.setVisibility(View.GONE);
            }

            LogUtil.i("加载完成" + url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            currentUrl = url;
            LogUtil.i("加载开始" + url);
            llSubmit.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onDestroy() {
        webView.destroy();
        super.onDestroy();
    }


}
