package com.bluebud.utils;

import java.util.ArrayList;

import com.bluebud.liteguardian_hk.R;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;



/**EditText 状态监听模块
 * @author user
 *
 */
/**
 * @author user
 *
 */
public class TextChangeUtils implements TextWatcher {
	private ArrayList<EditText> edtArray = new ArrayList<EditText>();
	private Button btn;
	//	private boolean isDefault = false;
	boolean isBtnEnable = false;

	public TextChangeUtils() {
	}


	/**
	 * 添加EditText
	 *
	 * @param edt
	 */
	public void addEditText(EditText edt) {
		edtArray.add(edt);
		edt.addTextChangedListener(this);
	}

	/**
	 * 设置需要需要改变状态的按钮
	 *
	 * @param btn
	 */
	public void setButton(Button btn, boolean isDefalut) {
		this.btn = btn;
//		this.isDefault = isDefalut;
	}

	public void setButton(Button btn) {
		this.btn = btn;
		showBackgroud();

	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		showBackgroud();
	}


	/**
	 * 显示状态
	 */
	private void showBackgroud() {
		if (edtArray.size() < 1)
			return;
		isBtnEnable = true;
		for (EditText edt : edtArray) {
			if (edt.getText().toString().length() == 0) {
				isBtnEnable = false;
				break;
			}
		}
		if (isBtnEnable) {
			btn.setEnabled(true);
//			if(!isDefault)
//				SkinSettingManager.getInstance((Activity) context)
//						.setBtnSkinsStyle(btn);
			//else
			btn.setBackgroundResource(R.drawable.btn_theme_selector);
//			btn.setTextColor(context.getResources().getColor(R.color.white));
		} else {
			btn.setEnabled(false);
//			SkinSettingManager.getInstance((Activity) context)
//					.setBtnSkinsStyle(btn);
			btn.setBackgroundResource(R.drawable.btn_theme_bg);
//			btn.setTextColor(context.getResources().getColor(R.color.text_theme3));
		}
	}
}
