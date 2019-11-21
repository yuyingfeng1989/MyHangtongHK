package com.bluebud.http;

import android.content.Context;

import com.bluebud.utils.LogUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.impl.cookie.BasicClientCookie;

public class HttpClientFileRest {
    //	private static AsyncHttpClient client = new AsyncHttpClient(true,80,7000);
    private static AsyncHttpClient client = new AsyncHttpClient();

    //    private static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    public static AsyncHttpClient getClient() {
        return client;
    }

    public static void setHttpClient(Context context) {
        client.setTimeout(60000); // 设置链接超时，如果不设置，默认为10s
        client.addHeader("Accept-Language", Utils.getLanguage2Url());
        client.addHeader("System", "Android");
        client.addHeader("Version", Utils.getVersionName(context));

        // 添加Cookie
        PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
        BasicClientCookie newCookie = new BasicClientCookie("cookiesare", "mycookie");
        newCookie.setVersion(1);
        newCookie.setDomain("mydomain.com");
        newCookie.setPath("/");
        myCookieStore.addCookie(newCookie);
    }


    /**
     * url带参数
     *
     * @param url
     * @param params
     * @param responseHandler
     */
    public static RequestHandle post(Context context, String url,
                                     RequestParams params, AsyncHttpResponseHandler responseHandler) {
        LogUtil.e(url + "/" + params.toString());
        setHttpClient(context);
        return client.post(url, params, responseHandler);
    }
}
