package com.bluebud.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebud.activity.BindListActivity;
import com.bluebud.app.AppApplication;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.TelephoneInfo;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.request.RequestUtil;
import com.bluebud.view.ClearEditText;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;


public class DialogUtil {
    private static AlertDialog mDialog;
    private static Context mContext = AppApplication.getInstance();

    public static AlertDialog show(Context context, int titleId, int msgId,
                                   int submintId, OnClickListener pListener, int cancelId,
                                   OnClickListener nListener) {
        String title = mContext.getResources().getString(titleId);
        String msg = mContext.getResources().getString(msgId);
        showDialog(title, msg, submintId, pListener, true, cancelId, nListener, true);
        return mDialog;
    }

    /**
     * 版本检测弹框
     */
//    public static AlertDialog showCheckApp(Context context, int titleId, String msg,
//                                           int submintId, OnClickListener pListener, int cancelId,
//                                           OnClickListener nListener, int isForceUpdate) {
//        String title = mContext.getResources().getString(titleId);
//        showDialogCheckApp(title, msg, submintId, pListener, cancelId, nListener, isForceUpdate);
//        return mDialog;
//    }

    private static boolean isNewDevice = false;

    public static AlertDialog show(Context context, int fraction,
                                   String hour_long, String mileage, String calorie, OnClickListener pListener) {
        showPetWalkRecordingDialog(fraction, hour_long, mileage, calorie, pListener, true);
        return mDialog;
    }

    public static AlertDialog show(Context context, int submintId, OnClickListener pListener, int cancelId, OnClickListener nListener) {
        showAddDeviceDialog(submintId, pListener, true, cancelId, nListener, true);
        return mDialog;
    }

    public static AlertDialog show(Context context, int titleId, String msg,
                                   int submintId, OnClickListener pListener, int cancelId, OnClickListener nListener) {
        String title = mContext.getResources().getString(titleId);
        showDialog(title, msg, submintId, pListener, true, cancelId, nListener, true);
        return mDialog;
    }

    public static void show(Context context, int titleId, int msgId,
                            int submintId, OnClickListener pListener) {
        String title = mContext.getResources().getString(titleId);
        String msg = mContext.getResources().getString(msgId);
        showDialog(title, msg, submintId, pListener, false, -1, null, false);
    }

    public static void show(Context context, int titleId, String msg,
                            int submintId, OnClickListener pListener) {
        String title = mContext.getResources().getString(titleId);
        showDialog(title, msg, submintId, pListener, false, -1, null, false);
    }

    public static void showSystemAlert(Context context, int titleId, int msgId,
                                       int submintId, OnClickListener pListener) {
        String title = mContext.getResources().getString(titleId);
        String msg = mContext.getResources().getString(msgId);
        showDialog(title, msg, submintId, pListener, false, -1, null, false);
    }

    public static void showInsuranceAlert(Context context, int titleId,int msgId, int submintId, OnClickListener pListener) {
        String title = context.getResources().getString(titleId);
        String msg = context.getResources().getString(msgId);
        showDialog(title, msg, submintId, pListener, false, -2, null, false);
//        return mDialog;
    }

    public static AlertDialog showSystemAlert1(Context context, int titleId,
                                               String msg, int submintId, OnClickListener pListener, int cancelId,
                                               OnClickListener nListener) {
        String title = mContext.getResources().getString(titleId);
        showDialog(title, msg, submintId, pListener, true, cancelId, nListener, false);
        return mDialog;
    }


