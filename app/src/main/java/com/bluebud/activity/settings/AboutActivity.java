package com.bluebud.activity.settings;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.activity.RegAgreementActivity;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;


public class AboutActivity extends BaseActivity implements OnClickListener {
    private TextView tvVersion;
    private RelativeLayout rlAgreement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_about);
        init();
    }

    private void init() {
        super.setBaseTitleText(R.string.about_mine);
        super.setBaseTitleVisible(View.VISIBLE);
        super.getBaseTitleLeftBack().setOnClickListener(this);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        rlAgreement = (RelativeLayout) findViewById(R.id.rl_agreement);
        rlAgreement.setOnClickListener(this);
        findViewById(R.id.text_net).setOnClickListener(this);
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        tvVersion.setText(getString(R.string.app_name) + " V" + version);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.rl_agreement://用户协议
                Intent agreementIntent = new Intent(this, RegAgreementActivity.class);
                agreementIntent.putExtra(Constants.EXTRA_AGREEMENT_TYPE, 1);
                startActivity(agreementIntent);
                break;
            case R.id.text_net:
                Intent intent = new Intent(this,WebActivity.class);
                startActivity(intent);
                break;

        }
    }
}
