package com.bluebud.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {
	private static Toast toast = null; 
	public static void show(Context context, String message) {
		if (Utils.isEmpty(message)) {
			return;
		}
		if (toast==null) {
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		}else {
			toast.setText(message);  
			toast.setDuration(Toast.LENGTH_SHORT); 
		}
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static void show(Context context, int resId) {
		if (toast==null) {
			toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
		}else {
			toast.setText(resId);  
			toast.setDuration(Toast.LENGTH_SHORT); 
		}
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	

}
