package com.bluebud.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.StepsInfo;
import com.bluebud.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by  felix on 2016/11/10.
 * Company :IV-Tech
 * Website :www.iv-tech.com
 * 芝麻分趋势图
 */

public class StepTrend extends View {
    private float viewWith;//整个view宽
    private float viewHeight;//整个view高

    private float brokenLineWith = 2.0f;//线的宽度

    private int brokenLineColor = 0xcccccccc;//画线
    private int straightLineColor = 0xcccccccc;//画折线
    private int textNormalColor = 0xcccccccc;//刻度字体颜色
    private int float_bg_color = 0xff31b188;//浮动背景颜色
    private int float_text_color = 0xffffffff;//浮动背景中的字体颜色
    private int out_circle_color = 0xffd0f3f2;//外圆点颜色
    private int buttom_text_color = 0xff7c7c7c;//底部文字颜色


    private List<String> stepLists;// = new String[]{"6月", "7月", "8月", "9月", "10月", "11月","12月"}时间集合
    private List<String> days;//日期
    private StepsInfo[] steps;//当前步数集合
    private int maxStep = 1500;//最大步数
    private int minStep = 0;//最小步数

    private int weekSize = 7;//最大显示数
    private int selectWeak = 7;//选中的月份
    private List<Point> scorePoints;
    private int textSize = Utils.dipToPx(15);
    private Paint brokenPaint;
    private Paint dottedPaint;
    private Paint textPaint;
    private Path brokenPath;

    public StepTrend(Context context) {
        super(context);
        initConfig(context, null);
        init();
    }

    public StepTrend(Context context, AttributeSet attrs) {
        super(context, attrs);
        initConfig(context, attrs);
        init();
    }

    public StepTrend(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfig(context, attrs);
        init();

    }

