package com.bluebud.obd_optimize.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebud.obd_optimize.minterface.IDriverInterface;
import com.bluebud.utils.Utils;

public class ObdPageIndicator extends HorizontalScrollView {
    private int[] widths;
    private LinearLayout.LayoutParams wrapTabLayoutParams;
    private LinearLayout.LayoutParams weightTabLayoutParams;
    private LinearLayout tabsContainer;
    private ViewPager pager;
    private int tabCount;
    private int currentPosition = 0;
    private Paint rectPaint;//画笔
    private int indicatorColor = Color.parseColor("#37c9ff");//底部指示线颜色
    private int tabTextSize = Utils.dipToPx(16);//标题的字体大小
    private int tabTextColor = Color.parseColor("#a4a4a4");// 标题未被选中时字体颜色
    private int tabTextColorSelected = Color.parseColor("#37c9ff");// 标题被选中时字体颜色
    private IDriverInterface mCallback;//回调接接口

    public ObdPageIndicator(Context context) {
        this(context, null);
    }

    public ObdPageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ObdPageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFillViewport(true);
        setWillNotDraw(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        tabsContainer.setLayoutParams(layoutParams);
        addView(tabsContainer);
        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        wrapTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        weightTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
    }

    public void setListener(IDriverInterface mCallback){
        this.mCallback = mCallback;
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        tabsContainer.removeAllViews();
        tabCount = pager.getAdapter().getCount();
        for (int i = 0; i < tabCount; i++) {
            addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
        }
        updateTabStyles();
    }

    private void addTextTab(final int position, String title) {
        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(position);
                for (int i = 0; i < tabCount; i++) {//使当前item高亮
                    View view = tabsContainer.getChildAt(i);
                    if (view instanceof TextView) {
                        currentPosition = position;//选中显示导航线条
                        invalidate();//重新绘制
                        TextView textView = (TextView) view;
                        textView.setTextColor(i == pager.getCurrentItem() ? tabTextColorSelected : tabTextColor);
                    }
                }
                mCallback.onclickPosition(position);
            }
        });
        tabsContainer.addView(tab, position, wrapTabLayoutParams);
    }

    private void updateTabStyles() {
        widths = new int[tabCount];
        for (int i = 0; i < tabCount; i++) {
            View v = tabsContainer.getChildAt(i);
            if (v instanceof TextView) {
                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tab.setTextColor(i == 0 ? tabTextColorSelected : tabTextColor);
            }
        }
    }

    /**
     * 一下这些代码是为了在设置weight的情况下，导航线也能跟标题长度一致
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int myWidth = getMeasuredWidth();// 其实就是屏幕的宽度
        int childWidth = 0;
        for (int i = 0; i < tabCount; i++) {
            childWidth += tabsContainer.getChildAt(i).getMeasuredWidth();
            if (widths[i] == 0) {
                widths[i] = tabsContainer.getChildAt(i).getMeasuredWidth();
            }
        }
        if (childWidth > 0 && myWidth > 0) {
            for (int i = 0; i < tabCount; i++) {
                tabsContainer.getChildAt(i).setLayoutParams(weightTabLayoutParams);
            }
        }
    }

    /**
     * draw底部的导航线
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode() || tabCount == 0) {
            return;
        }
        final int height = getHeight();
        rectPaint.setColor(indicatorColor);
        View currentTab = tabsContainer.getChildAt(currentPosition); // 默认在Tab标题的下面
        float currentOffWid = (currentTab.getWidth() - widths[currentPosition]) / 2;
        float lineLeft = currentTab.getLeft() + currentOffWid;
        float lineRight = currentTab.getRight() - currentOffWid;
        canvas.drawRect(lineLeft, height - Utils.dipToPx(3), lineRight, height - Utils.dipToPx(1), rectPaint);
    }

    /**
     * 底部指示线颜色
     *
     * @param indicatorColor
     */
    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    /**
     * 字体大小
     *
     * @param textSizePx
     */
    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    /**
     * 字体未被选中颜色
     *
     * @param textColor
     */
    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    /**
     * 字体被选中颜色
     *
     * @param textColorSelected
     */
    public void setTextColorSelected(int textColorSelected) {
        this.tabTextColorSelected = textColorSelected;
        updateTabStyles();
    }
}
