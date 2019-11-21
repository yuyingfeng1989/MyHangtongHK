package com.bluebud.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import com.bluebud.liteguardian_hk.R;

public class ProgressDialogIntroduceUtil {
    private static OnProgressDialogDismissListener progressDialogDismissListener;

    public static void show(Context context,
                            final OnProgressDialogDismissListener dismissListener) {
        progressDialogDismissListener = dismissListener;

        final ProgressDialog mProgressDialog = new ProgressDialog(context,
                R.style.Transparent_Dialog);
        mProgressDialog.show();

        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_main_introduce, null);
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mProgressDialog.dismiss();
            }
        });

        int width = ((Activity) context).getWindowManager().getDefaultDisplay()
                .getWidth();
        int height = ((Activity) context).getWindowManager()
                .getDefaultDisplay().getHeight();
        WindowManager.LayoutParams params = mProgressDialog.getWindow().getAttributes();
        params.width = width;
        params.height = LayoutParams.WRAP_CONTENT;

        mProgressDialog.getWindow().setAttributes(params);

        mProgressDialog.setContentView(view);
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setCancelable(true);

        mProgressDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface arg0) {
                progressDialogDismissListener.onDismiss();
            }
        });
    }

    public interface OnProgressDialogDismissListener {
        public void onDismiss();
    }
}
