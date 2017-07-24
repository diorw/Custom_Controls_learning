package com.example.wda.CustomControlsLearning;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Created by wda on 2017/7/24.
 */
public class RoundProgressbarWithProgress extends HorizontalProgressbarWithProgress {
    private int mRadius = dp2px(30);
    private int mMaxPaintWidth;
    private Paint mPaint = new Paint();


    public RoundProgressbarWithProgress(Context context) {
        this(context,null);
    }

    public RoundProgressbarWithProgress(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundProgressbarWithProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mReachHeight = (int)(mUnReachHeight*2.5f);
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.RoundProgressbarWithProgress);

        mRadius = (int)ta.getDimension(R.styleable.RoundProgressbarWithProgress_radius,mRadius);
        ta.recycle();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMaxPaintWidth = Math.max(mReachHeight,mUnReachHeight);

        int expect = mRadius*2+mMaxPaintWidth+getPaddingLeft()+getPaddingRight();
        //内部根据不同模式判别
        int width = resolveSize(expect,widthMeasureSpec);
        int height = resolveSize(expect,heightMeasureSpec);
        int realWidth = Math.min(width,height);
        mRadius = (realWidth-getPaddingLeft()-getPaddingRight()-mMaxPaintWidth)/2;
        setMeasuredDimension(realWidth,realWidth);

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        String text = getProgress()+"%";
        float textWidth = mPaint.measureText(text);
        float textHeight = (mPaint.descent()+mPaint.ascent())/2;
        canvas.save();
        canvas.translate(getPaddingLeft()+mMaxPaintWidth/2,getPaddingTop()+mMaxPaintWidth/2);
        mPaint.setStyle(Paint.Style.STROKE);
        //unreachbar;
        mPaint.setColor(mUnReachColor);
        mPaint.setStrokeWidth(mUnReachHeight);
        canvas.drawCircle(mRadius,mRadius,mRadius,mPaint);
        //reachbar
        mPaint.setColor(mReachColor);
        mPaint.setStrokeWidth(mReachHeight);
        float sweepAngle = getProgress()*1.0f/getMax()*360;
        canvas.drawArc(new RectF(0,0,mRadius*2,mRadius*2),0,sweepAngle,false,mPaint);
        //text
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mTextColor);
        canvas.drawText(text,mRadius-textWidth/2,mRadius-textHeight,mPaint);


        canvas.restore();
    }
}