    /**
     * 检测app升级弹框
     */
//    private static void showDialogCheckApp(String title, String msg,
//                                           int submintId, OnClickListener pListener,
//                                           int cancelId, OnClickListener nListener, int isForceUpdate) {
//        dismiss();
//        mDialog = new AlertDialog.Builder(mContext).create();
//        if (isForceUpdate == 0) {
//            mDialog.setCanceledOnTouchOutside(true);
//            mDialog.setCancelable(false);
//        } else {
//            mDialog.setCanceledOnTouchOutside(false);
//            mDialog.setCancelable(true);
//        }
//        Window window = mDialog.getWindow();
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        } else {
//            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//        }
//        mDialog.show();
//        window.setContentView(R.layout.layout_dialog);
//
//        TextView tvTitle = (TextView) window.findViewById(R.id.tv_dialog_title);
//        TextView tvContent = (TextView) window.findViewById(R.id.tv_dialog_content);
//        Button btnOk = (Button) window.findViewById(R.id.btn_dialog_ok);
//        Button btnCancel = (Button) window.findViewById(R.id.btn_dialog_cancel);
//        btnCancel.setText(cancelId);
//        btnCancel.setOnClickListener(nListener);
//        window.findViewById(R.id.ll_dialog_title).setVisibility(View.VISIBLE);
//        tvTitle.setText(title);
//        tvContent.setText(msg);
//        btnOk.setText(submintId);
//        btnOk.setOnClickListener(pListener);
//    }
    private static void showDialog(String title, String msg,
                                   int submintId, OnClickListener pListener, boolean bIsCancelBtn,
                                   int cancelId, OnClickListener nListener, boolean bIsCanceled) {
        dismiss();
        mDialog = new AlertDialog.Builder(mContext).create();
        if (cancelId == -2)
            mDialog.setCanceledOnTouchOutside(true);
        else
            mDialog.setCanceledOnTouchOutside(bIsCanceled);
        Window window = mDialog.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        mDialog.show();
        window.setContentView(R.layout.layout_dialog);

        if (cancelId == -2)
            window.findViewById(R.id.ll_dialog_title).setVisibility(View.VISIBLE);
        TextView tvTitle = (TextView) window.findViewById(R.id.tv_dialog_title);
        TextView tvContent = (TextView) window.findViewById(R.id.tv_dialog_content);
        Button btnOk = (Button) window.findViewById(R.id.btn_dialog_ok);
        Button btnCancel = (Button) window.findViewById(R.id.btn_dialog_cancel);
        View vLine = window.findViewById(R.id.in_line);
        if (bIsCancelBtn) {
            btnCancel.setText(cancelId);
            btnCancel.setOnClickListener(nListener);
            window.findViewById(R.id.ll_dialog_title).setVisibility(View.VISIBLE);
        } else {
            btnCancel.setVisibility(View.GONE);
            vLine.setVisibility(View.GONE);
        }
        tvTitle.setText(title);
        tvContent.setText(msg);
        btnOk.setText(submintId);
        btnOk.setOnClickListener(pListener);
    }

    private static void showPetWalkRecordingDialog(int fraction,
                                                   String hour_long, String mileage, String calorie, OnClickListener pListener, boolean bIsCanceled) {
        dismiss();
        mDialog = new AlertDialog.Builder(mContext).create();
        mDialog.setCanceledOnTouchOutside(bIsCanceled);
        mDialog.setCancelable(true);
        Window window = mDialog.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        mDialog.show();
        window.setContentView(R.layout.layout_pet_walk_recording_dialog);

        ImageView IvDelete = (ImageView) window.findViewById(R.id.iv_delete);
        ImageView wave2 = (ImageView) window.findViewById(R.id.wave2);
        ImageView normal = (ImageView) window.findViewById(R.id.normal);
        TextView tv_fraction = (TextView) window.findViewById(R.id.tv_fraction);//分值
        TextView tv_point = (TextView) window.findViewById(R.id.tv_point);//提示语
        TextView tv_hour_long = (TextView) window.findViewById(R.id.tv_hour_long);//时间
        TextView tv_mileage = (TextView) window.findViewById(R.id.tv_mileage);//j里程
        TextView tv_calorie = (TextView) window.findViewById(R.id.tv_calorie);//卡路里
        if (fraction >= 100) {//100分
            normal.setBackgroundResource(R.drawable.wave5);
            wave2.setBackgroundResource(R.drawable.wave55);
            tv_point.setText(mContext.getResources().getString(R.string.pet_walk_point));
        } else if (fraction >= 81 && fraction <= 99) {//81-99
            normal.setBackgroundResource(R.drawable.wave6);
            wave2.setBackgroundResource(R.drawable.wave66);
            tv_point.setText(mContext.getResources().getString(R.string.pet_walk_point80));
        } else if (fraction >= 31 && fraction <= 80) {//31-80
            normal.setBackgroundResource(R.drawable.wave7);
            wave2.setBackgroundResource(R.drawable.wave77);
            tv_point.setText(mContext.getResources().getString(R.string.pet_walk_point30));
        } else {
            normal.setBackgroundResource(R.drawable.wave11);
            wave2.setBackgroundResource(R.drawable.wave33);
            tv_point.setText(mContext.getResources().getString(R.string.pet_walk_point29));
        }
        tv_fraction.setText(fraction + "");
        tv_hour_long.setText(hour_long);
        tv_mileage.setText(mileage);
        tv_calorie.setText(calorie);
        IvDelete.setOnClickListener(pListener);
    }

    private static void showAddDeviceDialog(int submintId, OnClickListener pListener, boolean bIsCancelBtn,
                                            int cancelId, OnClickListener nListener, boolean bIsCanceled) {
        dismiss();
        mDialog = new AlertDialog.Builder(mContext).create();
        mDialog.setCanceledOnTouchOutside(bIsCanceled);
        mDialog.setCancelable(bIsCanceled);
        Window window = mDialog.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        mDialog.show();
        window.setContentView(R.layout.layout_dialog_add_device);

        Button btnOk = (Button) window.findViewById(R.id.btn_dialog_ok);
        Button btnCancel = (Button) window.findViewById(R.id.btn_dialog_cancel);
        View vLine = window.findViewById(R.id.in_line);
        if (bIsCancelBtn) {
            btnCancel.setText(cancelId);
            btnCancel.setOnClickListener(nListener);
        } else {
            btnCancel.setVisibility(View.GONE);
            vLine.setVisibility(View.GONE);
        }
        btnOk.setText(submintId);
        btnOk.setOnClickListener(pListener);
    }


    public static void showEditDialog(final Context context, int titleId,
                                      int submintId, int cancelId,
                                      final OnEditTextEditListener editListener, String sSIM, String hint) {
        dismiss();
        mDialog = new AlertDialog.Builder(context).create();
        Window window = mDialog.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        mDialog.show();
        mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.layout_dialog_edit);

        TextView tvTitle = (TextView) window.findViewById(R.id.tv_dialog_title);
        final EditText etContent = (EditText) window.findViewById(R.id.et_sim_no);
        Button btnOk = (Button) window.findViewById(R.id.btn_dialog_ok);
        Button btnCancel = (Button) window.findViewById(R.id.btn_dialog_cancel);
        tvTitle.setText(titleId);
        etContent.setHint(hint);
        etContent.setText(sSIM);
        etContent.setSelection(sSIM.length());
        btnOk.setText(submintId);
        btnCancel.setText(cancelId);

        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Utils.hiddenKeyboard(mContext, arg0);
                mDialog.dismiss();

            }
        });
        btnOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Utils.hiddenKeyboard(mContext, arg0);
                editListener.editTextEdit(etContent.getText().toString(), mDialog);
            }
        });
    }

    public static void dismiss() {
        if (null != mDialog) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

//    public interface OnSwitchButtonListener {
//        public void switchButtonChecked(boolean bRing, boolean bShake);
//    }

    public interface OnEditTextEditListener {
        public void editTextEdit(String str, AlertDialog mDialog);
    }

    public interface OnEditTextEditSOSListener {
        public void editTextEditSOS(String str, AlertDialog mDialog, boolean isNewDevice);
    }

    public static void showAddDevice(final Context context) {
        DialogUtil.show(context, R.string.bind_equipment, new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.dismiss();
                Intent intent = new Intent(context, BindListActivity.class);
                intent.putExtra("formpage", Constants.MAIN_PAGE);
                context.startActivity(intent);

            }
        }, R.string.back, new OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogUtil.dismiss();

            }
        });
    }


    /**
     * 用户使用条约责任需知
     */
    public static void showUserAgreement(final Context context, int titleId, boolean isRegistPolicy) {
        dismiss();
        mDialog = new AlertDialog.Builder(mContext).create();
        Window window = mDialog.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mDialog.show();
        window.setContentView(R.layout.layout_user_agreement);

        TextView tvTitle = (TextView) window.findViewById(R.id.tv_dialog_title);
        TextView text_read_message = (TextView) window.findViewById(R.id.text_read_message);
        Button btnConfirm = (Button) window.findViewById(R.id.btn_confirm);
        if (!isRegistPolicy)
            text_read_message.setText(context.getString(R.string.agreement_article));
        else
            text_read_message.setText(context.getString(R.string.privacy_article));
        tvTitle.setText(titleId);
        btnConfirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mDialog.dismiss();
            }
        });

    }

    public static AlertDialog show(Context context, int msgId,
                                   int submintId, OnClickListener pListener, int cancelId,
                                   OnClickListener nListener) {
        String msg = mContext.getResources().getString(msgId);
        showDialog1(msg, submintId, pListener, true, cancelId, nListener, true);
        return mDialog;
    }

    private static void showDialog1(String msg,
                                    int submintId, OnClickListener pListener, boolean bIsCancelBtn,
                                    int cancelId, OnClickListener nListener, boolean bIsCanceled) {
        dismiss();
        mDialog = new AlertDialog.Builder(mContext).create();
        mDialog.setCanceledOnTouchOutside(bIsCanceled);
        mDialog.setCancelable(bIsCanceled);
        Window window = mDialog.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        mDialog.show();
        window.setContentView(R.layout.layout_dialog);

        LinearLayout llTitle = (LinearLayout) window.findViewById(R.id.ll_dialog_title);
        llTitle.setVisibility(View.GONE);
        TextView tvContent = (TextView) window.findViewById(R.id.tv_dialog_content);
        Button btnOk = (Button) window.findViewById(R.id.btn_dialog_ok);
        Button btnCancel = (Button) window.findViewById(R.id.btn_dialog_cancel);
        View vLine = window.findViewById(R.id.in_line);
        if (bIsCancelBtn) {
            btnCancel.setText(cancelId);
            btnCancel.setOnClickListener(nListener);
        } else {
            btnCancel.setVisibility(View.GONE);
            vLine.setVisibility(View.GONE);
        }
        tvContent.setText(msg);
        btnOk.setText(submintId);
        btnOk.setOnClickListener(pListener);
    }


