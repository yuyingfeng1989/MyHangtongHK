package com.bluebud.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.chat.utils.ChatUtil;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.info.TrackerUser;
import com.bluebud.info.User;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.swipemenulistview.SwipeMenu;
import com.bluebud.swipemenulistview.SwipeMenuCreator;
import com.bluebud.swipemenulistview.SwipeMenuItem;
import com.bluebud.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.bluebud.utils.Constants;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.view.MySwipeMenuListView;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

//设备管理页面
public class DeviceManagement extends BaseActivity implements OnClickListener, OnProgressDialogClickListener {//OnItemClickListener
    private LinearLayout mLlDeviceAll;

    private MySwipeMenuListView mSlvDevice;
    private LinearLayout mLvauthorizedDevice;
    private User user;
    private List<Tracker> superTrackers;
    private RelativeLayout mRlauthorizedDevice;
    //    private int iType = 1;
    private View authUserView;
    private TrackerUser trackerUser;
    private RequestHandle requestHandle;

    private DeviceManagementAdaper superDeviceManagementAdaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_device_management);
        init();
    }

    private void init() {
        setBaseTitleGone();
        findViewById(R.id.rl_back).setOnClickListener(this);//返[回]
        findViewById(R.id.rl_add).setOnClickListener(this);//增加设备
        mLlDeviceAll = (LinearLayout) findViewById(R.id.ll_device_all);

        mSlvDevice = (MySwipeMenuListView) findViewById(R.id.swlv_management);
        mRlauthorizedDevice = (RelativeLayout) findViewById(R.id.rl_authorized_devices);
        mLvauthorizedDevice = (LinearLayout) findViewById(R.id.ll_auth_device);
//        mSlvDevice.setOnItemClickListener(this);
        initMenuListView();
        setDeviceInfo();
    }

    private void setDeviceInfo() {
        user = UserUtil.getUserInfo(this);
        List<Tracker> trackers = user.device_list;
        superTrackers = new ArrayList<Tracker>();
        final List<Tracker> authTrackers = new ArrayList<Tracker>();
        for (int i = 0; i < trackers.size(); i++) {
            if (null != trackers.get(i).super_user && null != UserSP.getInstance().getUserName(this)) {
                if (trackers.get(i).super_user.equalsIgnoreCase(UserSP.getInstance()
                        .getUserName(this))) {
                    superTrackers.add(trackers.get(i));
                } else {
                    authTrackers.add(trackers.get(i));
                }
            }

        }
        LogUtil.i("绑定设备数量：" + superTrackers.size() + ",被授权用户 ：" + authTrackers.size());
        if (trackers.size() == 0) {//当前用户无设备时
            mLlDeviceAll.setVisibility(View.GONE);
        } else {
            mLlDeviceAll.setVisibility(View.VISIBLE);
            //所有绑定设备
            if (superTrackers.size() <= 0) {//授权用户为0时，不显示
                mSlvDevice.setVisibility(View.GONE);
            } else {
                mSlvDevice.setVisibility(View.VISIBLE);
                superDeviceManagementAdaper = new DeviceManagementAdaper(DeviceManagement.this, superTrackers);
                mSlvDevice.setAdapter(superDeviceManagementAdaper);
            }

            if (authTrackers.size() <= 0) {//被授权用户为0时，不显示
                mRlauthorizedDevice.setVisibility(View.GONE);

            } else {
                mRlauthorizedDevice.setVisibility(View.VISIBLE);
                mLvauthorizedDevice.setVisibility(View.VISIBLE);
                mLvauthorizedDevice.removeAllViews();
                for (int i = 0; i < authTrackers.size(); i++) {
                    authUserView = LayoutInflater.from(this).inflate(R.layout.layout_device_item, null);
                    TextView authUserNickname = (TextView) authUserView.findViewById(R.id.tv_device_nickname);
                    if (TextUtils.isEmpty(authTrackers.get(i).nickname)) {
                        if (authTrackers.get(i).ranges == 1) {//个人
                            authUserNickname.setText(getResources().getString(R.string.PT718));//得改过来
                        } else if (authTrackers.get(i).ranges == 2) {
                            authUserNickname.setText(getResources().getString(R.string.PT690));//得改过来
                        } else if (authTrackers.get(i).ranges == 3 || authTrackers.get(i).ranges == 6) {
                            authUserNickname.setText(getResources().getString(R.string.TH213));//得改过来
                        } else if (authTrackers.get(i).ranges == 4) {
                            authUserNickname.setText(getResources().getString(R.string.MP620));
                        } else {
                            authUserNickname.setText(getResources().getString(R.string.PT690));
                        }
                    } else {
                        authUserNickname.setText(authTrackers.get(i).nickname);
                    }
                    mLvauthorizedDevice.addView(authUserView);
                }
            }
        }
    }


    private class DeviceManagementAdaper extends BaseAdapter {
        private Context mContext;
        private List<Tracker> superTrackerss;

        public DeviceManagementAdaper(Context mContext, List<Tracker> superTrackers) {
            super();
            this.mContext = mContext;
            this.superTrackerss = superTrackers;
        }

        @Override
        public int getCount() {
            if (superTrackerss.size() == 0) {
                return 0;
            } else {
                return superTrackerss.size();
                //return 3;
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_device_management_item, null);
                holder = new ViewHolder();
                holder.mNickname = (TextView) convertView.findViewById(R.id.tv_device_nickname);
                holder.mDeviceId = (TextView) convertView.findViewById(R.id.tv_device_id);
                holder.mServiceTime = (TextView) convertView.findViewById(R.id.tv_service_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (TextUtils.isEmpty(superTrackerss.get(position).nickname)) {
                //holder.mNickname.setText("默认昵称");//得改过来
                if (superTrackerss.get(position).ranges == 1) {//个人
                    holder.mNickname.setText(getResources().getString(R.string.PT718));//得改过来
                } else if (superTrackerss.get(position).ranges == 2) {
                    holder.mNickname.setText(getResources().getString(R.string.PT690));//得改过来
                } else if (superTrackerss.get(position).ranges == 3 || superTrackerss.get(position).ranges == 6) {
                    holder.mNickname.setText(getResources().getString(R.string.TH213));//得改过来
                } else if (superTrackerss.get(position).ranges == 4) {
                    holder.mNickname.setText(getResources().getString(R.string.MP620));
                } else {
                    holder.mNickname.setText(getResources().getString(R.string.PT690));
                }


            } else {
                holder.mNickname.setText(superTrackerss.get(position).nickname);
            }
            holder.mDeviceId.setText(superTrackerss.get(position).device_sn);
            if (!Utils.isEmpty(superTrackerss.get(position).expired_time_de)) {
                holder.mServiceTime.setText(getResources().getString(R.string.service_time, superTrackerss.get(position).expired_time_de.trim().substring(0, 10)));
            } else {
                holder.mServiceTime.setText(getResources().getString(R.string.service_time, "--"));
            }

            return convertView;
        }

    }

    private static class ViewHolder {
        TextView mNickname;//昵称
        TextView mDeviceId;//设备编号
        TextView mServiceTime;

    }

    private void initMenuListView() {
        //创建一个SwipeMenuCreator供ListView使用
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
//				//创建一个侧滑菜单
//				SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
//				//给该侧滑菜单设置背景
//				openItem.setBackground(R.color.background_bundling);
//				//设置宽度
//				openItem.setWidth(Utils.dip2px(DeviceManagement.this, 88));
//				//设置名称
//				openItem.setTitle(getString(R.string.equipment_authorization));
//				//字体大小
//				
//				openItem.setTitleSize(15);
//				//字体颜色
//				openItem.setTitleColor(getResources().getColor(R.color.white));
//				//加入到侧滑菜单中
//				menu.addMenuItem(openItem);

                //创建一个侧滑菜单
                SwipeMenuItem delItem = new SwipeMenuItem(getApplicationContext());
                //给该侧滑菜单设置背景
                delItem.setBackground(R.color.background_unbundling);
                //设置宽度
                delItem.setWidth(Utils.dip2px(DeviceManagement.this, 88));
                //设置名称
                delItem.setTitle(getString(R.string.unbind_device));

                delItem.setTitleSize(15);

                //字体大小
                delItem.setTitleColor(getResources().getColor(R.color.white));

                //加入到侧滑菜单中
                menu.addMenuItem(delItem);
            }
        };
        mSlvDevice.setMenuCreator(creator);
        //侧滑菜单的相应事件---添加设备
        mSlvDevice.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
