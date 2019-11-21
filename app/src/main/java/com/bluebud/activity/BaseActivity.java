package com.bluebud.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.app.AppManager;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.ProgressDialogUtil;
public class BaseActivity extends Activity implements ProgressDialogUtil.OnProgressDialogClickListener{
    private View contentView;
    private LinearLayout contentLayout;
    private RelativeLayout rlTitle;
    private RelativeLayout rlLeftBack;
    private RelativeLayout rlRightText;
    private ImageView ivRightSetting;
    private Button btnRight;
    private TextView tvBackTitle;
    private TextView tvTitle;
    private TextView tvTitleRightText;

    private InputMethodManager imm;
    private ImageView ivLeftBlack;
    private RelativeLayout rlRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        AppManager.getAppManager().addActivity(this);
        initView();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(this.getClass().getSimpleName());
//        MobclickAgent.onResume(this);//友盟统计

    }

    @Override
    protected void onPause() {
        super.onPause();
        DialogUtil.dismiss();
//        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
//        MobclickAgent.onPause(this);
    }

    private void initView() {
        contentLayout = (LinearLayout) findViewById(R.id.ll_base_content);
        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        rlLeftBack = (RelativeLayout) findViewById(R.id.rl_title_back);
        rlRightText = (RelativeLayout) findViewById(R.id.rl_title_right_text);
        ivRightSetting = (ImageView) findViewById(R.id.iv_title_right_setting);
        ivLeftBlack = (ImageView) findViewById(R.id.iv_back);
        btnRight = (Button) findViewById(R.id.btn_title_right);
        tvBackTitle = (TextView) findViewById(R.id.tv_title_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitleRightText = (TextView) findViewById(R.id.tv_title_right);
        rlRight = (RelativeLayout) findViewById(R.id.rl_title_right);
    }

    public void addContentView(int layoutResID) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(layoutResID, null);
        contentLayout.addView(contentView);
    }

    public void setBaseTitleGone() {
        rlTitle.setVisibility(View.GONE);
    }

    public RelativeLayout getRight() {
        return rlRight;
    }

    public void setBaseLeftImage(int image) {
        ivLeftBlack.setBackgroundResource(image);
    }

    public void setBaseTitleColor(int color) {
        rlTitle.setBackgroundColor(color);
    }

    public RelativeLayout getBaseTitleLeftBack() {
        return rlLeftBack;
    }

    public RelativeLayout getBaseTitleRightText() {
        return rlRightText;
    }

    public ImageView getBaseTitleRightSetting() {
        return ivRightSetting;
    }

    public Button getBaseTitleRightBtn() {
        return btnRight;
    }

    public void setBaseTitleRightTextVisible(int visibility) {
        rlRightText.setVisibility(visibility);
    }

    public void setBaseTitleRightSettingVisible(int visibility) {
        ivRightSetting.setVisibility(visibility);
    }

    public void setBaseTitleVisible(int visibility) {
        tvTitle.setVisibility(visibility);
    }

    public void setBaseTitleTextSize(float size) {
        tvTitle.setTextSize(size);
    }

    public void setBaseTitleTextColor(int Color) {
        tvTitle.setTextColor(Color);
    }

    public void setBaseTitleBackText(int resid) {
        tvBackTitle.setText(resid);
    }

    public void setBaseBackTextVisible(boolean visible) {
        if (visible) {
            tvBackTitle.setVisibility(View.VISIBLE);
        } else {
            tvBackTitle.setVisibility(View.GONE);
        }
    }

    public void setBaseTitleText(int resid) {
        tvTitle.setText(resid);
    }

    public void setBaseTitleSize(int size) {
        tvTitle.setTextSize(size);
    }

    public void setBaseTitleText(String msg) {
        tvTitle.setText(msg);
    }

    public void setBaseTitleRightBtnText(int resid) {
        btnRight.setText(resid);
    }

    public void setBaseTitleRightText(int resid) {
        tvTitleRightText.setText(resid);
    }

    public void setBaseTitleRightSettingBackground(int resid) {
        ivRightSetting.setBackgroundResource(resid);
    }

    public void setBaseTitleRightBtnVisible(int visibility) {
        btnRight.setVisibility(visibility);
    }

    public void setBaseTitleRightBtnBackground(int resId) {
        btnRight.setBackgroundResource(resId);
    }

    public void setBaseTitleRightBtnTextColor(int resId) {
        btnRight.setTextColor(resId);
    }

    @Override
    public void onProgressDialogBack() {

    }
}
