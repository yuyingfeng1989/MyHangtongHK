package com.bluebud.utils;

import com.bluebud.liteguardian_hk.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ProgressDialogUtil {
	private static ProgressDialog mProgressDialog;

	public static void show(Context context) {
		show(context, null, null, true);
	}

	public static void show(Context context, String sTitle) {
		show(context, sTitle, null, true);
	}


	public static void showNoCanceled(Context context, String sTitle,
			final OnProgressDialogClickListener clickListener) {
		show(context, null, clickListener, false);
	}

	private static void show(Context context, String sTitle,
			final OnProgressDialogClickListener clickListener, boolean bIsCancel) {
		if (null != mProgressDialog && mProgressDialog.isShowing()) {
			return;
		}

		mProgressDialog = new ProgressDialog(context,R.style.Transparent_Dialog);

		mProgressDialog.show();

		View view;
		if (bIsCancel) {
			view = LayoutInflater.from(context).inflate(
					R.layout.layout_progress_dialog, null);
		} else {
			view = LayoutInflater.from(context).inflate(
					R.layout.layout_progress_dialog1, null);
		}
		TextView tvTitle = (TextView) view.findViewById(R.id.tv_loading);
		if (null == sTitle) {
			tvTitle.setVisibility(View.VISIBLE);
		} else {
			tvTitle.setVisibility(View.VISIBLE);
			tvTitle.setText(sTitle);
		}

		mProgressDialog.setContentView(view);
		mProgressDialog.setCanceledOnTouchOutside(bIsCancel);
		mProgressDialog.setCancelable(bIsCancel);

		if (null != clickListener) {
			mProgressDialog.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK
							&& event.getRepeatCount() == 0) {
						mProgressDialog.dismiss();
						clickListener.onProgressDialogBack();
					}
					return false;
				}
			});
		}
	}

	public interface OnProgressDialogClickListener {
		public void onProgressDialogBack();
	}

    public static void dismiss() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 判断进度条是否显示
     */
    public static boolean isShow() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            return true;
        else return false;
    }
}
