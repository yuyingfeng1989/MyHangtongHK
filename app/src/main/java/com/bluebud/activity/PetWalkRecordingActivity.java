package com.bluebud.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.bluebud.adapter.PetWalkRecordingAdapter;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.DogTrailMap;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.info.petWalkRecordingInfo;
import com.bluebud.info.walkDogDataStatisticsMapInfo;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//遛狗记录页面
public class PetWalkRecordingActivity extends BaseActivity implements OnClickListener,
        OnItemClickListener {
    private ListView lvSetting;
    private RequestHandle requestHandle;
    private Context mContext;


    private Tracker mCurTracker;
    private String sTrackerNo;
    private TextView tvKilometre;
    private TextView tvTime;
    private TextView tvNumber;
    private TextView tvCalories;
    private TextView tvCurrentMouth;
    private PetWalkRecordingAdapter adapter;
    private List<DogTrailMap> dogTrailMap;
    private walkDogDataStatisticsMapInfo walkDogDataStatisticsMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_pet_walk_recording);
        mContext = this;
        init();
        getwalkDogTrail();
    }

    public void onProgressDialogBack() {
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }

    private void init() {
        super.setBaseTitleColor(getResources().getColor(R.color.bg_theme));
        super.setBaseTitleText(R.string.recording);
        super.setBaseTitleVisible(View.VISIBLE);
        super.getBaseTitleLeftBack().setOnClickListener(this);
        mCurTracker = UserUtil.getCurrentTracker(this);
        if (mCurTracker != null) {
            sTrackerNo = mCurTracker.device_sn;
        }

        tvKilometre = findViewById(R.id.tv_kilometre);//里程
        tvTime = findViewById(R.id.tv_time);//时间
        tvNumber = findViewById(R.id.tv_number);//次数
        tvCalories = findViewById(R.id.tv_calories);//卡路里
        tvCurrentMouth = findViewById(R.id.tv_current_mouth);//当月时间

        lvSetting = findViewById(R.id.lv_setting);
        adapter = new PetWalkRecordingAdapter(mContext, dogTrailMap);
        lvSetting.setAdapter(adapter);
        lvSetting.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rl_title_back) {
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        LogUtil.i("position=" + position);

        Intent intent = new Intent(mContext, MyPetWalkDetailActivity.class);
        DogTrailMap trailMap = dogTrailMap.get(position);
        intent.putExtra("trailMap", trailMap);
        startActivity(intent);
    }

    private void getwalkDogTrail() {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.getWalkDogTrail(sTrackerNo, Utils.getCurTime(mContext));
        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            petWalkRecordingInfo recordParse = GsonParse.getPetWalkRecordParse(new String(response));
                            if (recordParse == null) {
                                ToastUtil.show(mContext, obj.what);
                                return;
                            }
                            setData(recordParse);
                        } else {
                            ToastUtil.show(mContext, obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });

    }

    private void setData(petWalkRecordingInfo recordParse) {
        dogTrailMap = recordParse.dogTrailMap;
        walkDogDataStatisticsMap = recordParse.walkDogDataStatisticsMap;
        Collections.sort(dogTrailMap, new Comparator<DogTrailMap>() {

            @Override
            public int compare(DogTrailMap lhs, DogTrailMap rhs) {
                long longstr1 = Long.valueOf(lhs.start_time.replaceAll("[-\\s:]", ""));
                long longstr2 = Long.valueOf(rhs.start_time.replaceAll("[-\\s:]", ""));
                LogUtil.i("longstr1:" + longstr1 + ",longstr2:" + longstr2);
                if (longstr1 > longstr2) {
                    return -1;
                }
                if (longstr1 == longstr2) {
                    return 0;
                }
                return 1;

            }
        });
        adapter.setList(dogTrailMap);
        adapter.notifyDataSetChanged();
        if (walkDogDataStatisticsMap != null) {
            if (walkDogDataStatisticsMap.mileage != null) {
                tvKilometre.setText(walkDogDataStatisticsMap.mileage);//里程
            } else {
                tvKilometre.setText("--");
            }
            if (walkDogDataStatisticsMap.spendtime != null) {
                tvTime.setText(walkDogDataStatisticsMap.spendtime);//时间
            } else {
                tvTime.setText("--");
            }
            if (walkDogDataStatisticsMap.times != null) {
                tvNumber.setText(walkDogDataStatisticsMap.times);//时间
            } else {
                tvNumber.setText("--");
            }
            if (walkDogDataStatisticsMap.calorie != null) {
                tvCalories.setText(walkDogDataStatisticsMap.calorie);//时间
            } else {
                tvCalories.setText("--");
            }
            LogUtil.i("time=" + Utils.getCurTime(mContext).substring(0, 7));
            tvCurrentMouth.setText(Utils.getCurTime(mContext).substring(0, 7));
        }
    }
}
