package com.bluebud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;


public class WatchListActivity extends BaseActivity implements OnClickListener,
        OnProgressDialogClickListener {

    //	private TextView tvSkipStep;
    private ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_watch_list);
        init();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: init
     * @Description: 初始化，找到控件id并设置监听器
     */
    public void init() {

        // 去掉标题栏
        setBaseTitleGone();
        ivBack = (ImageView) findViewById(R.id.iv_back1);
        ivBack.setOnClickListener(this);
        LinearLayout llPt720 = (LinearLayout) findViewById(R.id.ll_pt720);
        LinearLayout llHt770 = (LinearLayout) findViewById(R.id.ll_ht770);
        LinearLayout ll_pt790 = (LinearLayout) findViewById(R.id.ll_pt790);
        ll_pt790.setVisibility(View.VISIBLE);
        ll_pt790.setOnClickListener(this);
        llPt720.setOnClickListener(this);
        llHt770.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back1:

                finish();
                break;
            case R.id.ll_pt720:// 720手表
                Intent intentWatch = new Intent(WatchListActivity.this,
                        BindWatchSIMActivity.class);
                startActivity(intentWatch);

                break;
            case R.id.ll_pt790:// 790手表
                String mEquipmentType1 = Constants.WATCH_EQUIPMENT;
                Intent intent1 = new Intent();
                intent1.putExtra("equipment_type", mEquipmentType1);
                intent1.putExtra("watcher", 1);
                intent1.setClass(WatchListActivity.this, BindActivity.class);
                startActivity(intent1);
                break;

            case R.id.ll_ht770://770手表
                String mEquipmentType = Constants.WATCH_EQUIPMENT;
                Intent intent2 = new Intent();
                intent2.putExtra("watcher", 2);
                intent2.putExtra("equipment_type", mEquipmentType);
                intent2.setClass(WatchListActivity.this, BindActivity.class);
                startActivity(intent2);

                break;


        }
    }

    @Override
    public void onProgressDialogBack() {
        LogUtil.i("onProgressDialogBack()");
    }


}
