package com.bluebud.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebud.activity.DeviceManagement;
import com.bluebud.activity.MainActivity;
import com.bluebud.activity.RemoteTakePhotoActivity;
import com.bluebud.activity.TelephoneBookActivity;
import com.bluebud.activity.TimeSchoolSwitchActivity;
import com.bluebud.activity.TimeSwitchActivity;
import com.bluebud.activity.settings.AlarmClockListActivity;
import com.bluebud.activity.settings.AlarmSwitchActivity;
import com.bluebud.activity.settings.ClassScheduleActivity;
import com.bluebud.activity.settings.DisturbTimeSchoolActivity;
import com.bluebud.activity.settings.FunctionControlActivity;
import com.bluebud.activity.settings.MedicineRemindActivity;
import com.bluebud.activity.settings.MemberManagementActivity;
import com.bluebud.activity.settings.MoreActivity;
import com.bluebud.activity.settings.PhoneBookActivity;
import com.bluebud.activity.settings.SceneryModeActivity;
import com.bluebud.activity.settings.SettingActivity;
import com.bluebud.activity.settings.SportStepActivity;
import com.bluebud.activity.settings.StepSettingActivity;
import com.bluebud.activity.settings.TimePositionActivity;
import com.bluebud.activity.settings.TimeZoneActivity;
import com.bluebud.activity.settings.TimeZoneWatchActivity;
import com.bluebud.activity.settings.TrackerEditActivity;
import com.bluebud.activity.settings.WifiSettingActivity;
import com.bluebud.activity.settings.targetSettingActivity;
import com.bluebud.adapter.MineAdapter;
import com.bluebud.chat.ChatInfoCardActivity;
import com.bluebud.chat.utils.ChatInfo;
import com.bluebud.chat.utils.ChatUtil;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.DeviceExpiredUtil;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.MineHttpRequestUtl;
import com.bluebud.utils.PopupWindowUtils;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.utils.resource.ResourceCallback;
import com.bluebud.view.CircleImageView;
import com.bluebud.view.MyListview;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;

import io.rong.eventbus.EventBus;

public class TabMineFragment extends Fragment implements OnClickListener, OnItemClickListener {
    private View parentView;
    private MyListview lvSetting;
    private Tracker mCurTracker;
    private String strTrackerNo;
    private int ranges = 1;
    private CircleImageView ivTrackerImage;
    private TextView tvNickName;
    private TextView tvAccount;
    private MainActivity mContext;
    private MineAdapter adapter;
    private View watch720_item;
    private View watch770_item;
    private TextView text_class;/*上课禁用*/
    private int protocol_type = 0;

