package com.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2018/11/12.
 */

public class ShowDialogActivity extends Activity {

    //    private static final int BACK_CODE = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showAlertDialog(getString(R.string.start_suspension_box), getString(R.string.suspension_box_prompt), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        });
    }


    /**
     * dialog弹框
     */
    private void showAlertDialog(String title, String msg, DialogInterface.OnClickListener onClickListener) {
        AlertDialog mDialog = new AlertDialog.Builder(this)//,R.style.AlertDialog
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton(getString(R.string.permission_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).setPositiveButton(getString(R.string.permission_ensure), onClickListener).create();
        if (mDialog != null && !mDialog.isShowing())
            mDialog.show();
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
