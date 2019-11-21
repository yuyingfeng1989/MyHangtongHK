package com.bluebud.activity.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.Tracker;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.view.MarqueeTextView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/21 0021.
 * 情景模式
 */

public class SceneryModeActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, ProgressDialogUtil.OnProgressDialogClickListener {

    private int names[] = {R.string.vibrating_990, R.string.diabolo_990, R.string.shake, R.string.silence_990};
    private Map<String, Boolean> map;
    private SceneryModeAdapter adapter;
    private Tracker mCurTracker;
    private String sUserName;
    private int index;
    private int oldIndex;
    private SceneryModeActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.scenerymode_activity);
        WeakReference<SceneryModeActivity> weakReference = new WeakReference<SceneryModeActivity>(SceneryModeActivity.this);
        mContext = weakReference.get();
        mCurTracker = UserUtil.getCurrentTracker(mContext);
        sUserName = UserSP.getInstance().getUserName(mContext);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        RelativeLayout rl_title_right_text = (RelativeLayout) findViewById(R.id.rl_title_right_text);
        TextView tv_title_right = (TextView) findViewById(R.id.tv_title_right);
        MarqueeTextView tv_title = (MarqueeTextView) findViewById(R.id.tv_title);
        ListView listview = (ListView) findViewById(R.id.listview_990);
        findViewById(R.id.iv_back).setOnClickListener(mContext);
        rl_title_right_text.setOnClickListener(mContext);
        listview.setOnItemClickListener(mContext);
        rl_title_right_text.setVisibility(View.VISIBLE);
        tv_title.setVisibility(View.VISIBLE);
        listview.setVisibility(View.VISIBLE);

        tv_title_right.setText(R.string.submit1);
        tv_title.setText(R.string.scenery_mode);
        map = new HashMap<String, Boolean>();
        for (int i = 0; i < names.length; i++) {
            map.put(String.valueOf(i), false);
        }
        requestSceneryMode("0");//获取信息
        adapter = new SceneryModeAdapter();
        listview.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_right_text:
                requestSceneryMode(String.valueOf(index));
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for (int i = 0; i < names.length; i++) {
            if (i == position) {
                if (!map.get(String.valueOf(position))) {
                    index = position + 1;
                    map.put(String.valueOf(i), true);
                }
            } else {
                map.put(String.valueOf(i), false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 网络请求
     */
    private void requestSceneryMode(final String mode) {
        ChatHttpParams.getInstallSigle(mContext).chatHttpRequest(14, sUserName, mCurTracker.device_sn, null, null, null, null, mode, null, new ChatCallbackResult() {
            @Override
            public void callBackStart() {
                super.callBackStart();
                ProgressDialogUtil.show(mContext);
            }

            @Override
            public void callBackResult(String result) {
                ProgressDialogUtil.dismiss();
                if (mode.equals("0")) {//获取
                    String parse = (String) ChatHttpParams.getParseResult(14, result);
                    if (TextUtils.isEmpty(parse))
                        return;
                    int defaultMode = Integer.valueOf(parse);
                    oldIndex = defaultMode - 1;
                    map.put(String.valueOf(oldIndex), true);
                } else {//设置
                    ToastUtil.show(mContext, GsonParse.reBaseObjParse(result).what);
                    oldIndex = index - 1;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void callBackFailResult(String result) {
                ProgressDialogUtil.dismiss();
                if (!mode.equals("0")) {
                    ToastUtil.show(mContext, result);
                    map.put(String.valueOf(oldIndex), true);
                    map.put(String.valueOf(index - 1), false);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onProgressDialogBack() {

    }


    /**
     * 数据适配器
     */
    class SceneryModeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return names[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            View v = View.inflate(mContext, R.layout.scenerymode_activity, null);
            v.findViewById(R.id.ll_scenery990).setVisibility(View.VISIBLE);
            TextView text_scenery990 = (TextView) v.findViewById(R.id.text_scenery990);
            CheckBox checkbox_scenery990 = (CheckBox) v.findViewById(R.id.checkbox_scenery990);
            text_scenery990.setText(names[position]);
            if (map.get(String.valueOf(position))) {
                checkbox_scenery990.setChecked(true);
            } else checkbox_scenery990.setChecked(false);
            return v;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (map != null) {
            map.clear();
            map = null;
        }
        adapter = null;
        mCurTracker = null;
        sUserName = null;
        mContext = null;
    }
}
