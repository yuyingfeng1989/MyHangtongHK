package com.bluebud.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Utils;

/***
 * 自定义圆弧进度条
 *
 * @author liujing
 */
public class ProgressView extends View {

    //分段颜色
    private float maxCount;//最多进度
    private int currentCount;//当前进度
    private int score = -1;//分数
    private Paint mPaint;
    private Paint mTextPaint;
    private int mWidth, mHeight;
    private final static int TIMING = 10;
    private Handler timingHandler = new Handler();
    private Runnable timingRunnable = new Runnable() {

        @Override
        public void run() {
            currentCount++;
            if (currentCount < score) {
                timingHandler.postDelayed(timingRunnable, TIMING);//开始轮询数据
            } else {
                timingHandler.removeCallbacks(timingRunnable);
            }
            invalidate();

        }
    };

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context) {
        this(context, null);
    }

    //    Paint paintDot;
    Paint paintRoung;

    private void init() {
        mPaint = new Paint();
        mTextPaint = new Paint();
//         paintDot = new Paint();
        paintRoung = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPaint();
        RectF rectBlackBg = new RectF(20, 20, mWidth - 20, mHeight - 20);

        /**
         * 绘制灰色进度圆
         */
        canvas.drawArc(rectBlackBg, 0, 360, false, paintRoung);

        /**
         * 绘制圆分数值
         */
        mTextPaint.setTextSize(Utils.dipToPx(46));
        mTextPaint.setColor(Color.WHITE);
        if (score < 0) {
            canvas.drawText("--", mWidth / 2, mHeight / 2 + Utils.dipToPx(5), mTextPaint);
        } else {
            canvas.drawText(score + "", mWidth / 2, mHeight / 2 + Utils.dipToPx(5), mTextPaint);
        }
        mTextPaint.setTextSize(Utils.dipToPx(12));
        mTextPaint.setColor(getResources().getColor(R.color.obd_f5ffffff));
        String prompt = getResources().getString(R.string.obd_score);//得分
        canvas.drawText(prompt, mWidth / 2, mHeight / 2 + Utils.dipToPx(20), mTextPaint);

        /**
         * 绘制圆进度
         */
        float section = currentCount / maxCount;//设置进度颜色
        int[] colors;
        colors = new int[]{
                getResources().getColor(R.color.obd_64f6ff),
                getResources().getColor(R.color.obd_72b0ff),
//                    getResources().getColor(R.color.obd_72b0ff),
//                    getResources().getColor(R.color.obd_72b0ff),
                getResources().getColor(R.color.obd_72b0ff),
                getResources().getColor(R.color.obd_72b0ff),
                getResources().getColor(R.color.obd_64c6ff),
                getResources().getColor(R.color.obd_64c6ff),
                getResources().getColor(R.color.obd_64c6ff),
                getResources().getColor(R.color.obd_64c6ff),
                getResources().getColor(R.color.obd_64f6ff),
                getResources().getColor(R.color.obd_64f6ff),
                getResources().getColor(R.color.obd_64f6ff),
                getResources().getColor(R.color.obd_64f6ff)
        };
//            colors = new int[]{
//                    getResources().getColor(R.color.car_round),
//                    getResources().getColor(R.color.car_round1),
//                    getResources().getColor(R.color.car_round2),
//                    getResources().getColor(R.color.car_round3),
//                    getResources().getColor(R.color.car_round4),
//                    getResources().getColor(R.color.car_round5),
//                    getResources().getColor(R.color.car_round6),
//                    getResources().getColor(R.color.car_round7)};

//            if (section <= 1.0f / 3.0f) {
//                if (section != 0.0f) {
//                    mPaint.setColor(colors[0]);
//                } else {
//                    mPaint.setColor(Color.TRANSPARENT);
//                }
//            }else {
        LinearGradient shader = new LinearGradient(3, 3, (mWidth - 3) * section, (mHeight - 3) * section, colors, null, Shader.TileMode.MIRROR);
        mPaint.setShader(shader);
//            }
//        mPaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.SOLID));//设置发光
//        }
        if (score > 0) {
            canvas.drawArc(rectBlackBg, 135, section * 360, false, mPaint);//180为起始绘制位置
        }
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint.setAntiAlias(true);//画进度
        mPaint.setStrokeWidth((float) Utils.dipToPx(6));
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeCap(Cap.ROUND);
//        mPaint.setColor(Color.GRAY);

//        paintDot.setAntiAlias(true);//画点
//        paintDot.setStrokeWidth((float) dipToPx(5));
//        paintDot.setStyle(Style.STROKE);
//        paintDot.setStrokeCap(Cap.ROUND);
//        paintDot.setColor(Color.WHITE);

        paintRoung.setAntiAlias(true);//画灰色进度条
        paintRoung.setStrokeWidth((float) Utils.dipToPx(4));
        paintRoung.setStyle(Style.STROKE);
        paintRoung.setStrokeCap(Cap.ROUND);
        paintRoung.setColor(getResources().getColor(R.color.obd_6e6e6e));

        mTextPaint.setAntiAlias(true);//画文字
        mTextPaint.setStrokeWidth((float) 3.0);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(Utils.dipToPx(36));
        mTextPaint.setColor(Color.WHITE);

    }


//    /**
//     * 计算dip单位值
//     */
//    private int dipToPx(int dip) {
//        float scale = getContext().getResources().getDisplayMetrics().density;
//        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
//    }

    /**
     * 设置分数
     *
     * @param score
     */
    public void setScore(int score, float maxCount) {
        this.score = score;
        this.maxCount = maxCount;
        this.currentCount = 1;
        if (currentCount < score) {
            timingHandler.postDelayed(timingRunnable, TIMING);//开始轮询数据
        } else {
            timingHandler.removeCallbacks(timingRunnable);
        }

        invalidate();
    }


    /**
     * 计算控件大小
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY || widthSpecMode == MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize;
        } else {
            mWidth = 0;
        }
        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            mHeight = Utils.dipToPx(15);
        } else {
            mHeight = heightSpecSize;
        }
        setMeasuredDimension(mWidth, mHeight);
    }


    public void clear() {
        timingHandler.removeCallbacks(timingRunnable);
    }
}
