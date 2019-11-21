package com.bluebud.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.bluebud.activity.CommonPoiListActivity;
import com.bluebud.activity.PeripherDetailGooglePoiActivity;
import com.bluebud.adapter.PeripherAdapter;
import com.bluebud.adapter.ViewPagerAdapter;
import com.bluebud.app.App;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.info.Advertisement;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.UserUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 儿童：PT-720，HT-770，HT-771，HT-772，HT-770-1，HT-770HK   ranges=5
 * 儿童医院、学校、游乐场、公园
 * 宠物：PT-690  ranges = 2
 * 医疗、美容、寄养、摄影、殡葬
 * 摩托车：MPIP-620，MPIP-620-1    ranges = 4 ranges = 4
 * 修理厂、加油站、俱乐部
 * 私家车：IDD-213HT，213GD，213L   ranges = 3 ranges = 6 ranges = 3
 * 4S店、修理厂、停车场、加油站
 * 老人：PT-718，PT-719，HT-990，HT-770S  ranges = 1  ranges = 1 ranges = 5 ranges = 5
 * 医院、药店、银行、公园
 * 成人：HT-880，HT-891，HT-892  raneg = 7 range = 5 range 5
 * 医院、药店、银行、健身房
 */
public class TabPeripheryFragment extends Fragment implements OnItemClickListener {
    private String TAG = "TabPeripheryFragment";
    private View parentView;

    private ListView lvSetting;
    private List<ImageView> imageViews;
    private List<View> dots;
    private ViewPager viewPager;
    private ScheduledExecutorService scheduledExecutorService;
    private int currentItem = 0;
    //	private LatLng curPointLocation;// 定时定位
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

    private int[] image;
    private int[] image1 = new int[]{R.drawable.icon_yiyuan, R.drawable.icon_yaodian,
            R.drawable.icon_bank, R.drawable.icon_gongyuan};
    private int[] image2 = new int[]{R.drawable.icon_medical, R.drawable.icon_beauty,
            R.drawable.icon_foster_care, R.drawable.icon_photography, R.drawable.icon_funeral_and_interment};
    private int[] image3 = new int[]{R.drawable.icon_4sdiancar, R.drawable.icon_xiulichangcar,
            R.drawable.icon_tingchechangcar, R.drawable.icon_jiayouzhancar};
    private int[] image4 = new int[]{R.drawable.icon_xiulidian, R.drawable.icon_jiayouzhan, R.drawable.icon_julebu};
    private int[] image5 = new int[]{R.drawable.icon_ertongyiyuan, R.drawable.icon_xuexiao,
            R.drawable.icon_youlechang, R.drawable.icon_gongyuan};
    private int[] image7 = new int[]{R.drawable.icon_yiyuan, R.drawable.icon_yaodian,
            R.drawable.icon_bank, R.drawable.icon_jianshenfang};

    private int[] themes;
    private int[] themes1 = new int[]{R.string.hospital, R.string.pharmacy, R.string.bank, R.string.park};
    private int[] themes2 = new int[]{R.string.medical, R.string.beauty, R.string.foster_care, R.string.photography, R.string.funeral_and_interment};
    private int[] themes3 = new int[]{R.string.car_shop, R.string.repair_shop, R.string.car_park, R.string.refuel};
    private int[] themes4 = new int[]{R.string.repair_shop1, R.string.refuel, R.string.club};
    private int[] themes5 = new int[]{R.string.children_hospital, R.string.school, R.string.playground, R.string.park};
    private int[] themes7 = new int[]{R.string.hospital, R.string.pharmacy, R.string.bank, R.string.gym};
    private String[] chthemes;
    private String[] chthemes1 = new String[]{"医院", "药店", "银行", "公园"};
    private String[] chthemes2 = new String[]{"医疗", "美容", "寄养", "摄影", "殡葬"};
    private String[] chthemes3 = new String[]{"4S店", "修理厂", "停车场", "加油站"};
    private String[] chthemes4 = new String[]{"修理店", "加油站", "俱乐部"};
    private String[] chthemes5 = new String[]{"儿童医院", "学校", "游乐场", "公园"};
    private String[] chthemes7 = new String[]{"医院", "药店", "银行", "健身房"};
    /**
     * 1.儿童医院 2.学校 3.游乐场 4.公园 5.医疗 6.美容 7.寄养 8.摄影 9.殡葬 10.修理厂 11.加油站 12.俱乐部 13.4S店 14.停车场 15.医院 16.药店 17.银行  18.健身房
     */
    private int[] storeTypes;
    private int[] storeType1 = {15, 16, 17, 4};
    private int[] storeType2 = {5, 6, 7, 8, 9};
    private int[] storeType3 = {13, 10, 14, 11};
    private int[] storeType4 = {10, 11, 12};
    private int[] storeType5 = {1, 2, 3, 4};
    private int[] storeType7 = {15, 16, 17, 18};


