package com.bluebud.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.activity.MainActivity;
import com.bluebud.activity.PageActivity;
import com.bluebud.adapter.AlarmClockHistoryAdapter;
import com.bluebud.app.AppManager;
import com.bluebud.data.dao.AlarmClockHistoryDao;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.info.AlarmClockHistoryInfo;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;

public class AlarmClockHistoryActivity extends BaseActivity implements
        OnClickListener {
    private ListView lvHistory;

    private AlarmClockHistoryAdapter alarmClockHistoryAdapter;
    private AlarmClockHistoryDao alarmClockHistoryDao;
    private List<AlarmClockHistoryInfo> alarmClockHistoryInfos;

    private String sUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null == AppManager.getAppManager().findActivity(MainActivity.class)) {
//            UserUtil.clearUserInfo(this);
            UserUtil.saveServerUrl(this, null);
            startActivity(new Intent(this, PageActivity.class));
            finish();
        }

        addContentView(R.layout.activity_alarm_clock_history);

        init();
    }

    private void init() {
        setBaseTitleText(R.string.history_alarm_clock);
        setBaseTitleRightBtnText(R.string.clear_empty);
        getBaseTitleLeftBack().setOnClickListener(this);
        getBaseTitleRightBtn().setOnClickListener(this);

        lvHistory = (ListView) findViewById(R.id.lv_history);

        alarmClockHistoryInfos = new ArrayList<AlarmClockHistoryInfo>();
        alarmClockHistoryDao = new AlarmClockHistoryDao(this);

        alarmClockHistoryAdapter = new AlarmClockHistoryAdapter(this,
                alarmClockHistoryInfos);
        lvHistory.setAdapter(alarmClockHistoryAdapter);

        sUserName = UserSP.getInstance().getUserName(this);
        getData();
    }

    @Override
    protected void onDestroy() {
        alarmClockHistoryDao.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.btn_title_right:
                DialogUtil.show(this, R.string.prompt, R.string.clear_prompt,
                        R.string.confirm, new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                DialogUtil.dismiss();

                                alarmClockHistoryDao.delete(sUserName);
                                if (null != alarmClockHistoryInfos) {
                                    alarmClockHistoryInfos.clear();
                                }
                                alarmClockHistoryAdapter
                                        .setData(alarmClockHistoryInfos);
                                alarmClockHistoryAdapter.notifyDataSetChanged();
                            }
                        }, R.string.cancel, new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                DialogUtil.dismiss();
                            }
                        });
                break;
        }
    }

    private void getData() {
        alarmClockHistoryInfos = alarmClockHistoryDao.query(sUserName);
        if (null != alarmClockHistoryInfos && alarmClockHistoryInfos.size() > 0) {
            super.setBaseTitleRightBtnVisible(View.VISIBLE);

            alarmClockHistoryAdapter.setData(alarmClockHistoryInfos);
            alarmClockHistoryAdapter.notifyDataSetChanged();
        } else {
            super.setBaseTitleRightBtnVisible(View.GONE);
        }
        sendBroadcast(new Intent(Constants.ACTION_CLOCK_CLEAR));
    }
}
