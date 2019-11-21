package com.bluebud.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.bluebud.chat.utils.TrackDriverBean;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义折线图
 * Created by xiaoyunfei on 16/11/29.
 */
public class ChartView extends View {
    //    private int xylinecolor = 0xffe2e2e2;//xy坐标轴颜色
    private int xylinewidth = dpToPx(1);//折线宽度
    private int xytextcolor = 0xff7e7e7e;//xy坐标轴文字颜色
    private int titleColor = 0xcdcdcdcd;//标题颜色
    private int xytextsize = spToPx(12);//xy坐标轴文字大小
    private int linecolor = 0xff02bbb7;//折线图中折线的颜色
    private int interval = dpToPx(50);//x轴各个坐标点水平间距
    private int bgcolor = 0xffffffff;//背景颜色
    private Paint xyPaint;//绘制XY轴虚线对应的画笔
    private Paint xyTextPaint;//绘制XY轴的文本对应的画笔
    private Paint linePaint;//画折线对应的画笔
    private int width;//绘制实际图宽度
    private int height;//绘制实际图高度
    private int mWidth;//布局实际宽
    private int mHight;//布局实际高
    private int xOri;//x轴的原点坐标
    private int yOri;//y轴的原点坐标
    private float xInit;//第一个点X的坐标
    //    private List<String> xValue = new ArrayList<>();//x轴坐标对应的数据
    private List<Integer> yValue = new ArrayList<>(); //y轴坐标对应的数据
    private List<TrackDriverBean> list = new ArrayList<>(); //折线对应的数据
    private String title;//标题
    private String unit;//单位
    private int index;//1里程，2，油耗，3时长
    private int selectIndex = 0;//点击的点对应的X轴的第几个点，默认0
    //    private Rect xValueRect;//X轴刻度文本对应的最大矩形，为了选中时，在x轴文本画的框框大小一致
    private Typeface font_italic = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC);//正常斜
    private Typeface font_normal = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);//正常斜

    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
        initPaint();
    }

    /**
     * 初始化畫筆
     */
    private void initPaint() {
        xyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//x和y轴虚线
        xyPaint.setAntiAlias(true);
        xyPaint.setStrokeWidth(1f);
        xyPaint.setStyle(Paint.Style.STROKE);
//        xyPaint.setColor(xylinecolor);

        xyTextPaint = new Paint();//x和y轴刻度值
        xyTextPaint.setAntiAlias(true);
        xyTextPaint.setTextSize(xytextsize);
        xyTextPaint.setStrokeCap(Paint.Cap.ROUND);
        xyTextPaint.setColor(xytextcolor);
        xyTextPaint.setStyle(Paint.Style.STROKE);
        xyTextPaint.setTypeface(font_italic);

        linePaint = new Paint();//折线
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(xylinewidth);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setColor(linecolor);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 初始化
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.chartView, defStyleAttr, 0);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
//                case R.styleable.chartView_xylinecolor://xy坐标轴颜色
//                    xylinecolor = array.getColor(attr, xylinecolor);
//                    break;
                case R.styleable.chartView_xylinewidth://xy坐标轴宽度
                    xylinewidth = (int) array.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, xylinewidth, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.chartView_xytextcolor://xy坐标轴文字颜色
                    xytextcolor = array.getColor(attr, xytextcolor);
                    break;
                case R.styleable.chartView_xytextsize://xy坐标轴文字大小
                    xytextsize = (int) array.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, xytextsize, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.chartView_linecolor://折线图中折线的颜色
                    linecolor = array.getColor(attr, linecolor);
                    break;
                case R.styleable.chartView_interval://x轴各个坐标点水平间距
                    interval = (int) array.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, interval, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.chartView_bgcolor: //背景颜色
                    bgcolor = array.getColor(attr, bgcolor);
                    break;
            }
        }
        array.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            //这里需要确定几个基本点，只有确定了xy轴原点坐标，第一个点的X坐标值及其最大最小值
            mHight = getHeight();
            height = mHight - dpToPx(15);
            mWidth = getWidth();
            width = mWidth - dpToPx(30);
            interval = width / 13;
            float textYWdith = getTextBounds("0000", xyTextPaint).width();//Y轴文本最大宽度
            xOri = xylinewidth + (int) textYWdith - dpToPx(5);//dp2是y轴文本距离左边，以及距离y轴的距离
            Rect xValueRect = getTextBounds("000", xyTextPaint); //X轴文本最大高度
            float textXHeight = xValueRect.height();
            yOri = height - xylinewidth - (int) textXHeight;//dp3是x轴文本距离底边，dp2是x轴文本距离x轴的距离
            xInit = interval + xOri;
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        drawXY(canvas);
        drawBrokenLineAndPoint(canvas);
    }

    /**
     * 绘制折线和折线交点处对应的点
     */
    private void drawBrokenLineAndPoint(Canvas canvas) {
        if (list.size() <= 0)
            return;
        drawBrokenLine(canvas);
        drawBrokenPoint(canvas);
    }

    /**
     * 绘制折线
     */
    private void drawBrokenLine(Canvas canvas) {
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(linecolor);
        //绘制折线
        Path path = new Path();
        float x = xInit + interval * 0;
        float y = yOri - yOri * (1 - 0.1f) * alculatecValue(0) / yValue.get(yValue.size() - 1);
        path.moveTo(x, y);
        for (int i = 1; i < list.size(); i++) {
            x = xInit + interval * i;
            y = yOri - yOri * (1 - 0.1f) * alculatecValue(i) / yValue.get(yValue.size() - 1);
            path.lineTo(x, y);
        }
        canvas.drawPath(path, linePaint);
    }


    /**
     * 绘制折线对应的点
     */
    private void drawBrokenPoint(Canvas canvas) {
        float dp3 = dpToPx(4);
        float dp4 = dpToPx(5);
        //绘制节点对应的原点
        for (int i = 0; i < list.size(); i++) {
            float x = xInit + interval * i;
            float y = yOri - yOri * (1 - 0.1f) * alculatecValue(i) / yValue.get(yValue.size() - 1);
            //绘制选中的点
            if (i == selectIndex - 1) {
                linePaint.setStyle(Paint.Style.FILL);
                linePaint.setColor(getResources().getColor(R.color.text_red));
                canvas.drawCircle(x, y, dp4, linePaint);//绘制内圆
                drawFloatTextBox(canvas, x, y - dp4,  alculatecValue(i));

                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setColor(Color.WHITE);
                canvas.drawCircle(x, y, dp4, linePaint);

            } else {
                //绘制普通的节点
                linePaint.setStyle(Paint.Style.FILL);
                linePaint.setColor(bgcolor);
                canvas.drawCircle(x, y, dp3, linePaint);
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setColor(Color.WHITE);
                canvas.drawCircle(x, y, dp3, linePaint);
            }
        }
    }

    /**
     * 绘制显示Y值的浮动框
     */
    private void drawFloatTextBox(Canvas canvas, float x, float y, float text) {
        int dp6 = dpToPx(6);
        int dp18 = dpToPx(18);
        //p1
        Path path = new Path();
        path.moveTo(x, y);
        //p2
        path.lineTo(x - dp6, y - dp6);
        //p3
        path.lineTo(x - dp18, y - dp6);
        //p4
        path.lineTo(x - dp18, y - dp6 - dp18);
        //p5
        path.lineTo(x + dp18, y - dp6 - dp18);
        //p6
        path.lineTo(x + dp18, y - dp6);
        //p7
        path.lineTo(x + dp6, y - dp6);
        //p1
        path.lineTo(x, y);
        canvas.drawPath(path, linePaint);

        linePaint.setColor(Color.WHITE);
        linePaint.setTextSize(spToPx(11));
        Rect rect = getTextBounds(String.valueOf(text), linePaint);
        canvas.drawText(text + "", x - rect.width() / 2, y - dp6 - (dp18 - rect.height()) / 2, linePaint);
        // x轴虚线
        Path yPath = new Path();
        DashPathEffect dash = new DashPathEffect(new float[]{10, 8, 10, 8}, 0);
        xyPaint.setPathEffect(dash);
        yPath.moveTo(x, yOri);
        yPath.lineTo(x, yOri - height + dpToPx(50));
        canvas.drawPath(yPath, xyPaint);
        linePaint.setTextSize(spToPx(12));
    }

    /**
     * 绘制XY坐标
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawXY(Canvas canvas) {
        //绘制y轴刻度
        int yLength = (int) (yOri * (1 - 0.1f) / (yValue.size() - 1));//y轴上面空出10%,计算出y轴刻度间距
        int size = yValue.size();

        // x轴虚线
        Path xPath = new Path();
        DashPathEffect dash = new DashPathEffect(new float[]{8, 10, 8, 10}, 0);
        xyPaint.setPathEffect(dash);
        xyPaint.setColor(xytextcolor);
        xyTextPaint.setColor(xytextcolor);
        for (int i = 0; i < size; i++) {
            //绘制Y轴刻度
            if (i < size - 1) {
                xPath.moveTo(xOri / 2, yOri - yLength * i + xylinewidth / 2);
                xPath.lineTo(width + dpToPx(10), yOri - yLength * i + xylinewidth / 2);
                canvas.drawPath(xPath, xyPaint);
//                //绘制Y轴文本
//                xyTextPaint.setColor(xytextcolor);
                if (index == 1)
                    xyTextPaint.setTextSize(dpToPx(10));
                else
                    xyTextPaint.setTextSize(dpToPx(12));
                String text = String.valueOf(yValue.get(i)) + unit;
                Rect rect = getTextBounds(text, xyTextPaint);
                canvas.drawText(text, 0, text.length(), xOri - rect.width() / 2, yOri - yLength * i - rect.height() / 2, xyTextPaint);
            } else {
                xyTextPaint.setTypeface(font_normal);
                xyTextPaint.setColor(titleColor);
                xyTextPaint.setTextSize(dpToPx(16));
                canvas.drawText(title, mWidth / 2-xyTextPaint.measureText(title)/2, yOri - yLength * i + dpToPx(15), xyTextPaint);
                xyTextPaint.setColor(xytextcolor);
                xyTextPaint.setTypeface(font_italic);

            }
        }

        xyTextPaint.setTextSize(dpToPx(12));
        for (int i = 0; i < list.size(); i++) { //绘制x轴刻度
            String date = list.get(i).getDate();
            String[] split = date.split("\\.");
            int month = Integer.valueOf(split[1]);
            float x = xInit + interval * i;
            if (x >= xOri) {//只绘制从原点开始的区域
                String text = String.valueOf(month);//绘制X轴文本
                xyTextPaint.setColor(xytextcolor);
                Rect rect = getTextBounds(text, xyTextPaint);
                if (i == selectIndex - 1) {
                    xyTextPaint.setColor(linecolor);
                    canvas.drawText(text, 0, text.length(), x - rect.width() / 2, yOri + 2 * rect.height(), xyTextPaint);
                } else {
                    canvas.drawText(text, 0, text.length(), x - rect.width() / 2, yOri + 2 * rect.height(), xyTextPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                clickAction(event);
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    /**
     * 点击X轴坐标或者折线节点
     */
    private boolean clickAction(MotionEvent event) {
        int dp8 = dpToPx(8);
        float eventX = event.getX();
        float eventY = event.getY();
        for (int i = 0; i < list.size(); i++) {
            //节点
            float x = xInit + interval * i;
            float y = yOri - yOri * (1 - 0.1f) * alculatecValue(i) / yValue.get(yValue.size() - 1);
            if (eventX >= x - dp8 && eventX <= x + dp8 && eventY >= y - dp8 && eventY <= y + dp8 && selectIndex != i + 1) {//每个节点周围8dp都是可点击区域
                selectIndex = i + 1;
                invalidate();
                return true;
            }
        }
        return false;
    }

    /**
     * 根据真实值动态计算长度
     */
    private float alculatecValue(int i) {
        float currValue;
        if (index == 1) {
            currValue = Utils.getDecimal(list.get(i).getKm(),"0.0");
        } else if (index == 2) {
            currValue = Utils.getDecimal(list.get(i).getFuel(),"0.0");
        } else {
            currValue = Utils.getDecimal(list.get(i).getTime()/60f,"0.0");
        }
        return currValue;
    }

    public void setValue(List<TrackDriverBean> value, List<Integer> yValue, String title, String unit, int index) {
        this.list = value;
        this.unit = unit;
        this.yValue = yValue;
        this.title = title;
        this.index = index;
        invalidate();
    }

    /**
     * 获取丈量文本的矩形
     *
     * @return
     */
    private Rect getTextBounds(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    /**
     * dp转化成为px
     */
    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f * (dp >= 0 ? 1 : -1));
    }

    /**
     * sp转化为px
     */
    private int spToPx(int sp) {
        float scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (scaledDensity * sp + 0.5f * (sp >= 0 ? 1 : -1));
    }
}

