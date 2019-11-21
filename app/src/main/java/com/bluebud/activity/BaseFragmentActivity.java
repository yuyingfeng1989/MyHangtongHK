package com.bluebud.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.app.AppManager;
import com.bluebud.liteguardian_hk.R;

public class BaseFragmentActivity extends FragmentActivity {
    private View contentView;
    private LinearLayout contentLayout;
    private RelativeLayout rlLeftBack;
    private RelativeLayout rlRightRegister;
    private ImageView ivRightSetting;
    private TextView tvBackTitle;
    private TextView tvTitle;

    private InputMethodManager imm;
    private RelativeLayout rlRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        AppManager.getAppManager().addActivity(this);
        initView();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
//		MobclickAgent.onPageStart(this.getClass().getSimpleName());
//		MobclickAgent.onResume(this);//友盟统计
    }

    @Override
    protected void onPause() {
        super.onPause();
//		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
//		MobclickAgent.onPause(this);
    }

    /**
     * 点击输入框以外其他地方，软键盘消失
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (this.getCurrentFocus() != null) {
                if (this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(this.getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void initView() {
        contentLayout = (LinearLayout) findViewById(R.id.ll_base_content);
        rlLeftBack = (RelativeLayout) findViewById(R.id.rl_title_back);
        rlRightRegister = (RelativeLayout) findViewById(R.id.rl_title_right_text);
        rlRight = (RelativeLayout) findViewById(R.id.rl_title_right);
        ivRightSetting = (ImageView) findViewById(R.id.iv_title_right_setting);
        tvBackTitle = (TextView) findViewById(R.id.tv_title_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);

    }

    public void addContentView(int layoutResID) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(layoutResID, null);
        contentLayout.addView(contentView);
    }

    public RelativeLayout getBaseTitleLeftBack() {
        return rlLeftBack;
    }

    public RelativeLayout getBaseTitleRightRegister() {
        return rlRightRegister;
    }

    public RelativeLayout getRight() {
        return rlRight;
    }

    public ImageView getBaseTitleRightSetting() {
        return ivRightSetting;
    }


    public void setBaseTitleRightRegisterVisible(int visibility) {
        rlRightRegister.setVisibility(visibility);
    }

    public void setBaseTitleRightSettingVisible(int visibility) {
        ivRightSetting.setVisibility(visibility);
    }

    public void setBaseTitleVisible(int visibility) {
        tvTitle.setVisibility(visibility);
    }

    public void setBaseTitleBackText(int resid) {
        tvBackTitle.setText(resid);
    }

    public void setBaseTitleText(int resid) {
        tvTitle.setText(resid);
    }

    public void setBaseTitleText(String msg) {
        tvTitle.setText(msg);
    }

}
