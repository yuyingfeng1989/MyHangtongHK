package com.bluebud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bluebud.adapter.PeripherDetailAdapter;
import com.bluebud.adapter.ViewPagerAdapter;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.info.Advertisement;
import com.bluebud.info.PeripherDetail;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.poi.MyPOIManager;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommonPoiListActivity extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, MyPOIManager.ResultCallback {
    private MyPOIManager mPoiManager;

    private List<View> dots;
    private int currentItem = 0;
    private List<PeripherDetail> peripherDetailList = new ArrayList<>();

    private View headView;
    private LinearLayout vAdvertisement;
    private String themesName;
    private String themesName1;

    private PeripherDetailAdapter adapter;
    private int storeType;//传递给平台需要那种类型数据


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_periper_detail);
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
            themesName1 = intent.getStringExtra("themesName1");
            storeType = intent.getIntExtra("storeType", 0);
            LogUtil.i("themesName" + themesName);
        }
        mPoiManager = MyPOIManager.getNewInstance();
        mPoiManager.setPoiSearchListener(this,this);
    }

    private void init() {
        initTheme();
        ListView lvSetting = findViewById(R.id.lv_setting);
        initAdvertisement();//广告栏
        if (headView != null) {
            lvSetting.addHeaderView(headView);
        }
        adapter = new PeripherDetailAdapter(this, peripherDetailList);
        lvSetting.setAdapter(adapter);
        lvSetting.setOnItemClickListener(this);
        initMapView();//获取周边商户信息
    }

    private void initTheme() {
        setBaseTitleColor(getResources().getColor(R.color.bg_theme));
        setBaseTitleText(themesName1);
        setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);
        getRight().setOnClickListener(this);
        getBaseTitleRightSetting().setVisibility(View.VISIBLE);
    }

    private void initMapView() {
        mPoiManager.getCurrentGPS(CommonPoiListActivity.this, themesName, storeType);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.rl_title_right:
                if (peripherDetailList != null && peripherDetailList.size() > 0) {
                    Intent intent = new Intent(this, PoiDetailActivity.class);
                    intent.putExtra(PoiDetailActivity.EXTRA_KEY_MY_LOCATION, mPoiManager.getCurrentLocation());
                    intent.putExtra(PoiDetailActivity.EXTRA_KEY_POI_LIST, (Serializable) peripherDetailList);
                    startActivity(intent);

                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        if (peripherDetailList == null || peripherDetailList.size() < position || position < 0) {
            return;
        }
        PeripherDetail detail = peripherDetailList.get(headView != null ? position - 1 : position);
        if (detail == null) {
            return;
        }

        Intent intent = new Intent(this, PoiDetailActivity.class);
        List<PeripherDetail> details = new ArrayList<>();
        details.add(detail);
        intent.putExtra(PoiDetailActivity.EXTRA_KEY_MY_LOCATION, mPoiManager.getCurrentLocation());
        intent.putExtra(PoiDetailActivity.EXTRA_KEY_POI_LIST, (Serializable) details);
        startActivity(intent);
    }

    /**
     * 移动广告栏
     */
    private void initAdvertisement() {
        final List<Advertisement> advertisements = UserUtil.getAdvertisement(this, 7);
        if (advertisements ==null || advertisements.size() == 0) {
            return;
        }

        headView = View.inflate(this, R.layout.layout_advertisement, null);

        vAdvertisement = headView.findViewById(R.id.view_advertisement);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, AppSP.getInstance()
                .getAdHeight(this));
        vAdvertisement.setLayoutParams(params);

        LinearLayout llDot = vAdvertisement.findViewById(R.id.ll_dot);
        List<ImageView> imageViews = new ArrayList<>();
        dots = new ArrayList<>();
        for (int i = 0; i < advertisements.size(); i++) {
            ImageView imageView = new ImageView(this);
            Glide.with(this)
                    .load(advertisements.get(i).image_url)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViews.add(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {

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
                View view = LayoutInflater.from(this).inflate(
                        R.layout.layout_dot, null);
                View vDot = view.findViewById(R.id.v_dot);
                if (i == 0) {
                    vDot.setBackgroundResource(R.drawable.dot_focused);
                }
                dots.add(vDot);
                llDot.addView(view);
            }
        }

        ViewPager viewPager = vAdvertisement.findViewById(R.id.vp);
        viewPager.setAdapter(new ViewPagerAdapter(imageViews));

        if (1 < advertisements.size()) {
            viewPager.setOnPageChangeListener(new MyPageChangeListener());
            viewPager.setCurrentItem(currentItem);

        }
    }


    /**
     * 广告栏
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
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
    public void handleResult(List<PeripherDetail> detailList) {
        ProgressDialogUtil.dismiss();
        if (detailList == null || detailList.size() == 0) {
            ToastUtil.show(this, getString(R.string.no_result));
        } else {
            peripherDetailList = detailList;
            adapter.setList(peripherDetailList);
            adapter.notifyDataSetChanged();
        }
    }

}
