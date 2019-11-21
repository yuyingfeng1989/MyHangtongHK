package com.bluebud.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.bluebud.liteguardian_hk.R;

import java.util.ArrayList;



/**EditText 状态监听模块
 * @author user
 *
 */

/**
 * @author user
 *
 */
public class TextSingleChangeUtils implements TextWatcher {
	private Context context;
	private ArrayList<EditText> edtArray = new ArrayList<EditText>();
	private Button btn;
	private boolean isDefault = false;

	public TextSingleChangeUtils() {
		
	}


	/**添加EditText
	 * @param edt
	 */
	public void addEditText(EditText edt) {
		edtArray.add(edt);
		edt.addTextChangedListener(this);
	}
	/**设置需要需要改变状态的按钮
	 * @param context
	 * @param btn
	 */
	public void setButton(Context context, Button btn, boolean isDefalut) {
		this.context = context;
		this.btn = btn;
		this.isDefault = isDefalut;
	}
	
	public void setButton(Context context, Button btn) {
		this.context = context;
		this.btn = btn;
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		boolean isBtnEnable = false;

		for (EditText edt : edtArray) {
			if (edt.getText().toString().length() > 0) {
				isBtnEnable = true;
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
			btn.setTextColor(context.getResources().getColor(R.color.white));
		} else {
			btn.setEnabled(false);
//			SkinSettingManager.getInstance((Activity) context)
//					.setBtnSkinsStyle(btn);
		//	btn.setBackgroundResource(R.drawable.btn_unenable_bg_style);
			btn.setTextColor(context.getResources().getColor(R.color.text_theme3));
		}
	}

}
