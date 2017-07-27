package com.example.wda.CustomControlsLearning;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by wda on 2017/7/27.
 */
public class TouchPullView extends View {
    private Paint mCirclePaint;
    private int mCircleRadius = 50;
    private float mCirclePointX,mCirclePointY;
    private float mProgress;
    private int mDragHeight = 800;
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
        mCirclePointX = getWidth()>>1;
        mCirclePointY = getHeight()>>1;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        canvas.drawCircle(mCirclePointX,
                mCirclePointY,
                mCircleRadius,
                mCirclePaint);
    }
    public void setProgress(float progress){
        Log.d("d", "setProgress: "+progress);
        mProgress=progress;
        //请求重新进行测量
        requestLayout();
    }
}
