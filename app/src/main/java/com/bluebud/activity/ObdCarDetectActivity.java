package com.bluebud.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebud.adapter.NormalExpandableListAdapter;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.CarDetectInfo;
import com.bluebud.info.CarInspectionData;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.DeviceExpiredUtil;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.view.ProgressRing;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.lang.ref.WeakReference;
import java.util.List;


public class ObdCarDetectActivity extends BaseActivity implements OnClickListener {
    private String sTrackerName;
    private ExpandableListView mExpandableListView;


    public static final int[] BOOKS = {R.string.air_oil_system, R.string.ignition_system, R.string.speed_control_system,
            R.string.ecu_control_system, R.string.emission_control_system, R.string.fault_code_detection};
    public static final int[] images = {R.drawable.icon_jqry, R.drawable.icon_dh, R.drawable.icon_csds,
            R.drawable.icon_dnkz, R.drawable.icon_fq, R.drawable.icon_gzm};
    private String[][] FIGURES;
    private ProgressRing mPvScore;
    private View mIvCarCanning;
    private ImageView mIvCarCanningLine;
    private LinearLayout mLiCarCanning;
    private TextView mTvCanningSpecies;
    private TextView mTvCanningPercentage;
    private LinearLayout mLlFinalScore;
    private TextView mTvSuggest;
    private TextView mTvDetectionTime;
    private Button mBtnCarDetect;
    private Handler timingHandler = new Handler();
    private Handler timingHandler1 = new Handler();
    private final static int TIMING = 5 * 1000;
    private final static int TIMING1 = 40;
    private int currentIndex = 0;
    private int currentPercentage = 0;
    private RequestHandle requestPost;
    private Runnable timingRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentIndex < 5) {
                adapter.updateIndexIcon(currentIndex);
                currentIndex++;
                currentPercentage = 0;

                mTvCanningPercentage.setText(currentPercentage + "%");
                mTvCanningSpecies.setText(getString(BOOKS[currentIndex]));
                timingHandler.postDelayed(timingRunnable, TIMING);//开始轮询数据
            } else {
                timingHandler.removeCallbacks(timingRunnable);
                timingHandler1.removeCallbacks(timingRunnable1);
                adapter.updateIndexIcon(5);
                mTvCanningPercentage.setText("100%");
                currentIndex = 0;
                endCarAnimation();
                isDetectioning = false;
                checkEnd();//显示检测分数
            }
        }
    };
    private Runnable timingRunnable1 = new Runnable() {
        @Override
        public void run() {
            if (currentPercentage < 100) {
                currentPercentage++;
                mTvCanningPercentage.setText(currentPercentage + "%");
                timingHandler1.postDelayed(timingRunnable1, TIMING1);//开始轮询数据
            } else {
                currentPercentage = 0;
                timingHandler1.postDelayed(timingRunnable1, TIMING1);//开始轮询数据
            }
        }
    };

    private TranslateAnimation horizontalAnimation;
    private Context mContext;
    private String sTrackerNo;
    private List<CarInspectionData> mCarInspectionData;
    private List<String> mFaultMsgList;
    private NormalExpandableListAdapter adapter;
    private int carStatus;
    private Tracker mCurTracker;
    private boolean isDetectioning = false;
    private int onlinestatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_obd_car_detect);
        WeakReference<ObdCarDetectActivity> wr = new WeakReference(this);
        mContext = wr.get();
        carStatus = getIntent().getIntExtra(Constants.VEHICLE_STATUS, 0);
        onlinestatus = getIntent().getIntExtra(Constants.ONLINESTATUS, 0);
        init();
        getCarDetectDate();//得到历史数据
    }

    /**
     * 初始化控件及数据
     */
    private void init() {
        mCurTracker = UserUtil.getCurrentTracker(mContext);
        if (mCurTracker != null) {
            sTrackerName = mCurTracker.nickname;
            sTrackerNo = mCurTracker.device_sn;
        }
        super.setBaseTitleText(sTrackerName);//设置标题
        super.setBaseTitleVisible(View.VISIBLE);
        super.getBaseTitleLeftBack().setOnClickListener(this);
        findViewById(R.id.rl_title).setBackgroundColor(getResources().getColor(R.color.obd_1b1b1b));

        mIvCarCanning = findViewById(R.id.iv_car_canning);//汽车图片
        mIvCarCanningLine = (ImageView) findViewById(R.id.iv_car_canning_line);//扫描图片
        //整个扫描布局
        mLiCarCanning = (LinearLayout) findViewById(R.id.ll_car_canning);
        //正在扫描种类
        mTvCanningSpecies = (TextView) findViewById(R.id.tv_canning_species);
        //正在扫描百分比
        mTvCanningPercentage = (TextView) findViewById(R.id.tv_canning_percentage);
        //最终得分布局
        mLlFinalScore = (LinearLayout) findViewById(R.id.ll_final_score);
        //建议
        mTvSuggest = (TextView) findViewById(R.id.tv_suggest);
        //检测时间
        mTvDetectionTime = (TextView) findViewById(R.id.tv_detection_time);
        //开始检测
        mBtnCarDetect = (Button) findViewById(R.id.btn_car_detect);
        mBtnCarDetect.setOnClickListener(this);
        mPvScore = (ProgressRing) findViewById(R.id.pv_score);//圆形进度条
        mExpandableListView = (ExpandableListView) findViewById(R.id.expandable_list);
        FIGURES = new String[6][];
        for (int i = 0; i < 6; i++) {
            FIGURES[i] = new String[0];
        }
        adapter = new NormalExpandableListAdapter(BOOKS, images, FIGURES, -1);
        mExpandableListView.setAdapter(adapter);
        //  设置分组项的点击监听事件
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                LogUtil.i("onGroupClick: groupPosition:" + groupPosition + ", id:" + id);
                // 请务必返回 false，否则分组不会展开
                if (isDetectioning) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        //  设置子选项点击监听事件
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //Toast.makeText(NormalExpandActivity.this, Constant.FIGURES[groupPosition][childPosition], Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        checkEnd();//开始默认是显示上次的数据,所以是显示检测完状态
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mCurTracker = UserUtil.getCurrentTracker(mContext);
    }

    /**
     * 开始动画
     */
    private void startCarAnimation() {
        mLlFinalScore.setVisibility(View.GONE);
        mPvScore.setVisibility(View.GONE);
        mLiCarCanning.setVisibility(View.VISIBLE);
        isDetectioning = true;
        for (int i = 0; i < 6; i++) {
            FIGURES[i] = new String[0];
        }
        adapter.addChildData(FIGURES, false, true);
        LogUtil.i("开始动画");
        mLiCarCanning.post(new Runnable() {//这里是为了拿到View的宽高
            @Override
            public void run() {
//                left = mIvCarCanning.getLeft() - 20;
//                right = mIvCarCanning.getRight();
                horizontalAnimation = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, -0.15f,
                        //X轴移动的结束位置
                        Animation.RELATIVE_TO_SELF, 2.3f,
                        //y轴开始位置
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        //y轴移动后的结束位置
                        Animation.RELATIVE_TO_SELF, 0.0f
                );
                horizontalAnimation.setDuration(3000); // 动画持续时间
                horizontalAnimation.setRepeatCount(Animation.INFINITE); // 无限循环
                //horizontalAnimation.setRepeatCount(9); // 无限循环次数
                horizontalAnimation.setRepeatMode(Animation.REVERSE);// 设置反方向执行
                mIvCarCanningLine.startAnimation(horizontalAnimation);
                horizontalAnimation.startNow();
                mTvCanningSpecies.setText(getString(BOOKS[0]));
                adapter.updateIndexIcon(-1);//更新下已请求数据图标
                timingHandler.postDelayed(timingRunnable, TIMING);//开始轮询数据
                timingHandler1.postDelayed(timingRunnable1, TIMING1);//开始轮询数据
            }
        });
    }

    /**
     * 结束动画
     */
    private void endCarAnimation() {
        if (horizontalAnimation != null) {
            horizontalAnimation.cancel();
        }
    }

    /**
     * 检测完
     */
    private void checkEnd() {
        mLiCarCanning.setVisibility(View.GONE);
        mLlFinalScore.setVisibility(View.VISIBLE);
        mPvScore.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.btn_car_detect://开始检测
                if (onlinestatus != 1) {
                    ToastUtil.show(mContext, R.string.onlinestatus);
                    return;
                }
                if (Utils.isSuperUser(mCurTracker, mContext)) {
                    if (DeviceExpiredUtil.advancedFeatures(mContext, mCurTracker, true))//判断是否需要付费
                        return;
                    DialogUtil.show(mContext, R.string.confirm,
                            R.string.detection_hint, R.string.confirm,
                            new OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    toCarDetect();
                                    DialogUtil.dismiss();
                                }
                            }, R.string.cancel, new OnClickListener() {

                                @Override
                                public void onClick(View arg0) {
                                    DialogUtil.dismiss();
                                }
                            });
                }
                break;
        }
    }

    /**
     * 开始检测
     */
    private void toCarDetect() {
        //OBD显示汽车是否有警情标示0：熄火，1：运行，2:怠速
        if (carStatus == 2) {
            startCarAnimation();
            getCarDetectDateFromServer(1);
        } else if (carStatus == 0) {
            ToastUtil.show(mContext, R.string.detection_turn_off);
        } else {
            ToastUtil.show(mContext, R.string.detection_run);
        }
    }

    /**
     * 初始化数据显示
     */
    private void getCarDetectDate() {
        String respose = UserSP.getInstance().getCarScoreAndTime(mContext, sTrackerNo);
        if (!TextUtils.isEmpty(respose)) {
            String[] split = respose.split(",");
            if (split.length > 1) {
                showCheckState(Integer.valueOf(split[0]), split[1]);
            } else {
                showCheckState(Integer.valueOf(split[0]), null);
            }
        } else {
            getCarDetectDateFromServer(0);
        }
    }


    /**
     * 获取车辆检测数据
     */
    private void getCarDetectDateFromServer(final int flag) {
        requestCancel();//前一次请求未结束取消前一次请求
        String url = UserUtil.getServerUrl(mContext);
        RequestParams params = HttpParams.startCarInspection(sTrackerNo, Utils.getCurTime(mContext), flag);
        requestPost = HttpClientUsage.getInstance().post(mContext, url,
                params, new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null) {
                            return;
                        }
                        if (obj.code == 0) {
                            LogUtil.v("begin parse real json date");
                            CarDetectInfo carDetectInfo = GsonParse.carDetectInfoParse(new String(response));
                            if (carDetectInfo == null)
                                return;
                            //保存数据
                            if (flag == 0) {//查看检测数据
                                String value = carDetectInfo.historyScore + "," + carDetectInfo.historyTestTime;
                                showCheckState(carDetectInfo.historyScore, carDetectInfo.historyTestTime);
                                UserSP.getInstance().saveCarScoreAndTimer(mContext, sTrackerNo, value);
                            } else {//开始获取检测数据
                                mCarInspectionData = carDetectInfo.carInspectionData;
                                mFaultMsgList = carDetectInfo.faultMsgList;
                                showDate(true);
                            }

                        } else {
                            ToastUtil.show(mContext, obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(mContext, R.string.net_exception);
                    }
                });
    }


    /**
     * @Title: showDate
     * @Description: 将从服务器上获取的数据展现在页面上
     */
    private void showDate(boolean isFromService) {
        if (mCarInspectionData == null)
            return;

        if (mFaultMsgList != null && mFaultMsgList.size() > 0) {//有错误码
            for (int i = 0; i < 6; i++) {
                if (i == 5) {
                    FIGURES[i] = new String[mFaultMsgList.size()];
                    for (int j = 0; j < mFaultMsgList.size(); j++) {
                        FIGURES[5][j] = mFaultMsgList.get(j);
                    }
                } else {
                    FIGURES[i] = new String[1];
                }
            }
            adapter.addChildData(FIGURES, true, isFromService);
        } else {//无错误码
            adapter.addChildData(FIGURES, false, isFromService);
        }
        int sumpoint = 0;
        for (CarInspectionData c : mCarInspectionData) {
            sumpoint += c.score;
        }
        sumpoint = sumpoint / mCarInspectionData.size();
        String checkTimer = mCarInspectionData.get(0).datetime;
        String value = sumpoint + "," + checkTimer;
        UserSP.getInstance().saveCarScoreAndTimer(mContext, sTrackerNo, value);
        showCheckState(sumpoint, checkTimer);
    }


    /**
     * 抽取显示时间和分数方法
     */
    private void showCheckState(int sumpoint, String timer) {
        mPvScore.setProgress(sumpoint);
        mTvSuggest.setVisibility(View.VISIBLE);
        if (sumpoint > 80)
            mTvSuggest.setText(getResources().getString(R.string.car_test_a_level));
        else if (sumpoint > 60 && sumpoint <= 80)
            mTvSuggest.setText(getResources().getString(R.string.car_test_b_level));
        else if (sumpoint > 0 && sumpoint <= 60) {
            mTvSuggest.setText(getResources().getString(R.string.car_test_c_level));
        } else {
            mTvSuggest.setVisibility(View.INVISIBLE);
        }
        if (TextUtils.isEmpty(timer)) {
            mTvDetectionTime.setText(getResources().getString(R.string.detect_time));
        } else {
            mTvDetectionTime.setText(getResources().getString(R.string.detect_time) + timer);
        }
    }

    /**
     * 取消请求
     */
    private void requestCancel() {
        if (requestPost != null && !requestPost.isFinished())//如果请求未结束，重复请求取消上一次请求
            requestPost.cancel(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endCarAnimation();
        requestCancel();
    }
}