    private Context mContext;
    private View headView;
    private LinearLayout vAdvertisement;

    private Tracker mCurTracker;
    private int ranges;

    private PeripherAdapter adapter;
    private String product_type = "";


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == parentView) {
            parentView = inflater.inflate(R.layout.activity_periper, container, false);
            initView();
        }

        ViewGroup parent = (ViewGroup) parentView.getParent();
        if (parent != null) {
            parent.removeView(parentView);
        }
        return parentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (null != mCurTracker) {
            mCurTracker = UserUtil.getCurrentTracker(mContext);
            product_type = mCurTracker.product_type;
            int iType = mCurTracker.ranges;
            int aroundRanges = mCurTracker.around_ranges;
            if (aroundRanges != 0) {
                ranges = aroundRanges;
            } else {
                ranges = iType;
            }
        }

        LogUtil.e("ranges==" + ranges + "==product_type=" + product_type);
        if (ranges == 1 || product_type.equals("15") || product_type.equals("26")) {//个人或HT-990，HT-770S
            themes = themes1;
            image = image1;
            chthemes = chthemes1;
            storeTypes = storeType1;
        } else if (ranges == 2) {//宠物
            themes = themes2;
            image = image2;
            chthemes = chthemes2;
            storeTypes = storeType2;
        } else if (ranges == 3 || ranges == 6) {//汽车或者OBD车辆
            themes = themes3;
            image = image3;
            chthemes = chthemes3;
            storeTypes = storeType3;
        } else if (ranges == 4) {//摩托车
            themes = themes4;
            image = image4;
            chthemes = chthemes4;
            storeTypes = storeType4;
        } else if (ranges == 5) {//手表但不包含HT-891手表
            if (mCurTracker.protocol_type == 8) {//litefamily显示
                LogUtil.e("周边litefamily=");
                themes = themes1;
                image = image1;
                chthemes = chthemes1;
                storeTypes = storeType1;
                return;
            }
          
            themes = themes5;
            image = image5;
            chthemes = chthemes5;
            storeTypes = storeType5;
        } else {//7 蓝牙手表
            themes = themes7;
            image = image7;
            chthemes = chthemes7;
            storeTypes = storeType7;
        }
        if (adapter != null) {
            adapter.setlist(image, themes);
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onPause() {
        LogUtil.i("TabPeripheryFragment onPause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        LogUtil.i("TabPeripheryFragment onDestroy");
        super.onDestroy();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mCurTracker = UserUtil.getCurrentTracker(mContext);
        if (null != mCurTracker) {
            product_type = mCurTracker.product_type;
            int iType = mCurTracker.ranges;
            int aroundRanges = mCurTracker.around_ranges;
            LogUtil.i("iType=" + iType + ",around_ranges=" + aroundRanges);
            if (aroundRanges != 0) {
                ranges = aroundRanges;
            } else {
                ranges = iType;
            }
        }
        lvSetting = (ListView) parentView.findViewById(R.id.lv_setting);
        initAdvertisement();
        if (headView != null) {
            lvSetting.addHeaderView(headView);
        }

        if (ranges == 1 || "15".equals(product_type) || "26".equals(product_type)) {//个人或HT-990，HT-770S
            themes = themes1;
            image = image1;
            chthemes = chthemes1;
        } else if (ranges == 2) {
            themes = themes2;
            image = image2;
            chthemes = chthemes2;
        } else if (ranges == 3 || ranges == 6) {
            themes = themes3;
            image = image3;
            chthemes = chthemes3;
        } else if (ranges == 4) {
            themes = themes4;
            image = image4;
            chthemes = chthemes4;
        } else if (ranges == 5) {//手表但不包含HT-891手表
            if (mCurTracker.protocol_type == 8) {
                LogUtil.e("litefamily");
                themes = themes1;
                image = image1;
                chthemes = chthemes1;
            } else {
                themes = themes5;
                image = image5;
                chthemes = chthemes5;
            }
        } else {
            themes = themes7;
            image = image7;
            chthemes = chthemes7;
        }
        adapter = new PeripherAdapter(mContext, image, themes);
        lvSetting.setAdapter(adapter);
        lvSetting.setOnItemClickListener(this);
    }


    /**
     * 广告位
     */
    private void initAdvertisement() {
        final List<Advertisement> advertisements = UserUtil.getAdvertisement(mContext, 7);
        if (advertisements == null || advertisements.size() == 0) {
            return;
        }
        headView = View.inflate(mContext, R.layout.layout_advertisement, null);

        vAdvertisement = (LinearLayout) headView.findViewById(R.id.view_advertisement);
        vAdvertisement.setVisibility(View.VISIBLE);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, AppSP.getInstance().getAdHeight(mContext));
        vAdvertisement.setLayoutParams(params);

        LinearLayout llDot = (LinearLayout) vAdvertisement
                .findViewById(R.id.ll_dot);
        imageViews = new ArrayList<ImageView>();
        dots = new ArrayList<View>();
        for (int i = 0; i < advertisements.size(); i++) {
            ImageView imageView = new ImageView(mContext);
            // imageView.setBackgroundResource(Constants.imageIds[i]);
            Glide.with(mContext).load(advertisements.get(i).image_url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageViews.add(imageView);

            imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    /**
                     * Intent it = new Intent(Intent.ACTION_VIEW, Uri
                     * .parse(advertisements.get(currentItem).ad_url));
                     * it.setClassName("com.android.browser",
                     * "com.android.browser.BrowserActivity");
                     * startActivity(it);
                     */
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

            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(scrollRunnable, 1, 2, TimeUnit.SECONDS);
        }
    }

    /**
     * 广告栏滑动监听
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
     * 条目点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        if (mCurTracker == null) {
            return;
        }
        position = headView != null ? position - 1 : position;

        if (headView != null && position == -1) {
            return;
        }
        if (App.getMapType() == App.MAP_TYPE_AMAP) {
            Intent intent = new Intent(mContext, CommonPoiListActivity.class);
            LogUtil.i(" themes[" + position + "]=" + chthemes[position]);
            intent.putExtra("themesName", chthemes[position]);
            intent.putExtra("themesName1", getResources().getString(themes[position]));
            intent.putExtra("storeType", storeTypes[position]);
            startActivity(intent);
        } else {//geogle搜索
            Intent intent = new Intent(mContext, PeripherDetailGooglePoiActivity.class);
            LogUtil.i(" themes[" + position + "]=" + getResources().getString(themes[position]));
            intent.putExtra("themesName", getResources().getString(themes[position]));
            intent.putExtra("position", position);
            intent.putExtra("storeType", storeTypes[position]);
            intent.putExtra("ranges", ranges);
            startActivity(intent);
        }
    }

}
