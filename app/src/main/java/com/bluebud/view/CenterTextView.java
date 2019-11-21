package com.bluebud.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**********************************************************
 * @文件名称：CenterTextView.java
 * @文件作者：rzq
 * @创建时间：2015年7月2日 上午10:12:16
 * @文件描述：换行居中显示TextView
 * @修改历史：2015年7月2日创建初始版本
 **********************************************************/
@SuppressLint("AppCompatCustomView")
public class CenterTextView extends TextView {
	private StaticLayout myStaticLayout;
	private TextPaint tp;

	public CenterTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		initView();
	}

	private void initView() {
		tp = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		tp.setTextSize(getTextSize());
		tp.setColor(getCurrentTextColor());
		myStaticLayout = new StaticLayout(getText(), tp, getWidth(),
				Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		myStaticLayout.draw(canvas);
	}
}