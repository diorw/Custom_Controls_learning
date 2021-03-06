package com.example.wda.CustomControlsLearning;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by wda on 2017/7/18.
 * 慕课教程源码
 * 实现了多点触控，双击放大缩小，自由移动，解决与viewpager的冲突
 */
public class ZoomImageview extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener,ScaleGestureDetector.OnScaleGestureListener,OnTouchListener{
    private boolean once=false;
    private float mInitScala;
    private float mMidScalar;
    private float mMaxScalar;
    private Matrix matrix;

    /*
    * 捕获用户多点触控时缩放比例
    * */
    private ScaleGestureDetector mScaleGestureDetector;

    private int mLastPointerCount;
    private float mLastX;
    private float mLastY;
    private int mTouchSlop;
    private boolean isCanDrag;
    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;

    private boolean isAutoScale;
    private GestureDetector mGestureDetector;
    public ZoomImageview(Context context) {
        this(context,null);
    }

    public ZoomImageview(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ZoomImageview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        matrix = new Matrix();
        setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(context,this);
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mGestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if(isAutoScale)
                    return true;
                float x = e.getX();
                float y = e.getY();
                if(getScale()<mMidScalar){
                    //matrix.postScale(mMidScalar/getScale(),mMidScalar/getScale(),x,y);
                    //setImageMatrix(matrix);
                    postDelayed(new AutoScaleRunnable(mMidScalar,x,y),16);
                    isAutoScale = false;
                }else{
                    //matrix.postScale(mInitScala/getScale(),mInitScala/getScale(),x,y);
                    //setImageMatrix(matrix);
                    postDelayed(new AutoScaleRunnable(mInitScala,x,y),16);
                    isAutoScale = false;
                }
                return super.onDoubleTap(e);
            }
        });

    }
    private class AutoScaleRunnable implements Runnable{
        /*要缩放的目标的值*/
        private float mTargetScale;
        private float x;
        private float y;

        private final float BIGGER = 1.07f;
        private final float SMALL = 0.93f;

        private float tempScale;
        public AutoScaleRunnable(float mTargetScale,float x,float y){
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;
            if(getScale()<mTargetScale){
                tempScale = BIGGER;
            }
            if(getScale()>mTargetScale){
                tempScale = SMALL;
            }
        }
        @Override
        public void run() {
            /*进行缩放*/
            matrix.postScale(tempScale,tempScale,x,y);
            checkBorderCenterWhenScale();
            setImageMatrix(matrix);
            float currentScale=getScale();
            if((tempScale>1.0f&&currentScale<mTargetScale)||(tempScale<1.0f&&currentScale>mTargetScale)){
                //再次调用run
                postDelayed(this,16);


            }else{
                float scale  = mTargetScale/currentScale;
                matrix.postScale(scale,scale,x,y);
                checkBorderCenterWhenScale();
                setImageMatrix(matrix);
                isAutoScale = false;
            }
        }
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if(!once){
            //控件的宽和高
            int width = getWidth();
            int height = getHeight();
            //图片的宽和高
            Drawable d= getDrawable();
            if(d == null)
                return;
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            float scale = 1.0f;
            if(dw>width&&dh<height) {
                scale = width*1.0f/dw;


            }
            if(dh>height&&dw<width){
                scale = height*1.0f/dh;
            }
            if(dw<width&&dh<height||(dw>width&&dh>height)){
                scale = Math.min(width*1.0f/dw,height*1.0f/dh);
            }
            mInitScala= scale;
            mMaxScalar = 4*mInitScala;
            mMidScalar= 2*mInitScala;

            int dx = getWidth()/2-dw/2;
            int dy = getHeight()/2-dh/2;

            matrix.postTranslate(dx,dy);
            matrix.postScale(mInitScala,mInitScala,width/2,height/2 );


            setImageMatrix(matrix);
            once = true;
        }
    }
    /*
    * 获取当前图片缩放值*/
    public float getScale(){
        float[] values = new float[9];
        matrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        //当前这次缩放比例
        float scaleFactor = detector.getScaleFactor();
        //与原图的缩放比例
        float scale = getScale();


        if(getDrawable()==null)
            return true;
        if((scale<mMaxScalar&&scaleFactor>1.0f)||(scale>mInitScala&&scaleFactor<1.0f)){
            if(scale * scaleFactor<mInitScala){
                scaleFactor = mInitScala/scale;
            }
            else if(scale*scaleFactor>mMaxScalar){
                scaleFactor= mMaxScalar/scale;
            }
            System.out.println("scaleFactor2"+scaleFactor);
            matrix.postScale(scaleFactor,scaleFactor,detector.getFocusX(),detector.getFocusY());


        }
        //防止缩放时出现白边同时调整至中央
        checkBorderCenterWhenScale();


        setImageMatrix(matrix);
        return true;
    }
    private void checkBorderCenterWhenScale(){
        RectF rect = getMaterixRectF();
        float deltax = 0.0f;
        float deltay = 0.0f;
        int width = getWidth();
        int height = getHeight();
        if(rect.width()>=width){
            if(rect.left>0){
                deltax = -rect.left;

            }
            if(rect.right<width){
                deltax=width-rect.right;
            }
        }
        if(rect.height()>=height){
            if(rect.top>0){
                deltay = -rect.top;
            }
            if(rect.bottom<height){
                deltay = height-rect.bottom;
            }
        }
        if(rect.width()<width){
            deltax = width/2f-rect.right+rect.width()/2f;
        }
        if(rect.height()<height){
            deltay = height/2f-rect.bottom+rect.height()/2f;
        }
        matrix.postTranslate(deltax,deltay);
    }
    private RectF getMaterixRectF(){
        Matrix matrixx = matrix;
        RectF rectF = new RectF();
        Drawable d = getDrawable();
        if(d!=null) {
            rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrixx.mapRect(rectF);
        }
        return rectF;
    }
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
     //   Toast.makeText(getContext(),"点击了",Toast.LENGTH_LONG).show();

        if(mGestureDetector.onTouchEvent(event))
            return true;
        mScaleGestureDetector.onTouchEvent(event);
        //移动的中心点
        float x = 0;
        float y = 0;

        int pointerCount = event.getPointerCount();
        for(int i = 0;i<pointerCount;i++){
            x+=event.getX(i);
            y+=event.getY(i);

        }
        x/=pointerCount;
        y/=pointerCount;
        //如果手指记录改变了
        if(pointerCount!=mLastPointerCount){
            System.out.print("change");
            isCanDrag=false;
            mLastX=x;
            mLastY=y;
        }
        mLastPointerCount=pointerCount;
        RectF rectf = getMaterixRectF();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //当图片被放大的时候请求事件不被父view拦截
                if(rectf.width()>getWidth()+0.01||rectf.height()>getHeight()+0.01){
                    if(getParent() instanceof ViewPager)
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if(rectf.width()>getWidth()+0.01||rectf.height()>getHeight()+0.01){
                    if(getParent() instanceof ViewPager)
                        getParent().requestDisallowInterceptTouchEvent(true);
                }
                float dx = x-mLastX;
                float dy = y-mLastY;
                //判断移动距离是否可认为是移动
                if(!isCanDrag){
                    isCanDrag = isMoveAction(dx,dy);

                }
                if(isCanDrag){
                    RectF rectF = getMaterixRectF();
                    if(getDrawable()!=null){
                        if(rectF.width()<=getWidth()){
                            isCheckLeftAndRight=false;
                            dx = 0;
                        }
                        if(rectF.height()<=getHeight()){
                            isCheckTopAndBottom=false;
                            dy = 0;
                        }
                        matrix.postTranslate(dx,dy);
                        checkBorderWhenTranslate();
                        setImageMatrix(matrix);
                    }

                }
                mLastX =x;
                mLastY =y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;
            default:
                break;
        }
        return true;
    }
    public boolean isMoveAction(float dx,float dy){
        return Math.sqrt(dx*dx+dy*dy)>mTouchSlop;
    }
    public void checkBorderWhenTranslate(){
        RectF rectf= getMaterixRectF();
        float deltax = 0;
        float deltay = 0;
        int width = getWidth();
        int height = getHeight();
        if(rectf.top>0&&isCheckTopAndBottom){
            deltay=-rectf.top;
        }
        if(rectf.bottom<height&&isCheckTopAndBottom){
            deltay = height-rectf.bottom;
        }
        if(rectf.left>0&&isCheckLeftAndRight){
            deltax = -rectf.left;
        }
        if(rectf.right<width&&isCheckLeftAndRight){
            deltax = width-rectf.right;
        }
        matrix.postTranslate(deltax,deltay);

    }
}
