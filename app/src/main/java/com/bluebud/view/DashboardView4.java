package com.bluebud.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.bluebud.info.CarInfo;
import com.bluebud.liteguardian_hk.R;

/**
 * DashboardView style 4，仿汽车速度仪表盘
 * Created by woxingxiao on 2016-12-19.
 */
public class DashboardView4 extends View {

    private int mRadius; // 扇形半径
    private int mStartAngle = 137; // 起始角度
    private int mSweepAngle = 268; // 绘制角度
    private int mMin = 0; // 最小值
    private int mMax = 8; // 最大值
    private int mSection = 8; // 值域（mMax-mMin）等分份数
    private int mPortion = 2; // 一个mSection等分份数
    private String mHeaderText = "km/h"; // 表头
    private float mVelocity = mMin; // 实时速度
    private int mStrokeWidth; // 画笔宽度
    private int mLength1; // 长刻度的相对圆弧的长度
    private int mLength2; // 刻度读数顶部的相对圆弧的长度
    private int mPLRadius; // 指针长半径
//    private int mPSRadius; // 指针短半径

    private float mCenterX, mCenterY; // 圆心坐标
    private Paint mPaint;//刻度和数值画笔
    private Rect mRectText;
    private String[] mTexts;
    private CarInfo.MileageAndFuel data;//仪表动态数据
    Typeface font_bold_italic = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC);//粗斜
    Typeface font_italic = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC);//正常斜
    Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);//正常

    public DashboardView4(Context context) {
        this(context, null);
    }

    public DashboardView4(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashboardView4(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mStrokeWidth = dp2px(3);
        mLength1 = dp2px(8) + mStrokeWidth;
        mLength2 = mLength1 + dp2px(4);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mRectText = new Rect();
        mTexts = new String[mSection + 1]; // 需要显示mSection + 1个刻度读数
        for (int i = 0; i < mTexts.length; i++) {
            int n = (mMax - mMin) / mSection;
            mTexts[i] = String.valueOf(mMin + i * n);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = resolveSize(dp2px(160), widthMeasureSpec);
        mRadius = (width - mStrokeWidth * 2) / 2;
        setMeasuredDimension(width, width);

        mCenterX = mCenterY = getMeasuredWidth() / 2f;
        mPaint.setTextSize(sp2px(16));
        mPaint.getTextBounds("0", 0, "0".length(), mRectText);
        mPLRadius = mRadius - dp2px(40);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 画长刻度
         * 画好起始角度的一条刻度后通过canvas绕着原点旋转来画剩下的长刻度
         */
        double cos = Math.cos(Math.toRadians(mStartAngle - 180));
        double sin = Math.sin(Math.toRadians(mStartAngle - 180));
        float x0 = (float) (mStrokeWidth + mRadius * (1 - cos));
        float y0 = (float) (mStrokeWidth + mRadius * (1 - sin));
        float x1 = (float) (mStrokeWidth + mRadius - (mRadius - mLength1) * cos);
        float y1 = (float) (mStrokeWidth + mRadius - (mRadius - mLength1) * sin);
        canvas.save();
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.e4e4e4));//刻度颜色
        mPaint.setTypeface(font_bold_italic);
        mPaint.setStrokeWidth(mStrokeWidth);
        canvas.drawLine(x0, y0, x1, y1, mPaint);
        float angle = mSweepAngle * 1f / mSection;
        for (int i = 0; i < mSection; i++) {
            canvas.rotate(angle, mCenterX, mCenterY);
            if (i >= 6 && data != null && data.rotationRate > 0) {
                mPaint.setColor(ContextCompat.getColor(getContext(), R.color.viewfinder_laser));
                canvas.drawLine(x0, y0, x1, y1, mPaint);
            } else {
                canvas.drawLine(x0, y0, x1, y1, mPaint);
            }
        }
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.e4e4e4));
        canvas.restore();

        /**
         * 画短刻度
         * 同样采用canvas的旋转原理
         */
        canvas.save();
        mPaint.setStrokeWidth(mStrokeWidth/2);
        float x2 = (float) (mStrokeWidth + mRadius - (mRadius - mLength1) * cos);
        float y2 = (float) (mStrokeWidth + mRadius - (mRadius - mLength1) * sin);
        canvas.drawLine(x0, y0, x2, y2, mPaint);
        angle = mSweepAngle * 1f / (mSection * mPortion);
        for (int i = 1; i < mSection * mPortion; i++) {
            canvas.rotate(angle, mCenterX, mCenterY);
            if (i % mPortion == 0) { // 避免与长刻度画重合
                continue;
            }
            if (i >= 13 && data != null && data.rotationRate > 0) {
                mPaint.setStrokeWidth(mStrokeWidth/2);
                mPaint.setColor(ContextCompat.getColor(getContext(), R.color.viewfinder_laser));
                canvas.drawLine(x0, y0, x2, y2, mPaint);
            } else {
                canvas.drawLine(x0, y0, x2, y2, mPaint);
            }
        }
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.e4e4e4));
        canvas.restore();

        /**
         * 画长刻度读数
         */
        mPaint.setTextSize(sp2px(14));
        mPaint.setStyle(Paint.Style.FILL);
        float α;
        float[] p;
        angle = mSweepAngle * 1f / mSection;
        for (int i = 0; i <= mSection; i++) {
            α = mStartAngle + angle * i;
            p = getCoordinatePoint(mRadius - mLength2, α);
            if (α % 360 > 135 && α % 360 < 225) {
                mPaint.setTextAlign(Paint.Align.LEFT);
            } else if ((α % 360 >= 0 && α % 360 < 45) || (α % 360 > 315 && α % 360 <= 360)) {
                mPaint.setTextAlign(Paint.Align.RIGHT);
            } else {
                mPaint.setTextAlign(Paint.Align.CENTER);
            }
            mPaint.getTextBounds(mHeaderText, 0, mTexts[i].length(), mRectText);
            int txtH = mRectText.height();
            if (i == 0) {
                canvas.drawText(mTexts[i], p[0] + txtH / 5, p[1], mPaint);
//                mPaint.setTextSize(sp2px(9));
//                mPaint.setTypeface(font);
//                canvas.drawText("x1000", p[0] + txtH / 5, p[1] + txtH, mPaint);
//                canvas.drawText(" r/min", p[0] + txtH / 4, p[1] + 1.5f * txtH, mPaint);
//                mPaint.setTextSize(sp2px(14));
//                mPaint.setTypeface(font_bold_italic);
            } else if (i == 1) {
                canvas.drawText(mTexts[i], p[0], p[1] + txtH / 2, mPaint);
            } else if (i == 2) {
                canvas.drawText(mTexts[i], p[0] + txtH / 4, p[1] + txtH / 2, mPaint);
            } else if (i == 3) {
                canvas.drawText(mTexts[i], p[0] + txtH / 2, p[1] + txtH, mPaint);
            } else if (i == 4) {
                canvas.drawText(mTexts[i], p[0], p[1] + 1.2f * txtH, mPaint);
            } else if (i == 5) {
                canvas.drawText(mTexts[i], p[0] - txtH / 2, p[1] + txtH, mPaint);
            } else if (i == 6) {
                canvas.drawText(mTexts[i], p[0] - txtH / 5, p[1] + 0.8f * txtH, mPaint);
            } else if (i == mSection - 1) {
                canvas.drawText(mTexts[i], p[0], p[1] + txtH / 2, mPaint);
            } else {
                canvas.drawText(mTexts[i], p[0] - txtH / 2, p[1] + txtH / 6, mPaint);
            }
        }

        /**
         * 画指针
         */
        float θ = mStartAngle + mSweepAngle * (mVelocity - mMin) / (mMax - mMin); // 指针与水平线夹角
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.background_bundling));
        float[] p1 = getCoordinatePoint(mPLRadius + dp2px(10), θ);
        float[] p2 = getCeterCoordinatePoint(mPLRadius+ dp2px(25), θ);
        canvas.drawLine(p1[0], p1[1], p2[0], p2[1], mPaint);

        /**
         * 时速单位
         */
        mPaint.setTextSize(sp2px(13));
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_9));
        mPaint.setTypeface(font_italic);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.getTextBounds(mHeaderText, 0, mHeaderText.length(), mRectText);
        canvas.drawText(mHeaderText, mCenterX, mCenterY + mRectText.height()*2.5f, mPaint);

        /**
         * 当前时速
         */
        mPaint.setTextSize(dp2px(25));
        mPaint.setTypeface(font_bold_italic);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.e4e4e4));
        if (data != null)
            canvas.drawText(String.valueOf((int) data.speed), mCenterX, mCenterY + mRectText.height(), mPaint);
        else
            canvas.drawText("0", mCenterX, mCenterY+mRectText.height(), mPaint);


