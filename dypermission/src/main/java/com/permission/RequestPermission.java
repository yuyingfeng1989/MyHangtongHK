package com.permission;

import android.Manifest;
import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/11/7.
 */

public class RequestPermission {
    private Context mContext;
    private RequestPermissionCallback iCallback;

    public static RequestPermission create(Context context, RequestPermissionCallback iCallback) {
        WeakReference<RequestPermission> weakReferenceInstance = new WeakReference<RequestPermission>(new RequestPermission(context, iCallback));
        return weakReferenceInstance.get();
    }

    private RequestPermission(Context context, RequestPermissionCallback iCallback) {
        this.mContext = context;
        this.iCallback = iCallback;
    }

    /**
     * 申请权限列表
     */
    public void checkPermissionsUtil() {
        List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
        Context context = mContext.getApplicationContext();
//        permissionItems.add(new PermissionItem(Manifest.permission.READ_PHONE_STATE, context.getString(R.string.permission_read_phone_state)));//允许程序访问电话状态
//        permissionItems.add(new PermissionItem(Manifest.permission.CALL_PHONE, context.getString(R.string.permission_call_phone)));//允许程序从非系统拨号器里拨打电话
//        permissionItems.add(new PermissionItem(Manifest.permission.READ_CALL_LOG, context.getString(R.string.permission_read_call_log)));//读取通话记录
//        permissionItems.add(new PermissionItem(Manifest.permission.WRITE_CALL_LOG, context.getString(R.string.permission_write_call_log)));//允许程序写入（但是不能读）用户的联系人数据
//        permissionItems.add(new PermissionItem(Manifest.permission.PROCESS_OUTGOING_CALLS, context.getString(R.string.permission_outgoing_calls)));//允许程序监视，修改或放弃播出电话

        permissionItems.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, context.getString(R.string.permission_read_storage)));
        permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getString(R.string.permission_write_storage)));//程序可以读取设备外部存储空间（内置SDcard和外置SDCard）的文件，如果您的App已经添加了“WRITE_EXTERNAL_STORAGE ”权限 ，则就没必要添加读的权限了，写权限已经包含了读权限了。

        permissionItems.add(new PermissionItem(Manifest.permission.ACCESS_FINE_LOCATION, context.getString(R.string.permission_access_fine_location)));//允许程序通过GPS芯片接收卫星的定位信息
        permissionItems.add(new PermissionItem(Manifest.permission.ACCESS_COARSE_LOCATION, context.getString(R.string.permission_access_coarse_location)));//允许程序通过WiFi或移动基站的方式获取用户错略的经纬度信息

//        permissionItems.add(new PermissionItem(Manifest.permission.RECORD_AUDIO, context.getString(R.string.permission_record_audio)));//允许程序录制声音通过手机或耳机的麦克
//        permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, context.getString(R.string.permission_cameras)));//允许程序访问摄像头进行拍照

//        permissionItems.add(new PermissionItem(Manifest.permission.SEND_SMS, context.getString(R.string.permission_send_sms)));//允许程序发送短信
//        permissionItems.add(new PermissionItem(Manifest.permission.RECEIVE_SMS, context.getString(R.string.permission_receive_sms)));//允许程序接收短信
//        permissionItems.add(new PermissionItem(Manifest.permission.READ_SMS, context.getString(R.string.permission_read_sms)));//允许程序读取短信内容
//        permissionItems.add(new PermissionItem(Manifest.permission.RECEIVE_WAP_PUSH, context.getString(R.string.permission_receive_wap_push)));//允许程序接收WAP PUSH信息

//        permissionItems.add(new PermissionItem(Manifest.permission.READ_CALENDAR, context.getString(R.string.permission_read_calendar)));//允许程序读取用户的日程信息

