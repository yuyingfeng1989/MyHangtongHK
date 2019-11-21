package com.bluebud.http;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;

import com.bluebud.utils.LogUtil;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

public class HttpClientUsage {
	private static HttpClientUsage instance;

	public final List<RequestHandle> requestHandles = new LinkedList<RequestHandle>();

	public static HttpClientUsage getInstance() {
		if (null == instance) {
			instance = new HttpClientUsage();
		}
		return instance;
	}

	public void addRequestHandle(RequestHandle handle) {
		if (null != handle) {
			requestHandles.add(handle);
		}
	}

	public List<RequestHandle> getRequestHandles() {
		return requestHandles;
	}

	public RequestHandle post(Context context, String url,
			AsyncHttpResponseHandlerReset responseHandlerRe) {
		responseHandlerRe.setContext(context);
		return HttpClientRest.post(context, url, responseHandlerRe);
	}

	public RequestHandle post(Context context, String url,
			RequestParams params,
			AsyncHttpResponseHandlerReset responseHandlerRe) {
		responseHandlerRe.setContext(context);
//		url = "http://172.18.2.159:8081/WebApi2d/WebAPI";
//		url = "http://172.18.2.100:8080/WebApi2d/WebAPI";
//		url = "http://172.18.2.200:8080/WebApi2d/WebAPI";
		return HttpClientRest.post(context, url, params, responseHandlerRe);
	}
	
//	public RequestHandle post1(Context context, String url,
//			RequestParams params,
//			AsyncHttpResponseHandlerReset responseHandlerRe) {
//		responseHandlerRe.setContext(context);
//		url = "http://218.17.161.66:10230/SyncData/RemotingAPI";
//		return HttpClientRest.post(context, url, params, responseHandlerRe);
//	}
	
	public RequestHandle postFile(Context context, String url,
			RequestParams params,
			AsyncHttpResponseHandlerReset responseHandlerRe) {
		responseHandlerRe.setContext(context);
		return HttpClientFileRest.post(context, url, params, responseHandlerRe);
	}

	public void cancelRequests(Context context) {
		HttpClientRest.getClient().cancelRequests(context, true);
		LogUtil.i("cancelRequests()");
	}

}
