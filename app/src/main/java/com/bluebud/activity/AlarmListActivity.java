package com.bluebud.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.bluebud.adapter.AlarmNoteAdapter1;
import com.bluebud.app.App;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientGoogleGeocode;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.Alarm;
import com.bluebud.info.AlarmInfo;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.utils.WheelViewUtil;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

// TODO: 2019/7/22 Handler重构，使用Presenter来弄，后面再整
public class AlarmListActivity extends BaseActivity implements View.OnClickListener,
        GeocodeSearch.OnGeocodeSearchListener, WheelViewUtil.OnWheeClicked {
//    private Context mContext;

    private GeocodeSearch mGeocodeSearch;

    private TextView tvDataStart;
    private TextView tvDataEnd;
    private Spinner spinner;
    //private RelativeLayout rlNoRead;
    private ListView listView;

    private List<Alarm> curAlarms;
    private List<Alarm> alarms;
//    private List<Alarm> pushNoReadalarms;


    private String sDayEnd, sDayStart;
    private String mTrackerNo;
    private Tracker mCurTracker;
    private ArrayAdapter<String> spinnerAdapter;
    private AlarmNoteAdapter1 adapter;

    //    private GeoCoder mSearch;
    private int positionAlarm = 0;
    private int iPage = 0;
    private LinearLayout llDataselect;

    private String[] titlesPeople;
    private String[] titlesPet;
    private String[] titlesCar;
    private String[] titlesOBD;
    private String[] titlesMoto;
    private String[] titlesWatch;
    private String[] titles;
    private StringBuffer sb;

    private int[] typesPeople;
    private int[] typesPet;
    private int[] typesCar;
    private int[] typesOBD;
    private int[] typesMoto;
    private int[] typesWatch;
    private int[] types = typesPeople;
    private int iType = 0;
    private int flag = 0;// 0表示不编辑

    private WheelViewUtil wheelViewUtil;
    private boolean bStartTime = true;
    //    private List<ImageView> imageViews;
//    private List<View> dots;
    private AlarmNoteAdapter1.ViewHolder holder;
    private boolean isSelectAll = true;
    private RelativeLayout rlBottom;

    private Button btnAllSellect;

    private Button btnDelete;

    private Button btnSetRead;

    private List<Alarm> deleteList;

    private ArrayList<Alarm> deletePushList;
    //    private AlarmDao alarmDao;
//    private List<HashMap<String, Object>> list = null;
    private View vAlarm;
    private View vSysmsg;
    private RelativeLayout llAlarm;
    private LinearLayout llSysmsg;
    private FrameLayout llAlarmIndex;
    private FrameLayout llSysmsgindex;
    //    private ImageView ivNewMsg;
//    private ImageView ivNewMsgAlarm;
    private String mAccount;
    private WebView tvSysMsg;
    private LinearLayout rlEdit;
    private TextView tvAlarm;
    private TextView tvSystem;
    private ImageView imageBack;
    private TextView tvEdit;
    private int iType2 = 0;

    /**
     * google警情返回
     */
    private Handler mHandlerGoogleMap = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HttpClientGoogleGeocode.SUCCESS_ALARM:
                    LogUtil.i("google警情有解析出来：positionAlarm:" + positionAlarm);
                    if (positionAlarm < 0) {
                        return;
                    }
                    if (null != curAlarms) {
                        int position = 0;
                        if (msg.arg1 >= curAlarms.size() - 1) {
                            position = curAlarms.size() - 1;
                        } else {
                            position = msg.arg1;
                        }
                        curAlarms.get(position).address = msg.obj.toString();
                        adapter.setLists(curAlarms);
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_alarm_list);
        EventBus.getDefault().register(this);
        init();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_alarm:
                finish();
                break;
            case R.id.tv_date_start:
                bStartTime = true;
                wheelViewUtil.showDay(sDayStart);
                break;
            case R.id.tv_date_end:
                bStartTime = false;
                wheelViewUtil.showDay(sDayEnd);
                break;
            case R.id.btn_select_all:// 全选
                selectAll();
                break;
            case R.id.btn_delete:// 删除
                deleteAlarm();
                break;
            case R.id.btn_set_read:// 设为已读
                LogUtil.i("设为已读");
                setReadedAlarm();
                break;
            case R.id.ll_title_right_poit:// 编辑
                LogUtil.i("edit is edit");
                if (curAlarms != null && curAlarms.size() > 0) {
                    changeEditStatus(true);//改变编辑状态
                } else {// 无警情信息，不可编辑
                    ToastUtil.show(this, R.string.no_alarm_can_not_be_edited);
                }
                break;
            case R.id.ll_alarm_index:
            case R.id.v_alarm://报警消息
                alarmMessage();
                break;
            case R.id.ll_sysmsg_index:
            case R.id.v_sysmsg://系统消息
                systemInformation();
        }
    }


    /**
     * 系统消息
     */
    private void systemInformation() {
        llAlarm.setVisibility(View.GONE);
        llSysmsg.setVisibility(View.VISIBLE);
        tvEdit.setVisibility(View.VISIBLE);
        tvEdit.setTextColor(getResources().getColor(R.color.bg_theme));
        tvSystem.setTextColor(getResources().getColor(R.color.white));
        tvSystem.setTextSize(18);
        tvAlarm.setTextColor(getResources().getColor(R.color.white_transparent));
        tvAlarm.setTextSize(16);
        showSystemMsg();
    }

    /**
     * 警情消息
     */
    private void alarmMessage() {
        llAlarm.setVisibility(View.VISIBLE);
        llSysmsg.setVisibility(View.GONE);
        tvEdit.setVisibility(View.VISIBLE);
        tvEdit.setTextColor(getResources().getColor(R.color.white));
        tvAlarm.setTextColor(getResources().getColor(R.color.white));
        tvAlarm.setTextSize(18);
        tvSystem.setTextColor(getResources().getColor(R.color.white_transparent));
        tvSystem.setTextSize(16);
    }

    /**
     * 设为已读警情
     */
    private void setReadedAlarm() {
        int isPushDeviceRead = 0;
        if (curAlarms != null && curAlarms.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < curAlarms.size(); i++) {
                if (AlarmNoteAdapter1.getIsSelected().get(i)) {
                    if (curAlarms.get(i).id <= 0) {//表推送的信息
                        isPushDeviceRead = isPushDeviceRead + 1;
                    } else {//查询的警情
                        String id = curAlarms.get(i).id + "";
                        sb.append(id).append(",");
                    }
                }
            }
            if (isPushDeviceRead <= 0 && Utils.isEmpty(sb.toString())) {
                ToastUtil.show(this, R.string.unchecked_alarm);
            } else if (isPushDeviceRead > 0 && Utils.isEmpty(sb.toString())) {
                if (curAlarms != null && curAlarms.size() > 0) {
                    for (int i = 0; i < curAlarms.size(); i++) {
                        if (AlarmNoteAdapter1.getIsSelected().get(i)) {
                            AlarmNoteAdapter1.getIsSelected().put(i, false);
                            curAlarms.get(i).readstatus = 1;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    changeEditStatus(false);
                }
            } else {
                sb.deleteCharAt(sb.length() - 1);
                setAlarmStatus(sb.toString(), 0);
            }
        }
    }

    /**
     * 删除警情
     */
    private void deleteAlarm() {
        if (null == mCurTracker) {
            return;
        }
        if (!mCurTracker.super_user.equalsIgnoreCase(UserSP.getInstance().getUserName(this))) {//不是超级用户
            ToastUtil.show(this, R.string.no_super_user);
            return;
        }

        if (null != curAlarms && curAlarms.size() > 0) {
            sb = new StringBuffer();
            deleteList = new ArrayList<>();
            deletePushList = new ArrayList<>();
            for (int i = 0; i < curAlarms.size(); i++) {
                if (AlarmNoteAdapter1.getIsSelected().get(i)) {
                    if (curAlarms.get(i).id <= 0) {//推送过来的信息
                        deletePushList.add(curAlarms.get(i));
                    } else {//查询的信息
                        String id = curAlarms.get(i).id + "";
                        sb.append(id).append(",");
                        deleteList.add(curAlarms.get(i));
                    }
                }
            }
            LogUtil.i("deletePushList.size:" + deletePushList.size() + "deleteList.size:" + deleteList.size());
            if (deletePushList.size() <= 0 && deleteList.size() <= 0) {
                ToastUtil.show(this, R.string.unchecked_alarm);
                return;
            }
        }
        DialogUtil.show(this, R.string.delete_alarm,
                R.string.delete_alarm_point, R.string.confirm,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        DialogUtil.dismiss();
                        if (null != deletePushList && deletePushList.size() > 0) {//删除推送的警情
                            positionAlarm = positionAlarm - deletePushList.size();
                            curAlarms.removeAll(deletePushList);
                            adapter.notifyDataSetChanged();
                            changeEditStatus(false);
                        }
                        if (!Utils.isEmpty(sb.toString())) {//删除查询的警情
                            LogUtil.i("警情删除时拼接数据为" + sb.toString());
                            sb.deleteCharAt(sb.length() - 1);
                            LogUtil.i("警情删除时拼接数据后去除最后一个字符" + sb.toString());
                            deleteAlarm(sb.toString());
                        }
                    }
                }, R.string.cancel, new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        DialogUtil.dismiss();
                    }
                });
    }

    /**
     * 全选
     */
    private void selectAll() {
        if (isSelectAll) {//是全选
            isSelectAll = false;
            if (curAlarms != null && curAlarms.size() > 0) {
                for (int i = 0; i < curAlarms.size(); i++) {
                    AlarmNoteAdapter1.getIsSelected().put(i, true);
                }
                adapter.notifyDataSetChanged();
            }
            btnAllSellect.setText(getString(R.string.cancel));
        } else {//是取消
            isSelectAll = true;
            if (curAlarms != null && curAlarms.size() > 0) {
                for (int i = 0; i < curAlarms.size(); i++) {
                    AlarmNoteAdapter1.getIsSelected().put(i, false);
                }
                adapter.notifyDataSetChanged();
            }
            btnAllSellect.setText(getString(R.string.all_select));
        }
    }

    /**
     * 显示系统消息
     */
    private void showSystemMsg() {
        String msg = "";
        try {
            msg = Utils.readFileSdcardFile(Environment.getExternalStorageDirectory() + "/" + Constants.DIR_SYSTEM_NOTICE + "/" + mAccount);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtil.i("account is " + mAccount + " msg is " + msg);
        tvSysMsg.getSettings().setJavaScriptEnabled(true);
        tvSysMsg.setBackgroundColor(0);
        tvSysMsg.getBackground().setAlpha(0);
        tvSysMsg.getSettings().setSupportZoom(false);
        tvSysMsg.getSettings().setBuiltInZoomControls(true);
        tvSysMsg.getSettings().setDefaultTextEncodingName("utf-8");
        tvSysMsg.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        tvSysMsg.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        tvSysMsg.loadDataWithBaseURL(null, msg, "text/html", "UTF-8", null);
    }

    /**
     * 改变编辑状态
     */
    private void changeEditStatus(boolean editStatus) {
        if (editStatus) {
            if (flag == 0) {//0表示不编辑
                flag = 1;
                tvEdit.setText(getString(R.string.cancel));
                isSelectAll = true;
                btnAllSellect.setText(getString(R.string.all_select));
                if (curAlarms != null && curAlarms.size() > 0) {
                    for (int i = 0; i < curAlarms.size(); i++) {
                        AlarmNoteAdapter1.getIsSelected().put(i, false);
                    }
                    adapter.notifyDataSetChanged();
                }
            } else {
                flag = 0;
                tvEdit.setText(getString(R.string.edit));
                if (null != curAlarms && curAlarms.size() > 0) {
                    for (int i = 0; i < curAlarms.size(); i++) {
                        AlarmNoteAdapter1.getIsSelected().put(i, false);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        } else {
            flag = 0;
            tvEdit.setText(getString(R.string.edit));
        }
        if (flag == 0) {// 在编辑状态到为显示，在不编辑状态到为不显示
            rlBottom.setVisibility(View.GONE);
            llDataselect.setVisibility(View.VISIBLE);
            tvEdit.setText(getString(R.string.edit));
        } else {
            rlBottom.setVisibility(View.VISIBLE);
            llDataselect.setVisibility(View.GONE);
            setBaseTitleRightText(R.string.cancel);
            tvEdit.setText(getString(R.string.cancel));
        }
        LogUtil.i("编辑状态标示0表示不编辑,flag=" + flag);
        adapter.setflag(flag);
        adapter.notifyDataSetChanged();
    }

    /**
     * 初始化控件
     */
    private String[] titlesWatch_k1;
    private int[] typesWatch_k1;

    private void init() {
        setBaseTitleGone();
        setBaseTitleText(R.string.alarm_note);
        getBaseTitleLeftBack().setOnClickListener(this);
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleRightText(R.string.edit);
        setBaseTitleRightTextVisible(View.VISIBLE);
        getBaseTitleRightText().setOnClickListener(this);
        mAccount = UserSP.getInstance().getUserName(getApplicationContext());
        titlesPeople = new String[]{getString(R.string.alarm_all),
                getString(R.string.alarm_sos), getString(R.string.alarm_over),
                getString(R.string.alarm_low_voltage)};
        titlesPet = new String[]{getString(R.string.alarm_all),
                getString(R.string.alarm_over),
                getString(R.string.alarm_low_voltage)};
        titlesCar = new String[]{getString(R.string.alarm_all),
                getString(R.string.alarm_over),
                getString(R.string.alarm_low_voltage),
                getString(R.string.alarm_tow),
                getString(R.string.alarm_power_line),
                getString(R.string.alarm_speed)};
        titlesOBD = new String[]{getString(R.string.alarm_all),
                getString(R.string.alarm_over),
                getString(R.string.alarm_low_voltage),
                getString(R.string.alarm_tow),
                getString(R.string.alarm_power_line),
                getString(R.string.alarm_speed),
                getString(R.string.alarm_blackout),
                getString(R.string.alarm_water)};
        titlesMoto = new String[]{getString(R.string.alarm_all),
                getString(R.string.alarm_over),
                getString(R.string.alarm_low_voltage),
                getString(R.string.alarm_shock),
                getString(R.string.alarm_power_line),
                getString(R.string.alarm_speed)};
        titlesWatch = new String[]{getString(R.string.alarm_all),
                getString(R.string.alarm_sos), getString(R.string.alarm_over),
                getString(R.string.alarm_low_voltage)};
        titlesWatch_k1 = new String[]{getString(R.string.alarm_all),
                getString(R.string.alarm_sos), getString(R.string.alarm_over),
                getString(R.string.alarm_low_voltage), getString(R.string.alarm_fall_off)};

        typesPeople = new int[]{0, 6, 29, 18};
        typesPet = new int[]{0, 29, 18};
        typesCar = new int[]{0, 29, 18, 28, 16, 14};
        typesOBD = new int[]{0, 29, 18, 28, 16, 14, 94, 83};
        typesMoto = new int[]{0, 29, 18, 3, 16, 14};
        typesWatch = new int[]{0, 6, 29, 18};
        typesWatch_k1 = new int[]{0, 6, 29, 18, 35};

        wheelViewUtil = new WheelViewUtil(this, this);

        mCurTracker = UserUtil.getCurrentTracker();
        if (mCurTracker != null) {
            mTrackerNo = mCurTracker.device_sn;
            iType2 = mCurTracker.ranges;
            if (2 == mCurTracker.ranges) {
                titles = titlesPet;
                types = typesPet;
            } else if (3 == mCurTracker.ranges) {
                titles = titlesCar;
                types = typesCar;
            } else if (mCurTracker.ranges == 6) {
                titles = titlesOBD;
                types = typesOBD;
            } else if (5 == mCurTracker.ranges) {
                if (mCurTracker.product_type.equals("22")) {//mCurTracker.product_type.equals("30") ||
                    titles = titlesWatch_k1;
                    types = typesWatch_k1;
                } else {
                    titles = titlesWatch;
                    types = typesWatch;
                }
            } else if (4 == mCurTracker.ranges) {
                if ("18".equals(mCurTracker.product_type)) {//620新设备有震动报警
                    titles = titlesMoto;
                    types = typesMoto;
                } else {
                    titles = titlesCar;
                    types = typesCar;
                }
            } else {
                titles = titlesPeople;
                types = typesPeople;
            }
        } else {
            titles = titlesPeople;
            types = typesPeople;
        }

        initView();
        if (App.getMapType() == App.MAP_TYPE_AMAP) {
            initMap();
        }
    }

    /**
     * 初始化布局控件
     */
    public void initView() {
        llDataselect = findViewById(R.id.ll_data_select);
        imageBack = findViewById(R.id.iv_back_alarm);
        rlEdit = findViewById(R.id.ll_title_right_poit);//编辑
        tvEdit = findViewById(R.id.tv_title_right_edit);
        tvEdit.setVisibility(View.VISIBLE);
        rlEdit.setOnClickListener(this);
        imageBack.setOnClickListener(this);

        vSysmsg = findViewById(R.id.v_sysmsg);
        vAlarm = findViewById(R.id.v_alarm);
        llAlarm = findViewById(R.id.ll_alarm);
        llSysmsg = findViewById(R.id.ll_sysmsg);
        llAlarmIndex = findViewById(R.id.ll_alarm_index);
        llSysmsgindex = findViewById(R.id.ll_sysmsg_index);
        tvSysMsg = findViewById(R.id.tv_sysmsg);

        tvAlarm = findViewById(R.id.tv_alarm);
        tvSystem = findViewById(R.id.tv_system);
        vAlarm.setOnClickListener(this);
        vSysmsg.setOnClickListener(this);
        llAlarmIndex.setOnClickListener(this);
        llSysmsgindex.setOnClickListener(this);
        if (flag == 0) {// 时间选择模块在编辑状态到为不显示，在不编辑状态到为显示 0表示不编辑
            llDataselect.setVisibility(View.VISIBLE);
        } else {
            llDataselect.setVisibility(View.GONE);
        }
        tvDataStart = findViewById(R.id.tv_date_start);
        tvDataEnd = findViewById(R.id.tv_date_end);
        listView = findViewById(R.id.lv_alarm);
        rlBottom = findViewById(R.id.rl_bottom);// 底 部
        if (flag == 0) {// 在编辑状态到为显示，在不编辑状态到为不显示
            rlBottom.setVisibility(View.GONE);
            setBaseTitleRightText(R.string.edit);
        } else {
            rlBottom.setVisibility(View.VISIBLE);
            setBaseTitleRightText(R.string.cancel);
        }
        btnAllSellect = findViewById(R.id.btn_select_all);// 全选
        btnAllSellect.setText(getString(R.string.all_select));
        btnDelete = findViewById(R.id.btn_delete);// 删除
        btnSetRead = findViewById(R.id.btn_set_read);// 设为已读

        btnAllSellect.setOnClickListener(this);

        btnDelete.setOnClickListener(this);
        btnSetRead.setOnClickListener(this);
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        adapter = new AlarmNoteAdapter1(this, curAlarms, flag);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                setNoPushAlarmStatus(view, position);//不是推过的信息
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        if (null == curAlarms || alarms == null) {
                            return;
                        }
                        if (curAlarms.size() < alarms.size()) {
                            iPage = iPage + 1;
                            List<Alarm> temp = new ArrayList<>(curAlarms);
                            if ((alarms.size() - curAlarms.size()) >= 8) {
                                temp.addAll(alarms.subList(iPage * 8, (iPage + 1) * 8));
                            } else {
                                temp.addAll(alarms.subList(iPage * 8, alarms.size()));
                            }
                            curAlarms = temp;
                            getGeoCode();
                            if (1 == flag) {
                                if (!isSelectAll) {
                                    if (curAlarms != null && curAlarms.size() > 0) {
                                        for (int i = 0; i < curAlarms.size(); i++) {
                                            AlarmNoteAdapter1.getIsSelected().put(i, true);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
            }
        });
        tvDataStart.setOnClickListener(this);
        tvDataEnd.setOnClickListener(this);
        sDayStart = Utils.curDate2BeforeTowDay(this);
        sDayEnd = Utils.curDate2Day();
        LogUtil.i("sdaystart:" + sDayStart + ",sdayend:" + sDayEnd);
        tvDataStart.setText(sDayStart.substring(5));
        tvDataEnd.setText(sDayEnd.substring(5));

        spinner = findViewById(R.id.spinner);
        spinnerAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner, titles);
        spinnerAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                iType = types[arg2];
                //改变选中的类型字体颜色
                TextView v1 = (TextView) arg1;
                v1.setTextColor(getResources().getColor(R.color.text_b2));
                LogUtil.i("选择的类型：" + iType);
                getAlarm();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        regesterBroadcast();
    }

    /**
     * 切换设备
     */
    private void trackerInfoChange() {
        mCurTracker = UserUtil.getCurrentTracker();
        if (mCurTracker != null) {
            mTrackerNo = mCurTracker.device_sn;
            if (2 == mCurTracker.ranges) {
                titles = titlesPet;
                types = typesPet;
            } else if (3 == mCurTracker.ranges) {
                titles = titlesCar;
                types = typesCar;
            } else if (mCurTracker.ranges == 6) {
                titles = titlesOBD;
                types = typesOBD;
            } else if (4 == mCurTracker.ranges) {
                if ("18".equals(mCurTracker.product_type)) {//620新设备有震动报警
                    titles = titlesMoto;
                    types = typesMoto;
                } else {
                    titles = titlesCar;
                    types = typesCar;
                }
            } else {
                titles = titlesPeople;
                types = typesPeople;
            }
        }
        spinnerAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner, titles);
        spinnerAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        iType = 0;
    }

    /**
     * 注册广播
     */
    private void regesterBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_TRACTER_RANGES_CHANGE);// UserInfoActivity发出广播
        registerReceiver(broadcastReceiver, filter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {

            if (TextUtils.equals(Constants.ACTION_TRACTER_RANGES_CHANGE, intent.getAction())) {
                trackerInfoChange();
            }
        }
    };


    /**
     * 注册google地图反地理编码
     */
    private void initMap() {
//        mSearch = GeoCoder.newInstance();
//        mSearch.setOnGetGeoCodeResultListener(this);
        mGeocodeSearch = new GeocodeSearch(this);
        mGeocodeSearch.setOnGeocodeSearchListener(this);
    }

    /**
     * 时间选择控件
     */
    @Override
    public void getWheelDay(String sDay) {
        LogUtil.i(sDay);
        if (bStartTime) {
            tvDataStart.setText(sDay.substring(5));
            sDayStart = sDay;
        } else {
            tvDataEnd.setText(sDay.substring(5));
            sDayEnd = sDay;
        }
        getAlarm();
    }

    @Override
    public void getWheelTime(String sTime) {
    }


    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        // 1000为成功码
        if (i != 1000) {
            return;
        }


        if (positionAlarm < 0) {
            return;
        }

        String strInfo = regeocodeResult.getRegeocodeAddress().getFormatAddress();
        LogUtil.i(strInfo);
        if (null == curAlarms) {
            return;
        }
        if (curAlarms.size() - 1 < 0) {
            return;
        }
        if (positionAlarm >= curAlarms.size() - 1) {
            curAlarms.get(curAlarms.size() - 1).address = strInfo;
            LogUtil.i("positionAlarm:" + positionAlarm + ",curAlarms.size=" + curAlarms.size());
        } else {
            curAlarms.get(positionAlarm).address = strInfo;
        }
        adapter.setLists(curAlarms);
        adapter.notifyDataSetChanged();

        if (positionAlarm == curAlarms.size() - 1) {
            positionAlarm = curAlarms.size();
            return;
        }
        positionAlarm = positionAlarm + 1;
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {


    }

    /**
     * g地图调用反地理编码
     */
    private void getGeoCode() {
        boolean isGoogleMap = App.getMapType() == App.MAP_TYPE_GMAP;
        for (int i = iPage * 8; i < curAlarms.size(); i++) {//google反地理编码
            Alarm alarm = curAlarms.get(i);
            if (isGoogleMap) {
                LatLng ll = Utils.getCorrectedLatLng(this, alarm.lat, alarm.lng);
                HttpClientGoogleGeocode httpClientGoogleGeocode = new HttpClientGoogleGeocode();
                httpClientGoogleGeocode.getFromLocation(HttpClientGoogleGeocode.MODE_ALARM, i, ll, mHandlerGoogleMap);
            } else {
                RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(alarm.lat, alarm.lng), 50, GeocodeSearch.GPS);
                mGeocodeSearch.getFromLocationAsyn(query);
            }
        }

    }

    /**
     * 得到推送的未 读警情
     */
    public void onEventMainThread(Alarm event) {
        alarms.add(event);
        if (alarms.size() <= 8) {
            curAlarms = alarms;
        } else {
            iPage = 0;
            curAlarms = alarms.subList(iPage, (iPage + 1) * 8);
        }
        adapter.setLists(curAlarms);
        adapter.notifyDataSetChanged();
        getGeoCode();
    }

    /**
     * 得到警情数据
     */
    private void getAlarm() {
        iPage = 0;
        if (Utils.compareDate(sDayStart, sDayEnd) > 0) {
            ToastUtil.show(this, R.string.date_error);
            return;
        }
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.getAlarmInfo(mTrackerNo, sDayStart, sDayEnd, iType);
        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(AlarmListActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        String result = new String(response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(result);
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            AlarmInfo info = GsonParse.alarmInfoParse(result);
                            if (info == null) {
                                return;
                            } else {
                                if (null == alarms) {
                                    alarms = info.alarm;
                                } else {
                                    alarms.clear();
                                    alarms.addAll(info.alarm);
                                }
                            }

                            if (alarms.size() <= 8) {
                                curAlarms = alarms;
                            } else {
                                iPage = 0;
                                if ((iPage + 1) * 8 >= alarms.size()) {
                                    curAlarms = alarms.subList(iPage, alarms.size());
                                } else {
                                    curAlarms = alarms.subList(iPage, (iPage + 1) * 8);
                                }
                            }
                            adapter.setLists(curAlarms);
                            adapter.notifyDataSetChanged();
                            positionAlarm = 0;
                            getGeoCode();
                        } else {
                            ToastUtil.show(AlarmListActivity.this, obj.what);
                            if (iType2 != 7) {
                                alarms = new ArrayList<>();
                                curAlarms = new ArrayList<>();
                                adapter.setLists(curAlarms);
                                adapter.notifyDataSetChanged();
                                getGeoCode();
                            }

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(AlarmListActivity.this, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });

    }

    /**
     * 删除警情数据
     */
    private void deleteAlarm(String alarmIDS) {
        Tracker currentTracker = UserUtil.getCurrentTracker(this);
        String trackerNo = currentTracker.device_sn;
        LogUtil.i("device_sn:" + currentTracker.device_sn);
        if (currentTracker.super_user != null && UserSP.getInstance().getUserName(this) != null) {
            if (!currentTracker.super_user.equalsIgnoreCase(UserSP.getInstance().getUserName(this))) {
                ToastUtil.show(this, R.string.no_super_user);
                return;
            }
        }

        String userName = UserSP.getInstance().getUserName(this);
        String url = UserUtil.getServerUrl(this);
        LogUtil.i("删除警情地址：" + url);
        RequestParams params = HttpParams.deleteAlarmInfo(userName, trackerNo, alarmIDS);

        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(AlarmListActivity.this);
                        LogUtil.i("删除警情开始onstart");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        String result = new String(response);

                        ReBaseObj obj = GsonParse.reBaseObjParse(result);
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            ToastUtil.show(AlarmListActivity.this, obj.what);
                            for (int i = 0; i < curAlarms.size(); i++) {
                                if (AlarmNoteAdapter1.getIsSelected().get(i)) {
                                    AlarmNoteAdapter1.getIsSelected().put(i, false);// 改变删除的状态
                                }
                            }
                            positionAlarm = positionAlarm - deleteList.size();
                            curAlarms.removeAll(deleteList);
                            alarms.removeAll(deleteList);

                            adapter.notifyDataSetChanged();
                            LogUtil.i("警情删除成功");
                            changeEditStatus(false);

                        } else {
                            ToastUtil.show(AlarmListActivity.this, obj.what);
                            LogUtil.i("警情删除失败");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(AlarmListActivity.this, R.string.net_exception);
                        LogUtil.i("删除警情onFailure");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                        LogUtil.i("删除警情onFinish");
                    }
                });

    }

    /**
     * 设置已读状态
     */
    private void setAlarmStatus(String alarmIDS, final int position) {
//		if (mCurTracker.super_user != null
//				&& UserSP.getInstance().getUserName(this) != null) {
//			if (!mCurTracker.super_user.equalsIgnoreCase(UserSP.getInstance()
//					.getUserName(mContext))) {
//				ToastUtil.show(AlarmActivity.this, R.string.no_super_user);
//				return;
//			}
//		}
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.setAlarmStatus(mTrackerNo, alarmIDS);

        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(AlarmListActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        String result = new String(response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(result);
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            LogUtil.i(obj.what);
                            if (flag == 0 && curAlarms.size() > position) {// 不编辑状态下
                                curAlarms.get(position).readstatus = 1;
                                adapter.notifyDataSetChanged();
                            } else {
                                if (curAlarms != null && curAlarms.size() > 0) {
                                    for (int i = 0; i < curAlarms.size(); i++) {
                                        if (AlarmNoteAdapter1.getIsSelected().get(i)) {
                                            AlarmNoteAdapter1.getIsSelected().put(i, false);//
                                            curAlarms.get(i).readstatus = 1;
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                    changeEditStatus(false);
                                }
                            }
                        } else {
                            if (flag == 0) {// 不编辑状态下
                                curAlarms.get(position).readstatus = 1;
                                adapter.notifyDataSetChanged();
                            }
                            LogUtil.i(obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(AlarmListActivity.this, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    @Override
    public void getFenceRange(String sRange) {
    }

    /**
     * 非推送的警情，通过接口查询获取的警情，点击跳转到地图详情页显示位置
     */
    private void setNoPushAlarmStatus(View view, int position) {
        if (flag == 0) {// flag＝0表示不在编辑状态，这里点击时进入警情详情页面
            Intent intent = new Intent();
            intent.setClass(this, MyAlarmDetailActivity.class);
            intent.putExtra("lat", curAlarms.get(position).lat);
            intent.putExtra("lng", curAlarms.get(position).lng);
            intent.putExtra("dtime", curAlarms.get(position).dtime);
            intent.putExtra("speed", curAlarms.get(position).speed);
            intent.putExtra("address", curAlarms.get(position).address);
            if (mCurTracker != null) {
                intent.putExtra("type", mCurTracker.ranges);
            }
            startActivity(intent);
            if (curAlarms.get(position).readstatus == 0) {// 未读的状态下＝
                StringBuilder sb = new StringBuilder();
                String id = curAlarms.get(position).id + "";
                sb.append(id).append(",");
                LogUtil.i("已读警情拼接数据为" + sb.toString());
                sb.deleteCharAt(sb.length() - 1);
                LogUtil.i("已读警情拼接数据后去除最后一个字符" + sb.toString());
                setAlarmStatus(sb.toString(), position);
            }
        } else {// flag＝1表示在编辑状态，这里点击时选择当前条目

            holder = (AlarmNoteAdapter1.ViewHolder) view.getTag();
            // 改变CheckBox的状态
            holder.ibSelect.toggle();
            if (holder.ibSelect.isChecked()) {
                holder.llSelectAlarm
                        .setBackgroundResource(R.color.opaque);
                holder.tvContent.setTextColor(getResources().getColor(
                        R.color.black));
                holder.tvSN.setTextColor(getResources().getColor(
                        R.color.black));
                holder.tvTime.setTextColor(getResources().getColor(
                        R.color.black));
                holder.tvAddress.setTextColor(getResources().getColor(
                        R.color.black));
            } else {
                holder.llSelectAlarm
                        .setBackgroundResource(R.color.bg_white_bg_nor);
                holder.tvContent.setTextColor(getResources().getColor(
                        R.color.text_9));
                holder.tvSN.setTextColor(getResources().getColor(
                        R.color.text_9));
                holder.tvTime.setTextColor(getResources().getColor(
                        R.color.text_9));
                holder.tvAddress.setTextColor(getResources().getColor(
                        R.color.text_9));
            }
            AlarmNoteAdapter1.getIsSelected().put(position,
                    holder.ibSelect.isChecked());
        }
    }

}