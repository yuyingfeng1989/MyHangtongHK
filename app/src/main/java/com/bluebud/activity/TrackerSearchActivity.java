package com.bluebud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bluebud.adapter.TrackerAdapter;
import com.bluebud.app.App;
import com.bluebud.app.AppManager;
import com.bluebud.constant.TrackerConstant;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TrackerSearchActivity extends BaseActivity implements
        OnClickListener {
    private TextView tvCancel;
    private EditText etSearch;
    private ListView lvTracker;

    private TrackerAdapter adapter;
    private List<Tracker> mTrackerList;
    private List<Tracker> trackers;
//	private View EmptyView;
//	private FrameLayout flTrackLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
    }

    private void init() {
        mTrackerList = UserUtil.getUserInfo(this).device_list;
        Tracker currTracker = UserUtil.getCurrentTracker(App.getContext());
        trackers = mTrackerList;
        if(currTracker.ranges == TrackerConstant.VALUE_RANGE_OBD)
            findViewById(R.id.main_search_bg).setBackgroundColor(getResources().getColor(R.color.black));
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        etSearch = (EditText) findViewById(R.id.et_search);
        lvTracker =  (ListView)findViewById(R.id.lv_tracker);
//		flTrackLay = (FrameLayout) findViewById(R.id.fl_tracklist);
        tvCancel.setOnClickListener(this);
        etSearch.setFocusableInTouchMode(true);
        etSearch.requestFocus();
//        lvTracker.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TrackerAdapter(this, trackers);
        lvTracker.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                String str = etSearch.getText().toString().trim();
                if (!Utils.isEmpty(str)) {
                    trackers = UserUtil.searchTrackers(
                            TrackerSearchActivity.this, str, mTrackerList);
                    adapter.setList(trackers);
                    lvTracker.setAdapter(adapter);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        lvTracker.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                int i = getTrackerListPosition(position);
                Intent intent = new Intent();
                intent.putExtra("POSITION", i);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) etSearch
                        .getContext()
                        .getSystemService(
                                TrackerSearchActivity.this.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(etSearch, 0);
            }
        }, 498);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
        }
    }

    private int getTrackerListPosition(int position) {
        for (int i = 0; i < mTrackerList.size(); i++) {
            if (mTrackerList.get(i).device_sn
                    .equals(trackers.get(position).device_sn)) {
                return i;
            }
        }
        return 0;
    }

//    private View createEmptyView() {
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        LayoutInflater inflater3 = LayoutInflater.from(this);
//        View view = inflater3.inflate(R.layout.layout_bottom_popup_empty, null);
//        view.setLayoutParams(lp);
//
//        return view;
//    }

}
