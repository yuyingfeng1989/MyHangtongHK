package com.bluebud.http;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.Context;

import com.bluebud.utils.LogUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

public class HttpClientRest {
//	private static AsyncHttpClient client = new AsyncHttpClient(true,80,7000);
	private static AsyncHttpClient client = new AsyncHttpClient();

	public static AsyncHttpClient getClient() {
		return client;
	}

	public static void setHttpClient(Context context) {
		LogUtil.e("当前请求语言="+Utils.getLanguage2Url());
		client.setTimeout(60000); // 设置链接超时，如果不设置，默认为10s
		client.addHeader("Accept-Language", Utils.getLanguage2Url());
		client.addHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		client.addHeader("System", "Android");
		client.addHeader("Version", Utils.getVersionName(context));
		// 添加Cookie
		PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
		client.setCookieStore(myCookieStore);
		BasicClientCookie newCookie = new BasicClientCookie("cookiesare",
				"mycookie");
		newCookie.setVersion(1);
		newCookie.setDomain("mydomain.com");
		newCookie.setPath("/");
		myCookieStore.addCookie(newCookie);
	}

	/**
	 * url不带参数
	 * 
	 * @param url
	 * @param responseHandler
	 */
	public static RequestHandle post(Context context, String url,
			AsyncHttpResponseHandler responseHandler) {
		LogUtil.i(url);
		setHttpClient(context);
		return client.post(url, responseHandler);
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
		LogUtil.i("请求：");
		return client.post(url, params, responseHandler);
	}
}
