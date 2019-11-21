package com.bluebud.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.bluebud.adapter.PeripherDetailAdapter;
import com.bluebud.adapter.ViewPagerAdapter;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientGooglePlacesPeripher;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.Advertisement;
import com.bluebud.info.AroundStoreInfo;
import com.bluebud.info.CurrentGPS;
import com.bluebud.info.PeripherDetail;
import com.bluebud.info.PoiReInfo;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//设置页面
public class PeripherDetailGooglePoiActivity extends BaseActivity implements OnClickListener,
        OnItemClickListener {
    private ListView lvSetting;

    private Tracker mCurTracker;
    private String sTrackerNo;
    private String keyword;

    private List<ImageView> imageViews;
    private List<View> dots;
    private ViewPager viewPager;
    private ScheduledExecutorService scheduledExecutorService;
    private int currentItem = 0;
    private List<PeripherDetail> peripherDetailList = new ArrayList<PeripherDetail>();
    private List<PeripherDetail> geogleList;

    private LatLng curPointLocation;// 定时定位
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
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

    private Context mContext;
    private View headView;
    private LinearLayout vAdvertisement;
    private String themesName;

    private PeripherDetailAdapter adapter;
    private int ranges;
    private int position;
    private String[] themes;
    private String[] themes1 = new String[]{"hospital", "pharmacy", "bank", "park"};
    private String[] themes2 = new String[]{"medical", "hairdressing", "foster", "shoot", "funeral"};
    private String[] themes3 = new String[]{"vechicle", "repair", "parking", "gas"};
    private String[] themes4 = new String[]{"repair", "gas", "club"};
    private String[] themes5 = new String[]{"hospital", "school", "playground", "park"};
    private String[] themes7 = new String[]{"hospital", "pharmacy", "bank", "gym"};
    private int storeType;//平台搜索类型


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_periper_detail);
        mContext = this;
        mCurTracker = UserUtil.getCurrentTracker(mContext);
        if (mCurTracker != null) {
            sTrackerNo = mCurTracker.device_sn;
        }
        geogleList = new ArrayList<PeripherDetail>();
        initData();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initData() {
        Intent intent = getIntent();
        if (null != intent) {
            themesName = intent.getStringExtra("themesName");
            storeType = intent.getIntExtra("storeType", 0);//类型
            ranges = intent.getIntExtra("ranges", 0);
            position = intent.getIntExtra("position", 0);
            LogUtil.i("themesName:" + themesName + ",ranges:" + ranges + ",position:" + position);
        }
    }


    private void init() {
        super.setBaseTitleColor(getResources().getColor(R.color.bg_theme));
        super.setBaseTitleText(themesName);
        super.setBaseTitleVisible(View.VISIBLE);
        super.getBaseTitleLeftBack().setOnClickListener(this);
        super.getRight().setOnClickListener(this);
        super.getBaseTitleRightSetting().setVisibility(View.VISIBLE);
        lvSetting = (ListView) findViewById(R.id.lv_setting);
        initAdvertisement();
        lvSetting.addHeaderView(headView);
        adapter = new PeripherDetailAdapter(mContext, peripherDetailList);
        lvSetting.setAdapter(adapter);
        lvSetting.setOnItemClickListener(this);
        if (ranges == 1) {
            themes = themes1;
        } else if (ranges == 2) {
            themes = themes2;
        } else if (ranges == 3 || ranges == 6) {
            themes = themes3;
        } else if (ranges == 4) {
            themes = themes4;
        } else if (ranges == 5) {
            if (mCurTracker.protocol_type == 8) {//litefamily
                themes = themes1;
                return;
            }
            themes = themes5;
        } else {
            themes = themes7;
        }
        keyword = "";
        switch (position) {
            case 0:
                keyword = themes[0];
                break;
            case 1:
                keyword = themes[1];
                break;
            case 2:
                keyword = themes[2];
                break;
            case 3:
                keyword = themes[3];
                break;
            case 4:
                keyword = themes[4];
                break;


            default:
                break;
        }
        initMapView();
    }

    private void initMapView() {
        getCurrentGPS();//获取gps及周边位置
    }


    /**
     * 获取周边信息
     */
    private void getAroundStore(final CurrentGPS currentGPS) {
        if (currentGPS == null) {
            ProgressDialogUtil.dismiss();
            ToastUtil.show(mContext, getString(R.string.no_result));
            return;
        }
        double lng = currentGPS.lng;//经度
        double lat = currentGPS.lat;//纬度
        curPointLocation = new LatLng(currentGPS.lat, currentGPS.lng);
        ChatHttpParams.getInstallSigle(this).chatHttpRequest(17, String.valueOf(lng), sTrackerNo, null, null, String.valueOf(lat),
                String.valueOf(storeType), null, null, new ChatCallbackResult() {
                    @Override
                    public void callBackResult(String result) {
                        List<AroundStoreInfo> list = (List<AroundStoreInfo>) ChatHttpParams.getParseResult(17, result);
                        if (list != null) {
                            for (AroundStoreInfo info : list) {
                                PeripherDetail pd = new PeripherDetail();
                                pd.address = info.address;
                                pd.name = info.name;
                                pd.latitude = info.lat;
                                pd.longitude = info.lng;
                                pd.distance = (int) Utils.getDistance(curPointLocation.longitude, curPointLocation.latitude, info.lng, info.lat);
                                peripherDetailList.add(pd);
                            }
                        }
                        LogUtil.i("keyword=" + keyword);
                        getGeoCodeFromLocation(curPointLocation, 4000, keyword);
                    }

                    @Override
                    public void callBackFailResult(String result) {
                        getGeoCodeFromLocation(curPointLocation, 4000, keyword);
                    }
                });
    }

    /**
     * "反地理编码
     *
     * @param ll
     * @return
     */
    private void getGeoCodeFromLocation(LatLng ll, int radius, String types) {
        HttpClientGooglePlacesPeripher httpClientGoogleGeocode = new HttpClientGooglePlacesPeripher();
        httpClientGoogleGeocode.getNearByEarch(ll, radius, types, mHandlerGoogleMap);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.rl_title_right:
                if (peripherDetailList != null && peripherDetailList.size() > 0) {
                    Intent intent = new Intent(mContext, PeripherDetailGoogleTotalActivity.class);
                    intent.putExtra("peripherDetailList", (Serializable) peripherDetailList);
                    startActivity(intent);
                }
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Intent intent = new Intent(mContext, PeripherDetailGoogleActivity.class);
        intent.putExtra("lat", peripherDetailList.get(position - 1).latitude);
        intent.putExtra("lng", peripherDetailList.get(position - 1).longitude);
        intent.putExtra("address", peripherDetailList.get(position - 1).address);
        intent.putExtra("name", peripherDetailList.get(position - 1).name);
        startActivity(intent);
    }

    /**
     * 广告栏
     */
    private void initAdvertisement() {
        final List<Advertisement> advertisements = UserUtil.getAdvertisement(mContext, 7);
        headView = View.inflate(mContext, R.layout.layout_advertisement, null);

        vAdvertisement = (LinearLayout) headView.findViewById(R.id.view_advertisement);
        vAdvertisement.setVisibility(View.VISIBLE);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                LayoutParams.MATCH_PARENT, AppSP.getInstance()
                .getAdHeight(mContext));
        vAdvertisement.setLayoutParams(params);

        LinearLayout llDot = (LinearLayout) vAdvertisement
                .findViewById(R.id.ll_dot);
        imageViews = new ArrayList<ImageView>();
        dots = new ArrayList<View>();
        for (int i = 0; i < advertisements.size(); i++) {
            ImageView imageView = new ImageView(mContext);
            Glide.with(mContext).load(advertisements.get(i).image_url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageViews.add(imageView);

            imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                }
            });

            if (1 < advertisements.size()) {
                View view = LayoutInflater.from(mContext).inflate(
                        R.layout.layout_dot, null);
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

            scheduledExecutorService = Executors
                    .newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(scrollRunnable, 1, 2,
                    TimeUnit.SECONDS);
        }
    }

    /**
     * 广告滑动
     */
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


    /**
     * @Title: getCurrentGPS
     * @Description: 获取当前设备的gps地址
     */
    private void getCurrentGPS() {
        if (null == mCurTracker) {
            return;
        }
        LogUtil.i("sTrackerNo is:" + sTrackerNo);
        String url = UserUtil.getServerUrl(mContext);
        RequestParams params = HttpParams.currentGPS(sTrackerNo,
                Utils.getCurTime(mContext));
        HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(mContext);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;

                        if (obj.code == 0) {
                            CurrentGPS currentGPS = GsonParse.currentGPSParse(new String(response));
                            ProgressDialogUtil.show(mContext);
                            getAroundStore(currentGPS);
                        } else {
                            if (null != obj.what)
                                ToastUtil.show(mContext, obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(mContext, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }


    private Handler mHandlerGoogleMap = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HttpClientGooglePlacesPeripher.SUCCESS:
                    ProgressDialogUtil.dismiss();
                    List<PoiReInfo> poiList = (List<PoiReInfo>) msg.obj;
                    if (poiList == null) {
                        if (peripherDetailList.size() > 0) {
                            sortData(peripherDetailList);
                            adapter.setList(peripherDetailList);
                            adapter.notifyDataSetChanged();
                        } else ToastUtil.show(mContext, getString(R.string.no_result));
                        return;
                    }

                    for (int i = 0; i < poiList.size(); i++) {
                        PeripherDetail detail = new PeripherDetail();
                        PoiReInfo poiReInfo = poiList.get(i);
                        detail.address = poiReInfo.address;
                        detail.name = poiReInfo.name;
                        detail.latitude = poiReInfo.lat;
                        detail.longitude = poiReInfo.lon;
                        detail.distance = (int) Utils.getDistance(curPointLocation.longitude, curPointLocation.latitude, poiReInfo.lon, poiReInfo.lat);
                        geogleList.add(detail);
                    }
                    sortData(peripherDetailList);//排序
                    for (PeripherDetail info : geogleList) {//排除相同的位置
                        if (!peripherDetailList.contains(info))
                            peripherDetailList.add(info);
                    }

                    adapter.setList(peripherDetailList);
                    adapter.notifyDataSetChanged();
                    poiList.clear();

                    break;
                case HttpClientGooglePlacesPeripher.FAIL:
                    ProgressDialogUtil.dismiss();
                    if (peripherDetailList.size() > 0) {
                        sortData(peripherDetailList);
                        adapter.setList(peripherDetailList);
                        adapter.notifyDataSetChanged();
                    } else
                        ToastUtil.show(mContext, getString(R.string.no_result));
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 近到远排序升序排列
     */
    private void sortData(List<PeripherDetail> list) {
        if (list == null)
            return;
        Collections.sort(list, new Comparator<PeripherDetail>() {

            @Override
            public int compare(PeripherDetail lhs, PeripherDetail rhs) {
                if (lhs.distance > rhs.distance)
                    return 1;
                if (lhs.distance == rhs.distance)
                    return 0;
                return -1;
            }
        });
    }

}
