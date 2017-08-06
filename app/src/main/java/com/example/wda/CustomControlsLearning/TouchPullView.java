package com.example.wda.CustomControlsLearning;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by wda on 2017/7/27.
 */
public class TouchPullView extends View {
    private Paint mCirclePaint;
    private int mCircleRadius = 50;
    private float mCirclePointX,mCirclePointY;
    private float mProgress;
    private int mDragHeight = 400;
    private int mtargetWidth = 400;
    private Path mPath = new Path();
    private Paint mPaint;
    //控制控制点的Y坐标
    private int mTargetGravity;
    //角度变换 0-135
    private int mTangentAngle = 110;
    private Interpolator mProgressInterpolator = new DecelerateInterpolator();

    public TouchPullView(Context context) {
        super(context);
        init();
    }

    public TouchPullView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchPullView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public TouchPullView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        //抗锯齿
        p.setAntiAlias(true);
        //防抖动
        p.setDither(true);
        //填充
        p.setStyle(Paint.Style.FILL);
        p.setColor(0xFF000000);
        mCirclePaint = p;

        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        //抗锯齿
        p.setAntiAlias(true);
        //防抖动
        p.setDither(true);
        //填充
        p.setStyle(Paint.Style.FILL);
        p.setColor(0xFF000000);
        mPaint = p;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthmode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int heightmode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int iHeight = (int)((mDragHeight*mProgress+0.5f)+getPaddingBottom()+getPaddingTop());

        int iWidth = 2*mCircleRadius+getPaddingRight()+getPaddingLeft();
        int measurewidth,measureheight;
        if(widthmode==MeasureSpec.EXACTLY){
            measurewidth = width;
        }else if(widthmode==MeasureSpec.AT_MOST){
            measurewidth = Math.min(iWidth,width);
        }else{
            measurewidth =iWidth;
        }

        if(heightmode==MeasureSpec.EXACTLY){
            measureheight = height;
        }else if(heightmode==MeasureSpec.AT_MOST){
            measureheight= Math.min(iHeight,height);
        }else{
            measureheight= iHeight;
        }
        setMeasuredDimension(measurewidth,measureheight);



    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updatePathLayout();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //进行基础坐标转换
        int count = canvas.save();
        float tranX = (getWidth()-getValueByLine(getWidth(),mtargetWidth,mProgress))/2;
        canvas.translate(tranX,0);
        //画贝塞尔曲线
        canvas.drawPath(mPath,mPaint);


        //画圆
        canvas.drawCircle(mCirclePointX,
                mCirclePointY,
                mCircleRadius,
                mCirclePaint);
        canvas.restoreToCount(count);
    }
    public void setProgress(float progress){
        Log.d("d", "setProgress: "+progress);
        mProgress=progress;
        //请求重新进行测量
        requestLayout();
    }

    private void updatePathLayout(){
        final float progress = mProgressInterpolator.getInterpolation(mProgress);

//        path.reset();
        final float w = getValueByLine(getWidth(),mtargetWidth,mProgress);
        final float h = getValueByLine(0,mDragHeight,mProgress);
        final float cPointX = w/2.0f;
        final float cRadius = mCircleRadius;
        final float cPointY = h-cRadius;
        //控制点结束的Y值
        final float endControlY = mTargetGravity;

        //更新圆的坐标
        mCirclePointX = cPointX;
        mCirclePointY = cPointY;
        final Path path = mPath;

        path.reset();
        path.moveTo(0,0);
        //左边部分的结束点和控制点
        float lEndPointX,lEndPointY;
        float lCountrolPointX,lCountrolPointY;

        double radian = Math.toRadians(getValueByLine(0,mTangentAngle,mProgress));
        float x = (float)Math.sin(radian)*cRadius;
        float y = (float)(Math.cos(radian)*cRadius);
        lEndPointX = cPointX - x;
        lEndPointY = cPointY + y;
        //控制点Y坐标的变化
        lCountrolPointY = getValueByLine(0,endControlY,progress);
        float tHeight = lEndPointY-lCountrolPointY;
        float tWidth = (float)(tHeight/Math.tan(radian));
        lCountrolPointX = lEndPointX-tWidth;


        //贝塞尔曲线
        path.quadTo(lCountrolPointX,lCountrolPointY,lEndPointX,lEndPointY);
        //链接到右边，画右边的贝塞尔曲线
        path.lineTo(cPointX+cPointX-lEndPointX ,lEndPointY);
        path.quadTo(cPointX+cPointX-lCountrolPointX,lCountrolPointY,w,0);



    }
    private float getValueByLine(float start,float end,float Progress){
        return start+(end-start)*Progress;
    }
    private ValueAnimator valueAnimator;

    public void release(){
        if(valueAnimator==null){
            ValueAnimator animator = ValueAnimator.ofFloat(mProgress,0f);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(400);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Object val = animation.getAnimatedValue();
                    if(val instanceof Float){
                        setProgress((Float) val);
                    }
                }
            });
            valueAnimator = animator;
        }else{
            valueAnimator.cancel();
            valueAnimator.setFloatValues(mProgress,0f);

        }
        valueAnimator.start();
    }
}
