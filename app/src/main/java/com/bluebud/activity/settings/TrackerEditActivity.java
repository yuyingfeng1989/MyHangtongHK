package com.bluebud.activity.settings;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.baidu.mapapi.model.LatLng;
//import com.baidu.mapapi.search.core.SearchResult;
//import com.baidu.mapapi.search.geocode.GeoCodeResult;
//import com.baidu.mapapi.search.geocode.GeoCoder;
//import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
//import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.bluebud.activity.BaseActivity;
import com.bluebud.activity.InsuranceInformationActivity;
import com.bluebud.activity.MainActivity;
import com.bluebud.activity.PetInsurActivity;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.LostCard2Car;
import com.bluebud.info.LostCard2People;
import com.bluebud.info.LostCard2Pet;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.DialogUtil.OnEditTextEditListener;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.PopupWindowUtils;
import com.bluebud.utils.PopupWindowYearMonthDayUtils;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.SettingPhotoUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.permission.RequestPermission;
import com.permission.RequestPermissionCallback;

import org.apache.http.Header;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Calendar;

//信息卡
public class TrackerEditActivity extends BaseActivity implements
        OnClickListener, OnProgressDialogClickListener, PopupWindowYearMonthDayUtils.OnWheeClicked, OnEditTextEditListener, RequestPermissionCallback {

    private RelativeLayout rlChangePortrait;
    private CircleImageView ivHead;

    private View viewPeople;
    private TextView etName;
    private TextView etMark;
    private TextView etPhone1;

    private TextView etHeight;
    private TextView etWeight;
    private TextView etWeightPet;
    private TextView etPetType;
    private TextView etStepLength;
    private TextView etCarType;

//    private TextView etLostAddress;
    private LinearLayout llPeople;
    private LinearLayout llPeoplePhone;
    private LinearLayout llPet;
    private TextView tvSex;

    private View viewCar;

    private static final int PHOTO_GRAPH = 0;
    private static final int PICK = 111;
    private static final int ZOOM = 121;
    private static final int NICKNAMECODE = 3;
    private static final int HEIGHTCODE = 4;
    private static final int WEIGHTCODE = 5;
    private static final int LEIGHTCODE = 6;
    private static final int PETTYPECODE = 7;
    private static final int WEIGHTPETCODE = 8;
    private static final int TRACKERMARKCODE = 9;
    private static final int PHONECODE = 10;
    private static final int CARPLATENUMBERCODE = 11;
    private static final int CARTYPECODE = 12;
    private static final int CARPHONECODE = 13;
    private static final int CARVINCODE = 14;
    private static final int SIMCODE = 15;

    private LostCard2People lostCard2People;
    private LostCard2Pet lostCard2Pet;
    private LostCard2Car lostCard2Car;

    private String strTrackerNo = "";
    private int strTrackerType = 1;
    private Tracker mTracker;

    private String trackerName;
    private String mark;
    private String sDay;
    private String sex;
//    private GeoCoder mSearch;

    private TextView etBirthday;
    private TextView etCarNumber;
    private TextView etCarTime;
    private TextView etCarPhone;
    private TextView et_car_vin;//车架号
    private TextView tv_sim_code;//sim卡显示

    private RequestHandle requestHandle;
    private PopupWindowUtils popupWindowUtils;
    private PopupWindowYearMonthDayUtils popupWindowYearMonthDayUtils;
    private String fromWhere = Constants.MAIN_PAGE;
    private TextView tvActiveState;
    private LinearLayout llPetLnsurance;
    private int insurCode = 0;
    private Context mContext;
    private SettingPhotoUtil photoUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_tracker_edit);
        WeakReference<TrackerEditActivity> weakReference = new WeakReference<>(this);
        mContext = weakReference.get();
        popupWindowYearMonthDayUtils = new PopupWindowYearMonthDayUtils(mContext, this);
        init();
//        initBaiduMap();
        getLostCard();
        popupWindowUtils = new PopupWindowUtils(mContext);
        photoUtil = new SettingPhotoUtil(mContext, mTracker, popupWindowUtils);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getLostCard();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fromWhere.equals(Constants.BINDACTIVITY)) {//从绑定设备页面进来的就返回到主页
            startActivity(new Intent(mContext, MainActivity.class));
            LogUtil.i("formwhere bindactivity");
            sendBroadcast(new Intent(Constants.ACTION_TRACTER_ENTER_MAIN));
            finish();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
//        if (null != mSearch) {
//            mSearch.destroy();
//        }
        super.onDestroy();
        File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
        if (f.exists()) {
            f.delete();
        }
    }

    /**
     * 地图最后的定位点
     */
