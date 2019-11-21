package com.bluebud.activity.settings;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.adapter.AlarmClockAdapter;
import com.bluebud.adapter.ViewPagerAdapter;
import com.bluebud.data.dao.AlarmClockDao;
import com.bluebud.data.dao.AlarmClockHistoryDao;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.Advertisement;
import com.bluebud.info.AlarmClockInfo;
import com.bluebud.info.AlarmClockInfos;
import com.bluebud.info.AlarmClockTimeInfo;
import com.bluebud.info.ReBaseObj;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.swipemenulistview.SwipeMenu;
import com.bluebud.swipemenulistview.SwipeMenuCreator;
import com.bluebud.swipemenulistview.SwipeMenuItem;
import com.bluebud.swipemenulistview.SwipeMenuListView;
import com.bluebud.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.bluebud.utils.Constants;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class AlarmClockActivity extends BaseActivity implements
        OnClickListener, OnItemClickListener, OnProgressDialogClickListener {
    private RelativeLayout rlHistory;
    private TextView tvAlarmClockNum;
    private ImageView ivNew;
    private SwipeMenuListView mListView;
    private AlarmClockAdapter alarmClockAdapter;
    private List<AlarmClockInfo> alarmClockInfos;
//    private int iPosition;

    private AlarmClockHistoryDao alarmClockHistoryDao;
    private AlarmClockDao alarmClockDao;
    private String sUserName;

    private View vAdvertisement;
    private ViewPager viewPager;
    private List<ImageView> imageViews;
    private List<View> dots;
    private int currentItem = 0;
    private ScheduledExecutorService scheduledExecutorService;
    private RequestHandle requestHandle;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            viewPager.setCurrentItem(currentItem);
        }

        ;
    };
    private Runnable scrollRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (viewPager) {
                currentItem = (currentItem + 1) % imageViews.size();
                handler.obtainMessage().sendToTarget();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_alarm_clock);

        alarmClockHistoryDao = new AlarmClockHistoryDao(this);
        alarmClockDao = new AlarmClockDao(this);
        sUserName = UserSP.getInstance().getUserName(this);

        init();

        if (0 == UserSP.getInstance().getServerAndMap(this)) {
            initAdvertisement();
        }
        regesterBroadcast();
        getAlarmClock();
        //MobclickAgent.onEvent(this, Constants.UMENG_EVENT_ALARM_CLOCK);
    }

    private void init() {
        setBaseTitleText(R.string.lift_helper);
        setBaseTitleRightSettingVisible(View.VISIBLE);
        setBaseTitleRightSettingBackground(R.drawable.title_add_selector);
        getBaseTitleLeftBack().setOnClickListener(this);
        getBaseTitleRightSetting().setOnClickListener(this);

        rlHistory = (RelativeLayout) findViewById(R.id.rl_history);
        tvAlarmClockNum = (TextView) findViewById(R.id.tv_num);
        ivNew = (ImageView) findViewById(R.id.iv_new);
        mListView = (SwipeMenuListView) findViewById(R.id.listView);

        rlHistory.setOnClickListener(this);
        mListView.setOnItemClickListener(this);

        alarmClockInfos = new ArrayList<AlarmClockInfo>();
        alarmClockAdapter = new AlarmClockAdapter(this, alarmClockInfos);
        mListView.setAdapter(alarmClockAdapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @SuppressLint("ResourceAsColor")
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(Utils.dp2px(AlarmClockActivity.this, 90));
                // set a icon
                deleteItem.setIcon(R.drawable.delete_item);

//				deleteItem.setTitleSize(22);
//				deleteItem.setTitleColor(Color.rgb(0xFF, 0xFF, 0xFF));
//				deleteItem.setTitle(R.string.delete);

                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);
        mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu,
                                           int index) {
                switch (index) {
                    case 0:
                        deleteAlarmClock(position);
                        break;
                }
                return false;
            }
        });

        tvAlarmClockNum.setText("(" + alarmClockInfos.size() + ")");

        getAlarmHistoryNum();
    }

    private void initAdvertisement() {
        final List<Advertisement> advertisements = UserUtil.getAdvertisement(this, 2);

        vAdvertisement = findViewById(R.id.view_advertisement);
        vAdvertisement.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                AppSP.getInstance().getAdHeight(this));
        vAdvertisement.setLayoutParams(params);

        LinearLayout llDot = (LinearLayout) vAdvertisement.findViewById(R.id.ll_dot);
        imageViews = new ArrayList<ImageView>();
        dots = new ArrayList<View>();
        for (int i = 0; i < advertisements.size(); i++) {
            ImageView imageView = new ImageView(this);
//			imageView.setBackgroundResource(Constants.imageIds[i]);
            Glide.with(this).load(advertisements.get(i).image_url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageViews.add(imageView);

            imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent it = new Intent(Intent.ACTION_VIEW, Uri
                            .parse(advertisements.get(currentItem).ad_url));
                    it.setClassName("com.android.browser",
                            "com.android.browser.BrowserActivity");
                    startActivity(it);
                }
            });

            if (1 < advertisements.size()) {
                View view = LayoutInflater.from(this).inflate(R.layout.layout_dot,
                        null);
                View vDot = view.findViewById(R.id.v_dot);
                if (i == 0) {
                    vDot.setBackgroundResource(R.drawable.dot_focused);
                }
                dots.add(vDot);
                llDot.addView(view);
            }
        }

        viewPager = (ViewPager) vAdvertisement.findViewById(R.id.vp);
        viewPager.setAdapter(new ViewPagerAdapter(imageViews));

        if (1 < advertisements.size()) {
            viewPager.setOnPageChangeListener(new MyPageChangeListener());

            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(scrollRunnable, 1, 2,
                    TimeUnit.SECONDS);
        }
    }

    private class MyPageChangeListener implements OnPageChangeListener {
        private int oldPosition = 0;

        @Override
        public void onPageSelected(int position) {
            currentItem = position;
            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
            dots.get(position).setBackgroundResource(R.drawable.dot_focused);
            oldPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
    }

    @Override
    protected void onDestroy() {
        if (null != scheduledExecutorService) {
            scheduledExecutorService.shutdown();
        }

        alarmClockHistoryDao.close();
        alarmClockDao.close();
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.iv_title_right_setting:
                Intent addAlarmClockIntent = new Intent(this,
                        AlarmClockAddActivity.class);
                addAlarmClockIntent.putExtra("TYPE", 0);
                startActivityForResult(addAlarmClockIntent, 1);
                break;
            case R.id.rl_history:
                Intent historyIntent = new Intent(this,
                        AlarmClockHistoryActivity.class);
                startActivity(historyIntent);
                ivNew.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//        iPosition = position;
        Intent addAlarmClockIntent = new Intent(this,
                AlarmClockAddActivity.class);
        addAlarmClockIntent.putExtra("TYPE", 1);
        addAlarmClockIntent.putExtra("CLOCK_INFO",
                alarmClockInfos.get(position));
        startActivityForResult(addAlarmClockIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            getAlarmClock();
        }
    }

    @Override
    public void onProgressDialogBack() {
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }

    private void getAlarmHistoryNum() {
        int num = alarmClockHistoryDao.queryNoRead(sUserName);
        if (num > 0) {
            ivNew.setVisibility(View.VISIBLE);
        } else {
            ivNew.setVisibility(View.INVISIBLE);
        }
    }

    private void regesterBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_CLOCK);
        filter.addAction(Constants.ACTION_CLOCK_CLEAR);
        registerReceiver(broadcastReceiver, filter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            LogUtil.i(intent.getAction());
            if (intent.getAction().equals(Constants.ACTION_CLOCK)) {
                ivNew.setVisibility(View.VISIBLE);
            } else if (intent.getAction().equals(Constants.ACTION_CLOCK_CLEAR)) {
                ivNew.setVisibility(View.GONE);
            }
        }

        ;
    };

    private void getAlarmClock() {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.getClock();
        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                AlarmClockActivity.this, null,
                                AlarmClockActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (0 == obj.code) {
                            AlarmClockInfos alarmClockInfos1 = GsonParse
                                    .alarmClockParse(new String(response));
                            alarmClockInfos.clear();
                            for (AlarmClockInfo alarmClockInfo : alarmClockInfos1.reminds) {
                                alarmClockInfo.sUserName = sUserName;

                                if (alarmClockInfo.yearly) {
                                    alarmClockInfo.iType = 0;
                                    alarmClockInfo.sDay = alarmClockInfo.repeat_year
                                            + "-"
                                            + alarmClockInfo.repeat_month
                                            + "-" + alarmClockInfo.repeat_day;
                                } else if (alarmClockInfo.monthly) {
                                    alarmClockInfo.iType = 1;
                                    alarmClockInfo.sDay = alarmClockInfo.repeat_year
                                            + "-"
                                            + alarmClockInfo.repeat_month
                                            + "-" + alarmClockInfo.repeat_day;
                                } else if (alarmClockInfo.weekly) {
                                    alarmClockInfo.iType = 2;
                                    String[] arrWeeks = new String[]{"0",
                                            "0", "0", "0", "0", "0", "0"};
                                    if (alarmClockInfo.monday) {
                                        arrWeeks[0] = "1";
                                    } else {
                                        arrWeeks[0] = "0";
                                    }
                                    if (alarmClockInfo.tuesday) {
                                        arrWeeks[1] = "1";
                                    } else {
                                        arrWeeks[1] = "0";
                                    }
                                    if (alarmClockInfo.wednesday) {
                                        arrWeeks[2] = "1";
                                    } else {
                                        arrWeeks[2] = "0";
                                    }
                                    if (alarmClockInfo.thursday) {
                                        arrWeeks[3] = "1";
                                    } else {
                                        arrWeeks[3] = "0";
                                    }
                                    if (alarmClockInfo.friday) {
                                        arrWeeks[4] = "1";
                                    } else {
                                        arrWeeks[4] = "0";
                                    }
                                    if (alarmClockInfo.saturday) {
                                        arrWeeks[5] = "1";
                                    } else {
                                        arrWeeks[5] = "0";
                                    }
                                    if (alarmClockInfo.sunday) {
                                        arrWeeks[6] = "1";
                                    } else {
                                        arrWeeks[6] = "0";
                                    }
                                    alarmClockInfo.arrWeeks = arrWeeks;
                                }
                                for (AlarmClockTimeInfo alarmClockTimeInfo : alarmClockInfo.diabolo) {
                                    alarmClockInfo.times
                                            .add(alarmClockTimeInfo.AlarmTime);
                                }

                                alarmClockInfos.add(alarmClockInfo);

                                if (null == alarmClockInfos) {
                                    alarmClockInfos = new ArrayList<AlarmClockInfo>();
                                }
                                alarmClockAdapter.setData(alarmClockInfos);
                                alarmClockAdapter.notifyDataSetChanged();

                                tvAlarmClockNum.setText("("
                                        + alarmClockInfos.size() + ")");

                                alarmClockDao.insert(alarmClockInfos);
                            }
                        } else {
                            ToastUtil.show(AlarmClockActivity.this, obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(AlarmClockActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    private void deleteAlarmClock(final int position) {
        if (UserUtil.isGuest(this)) {
            ToastUtil.show(this, R.string.guest_no_set);
            return;
        }

        final int id = alarmClockInfos.get(position).id;

        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.deleteClock(id);
        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                AlarmClockActivity.this, null,
                                AlarmClockActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (0 == obj.code) {
                            alarmClockDao.delete(id);
                            alarmClockInfos.remove(position);
                            alarmClockAdapter.setData(alarmClockInfos);
                            alarmClockAdapter.notifyDataSetChanged();
                        }
                        ToastUtil.show(AlarmClockActivity.this, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(AlarmClockActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

}
