package com.bluebud.activity.settings;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bluebud.activity.BaseActivity;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.SystemUtil;

public class HelpActivity extends BaseActivity {
    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_help);

        int position = getIntent().getIntExtra("position", 1);
        String sUrl = "file:///android_asset/introduce-cn/1.html";
        switch (position) {
            case 1:
                if ("zh".equals(SystemUtil.getSystemLanguage())) {
                    if ("HK".equals(SystemUtil.getSystemCountry()) || "TW".equals(SystemUtil.getSystemCountry())) {
                        sUrl = "file:///android_asset/introduce-hk/1.html";
                    } else {
                        sUrl = "file:///android_asset/introduce-cn/1.html";
                    }
                } else {
                    sUrl = "file:///android_asset/introduce-en/1.html";
                }
                setBaseTitleText(R.string.account_login_issues1);
                setBaseTitleVisible(View.VISIBLE);
                break;
            case 2:
                if ("zh".equals(SystemUtil.getSystemLanguage())) {
                    if ("HK".equals(SystemUtil.getSystemCountry()) || "TW".equals(SystemUtil.getSystemCountry())) {
                        sUrl = "file:///android_asset/introduce-hk/2.html";
                    } else {
                        sUrl = "file:///android_asset/introduce-cn/2.html";
                    }
                } else {
                    sUrl = "file:///android_asset/introduce-en/2.html";
                }
                setBaseTitleText(R.string.add_delete_device);
                setBaseTitleVisible(View.VISIBLE);
                break;
            case 3:
                if ("zh".equals(SystemUtil.getSystemLanguage())) {
                    if ("HK".equals(SystemUtil.getSystemCountry()) || "TW".equals(SystemUtil.getSystemCountry())) {
                        sUrl = "file:///android_asset/introduce-hk/3.html";
                    } else {
                        sUrl = "file:///android_asset/introduce-cn/3.html";
                    }
                } else {
                    sUrl = "file:///android_asset/introduce-en/3.html";
                }
                setBaseTitleText(R.string.apn_set_issuses1);
                setBaseTitleVisible(View.VISIBLE);
                break;
            case 4:
                if ("zh".equals(SystemUtil.getSystemLanguage())) {
                    if ("HK".equals(SystemUtil.getSystemCountry()) || "TW".equals(SystemUtil.getSystemCountry())) {
                        sUrl = "file:///android_asset/introduce-hk/4.html";
                    } else {
                        sUrl = "file:///android_asset/introduce-cn/4.html";
                    }
                } else {
                    sUrl = "file:///android_asset/introduce-en/4.html";
                }
                setBaseTitleText(R.string.PT718_issuses1);
                setBaseTitleVisible(View.VISIBLE);
                break;
            case 5:
                if ("zh".equals(SystemUtil.getSystemLanguage())) {
                    if ("HK".equals(SystemUtil.getSystemCountry()) || "TW".equals(SystemUtil.getSystemCountry())) {
                        sUrl = "file:///android_asset/introduce-hk/5.html";
                    } else {
                        sUrl = "file:///android_asset/introduce-cn/5.html";
                    }
                } else {
                    sUrl = "file:///android_asset/introduce-en/5.html";
                }
                setBaseTitleText(R.string.PT719_issuses1);
                setBaseTitleVisible(View.VISIBLE);
                break;
            case 6:
                if ("zh".equals(SystemUtil.getSystemLanguage())) {
                    if ("HK".equals(SystemUtil.getSystemCountry()) || "TW".equals(SystemUtil.getSystemCountry())) {
                        sUrl = "file:///android_asset/introduce-hk/6.html";
                    } else {
                        sUrl = "file:///android_asset/introduce-cn/6.html";
                    }
                } else {
                    sUrl = "file:///android_asset/introduce-en/6.html";
                }
                setBaseTitleText(R.string.device_authorize1);
                setBaseTitleVisible(View.VISIBLE);
                break;
            case 7:
                if ("zh".equals(SystemUtil.getSystemLanguage())) {
                    if ("HK".equals(SystemUtil.getSystemCountry()) || "TW".equals(SystemUtil.getSystemCountry())) {
                        sUrl = "file:///android_asset/introduce-hk/7.html";
                    } else {
                        sUrl = "file:///android_asset/introduce-cn/7.html";
                    }
                } else {
                    sUrl = "file:///android_asset/introduce-en/7.html";
                }
                setBaseTitleText(R.string.PT720_issuses1);
                setBaseTitleVisible(View.VISIBLE);
                break;
            case 8:
                if ("zh".equals(SystemUtil.getSystemLanguage())) {
                    if ("HK".equals(SystemUtil.getSystemCountry()) || "TW".equals(SystemUtil.getSystemCountry())) {
                        sUrl = "file:///android_asset/introduce-hk/8.html";
                    } else {
                        sUrl = "file:///android_asset/introduce-cn/8.html";
                    }
                } else {
                    sUrl = "file:///android_asset/introduce-en/8.html";
                }
                setBaseTitleText(R.string.HT770_issuses);
                setBaseTitleVisible(View.VISIBLE);
                break;
            case 9:
                if ("zh".equals(SystemUtil.getSystemLanguage())) {
                    if ("HK".equals(SystemUtil.getSystemCountry()) || "TW".equals(SystemUtil.getSystemCountry())) {
                        sUrl = "file:///android_asset/introduce-hk/9.html";
                    } else {
                        sUrl = "file:///android_asset/introduce-cn/9.html";
                    }
                } else {
                    sUrl = "file:///android_asset/introduce-en/9.html";
                }
                setBaseTitleText(R.string.HT790s_issuses);
                setBaseTitleVisible(View.VISIBLE);
                break;
            case 10:
                if ("zh".equals(SystemUtil.getSystemLanguage())) {
                    if ("HK".equals(SystemUtil.getSystemCountry()) || "TW".equals(SystemUtil.getSystemCountry())) {
                        sUrl = "file:///android_asset/introduce-hk/10.html";
                    } else {
                        sUrl = "file:///android_asset/introduce-cn/10.html";
                    }
                } else {
                    sUrl = "file:///android_asset/introduce-en/10.html";
                }
                setBaseTitleText(R.string.PT880_issuses);
                setBaseTitleVisible(View.VISIBLE);
                break;
            case 11:
                if ("zh".equals(SystemUtil.getSystemLanguage())) {
                    if ("HK".equals(SystemUtil.getSystemCountry()) || "TW".equals(SystemUtil.getSystemCountry())) {
                        sUrl = "file:///android_asset/introduce-hk/11.html";
                    } else {
                        sUrl = "file:///android_asset/introduce-cn/11.html";
                    }
                } else {
                    sUrl = "file:///android_asset/introduce-en/11.html";
                }
                setBaseTitleText(R.string.PT990_issuses);
                setBaseTitleVisible(View.VISIBLE);
                break;

            default:
                break;
        }

        webView = (WebView) this.findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.clearCache(true);

        webView.loadUrl(sUrl);
        webView.setWebViewClient(new HelloWebViewClient());

        getBaseTitleLeftBack().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

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
        // 在WebView中而不是默认浏览器中显示页面
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        super.onDestroy();
    }
}