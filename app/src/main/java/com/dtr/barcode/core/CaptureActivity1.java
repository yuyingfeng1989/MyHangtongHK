package com.dtr.barcode.core;

import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.liteguardian_hk.R;

import com.bluebud.utils.LogUtil;
import com.dtr.barcode.camera.CameraManager;
import com.dtr.barcode.executor.ResultHandler;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.common.HybridBinarizer;


public final class CaptureActivity1 extends BaseActivity implements SurfaceHolder.Callback, OnClickListener {

	private static final String TAG ="gf";
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private TextView statusView;
	private SurfaceView surfaceView=null;

	private Result lastResult;
	private boolean hasSurface;
	private IntentSource source;
	private Collection<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	
	//private Button from_gallery;
	private final int from_photo = 010;
	static final int PARSE_BARCODE_SUC = 3035;
	static final int PARSE_BARCODE_FAIL = 3036;
	String photoPath;
	ProgressDialog mProgress;

	// Dialog dialog;

	enum IntentSource {

		ZXING_LINK, NONE

	}

	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addContentView(R.layout.activity_qr_scan1);
		getBaseTitleLeftBack().setOnClickListener(this);
		setBaseTitleText(R.string.scan);
		hasSurface = false;
		surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
		if (null==surfaceView) {
			LogUtil.i(" SurfaceView is null");
		}
		inactivityTimer = new InactivityTimer(this);
		cameraManager = new CameraManager(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);
		statusView = (TextView) findViewById(R.id.tv_tip);
	
	}
	@Override
	protected void onResume() {
		super.onResume();
		handler = null;
		lastResult = null;
		resetStatusView();
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		inactivityTimer.onResume();
		source = IntentSource.NONE;
		decodeFormats = null;
	}
	

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	// 这里初始化界面，调用初始化相机
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if (holder == null) {
				Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
			}
			if (!hasSurface) {
				hasSurface = true;
				initCamera(holder);
			}
		}
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			hasSurface = false;
		}
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		}
		
		// 解析二维码
		public void handleDecode(final Result rawResult, Bitmap barcode) {
			inactivityTimer.onActivity();
			lastResult = rawResult;
			ResultHandler resultHandler = new ResultHandler(parseResult(rawResult));

			handler.postDelayed(new Runnable() {

	            @Override
	            public void run() {
	                handleText(rawResult.getText());
	            }
	        }, 800);
		}
	    private void handleText(String text) {
		       LogUtil.i(text);
		       Intent resultIntent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("result", text);
				resultIntent.putExtras(bundle);
				this.setResult(RESULT_OK, resultIntent);
				finish();
	    }
	    
	 // 初始化照相机，CaptureActivityHandler解码
		private void initCamera(SurfaceHolder surfaceHolder) {
			if (surfaceHolder == null) {
				throw new IllegalStateException("No SurfaceHolder provided");
			}
			if (cameraManager.isOpen()) {
				Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
				return;
			}
			try {
				cameraManager.openDriver(surfaceHolder);
				if (handler == null) {
					handler = new CaptureActivityHandler(this, decodeFormats, characterSet, cameraManager);
				}
			} catch (IOException ioe) {
				Log.w(TAG, ioe);
				displayFrameworkBugMessageAndExit();
			} catch (RuntimeException e) {
				Log.w(TAG, "Unexpected error initializing camera", e);
				displayFrameworkBugMessageAndExit();
			}
		}
		
		private void displayFrameworkBugMessageAndExit() {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.app_name));
			builder.setMessage("相机打开出错，请稍后重试");
			builder.setPositiveButton(R.string.confirm, new FinishListener(this));
			builder.setOnCancelListener(new FinishListener(this));
			builder.show();
		}
		public void restartPreviewAfterDelay(long delayMS) {
			if (handler != null) {
				handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
			}
			resetStatusView();
		}

		

	public String parsLocalPic(String path) {
		String parseOk = null;
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF8");

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 获取新的大小
		// 缩放比
		int be = (int) (options.outHeight / (float) 200);
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(path, options);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		System.out.println(w + "   " + h);
		RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader2 = new QRCodeReader();
		Result result;
		try {
			result = reader2.decode(bitmap1, hints);
			Log.i("steven", "result:" + result);
			parseOk = result.getText();

		} catch (NotFoundException e) {
			parseOk = null;
		} catch (ChecksumException e) {
			parseOk = null;
		} catch (FormatException e) {
			parseOk = null;
		}
		return parseOk;
	}

	
	


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {
				restartPreviewAfterDelay(0L);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			cameraManager.setTorch(false);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			cameraManager.setTorch(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	

	


	private static ParsedResult parseResult(Result rawResult) {
		return ResultParser.parseResult(rawResult);
	}

	

	




	private void resetStatusView() {
		statusView.setText(R.string.scan_text);
		statusView.setVisibility(View.VISIBLE);
		viewfinderView.setVisibility(View.VISIBLE);
		lastResult = null;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_title_back:
			finish();
			break;
		}
		
	}

}
