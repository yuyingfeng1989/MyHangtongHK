package com.bluebud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.bluebud.adapter.ViewPagerAdapter;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.liteguardian_hk.R;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity {
    private Button btnStart;
    private ViewPager viewPager;
    private List<ImageView> imageViews;
    private List<View> dots;
    //	private int[] imageIds = { R.drawable.guide_1, R.drawable.guide_2,
//			R.drawable.guide_3, R.drawable.guide_4 };
    private int[] imageIds = {R.drawable.guide_2, R.drawable.guide_1,
            R.drawable.guide_3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        init();
    }

    private void init() {
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AppSP.getInstance().saveFirstStart(GuideActivity.this, false);
                Intent intent = new Intent(GuideActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        imageViews = new ArrayList<ImageView>();

        for (int i = 0; i < imageIds.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(imageIds[i]);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageViews.add(imageView);
        }
        dots = new ArrayList<View>();
        dots.add(findViewById(R.id.v_dot0));
        dots.add(findViewById(R.id.v_dot1));
        dots.add(findViewById(R.id.v_dot2));
//		dots.add(findViewById(R.id.v_dot3));
        viewPager = (ViewPager) findViewById(R.id.vp);
        viewPager.setAdapter(new ViewPagerAdapter(imageViews));
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
    }

    private class MyPageChangeListener implements OnPageChangeListener {
        //	private int oldPosition = 0;

        @Override
        public void onPageSelected(int position) {
//			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal2);
//			dots.get(position).setBackgroundResource(R.drawable.dot_focused2);
//			oldPosition = position;	
            //add by zengms 2016-4-26
            if (position == 0) {
                for (int i = 0; i < dots.size(); i++) {
                    if (i == position) {
                        dots.get(i).setBackgroundResource(R.drawable.dot_focused2);
                    } else {
                        dots.get(i).setBackgroundResource(R.drawable.dot_normal2);
                    }
                }
            } else if (position == 1) {
                for (int i = 0; i < dots.size(); i++) {
                    if (i == position) {
                        dots.get(i).setBackgroundResource(R.drawable.dot_focused1);
                    } else {
                        dots.get(i).setBackgroundResource(R.drawable.dot_normal1);
                    }
                }
            } else if (position == 2) {
                for (int i = 0; i < dots.size(); i++) {
                    if (i == position) {
                        dots.get(i).setBackgroundResource(R.drawable.dot_focused3);
                    } else {
                        dots.get(i).setBackgroundResource(R.drawable.dot_normal3);
                    }
                }
            }

            if (position == imageIds.length - 1) {
                btnStart.setVisibility(View.VISIBLE);
            } else {
                btnStart.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
    }

}
