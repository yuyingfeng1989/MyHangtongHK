package com.bluebud.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Utils;

/**
 * Created by Administrator on 2019/3/18.
 */

public class ProgressRing extends View {
    private int progressStartColor;
    private int progressMidColor;
    private int progressEndColor;
    //    private int bgColor;
//    private int bgStartColor;
    //    private int bgMidColor;
//    private int bgEndColor;
    private int progress;
    private float progressWidth;
    private int startAngle;
    private int sweepAngle;
    private boolean showAnim;

    private int mMeasureHeight;
    private int mMeasureWidth;

    private Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    private Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
//    Typeface font_italic = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);//正常斜
    Typeface font_bold = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);//正常加粗
    Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);//正常

    private RectF pRectF;

    private float unitAngle;

    private int curProgress = 0;

    public ProgressRing(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressRing);
        progressStartColor = ta.getColor(R.styleable.ProgressRing_pr_progress_start_color, Color.YELLOW);
        progressMidColor = ta.getColor(R.styleable.ProgressRing_pr_progress_mid_color, Color.YELLOW);
        progressEndColor = ta.getColor(R.styleable.ProgressRing_pr_progress_end_color, Color.YELLOW);

//        bgColor = ta.getColor(R.styleable.ProgressRing_pr_bg_start_color, getResources().getColor(R.color.obd_6e6e6e));
//        bgStartColor = ta.getColor(R.styleable.ProgressRing_pr_bg_start_color, Color.LTGRAY);
//        bgMidColor = ta.getColor(R.styleable.ProgressRing_pr_bg_mid_color, bgStartColor);
//        bgEndColor = ta.getColor(R.styleable.ProgressRing_pr_bg_end_color, bgStartColor);

        progress = ta.getInt(R.styleable.ProgressRing_pr_progress, 0);
        progressWidth = ta.getDimension(R.styleable.ProgressRing_pr_progress_width, 8f);
        startAngle = ta.getInt(R.styleable.ProgressRing_pr_start_angle, 150);
        sweepAngle = ta.getInt(R.styleable.ProgressRing_pr_sweep_angle, 240);
        showAnim = ta.getBoolean(R.styleable.ProgressRing_pr_show_anim, true);
        ta.recycle();

        unitAngle = (float) (sweepAngle / 100.0);
        bgPaint.setStrokeWidth(Utils.dipToPx(3));
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(progressWidth);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasureWidth = getMeasuredWidth();
        mMeasureHeight = getMeasuredHeight();
        if (pRectF == null) {
            float halfProgressWidth = progressWidth / 2;
            pRectF = new RectF(halfProgressWidth + getPaddingLeft(),
                    halfProgressWidth + getPaddingTop(),
                    mMeasureWidth - halfProgressWidth - getPaddingRight(),
                    mMeasureHeight - halfProgressWidth - getPaddingBottom());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!showAnim) {
            curProgress = progress;
        }

        drawBg(canvas);
        drawProgress(canvas);
        obdScore(canvas);
        if (curProgress < progress) {
            curProgress++;
            postInvalidate();
        }
    }

    /**
     * 只需要画进度之外的背景即可
     */
    private void drawBg(Canvas canvas) {
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setColor(getResources().getColor(R.color.obd_6e6e6e));
        canvas.drawArc(pRectF, 0, 360, false, bgPaint);
    }

    private void drawProgress(Canvas canvas) {

        for (int i = 1, end = (int) (curProgress * unitAngle); i <= end; i++) {
            float halfSweep = sweepAngle / 2;
            if (i - halfSweep > 0) {
                progressPaint.setColor(getGradient((i - halfSweep) / halfSweep, progressMidColor, progressStartColor));
            } else {
                progressPaint.setColor(getGradient((halfSweep - i) / halfSweep, progressMidColor, progressEndColor));
            }
            canvas.drawArc(pRectF, startAngle + i, 1, false, progressPaint);
        }
    }

    /**
     * 绘制圆分数值
     */
    private String score;

    private void obdScore(Canvas canvas) {
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setTextSize(Utils.dipToPx(40));
        bgPaint.setColor(Color.WHITE);
        bgPaint.setTypeface(font_bold);
        if (progress < 1) score = "--";
        else
            score = String.valueOf(getProgress());
        float scoreProgress = bgPaint.measureText(score) / 2;
        canvas.drawText(score, mMeasureWidth / 2 - scoreProgress, mMeasureHeight / 2 + Utils.dipToPx(5), bgPaint);
        bgPaint.setTextSize(Utils.dipToPx(14));
        bgPaint.setTypeface(font);
        bgPaint.setColor(getResources().getColor(R.color.obd_f5ffffff));
        String prompt = getResources().getString(R.string.obd_score);//得分
        float scoreUnit = bgPaint.measureText(prompt) / 2;
        canvas.drawText(prompt, mMeasureWidth / 2 - scoreUnit, mMeasureHeight / 2 + Utils.dipToPx(20), bgPaint);
    }

    public void setProgress(@IntRange(from = 0, to = 100) int progress) {
        this.progress = progress;
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public int getGradient(float fraction, int startColor, int endColor) {
        if (fraction > 1) fraction = 1;
        int alphaStart = Color.alpha(startColor);
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int alphaEnd = Color.alpha(endColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);
        int alphaDifference = alphaEnd - alphaStart;
        int redDifference = redEnd - redStart;
        int blueDifference = blueEnd - blueStart;
        int greenDifference = greenEnd - greenStart;
        int alphaCurrent = (int) (alphaStart + fraction * alphaDifference);
        int redCurrent = (int) (redStart + fraction * redDifference);
        int blueCurrent = (int) (blueStart + fraction * blueDifference);
        int greenCurrent = (int) (greenStart + fraction * greenDifference);
        return Color.argb(alphaCurrent, redCurrent, greenCurrent, blueCurrent);
    }
}
