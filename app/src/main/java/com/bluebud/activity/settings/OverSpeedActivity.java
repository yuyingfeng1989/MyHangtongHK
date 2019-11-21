package com.bluebud.activity.settings;

import org.apache.http.Header;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.activity.BaseActivity;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.SpeedObj;
import com.bluebud.info.Tracker;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class OverSpeedActivity extends BaseActivity implements OnClickListener {
	private Button m_CommitBtn;
	private Dialog loadingDialog;
	private String m_TrackerNo;
	private Tracker m_CurTracker;
	private RadioGroup m_SpeedGroup;
	private int m_CheckId = R.id.rbtn_close;
	private int m_Index = -1;
	private int m_Position;
	private int[] m_RbtnIds = { R.id.rbtn_cancel, R.id.rbtn_speed_60,
			R.id.rbtn_speed_80, R.id.rbtn_speed_100, R.id.rbtn_speed_120,
			R.id.rbtn_speed_140, R.id.rbtn_speed_160, R.id.rbtn_speed_200 };
	private int[] m_Speeds = { 0, 60, 80, 100, 120, 140, 160, 200 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addContentView(R.layout.activity_over_speed);
		findID();
		setData();
		Listener();
		GetSpeedNetworkConnect();
	}

	public void findID() {
		loadingDialog = new Dialog(this, R.style.Transparent_Dialog);
		loadingDialog.setContentView(R.layout.loading_dialog);

		m_CommitBtn = (Button) findViewById(R.id.btn_commit);
		m_SpeedGroup = (RadioGroup) findViewById(R.id.group);
	}

	public void setData() {
		m_CurTracker = UserUtil.getCurrentTracker(this);
		m_TrackerNo = m_CurTracker.device_sn;
	}

	public void Listener() {
		setBaseTitleText(R.string.alarm_speed);
		getBaseTitleLeftBack().setOnClickListener(this);
		m_CommitBtn.setOnClickListener(this);
		m_SpeedGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				m_CheckId = checkedId;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_commit:
			m_Position = getIndexFromId(m_CheckId);
			if (m_Position == -1) {
				CancelSpeedNetworkConnect();
				return;
			}
			SetSpeedNetworkConnect();
			break;

		case R.id.rl_title_back:
			finish();
			break;
		default:
			break;
		}
	}

	private void GetSpeedNetworkConnect() {
		String url = UserUtil.getServerUrl(this);
		RequestParams params = new RequestParams();
		params.put("class", "TrackersBusiness");
		params.put("function", "GetSpeed");
		String paramsStr = "\"" + m_TrackerNo + "\"";
		params.put("params", paramsStr);

		HttpClientUsage.getInstance().post(this, url, params,
				new AsyncHttpResponseHandlerReset() {
					@Override
					public void onStart() {
						super.onStart();
						ProgressDialogUtil.show(OverSpeedActivity.this);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] response) {
						super.onSuccess(statusCode, headers, response);
						ReBaseObj obj = GsonParse.reBaseObjParse(new String(
								response));
						if (obj == null)
							return;
						if (obj.code == 0) {
							SpeedObj speedObj = GsonParse
									.speedObjParse(new String(response));
							if (speedObj == null) {
								m_SpeedGroup.check(R.id.rbtn_close);
								return;
							}
							m_Index = getIndex(speedObj.speed);
							if (m_Index == -1)
								return;
							m_SpeedGroup.check(m_RbtnIds[m_Index]);// 设置选中项
							return;
						}
						ToastUtil.show(OverSpeedActivity.this, obj.what);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] errorResponse, Throwable throwable) {
						super.onFailure(statusCode, headers, errorResponse,
								throwable);
						ToastUtil.show(OverSpeedActivity.this,
								R.string.net_exception);
					}

					@Override
					public void onFinish() {
						super.onFinish();
						ProgressDialogUtil.dismiss();
					}
				});
	}

	private void SetSpeedNetworkConnect() {
		int speed = 0;
		speed = m_Speeds[m_Position];

		String url = UserUtil.getServerUrl(this);
		RequestParams params = new RequestParams();
		params.put("class", "TrackersBusiness");
		params.put("function", "SetSpeed");
		String paramsStr = "\"" + m_TrackerNo + "\"|\"" + speed + "\"|\"60\"";
		params.put("params", paramsStr);

		HttpClientUsage.getInstance().post(this, url, params,
				new AsyncHttpResponseHandlerReset() {
					@Override
					public void onStart() {
						super.onStart();
						ProgressDialogUtil.show(OverSpeedActivity.this);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] response) {
						super.onSuccess(statusCode, headers, response);
						ReBaseObj obj = GsonParse.reBaseObjParse(new String(
								response));
						if (obj == null)
							return;
						ToastUtil.show(OverSpeedActivity.this, obj.what);
						if (obj.code == 0) {
							m_Index = m_Position;
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] errorResponse, Throwable throwable) {
						super.onFailure(statusCode, headers, errorResponse,
								throwable);
						ToastUtil.show(OverSpeedActivity.this,
								R.string.net_exception);
					}

					@Override
					public void onFinish() {
						super.onFinish();
						ProgressDialogUtil.dismiss();
					}
				});
	}

	private void CancelSpeedNetworkConnect() {
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Accept-Language", "zh-cn");
		String url = UserUtil.getServerUrl(this);
		RequestParams params = new RequestParams();
		params.put("class", "TrackersBusiness");
		params.put("function", "CancelSpeed");

		// String trackNo = CommonUtils.getTrackerNo(this);
		String paramsStr = "\"" + m_TrackerNo + "\"";
		Log.d("TAG", "paramsStr:" + paramsStr);
		params.put("params", paramsStr);
		// 添加Cookie
		PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
		client.setCookieStore(myCookieStore);
		BasicClientCookie newCookie = new BasicClientCookie("cookiesare",
				"mycookie");
		newCookie.setVersion(1);
		newCookie.setDomain("mydomain.com");
		newCookie.setPath("/");
		myCookieStore.addCookie(newCookie);

		HttpClientUsage.getInstance().post(this, url, params,
				new AsyncHttpResponseHandlerReset() {
					@Override
					public void onStart() {
						super.onStart();
						ProgressDialogUtil.show(OverSpeedActivity.this);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] response) {
						super.onSuccess(statusCode, headers, response);
						ReBaseObj obj = GsonParse.reBaseObjParse(new String(
								response));
						if (obj == null)
							return;
						ToastUtil.show(OverSpeedActivity.this, obj.what);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] errorResponse, Throwable throwable) {
						super.onFailure(statusCode, headers, errorResponse,
								throwable);
						ToastUtil.show(OverSpeedActivity.this,
								R.string.net_exception);
					}

					@Override
					public void onFinish() {
						super.onFinish();
						ProgressDialogUtil.dismiss();
					}
				});

	}

	private int getIndex(int interval) {
		int len = m_Speeds.length;
		for (int i = 0; i < len; i++) {
			if (interval == m_Speeds[i])
				return i;
		}
		return -1;
	}

	private int getIndexFromId(int checkedId) {
		int len = m_Speeds.length;
		for (int i = 0; i < len; i++) {
			if (checkedId == m_RbtnIds[i])
				return i;
		}
		return -1;
	}
}
