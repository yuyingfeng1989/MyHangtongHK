package com.bluebud.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.bluebud.activity.settings.SettingActivity;
import com.bluebud.adapter.TrackerAdapter;
import com.bluebud.app.App;
import com.bluebud.app.AppManager;
import com.bluebud.chat.utils.ChatUtil;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.fragment.AmapTabLocationFragment;
import com.bluebud.fragment.GmapTabLocationMapFragment;
import com.bluebud.fragment.TabMineFragment;
import com.bluebud.fragment.TabPeripheryFragment;
import com.bluebud.fragment.TestFragment;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.Alarm;
import com.bluebud.info.DeviceStatusInfo;
import com.bluebud.info.PushAlarmInfo;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.SystemNoticeObj;
import com.bluebud.info.Tracker;
import com.bluebud.info.User;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.service.EventsService;
import com.bluebud.utils.Constants;
import com.bluebud.utils.DeviceExpiredUtil;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogIntroduceUtil.OnProgressDialogDismissListener;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.utils.request.DownloadApkUtil;
import com.bluebud.utils.request.RequestLocationUtil;
import com.bluebud.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.CommandNotificationMessage;

public class MainActivity extends FragmentActivity implements OnClickListener,
        OnProgressDialogClickListener,
        OnProgressDialogDismissListener, RongIM.UserInfoProvider,
        RongIM.GroupInfoProvider {//OnCheckedMenuItemListener,
    public static MainActivity mainActivity;

    private FragmentTabHost mTabHost;
    private LinearLayout ll_tab_bg;//底部导航栏整体布局
    private LinearLayout[] linearLayouts;
    private ImageView[] imageViews;
    private TextView[] textViews;
    private View tabhos_line;
    private int iDefaultHeadIconDrawableID;

    private AlertDialog mDialog;

    private Integer[] drawables = new Integer[]{
            R.drawable.tab_home_selector,
            R.drawable.tab_periphery_selector,
            R.drawable.tab_mall_selector, R.drawable.tab_mine_selector,
    };
    private Integer[] obddrawables = new Integer[]{
            R.drawable.obd_tab_home_selector,
            R.drawable.obd_tab_periphery_selector,
            R.drawable.obd_tab_mall_selector, R.drawable.obd_tab_mine_selector,
    };
    private Integer[] strings = new Integer[]{R.string.home,
            R.string.periphery, R.string.mall, R.string.mine,
    };

    private Class[] fragments1 = {AmapTabLocationFragment.class,
            TabPeripheryFragment.class, TestFragment.class,
            TabMineFragment.class,
    };
    private Class[] fragments2 = {GmapTabLocationMapFragment.class,
            TabPeripheryFragment.class,
            TestFragment.class, TabMineFragment.class,//TabShoppingFragment
    };

    private Class[] fragments;
    private int size;

    private List<Tracker> trackerLists;
    private Tracker mCurTracker;
    private int iType = 1;// 设备类型,1.表示人 2表示宠物，3.表示汽车4.表示摩托车,5表示手表
    private boolean iswalkaround = false;
    private boolean bInterruptPower = false;
    private String sInterruptPower = "";

    private View vTitle;
    private LinearLayout llTitleLeft;
    private LinearLayout llTitleRight;
    private TextView tvTitle;
    private TextView tvTitleSubject;
    private ImageView ivDownUp;
    private PopupWindow popupWindow;
    private RequestHandle requestHandle;
    private InputMethodManager imm;

    private static OnTabClickListener mOnTabClickListener;
    private static OnChangeListener mOnChangeListener;
    private ImageView ivRightMenu;
    private CircleImageView ivDefaultHead;
    private String mCurCount;
    private int aroundRanges;
    private boolean isMineposition = false;
    private int tabPosition = 0;
    private String url;//注册服务起接口

    public interface OnTabClickListener {// 这些接口都在TabLocationBaiduFrament和TabLocationGooleFrament

        public void onLocationClick();// 定位接口
    }

    public interface OnChangeListener {// 这些接口都在TabLocationBaiduFrament和TabLocationGooleFrament

        public void onChangeTracker(int position);// 改变设备接口

        public void onChangeTrackerClear();
    }

    public static void setClickListener(OnTabClickListener onTabClickListener, OnChangeListener onChangeListener) {
        mOnTabClickListener = onTabClickListener;
        mOnChangeListener = onChangeListener;
    }

    // 719设备休眠状态定时器
    private final static int TIMING = 10 * 1000;
    private Handler timingHandler = new Handler();
    private Runnable timingRunnable = new Runnable() {

        @Override
        public void run() {
            getOnLineStatus();
            timingHandler.postDelayed(this, TIMING);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        SoftReference<MainActivity> weakReference = new SoftReference<MainActivity>(MainActivity.this);
        mainActivity = weakReference.get();
        url = UserUtil.getServerUrl(mainActivity);
        init();
        Intent intent = new Intent(this, EventsService.class);  // 闹钟
        startService(intent);
        AppManager.getAppManager().finishAllActivity();
        AppManager.getAppManager().addActivity(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (ChatUtil.token == null)
            ChatUtil.token = UserSP.getInstance().getToken(MainActivity.this);
        initeViewData();
    }

    private void initeViewData() {
        RongIM.setUserInfoProvider(mainActivity, true);// 用户信息内容提供者
        RongIM.setGroupInfoProvider(mainActivity, true);// 群组信息内容提供者
        EventBus.getDefault().register(this);

        ChatUtil.connect(getApplicationInfo(), getApplicationContext());// 微信聊天连接
        checkAPPSystemNotice();// 从服务器查询是否有系统消息
        new DownloadApkUtil(mainActivity).checkAPPUpdate(url, true);
    }

    @Override
    protected void onResume() {
        // //得到手机屏幕宽高
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        AppSP.getInstance().saveScreenWidth(this, width);
        AppSP.getInstance().saveAdHeight(this, width / 3);
        super.onResume();
        if (iswalkaround) {
            iswalkaround = false;
            tabChange(0);
            mTabHost.setCurrentTab(0);
            mOnTabClickListener.onLocationClick();
            DeviceExpiredUtil.advancedFeatures(mainActivity, mCurTracker, false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Constants.isPay = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (Constants.isPay)
            mCurTracker = UserUtil.getCurrentTracker(mainActivity);
        if (RongIM.getInstance().getCurrentConnectionStatus().getValue() != 0) {//判断融云是否连接成功
            LogUtil.e("重新连接融云=" + ChatUtil.token);
            ChatUtil.connect(getApplicationInfo(), getApplicationContext());
        }
    }

    /**
     * 更新title
     */
    public void showRefreshTitle() {
        if (tvTitle == null)
            return;
        if (Utils.isEmpty(UserUtil.getTitleTrackerName(mCurTracker))) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(UserUtil.getTitleTrackerName(mCurTracker));
        }
    }

    /**
     * 2016-03-30 add zengms 点击输入框以外其他地方，软键盘消失
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (this.getCurrentFocus() != null) {
                if (this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }


    @Override
    protected void onDestroy() {
        LogUtil.i("main onDestroy");
        EventBus.getDefault().unregister(this);
        unregisterReceiver(broadcastReceiver);
        timingHandler.removeCallbacks(timingRunnable);
        timingHandler.removeCallbacksAndMessages(null);
        stopService(new Intent(this, EventsService.class));
        ChatUtil.userPhoto = null;
        ChatUtil.userNickname = null;
        ChatUtil.token = null;
        super.onDestroy();
        RequestLocationUtil.getRequestLocation(mainActivity).releaseMemory();//释放轮询位置资源
    }

    @Override
    public void onBackPressed() {// 回到系统页面
        moveTaskToBack(true);
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        startActivity(intent);
    }

    private void init() {
        mCurCount = UserSP.getInstance().getUserName(MainActivity.this);
        mCurTracker = UserUtil.getCurrentTracker(this);
        if (null != mCurTracker) {
            iType = mCurTracker.ranges;
            aroundRanges = mCurTracker.around_ranges;
        }
        initTopView();
        initTabView();
        regesterBroadcast();
        if (null == mCurTracker) {
            DialogUtil.showAddDevice(this);// 增加设备对话框
        }
    }


    private void initTopView() {
        vTitle = findViewById(R.id.rl_title);
        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left_menu);
        ivDefaultHead = (CircleImageView) findViewById(R.id.iv_title_left_menu);//默认头像
        ivDownUp = (ImageView) findViewById(R.id.iv_down_up);//

        tvTitle = (TextView) findViewById(R.id.tv_title);//标题
        tvTitleSubject = (TextView) findViewById(R.id.tv_title_subject);
        llTitleRight = (LinearLayout) findViewById(R.id.ll_title_right_setting);
        ivRightMenu = (ImageView) findViewById(R.id.iv_title_right_menu);//右侧图标

        llTitleLeft.setOnClickListener(this);
        llTitleRight.setOnClickListener(this);

        // 显示设备号
        if (mCurTracker != null) {
            tvTitleSubject.setText(mCurTracker.device_sn);
        }
        setTrackerHead();
    }

    /**
     * 设置右侧图标
     */
    private void setRightIcon(boolean isReadMessage) {
        if (isReadMessage) {
            ivRightMenu.setImageResource(R.drawable.menu_new_information_selector);
        } else {
            ivRightMenu.setImageResource(R.drawable.menu_setting_selector);
        }
    }


    /**
     * 显示头像是否在线
     */
    private void setHeadOnlineOrOffline(boolean online) {
        ColorMatrix matrix = new ColorMatrix();
        if (online) {
            matrix.setSaturation(1);//1表示原图，设备在线
        } else {
            matrix.setSaturation(0);//0表示灰色图，设备不在线
        }
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        ivDefaultHead.setColorFilter(filter);
    }

    private void initTabView() {

        if (App.getMapType() == App.MAP_TYPE_AMAP) {
            fragments = fragments1;//地图为高德地图，确保安全发一下广播让google地图onDestory
            sendBroadcast(new Intent(Constants.ACTION_MAP_GOOGLE_ONDESTORY));
            LogUtil.i("fragments1");
        } else {
            fragments = fragments2;//地图为google地图，确保安全发一下广播让百度地图onDestory
            sendBroadcast(new Intent(Constants.ACTION_MAP_BAIDU_ONDESTORY));
            LogUtil.i("fragments2");
        }
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabhos_line = findViewById(R.id.tabhost_line);
        ll_tab_bg = findViewById(R.id.ll_tab_bg);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        int count = fragments.length;
        for (int i = 0; i < count; i++) {
            TabSpec tabSpec = mTabHost.newTabSpec(i + "").setIndicator(i + "");
            mTabHost.addTab(tabSpec, fragments[i], null);
        }
        mTabHost.setCurrentTab(0);// 默认第一个条目
        linearLayouts = new LinearLayout[]{
                (LinearLayout) findViewById(R.id.layout_tab0),// 首页
                (LinearLayout) findViewById(R.id.layout_tab1),// 周边
                (LinearLayout) findViewById(R.id.layout_tab2),// 商城
                (LinearLayout) findViewById(R.id.layout_tab3)// 我的
        };


        size = linearLayouts.length;
        imageViews = new ImageView[size];
        textViews = new TextView[size];
        for (int i = 0; i < size; i++) {
            linearLayouts[i].setOnClickListener(this);
            imageViews[i] = (ImageView) linearLayouts[i].findViewById(R.id.iv_tab);
            textViews[i] = (TextView) linearLayouts[i].findViewById(R.id.tv_tab);
            textViews[i].setText(strings[i]);
//            if (i != 2)
//                textViews[i].setText(strings[i]);
//            else if(i==2&&iType==6) {
//                textViews[i].setVisibility(View.VISIBLE);
//                textViews[i].setText(strings[i]);
//            }else textViews[i].setVisibility(View.GONE);
        }
        changeTabStyle();//初始化改变导航栏
        tabChange(0);// 初始化默认第一个条目
    }


    /**
     * 改变底部 导航栏显示
     */
    private void tabChange(int position) {
        if (position == 0) {//首页显示左右图标
            llTitleLeft.setVisibility(View.VISIBLE);
            llTitleRight.setVisibility(View.VISIBLE);
            // 显示设备号
            if (mCurTracker != null) {
                tvTitleSubject.setText(mCurTracker.device_sn);
                tvTitleSubject.setTextSize(12);
            } else {
                tvTitleSubject.setText(R.string.app_name);
                tvTitleSubject.setTextSize(18);
                setTitleLeftHeadImage(R.drawable.img_defaulthead_628);
            }
            isMineposition = false;
        } else if (position == 1) {//周边
            llTitleLeft.setVisibility(View.GONE);
            llTitleRight.setVisibility(View.GONE);
            tvTitleSubject.setText(R.string.periphery);
            tvTitleSubject.setTextSize(18);
            tvTitle.setVisibility(View.GONE);
            isMineposition = false;
        } else if (position == 2) {//商城
            llTitleLeft.setVisibility(View.GONE);
            llTitleRight.setVisibility(View.GONE);
            tvTitleSubject.setTextSize(18);
            tvTitleSubject.setText(R.string.mall);
            tvTitle.setVisibility(View.GONE);
            isMineposition = false;
        } else if (position == 3) {
            llTitleLeft.setVisibility(View.GONE);
            llTitleRight.setVisibility(View.VISIBLE);
            tvTitleSubject.setTextSize(18);
            tvTitleSubject.setText(R.string.mine);
            tvTitle.setVisibility(View.GONE);
            ivRightMenu.setImageResource(R.drawable.btn_settings_selector);
            isMineposition = true;
        }

        for (int i = 0; i < size; i++) {
            if (position == i) {
                imageViews[i].setSelected(true);
                if (iType == 6) {
                    textViews[i].setTextColor(getResources().getColor(R.color.e4e4e4));
                } else {
                    textViews[i].setTextColor(getResources().getColor(R.color.text_theme));
                }
//                textViews[i].setSelected(true);
            } else {
                imageViews[i].setSelected(false);
                textViews[i].setTextColor(getResources().getColor(R.color.text_gary));
//                textViews[i].setSelected(false);
            }
        }
    }

    //设置标题头像
    public void setTitleLeftHeadImage(int headImage) {
        ivDefaultHead.setImageResource(headImage);
    }


    /**
     * type: 1:追踪器切换 2:当前追踪器修改
     */
    public void changeTracker(int type) {
        mCurTracker = UserUtil.getCurrentTracker(this);
        showRefreshTitle();//刷新title
        if (null == mCurTracker) {
            tvTitleSubject.setText(getResources().getString(R.string.app_name));
            iType = 1;
            if (type == 1) {
                if (null != mOnChangeListener) {
                    mOnChangeListener.onChangeTracker(0);
                    DeviceExpiredUtil.advancedFeatures(mainActivity, mCurTracker, false);
                }
            }
            return;
        }
        iType = mCurTracker.ranges;
        aroundRanges = mCurTracker.around_ranges;
        if (type == 1) {
            if (null != mOnChangeListener) {
                mOnChangeListener.onChangeTracker(0);
                DeviceExpiredUtil.advancedFeatures(mainActivity, mCurTracker, false);
            }
        }

        // 显示设备号
        tvTitleSubject.setText(mCurTracker.device_sn);
        if (Utils.serialNumberRange719(iType, mCurTracker.device_sn)) {
            timingHandler.postDelayed(timingRunnable, TIMING);
        } else {
            timingHandler.removeCallbacks(timingRunnable);
        }
        setTrackerHead();//设置头像
        if (tabPosition == 0) {
            tabChange(0);
        } else if (tabPosition == 1) {
            tabChange(1);
        } else if (tabPosition == 2) {
            tabChange(2);
        } else if (tabPosition == 3) {
            tabChange(3);
        }
        changeTabStyle();//改变底部导航栏样式
    }

    /**
     * 设置底部导航栏样式变化
     */
    private void changeTabStyle() {
        if (iType == 6) {
            ll_tab_bg.setBackgroundColor(getResources().getColor(R.color.black));
//            textViews[2].setVisibility(View.VISIBLE);
//            textViews[2].setText(strings[2]);
            tabhos_line.setVisibility(View.GONE);
            vTitle.setBackgroundColor(getResources().getColor(R.color.black));
        } else {
            ll_tab_bg.setBackgroundColor(getResources().getColor(R.color.white));
//            textViews[2].setVisibility(View.GONE);
            tabhos_line.setVisibility(View.VISIBLE);
            vTitle.setBackgroundColor(getResources().getColor(R.color.bg_theme));
        }
        for (int i = 0; i < size; i++) {
            if (iType == 6) {
                imageViews[i].setBackgroundResource(obddrawables[i]);
            } else {
                imageViews[i].setBackgroundResource(drawables[i]);
            }
        }
    }

    /**
     * 设置显示的第一个头像
     */
    private void setTrackerHead() {
        if (null == mCurTracker) {
            return;
        }

        ivDefaultHead.setImageDrawable(null);
        ivDefaultHead.setBackgroundDrawable(null);
        if (2 == iType) {
            iDefaultHeadIconDrawableID = R.drawable.image_pet;
        } else if (3 == iType || 6 == iType) {
            iDefaultHeadIconDrawableID = R.drawable.image_car;
        } else if (4 == iType) {
            iDefaultHeadIconDrawableID = R.drawable.image_motorcycle;
        } else if (1 == iType) {
            iDefaultHeadIconDrawableID = R.drawable.image_preson_sos;
        } else {
            iDefaultHeadIconDrawableID = R.drawable.image_watch;
        }
        ivDefaultHead.setImageResource(iDefaultHeadIconDrawableID);

        if (!Utils.isEmpty(mCurTracker.head_portrait)) {
            String url = Utils.getImageUrl(MainActivity.this) + mCurTracker.head_portrait;
            LogUtil.i("头像的地址的前头：" + Utils.getImageUrl(MainActivity.this));
            ivDefaultHead.setImageDrawable(null);
            Glide.with(this).load(url).dontAnimate().placeholder(iDefaultHeadIconDrawableID).error(iDefaultHeadIconDrawableID).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(ivDefaultHead);
        }

        boolean deviceOnLine = AppSP.getInstance().isDeviceOnLine(MainActivity.this);
        if (deviceOnLine) {
            setHeadOnlineOrOffline(true);
        } else {
            setHeadOnlineOrOffline(false);
        }
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_tab0:// 首页点名
                tabChange(0);
                mTabHost.setCurrentTab(0);
                if (null != mOnTabClickListener)
                    mOnTabClickListener.onLocationClick();
                DeviceExpiredUtil.advancedFeatures(mainActivity, mCurTracker, false);
                setRightIcon(Constants.isNewMessage);
                tabPosition = 0;
                break;
            case R.id.layout_tab1:// 周边
                tabChange(1);
                mTabHost.setCurrentTab(1);
                tabPosition = 1;
                break;
            case R.id.layout_tab2:// 商城
                Intent intentMall = new Intent(this, MallActivity.class);
                startActivityForResult(intentMall, 2);
//                tabChange(2);
//                mTabHost.setCurrentTab(2);
//                tabPosition = 2;

                break;
            case R.id.layout_tab3:// 我的
                tabChange(3);
                mTabHost.setCurrentTab(3);
                tabPosition = 3;
                break;

            case R.id.ll_title_left_menu:// 左侧标题 点击显示所有绑定设备列表
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    ivDownUp.setImageResource(R.drawable.arrow_down);
                    return;
                }
                showTrackerListPopupWindow(vTitle);
                break;
            case R.id.ll_title_right_setting:// 右内里标题,显示警情设置 在我的页面时进入设置
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    ivDownUp.setImageResource(R.drawable.arrow_down);
                }
                if (isMineposition) {
                    startActivity(new Intent(MainActivity.this, SettingActivity.class));
                } else {
                    Intent alarmIntent = new Intent(MainActivity.this, AlarmListActivity.class);
                    startActivity(alarmIntent);
                    Constants.isNewMessage = false;
                    setRightIcon(Constants.isNewMessage);
                }

                break;
            case R.id.ll_tracker_del:// 删除设备
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }

                if (null == mCurTracker) {
                    DialogUtil.showAddDevice(this);
                    return;
                }

                DialogUtil.show(MainActivity.this, R.string.unbind,
                        R.string.notice_unbind, R.string.confirm,
                        new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                DialogUtil.dismiss();

                                if (UserUtil.isGuest(MainActivity.this)) {
                                    ToastUtil.show(MainActivity.this,
                                            R.string.guest_no_set);
                                    return;
                                }
                                cancelAuthNetworkConnect();
                            }
                        }, R.string.cancel, new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                DialogUtil.dismiss();
                            }
                        });
                break;
            case R.id.ll_tracker_add:// 增加设备
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }

                Intent intent = new Intent(MainActivity.this, BindListActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_people:
                showTrackerDialog(1);
                break;
            case R.id.iv_pet:
                showTrackerDialog(2);
                break;
            case R.id.iv_car:
                showTrackerDialog(3);
                break;
            case R.id.iv_motor:
                showTrackerDialog(4);
                break;
            case R.id.ll_search_edit:// 搜索设备
                Intent intentSearch = new Intent(MainActivity.this, TrackerSearchActivity.class);
                startActivityForResult(intentSearch, 1);
                break;
            case R.id.v_shadow:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                break;
        }
    }

    // 搜索设备回调的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            int position = data.getIntExtra("POSITION", 0);
            LogUtil.i("position:" + position);
            if (!trackerLists.get(position).device_sn.equals(mCurTracker.device_sn)) {
                UserUtil.saveCurrentTracker(MainActivity.this, trackerLists.get(position));
                changeTracker(1);
                tabChange(0);
            }
        } else if (requestCode == 2) {
            iswalkaround = true;
        }
    }

    @Override
    public void onProgressDialogBack() {
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }

    @Override
    public void onDismiss() {
        if (null == mCurTracker) {
            DialogUtil.showAddDevice(this);
        }
    }

    /**
     * 注册广播
     */
    private void regesterBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_TRACTER_CHANGE);// 在绑定设备和
        filter.addAction(Constants.ACTION_TRACTER_RANGES_CHANGE);// UserInfoActivity发出广播改变设备使用类型
        filter.addAction(Constants.ACTION_TRACTER_NICKNAME_CHANGE);// TrackerEditActivity发出广播
        filter.addAction(Constants.ACTION_ACC_STATUS);// //Notifier发出广播
        filter.addAction(Constants.ACTION_MAP_SWITCH);//地图切换
        filter.addAction(Constants.ACTION_ONLINE_TO_CHANGE_MAIN);//头像在线状态
        filter.addAction(Constants.ACTION_TRACTER_PICTURE_CHANGE);//信息卡头像改变
        filter.addAction(Constants.ACTION_CLOCK);
        filter.addAction(Constants.ACTION_CLOCK_CLEAR);
        filter.addAction(Constants.ACTION_TRACTER_ENTER_MAIN);
        filter.addAction(Constants.ACTION_TIME_SWITCH);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);//系统语言切换
        registerReceiver(broadcastReceiver, filter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            LogUtil.i(intent.getAction());

            if (intent.getAction().equals(Constants.ACTION_MAP_SWITCH)) {
                LogUtil.e("小二，地图要切换了");
                LogUtil.e("mainActivity切换的地图:" + UserSP.getInstance().getServerAndMap(MainActivity.this));
                if (0 == UserSP.getInstance().getServerAndMap(MainActivity.this)) {
                    //地图为百度地图，确保安全发一下广播让google地图onDestory
                    sendBroadcast(new Intent(Constants.ACTION_MAP_GOOGLE_ONDESTORY));
                } else {
                    //地图为google地图，确保安全发一下广播让百度地图onDestory
                    sendBroadcast(new Intent(Constants.ACTION_MAP_BAIDU_ONDESTORY));
                }
                finish();
            } else if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {////系统语言切换
                LogUtil.i("系统语言切换啦");
                //系统语言切换时，要把整个应用全退出，为了防止再入进入时发生闪退的现像.
                finish();
                AppManager.getAppManager().finishAllActivity();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            } else if (intent.getAction().equals(Constants.ACTION_ONLINE_TO_CHANGE_MAIN)) {//头像在线状态发出改变
                boolean deviceOnLine = AppSP.getInstance().isDeviceOnLine(MainActivity.this);
                if (deviceOnLine) {
                    setHeadOnlineOrOffline(true);
                } else {
                    setHeadOnlineOrOffline(false);
                }
            } else if (intent.getAction().equals(Constants.ACTION_TRACTER_ENTER_MAIN)) {
                iswalkaround = true;
            } else if (intent.getAction().equals(Constants.ACTION_TRACTER_PICTURE_CHANGE)) {//信息卡头像发出改变
                mCurTracker = UserUtil.getCurrentTracker(MainActivity.this);
                setTrackerHead();
            } else if (intent.getAction().equals(Constants.ACTION_TRACTER_CHANGE)) {
                changeTracker(1);// 1:追踪器切换 2:当前追踪器修改
            } else if (intent.getAction().equals(Constants.ACTION_TIME_SWITCH)) {
                mCurTracker = UserUtil.getCurrentTracker(MainActivity.this);
            } else if (intent.getAction().equals(
                    Constants.ACTION_TRACTER_RANGES_CHANGE)) {
                changeTracker(2);
            } else if (intent.getAction().equals(Constants.ACTION_TRACTER_NICKNAME_CHANGE)) {
                changeTracker(2);
            } else if (intent.getAction().equals(Constants.ACTION_ACC_STATUS)) {
                final PushAlarmInfo pushAlarmInfo = (PushAlarmInfo) intent
                        .getSerializableExtra("PUSH_ALARM_INFO");

                if (1 == mCurTracker.defensive && 1 == pushAlarmInfo.accon) {
                    if (null != mDialog && mDialog.isShowing()) {
                        return;
                    }
//-------------------------------
                    @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) String msg = getString(R.string.interrupt_power_prompt_content, pushAlarmInfo.localDateTime);
                    mDialog = DialogUtil.showSystemAlert1(MainActivity.this,
                            R.string.interrupt_power_prompt, msg,
                            R.string.confirm, new OnClickListener() {

                                @Override
                                public void onClick(View arg0) {
                                    DialogUtil.dismiss();

                                    sendDefensiveOrder(pushAlarmInfo.equipId);
                                }
                            }, R.string.cancel, new OnClickListener() {

                                @Override
                                public void onClick(View arg0) {
                                    // TODO Auto-generated method stub
                                    DialogUtil.dismiss();
                                }
                            });
                }
            }
        }

        ;
    };

    private void showTrackerDialog(int type) {
        String sMsg = getString(R.string.tracker_prompt_people);
        if (4 == type) {
            sMsg = getString(R.string.tracker_prompt_motor);
        } else if (3 == type) {
            sMsg = getString(R.string.tracker_prompt_car);
        } else if (2 == type) {
            sMsg = getString(R.string.tracker_prompt_pet);
        } else {
            sMsg = getString(R.string.tracker_prompt_people);
        }
        DialogUtil.show(this, R.string.prompt, sMsg, R.string.go_shopping,
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        DialogUtil.dismiss();
                        startActivity(new Intent(MainActivity.this, ShoppingActivity.class));
                    }
                }, R.string.cancel, new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        DialogUtil.dismiss();
                    }
                });
    }

    /**
     * 显示绑定的追踪器列表（可删除、可添加）
     *
     * @param parent
     */
    private void showTrackerListPopupWindow(View parent) {
        trackerLists = UserUtil.getUserInfo(this).device_list;

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_tracker_list, null);

        LinearLayout llSearch = (LinearLayout) view.findViewById(R.id.ll_search);
        LinearLayout llSearchEdit = (LinearLayout) view.findViewById(R.id.ll_search_edit);
        LinearLayout llDel = (LinearLayout) view.findViewById(R.id.ll_tracker_del);
        LinearLayout llAdd = (LinearLayout) view.findViewById(R.id.ll_tracker_add);
        View vShadow = view.findViewById(R.id.v_shadow);
        ImageView ivPeople = (ImageView) view.findViewById(R.id.iv_people);
        ImageView ivPet = (ImageView) view.findViewById(R.id.iv_pet);
        ImageView ivCar = (ImageView) view.findViewById(R.id.iv_car);
        ImageView ivMotor = (ImageView) view.findViewById(R.id.iv_motor);
        final ListView lvTracker = (ListView) view.findViewById(R.id.lv_tracker);
        if (iType == 6)
            llSearch.setBackgroundColor(getResources().getColor(R.color.black));
        else llSearch.setBackgroundColor(getResources().getColor(R.color.bg_theme));
        if (trackerLists.size() > 10) {
            llSearch.setVisibility(View.VISIBLE);
        } else {
            llSearch.setVisibility(View.GONE);
        }

        TrackerAdapter adapter = new TrackerAdapter(this, trackerLists);
        lvTracker.setAdapter(adapter);

        llSearchEdit.setOnClickListener(this);
        llDel.setOnClickListener(this);
        llAdd.setOnClickListener(this);
        vShadow.setOnClickListener(this);
        ivPeople.setOnClickListener(this);
        ivPet.setOnClickListener(this);
        ivCar.setOnClickListener(this);
        ivMotor.setOnClickListener(this);

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        popupWindowInitParams(adapter, lvTracker, windowManager);//动态设置设备列表高度
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                ivDownUp.setImageResource(R.drawable.arrow_down);
            }
        });
        showAsDropDown(popupWindow, windowManager, parent);//显示popuwindow位置

        lvTracker.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                if (!trackerLists.get(position).device_sn.equals(mCurTracker.device_sn)) {
                    UserUtil.saveCurrentTracker(MainActivity.this, trackerLists.get(position));
                    changeTracker(1);
                    tabChange(0);
                }
            }
        });
        ivDownUp.setImageResource(R.drawable.arrow_up);
    }

    /**
     * 动态计算popupwindow中布局大小
     */
    private void popupWindowInitParams(TrackerAdapter adapter, ListView lvTracker, WindowManager wm) {
        int size = trackerLists.size();
        if (size == 0)
            return;
        int height = wm.getDefaultDisplay().getHeight() / 3;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, lvTracker);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        LayoutParams params = lvTracker.getLayoutParams();
        int paramHeight = totalHeight + (lvTracker.getDividerHeight() * size);
        if (paramHeight > height)
            params.height = height;
        else
            params.height = paramHeight;
        lvTracker.setLayoutParams(params);
    }

    /**
     * popuwind解决在7.0以上版本设置位置无效的解决
     *
     * @param parent 相对某个控件的位置
     */
    private void showAsDropDown(PopupWindow pw, WindowManager wm, View parent) {
        if (Build.VERSION.SDK_INT >= 24) {
            int[] location = new int[2];
            parent.getLocationOnScreen(location);
            if (Build.VERSION.SDK_INT == 25) {// 7.1 版本处理
                int screenHeight = wm.getDefaultDisplay().getHeight();
                pw.setHeight(screenHeight - location[1] - parent.getHeight());
            }
            pw.showAtLocation(parent, Gravity.NO_GRAVITY, 0, location[1] + parent.getHeight());
        } else
            pw.showAsDropDown(parent, 0, 0);
    }

    /**
     * 采用EventBus传值
     * 微聊是否被添加或踢出群聊
     */
    public void onEventMainThread(io.rong.imlib.model.Message message) {// Message
        String targetId = message.getTargetId();// 设备群号
        Tracker currTracker = UserUtil.getCurrentTracker(mainActivity);//获取当前设备
        try {
            MessageContent messageContent = message.getContent();//信息内容体
            if (messageContent instanceof CommandNotificationMessage) {
                CommandNotificationMessage msg = (CommandNotificationMessage) messageContent;//强转为命令通知消息，是否是退群还是加群
                String data = msg.getData().toString();
                JSONObject json = new JSONObject(data);
                int index = json.getInt("type");//判断是否是加群还是退群，0退群、1加群
                chatEvent(index, targetId, currTracker);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否有推送消息，显示右上角小图标
     */
    public void onEventMainThread(Alarm event) {
        Constants.isNewMessage = true;
        setRightIcon(true);
    }

    /**
     * 返回数据操作
     */
    private void chatEvent(int index, String targetId, Tracker currTracker) {
        trackerLists = UserUtil.getUserInfo(mainActivity).device_list;//获取保存的用户信息中的设备列表

        if (index == 0) {//退出群聊
            for (int i = 0; i < trackerLists.size(); i++) {
                Tracker tracker = trackerLists.get(i);
                if (tracker.device_sn.equals(targetId)) {//判断哪个设备退出群组
                    if (currTracker != null && currTracker.device_sn.equals(targetId)) {//当前显示设备
                        currTracker.isExistGroup = null;
                        EventBus.getDefault().post(currTracker);//通知当前显示界面隐藏微聊图标
                        UserUtil.saveCurrentTracker(mainActivity, currTracker);//保存当前状态
                        saveUserInfo(currTracker, null, i);//更改用户信息中当前设备状态
                        break;
                    }
                    tracker.isExistGroup = null;//判断微聊图标是否显示
                    saveUserInfo(tracker, null, i);// 不是当前设备，修改该设备的微聊显示状态
                    break;
                }
            }
        } else if (index == 1) {//加入群聊
            for (int i = 0; i < trackerLists.size(); i++) {
                Tracker tracker = trackerLists.get(i);
                if (tracker.device_sn.equals(targetId)) {
                    if (currTracker != null && currTracker.device_sn.equals(targetId)) {
                        currTracker.isExistGroup = targetId;
                        EventBus.getDefault().post(currTracker);
                        UserUtil.saveCurrentTracker(mainActivity, tracker);
                        saveUserInfo(currTracker, targetId, i);
                        break;
                    }
                    tracker.isExistGroup = targetId;
                    saveUserInfo(tracker, targetId, i);
                    break;
                }
            }
        }
    }

    /**
     * 保存修改后的用户信息UserInfo
     * i 表示第几个设备需要更改
     */
    private void saveUserInfo(Tracker tracker, String targetId, int i) {
        User user = UserUtil.getUserInfo(mainActivity);
        if (targetId == null) {
            ChatUtil.clearChatMessage(tracker.device_sn);// 解除绑定后，清空该群聊消息
            ChatUtil.clearMessageDrag(tracker.device_sn);//解除绑定后清空该群聊草稿信息
        }
        if (user != null) {//保存状态
            user.device_list.get(i).isExistGroup = targetId;
            UserUtil.savaUserInfo(mainActivity, user);//保存当前用户信息更改
        }
    }


    /**
     * 解除绑定
     */
    private void cancelAuthNetworkConnect() {
        final String trackerNo = UserUtil.getCurrentTracker(this).device_sn;
        RequestParams params = HttpParams.cancelAuthorization(trackerNo, mCurCount);
        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(MainActivity.this,
                                null, MainActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        ToastUtil.show(MainActivity.this, obj.what);
                        if (obj.code == 0) {
                            User user = UserUtil.getUserInfo(MainActivity.this);
                            for (int i = 0; i < user.device_list.size(); i++) {
                                if (trackerNo.equals(user.device_list.get(i).device_sn)) {
                                    user.device_list.remove(i);
                                    break;
                                }
                            }
                            UserUtil.savaUserInfo(MainActivity.this, user);

                            List<Tracker> trackers = user.device_list;
                            if (trackers != null && trackers.size() > 0) {
                                UserUtil.saveCurrentTracker(MainActivity.this, trackers.get(0));
                                changeTracker(1);
                                tabChange(0);
                            } else {
                                UserUtil.saveCurrentTracker(MainActivity.this, null);
                                tvTitle.setText("");
                                tvTitleSubject.setText("");
                                mOnChangeListener.onChangeTrackerClear();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(MainActivity.this, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }


    // 发送断电设置指令
    private void sendDefensiveOrder(final String sTrackerNo) {
        bInterruptPower = false;
        RequestParams params = HttpParams.sendDefensiveOrder(sTrackerNo);
        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(MainActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            bInterruptPower = true;
                            sInterruptPower = obj.what;
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(MainActivity.this, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                        if (bInterruptPower) {
                            ToastUtil.show(MainActivity.this, sInterruptPower);
                        } else {
                            DialogUtil.show(MainActivity.this,
                                    R.string.interrupt_power_prompt,
                                    R.string.interrupt_power_prompt_content_fail,
                                    R.string.confirm,
                                    new OnClickListener() {
                                        @Override
                                        public void onClick(View arg0) {
                                            DialogUtil.dismiss();
                                            sendDefensiveOrder(sTrackerNo);
                                        }
                                    }, R.string.cancel,
                                    new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            DialogUtil.dismiss();
                                        }
                                    });
                        }
                    }
                });
    }

    /**
     * 得到设备在线状态
     */
    private void getOnLineStatus() {
        RequestParams params = HttpParams.getOnLineStatus(mCurTracker.device_sn);
        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            DeviceStatusInfo deviceStatusInfo = GsonParse.deviceStatus(new String(response));
                            mCurTracker.onlinestatus = deviceStatusInfo.onlinestatus;
                            UserUtil.changeTrackerList(MainActivity.this, mCurTracker);
                        }
                    }
                });
    }


    /**
     * 得到系统消息
     */
    private void checkAPPSystemNotice() {
        setRightIcon(false);
        try {
            Utils.saveFile(Constants.DIR_SYSTEM_NOTICE, "", mCurCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestParams params = HttpParams.geSystemNotice();
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
                            SystemNoticeObj versionObj = GsonParse.SystemNoticeParse(new String(response));
                            if (versionObj == null) {
                                return;
                            }
                            String newSystemNotice = versionObj.content;
                            String code = versionObj.code;
                            // 是新消息
                            if (code != null && newSystemNotice != null) {// && !code.equalsIgnoreCase(UserSP.getInstance().getSysmsgCode(mainActivity))
                                Constants.isNewMessage = true;
                                setRightIcon(true);
                                try {
                                    Utils.saveFile(Constants.DIR_SYSTEM_NOTICE, versionObj.content, mCurCount);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
    }

    /**
     * 群组信息内容提供者
     */
    @Override
    public Group getGroupInfo(String s) {
        return new Group(s, "微聊", null);
    }

    /**
     * 用户信息内容提供者
     */
    @Override
    public UserInfo getUserInfo(String s) {
        return new UserInfo(s, s, null);
    }
}