    /**
     * 初始化布局配置
     */
    private void initConfig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StepTrend);
        maxStep = a.getInt(R.styleable.StepTrend_max_step, 1500);
        minStep = a.getInt(R.styleable.StepTrend_min_step, 0);
        brokenLineColor = a.getColor(R.styleable.StepTrend_broken_line_color, brokenLineColor);
        a.recycle();
    }

    /**
     * 初始化控件
     */
    Paint drawimage;

    private void init() {
        brokenPath = new Path();
        brokenPaint = new Paint();
        brokenPaint.setAntiAlias(true);
        brokenPaint.setStyle(Paint.Style.STROKE);
        brokenPaint.setStrokeWidth(Utils.dipToPx(brokenLineWith));
        brokenPaint.setStrokeCap(Paint.Cap.ROUND);

        drawimage = new Paint();
        drawimage.setAntiAlias(true);

        dottedPaint = new Paint();
        dottedPaint.setAntiAlias(true);
        dottedPaint.setStyle(Paint.Style.STROKE);
        dottedPaint.setStrokeWidth(brokenLineWith);
        dottedPaint.setColor((straightLineColor));
        dottedPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor((textNormalColor));
        textPaint.setTextSize(Utils.dipToPx(15));

    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (steps == null)
            return;
        scorePoints = new ArrayList<>();
        float maxScoreYCoordinate = viewHeight * 0.15f;//最大高度
        float minScoreYCoordinate = viewHeight * 0.7f;//最小高度

        float newWith = viewWith - (viewWith * 0.1f) * 2;//分隔线距离最左边和最右边的距离是0.15倍的viewWith
        int coordinateX;

        for (int i = 0; i < steps.length; i++) {
            StepsInfo info = steps[i];
            Point point = new Point();
            coordinateX = (int) (newWith * ((float) (i) / (weekSize - 1)) + (viewWith * 0.1f));
            point.x = coordinateX;
            if (info == null) {
                info = new StepsInfo();
                info.step = 0;
            }
            if (info.step > maxStep) {
                info.step = maxStep;
            } else if (info.step < minStep) {
                info.step = minStep;
            }
            point.y = (int) (((float) (maxStep - info.step) / (maxStep - minStep)) * (minScoreYCoordinate - maxScoreYCoordinate) + maxScoreYCoordinate);
            scorePoints.add(point);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWith = w;
        viewHeight = h;
        initData();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);//画刻度数据
        drawBrokenLine(canvas);//画连接点的折线
        drawPoint(canvas);//画折线点
        drawMonthLine(canvas);//画下方刻度线
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.getParent().requestDisallowInterceptTouchEvent(true);//一旦底层View收到touch的action后调用这个方法那么父层View就不会再调用onInterceptTouchEvent了，也无法截获以后的action

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                onActionUpEvent(event);
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    private void onActionUpEvent(MotionEvent event) {
        boolean isValidTouch = validateTouch(event.getX(), event.getY());
        if (isValidTouch) {
            invalidate();
        }
    }

    /**
     * 是否是有效的触摸范围
     */
    private boolean validateTouch(float x, float y) {
        if (steps == null)
            return false;
        //曲线触摸区域
        for (int i = 0; i < scorePoints.size(); i++) {
            // dipToPx(8)乘以2为了适当增大触摸面积
            if (x > (scorePoints.get(i).x - Utils.dipToPx(8) * 2) && x < (scorePoints.get(i).x + Utils.dipToPx(8) * 2)) {
                if (y > (scorePoints.get(i).y - Utils.dipToPx(8) * 2) && y < (scorePoints.get(i).y + Utils.dipToPx(8) * 2)) {
                    selectWeak = i + 1;
                    return true;
                }
            }
        }

        //月份触摸区域
        //计算每个月份X坐标的中心点
        float monthTouchY = viewHeight * 0.7f - Utils.dipToPx(3);//减去dipToPx(3)增大触摸面积

        float newWith = viewWith - (viewWith * 0.1f) * 2;//分隔线距离最左边和最右边的距离是0.15倍的viewWith
        float validTouchX[] = new float[stepLists.size()];
        for (int i = 0; i < stepLists.size(); i++) {
            validTouchX[i] = newWith * ((float) (i) / (weekSize - 1)) + (viewWith * 0.1f);
        }

        if (y > monthTouchY) {
            for (int i = 0; i < validTouchX.length; i++) {
                Log.v("ScoreTrend", "validateTouch: validTouchX:" + validTouchX[i]);
                if (x < validTouchX[i] + Utils.dipToPx(8) && x > validTouchX[i] - Utils.dipToPx(8)) {
                    Log.v("ScoreTrend", "validateTouch: " + (i + 1));
                    selectWeak = i + 1;
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 绘制折线穿过的点
     */
    protected void drawPoint(Canvas canvas) {
        if (scorePoints == null) {
            return;
        }
        for (int i = 0; i < scorePoints.size(); i++) {
            dottedPaint.setStyle(Paint.Style.STROKE);
            dottedPaint.setColor(Color.WHITE);
            canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, Utils.dipToPx(4.5f), dottedPaint);
            dottedPaint.setStyle(Paint.Style.FILL);
            drawFloatBg(canvas, i);
            dottedPaint.setColor(float_bg_color);
            canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, Utils.dipToPx(3.5f), dottedPaint);
        }
    }

    /**
     * 绘制浮动框和字体
     *
     * @param canvas
     * @param i
     */
    private void drawFloatBg(Canvas canvas, int i) {
        if (steps == null)
            return;
        if (i == selectWeak - 1) {
            dottedPaint.setColor(out_circle_color);
            canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, Utils.dipToPx(5f), dottedPaint);
            drawFloatTextBackground(canvas, scorePoints.get(i).x, scorePoints.get(i).y - Utils.dipToPx(8f));//绘制浮动文本背景框
            textPaint.setColor(float_text_color);
            textPaint.setTextSize(Utils.dipToPx(12));
            if (steps[i] == null) {
                canvas.drawText(String.valueOf(0), scorePoints.get(i).x, scorePoints.get(i).y - Utils.dipToPx(5f) - textSize, textPaint);//绘制浮动文字
            } else {
                canvas.drawText(String.valueOf(steps[i].step), scorePoints.get(i).x, scorePoints.get(i).y - Utils.dipToPx(5f) - textSize, textPaint);//绘制浮动文字
            }
        }
    }

    /**
     * 绘制月份的直线(包括刻度)
     */
    private void drawMonthLine(Canvas canvas) {
        brokenPaint.setColor(brokenLineColor);
        brokenPaint.setStrokeWidth(Utils.dipToPx(0.5f));
        canvas.drawLine(Utils.dipToPx(5), viewHeight * 0.7f, viewWith - Utils.dipToPx(5f), viewHeight * 0.7f, brokenPaint);
    }

    /**
     * 绘制折线
     */
    private void drawBrokenLine(Canvas canvas) {
        if (steps == null)
            return;
        brokenPath.reset();
        brokenPaint.setStrokeWidth(Utils.dipToPx(brokenLineWith));
        brokenPaint.setColor(brokenLineColor);
        brokenPaint.setStyle(Paint.Style.STROKE);
        if (steps.length == 0) {
            return;
        }
        Log.v("ScoreTrend", "drawBrokenLine: " + scorePoints.get(0));
        brokenPath.moveTo(scorePoints.get(0).x, scorePoints.get(0).y);
        for (int i = 0; i < scorePoints.size(); i++) {
            brokenPath.lineTo(scorePoints.get(i).x, scorePoints.get(i).y);
        }
        canvas.drawPath(brokenPath, brokenPaint);

    }

    /**
     * 绘制文本
     */
    private void drawText(Canvas canvas) {
        if (stepLists == null || days == null)
            return;
        textPaint.setTextSize(Utils.dipToPx(12));
        textPaint.setColor(buttom_text_color);
        float newWith = viewWith - (viewWith * 0.1f) * 2;//分隔线距离最左边和最右边的距离是0.15倍的viewWith
        float coordinateX;//分隔线X坐标
        textPaint.setTextSize(Utils.dipToPx(12));
        textPaint.setStyle(Paint.Style.FILL);
        textSize = (int) textPaint.getTextSize();
        for (int i = 0; i < stepLists.size(); i++) {
            coordinateX = newWith * ((float) (i) / (weekSize - 1)) + (viewWith * 0.1f);
            if (i == selectWeak - 1) {
                textPaint.setStyle(Paint.Style.STROKE);//底部文字包含框
                RectF r2 = new RectF();
                r2.left = coordinateX - textSize - Utils.dipToPx(4);
                r2.top = viewHeight * 0.7f + Utils.dipToPx(4);// + textSize / 2
                r2.right = coordinateX + textSize + Utils.dipToPx(4);
                r2.bottom = viewHeight * 0.7f + Utils.dipToPx(4) + 2 * textSize + Utils.dipToPx(4);//dipToPx(8
                canvas.drawRoundRect(r2, 10, 10, textPaint);

                if (i <= scorePoints.size() - 1) {
                    Rect rect = new Rect();// 柱状图的形状
                    rect.left = (int) coordinateX - textSize - Utils.dipToPx(4);
                    rect.right = (int) coordinateX + textSize + Utils.dipToPx(4);
                    rect.top = scorePoints.get(i).y;
                    rect.bottom = (int) (viewHeight * 0.7);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_step_bg); // 绘图
                    canvas.drawBitmap(bitmap, null, rect, drawimage);
                }
            }
            //绘制时间
            canvas.drawText(stepLists.get(i), coordinateX, viewHeight * 0.7f + Utils.dipToPx(4) + textSize, textPaint);// + dipToPx(5)
            textPaint.setTextSize(Utils.dipToPx(13));
            canvas.drawText(days.get(i), coordinateX, viewHeight * 0.7f + Utils.dipToPx(6) + 2 * textSize, textPaint);// + dipToPx(5)
        }
    }

    /**
     * 绘制显示浮动文字的背景
     */
    private void drawFloatTextBackground(Canvas canvas, int x, int y) {
        brokenPath.reset();
        brokenPaint.setColor(float_bg_color);
        brokenPaint.setStyle(Paint.Style.FILL);

        //P1
        Point point = new Point(x, y);
        brokenPath.moveTo(point.x, point.y);

        //P2
        point.x = point.x + Utils.dipToPx(5);
        point.y = point.y - Utils.dipToPx(5);
        brokenPath.lineTo(point.x, point.y);

        //P3
        point.x = point.x + Utils.dipToPx(12);
        brokenPath.lineTo(point.x, point.y);

        //P4
        point.y = point.y - Utils.dipToPx(17);
        brokenPath.lineTo(point.x, point.y);

        //P5
        point.x = point.x - Utils.dipToPx(34);
        brokenPath.lineTo(point.x, point.y);

        //P6
        point.y = point.y + Utils.dipToPx(17);
        brokenPath.lineTo(point.x, point.y);

        //P7
        point.x = point.x + Utils.dipToPx(12);
        brokenPath.lineTo(point.x, point.y);

        //最后一个点连接到第一个点
        brokenPath.lineTo(x, y);

        canvas.drawPath(brokenPath, brokenPaint);
    }

    public void setScore(StepsInfo[] step, List<String> stepLists, List<String> days, int maxStep, int minStep, int weekSize, int selectWeak) {
        this.stepLists = stepLists;
        this.days = days;
        this.maxStep = maxStep;//最大步数
        this.minStep = minStep;//最小步数
        this.weekSize = weekSize;//最大显示数
        this.selectWeak = selectWeak;//选中的月份
        this.steps = step;
        initData();
        invalidate();
    }

    public void setRefreshValue(StepsInfo[] step, int maxStep) {
        this.steps = step;
        this.maxStep = maxStep;
        initData();
        invalidate();
    }

//    /**
//     * dip 转换成px
//     */
//    private int dipToPx(float dip) {
//        float density = getContext().getResources().getDisplayMetrics().density;
//        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
//    }

}