//    public static void showEditSOSDialog(final Context context, String sTrackerNo,
//                                         final OnEditTextEditSOSListener editListener) {
//        dismiss();
//        mDialog = new AlertDialog.Builder(mContext).create();
//        Window window = mDialog.getWindow();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
//            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            window.setType(WindowManager.LayoutParams.TYPE_TOAST);
//        } else {
//            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        }
//        mDialog.show();
//        mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        window.setContentView(R.layout.layout_dialog_sos_edit);
//
//        final EditText etContent = (EditText) window.findViewById(R.id.et_sim_no1);
//        final EditText etContent2 = (EditText) window.findViewById(R.id.et_sim_no2);
//        final List<EditText> etTelephones = new ArrayList<EditText>();
//        etTelephones.add(etContent);
//        etTelephones.add(etContent2);
//        Button btnOk = (Button) window.findViewById(R.id.btn_dialog_ok);
//        getPhoneBook(context, sTrackerNo, etTelephones, btnOk);//获取sos号码
//        // 帐号和密码监听
//        TextSingleChangeUtils tc = new TextSingleChangeUtils();
//        tc.addEditText(etContent);
//        tc.addEditText(etContent2);
//        tc.setButton(mContext, btnOk, true);
//        btnOk.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                Utils.hiddenKeyboard(mContext, arg0);
//                editListener.editTextEditSOS(getEtContent(etTelephones, isNewDevice),
//                        mDialog, isNewDevice);
//            }
//        });
//    }


    /**
     * 课程表设置弹框
     */
    public static void showDialogs(final Context context, String title, final RequestUtil.ICallBack iCallBack) {
        dismiss();
        mDialog = new AlertDialog.Builder(context).create();
        mDialog.setCanceledOnTouchOutside(true);
        Window window = mDialog.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        mDialog.show();
        if (context instanceof Activity)
            reParamLayout(context, window);
        mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.shedule_dialog);

        TextView tvTitle = (TextView) window.findViewById(R.id.tv_dialog_title);
        final ClearEditText class_name = (ClearEditText) window.findViewById(R.id.et_class_name);
        Button btnOk = (Button) window.findViewById(R.id.btn_dialog_ok);
        Button btnCancel = (Button) window.findViewById(R.id.btn_dialog_cancel);
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hiddenKeyboard(context, v);
                DialogUtil.dismiss();
            }
        });
        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.dismiss();
                Utils.hiddenKeyboard(context, v);
                iCallBack.callBackData(class_name.getText().toString(), 1);
            }
        });
        if (!TextUtils.isEmpty(title)) {
            class_name.setText(title);
            tvTitle.setText(context.getString(R.string.edit_course));
        } else {
            tvTitle.setText(context.getString(R.string.add_course));
        }
    }


    private static void setBtnEnble(List<EditText> etTelephones, Button btnOk) {

        if (etTelephones.get(0).getText().toString().length() > 0 || etTelephones.get(1).getText().toString().length() > 0) {
            btnOk.setEnabled(true);
            btnOk.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            btnOk.setEnabled(false);
            btnOk.setTextColor(mContext.getResources().getColor(R.color.next_step_tip));
        }
    }

    private static String getEtContent(List<EditText> etTelephones, boolean isNewDevice) {
        StringBuffer sb = new StringBuffer();
        if (!isNewDevice) {
            for (int i = 0; i < etTelephones.size(); i++) {
                sb.append(etTelephones.get(i).getText().toString() + ",");
            }
            sb.append(",,,,,,,");
        } else {
            for (int i = 0; i < etTelephones.size(); i++) {
                sb.append(":" + etTelephones.get(i).getText().toString() + ",");
            }
            sb.append(":,:,:,:,:,:,:,:");
        }
        return sb.toString();

    }


    public static void getPhoneBook(final Context context, String sTrackerNo, final List<EditText> etTelephones, final Button btnOk) {
        String url = UserUtil.getServerUrl(context);
        RequestParams params = HttpParams.getPhoneBook(sTrackerNo);
        HttpClientUsage.getInstance().post(context, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        if (0 == obj.code) {
                            TelephoneInfo telephoneInfo = GsonParse.getTelephone(new String(response));
                            /**guoqz add 20160318.*/
//							isNewDevice = true;
                            if (telephoneInfo == null)
                                return;
                            //boolean isNewDevice =false;
                            if (telephoneInfo.hardware >= 15) {//720新版本可以编辑，720老版本不可编辑
                                isNewDevice = true;
                            } else {
                                isNewDevice = false;
                            }
                            setTelephone(isNewDevice, telephoneInfo.phone, etTelephones);
                            setBtnEnble(etTelephones, btnOk);
                        }
                        ToastUtil.show(context, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(context, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }


    private static void setTelephone(boolean isNewDevice, String str, List<EditText> etTelephones) {

        if (!isNewDevice) {
            // 旧设备
            LogUtil.i("str旧设备：" + str);
            String[] strs = str.split(",", 10);
            for (int i = 0; i < 2; i++) {
                int index = strs[i].indexOf(":");
                if (index != -1) {
                    String strtemp = strs[i].substring(index + 1, strs[i].length());
                    etTelephones.get(i).setText(strtemp);
                    etTelephones.get(i).setSelection(strtemp.length());
                } else {
                    etTelephones.get(i).setText(strs[i]);
                    etTelephones.get(i).setSelection(strs[i].length());
                }
            }
        } else {
            // 新设备
//			str = "昵称1:,昵称2:电话号码2,:,:电话号码4";
            // guoqz add 20160302.
            // 解析通讯录昵称及电话号码
            // 数据格式举例: 昵称1:电话号码1,昵称2:电话号码2,昵称3:电话号码3,昵称4:电话号码4
            LogUtil.i("str新设备：" + str);
            String[] strs = str.split(",");
            for (int i = 0; i < 2; i++) {
                int index = strs[i].indexOf(":");
                if (-1 == index) {
                    // 针对接口之前的数据(无昵称)
                    if (strs[i].length() > 0) {
                        // 第一种情况：电话号码不为空   "电话号码"
                        //etNickNames.get(i).setText("");
                        etTelephones.get(i).setText(strs[i]);
                        etTelephones.get(i).setSelection(strs[i].length());
                    } else {
                        // 第二种情况：电话号码为空   ""
                        //etNickNames.get(i).setText("");
                        etTelephones.get(i).setText("");
                    }
                } else {
                    if (1 == strs[i].length()) {
                        // 第三种情况：昵称电话号码为空   ":"
                        //etNickNames.get(i).setText("");
                        etTelephones.get(i).setText("");
                    } else if (strs[i].length() > 1) {
                        if (1 == (index + 1)) {
                            // 第四种情况：昵称为空,电话号码不为空   ":电话号码"
                            //etNickNames.get(i).setText("");
                            etTelephones.get(i).setText(strs[i].substring(1, strs[i].length()));
                            etTelephones.get(i).setSelection(strs[i].length() - 1);
                        } else if (strs[i].length() == (index + 1)) {
                            // 第五种情况：昵称不为空,电话号码为空   "昵称:"
                            //etNickNames.get(i).setText(strs[i].substring(0, strs[i].length()-1));
                            //etNickNames.get(i).setText(arrName[i]);
                            //etNickNames.get(i).setText(strs[i].substring(0, strs[i].indexOf(":")));
                            etTelephones.get(i).setText("");
                        } else {
                            // 第六种情况：昵称和电话号码都不为空   "昵称:电话号码"
                            //etNickNames.get(i).setText(strs[i].substring(0, strs[i].indexOf(":")));
                            etTelephones.get(i).setText(strs[i].substring(strs[i].indexOf(":") + 1, strs[i].length()));
                            etTelephones.get(i).setSelection(strs[i].length());
                        }
                    }
                }
            }
        }
    }

    /**
     * 一键拨号
     */
    public static void callPhoneDialog(String title, String content, String confire, String cancel, OnClickListener mOk, OnClickListener mCancel) {
        dismiss();
        mDialog = new AlertDialog.Builder(mContext).create();
        Window window = mDialog.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        mDialog.show();
        window.setContentView(R.layout.layout_dialog);
        if (!TextUtils.isEmpty(title)) {
            window.findViewById(R.id.ll_dialog_title).setVisibility(View.VISIBLE);
            TextView tvTitle = (TextView) window.findViewById(R.id.tv_dialog_title);
            tvTitle.setText(title);
            tvTitle.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        window.findViewById(R.id.tv_dialog_content).setVisibility(View.GONE);
        TextView tvContent_center = (TextView) window.findViewById(R.id.tv_dialog_content_center);
        Button btnOk = (Button) window.findViewById(R.id.btn_dialog_ok);
        Button btnCancel = (Button) window.findViewById(R.id.btn_dialog_cancel);
        tvContent_center.setVisibility(View.VISIBLE);
        tvContent_center.setText(content);
        btnOk.setText(confire);
        btnCancel.setText(cancel);
        btnOk.setTextColor(mContext.getResources().getColor(R.color.black));
        btnCancel.setTextColor(mContext.getResources().getColor(R.color.black));
        btnOk.setOnClickListener(mOk);
        btnCancel.setOnClickListener(mCancel);
    }


    /**
     * 计算宽高
     */
    private static void reParamLayout(Context context, Window window) {
        int width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        int height = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
        if (width < height) {
            width = width * 8 / 10;
        } else {
            width = height * 6 / 10;
        }
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = width;
        window.setAttributes(params);
    }
}
