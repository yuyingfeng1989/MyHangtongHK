package com.bluebud.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bluebud.adapter.RemoteTakePhotoAdapter;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.TakePhotoInfo;
import com.bluebud.info.Tracker;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.GridMarginDecoration;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.PopupWindowUtils;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.view.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2018/6/6.
 */

public class RemoteTakePhotoActivity extends BaseActivity implements RemoteTakePhotoAdapter.OnItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        RefreshLayout.OnLoadListener {

    private Context mcontext;
    private ProgressBar mProgress;
    private RecyclerView mRecyclerView;
    private RemoteTakePhotoAdapter adapter;
    private PopupWindowUtils popupWindowUtils;
    private TextView text_state;
    private boolean isClickPhoto;
    private RefreshLayout swipeLayout;
    private Tracker mCurTracker;
    private List<TakePhotoInfo> infos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_takephoto_activity);
        mcontext = this;
        popupWindowUtils = new PopupWindowUtils(mcontext);
        mCurTracker = UserUtil.getCurrentTracker(mcontext);
        initerView();
        infos = new ArrayList<>();
    }

    private void initerView() {
        swipeLayout = (RefreshLayout) findViewById(R.id.swipe_container);//刷新
        swipeLayout.setColorSchemeResources(R.color.material_blue_grey_900,
                R.color.material_blue_grey_900,
                R.color.material_blue_grey_900,
                R.color.material_blue_grey_900);

        mProgress = (ProgressBar) findViewById(R.id.progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        TextView text1 = (TextView) findViewById(R.id.txt1);
        text_state = (TextView) findViewById(R.id.chat_title_text);
        findViewById(R.id.img3).setVisibility(View.GONE);
        text_state.setVisibility(View.VISIBLE);
        text1.setText(R.string.remote_photo);
        text_state.setText(R.string.select_text);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.btn_tackphoto).setOnClickListener(this);
        text_state.setOnClickListener(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mcontext, 4));
        adapter = new RemoteTakePhotoAdapter(this, infos);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.addItemDecoration(new GridMarginDecoration(12));
        requestData();//下拉刷新获取天气数据
        initListener();
    }

    private void initListener() {
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);
    }

    /**
     * 获取头像数据
     */
    private void requestData() {
        ChatHttpParams.getInstallSigle(mcontext).chatHttpRequest(22, null, mCurTracker.device_sn, null, null, null, null, null, null, new ChatCallbackResult() {
            @Override
            public void callBackResult(String result) {
                mProgress.setVisibility(View.GONE);
                LogUtil.e("result==" + result);
                List<TakePhotoInfo> takePhotos = (List<TakePhotoInfo>) ChatHttpParams.getParseResult(22, result);
                if (takePhotos != null) {
                    infos.clear();
                    infos.addAll(takePhotos);
                }
                adapter.setItems(infos);
            }

            @Override
            public void callBackFailResult(String result) {
                mProgress.setVisibility(View.GONE);
                ToastUtil.show(mcontext, result);
            }
        });
    }

    @Override
    public void onItemClick(View v, int position, TakePhotoInfo photo) {
        if (photo.isShow) {
            infos.set(position, photo);
            return;
        }
        popupWindowUtils.openBigImage(this, photo.url);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_tackphoto:
                ChatHttpParams.getInstallSigle(mcontext).chatHttpRequest(21, null, mCurTracker.device_sn, null, null, null, "1", null, null, new ChatCallbackResult() {
                    @Override
                    public void callBackResult(String result) {
                        ToastUtil.show(mcontext, getString(R.string.takephoto_success));
                    }

                    @Override
                    public void callBackFailResult(String result) {
                        ToastUtil.show(mcontext, result);
                    }
                });
                break;

            case R.id.chat_title_text:
                if (infos.size() < 1)
                    return;
                if (!isClickPhoto) {
                    isClickPhoto = true;
                    text_state.setText(R.string.cancel);
                    for (TakePhotoInfo photo : infos) {
                        photo.isShow = true;
                    }
                    adapter.setItems(infos);
                } else {
                    isClickPhoto = false;
                    text_state.setText(R.string.select_text);
                    popupWindowUtils.dismiss();
                    for (TakePhotoInfo photo : infos) {
                        photo.isShow = false;
                        photo.isSelect = false;
                    }
                    adapter.setItems(infos);
                    return;
                }

                popupWindowUtils.initPopupWindowPicture(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        for (TakePhotoInfo photo : infos) {
                            if (b)
                                photo.isSelect = true;
                            else
                                photo.isSelect = false;
                        }
                        adapter.setItems(infos);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (infos.size() < 1)
                            return;
                        DialogUtil.show(mcontext, R.string.prompt, R.string.remind_message,
                                R.string.confirm, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View arg0) {
                                        if (Utils.isSuperUser(mCurTracker, mcontext)) {
                                            DialogUtil.dismiss();
                                            popupWindowUtils.dismiss();
                                            text_state.setText(R.string.select_text);
                                            removeDate();
                                        }
                                    }
                                }, R.string.cancel, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View arg0) {
                                        DialogUtil.dismiss();
                                    }
                                });
                    }
                });
                break;
        }
    }

    /**
     * 删除
     */
    private void removeDate() {
        StringBuilder builder = new StringBuilder();
        for (int i = infos.size() - 1; i >= 0; i--) {
            TakePhotoInfo photo = infos.get(i);
            photo.isShow = false;
            if (photo.isSelect)
                builder.append(photo.devicePhotoID).append(",");
        }
        int a = builder.length();
        if (a < 1) {
            adapter.setItems(infos);
            return;
        }
        builder.deleteCharAt(builder.length() - 1);
        String s = builder.toString();
        ChatHttpParams.getInstallSigle(mcontext).chatHttpRequest(23, s, mCurTracker.device_sn, null, null, null, null, null, null, new ChatCallbackResult() {
            @Override
            public void callBackResult(String result) {
                for (int i = infos.size() - 1; i >= 0; i--) {
                    TakePhotoInfo photo = infos.get(i);
                    if (photo.isSelect) {
                        infos.remove(i);
                    }
                }
                adapter.setItems(infos);
            }

            @Override
            public void callBackFailResult(String result) {
                for (int i = infos.size() - 1; i >= 0; i--) {
                    TakePhotoInfo photo = infos.get(i);
                    photo.isSelect = false;
                }
                adapter.setItems(infos);
                ToastUtil.show(mcontext, result);
            }
        });
    }

    @Override
    public void onRefresh() {
        requestData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 2000);
    }

    @Override
    public void onLoad() {
        swipeLayout.setLoading(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setLoading(false);
            }
        }, 3000);
    }
}