//						case 0://设备授权
//							//open(mAppList.get(position));
//							LogUtil.i("设备授权："+position);
//							Intent intent = new Intent(DeviceManagement.this, AuthActivity.class);
//							intent.putExtra("device_sn", superTrackers.get(position).device_sn);
//							
//							startActivity(intent);
//							
//							
//							break;
                    case 0://解绑设备
                        //mAppList.remove(position);
                        //mAdapter.notifyDataSetChanged();
                        LogUtil.i("解绑设备：" + position);

                        DialogUtil.show(DeviceManagement.this,
                                R.string.unbind_device_tips,
                                R.string.confirm,
                                new OnClickListener() {//确认

                                    @Override
                                    public void onClick(View v) {//解绑设备
                                        if (Utils.isCorrectEmail(UserSP.getInstance().getUserName(DeviceManagement.this))) {
                                            cancelAuthNetworkConnect(superTrackers.get(position).device_sn);//邮箱解绑设备
//                                            if (superTrackers.get(position).ranges == 7) {
//                                                BluetoothDevice remoteDevice = WearableManager.getInstance().getRemoteDevice();
//                                                if (remoteDevice == null) {
//                                                } else if (WearableManager.getInstance().isAvailable()) {
//                                                    WearableManager.getInstance().disconnect();
//                                                    AppSP.getInstance().saveFirstMainActivity(DeviceManagement.this, true);
//                                                    LogUtil.i("解绑设备时蓝牙断开");
//                                                }
//                                            }
                                            DialogUtil.dismiss();
                                        } else {//手机解绑设备
//												//cancelAuthNetworkConnect(superTrackers.get(position).device_sn);//邮箱解绑设备
//												Intent intent = new Intent(DeviceManagement.this, GetVerificationCodeActivity.class);
//												intent.putExtra("type", Constants.DELETE_DEVICE);//解绑设备
//												intent.putExtra("position", position);
//												startActivityForResult(intent, 2);
//												DialogUtil.dismiss();
                                        }
                                    }
                                }, R.string.cancel,
                                new OnClickListener() {//取消

                                    @Override
                                    public void onClick(View v) {
                                        DialogUtil.dismiss();

                                    }
                                }
                        );

                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_add:
                Intent intent = new Intent(DeviceManagement.this, BindListActivity.class);
                intent.putExtra("formpage", Constants.MAIN_PAGE);
                startActivity(intent);

                break;

            default:
                break;
        }

    }
