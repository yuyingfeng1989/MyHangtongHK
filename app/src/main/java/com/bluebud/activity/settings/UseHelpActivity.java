package com.bluebud.activity.settings;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.bluebud.activity.BaseActivity;
import com.bluebud.liteguardian_hk.R;

//使用帮助
public class UseHelpActivity extends BaseActivity implements OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_use_help);
        init();
    }

    private void init() {
        super.setBaseTitleText(R.string.use_help);
        super.setBaseTitleVisible(View.VISIBLE);
        super.getBaseTitleLeftBack().setOnClickListener(this);
        findViewById(R.id.rl_account_login_issues).setOnClickListener(this);
        findViewById(R.id.rl_add_delete_device).setOnClickListener(this);
        findViewById(R.id.rl_apn_set_issuses).setOnClickListener(this);
        findViewById(R.id.rl_PT718_issuses).setOnClickListener(this);
        findViewById(R.id.rl_PT719_issuses).setOnClickListener(this);
        findViewById(R.id.rl_device_authorize).setOnClickListener(this);
        findViewById(R.id.rl_PT720_issuses).setOnClickListener(this);
        findViewById(R.id.rl_PT880_issuses).setOnClickListener(this);
        findViewById(R.id.rl_HT770_issuses).setOnClickListener(this);
        findViewById(R.id.rl_PT990_issuses).setOnClickListener(this);
        findViewById(R.id.rl_790s).setOnClickListener(this);

//		Locale locale = Locale.getDefault();
//		String language = locale.getLanguage();
//		String country = locale.getCountry();
//		if ("zh".equals(language)) {
//			((LinearLayout) findViewById(R.id.ll_PT880_issuses)).setVisibility(View.VISIBLE);
//		} else {
//			((LinearLayout) findViewById(R.id.ll_PT880_issuses)).setVisibility(View.GONE);
//		}
//
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        switch (view.getId()) {
            case R.id.rl_title_back://返回
                finish();
                break;
            case R.id.rl_account_login_issues://帐号、登录问题
                intent.putExtra("position", 1);
                startActivity(intent);
                break;
            case R.id.rl_add_delete_device://添加设备和解绑设备
                intent.putExtra("position", 2);
                startActivity(intent);
                break;
            case R.id.rl_apn_set_issuses://APN设置
                intent.putExtra("position", 3);
                startActivity(intent);

                break;
            case R.id.rl_PT718_issuses://PT718个人追踪器
                intent.putExtra("position", 4);
                startActivity(intent);
                break;
            case R.id.rl_PT719_issuses://PT719个人追踪器
                intent.putExtra("position", 5);
                startActivity(intent);
                break;
            case R.id.rl_device_authorize://设备授权与解除授权
                intent.putExtra("position", 6);
                startActivity(intent);

                break;
            case R.id.rl_PT720_issuses://设备授权与解除授权
                intent.putExtra("position", 7);
                startActivity(intent);

                break;

            case R.id.rl_HT770_issuses://770
                intent.putExtra("position", 8);
                startActivity(intent);
                break;
            case R.id.rl_790s:
                intent.putExtra("position", 9);
                startActivity(intent);
                break;

            case R.id.rl_PT880_issuses://880
                intent.putExtra("position", 10);
                startActivity(intent);
                break;
            case R.id.rl_PT990_issuses://990
                intent.putExtra("position", 11);
                startActivity(intent);
                break;
        }
    }
}
