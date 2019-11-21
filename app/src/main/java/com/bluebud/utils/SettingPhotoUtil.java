package com.bluebud.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;

import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.chat.utils.ChatUtil;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Administrator on 2018/7/13.
 */

public class SettingPhotoUtil {
    private Activity mContext;
    private Tracker mTracker;
    private static final int PHOTO_GRAPH = 0;
    private static final int PICK = 111;
    private static final int ZOOM = 121;
    private PopupWindowUtils popupWindowUtils;

    public SettingPhotoUtil(Context context, Tracker tracker, PopupWindowUtils popupWindowUtils) {
        this.mContext = (Activity) context;
        this.mTracker = tracker;
        this.popupWindowUtils = popupWindowUtils;
    }


    /**
     * 申请拍照权限
     */
    public void requestPermission(final String photoName) {
        setHendPicture(photoName);
    }


    /**
     * 设置头像
     */
    private void setHendPicture(final String photoName) {
        // 点击弹出加载图片泡泡窗口
        popupWindowUtils.initPopupWindow(mContext.getString(R.string.photograph),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {// 拍照
                        takePhoto(photoName);
                    }
                }, mContext.getString(R.string.select_from_the_phone_album),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {// 从手机选择图片
                        photoAlbum();
                    }
                }, mContext.getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindowUtils.dismiss();
                    }
                });

    }

    /**
     * 相机拍照
     */
    private void takePhoto(String photoName) {
        File f = new File(Environment.getExternalStorageDirectory(), photoName);
        if (f.exists()) {
            f.delete();
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        else
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(mContext, "com.bluebud.liteguardian_hk", f));
        mContext.startActivityForResult(intent, PHOTO_GRAPH);
        popupWindowUtils.dismiss();
    }

    /**
     * 相册中选取图片
     */
    private void photoAlbum() {
        Intent mIntent2 = new Intent(Intent.ACTION_PICK);
        mIntent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        mContext.startActivityForResult(mIntent2, PICK);
        popupWindowUtils.dismiss();
    }

    /**
     * 对图片进行框选
     */
    public void startPhotoZOOM(Uri uri, String cacheName) {
        LogUtil.i("对图片进行框选");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 设置高宽比
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
//        intent.putExtra("return-data", true);
        Uri uritempFile = Uri.parse("file://" + "/" + Constants.CACHE_SAVE_PATH + cacheName);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        mContext.startActivityForResult(intent, ZOOM);
    }

    /**
     * 上传设备头像
     */
    public void setTrackerHead(final CircleImageView imageview, String cacheName) {
        final File file = new File(Constants.CACHE_SAVE_PATH + cacheName);
        if (!file.exists()) {
            ToastUtil.show(mContext, mContext.getString(R.string.file_no_find));
            return;
        }

        final String url = UserUtil.getServerUrl(mContext);
        RequestParams params = null;
        try {
            params = HttpParams.trackerPicture(mTracker.device_sn, file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (params == null)
            return;
        HttpClientUsage.getInstance().postFile(mContext, url,
                params, new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(mContext);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            mTracker.head_portrait = GsonParse.headPortraitParse(new String(response)).headPortrait;
                            UserUtil.saveTrackerportrait(mContext, mTracker);
//                            String name = UserSP.getInstance().getUserName(mContext);
//                            String photoUrl = UserSP.getInstance().getUserUrl(mContext, name) + mTracker.head_portrait;
                            String photoUrl = UserUtil.getServerIP(mContext) + mTracker.head_portrait;
                            if (mTracker.head_portrait != null)
                                Glide.with(mContext).load(photoUrl).placeholder(R.drawable.image_watch).dontAnimate().error(R.drawable.image_watch).into(imageview);
                            mContext.sendBroadcast(new Intent(Constants.ACTION_TRACTER_PICTURE_CHANGE));//通知首页定位更换头像
                        }
                        ToastUtil.show(mContext, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(mContext, mContext.getString(R.string.net_exception));
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }


    /**
     * 上传账号头像
     */
    public void setUserHead(final CircleImageView imageview, String cacheName) {
        File file = new File(Constants.CACHE_SAVE_PATH + cacheName);
        if (!file.exists()) {
            ToastUtil.show(mContext, R.string.file_no_find);
            return;
        }
        final String userName = UserSP.getInstance().getUserName(mContext);
        ChatHttpParams.getInstallSigle(mContext).chatHttpRequestFile(4, userName, file, null, null, null, null,
                new ChatCallbackResult() {
                    @Override
                    public void callBackStart() {
                        super.callBackStart();
                        ProgressDialogUtil.show(mContext);
                    }

                    @Override
                    public void callBackResult(String result) {
                        ProgressDialogUtil.dismiss();
                        if (result == null)
                            return;
                        String imageUrl = (String) ChatHttpParams.getInstallSigle(mContext).getParseResult(4, result);
                        if (!TextUtils.isEmpty(imageUrl)) {
                            String url = UserUtil.getServerIP(mContext) + imageUrl;
//                            String url = UserSP.getInstance().getUserUrl(mContext, userName) + imageUrl;
                            showPhoto(url, imageview);
                            UserSP.getInstance().saveChatValue(mContext, null, imageUrl, null, null);
                        }
                    }

                    @Override
                    public void callBackFailResult(String result) {
                        ProgressDialogUtil.dismiss();
                        ToastUtil.show(mContext, result);
                    }
                });
    }

    /**
     * 显示头像
     */
    public void showPhoto(String url, CircleImageView imageview) {
        ChatUtil.userPhoto = url;
        Glide.with(mContext).load(url).placeholder(R.drawable.img_defaulthead_628).error(R.drawable.img_defaulthead_628).dontAnimate().into(imageview);
    }
}