    private LinearLayout ll_mine_placeholder1;//770系列手表下半部分站位符
    private LinearLayout ll_mine_placeholder2;//770系列手表下半部分站位符
    private LinearLayout ll_mine_placeholder3;//770系列手表下半部分站位符
    private LinearLayout ll_mine_placeholder4;//770系列手表下半部分站位符
    private ImageView image_mine_placeholder1;
    private ImageView image_mine_placeholder2;
    private ImageView image_mine_placeholder3;
    private ImageView image_mine_placeholder4;
    private TextView name_mine_placeholder1;
    private TextView name_mine_placeholder2;
    private TextView name_mine_placeholder3;
    private TextView name_mine_placeholder4;
    private String product_type;
    private MineHttpRequestUtl requestUtl;
    private PopupWindowUtils popupWindowUtils;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        WeakReference wr = new WeakReference(activity);
        mContext = (MainActivity) wr.get();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == parentView) {
            parentView = inflater.inflate(R.layout.activity_mine, container, false);
            initView();
            return parentView;
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
        if (ChatUtil.userPhoto == null || TextUtils.isEmpty(ChatUtil.userPhoto))
            ChatUtil.userPhoto = UserSP.getInstance().getUserPhoto(mContext);
        if (ChatUtil.userNickname == null || TextUtils.isEmpty(ChatUtil.userNickname))
            ChatUtil.userNickname = UserSP.getInstance().getNickName(mContext);
        setUserInfomation(ChatUtil.userNickname, ChatUtil.userPhoto);// 微聊设置头像和昵称
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constants.isChangeDevice) {
            mCurTracker = UserUtil.getCurrentTracker(mContext);
            Constants.isChangeDevice = false;
            if (initData()) {//显示当前设备布局
                adapter.setList(ResourceCallback.singleResource().getMineName(ranges, product_type), ResourceCallback.singleResource().getMineImage(ranges, product_type), ranges, protocol_type);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mCurTracker = UserUtil.getCurrentTracker(mContext);
        Constants.isChangeDevice = false;
        ivTrackerImage = (CircleImageView) parentView.findViewById(R.id.iv_tracker_image);//头像
        tvNickName = (TextView) parentView.findViewById(R.id.tv_nickname);//昵称
        tvAccount = (TextView) parentView.findViewById(R.id.tv_account);//账号
        watch720_item = parentView.findViewById(R.id.ic_watch720_item);
        watch770_item = parentView.findViewById(R.id.ic_watch770_item);
        tvAccount.setText(UserSP.getInstance().getUserName(mContext));
        text_class = (TextView) parentView.findViewById(R.id.text_class_disable);//将上课禁用改为免打扰
        /*720*/
        parentView.findViewById(R.id.ll_phone_book).setOnClickListener(this);//电话本
        parentView.findViewById(R.id.ll_class_disabled).setOnClickListener(this);//上课禁用
        parentView.findViewById(R.id.ll_timer_switch).setOnClickListener(this);//定时开关机
        /*770系列*/
        parentView.findViewById(R.id.ll_phone_book1).setOnClickListener(this);//电话本
        parentView.findViewById(R.id.ll_class_disabled1).setOnClickListener(this);//上课禁用
        parentView.findViewById(R.id.ll_alarm_clock).setOnClickListener(this);//闹钟
        parentView.findViewById(R.id.ll_find_watch).setOnClickListener(this);//找手表
        parentView.findViewById(R.id.rl_account_information_card).setOnClickListener(this);//账号信息卡

        ll_mine_placeholder1 = (LinearLayout) parentView.findViewById(R.id.ll_mine_placeholder1);
        ll_mine_placeholder2 = (LinearLayout) parentView.findViewById(R.id.ll_mine_placeholder2);
        ll_mine_placeholder3 = (LinearLayout) parentView.findViewById(R.id.ll_mine_placeholder3);
        ll_mine_placeholder4 = (LinearLayout) parentView.findViewById(R.id.ll_mine_placeholder4);
        image_mine_placeholder1 = (ImageView) ll_mine_placeholder1.findViewById(R.id.image_mine_placeholder1);
        image_mine_placeholder2 = (ImageView) ll_mine_placeholder2.findViewById(R.id.image_mine_placeholder2);
        image_mine_placeholder3 = (ImageView) ll_mine_placeholder3.findViewById(R.id.image_mine_placeholder3);
        image_mine_placeholder4 = (ImageView) ll_mine_placeholder4.findViewById(R.id.image_mine_placeholder4);
        name_mine_placeholder1 = (TextView) ll_mine_placeholder1.findViewById(R.id.name_mine_placeholder1);
        name_mine_placeholder2 = (TextView) ll_mine_placeholder2.findViewById(R.id.name_mine_placeholder2);
        name_mine_placeholder3 = (TextView) ll_mine_placeholder3.findViewById(R.id.name_mine_placeholder3);
        name_mine_placeholder4 = (TextView) ll_mine_placeholder4.findViewById(R.id.name_mine_placeholder4);
        ll_mine_placeholder1.setOnClickListener(this);
        ll_mine_placeholder2.setOnClickListener(this);
        ll_mine_placeholder3.setOnClickListener(this);
        ll_mine_placeholder4.setOnClickListener(this);
        if (initData()) {
            lvSetting = (MyListview) parentView.findViewById(R.id.lv_setting);
            lvSetting.setFocusable(false);
            adapter = new MineAdapter(mContext, ResourceCallback.singleResource().getMineName(ranges, product_type), ResourceCallback.singleResource().getMineImage(ranges, product_type), ranges, protocol_type);
            lvSetting.setAdapter(adapter);
            lvSetting.setOnItemClickListener(this);
        }
    }

    /**
     * 初始化布局
     */
    private boolean initData() {
        if (null != mCurTracker) {
            strTrackerNo = mCurTracker.device_sn;
            ranges = mCurTracker.ranges;
            protocol_type = mCurTracker.protocol_type;
            product_type = mCurTracker.product_type;
        }
        if (requestUtl == null && mCurTracker != null)//网络请求对象
            requestUtl = new MineHttpRequestUtl(mContext, strTrackerNo);
        else if (requestUtl != null) requestUtl.refreshValue(strTrackerNo);//刷新设备号
        LogUtil.e("strTrackerNo=" + strTrackerNo + " ranges=" + ranges + " protocol_type=" + protocol_type + " product_type=" + product_type);
        watch720_item.setVisibility(View.GONE);
        watch770_item.setVisibility(View.GONE);

        if (5 == ranges) {//是手表时要加入手表时区设置
            if (protocol_type == 8)//litefamily 3G智能手表，只保留我的手表、设备管理、手表时区和语言
                return true;
            if (protocol_type == 5 || protocol_type == 6 || protocol_type == 7) {
                watch770_item.setVisibility(View.VISIBLE);//显示770系列布局
                ll_mine_placeholder1.setVisibility(View.VISIBLE);
                ll_mine_placeholder2.setVisibility(View.VISIBLE);
                ll_mine_placeholder3.setVisibility(View.INVISIBLE);
                ll_mine_placeholder4.setVisibility(View.INVISIBLE);
                text_class.setText(R.string.class_disabled);
                image_mine_placeholder1.setImageResource(R.drawable.icon_yuancgj);//远程关机
                name_mine_placeholder1.setText(getString(R.string.remote_shutdown));
                image_mine_placeholder2.setImageResource(R.drawable.icon_yuancjk);//远程监护
                name_mine_placeholder2.setText(getString(R.string.remote_monitoring));
                if ("30".equals(product_type)) {//k1  4g手表
                    ll_mine_placeholder3.setVisibility(View.VISIBLE);
                    ll_mine_placeholder4.setVisibility(View.VISIBLE);
                    image_mine_placeholder3.setImageResource(R.drawable.remote_record_image);//远程录音
                    name_mine_placeholder3.setText(getString(R.string.remote_recoder));
                    image_mine_placeholder4.setImageResource(R.drawable.remote_photo);//远程拍照
                    name_mine_placeholder4.setText(getString(R.string.remote_photo));
                } else if ("15".equals(product_type)) {//770s老人手表上课禁用改成免打扰
                    text_class.setText(R.string.no_disturbing);
                    ll_mine_placeholder2.setVisibility(View.INVISIBLE);
                } else if ("26".equals(product_type)) {//990老人手表上课禁用改成免打扰
                    ll_mine_placeholder3.setVisibility(View.VISIBLE);
                    image_mine_placeholder2.setImageResource(R.drawable.icon_take_medicine);//吃药提醒
                    name_mine_placeholder2.setText(getString(R.string.take_medicine));
                    image_mine_placeholder3.setImageResource(R.drawable.icon_qingjingmoshi);//情景模式
                    name_mine_placeholder3.setText(getString(R.string.scenery_mode));
                    text_class.setText(R.string.no_disturbing);
                } else if (mCurTracker.product_type.equals("31")) {
                    ll_mine_placeholder3.setVisibility(View.VISIBLE);
                    ll_mine_placeholder4.setVisibility(View.VISIBLE);
                    image_mine_placeholder3.setImageResource(R.drawable.ico_class_schedule);//课程表
                    image_mine_placeholder4.setImageResource(R.drawable.function_control);
                    name_mine_placeholder3.setText(getString(R.string.class_timetable_title));
                    name_mine_placeholder4.setText(getString(R.string.function_controy_title));
                }
            } else {
                watch720_item.setVisibility(View.VISIBLE);
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 设置昵称和头像后数据回传更新当前显示状态
     */
    public void onEventMainThread(ChatInfo event) {
        if (event != null)
            setUserInfomation(ChatUtil.userNickname, ChatUtil.userPhoto);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rl_account_information_card) {
            startActivity(new Intent(mContext, ChatInfoCardActivity.class));
            return;
        }
        if (mCurTracker == null)
            return;
        switch (view.getId()) {
            case R.id.rl_title_right://设置
                startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.ll_phone_book://电话本
                startActivity(new Intent(mContext, TelephoneBookActivity.class));
                break;
            case R.id.ll_class_disabled://上课禁用
                startActivity(new Intent(mContext, TimeSchoolSwitchActivity.class));
                break;
            case R.id.ll_timer_switch://定时开关机
                Intent timeSwitchIntent = new Intent(mContext, TimeSwitchActivity.class);
                timeSwitchIntent.putExtra(Constants.EXTRA_DEVICE_TYPE, Constants.EXTRA_DEVICE_TYPE_720);
                startActivity(timeSwitchIntent);
                break;
            case R.id.ll_phone_book1://电话本
                if (!product_type.equals("24") && !product_type.equals("30") && !mCurTracker.product_type.equals("31"))
                    startActivity(new Intent(mContext, TelephoneBookActivity.class));
                else
                    startActivity(new Intent(mContext, PhoneBookActivity.class));
                break;
            case R.id.ll_class_disabled1://上课禁用
                if ("26".equals(product_type))//老人手表
                    startActivity(new Intent(mContext, DisturbTimeSchoolActivity.class));
                else startActivity(new Intent(mContext, TimeSchoolSwitchActivity.class));
                break;

            case R.id.ll_alarm_clock://闹钟
                startActivity(new Intent(mContext, AlarmClockListActivity.class));
                break;
            case R.id.ll_find_watch://找手表
                requestUtl.setMineHttpRequest(2, null);
                break;

            case R.id.ll_mine_placeholder1://远程关机
                if (product_type.equals("30") || product_type.equals("31")) {//k1 790s 4g手表
                    if (popupWindowUtils == null)
                        popupWindowUtils = new PopupWindowUtils(mContext);
                    popupWindowUtils.popupWindRemoteShutdown(requestUtl);
                } else
                    requestUtl.remoteShutDowm(3);
                break;
            case R.id.ll_mine_placeholder2://吃药提醒，监护
                if (product_type.equals("26")) {//除了990和770s、720没有没有远程监护其他手表都有
                    if (DeviceExpiredUtil.advancedFeatures(mContext, mCurTracker, true))//判断是否需要付费
                        return;
                    startActivity(new Intent(mContext, MedicineRemindActivity.class));//吃药提醒
                } else {//k1 手表
                    requestUtl.setMineHttpRequest(4, null);//远程监护
                }
                break;

            case R.id.ll_mine_placeholder3://录音监听
                if (product_type.equals("30")) {
                    DialogUtil.show(mContext, R.string.remote_recoder, R.string.remote_recoder_hint, R.string.confirm,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    requestUtl.setMineHttpRequest(6, strTrackerNo);
                                    DialogUtil.dismiss();
                                }
                            }, R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    DialogUtil.dismiss();
                                }
                            });
                    return;
                } else if (product_type.equals("31")) {//课程表
                    startActivity(new Intent(mContext, ClassScheduleActivity.class));
                    return;
                }
                startActivity(new Intent(mContext, SceneryModeActivity.class));
                break;
            case R.id.ll_mine_placeholder4:
                if (product_type.equals("30")) {
                    startActivity(new Intent(mContext, RemoteTakePhotoActivity.class));//远程拍照
                } else if (product_type.equals("31")) {//790s
                    startActivity(new Intent(mContext, FunctionControlActivity.class));
                }
                break;
        }
    }

    /**
     * 微聊信息头像和昵称设置
     */
    private void setUserInfomation(String nickName, String userPhoto) {
        LogUtil.e("url==" + userPhoto + "nickName==" + nickName);
        if (!TextUtils.isEmpty(nickName)) {
            tvNickName.setText(nickName);
        }
        if (!TextUtils.isEmpty(userPhoto)) {
            Glide.with(mContext.getApplicationContext()).load(userPhoto).dontAnimate().error(R.drawable.img_defaulthead_628).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(ivTrackerImage);
        } else ivTrackerImage.setImageResource(R.drawable.img_defaulthead_628);
    }

    /**
     * 下方条目点击事件监听
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        if (mCurTracker == null) {
            DialogUtil.showAddDevice(mContext);
            return;
        }
        Log.e("TAG", "rang=" + ranges + " protocol_type=" + protocol_type + " product_type=" + product_type);
        switch (position) {
            case 0://我的设备，我的手表//信息卡页面
                if (protocol_type == 8 && product_type.equals("29")) {//litefamily
                    startActivity(new Intent(mContext, DeviceManagement.class));
                    return;
                }
                Intent trackerEditIntent = new Intent(mContext, TrackerEditActivity.class);
                trackerEditIntent.putExtra(Constants.EXTRA_TRACKER, mCurTracker);
                trackerEditIntent.putExtra("fromwhere", Constants.MINEACTIVITY);
                startActivity(trackerEditIntent);
                break;
            case 1://设备管理
                if (protocol_type == 8 && product_type.equals("29")) {//成员管理
                    if (Utils.isSuperUser(mCurTracker, mContext)) {
                        if (DeviceExpiredUtil.advancedFeatures(mContext, mCurTracker, true))//判断是否付费
                            return;
                    }
                    startActivity(new Intent(mContext, MemberManagementActivity.class));
                    return;
                }
                startActivity(new Intent(mContext, DeviceManagement.class));
                break;
            case 2://成员管理
                if (protocol_type == 8 && product_type.equals("29")) {//litefamily  3G智能手表，只保留我的手表、设备管理、报警设置，App时区设置
                    Intent alarmIntent = new Intent(mContext, AlarmSwitchActivity.class);
                    alarmIntent.putExtra(Constants.EXTRA_TRACKER, mCurTracker);
                    startActivity(alarmIntent);
                    return;
                }

                if (Utils.isSuperUser(mCurTracker, mContext)) {
                    if (DeviceExpiredUtil.advancedFeatures(mContext, mCurTracker, true))//判断是否付费
                        return;
                    startActivity(new Intent(mContext, MemberManagementActivity.class));
                }
                break;
            case 3:
                if (ranges == 6 && "16".equals(product_type)) {//OBD有WIFI WIFI设置
                    startActivity(new Intent(mContext, WifiSettingActivity.class));
                } else {//报警设置
                    Intent alarmIntent = new Intent(mContext, AlarmSwitchActivity.class);
                    alarmIntent.putExtra(Constants.EXTRA_TRACKER, mCurTracker);
                    startActivity(alarmIntent);
                }
                break;
            case 4:
                if (ranges == 6 && "16".equals(product_type)) {// OBD有WIFI//报警设置
                    Intent alarmIntent = new Intent(mContext, AlarmSwitchActivity.class);
                    alarmIntent.putExtra(Constants.EXTRA_TRACKER, mCurTracker);
                    startActivity(alarmIntent);

                } else if (product_type.equals("30")) {//k1手表运动计步
                    Intent stepIntent = new Intent(mContext, SportStepActivity.class);
                    startActivity(stepIntent);
                } else {// 时区设置//
                    Intent zoneIntent = new Intent(mContext, TimeZoneActivity.class);
                    zoneIntent.putExtra(Constants.EXTRA_TRACKER, mCurTracker);
                    startActivity(zoneIntent);
                }

                break;
            case 5://ranges为5时是手表时区设置,其他是高级设置,7为//通知推送
                if (ranges == 6 && "16".equals(product_type)) {//OBD有WIFI//时区设置//
                    Intent zoneIntent = new Intent(mContext, TimeZoneActivity.class);
                    zoneIntent.putExtra(Constants.EXTRA_TRACKER, mCurTracker);
                    startActivity(zoneIntent);
                } else if (5 == ranges) {
                    if ("30".equals(product_type)) {//k1手表直接进入定位频率
                        Intent positionIntent = new Intent(mContext, TimePositionActivity.class);
                        positionIntent.putExtra(Constants.EXTRA_TRACKER, mCurTracker);
                        startActivity(positionIntent);
                    } else {
                        Intent zoneWatchIntent = new Intent(mContext, TimeZoneWatchActivity.class);
                        zoneWatchIntent.putExtra(Constants.EXTRA_TRACKER, mCurTracker);
                        startActivity(zoneWatchIntent);
                    }
                } else {// 关于我们
                    startActivity(new Intent(mContext, MoreActivity.class));
                }

                break;
            case 6:
                if (5 == ranges) {
                    if (protocol_type == 5 || protocol_type == 6 || protocol_type == 7) {
                        if (product_type.equals("22") || product_type.equals("25")) {//product_type.equals("23") ||
                            startActivity(new Intent(mContext, MoreActivity.class));//该两款设备没有计步设置，高级设置往上移动一个item
                        } else if ("24".equals(product_type) || mCurTracker.product_type.equals("31")) {
                            startActivity(new Intent(mContext, SportStepActivity.class));
                        } else {
                            startActivity(new Intent(mContext, StepSettingActivity.class));
                        }
                    } else {
                        startActivity(new Intent(mContext, MoreActivity.class));
                    }
                } else if (7 == ranges) {// 目标设置
                    startActivity(new Intent(mContext, targetSettingActivity.class));
                } else {//高级设置
                    startActivity(new Intent(mContext, MoreActivity.class));
                }
                break;
            case 7://高级设置
                if (5 == ranges && (protocol_type == 5 || protocol_type == 7)) {
                    startActivity(new Intent(mContext, MoreActivity.class));
                } else if (5 == ranges && ("24".equals(product_type) || mCurTracker.product_type.equals("31"))) {
                    Intent positionIntent = new Intent(mContext, TimePositionActivity.class);
                    positionIntent.putExtra(Constants.EXTRA_TRACKER, mCurTracker);
                    startActivity(positionIntent);
                }
                break;
        }
    }
}
