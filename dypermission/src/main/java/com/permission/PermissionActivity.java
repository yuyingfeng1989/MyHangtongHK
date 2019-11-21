package com.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import java.util.List;
import java.util.ListIterator;

/**
 * @author SelfZhangTQ
 */

public class PermissionActivity extends Activity {
    private static PermissionCallback mCallback;
    private List<PermissionItem> mCheckPermissions;
    private static final int REQUEST_CODE_MUTI = 2;
    public static final int REQUEST_CODE_MUTI_SINGLE = 3;
    private static final int REQUEST_SETTING = 110;
    private CharSequence mAppName;
    private AlertDialog mDialog;

    public static void setCallBack(PermissionCallback callBack) {
        PermissionActivity.mCallback = callBack;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mCallback = null;
    }

    /**
     * 获取传入的数据
     */
    private void getDatas() {
        Intent intent = getIntent();
        mCheckPermissions = (List<PermissionItem>) intent.getSerializableExtra(ConstantValue.DATA_PERMISSIONS);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDatas();
        mAppName = getApplicationInfo().loadLabel(getPackageManager());
        String[] strs = getPermissionStrArray();
        ActivityCompat.requestPermissions(PermissionActivity.this, strs, REQUEST_CODE_MUTI);
    }

    /**
     * 权限申请
     */
    private void reRequestPermission(final String permission) {
        requestPermission(new String[]{permission}, REQUEST_CODE_MUTI_SINGLE);
    }

    /**
     * 权限集合申请
     */
    private void requestPermission(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(PermissionActivity.this, permissions, requestCode);
    }

    /**
     * 重新申请权限数组的索引
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermission();
        switch (requestCode) {
            case REQUEST_CODE_MUTI://多权限授权返回结果
                if (mCheckPermissions == null || mCheckPermissions.size() < 1) {
                    onFinish();
                    return;
                }
                PermissionItem item = mCheckPermissions.get(0);
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, item.Permission)) {//拒绝授予权限并且勾选了不在提示
                    if (mDialog != null && mDialog.isShowing())
                        mDialog.dismiss();
                    backPermissionDeney(item);
                    return;
                }
                if (mCheckPermissions.size() > 0) {//用户拒绝了某个或多个权限，重新申请
                    String[] strs = getPermissionStrArray();
                    ActivityCompat.requestPermissions(PermissionActivity.this, strs, REQUEST_CODE_MUTI);
                } else {
                    onFinish();
                }
                break;
            case REQUEST_CODE_MUTI_SINGLE:
                if (mCheckPermissions == null || mCheckPermissions.size() < 1) {
                    onFinish();
                    return;
                }
                if (grantResults == null || grantResults.length < 1) {
                    onFinish();
                    return;
                }
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {  //重新申请后再次拒绝
                    try { //弹框警告! haha
                        if (mCheckPermissions.size() > 0) {//用户拒绝了某个或多个权限，重新申请
                            String[] strs = getPermissionStrArray();
                            ActivityCompat.requestPermissions(PermissionActivity.this, strs, REQUEST_CODE_MUTI);
                        } else {
                            onFinish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        onClose();
                    }
                } else {
                    if (mCheckPermissions.size() > 0) {//用户拒绝了某个或多个权限，重新申请
                        String[] strs = getPermissionStrArray();
                        ActivityCompat.requestPermissions(PermissionActivity.this, strs, REQUEST_CODE_MUTI);
                    } else {
                        onFinish();
                    }
                }
                break;
        }
    }

    /**
     * 结果处理
     */
    private void backPermissionDeney(PermissionItem item) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, item.Permission)) {
            String name = item.PermissionName;
            String title = String.format(getString(R.string.permission_title), name);
            String msg = String.format(getString(R.string.permission_denied_with_naac), mAppName, name, mAppName);
            showAlertDialog(title, msg, getString(R.string.permission_reject), getString(R.string.permission_go_to_setting), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Uri packageURI = Uri.parse("package:" + getPackageName());
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        startActivityForResult(intent, REQUEST_SETTING);
                    } catch (Exception e) {
                        e.printStackTrace();
                        onClose();
                    }
                }
            });
        }
    }

    /**
     * 设置中授予权限返回
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTING) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            checkPermission();
            if (mCheckPermissions.size() > 0) {
                reRequestPermission(mCheckPermissions.get(0).Permission);
            } else {
                onFinish();
            }
        }
    }

    /**
     * dialog弹框
     */
    private void showAlertDialog(String title, String msg, String cancelTxt, String PosTxt, DialogInterface.OnClickListener onClickListener) {
        mDialog = new AlertDialog.Builder(this)//,R.style.AlertDialog
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton(cancelTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onClose();
                    }
                }).setPositiveButton(PosTxt, onClickListener).create();
        if (mDialog != null && !mDialog.isShowing())
            mDialog.show();
    }

    /**
     * 获取权限集合
     */
    private String[] getPermissionStrArray() {
        String[] str = new String[mCheckPermissions.size()];
        for (int i = 0; i < mCheckPermissions.size(); i++) {
            str[i] = mCheckPermissions.get(i).Permission;
        }
        return str;
    }

    /**
     * 检测权限
     */
    private void checkPermission() {
        if (mCheckPermissions == null || mCheckPermissions.size() < 1) {
            onFinish();
            return;
        }
        ListIterator<PermissionItem> iterator = mCheckPermissions.listIterator();
        while (iterator.hasNext()) {
            PermissionItem permissionItem = iterator.next();
            boolean isPerssion = PermissionManager.checkPermission(getApplication(), permissionItem.Permission);
            if (mCheckPermissions.contains(permissionItem)) {
                if (isPerssion) {
                    iterator.remove();
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    private void onFinish() {
        if (mCheckPermissions != null && mCheckPermissions.size() > 0) {
            finish();
            return;
        }
        if (mCallback != null) {
            mCallback.onSuccess();
        }
        finish();
    }

    private void onClose() {
        if (mCallback != null) {
            mCallback.onClose();
        }
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
        overridePendingTransition(0, 0);
    }


    private void onDeny(String permission, int position) {
        if (mCallback != null) {
            mCallback.onDeny(permission, position);
        }
    }

    private void onGuarantee(String permission, int position) {
        if (mCallback != null) {
            mCallback.onGuarantee(permission, position);
        }
    }

    private PermissionItem getPermissionItem(String permission) {
        for (PermissionItem permissionItem : mCheckPermissions) {
            if (permissionItem.Permission.equals(permission)) {
                return permissionItem;
            }
        }
        return null;
    }
}
