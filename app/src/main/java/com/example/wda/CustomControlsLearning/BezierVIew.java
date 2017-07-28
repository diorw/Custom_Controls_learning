package com.example.wda.CustomControlsLearning;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wda on 2017/7/28.
 */
public class BezierView extends View {
    public BezierView(Context context) {
        super(context);
        init();
    }

    public BezierView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private Path mBezier = new Path();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public BezierView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        Paint paint = mPaint;
        //抗锯齿
        paint.setAntiAlias(true);
        //防抖动
        paint.setDither(true);
        //填充
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        new Thread(){
            @Override
            public void run() {
                super.run();
                initBezier();
            }
        }.start();

    }
    private void initBezier(){
       float[] xPoints = new float[]{0,300,200,500,700,800,900};
       float[] yPoints = new float[]{0,300,700,1200,700,500,200};

        Path path = mBezier;
        int fps = 10000;
        for(int i = 0;i<=fps;i++){

            float progress = i/(float)fps;
            float x = calculateBezier(progress,xPoints);
            float y = calculateBezier(progress,yPoints);
            path.lineTo(x,y);
            postInvalidate();
            try{
                Thread.sleep(10);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }

    }
    /*
    * 计算某时刻的贝塞尔曲线所处的值(x或y)
    * */
    private float calculateBezier(float t,float... values){
        final int len = values.length;
        for(int i = len-1;i>0;i--){
            for(int j = 0 ;j<i;j++){
                values[j] = values[j]+(values[j+1]-values[j])*t;
            }
        }

        //运算时结果放在第一位
        return values[0];

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(mBezier,mPaint);

    }
}
