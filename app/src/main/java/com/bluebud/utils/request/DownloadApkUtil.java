package com.bluebud.utils.request;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.VersionObj;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;


/**
 * Created by Administrator on 2018/6/26.
 */

public class DownloadApkUtil {
//    private File dir;
    private Context mContext;
//    private String fileName = "Hangtong.apk";
//    private static final String DOWNLOAD_ACTION = "download_helper_first_action";
//    private File pathSave;//保存下载路径

    public DownloadApkUtil(Context context) {
        this.mContext = context;
    }

    /**
     * 检测版本更新
     */
    public void checkAPPUpdate(String url, final boolean isMain) {
//        if (DownloadSingleService.canRequest) {
//            ToastUtil.show(mContext, R.string.update_downloading);
//            return;
//        }
        RequestParams params = HttpParams.checkForUpdate("", UserSP.getInstance().getUserName(mContext));
        HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        LogUtil.e("版本检测==" + new String(response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            VersionObj versionObj = GsonParse.versionObjParse(new String(response));
                            if (versionObj == null) {
                                if (!isMain) {
                                    ToastUtil.show(mContext, obj.what);
                                }
                                return;
                            }
                            updateAppWeb(versionObj, isMain);
//                            showAPPUpdate(versionObj, isMain);
                        } else if (!isMain)
                            ToastUtil.show(mContext, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        if (!isMain)
                            ToastUtil.show(mContext, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }


    /**
     * 网页更新
     */
    private void updateAppWeb(VersionObj versionObj, boolean isMain) {
        String sCurVersion = Utils.getVersionName(mContext);
        String sNewVersion = versionObj.appVersion;
        final String sAPPUrl = versionObj.appUrl;
        int iCompare = Utils.verCompare(sNewVersion, sCurVersion);
        if (0 < iCompare && !TextUtils.isEmpty(sAPPUrl)) {
            AppSP.getInstance().saveVersion(mContext, sCurVersion);//保存当前版本
            DialogUtil.show(mContext, R.string.update_prompt,
                    R.string.update_prompt_content, R.string.update,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            DialogUtil.dismiss();
                            Uri uri = Uri.parse(sAPPUrl);
                            Intent netIntent = new Intent(Intent.ACTION_VIEW, uri);
                            mContext.startActivity(netIntent);
                        }
                    }, R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            DialogUtil.dismiss();
                        }
                    });
        } else {
            if (!isMain)
                ToastUtil.show(mContext, R.string.is_new_ver);
        }
    }

    /**
     * 显示APP升级对话框
     */
//    private void showAPPUpdate(VersionObj versionObj, boolean isMain) {
//        String sCurVersion = Utils.getVersionName(mContext);
//        String sNewVersion = versionObj.appVersion;
//        final String sAPPUrl = versionObj.phoneUrlAPK;
//        int iCompare = Utils.verCompare(sNewVersion, sCurVersion);
//
//        pathSave = new File(getDir(), fileName); //保存apk路径
//
//        if (0 < iCompare && !TextUtils.isEmpty(sAPPUrl)) {
//            AppSP.getInstance().saveVersion(mContext, sCurVersion);//保存当前版本
//            final int downloadState = DownloadSp.getSingleDownloadSp(mContext).getDownloadState();
//
//            if (!pathSave.exists() && downloadState == DownloadStatus.COMPLETE) {
//                DownloadSp.getSingleDownloadSp(mContext).clearDownloadValue();
//            } else if (pathSave.exists() && downloadState != DownloadStatus.COMPLETE) {
//                pathSave.delete();
//                DownloadSp.getSingleDownloadSp(mContext).clearDownloadValue();
//            }
//
//            if (pathSave.exists() && downloadState == DownloadStatus.COMPLETE) {
//                showDialogApk(versionObj, true);//安装
//            } else if (isMain) {
//                downloadApk(sAPPUrl, pathSave);//下载
//            } else {
//                showDialogApk(versionObj, false);//下载
//            }
//        } else {
//            if (!isMain) {
//                ToastUtil.show(mContext, R.string.is_new_ver);
//            }
//            if (pathSave.exists()) {
//                pathSave.delete();
//                DownloadSp.getSingleDownloadSp(mContext).clearDownloadValue();
//            }
//        }
//    }
//
//    /**
//     * 显示对话框
//     *
//     * @param isMain 是不是主界面自动下载true是，false不是
//     */
//    private void showDialogApk(final VersionObj versionObj, final boolean isMain) {
//        DialogUtil.showCheckApp(mContext, R.string.update_prompt,
//                versionObj.description, isMain ? R.string.installApk : R.string.update,
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View arg0) {
//                        DialogUtil.dismiss();
//                        if (isMain) inStallationAPK(pathSave);
//                        else downloadApk(versionObj.phoneUrlAPK, pathSave);
//                    }
//                }, R.string.cancel, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View arg0) {
//                        DialogUtil.dismiss();
//                    }
//                }, versionObj.isForceUpdate);
//    }
//
//    /**
//     * 启动更新下载apk
//     */
//    private void downloadApk(String url, File pathFile) {
//        DownloadHelper.getInstance().addTask(url, pathFile, DOWNLOAD_ACTION).submit(mContext);
//    }
//
//    /**
//     * 获取包是否完整
//     */
//    public boolean getUninatllApkInfo(String filePath) {
//        boolean result = false;
//        try {
//            PackageManager pm = mContext.getPackageManager();
//            PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
//            if (info != null) {
//                result = true;//完整
//            }
//        } catch (Exception e) {
//            result = false;//不完整
//        }
//        return result;
//    }
//
//    /**
//     * 直接安装apk
//     */
//    private void inStallationAPK(File pathFile) {
//        boolean isComplement = getUninatllApkInfo(pathFile.getAbsolutePath());
//        if (!isComplement) {
//            pathFile.delete();
//            DownloadSp.getSingleDownloadSp(mContext).clearDownloadValue();
//            return;
//        }
////        Uri uri;
//        Intent intent = new Intent(Intent.ACTION_VIEW);
////        if (Build.VERSION.SDK_INT >= 24) {
////            Log.e("TAG", "当前版本号为=" + Build.VERSION.SDK_INT);
////            //如果是7.0以上的系统，要使用FileProvider的方式构建Uri
////            uri = FileProvider.getUriForFile(mContext, "com.bluebud.liteguardian_hk", pathFile);
////            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////            intent.setDataAndType(uri, "application/vnd.android.package-archive");
////        } else {
//        intent.setDataAndType(Uri.fromFile(pathFile), "application/vnd.android.package-archive");
////        }
//        mContext.startActivity(intent);
//    }
//
//
//    /**
//     * 获取保存apk路径
//     */
//    private File getDir() {
//        if (dir != null && dir.exists()) {
//            return dir;
//        }
//        dir = new File(mContext.getExternalCacheDir(), "DownloadApk");
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        return dir;
//    }
}
