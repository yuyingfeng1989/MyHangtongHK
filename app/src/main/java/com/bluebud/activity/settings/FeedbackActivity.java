package com.bluebud.activity.settings;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.bluebud.activity.BaseActivity;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

//意见反馈页面
public class FeedbackActivity extends BaseActivity implements OnClickListener,
        OnProgressDialogClickListener {

    private EditText etContent;
    private RequestHandle requestHandle;
    private String devicesn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_feedback);
        init();
    }

    private void init() {

        super.setBaseTitleText(R.string.feedback_suggestion);
        super.setBaseTitleRightText(R.string.submit1);
        super.setBaseTitleRightTextVisible(View.VISIBLE);
        super.setBaseTitleVisible(View.VISIBLE);
        super.getBaseTitleRightText().setOnClickListener(this);
        super.getBaseTitleLeftBack().setOnClickListener(this);
        Tracker currentTracker = UserUtil.getCurrentTracker(this);
        if (currentTracker != null) {
            devicesn = currentTracker.device_sn;
        }
        etContent = (EditText) findViewById(R.id.et_content);
        etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1000)}); //限定最大输入最大1000个字符
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.rl_title_right_text://提交
                LogUtil.e("意见反馈************8");
                submit();
                break;
        }
    }

    @Override
    public void onProgressDialogBack() {
        // TODO Auto-generated method stub
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }

    /**
     * 提交意见反馈
     */
    private void submit() {
        String sContent = etContent.getText().toString().trim();
        if (TextUtils.isEmpty(sContent)) {
            ToastUtil.show(this, R.string.content_is_null);
            return;
        }
        String url = UserUtil.getServerUrl(this);
        if ("show".equals(sContent)) {//显示本地注册ip
            showIP(url);
            return;
        }

        if (TextUtils.isEmpty(devicesn)) {
            ToastUtil.show(this, R.string.prompt_unbind);
            return;
        }
        String model = android.os.Build.MODEL;//手机型号
        String phoneVersion = android.os.Build.VERSION.RELEASE;//手机系统版本号
        RequestParams params = HttpParams.saveIdea(devicesn, "feedback", sContent, model, phoneVersion);
        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                FeedbackActivity.this, null,
                                FeedbackActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            ToastUtil.show(FeedbackActivity.this, obj.what);
                            finish();
                        } else {
                            ToastUtil.show(FeedbackActivity.this, obj.what);
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(FeedbackActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    /**
     * 查看本地缓存服务器地址
     */
    private void showIP(String ip) {
        ToastUtil.show(this, ip);
    }

}
