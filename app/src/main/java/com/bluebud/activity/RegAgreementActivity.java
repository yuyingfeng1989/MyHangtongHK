package com.bluebud.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.bluebud.liteguardian_hk.R;


public class RegAgreementActivity extends BaseActivity implements OnClickListener {
    //	private int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_agreement);
//		type = getIntent().getIntExtra(Constants.EXTRA_AGREEMENT_TYPE, 0);
        init();
    }

    public void init() {
        setBaseTitleText(R.string.agreement_text);
        setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
        }
    }
}
