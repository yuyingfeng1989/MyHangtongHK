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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.bluebud.activity.ShoppingActivity;
import com.bluebud.adapter.MallAdapter;
import com.bluebud.adapter.ViewPagerAdapter;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.Advertisement;
import com.bluebud.info.GoodsInfo;
import com.bluebud.info.ReBaseObj;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.UserUtil;
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

public class TabShoppingFragment extends Fragment implements OnItemClickListener {
    private String TAG = "TabShoppingFragment";
    private View parentView;
    private List<GoodsInfo> goodsList = null;
    private List<ImageView> imageViews;
    private List<View> dots;
    private ViewPager viewPager;
    private ScheduledExecutorService scheduledExecutorService;
    private int currentItem = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            viewPager.setCurrentItem(currentItem);
        }
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

    private RequestHandle requestHandle;

    private Context mContext;

    private View vAdvertisement;


    private GridView gv;
    private MallAdapter adapter;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == parentView) {
            parentView = inflater.inflate(R.layout.activity_mall_first, container, false);
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        initAdvertisement();
        gv = (GridView) parentView.findViewById(R.id.gv);
        adapter = new MallAdapter(mContext, goodsList);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(this);
        getGoodsInformation();
    }


    private void initAdvertisement() {
        final List<Advertisement> advertisements = UserUtil.getAdvertisement(
                mContext, 8);

        if (advertisements == null || advertisements.size() == 0) {
            return;
        }

        vAdvertisement = parentView.findViewById(R.id.view_advertisement);
        vAdvertisement.setVisibility(View.VISIBLE);
        LayoutParams params = new LayoutParams( LayoutParams.MATCH_PARENT, AppSP.getInstance().getAdHeight(mContext));
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

            scheduledExecutorService = Executors
                    .newSingleThreadScheduledExecutor();
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

    private void getGoodsInformation() {
        String url = UserUtil.getServerUrl(mContext);
        RequestParams params = HttpParams.getGoodsInformation();
        requestHandle = HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {


                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        LogUtil.i("system notice msg begin ");
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            goodsList = GsonParse.GoodsInformationParse(new String(response));
                            if (goodsList != null) {
                                LogUtil.i("goodsList size is:" + goodsList.size());
                                adapter.setList(goodsList);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
        GoodsInfo goodsInfo = goodsList.get(position);
        if (goodsInfo.type == 2) {
            showDialogInsurance(position);
            return;
        }
        Intent intent = new Intent(mContext, ShoppingActivity.class);
        intent.putExtra("url", goodsList.get(position).goods_url);
        startActivity(intent);
    }

    /**
     * 弹出免责声明对话框
     */
    private void showDialogInsurance(final int position) {
        DialogUtil.showInsuranceAlert(mContext, R.string.disclaimer_title, R.string.disclaimer_content,
                R.string.disclaimer_read, new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ShoppingActivity.class);
                        intent.putExtra("url", goodsList.get(position).goods_url);
                        startActivity(intent);
                        DialogUtil.dismiss();
                    }
                });
    }
}