//进入授权用户页面

//    @Override
//    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
//        LogUtil.i("设备管理列表点击：" + position);
//        Intent intent = new Intent(DeviceManagement.this, ChangeDeviceManagementActivity.class);
//        intent.putExtra("device_sn", superTrackers.get(position).device_sn);
//        intent.putExtra("around_ranges", superTrackers.get(position).around_ranges);
//        startActivityForResult(intent, 1);
//		if (!Utils.isEmpty(superTrackers.get(position).nickname)) {
//			getTrackerUser(superTrackers.get(position).device_sn,superTrackers.get(position).nickname);
//		}else {
//			getTrackerUser(superTrackers.get(position).device_sn,"");
//		}
//
//    }


    /**
     * 获取追踪器下的所有用户
     */
    private void getTrackerUser(final String trackerNo, final String nickname) {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.getTrackerUser(trackerNo);

        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        // ProgressDialogUtil.show(AuthActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null) {//暂无授权帐号
                            ToastUtil.show(DeviceManagement.this, getResources().getString(R.string.no_authed_account));
                            return;
                        }
                        if (obj.code == 0) {
                            trackerUser = GsonParse.usersParse(new String(
                                    response));


                            enterAuthUsers(trackerNo, nickname);
                        } else {
                            ToastUtil.show(DeviceManagement.this, obj.what);
                        }
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(DeviceManagement.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        // ProgressDialogUtil.dismiss();
                    }
                });
    }

    //进入授权用户页面
    private void enterAuthUsers(String trackerNo, String nickname) {
        if (trackerUser.users.size() <= 0) {
            ToastUtil.show(DeviceManagement.this, getString(R.string.no_authed_account));
            return;
        }
        Intent intent = new Intent(DeviceManagement.this, AuthorizedUserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("trackerUser", trackerUser);
        bundle.putString("trackerNo", trackerNo);
        bundle.putString("nickname", nickname);
        intent.putExtras(bundle);
        startActivity(intent);

    }


    /**
     * 解除绑定
     */
    private void cancelAuthNetworkConnect(final String trackerNo) {
    /*	if (UserUtil.isGuest(this)) {
            ToastUtil.show(this, R.string.guest_no_set);
			return;
		}
*/
        String url = UserUtil.getServerUrl(this);

        String userName = UserSP.getInstance().getUserName(this);

        RequestParams params = HttpParams.cancelAuthorization(trackerNo,
                userName);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                DeviceManagement.this, null,
                                DeviceManagement.this);

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        ToastUtil.show(DeviceManagement.this, obj.what);
                        if (obj.code == 0) {
                            ChatUtil.clearChatMessage(trackerNo);//解除绑定后，清空该群聊消息
                            ChatUtil.clearMessageDrag(trackerNo);//解除绑定后清空该群聊草稿信息
                            UserUtil.deleteTracker(DeviceManagement.this, trackerNo);
                            List<Tracker> trackers = UserUtil.getUserInfo(DeviceManagement.this).device_list;
                            if (trackers != null && trackers.size() > 0) {
                                if (trackerNo.equals(UserUtil
                                        .getCurrentTracker(DeviceManagement.this).device_sn)) {
                                    UserUtil.saveCurrentTracker(
                                            DeviceManagement.this,
                                            trackers.get(0));
                                    sendBroadcast(new Intent(Constants.ACTION_TRACTER_CHANGE));
                                }

                                setDeviceInfo();
                                //superDeviceManagementAdaper.notifyDataSetChanged();
                            } else {
                                LogUtil.i("没有设备啦");
                                UserUtil.saveCurrentTracker(
                                        DeviceManagement.this, null);
                                setDeviceInfo();
                                sendBroadcast(new Intent(
                                        Constants.ACTION_TRACTER_CHANGE));
//								DialogUtil.show(DeviceManagement.this,
//										R.string.prompt,
//										R.string.notice_no_trackers,
//										R.string.confirm,
//										new OnClickListener() {
//
//											@Override
//											public void onClick(View arg0) {
//												DialogUtil.dismiss();
//
//												Intent intent = new Intent(
//														DeviceManagement.this,
//														LoginActivity.class);
//												intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//														| Intent.FLAG_ACTIVITY_NEW_TASK);
//												startActivity(intent);
//											}
//										});
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(DeviceManagement.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }


    public void onProgressDialogBack() {
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }

    //得验证码返回来的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            LogUtil.i("返回来的数据");
            setDeviceInfo();
        }
    }


}
