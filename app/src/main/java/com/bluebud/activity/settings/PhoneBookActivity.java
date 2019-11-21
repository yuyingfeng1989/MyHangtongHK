package com.bluebud.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.adapter.PhoneBookAdapter;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.PhonebookInfo;
import com.bluebud.info.Tracker;
import com.bluebud.swipemenulistview.SwipeMenu;
import com.bluebud.swipemenulistview.SwipeMenuCreator;
import com.bluebud.swipemenulistview.SwipeMenuItem;
import com.bluebud.swipemenulistview.SwipeMenuListView;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.view.MySwipeMenuListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2018/6/11.
 */

public class PhoneBookActivity extends BaseActivity implements View.OnClickListener {
    private int[] image1 = new int[]{R.drawable.img_defaulthead_628, R.drawable.img_dad, R.drawable.img_mom,
            R.drawable.img_grandpa, R.drawable.img_grandma,
            R.drawable.img_grandfather, R.drawable.img_grandmother,
            R.drawable.elder_brother, R.drawable.elder_syster
            , R.drawable.younger_brother, R.drawable.younger_sister};
    private MySwipeMenuListView phonebook_swipe_list;
    private List<PhonebookInfo> bookInfos;
    private PhoneBookAdapter adapter;
    private LinearLayout ll_phone_remind;
    private Context mContext;
    private Tracker mTracker;
    private int selectPosition = -1;
    private boolean isUpdate;//判断是添加还是修改

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonebook_activity);
        WeakReference<PhoneBookActivity> weakReference = new WeakReference<>(this);
        mContext = weakReference.get();
        mTracker = UserUtil.getCurrentTracker(mContext);
        bookInfos = new ArrayList<>();
        initeView();
    }

    /**
     * 初始化控件
     */
    private void initeView() {
        ImageView img3 = (ImageView) findViewById(R.id.img3);
        TextView txt1 = (TextView) findViewById(R.id.txt1);
        txt1.setText(getString(R.string.telephone_book));
        phonebook_swipe_list = (MySwipeMenuListView) findViewById(R.id.phonebook_swipe_list);
        ll_phone_remind = (LinearLayout) findViewById(R.id.ll_phone_remind);
        if (mTracker != null && mTracker.product_type.equals("31")) {
            TextView tv_prompt = findViewById(R.id.tv_prompt);
            tv_prompt.setText(getString(R.string.telephone_watch_book_prompt_790s));
        }
        img3.setImageResource(R.drawable.add_phonebook_image);
        findViewById(R.id.back).setOnClickListener(this);
        img3.setOnClickListener(this);
        initMenuListView();
        updatePhoneBook();
        adapter = new PhoneBookAdapter(mContext, bookInfos, image1, mTracker.product_type);
        phonebook_swipe_list.setAdapter(adapter);
        getPhoneBooks();//获取电话本信息
    }

    /**
     * 侧滑删除
     */
    private void initMenuListView() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {//创建一个SwipeMenuCreator供ListView使用
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem delItem = new SwipeMenuItem(getApplicationContext());  //创建一个侧滑菜单
                delItem.setBackground(R.color.background_unbundling); //给该侧滑菜单设置背景
                delItem.setWidth(140); //设置宽度
                delItem.setTitle(getString(R.string.delect_phonebook));//设置名称
                delItem.setTitleSize(15);//设置字体大小
                delItem.setTitleColor(getResources().getColor(R.color.white));//字体颜色
                menu.addMenuItem(delItem);//加入到侧滑菜单中
            }
        };
        phonebook_swipe_list.setMenuCreator(creator);//添加右侧删除控件
        phonebook_swipe_list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {//侧滑菜单的相应事件
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {//条目点击
                switch (index) {
                    case 0://解绑设备
                        deletePhoneBook(position);
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 删除电话号码
     */
    private void deletePhoneBook(final int position) {
        DialogUtil.show(mContext, R.string.phone_delete_prompt, R.string.delete,
                new View.OnClickListener() {//确认
                    @Override
                    public void onClick(View v) {//解绑设备
                        DialogUtil.dismiss();
                        delectPhoneBook(position);
                    }
                }, R.string.cancel,
                new View.OnClickListener() {//取消
                    @Override
                    public void onClick(View v) {
                        DialogUtil.dismiss();
                    }
                }
        );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.img3://添加
                isUpdate = false;
                if (mTracker.product_type.equals("31") && bookInfos.size() > 14) {
                    ToastUtil.show(mContext, R.string.phone_limit);
                    return;
                }
                if (bookInfos.size() > 49) {
                    ToastUtil.show(mContext, R.string.phone_limit);
                    return;
                }
                Intent intent = new Intent(mContext, PhoneBookEditActivity.class);
                intent.putExtra("phonebookinfo", new PhonebookInfo());
                startActivityForResult(intent, 1);
                break;
        }
    }

    /**
     * 修改电话本信息
     */
    private void updatePhoneBook() {
        phonebook_swipe_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                isUpdate = true;
                selectPosition = i;
                Intent intent = new Intent(mContext, PhoneBookEditActivity.class);
                intent.putExtra("phonebookinfo", bookInfos.get(i));
                startActivityForResult(intent, 1);
            }
        });
    }

    /**
     * 获取电话本
     */
    private void getPhoneBooks() {
        ChatHttpParams.getInstallSigle(mContext).chatHttpRequest(24, null, mTracker.device_sn, null, null, null, null, null, null, new ChatCallbackResult() {
            @Override
            public void callBackStart() {
                super.callBackStart();
                ProgressDialogUtil.show(mContext);
            }

            @Override
            public void callBackResult(String result) {
                ProgressDialogUtil.dismiss();
                List<PhonebookInfo> phonebookInfos = (List<PhonebookInfo>) ChatHttpParams.getParseResult(24, result);
                if (phonebookInfos == null)
                    return;
                ll_phone_remind.setVisibility(View.GONE);
                bookInfos.addAll(phonebookInfos);
                adapter.refreshPhoneBookInfos(bookInfos);
            }

            @Override
            public void callBackFailResult(String result) {
                ProgressDialogUtil.dismiss();
                if (bookInfos == null || bookInfos.size() < 1)
                    ll_phone_remind.setVisibility(View.VISIBLE);
                ToastUtil.show(mContext, result);
            }
        });
    }

    /**
     * 删除电话本成员
     */
    private void delectPhoneBook(final int position) {
        ChatHttpParams.getInstallSigle(mContext).chatHttpRequest(26, bookInfos.get(position).index, mTracker.device_sn, null, null, null, null, null, null, new ChatCallbackResult() {
            @Override
            public void callBackStart() {
                super.callBackStart();
                ProgressDialogUtil.show(mContext);
            }

            @Override
            public void callBackResult(String result) {
                ProgressDialogUtil.dismiss();
                bookInfos.remove(position);
                if (bookInfos == null || bookInfos.size() < 1)
                    ll_phone_remind.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void callBackFailResult(String result) {
                ProgressDialogUtil.dismiss();
                ToastUtil.show(mContext, result);
            }
        });
    }

    /**
     * 添加结果返回
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1 && data != null) {
            PhonebookInfo info = (PhonebookInfo) data.getSerializableExtra("phonebookinfo");
            if (info == null)
                return;
            ll_phone_remind.setVisibility(View.GONE);
            if (isUpdate)
                bookInfos.set(selectPosition, info);
            else
                bookInfos.add(info);
            adapter.notifyDataSetChanged();
        }
    }
}
