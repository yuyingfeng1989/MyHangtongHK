package com.bluebud.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebud.activity.settings.AdvertisingWebActivity;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.Advertisement;
import com.bluebud.utils.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by user on 2018/4/4.
 */

public class AdvertisingPageActivity extends BaseActivity implements View.OnClickListener {

    private ImageView advertImage;
    private int count = 6;
    private TextView times;
//    private int mapIndex;
    private List<Advertisement> parseResult;
    private int size;//设备个数,添加注释
	private int git;//测试用
	private int xiugai;//修改s---------
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        WeakReference<AdvertisingPageActivity> reference = new WeakReference(this);
        mContext = reference.get();
        findViewById(R.id.text_appname).setVisibility(View.GONE);
        findViewById(R.id.tv_version).setVisibility(View.GONE);
        advertImage = (ImageView) findViewById(R.id.image_advertising);
        advertImage.clearAnimation();
        getAddvertisingPage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (times == null) {
            LinearLayout rl_back_time = (LinearLayout) findViewById(R.id.ll_back_times);
            times = (TextView) findViewById(R.id.times);
            rl_back_time.setVisibility(View.VISIBLE);
            rl_back_time.setOnClickListener(this);
            advertImage.setOnClickListener(this);
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    /**
     * 显示广告
     */
    private void getAddvertisingPage() {
        String result = getIntent().getStringExtra("adverts");
//        mapIndex = getIntent().getIntExtra("mapIndex", 0);//区分地图
        size = getIntent().getIntExtra("size", 0);//设备数量
        parseResult = (List<Advertisement>) ChatHttpParams.getParseResult(20, result);//广告页数据列表
        addAdvertisingPage(parseResult.get(parseResult.size() - 1).image_url);
    }

    /**
     * 计算界面的广告时间结束后进入主界面的方法
     *
     * @return
     */
    private int getCount() {
        count--;
        if (count == 0)
            intentClass();
        return count;
    }

    /**
     * 进行一个消息的处理
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0 && count != 0) {
                times.setText("（" + getCount() + "s）");
                handler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_advertising:
                String ad_url = parseResult.get(parseResult.size() - 1).ad_url;
                if (TextUtils.isEmpty(ad_url))
                    return;
                handler.removeMessages(0);//移除延时消息
                Intent intent = new Intent(mContext, AdvertisingWebActivity.class);
//                intent.putExtra("mapIndex", mapIndex);
                intent.putExtra("size", size);
                intent.putExtra("webUrl", ad_url);
                startActivity(intent);
                finish();
                break;
            case R.id.ll_back_times:
                handler.removeMessages(0);//移除延时消息
                count = 1;
                getCount();//执行当前消息
                break;
        }
    }

    /**
     * 跳转界面
     */
    private void intentClass() {
        Intent intent;
        if (size > 1) {//大于两个设备
            intent = new Intent(mContext, HomePageActivity.class);
//            if (mapIndex == 0)
//                intent = new Intent(mContext, HomePageActivity.class);
//            else
//                intent = new Intent(mContext, HomePageActivity.class);
        } else if (size < 1) {//没有设备
            intent = new Intent(mContext, BindListActivity.class);
            intent.putExtra("formpage", Constants.REGISTRATION_COMPLETED);
        } else {//只有一个设备
            intent = new Intent(mContext, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    /**
     * 加载缓存图片
     */
    private void addAdvertisingPage(String url) {
        Glide.with(mContext).load(url).placeholder(R.drawable.welcome_bg).error(R.drawable.welcome_bg).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(advertImage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        advertImage = null;
        times = null;
        parseResult.clear();
        parseResult = null;
        mContext = null;
    }
}
