package com.bluebud.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluebud.app.AppManager;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.service.EventsService;
import com.bluebud.utils.Constants;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.UserUtil;
import com.permission.RequestPermission;
import com.permission.RequestPermissionCallback;

public class BindListActivity extends BaseActivity implements OnClickListener, AdapterView.OnItemClickListener,
        OnProgressDialogClickListener,RequestPermissionCallback {

    private TextView tvSkipStep;
    private ImageView ivBack;


    private String mEquipmentType;
    private String fromPage;
    private Tracker mCurTracker;
    //  private RelativeLayout rlBluetooth;
    private int[] images = {R.drawable.img_chongwu, R.drawable.img_ertongshoubiao,
            R.drawable.img_gerenshibei, R.drawable.img_car, R.drawable.img_moto,
            R.drawable.icon_old_people_watch};//, R.drawable.icon_lanyashoubiao,, R.drawable.icon_3gwatch
    private int[] names = {R.string.pet_gridview, R.string.whatch_gridview, R.string.sos_gridview,
            R.string.car_gridview, R.string.moto_gridview,//R.string.bluetooth_gridview
            R.string.old_people_gridview};//R.string.whatch_3G_gridview

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_bind_list1);
        init();
    }

    /**
     * @Description: 初始化，找到控件id并设置监听器
     */
    public void init() { // 去掉标题栏
        setBaseTitleGone();
        if (null != getIntent()) {
            fromPage = getIntent().getStringExtra("formpage");
        }
        tvSkipStep = (TextView) findViewById(R.id.tv_skip);
        tvSkipStep.setVisibility(View.GONE);
        ivBack = (ImageView) findViewById(R.id.iv_back1);
        TextView tvBuyNow = (TextView) findViewById(R.id.tv_buy_now);

        GridView gridview = (GridView) findViewById(R.id.bind_listview);
        BindListAdapter adapter = new BindListAdapter();
        gridview.setAdapter(adapter);
        findViewById(R.id.mall_image).setOnClickListener(this);
        ivBack.setOnClickListener(this);
        tvBuyNow.setOnClickListener(this);
        gridview.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i("onDestroy()");
    }

    private int position;
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        this.position = position;
        RequestPermission.create(this,this).checkSinglePermission(Manifest.permission.CAMERA,getString(R.string.permission_cameras));
    }

    @Override
    public void onPermissionSuccess() {
        switch (position) {
            case 0:// 宠物智能防丢器
                mEquipmentType = Constants.PET_EQUIPMENT;
                Intent intent = new Intent();
                intent.putExtra("equipment_type", mEquipmentType);
                intent.setClass(BindListActivity.this, BindActivity.class);
                startActivity(intent);
                break;
            case 1:// 儿童智能手表
                Intent intentWatch = new Intent(BindListActivity.this, WatchListActivity.class);
                startActivity(intentWatch);
                break;
            case 2:// 个人SOS智能定位器
                mEquipmentType = Constants.PERSON_EQUIPMENT;
                Intent intent2 = new Intent();
                intent2.putExtra("equipment_type", mEquipmentType);
                intent2.setClass(BindListActivity.this, BindActivity.class);
                startActivity(intent2);
                break;
            case 3:// 私家车定位追踪器
                mEquipmentType = Constants.CAR_EQUIPMENT;
                Intent intent3 = new Intent();
                intent3.putExtra("equipment_type", mEquipmentType);
                intent3.setClass(BindListActivity.this, BindActivity.class);
                startActivity(intent3);
                break;
            case 4:// 摩托车定位追踪器
                mEquipmentType = Constants.MOTO_EQUIPMENT;
                Intent intent4 = new Intent();
                intent4.putExtra("equipment_type", mEquipmentType);
                intent4.setClass(BindListActivity.this, BindActivity.class);
                startActivity(intent4);
                break;
            case 5:// 蓝牙手表
//                Intent intent5 = new Intent();
//                intent5.setClass(BindListActivity.this, DeviceScanActivity.class);
//                startActivity(intent5);
                mEquipmentType = Constants.OLD_PEOPLE_EQUIPMENT;
                Intent intent6 = new Intent();
                intent6.putExtra("equipment_type", mEquipmentType);
                intent6.setClass(BindListActivity.this, BindActivity.class);
                startActivity(intent6);
                break;
//            case 6:
//                mEquipmentType = Constants.Watcher_3G;
//                Intent intent7 = new Intent();
//                intent7.putExtra("equipment_type", mEquipmentType);
//                intent7.setClass(BindListActivity.this, BindActivity.class);
//                startActivity(intent7);
//                break;
//            case 7:
//                mEquipmentType = Constants.Watcher_3G;
//                Intent intent7 = new Intent();
//                intent7.putExtra("equipment_type", mEquipmentType);
//                intent7.setClass(BindListActivity.this, BindActivity.class);
//                startActivity(intent7);
//                break;
        }
    }


    //适配器
    class BindListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int i) {
            return names[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            viewHoldBind bind;
            if (view == null) {
                bind = new viewHoldBind();
                view = View.inflate(BindListActivity.this, R.layout.bindlist_item, null);
                bind.image = (ImageView) view.findViewById(R.id.bind_image_item);
                bind.textview = (TextView) view.findViewById(R.id.bind_text_item);
                view.setTag(bind);
            } else {
                bind = (viewHoldBind) view.getTag();
            }
            bind.image.setImageResource(images[i]);
            bind.textview.setText(names[i]);
            return view;
        }

        class viewHoldBind {
            ImageView image;
            TextView textview;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_skip:
                break;
            case R.id.tv_buy_now:
                Intent intent10 = new Intent(this, ShoppingActivity1.class);
                intent10.putExtra("type", Constants.PERSON_EQUIPMENT);
                startActivity(intent10);
                break;
            case R.id.mall_image:
                Intent mailIntent = new Intent(this, MallActivity.class);
                mailIntent.putExtra("isBind", true);
                startActivity(mailIntent);
                break;
            case R.id.iv_back1://返回
                LogUtil.v("finish");
                mCurTracker = UserUtil.getCurrentTracker(BindListActivity.this);
                if ((null != fromPage && fromPage.equals(Constants.REGISTRATION_COMPLETED)) || (null != fromPage && fromPage.equals(Constants.MAIN_PAGE1))) {//注册页面进入来时
                    if (null == mCurTracker) {
//					UserSP.getInstance().savePWD(BindActivity.this,
//					"");// 清空密码
                        BindListActivity.this.stopService(new Intent(
                                BindListActivity.this, EventsService.class));
//                        UserUtil.clearUserInfo(this);
//                        UserSP.getInstance().saveAutologin(this, false);
                        AppManager.getAppManager().finishAllActivity();
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        startActivity(new Intent(BindListActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    public void onProgressDialogBack() {
        LogUtil.i("onProgressDialogBack()");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mCurTracker = UserUtil.getCurrentTracker(BindListActivity.this);

        if ((null != fromPage && fromPage.equals(Constants.REGISTRATION_COMPLETED)) || (null != fromPage && fromPage.equals(Constants.MAIN_PAGE1))) {//注册页面进入来时
            if (null == mCurTracker) {
//				UserSP.getInstance().savePWD(BindActivity.this,
//				"");// 清空密码
                BindListActivity.this.stopService(new Intent(
                        BindListActivity.this, EventsService.class));
//                UserUtil.clearUserInfo(this);
//                UserSP.getInstance().saveAutologin(this, false);
                AppManager.getAppManager().finishAllActivity();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                startActivity(new Intent(BindListActivity.this, MainActivity.class));
                finish();
            }
        } else {
            finish();
        }

    }


//    private void openBluetoothDlaog() {
//        DialogUtil.show(this, R.string.pls_switch_bt_on1,
//                R.string.pls_switch_bt_on, R.string.confirm,
//                new OnClickListener() {
//
//                    @Override
//                    public void onClick(View arg0) {
//                        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
//                        DialogUtil.dismiss();
//                    }
//                }, R.string.cancel, new OnClickListener() {
//
//                    @Override
//                    public void onClick(View arg0) {
//                        DialogUtil.dismiss();
//                    }
//                });
//    }

}
