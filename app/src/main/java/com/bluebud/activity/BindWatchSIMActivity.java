package com.bluebud.activity;

import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.TextChangeUtils;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.Utils;
import com.castel.obd.OBD;
import com.dtr.barcode.core.CaptureActivity1;

public class BindWatchSIMActivity extends BaseActivity implements
        OnClickListener {
    private EditText etSimNo;
    private Button btnCommit;

    private String sSimNo = "";
    private String sTrackerNo = "";

    private boolean bScan = false;
    private boolean bPause = false;
    private boolean bChange = false;

    private SmsObserver smsObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_bind_watch_sim);

        init();

        // 为content://sms的数据改变注册监听器
        smsObserver = new SmsObserver(new Handler());
        getContentResolver().registerContentObserver(
                Uri.parse("content://sms"), true, smsObserver);
    }

    public void init() {
        setBaseTitleText(R.string.device_watch_add);
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleRightText(R.string.submit1);
        setBaseTitleRightTextVisible(View.VISIBLE);
        getBaseTitleRightText().setOnClickListener(this);
        getBaseTitleLeftBack().setOnClickListener(this);

        etSimNo = (EditText) findViewById(R.id.et_sim_no);
        btnCommit = (Button) findViewById(R.id.btn_submit);

        btnCommit.setOnClickListener(this);
        etSimNo.setInputType(InputType.TYPE_CLASS_NUMBER);
        etSimNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});//限制输入
        // 帐号和密码监听
        TextChangeUtils tc = new TextChangeUtils();
        tc.addEditText(etSimNo);
        tc.setButton(btnCommit);
    }

    @Override
    protected void onResume() {
        bPause = false;
        if (bScan) {
            bScan = false;
            toScan();
        }
        super.onResume();
        if (etSimNo.getText().toString().length() > 0) {
            ((Button) findViewById(R.id.btn_submit)).setEnabled(true);
            ((Button) findViewById(R.id.btn_submit)).setTextColor(this
                    .getResources().getColor(R.color.white));

        } else {
            ((Button) findViewById(R.id.btn_submit)).setEnabled(false);
            ((Button) findViewById(R.id.btn_submit)).setTextColor(this
                    .getResources().getColor(R.color.text_theme3));

        }

    }

    @Override
    protected void onPause() {
        bPause = true;
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        getContentResolver().unregisterContentObserver(smsObserver);

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.btn_submit:
            case R.id.rl_title_right_text:
                //toScan();
                sendSMS();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    sTrackerNo = data.getStringExtra("result");

                    sTrackerNo = OBD.SMSDecrypt(sTrackerNo);

                    LogUtil.i(sTrackerNo);

                    // int snType = Utils.serialNumberRange(sTrackerNo);

                    // if (0 == snType) {
                    // DialogUtil.show(this, R.string.prompt,
                    // R.string.tracker_type_error, R.string.confirm,
                    // new OnClickListener() {
                    //
                    // @Override
                    // public void onClick(View arg0) {
                    // DialogUtil.dismiss();
                    // }
                    // }, R.string.cancel, new OnClickListener() {
                    //
                    // @Override
                    // public void onClick(View arg0) {
                    // DialogUtil.dismiss();
                    // }
                    // });
                    // } else {
                    Intent intentWatch = new Intent(BindWatchSIMActivity.this,
                            BindWatchActivity.class);
                    intentWatch.putExtra("TRACKER_NO", sTrackerNo);
                    intentWatch.putExtra("SIM_NO", sSimNo);
                    startActivity(intentWatch);
                    finish();
                    // }
                }
            }
        }
    }

    private void toScan() {
//		Intent intentScan = new Intent();
//		intentScan.setClass(BindWatchSIMActivity.this, CaptureActivity1.class);
//		intentScan.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivityForResult(intentScan, 1);


        Intent intent = new Intent();
        intent.setClass(this, CaptureActivity1.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
    }

    private void sendSMS() {
        sSimNo = etSimNo.getText().toString().trim();

        if (Utils.isEmpty(sSimNo) || !Utils.isCorrectMobile(sSimNo)) {
            ToastUtil.show(this, R.string.input_watch_sim);
            return;
        }

        Uri uri = Uri.parse("smsto:" + sSimNo);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", OBD.SMSEncrypt(sSimNo));
        startActivity(intent);

        bChange = false;
    }

    // 一个继承自ContentObserver的监听器类
    class SmsObserver extends ContentObserver {

        public SmsObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {

            if (bChange) {
                return;
            } else {
                bChange = true;
            }

            if (!bPause) {
                toScan();
            } else {
                bScan = true;
            }

            // // 查询发送向箱中的短信
            // Cursor cursor = getContentResolver().query(
            // Uri.parse("content://sms/outbox"), null, null, null, null);
            // // 遍历查询结果获取用户正在发送的短信
            // while (cursor.moveToNext()) {
            // StringBuffer sb = new StringBuffer();
            // // 获取短信的发送地址
            // sb.append("发送地址："
            // + cursor.getString(cursor.getColumnIndex("address")));
            // // 获取短信的标题
            // sb.append("\n标题："
            // + cursor.getString(cursor.getColumnIndex("subject")));
            // // 获取短信的内容
            // sb.append("\n内容："
            // + cursor.getString(cursor.getColumnIndex("body")));
            // // 短信类型1是接收到的，2是已发出
            // sb.append("\ntype："
            // + cursor.getString(cursor.getColumnIndex("type")));
            // sb.append("\nstatus："
            // + cursor.getString(cursor.getColumnIndex("status")));
            // sb.append("\nread："
            // + cursor.getString(cursor.getColumnIndex("read")));
            // // 获取短信的发送时间
            // Date date = new Date(cursor.getLong(cursor
            // .getColumnIndex("date")));
            // // 格式化以秒为单位的日期
            // SimpleDateFormat sdf = new SimpleDateFormat(
            // "yyyy年MM月dd日 hh时mm分ss秒");
            // sb.append("\n时间：" + sdf.format(date));
            // System.out.println("查询到的正在发送的短信：" + sb.toString());
            //
            // LogUtil.i("查询到的正在发送的短信：" + sb.toString());
            // }
            super.onChange(selfChange);
        }

    }

}