//    private void initBaiduMap() {
//        mSearch = GeoCoder.newInstance();
//        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
//            @Override
//            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
//                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//                    return;
//                }
//                LogUtil.i(result.getAddress());
//                etLostAddress.setText(result.getAddress());// 最后定位点
//            }
//
//            @Override
//            public void onGetGeoCodeResult(GeoCodeResult arg0) {
//            }
//        });
//    }

    /**
     * 初始化控件
     */
    public void init() {
        setBaseTitleText(R.string.tracker_info);
        setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            mTracker = (Tracker) getIntent().getSerializableExtra(Constants.EXTRA_TRACKER);
            strTrackerNo = mTracker.device_sn;// 设备ID
            strTrackerType = mTracker.ranges;// 设备类型
            fromWhere = getIntent().getStringExtra("fromwhere");
        }
        rlChangePortrait = (RelativeLayout) findViewById(R.id.rl_change_portrait);// 头像整个布局
        ivHead = (CircleImageView) findViewById(R.id.iv_tracker_headicon);// 头像

        viewPeople = findViewById(R.id.ll_people);// 人，宠物整个布局
        viewCar = findViewById(R.id.ll_car);// 汽车，摩托车整个布局

        LinearLayout ll_sim = (LinearLayout) findViewById(R.id.ll_sim);//sim卡设置
        tv_sim_code = (TextView) findViewById(R.id.tv_sim_code);//sim卡显示
        rlChangePortrait.setOnClickListener(this);
        ll_sim.setOnClickListener(this);
        // 设置头像的默认的图片
        // 使用范围 1.个人，2.宠物，3.汽车，4.摩托车,5.手表，6.OBD汽车
        if (7 == strTrackerType) {//880蓝牙手表没有sim卡
            ll_sim.setVisibility(View.GONE);
        }
        if (4 == strTrackerType) {
            ivHead.setImageResource(R.drawable.image_motorcycle);
        } else if (3 == strTrackerType || 6 == strTrackerType) {
            ivHead.setImageResource(R.drawable.image_car);
        } else if (2 == strTrackerType) {
            ivHead.setImageResource(R.drawable.image_pet);
        } else if (5 == strTrackerType) {
            ivHead.setImageResource(R.drawable.image_watch);

        } else {
            ivHead.setImageResource(R.drawable.image_preson_sos);
        }

        if (7 == strTrackerType || 1 == strTrackerType || 2 == strTrackerType || 5 == strTrackerType) {
            viewPeople.setVisibility(View.VISIBLE);
            viewCar.setVisibility(View.GONE);
            initPeopleView();
            setPeopleData();
        } else if (3 == strTrackerType || 4 == strTrackerType
                || 6 == strTrackerType) {
            viewPeople.setVisibility(View.GONE);
            viewCar.setVisibility(View.VISIBLE);
            initCarView();
            setCarData();
        }
    }

    /**
     * 初始化人的设备控件
     */
    private void initPeopleView() {
        ((LinearLayout) viewPeople.findViewById(R.id.ll_tracker_name)).setOnClickListener(this);
        etName = (TextView) viewPeople.findViewById(R.id.et_tracker_name);// 昵称
        etName.setText(mTracker.nickname);
        ((LinearLayout) viewPeople.findViewById(R.id.ll_sex)).setOnClickListener(this);
        tvSex = (TextView) viewPeople.findViewById(R.id.tv_sex);// 性别
//        if (strTrackerType == 2) {// 默认性别显示
//            tvSex.setText(getString(R.string.female));
//        } else {
//            tvSex.setText(getString(R.string.woman));
//        }

        ((LinearLayout) viewPeople.findViewById(R.id.ll_birthday)).setOnClickListener(this);
        etBirthday = (TextView) viewPeople.findViewById(R.id.et_birthday);// 生日
        llPeople = (LinearLayout) viewPeople.findViewById(R.id.ll_people_info);
        llPet = (LinearLayout) viewPeople.findViewById(R.id.ll_pet);

        if (7 == strTrackerType || 1 == strTrackerType || 5 == strTrackerType) {
            llPeople.setVisibility(View.VISIBLE);
            llPet.setVisibility(View.GONE);
            ((LinearLayout) viewPeople.findViewById(R.id.ll_height))
                    .setOnClickListener(this);//
            etHeight = (TextView) viewPeople.findViewById(R.id.et_height);// 身高
            ((LinearLayout) viewPeople.findViewById(R.id.ll_weight))
                    .setOnClickListener(this);//
            etWeight = (TextView) viewPeople.findViewById(R.id.et_weight);// 体重
            ((LinearLayout) viewPeople.findViewById(R.id.ll_step_length)).setOnClickListener(this);
            etStepLength = (TextView) viewPeople.findViewById(R.id.et_step_length);// 步长
        } else if (2 == strTrackerType) {
            llPeople.setVisibility(View.GONE);
            llPet.setVisibility(View.VISIBLE);
            ((LinearLayout) viewPeople.findViewById(R.id.ll_pet_type)).setOnClickListener(this);
            etPetType = (TextView) viewPeople.findViewById(R.id.et_pet_type);// 品种
            ((LinearLayout) viewPeople.findViewById(R.id.ll_weight_pet)).setOnClickListener(this);
            etWeightPet = (TextView) viewPeople.findViewById(R.id.et_weight_pet);// 宠物的体重
        }
        viewPeople.findViewById(R.id.ll_tracker_mark).setOnClickListener(this);
        etMark = (TextView) viewPeople.findViewById(R.id.et_tracker_mark);// 外貌特证
        llPeoplePhone = ((LinearLayout) viewPeople.findViewById(R.id.ll_phone));
        llPeoplePhone.setOnClickListener(this);
        LogUtil.i("是否设备718" + mTracker.product_type);
        if (Utils.serialNumberRange7181(mTracker.ranges, mTracker.product_type)) {//是否是718，是，显示紧急联系人，不是不显示
            LogUtil.i("是718:" + mTracker.product_type);
            llPeoplePhone.setVisibility(View.VISIBLE);
        } else {
            llPeoplePhone.setVisibility(View.GONE);
        }
        etPhone1 = (TextView) viewPeople.findViewById(R.id.et_tracker_phone1);// 紧急联系人

//        etLostAddress = (TextView) viewPeople.findViewById(R.id.et_lost_address);// 最后定位地址
        llPetLnsurance = (LinearLayout) viewPeople.findViewById(R.id.ll_pet_lnsurance);//宠物保险
        llPetLnsurance.setOnClickListener(this);
        tvActiveState = (TextView) viewPeople.findViewById(R.id.tv_active_state);//宠物保险激活状态

    }

    /**
     * 初始化汽车控件
     */
    private void initCarView() {
        viewCar.findViewById(R.id.ll_car_plate_number).setOnClickListener(this);
        etCarNumber = (TextView) viewCar.findViewById(R.id.et_car_plate_number);// 车牌号
        etCarNumber.setText(mTracker.nickname);

        if (6 == strTrackerType) {
            LinearLayout ll_car_vin = (LinearLayout) viewCar.findViewById(R.id.ll_car_vin);//车架号
            ll_car_vin.setVisibility(View.VISIBLE);
            ll_car_vin.setOnClickListener(this);
            et_car_vin = (TextView) viewCar.findViewById(R.id.et_car_vin);
        } else if (4 == strTrackerType) {
            TextView text_car_type = (TextView) viewCar.findViewById(R.id.text_car_type);
            text_car_type.setText(R.string.car_brand);
        }

        viewCar.findViewById(R.id.ll_car_type).setOnClickListener(this);
        etCarType = (TextView) viewCar.findViewById(R.id.et_car_type);// 车型

        viewCar.findViewById(R.id.ll_car_time).setOnClickListener(this);
        etCarTime = (TextView) findViewById(R.id.et_car_time);// 购车时间

        viewCar.findViewById(R.id.ll_tracker_car_phone).setOnClickListener(this);
        etCarPhone = (TextView) viewCar.findViewById(R.id.et_tracker_phone1);// 紧急联系人
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                if (fromWhere.equals(Constants.BINDACTIVITY)) {//从绑定设备页面进来的就返回到主页
                    startActivity(new Intent(mContext, MainActivity.class));
                    sendBroadcast(new Intent(Constants.ACTION_TRACTER_ENTER_MAIN));
                } else {
                    finish();
                }
                break;

            case R.id.ll_pet_lnsurance:// 宠物保险
                if (Utils.isSuperUser(mTracker, mContext)) {
                    if (2 == insurCode) {// 表示已激活
                        startActivity(new Intent(mContext, InsuranceInformationActivity.class));
                    } else if (1 == insurCode) {// 表示立即激活
                        instantlyActivatedDlaog();
                    }
                }
                break;
            case R.id.rl_change_portrait:// 头像
                if (!isSuperUser())
                    return;
                RequestPermission.create(mContext, this).checkSinglePermission(Manifest.permission.CAMERA, getString(R.string.permission_cameras));
//                photoUtil.requestPermission("temp.jpg");
                break;

            case R.id.ll_tracker_name:// 昵称
                if (!isSuperUser()) {
                    return;
                }
                setLostCard2People();
                Intent nickNameIntent = new Intent(mContext, InformationCardEditorActivity.class);
                Bundle bundle = new Bundle();
                if (7 == strTrackerType || 1 == strTrackerType || 5 == strTrackerType) {
                    bundle.putSerializable("lostCard", lostCard2People);
                } else {
                    bundle.putSerializable("lostCard", lostCard2Pet);
                }
                nickNameIntent.putExtras(bundle);
                nickNameIntent.putExtra("Titlename", R.string.edit_name);
                nickNameIntent.putExtra("data", etName.getText().toString().trim());
                nickNameIntent.putExtra("code", NICKNAMECODE);
                nickNameIntent.putExtra("trackerNo", strTrackerNo);
                nickNameIntent.putExtra("type", strTrackerType);
                nickNameIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
                startActivityForResult(nickNameIntent, NICKNAMECODE);
                break;
            case R.id.ll_sex:// 性别
                if (!isSuperUser()) {
                    return;
                }
                if (7 == strTrackerType || strTrackerType == 1 || strTrackerType == 5) {// 人
                    popupWindowUtils.initPopupWindowSex(getString(R.string.man),
                            new OnClickListener() {


                                @Override
                                public void onClick(View v) {// 男
                                    sex = getString(R.string.man);

                                    setLostCard2People();
                                    setLostCard();
                                    popupWindowUtils.dismiss();

                                }
                            }, getString(R.string.woman), new OnClickListener() {

                                @Override
                                public void onClick(View v) {// 女
                                    sex = getString(R.string.woman);
                                    //tvSex.setText(getString(R.string.woman));
                                    setLostCard2People();
                                    setLostCard();
                                    popupWindowUtils.dismiss();

                                }
                            }, getString(R.string.cancel), new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    popupWindowUtils.dismiss();

                                }
                            }, true);
                } else if (strTrackerType == 2) {// 宠物
                    popupWindowUtils.initPopupWindowSex(getString(R.string.male),
                            new OnClickListener() {

                                @Override
                                public void onClick(View v) {// 雄
                                    //tvSex.setText(getString(R.string.male));
                                    sex = getString(R.string.male);
                                    setLostCard2People();
                                    setLostCard();
                                    popupWindowUtils.dismiss();

                                }
                            }, getString(R.string.female), new OnClickListener() {

                                @Override
                                public void onClick(View v) {// 雌
                                    //tvSex.setText(getString(R.string.female));
                                    sex = getString(R.string.female);
                                    setLostCard2People();
                                    setLostCard();
                                    popupWindowUtils.dismiss();

                                }
                            }, getString(R.string.cancel), new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    popupWindowUtils.dismiss();

                                }
                            }, true);
                }

                break;
            case R.id.ll_birthday:// 生日
                if (!isSuperUser()) {
                    return;
                }
                if (Utils.isEmpty(sDay)) {
                    Calendar calendar = Calendar.getInstance();
                    popupWindowYearMonthDayUtils.ShowTime(Utils.getDate(calendar));
                } else {
                    popupWindowYearMonthDayUtils.ShowTime(sDay);
                }
                break;
            case R.id.ll_height:// 身高
                if (!isSuperUser()) {
                    return;
                }
                setLostCard2People();
                Intent heightIntent = new Intent(mContext,
                        InformationCardEditorActivity.class);
                Bundle bundle1 = new Bundle();
                if (7 == strTrackerType || 1 == strTrackerType || 5 == strTrackerType) {
                    bundle1.putSerializable("lostCard", lostCard2People);
                } else {
                    bundle1.putSerializable("lostCard", lostCard2Pet);
                }
                heightIntent.putExtras(bundle1);
                heightIntent.putExtra("Titlename", R.string.edit_name);
                heightIntent.putExtra("data", etHeight.getText().toString().trim());
                heightIntent.putExtra("code", HEIGHTCODE);
                heightIntent.putExtra("trackerNo", strTrackerNo);
                heightIntent.putExtra("type", strTrackerType);
                heightIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
                startActivityForResult(heightIntent, HEIGHTCODE);
                break;
            case R.id.ll_weight:// 体重
                if (!isSuperUser()) {
                    return;
                }
                setLostCard2People();
                Intent weightIntent = new Intent(mContext,
                        InformationCardEditorActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("lostCard", lostCard2People);
                weightIntent.putExtras(bundle2);
                weightIntent.putExtra("Titlename", R.string.edit_name);
                weightIntent.putExtra("data", etWeight.getText().toString().trim());
                weightIntent.putExtra("code", WEIGHTCODE);
                weightIntent.putExtra("trackerNo", strTrackerNo);
                weightIntent.putExtra("type", strTrackerType);
                weightIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
                startActivityForResult(weightIntent, WEIGHTCODE);
                break;

            case R.id.ll_step_length:// 步长
                if (!isSuperUser()) {
                    return;
                }
                setLostCard2People();
                Intent leightIntent = new Intent(mContext,
                        InformationCardEditorActivity.class);
                Bundle bundle3 = new Bundle();
                bundle3.putSerializable("lostCard", lostCard2People);
                leightIntent.putExtras(bundle3);
                leightIntent.putExtra("Titlename", R.string.edit_name);
                leightIntent.putExtra("data", etStepLength.getText().toString()
                        .trim());
                leightIntent.putExtra("code", LEIGHTCODE);
                leightIntent.putExtra("trackerNo", strTrackerNo);
                leightIntent.putExtra("type", strTrackerType);
                leightIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
                startActivityForResult(leightIntent, LEIGHTCODE);
                break;
            case R.id.ll_pet_type:// 品种
                if (!isSuperUser()) {
                    return;
                }
                setLostCard2People();
                Intent petTypeIntent = new Intent(mContext,
                        InformationCardEditorActivity.class);
                Bundle bundle4 = new Bundle();
                bundle4.putSerializable("lostCard", lostCard2Pet);
                petTypeIntent.putExtras(bundle4);
                petTypeIntent.putExtra("Titlename", R.string.edit_name);
                petTypeIntent.putExtra("data", etPetType.getText().toString()
                        .trim());
                petTypeIntent.putExtra("code", PETTYPECODE);
                petTypeIntent.putExtra("trackerNo", strTrackerNo);
                petTypeIntent.putExtra("type", strTrackerType);
                petTypeIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
                startActivityForResult(petTypeIntent, PETTYPECODE);
                break;
            case R.id.ll_weight_pet:// 宠物的体重
                if (!isSuperUser()) {
                    return;
                }
                setLostCard2People();
                Intent weightPetIntent = new Intent(mContext,
                        InformationCardEditorActivity.class);
                Bundle bundle5 = new Bundle();
                bundle5.putSerializable("lostCard", lostCard2Pet);
                weightPetIntent.putExtras(bundle5);

                weightPetIntent.putExtra("Titlename", R.string.edit_name);
                weightPetIntent.putExtra("data", etWeightPet.getText().toString()
                        .trim());
                weightPetIntent.putExtra("code", WEIGHTPETCODE);
                weightPetIntent.putExtra("trackerNo", strTrackerNo);
                weightPetIntent.putExtra("type", strTrackerType);
                weightPetIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
                startActivityForResult(weightPetIntent, WEIGHTPETCODE);
                break;
            case R.id.ll_tracker_mark:// 外貌特证
                if (!isSuperUser()) {
                    return;
                }
                setLostCard2People();
                Intent trackerMarkIntent = new Intent(mContext,
                        InformationCardEditorActivity.class);
                Bundle bundle6 = new Bundle();
                if (7 == strTrackerType || 1 == strTrackerType || 5 == strTrackerType) {
                    bundle6.putSerializable("lostCard", lostCard2People);
                } else {
                    bundle6.putSerializable("lostCard", lostCard2Pet);
                }
                trackerMarkIntent.putExtras(bundle6);
                trackerMarkIntent.putExtra("Titlename", R.string.edit_name);
                trackerMarkIntent.putExtra("data", etMark.getText().toString()
                        .trim());
                trackerMarkIntent.putExtra("code", TRACKERMARKCODE);
                trackerMarkIntent.putExtra("trackerNo", strTrackerNo);
                trackerMarkIntent.putExtra("type", strTrackerType);
                trackerMarkIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
                startActivityForResult(trackerMarkIntent, TRACKERMARKCODE);
                break;

            case R.id.ll_phone:// 紧急联络人
                if (!isSuperUser()) {
                    return;
                }
                setLostCard2People();
                Intent phoneIntent = new Intent(mContext,
                        InformationCardEditorActivity.class);
                Bundle bundle7 = new Bundle();
                if (7 == strTrackerType || 1 == strTrackerType || 5 == strTrackerType) {
                    bundle7.putSerializable("lostCard", lostCard2People);
                } else {
                    bundle7.putSerializable("lostCard", lostCard2Pet);
                }
                phoneIntent.putExtras(bundle7);

                phoneIntent.putExtra("Titlename", R.string.edit_name);
                phoneIntent.putExtra("data", etPhone1.getText().toString().trim());
                phoneIntent.putExtra("code", PHONECODE);
                phoneIntent.putExtra("trackerNo", strTrackerNo);
                phoneIntent.putExtra("type", strTrackerType);
                phoneIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
                startActivityForResult(phoneIntent, PHONECODE);
                break;
            case R.id.ll_car_plate_number:// 车牌号
                if (!isSuperUser()) {
                    return;
                }
                setLostCard2Car();
                Intent carPlateNumberIntent = new Intent(mContext,
                        InformationCardEditorActivity.class);
                Bundle bundle8 = new Bundle();
                bundle8.putSerializable("lostCard", lostCard2Car);
                carPlateNumberIntent.putExtras(bundle8);
                carPlateNumberIntent.putExtra("Titlename", R.string.edit_name);
                carPlateNumberIntent.putExtra("data", etCarNumber.getText().toString().trim());
                carPlateNumberIntent.putExtra("code", CARPLATENUMBERCODE);
                carPlateNumberIntent.putExtra("trackerNo", strTrackerNo);
                carPlateNumberIntent.putExtra("type", strTrackerType);
                carPlateNumberIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
                startActivityForResult(carPlateNumberIntent, CARPLATENUMBERCODE);
                break;

            case R.id.ll_car_vin://车架号
                if (!isSuperUser()) {
                    return;
                }
                setLostCard2Car();
                Intent carvinIntent = new Intent(mContext,
                        InformationCardEditorActivity.class);
                Bundle bundle11 = new Bundle();
                bundle11.putSerializable("lostCard", lostCard2Car);
                carvinIntent.putExtras(bundle11);
                carvinIntent.putExtra("Titlename", R.string.edit_name);
                carvinIntent.putExtra("data", et_car_vin.getText().toString().trim());
                carvinIntent.putExtra("code", CARVINCODE);
                carvinIntent.putExtra("trackerNo", strTrackerNo);
                carvinIntent.putExtra("type", strTrackerType);
                carvinIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
                startActivityForResult(carvinIntent, CARVINCODE);
                break;


            case R.id.ll_car_type:// 车的类型
                if (!isSuperUser()) {
                    return;
                }
                setLostCard2Car();
                Intent cartypeIntent = new Intent(mContext,
                        InformationCardEditorActivity.class);
                Bundle bundle9 = new Bundle();
                bundle9.putSerializable("lostCard", lostCard2Car);
                cartypeIntent.putExtras(bundle9);
                cartypeIntent.putExtra("Titlename", R.string.edit_name);
                cartypeIntent.putExtra("data", etCarType.getText().toString()
                        .trim());
                cartypeIntent.putExtra("code", CARTYPECODE);
                cartypeIntent.putExtra("trackerNo", strTrackerNo);
                cartypeIntent.putExtra("type", strTrackerType);
                cartypeIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
                startActivityForResult(cartypeIntent, CARTYPECODE);
                break;
            case R.id.ll_car_time:// 购车时间
                if (!isSuperUser()) {
                    return;
                }
                if (Utils.isEmpty(sDay)) {
                    Calendar calendar = Calendar.getInstance();
                    popupWindowYearMonthDayUtils.ShowTime(Utils.getDate(calendar));
                } else {
                    popupWindowYearMonthDayUtils.ShowTime(sDay);
                }
                break;
            case R.id.ll_tracker_car_phone:// 车的紧急联络人
                if (!isSuperUser()) {
                    return;
                }
                setLostCard2Car();
                Intent carPhoneIntent = new Intent(mContext,
                        InformationCardEditorActivity.class);
                Bundle bundle10 = new Bundle();
                bundle10.putSerializable("lostCard", lostCard2Car);
                carPhoneIntent.putExtras(bundle10);
                carPhoneIntent.putExtra("Titlename", R.string.edit_name);
                carPhoneIntent.putExtra("data", etCarPhone.getText().toString()
                        .trim());
                carPhoneIntent.putExtra("code", CARPHONECODE);
                carPhoneIntent.putExtra("trackerNo", strTrackerNo);
                carPhoneIntent.putExtra("type", strTrackerType);
                carPhoneIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
                startActivityForResult(carPhoneIntent, CARPHONECODE);
                break;
            case R.id.ll_sim:
                if (!isSuperUser())
                    return;
                Intent simIntent = new Intent(mContext, InformationCardEditorActivity.class);
                simIntent.putExtra("code", SIMCODE);
                simIntent.putExtra("trackerNo", strTrackerNo);
                simIntent.putExtra("type", strTrackerType);
                simIntent.putExtra("sim", tv_sim_code.getText().toString().trim());
                simIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
                startActivityForResult(simIntent, SIMCODE);
                break;

        }

    }

    /**
     * 是否超级用户
     */
    private Boolean isSuperUser() {
        if (null != mTracker) {
            if (mTracker.super_user != null
                    && UserSP.getInstance().getUserName(mContext) != null) {
                if (!mTracker.super_user.equalsIgnoreCase(UserSP.getInstance()
                        .getUserName(mContext))) {
                    ToastUtil.show(mContext, R.string.no_super_user);
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == NICKNAMECODE && resultCode == RESULT_OK) {// 昵称
            etName.setText(data.getStringExtra("data"));
        } else if (requestCode == HEIGHTCODE && resultCode == RESULT_OK) {// 身高
            if (!Utils.isEmpty(data.getStringExtra("data"))) {
                etHeight.setText(data.getStringExtra("data") + "cm");
            } else {
                etHeight.setText(data.getStringExtra("data"));
            }

        } else if (requestCode == WEIGHTCODE && resultCode == RESULT_OK) {// 体重
            if (!Utils.isEmpty(data.getStringExtra("data"))) {
                etWeight.setText(data.getStringExtra("data") + "kg");
            } else {
                etWeight.setText(data.getStringExtra("data"));
            }
        } else if (requestCode == LEIGHTCODE && resultCode == RESULT_OK) {// 步长

            // etStepLength.setText(getString(R.string.unit_m,data.getStringExtra("data")));
            etStepLength.setText(data.getStringExtra("data"));
        } else if (requestCode == PETTYPECODE && resultCode == RESULT_OK) {// 宠物的品种
            etPetType.setText(data.getStringExtra("data"));
        } else if (requestCode == WEIGHTPETCODE && resultCode == RESULT_OK) {// 宠物的体重

            if (!Utils.isEmpty(data.getStringExtra("data"))) {
                etWeightPet.setText(data.getStringExtra("data") + "kg");
            } else {
                etWeightPet.setText(data.getStringExtra("data"));
            }
        } else if (requestCode == TRACKERMARKCODE && resultCode == RESULT_OK) {// 外貌特证
            etMark.setText(data.getStringExtra("data"));
        } else if (requestCode == PHONECODE && resultCode == RESULT_OK) {// //紧急联络人
            etPhone1.setText(data.getStringExtra("data"));
        } else if (requestCode == CARPLATENUMBERCODE && resultCode == RESULT_OK) {// //车牌号
            etCarNumber.setText(data.getStringExtra("data"));
        } else if (requestCode == CARVINCODE && resultCode == RESULT_OK) {//车架号
            et_car_vin.setText(data.getStringExtra("data"));
        } else if (requestCode == CARTYPECODE && resultCode == RESULT_OK) {// 车的型号
            etCarType.setText(data.getStringExtra("data"));
        } else if (requestCode == CARPHONECODE && resultCode == RESULT_OK) {// 车的紧急联络人
            etCarPhone.setText(data.getStringExtra("data"));
        } else if (requestCode == SIMCODE && resultCode == RESULT_OK) {//sim卡号设置
            String simNo = data.getStringExtra("data");
            int typeInfo = data.getIntExtra("typeInfo", 0);
            tv_sim_code.setText(data.getStringExtra("data"));
            if (typeInfo == 1)
                lostCard2Pet.simNo = simNo;
            else if (typeInfo == 2) {
                lostCard2People.simNo = simNo;
                mTracker.tracker_sim = simNo;
                UserUtil.saveTracker(mContext, mTracker);
            } else if (typeInfo == 3)
                lostCard2Car.simNo = simNo;
        } else if (requestCode == PHOTO_GRAPH) {// 从手机拍照跳转过来
            File picture = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
            if (picture.exists()) {
                Uri uri;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                    uri = Uri.fromFile(picture);
                else {
                    uri = FileProvider.getUriForFile(mContext, "com.bluebud.liteguardian_hk", picture);
                }
                photoUtil.startPhotoZOOM(uri, "internal_headPortrait.png");
            }
        }

        if (data == null) {
            return;
        }
        // 从相册跳转过来
        else if (requestCode == PICK) {
            Uri uri = data.getData();
            photoUtil.startPhotoZOOM(uri, "internal_headPortrait.png");
        } else if (requestCode == ZOOM) {
            DialogUtil.show(mContext, R.string.prompt, R.string.head_portrait,
                    R.string.confirm, new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            DialogUtil.dismiss();
                            photoUtil.setTrackerHead(ivHead, "internal_headPortrait.png");
                        }
                    }, R.string.cancel, new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            DialogUtil.dismiss();
                        }
                    });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置信息卡
     */
    private void setPeopleData() {
        double lat = 0;
        double lng = 0;

        if (7 == strTrackerType || strTrackerType == 1 || strTrackerType == 5) {// 个人
            if (null == lostCard2People) {
                return;
            }
            if (!TextUtils.isEmpty(lostCard2People.simNo)) {//sim卡号
                tv_sim_code.setText(lostCard2People.simNo);
            }
            if (!Utils.isEmpty(lostCard2People.nickname)) {
                etName.setText(lostCard2People.nickname);// 昵称
            }
            if (!Utils.isEmpty(lostCard2People.human_feature)) {
                etMark.setText(lostCard2People.human_feature);// 外貌特证
            }

            if (!Utils.isEmpty(lostCard2People.mobile1)) {// 紧急联系人
                etPhone1.setText(lostCard2People.mobile1);
            }

            if (!Utils.isEmpty(lostCard2People.human_height)) {// 身高
                etHeight.setText(lostCard2People.human_height.replaceAll("cm", "") + "cm");
            }
            if (!Utils.isEmpty(lostCard2People.human_weight)) {// 体重
                etWeight.setText(lostCard2People.human_weight.replace("kg", "") + "kg");
            }
            if (!Utils.isEmpty(lostCard2People.human_step)) {// 步长
                etStepLength.setText(lostCard2People.human_step);
            }
            LogUtil.i("humanBirthday=" + lostCard2People.human_birthday);
            if (!Utils.isEmpty(lostCard2People.human_birthday)) {// 生日
                LogUtil.i("humanBirthday1=" + lostCard2People.human_birthday);
                //etBirthday.setText(lostCard2People.human_birthday);
                LogUtil.i("截取前human_birthday:" + lostCard2People.human_birthday + ",截取后human_birthday：" + lostCard2People.human_birthday.trim().substring(0, 10));
                etBirthday.setText(lostCard2People.human_birthday.trim().substring(0, 10));
                sDay = lostCard2People.human_birthday.trim().substring(0, 10);
            }

            if (!Utils.isEmpty(lostCard2People.human_sex)) {// 性别
                if ("1".equals(lostCard2People.human_sex)) {
                    tvSex.setText(getString(R.string.man));
                    sex = getString(R.string.man);
                } else {
                    tvSex.setText(getString(R.string.woman));
                    sex = getString(R.string.woman);
                }
            }
            if (1 == strTrackerType) {
                ivHead.setBackgroundResource(R.drawable.image_preson_sos);// 头像
            } else {
                ivHead.setBackgroundResource(R.drawable.image_watch);// 头像
            }
            if (!Utils.isEmpty(lostCard2People.lat)
                    && !Utils.isEmpty(lostCard2People.lng)) {
                lat = Double.parseDouble(lostCard2People.lat);
                lng = Double.parseDouble(lostCard2People.lng);
            }
        } else if (strTrackerType == 2) {// 宠物
            if (null == lostCard2Pet) {
                LogUtil.i("lostCard2Pet is null");
                return;
            }
            if (!TextUtils.isEmpty(lostCard2Pet.simNo)) {//sim卡号
                tv_sim_code.setText(lostCard2Pet.simNo);
            }

            if (!Utils.isEmpty(lostCard2Pet.nickname)) {// 昵称
                etName.setText(lostCard2Pet.nickname);
            }
            if (!Utils.isEmpty(lostCard2Pet.pet_feature)) {// //外貌特证
                etMark.setText(lostCard2Pet.pet_feature);
            }
            if (!Utils.isEmpty(lostCard2Pet.mobile1)) {// 紧急联系人
                etPhone1.setText(lostCard2Pet.mobile1);
            }
            if (!Utils.isEmpty(lostCard2Pet.pet_birthday)) {// 生日
                etBirthday.setText(lostCard2Pet.pet_birthday.trim().substring(0, 10));
                sDay = lostCard2Pet.pet_birthday.trim().substring(0, 10);
            }

            if (!Utils.isEmpty(lostCard2Pet.pet_weight)) {// 体重
                etWeightPet.setText(lostCard2Pet.pet_weight.replaceAll("kg", "") + "kg");
            }
            if (!Utils.isEmpty(lostCard2Pet.pet_breed)) {// 品种
                etPetType.setText(lostCard2Pet.pet_breed);
            }

            if (!Utils.isEmpty(lostCard2Pet.pet_sex)) {// 性别
                if ("1".equals(lostCard2Pet.pet_sex)) {
                    tvSex.setText(getString(R.string.male));
                    sex = getString(R.string.male);
                } else {
                    tvSex.setText(getString(R.string.female));
                    sex = getString(R.string.female);
                }
            }
            if (2 == lostCard2Pet.insur_code) {//表示已激活
                insurCode = 2;
                llPetLnsurance.setVisibility(View.VISIBLE);
                tvActiveState.setText(R.string.activated);
            } else if (1 == lostCard2Pet.insur_code) {//表示立即激活
                insurCode = 1;
                llPetLnsurance.setVisibility(View.VISIBLE);
                tvActiveState.setText(R.string.instantly_activated);
            } else {//表示不显示
                insurCode = 0;
                llPetLnsurance.setVisibility(View.GONE);
            }
            ivHead.setBackgroundResource(R.drawable.image_pet);// 头像

            if (!Utils.isEmpty(lostCard2Pet.lat)
                    && !Utils.isEmpty(lostCard2Pet.lng)) {
                lat = Double.parseDouble(lostCard2Pet.lat);
                lng = Double.parseDouble(lostCard2Pet.lng);
            }
        }

        if (mTracker.head_portrait != null
                && !mTracker.head_portrait.equals("")) {
            String url = Utils.getImageUrl(mContext) + mTracker.head_portrait;
            LogUtil.i("头像的地址：" + url);
            Glide.with(mContext).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(ivHead);
        }

//        if (0 != lat && 0 != lng) {
//            LatLng ll = Utils.gpsConvert2BaiduPoint(CoordType.GPS, new LatLng(lat, lng));
//            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
//        }
    }

    /**
     * 设置汽车和摩托车的信息卡
     */
    private void setCarData() {
        if (null == lostCard2Car) {
            return;
        }

        if (!TextUtils.isEmpty(lostCard2Car.simNo)) {//sim卡号
            tv_sim_code.setText(lostCard2Car.simNo);
        }

        if (!Utils.isEmpty(lostCard2Car.mobile1)) {
            etCarPhone.setText(lostCard2Car.mobile1);
        }

        if (6 == strTrackerType) {//obd车辆 车架号vin
            et_car_vin.setText(lostCard2Car.car_vin);
        }

        if (strTrackerType == 3) {//汽车

            if (!Utils.isEmpty(lostCard2Car.nickname)) {// 车牌号
                etCarNumber.setText(lostCard2Car.nickname);
            }

            if (!Utils.isEmpty(lostCard2Car.car_buytime)) {// 要加购车时间
                etCarTime.setText(lostCard2Car.car_buytime.trim().substring(0, 10));
            }
            if (!Utils.isEmpty(lostCard2Car.car_type)) {// 车型
                etCarType.setText(lostCard2Car.car_type);
            }

            ivHead.setBackgroundResource(R.drawable.image_car);
        } else if (strTrackerType == 4) {//摩托车
            if (!Utils.isEmpty(lostCard2Car.nickname)) {// 车牌号
                etCarNumber.setText(lostCard2Car.nickname);
            }

            if (!Utils.isEmpty(lostCard2Car.motor_buytime)) {// 要加购车时间
                etCarTime.setText(lostCard2Car.motor_buytime.trim().substring(0, 10));
            }
            if (!Utils.isEmpty(lostCard2Car.moto_type)) {// 车型
                etCarType.setText(lostCard2Car.moto_type);
            }

            ivHead.setBackgroundResource(R.drawable.image_motorcycle);
        } else {
            if (!Utils.isEmpty(lostCard2Car.nickname)) {// 车牌号
                etCarNumber.setText(lostCard2Car.nickname);
            }
            if (!Utils.isEmpty(lostCard2Car.obd_buytime)) {// 要加购车时间
                etCarTime.setText(lostCard2Car.obd_buytime.trim().substring(0, 10));
            }
            if (!Utils.isEmpty(lostCard2Car.obd_type)) {// 车型
                etCarType.setText(lostCard2Car.obd_type);
            }
            ivHead.setBackgroundResource(R.drawable.image_car);
        }

        if (mTracker.head_portrait != null
                && !mTracker.head_portrait.equals("")) {
            String url = Utils.getImageUrl(mContext) + mTracker.head_portrait;
            LogUtil.i("头像的地址：" + url);
            Glide.with(mContext).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(ivHead);
        }
    }

    @Override
    public void onProgressDialogBack() {
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }

    /**
     * 获取信息卡信息接口
     */
    private void getLostCard() {
        String url = UserUtil.getServerUrl(mContext);
        RequestParams params = HttpParams.getLostCard(strTrackerNo);

        requestHandle = HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(mContext, null, TrackerEditActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            if (7 == strTrackerType || 1 == strTrackerType || 5 == strTrackerType) {
                                lostCard2People = GsonParse.lostCard2PeopleParse(new String(response));
                                mTracker.head_portrait = lostCard2People.head_portrait;
                                setPeopleData();
                            } else if (2 == strTrackerType) {
                                lostCard2Pet = GsonParse.lostCard2PetParse(new String(response));
                                mTracker.head_portrait = lostCard2Pet.head_portrait;
                                setPeopleData();
                            } else if (3 == strTrackerType
                                    || 4 == strTrackerType
                                    || 6 == strTrackerType) {
                                lostCard2Car = GsonParse.lostCard2CarParse(new String(response));
                                mTracker.head_portrait = lostCard2Car.head_portrait;
                                setCarData();
                            }
                        } else {
                            ToastUtil.show(mContext, obj.what);
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(mContext,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }

                });
    }

    private void saveTrackerInfoSuccess() {
        if (!Utils.isEmpty(trackerName)) {
            mTracker.nickname = trackerName;
            LogUtil.i("nickname:" + mTracker.nickname);
        }
        if (7 == strTrackerType || 1 == strTrackerType || 5 == strTrackerType) {
            LogUtil.i("!!!!!!!!!!!!!!!!!!!!!!!!!7.1.5");
            setPeopleData();
        } else if (2 == strTrackerType) {
            LogUtil.i("!!!!!!!!!!!!!!!!!!!!!!!!!2");
            setPeopleData();
        } else if (3 == strTrackerType
                || 4 == strTrackerType
                || 6 == strTrackerType) {
            LogUtil.i("!!!!!!!!!!!!!!!!!!!!!!!!!3.4.6");
            setCarData();
        }
        UserUtil.saveTracker(mContext, mTracker);
        sendBroadcast(new Intent(Constants.ACTION_TRACTER_NICKNAME_CHANGE));

    }

    private void setLostCard() {
        String url = UserUtil.getServerUrl(mContext);
        LogUtil.i("设备号：" + strTrackerNo);
        RequestParams params = new RequestParams();
        if (7 == strTrackerType || 1 == strTrackerType || 5 == strTrackerType) {
            params = HttpParams.setLostCard2People(strTrackerNo,
                    lostCard2People);
            LogUtil.e("上传信息卡" + lostCard2People.toString());
        } else if (2 == strTrackerType) {
            params = HttpParams.setLostCard2Pet(strTrackerNo, lostCard2Pet);
            LogUtil.e("上传信息卡" + lostCard2Pet.toString());
        } else if (3 == strTrackerType) {
            params = HttpParams.setLostCard2Car(strTrackerNo, lostCard2Car);
            LogUtil.e("上传信息卡" + lostCard2Car.toString());
        } else if (4 == strTrackerType) {
            params = HttpParams.setLostCard2Motor(strTrackerNo, lostCard2Car);
            LogUtil.e("上传信息卡" + lostCard2Car.toString());

        } else if (6 == strTrackerType) {
            params = HttpParams.setLostCard2Obd(strTrackerNo, lostCard2Car);
            LogUtil.e("上传信息卡" + lostCard2Car.toString());
        }

        requestHandle = HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                mContext, null,
                                TrackerEditActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (0 == obj.code) {
                            saveTrackerInfoSuccess();
                            ToastUtil.show(mContext, obj.what);
                        } else {
                            ToastUtil.show(mContext, obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(mContext, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }


    private void setLostCard2People() {
        trackerName = etName.getText().toString().trim();
        mark = etMark.getText().toString().trim();
        final String mobile1 = etPhone1.getText().toString().trim();

        if (null != trackerName && !trackerName.equals("")
                && !Utils.isCorrectTrackerName(trackerName)) {
            ToastUtil.show(mContext, R.string.input_tracker_name);
            return;
        }
        if (mark != null && !mark.equals("") && !Utils.isCorrectMark(mark)) {
            ToastUtil.show(mContext, R.string.input_tracker_mark);
            return;
        }
        if (5 != strTrackerType) {
            if (mobile1 != null && !mobile1.equals("")
                    && !Utils.isCorrectPhone(mobile1)) {
                ToastUtil.show(mContext, R.string.input_tracker_contect);
                return;
            }
        }

        if (7 == strTrackerType || 1 == strTrackerType || 5 == strTrackerType) {
            if (null == lostCard2People) {
                lostCard2People = new LostCard2People();
            }

            if (!Utils.isEmpty(mark)) {
                lostCard2People.human_feature = mark;
            } else {
                lostCard2People.human_feature = "";
            }
            if (!Utils.isEmpty(trackerName)) {
                lostCard2People.nickname = trackerName;
            } else {
                lostCard2People.nickname = "";
            }
            if (!Utils.isEmpty(mobile1)) {
                lostCard2People.mobile1 = mobile1;
            } else {
                lostCard2People.mobile1 = "";
            }

            if (!Utils.isEmpty(etHeight.getText().toString().trim())) {
                lostCard2People.human_height = etHeight.getText().toString()
                        .trim();
            } else {
                lostCard2People.human_height = "";
            }
            if (!Utils.isEmpty(etWeight.getText().toString().trim())) {
                lostCard2People.human_weight = etWeight.getText().toString()
                        .trim();
            } else {
                lostCard2People.human_weight = "";
            }
            if (!Utils.isEmpty(etStepLength.getText().toString().trim())) {
                lostCard2People.human_step = etStepLength.getText().toString()
                        .trim();
            } else {
                lostCard2People.human_step = "";
            }
            if (getString(R.string.man).equals(sex)) {
                lostCard2People.human_sex = "1";
            } else if (getString(R.string.woman).equals(sex)) {
                lostCard2People.human_sex = "0";
            }
            if (!Utils.isEmpty(sDay)) {// 生日
                lostCard2People.human_birthday = sDay;

            } else {
                lostCard2People.human_birthday = "";
            }

        } else if (2 == strTrackerType) {
            if (null == lostCard2People) {
                lostCard2Pet = new LostCard2Pet();
            }

            if (!Utils.isEmpty(mark)) {
                lostCard2Pet.pet_feature = mark;
            } else {
                lostCard2Pet.pet_feature = "";
            }
            if (!Utils.isEmpty(trackerName)) {
                lostCard2Pet.nickname = trackerName;
            } else {
                lostCard2Pet.nickname = "";
            }
            if (!Utils.isEmpty(mobile1)) {
                lostCard2Pet.mobile1 = mobile1;
            } else {
                lostCard2Pet.mobile1 = "";
            }

            if (!Utils.isEmpty(etPetType.getText().toString().trim())) {
                lostCard2Pet.pet_breed = etPetType.getText().toString().trim();
            } else {
                lostCard2Pet.pet_breed = "";
            }
            if (!Utils.isEmpty(etWeightPet.getText().toString().trim())) {
                lostCard2Pet.pet_weight = etWeightPet.getText().toString()
                        .trim();
            } else {
                lostCard2Pet.pet_weight = "";
            }
            if ((getString(R.string.male)).equals(sex)) {
                lostCard2Pet.pet_sex = "1";
            } else if (getString(R.string.female).equals(sex)) {
                lostCard2Pet.pet_sex = "0";
            }
            if (!Utils.isEmpty(sDay)) {// 生日
                lostCard2Pet.pet_birthday = sDay;
            } else {
                lostCard2Pet.pet_birthday = "";
            }
            if (insurCode == 2) {
                lostCard2Pet.insur_code = 2;
            } else if (insurCode == 1) {
                lostCard2Pet.insur_code = 1;
            } else {
                lostCard2Pet.insur_code = 0;
            }
        }
    }

    private void setLostCard2Car() {
        trackerName = etCarNumber.getText().toString().trim();

        if (null != trackerName && !trackerName.equals("")
                && !Utils.isCorrectTrackerName(trackerName)) {
            ToastUtil.show(mContext, R.string.input_tracker_name);
            return;
        }

        final String mobile1 = etCarPhone.getText().toString().trim();

        if (null == lostCard2Car) {
            lostCard2Car = new LostCard2Car();
        }

        if (!Utils.isEmpty(mobile1)) {
            lostCard2Car.mobile1 = mobile1;
        } else {
            lostCard2Car.mobile1 = "";
        }
        LogUtil.i("trackerName:" + trackerName);

        if (3 == strTrackerType) {
            if (!Utils.isEmpty(etCarNumber.getText().toString().trim())) {
                lostCard2Car.nickname = etCarNumber.getText().toString().trim();
            } else {
                lostCard2Car.nickname = "";
            }

            if (!Utils.isEmpty(etCarType.getText().toString().trim())) {
                lostCard2Car.car_type = etCarType.getText().toString().trim();
            } else {
                lostCard2Car.car_type = "";
            }
            if (!Utils.isEmpty(sDay)) {// 购车时间
                lostCard2Car.car_buytime = sDay;
            } else {
                lostCard2Car.car_buytime = "";
            }
        }

        if (6 == strTrackerType) {

            if (!Utils.isEmpty(etCarNumber.getText().toString().trim())) {
                lostCard2Car.nickname = etCarNumber.getText().toString().trim();
            } else {
                lostCard2Car.nickname = "";
            }

            if (!Utils.isEmpty(et_car_vin.getText().toString().trim())) {//车架号vin
                lostCard2Car.car_vin = et_car_vin.getText().toString().trim();
            } else {
                lostCard2Car.car_vin = "";
            }

            if (!Utils.isEmpty(etCarType.getText().toString().trim())) {
                lostCard2Car.obd_type = etCarType.getText().toString().trim();
            } else {
                lostCard2Car.obd_type = "";
            }
            if (!Utils.isEmpty(sDay)) {// 购车时间
                lostCard2Car.obd_buytime = sDay;
            } else {
                lostCard2Car.obd_buytime = "";
            }
        }

        if (4 == strTrackerType) {

            if (!Utils.isEmpty(etCarNumber.getText().toString().trim())) {
                lostCard2Car.nickname = etCarNumber.getText().toString().trim();
            } else {
                lostCard2Car.nickname = "";
            }

            if (!Utils.isEmpty(etCarType.getText().toString().trim())) {
                lostCard2Car.moto_type = etCarType.getText().toString().trim();
            } else {
                lostCard2Car.moto_type = "";
            }
            if (!Utils.isEmpty(sDay)) {// 购车时间
                lostCard2Car.motor_buytime = sDay;
            } else {
                lostCard2Car.motor_buytime = "";
            }

        }

    }


    @Override
    public void getWheelYearMonthDay(String sTime) {

        this.sDay = sTime;
        LogUtil.i(sDay);
        if (Utils.compareDate(Utils.curDate2Day(this), sTime) < 0) {
            ToastUtil.show(mContext, R.string.date_error);
            return;
        }
        if (strTrackerType == 3 || strTrackerType == 4 || strTrackerType == 6) {// 购车时间
            //etCarTime.setText(sTime);
            LogUtil.i("时间 ：" + etCarTime.getText().toString().trim());
            setLostCard2Car();
            setLostCard();
        } else {
            //etBirthday.setText(sTime);// 生日
            LogUtil.i("时间" + etBirthday.getText().toString().trim());
            setLostCard2People();
            setLostCard();
        }

    }

    //立即激活
    private void instantlyActivatedDlaog() {
        DialogUtil.showEditDialog(mContext, R.string.pls_input_lnsurance_code, R.string.confirm, R.string.cancel, this, "", getString(R.string.pls_input_lnsurance_code));
    }

    @Override
    public void editTextEdit(String str, AlertDialog mDialog) {
        LogUtil.i("str:" + str);
        mDialog.dismiss();
        editInsurCode(str);
    }


    private void editInsurCode(String insurCode) {

        String url = UserUtil.getServerUrl(mContext);
        //验证宠物验证码
        RequestParams params = HttpParams.isExistDeviceCode(strTrackerNo, insurCode);

        requestHandle = HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                mContext, null,
                                TrackerEditActivity.this);
                    }


                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {//宠物保险验证成功
                            startActivity(new Intent(mContext, PetInsurActivity.class));
                        }
                        ToastUtil.show(mContext, obj.what);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(mContext,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });

    }

    /**
     * 申请权限
     */
    @Override
    public void onPermissionSuccess() {
        photoUtil.requestPermission("temp.jpg");
    }
}