//        permissionItems.add(new PermissionItem(Manifest.permission.READ_CONTACTS, context.getString(R.string.permission_read_contacts)));//允许程序访问联系人通讯录信息
//        permissionItems.add(new PermissionItem(Manifest.permission.WRITE_CONTACTS, context.getString(R.string.permission_write_calendar)));//写入联系人,但不可读取
//        permissionItems.add(new PermissionItem(Manifest.permission.GET_ACCOUNTS, context.getString(R.string.permission_get_accounts)));//访问一个联系人帐户列表在Accounts Service中

        PermissionManager.create(mContext)
                .permissions(permissionItems)
                .checkArrayPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                    }

                    @Override
                    public void onSuccess() {
                        iCallback.onPermissionSuccess();
                    }

                    @Override
                    public void onDeny(String permission, int position) {//权限授予失败
                    }

                    @Override
                    public void onGuarantee(String permission, int position) {//权限授予成功
                    }
                });
    }


    /**
     * 启动页申请权限列表
     */
    public void checkPageActivityPermissions() {
        List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
        Context context = mContext.getApplicationContext();
//        permissionItems.add(new PermissionItem(Manifest.permission.READ_PHONE_STATE, context.getString(R.string.permission_read_phone_state)));
//        permissionItems.add(new PermissionItem(Manifest.permission.CALL_PHONE, context.getString(R.string.permission_call_phone)));
//        permissionItems.add(new PermissionItem(Manifest.permission.READ_CALL_LOG, context.getString(R.string.permission_read_call_log)));
//        permissionItems.add(new PermissionItem(Manifest.permission.WRITE_CALL_LOG, context.getString(R.string.permission_write_call_log)));
//        permissionItems.add(new PermissionItem(Manifest.permission.PROCESS_OUTGOING_CALLS, context.getString(R.string.permission_outgoing_calls)));

        permissionItems.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, context.getString(R.string.permission_read_storage)));
        permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getString(R.string.permission_write_storage)));//程序可以读取设备外部存储空间（内置SDcard和外置SDCard）的文件，如果您的App已经添加了“WRITE_EXTERNAL_STORAGE ”权限 ，则就没必要添加读的权限了，写权限已经包含了读权限了。

        permissionItems.add(new PermissionItem(Manifest.permission.ACCESS_FINE_LOCATION, context.getString(R.string.permission_access_fine_location)));
        permissionItems.add(new PermissionItem(Manifest.permission.ACCESS_COARSE_LOCATION, context.getString(R.string.permission_access_coarse_location)));

//        permissionItems.add(new PermissionItem(Manifest.permission.SEND_SMS, context.getString(R.string.permission_send_sms)));
//        permissionItems.add(new PermissionItem(Manifest.permission.RECEIVE_SMS, context.getString(R.string.permission_receive_sms)));
//        permissionItems.add(new PermissionItem(Manifest.permission.READ_SMS, context.getString(R.string.permission_read_sms)));
//        permissionItems.add(new PermissionItem(Manifest.permission.RECEIVE_WAP_PUSH, context.getString(R.string.permission_receive_wap_push)));
        PermissionManager.create(mContext)
                .permissions(permissionItems)
                .checkArrayPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        iCallback.onPermissionSuccess();
                    }

                    @Override
                    public void onSuccess() {
                        iCallback.onPermissionSuccess();
                    }

                    @Override
                    public void onDeny(String permission, int position) {//权限授予失败
                    }

                    @Override
                    public void onGuarantee(String permission, int position) {//权限授予成功
                    }
                });
    }


    /**
     * 申请单个权限
     */
    public void checkSinglePermission(String permission, String permissionName) {
        PermissionManager.create(mContext)
                .animStyle(R.style.PermissionAnimScale)
                .checkSinglePermission(permission, permissionName, new PermissionCallback() {
                    @Override
                    public void onClose() {
                    }

                    @Override
                    public void onSuccess() {
                        iCallback.onPermissionSuccess();
                    }

                    @Override
                    public void onDeny(String permission, int position) {
                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                    }
                });
    }
}
