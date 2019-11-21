package com.bluebud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bluebud.app.App;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.info.GeofenceObj;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.bean.MyLatLng;
import com.bluebud.utils.Constants;
import com.bluebud.utils.FenceRequestUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import static android.view.View.OnClickListener;
import static android.view.View.inflate;

/**
 * Created by user on 2018/6/12.
 */

public class FenceSettingListActivity extends BaseActivity implements OnClickListener {

    //    private String[] names = {this.getString(R.string.family), this.getString(R.string.school)};
//    private Context mContext;
    private Tracker mCurrentTracker;
//    private LatLng curPointLocationGg;//google经纬度对象
//    private com.baidu.mapapi.model.LatLng curPointLocationBD;//百度经纬度对象
    private MyLatLng mLocation;
//    private boolean isBaidu;
    private List<GeofenceObj.DefenceList> defenceList;
    private FenceSettingListAdapter adapter;
    private ListView fence_listview;
    private int RequestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fencesettinglist_activity);
//        WeakReference<FenceSettingListActivity> weakReference = new WeakReference<FenceSettingListActivity>(this);
//        mContext = weakReference.get();
        mCurrentTracker = UserUtil.getCurrentTracker();
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        fence_listview = findViewById(R.id.fence_listview);
        findViewById(R.id.img3).setVisibility(View.GONE);
        TextView textTitle = findViewById(R.id.txt1);
        textTitle.setText(getString(R.string.geofence_setting));
        Intent intent = getIntent();
        if (intent != null) {
//            isBaidu = intent.getBooleanExtra("isBaidu", false);
//            LogUtil.e("isBaidu=" + isBaidu);
            mLocation = (MyLatLng) intent.getSerializableExtra(Constants.CURPOINTLOCATION);
//            if (App.getMapType() == App.MAP_TYPE_GMAP) {
//                curPointLocationGg = intent.getParcelableExtra(Constants.CURPOINTLOCATION);
//            } else {
//                mLocation = (MyLatLng) intent.getSerializableExtra(Constants.CURPOINTLOCATION);
//            }
//            if (isBaidu) {
//
//            } else {
//
//            }
        }
        itemOnclickEvent();
        getGEOfence();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            finish();
        }
    }

    /**
     * 条目点击事件
     */
    private void itemOnclickEvent() {
        findViewById(R.id.back).setOnClickListener(this);
        fence_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FenceSettingListActivity.this, MapFenceEditActivity.class);
                intent.putExtra(Constants.CURPOINTLOCATION, mLocation);
//                if (App.getMapType() != App.MAP_TYPE_GMAP) {
//                    intent = new Intent(FenceSettingListActivity.this, MapFenceEditActivity.class);
//                    intent.putExtra(Constants.CURPOINTLOCATION, mLocation);
//                } else {
//                    intent = new Intent(FenceSettingListActivity.this, FenceSettingOnGoogleMap.class);
//                    intent.putExtra(Constants.CURPOINTLOCATION, curPointLocationGg);
//                }
                intent.putExtra("DefenceList", defenceList.get(i));
                startActivityForResult(intent, RequestCode);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode)
            getGEOfence();
    }

    /**
     * 获取当前围栏信息接口
     */
    private void getGEOfence() {
        FenceRequestUtil request = new FenceRequestUtil(this);
        request.getGEOfence(mCurrentTracker.device_sn, new ChatCallbackResult() {
            @Override
            public void callBackResult(String result) {
                GeofenceObj mGeofenceObj = GsonParse.fenceSettingDataParse(result);
                if (mGeofenceObj == null || mGeofenceObj.defenceList == null)
                    return;
                defenceList = mGeofenceObj.defenceList;
                LogUtil.e("defenceList=" + defenceList.toString());
                if (adapter == null) {
                    adapter = new FenceSettingListAdapter(defenceList);
                    fence_listview.setAdapter(adapter);
                } else adapter.notifyDataSetChanged();
            }

            @Override
            public void callBackFailResult(String result) {
                ToastUtil.show(FenceSettingListActivity.this, result);
            }
        });
    }

    /**
     * 适配器内部类
     * //
     */
    private class FenceSettingListAdapter extends BaseAdapter {

        private List<GeofenceObj.DefenceList> mDefenceList;

        public FenceSettingListAdapter(List<GeofenceObj.DefenceList> defenceList) {
            mDefenceList = defenceList;
        }

        @Override
        public int getCount() {
            return mDefenceList == null ? 0 : mDefenceList.size();
        }

        @Override
        public Object getItem(int i) {
            if (mDefenceList == null || mDefenceList.size() <= i) {
                return null;
            }
            return mDefenceList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (mDefenceList == null) {
                return null;
            }
            View layout = inflate(viewGroup.getContext(), R.layout.text_image_item, null);
            TextView fence_name = layout.findViewById(R.id.fence_name);
            GeofenceObj.DefenceList defenceList = mDefenceList.get(i);
            if (!TextUtils.isEmpty(defenceList.defencename))
                fence_name.setText(defenceList.defencename);
            else {
                if (i == 0)
                    fence_name.setText(getString(R.string.family));
                else fence_name.setText(getString(R.string.school));
            }
            return layout;
        }
    }
}