//        /**
//         * 画指针
//         */
//        float θ = mStartAngle + mSweepAngle * (mVelocity - mMin) / (mMax - mMin); // 指针与水平线夹角
//        mPaint.setStrokeWidth(mStrokeWidth * 1.5f);
//        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.background_bundling));
//        float[] p1 = getCoordinatePoint(mPLRadius+dp2px(14), θ);
//        float[] p2 = getCeterCoordinatePoint(mPLRadius+dp2px(14), θ);
//        canvas.drawLine(p1[0], p1[1], p2[0], p2[1], mPaint);

//        /**
//         * 画实总里程数值和单位
//         */
//        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_9));
//        mPaint.setTypeface(font_italic);
//        mPaint.setTextAlign(Paint.Align.CENTER);
//        mPaint.getTextBounds(mHeaderText, 0, mHeaderText.length(), mRectText);
//        mPaint.setTextSize(sp2px(25));
//        if (data != null)
//            canvas.drawText(String.valueOf((int) (data.totalmileage + 0.5f)), mCenterX, mCenterY + mRectText.height() + dp2px(24), mPaint);
//        else
//            canvas.drawText("0", mCenterX, mCenterY + mRectText.height() + dp2px(24), mPaint);
//        mPaint.setTextSize(sp2px(16));
//        canvas.drawText("km", mCenterX, mCenterY + mRectText.height() + dp2px(40), mPaint);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }

    public float[] getCoordinatePoint(int radius, float angle) {
        float[] point = new float[2];
        double arcAngle = Math.toRadians(angle); //将角度转换为弧度
        if (angle < 90) {
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 90) {
            point[0] = mCenterX;
            point[1] = mCenterY + radius;
        } else if (angle > 90 && angle < 180) {
            arcAngle = Math.PI * (180 - angle) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 180) {
            point[0] = mCenterX - radius;
            point[1] = mCenterY;
        } else if (angle > 180 && angle < 270) {
            arcAngle = Math.PI * (angle - 180) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        } else if (angle == 270) {
            point[0] = mCenterX;
            point[1] = mCenterY - radius;
        } else {
            arcAngle = Math.PI * (360 - angle) / 180.0;
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        }
        return point;
    }

    public float[] getCeterCoordinatePoint(int radius, float angle) {
        float[] point = new float[2];
        double arcAngle = Math.toRadians(angle); //将角度转换为弧度
        float a = dp2px(40);
        if (angle < 90) {
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * (radius - a));
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * (radius - a));
        } else if (angle == 90) {
            point[0] = mCenterX;
            point[1] = mCenterY + radius - a;
        } else if (angle > 90 && angle < 180) {
            arcAngle = Math.PI * (180 - angle) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * (radius - a));
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * (radius - a));
        } else if (angle == 180) {
            point[0] = mCenterX - radius + a;
            point[1] = mCenterY;
        } else if (angle > 180 && angle < 270) {
            arcAngle = Math.PI * (angle - 180) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * (radius - a));
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * (radius - a));
        } else if (angle == 270) {
            point[0] = mCenterX;
            point[1] = mCenterY - radius + a;
        } else {
            arcAngle = Math.PI * (360 - angle) / 180.0;
            point[0] = (float) (mCenterX - a * Math.cos(arcAngle) + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + a * Math.sin(arcAngle) - Math.sin(arcAngle) * radius);
        }
        return point;
    }

    public void setVelocity(float velocity) {
        if ((mVelocity == velocity && velocity > 0) || velocity < mMin || velocity > mMax) {
            return;
        }
        mVelocity = velocity;
        postInvalidate();
    }

    public float getVelocity() {
        return mVelocity;
    }

    public void setDashboardValue(CarInfo.MileageAndFuel data) {
        this.data = data;
        postInvalidate();
    }
}
