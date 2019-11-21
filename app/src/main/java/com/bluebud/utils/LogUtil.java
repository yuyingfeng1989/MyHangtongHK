package com.bluebud.utils;

import android.text.TextUtils;
import android.util.Log;

import com.bluebud.app.App;

public final class LogUtil {
	private static final String TAG_GF = "gf";


	public static void v(String msg) {
		if (App.isDebug() && null != msg) {
			Log.v(TAG_GF, msg);
		}
	}


	public static void v(String msg, Throwable tr) {
		if (App.isDebug() && null != msg) {
			Log.v(TAG_GF, msg, tr);
		}
	}


	public static void d(String msg) {
		if (App.isDebug() && null != msg) {
			Log.d(TAG_GF, msg);
		}
	}

	public static void debug(String TAG, String msg) {
		if (App.isDebug() && !TextUtils.isEmpty(msg)) {
			Log.d(TAG, msg);
		}
	}


	public static void d(String msg, Throwable tr) {
		if (App.isDebug() && null != msg) {
			Log.d(TAG_GF, msg, tr);
		}
	}


	public static void i(String msg) {
		if (App.isDebug() && null != msg) {
			Log.i(TAG_GF, msg);
		}
	}


	public static void i(String msg, Throwable tr) {
		if (App.isDebug() && null != msg) {
			Log.i(TAG_GF, msg, tr);
		}
	}


	public static void w(String msg) {
		if (App.isDebug() && null != msg) {
			Log.w(TAG_GF, msg);
		}
	}


	public static void w(String msg, Throwable tr) {
		if (App.isDebug() && null != msg) {
			Log.w(TAG_GF, msg, tr);
		}
	}


	public static void e(String msg) {
		if (App.isDebug() && null != msg) {
			Log.e(TAG_GF, msg);
		}
	}

	public static void error(String TAG, String msg) {
		if (App.isDebug() && !TextUtils.isEmpty(msg)) {
			Log.e(TAG, msg);
		}
	}


	public static void e(String msg, Throwable tr) {
		if (App.isDebug() && null != msg) {
			Log.e(TAG_GF, msg, tr);
		}
	}

}
