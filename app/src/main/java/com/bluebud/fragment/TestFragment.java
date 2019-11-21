package com.bluebud.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.view.ProgressWebView;

/**
 * Created by Administrator on 2018/11/16.
 */

public class TestFragment extends Fragment {

    private ProgressWebView webView;

    //    private String url = "https://www.baidu.com/";
//    private String url;

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);

//        url = UserSP.getInstance().getMallShop(getActivity().getApplicationContext());
//        Log.e("TAG", "URL==" + url);
//    }

    private View contentView;
    private ViewGroup containers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == contentView) {
            this.containers = container;
            contentView = inflater.inflate(R.layout.activity_shopping, container, false);
            webView = (ProgressWebView) contentView.findViewById(R.id.webView);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setDomStorageEnabled(true);//设置适应Html5
            webView.setVisibility(View.VISIBLE);
            webView.setWebViewClient(new MyWebViewClient());
            if (Constants.MALL_SHOP != null) webView.loadUrl(Constants.MALL_SHOP);
        }

        ViewGroup parent = (ViewGroup) contentView.getParent();
        if (parent != null) {
            parent.removeView(contentView);
        }
        goBack();
        return contentView;
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return super.shouldOverrideUrlLoading(view, url);
            Log.e("TAG", "URL==" + url);
            Log.e("TAG", "shouldOverrideUrlLoading");
            view.loadUrl(url);
            return true;////返回true代表在当前webview中打开，返回false表示打开浏览器
        }

//        @Override
//        public void onFormResubmission(WebView view, Message dontResend, Message resend) {
//            super.onFormResubmission(view, dontResend, resend);
//        }
//
//        @Override
//        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
//            super.onReceivedHttpAuthRequest(view, handler, host, realm);
//            Log.e("TAG","onReceivedHttpAuthRequest");
//        }
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
////            super.onPageStarted(view, url, favicon);
//            Log.e("TAG","onPageStarted");
//        }
//
//        @Override
//        public void onPageFinished(WebView view, String url) {
////            super.onPageFinished(view, url);
//            Log.e("TAG","onPageFinished");
//        }
//
//        @Override
//        public void onPageCommitVisible(WebView view, String url) {
////            super.onPageCommitVisible(view, url);
//            Log.e("TAG","onPageCommitVisible");
//        }
//
//        @Override
//        public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
//            super.onReceivedLoginRequest(view, realm, account, args);
//            Log.e("TAG","onReceivedLoginRequest");
//        }
//
//        @Override
//        public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
//            super.onReceivedClientCertRequest(view, request);
//            Log.e("TAG","onReceivedClientCertRequest");
//        }
//
//        @Override
//        public void onLoadResource(WebView view, String url) {
////            super.onLoadResource(view, url);
//            Log.e("TAG","onLoadResource");
//        }
//
//        @Override
//        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//            super.onReceivedError(view, request, error);
//            Log.e("TAG","onReceivedError");
//        }
//
//        @Override
//        public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
//            super.onUnhandledKeyEvent(view, event);
//            Log.e("TAG","onUnhandledKeyEvent");
//        }
//
//        @Override
//        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
//            super.onReceivedHttpError(view, request, errorResponse);
//            Log.e("TAG","onReceivedHttpError");
//        }
//
//        @Override
//        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            super.onReceivedError(view, errorCode, description, failingUrl);
//            Log.e("TAG","onReceivedError++");
//        }

        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            handler.proceed();  // 接受所有网站的证书
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("是否打开网页");
            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed(); // 接受所有网站的证书
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();//拒绝所有网站的证书
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
            Log.e("TAG", "onReceivedSslError");
        }
    }

    private void goBack() {
        webView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (containers != null) {
            containers.removeView(webView);
            webView.destroy();
        }
    }
}
