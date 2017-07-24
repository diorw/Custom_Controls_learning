package com.example.wda.CustomControlsLearning;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.example.wda.CustomControlsLearning.R;

import java.text.AttributedCharacterIterator;

/**
 * Created by wda on 2017/7/23.
 */
public class HorizontalProgressbarWithProgress  extends ProgressBar{
    private static final int DEFAULT_TEXT_SIEZ = 10;//sp
    private static final int DEFAULT_TEXT_COLOR = 0XFFFC00D1;
    private static final int DEFAULT_COLOR_UNREACH = 0XFFD3D6DA;
    private static final int DEFAULT_HEIGHT_UNREACH = 2;//dp
    private static final int DEFAULT_COLOR_REACH = DEFAULT_TEXT_COLOR;
    private static final int DEFAULT_HEIGHT_REACH = 2;//dp
    private static final int DEFAULT_TEXT_OFFSET = 10;//dp


    protected int mTextSize = sp2px(DEFAULT_TEXT_SIEZ);
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    protected int mUnReachColor = DEFAULT_COLOR_UNREACH;
    protected int mReachColor = DEFAULT_COLOR_REACH;
    protected int mUnReachHeight = dp2px(DEFAULT_HEIGHT_UNREACH);
    protected int mReachHeight = dp2px(DEFAULT_HEIGHT_REACH);
    protected int mTextOffset = DEFAULT_TEXT_OFFSET;

    private Paint mPaint = new Paint();
    private int mRealWidth;
    public HorizontalProgressbarWithProgress(Context context) {
        super(context,null);
    }

    public HorizontalProgressbarWithProgress(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HorizontalProgressbarWithProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttrs(attrs);
    }

    protected int dp2px(int dpval){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpval,
                getResources().getDisplayMetrics());

    }
    protected int sp2px(int spval){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,spval,
                getResources().getDisplayMetrics());
    }
    private void obtainStyledAttrs(AttributeSet attrs){
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressbarWithProgress);
        mTextSize = (int)ta.getDimensionPixelSize(R.styleable.HorizontalProgressbarWithProgress_progress_text_size,mTextSize);
        mTextColor = ta.getColor(R.styleable.HorizontalProgressbarWithProgress_progress_text_color,mTextColor);
        mTextOffset = (int)ta.getDimension(R.styleable.HorizontalProgressbarWithProgress_progress_text_offset,mTextOffset);
        mUnReachColor = ta.getColor(R.styleable.HorizontalProgressbarWithProgress_progress_unreach_color,mUnReachColor);
        mUnReachHeight = (int)ta.getDimension(R.styleable.HorizontalProgressbarWithProgress_progress_unreach_height,mReachHeight);
        mReachColor = ta.getColor(R.styleable.HorizontalProgressbarWithProgress_progress_reach_color,mReachColor);
        mReachHeight = (int)ta.getDimension(R.styleable.HorizontalProgressbarWithProgress_progress_reach_height,mReachHeight);
        ta.recycle();
        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthval = MeasureSpec.getSize(widthMeasureSpec);

        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(widthval,height);
        mRealWidth = getMeasuredWidth()-getPaddingLeft()-getPaddingRight();

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(),getHeight()/2);

        boolean noNeedUnReach = false;
        String text = getProgress()+"%";
        int textWidth = (int)mPaint.measureText(text);
        float radio = getProgress()*1.0f/getMax();
        float progressX = radio*mRealWidth;
        if(progressX+textWidth>mRealWidth){
            progressX = mRealWidth-textWidth;
            noNeedUnReach=true;
        }
        float endX = progressX-mTextOffset/2;
        if(endX>0){
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0,0,endX,0,mPaint);

        }
        mPaint.setColor(mTextColor);
        int y =(int)-(mPaint.descent()+mPaint.ascent())/2;
        canvas.drawText(text,progressX,y,mPaint);

        if(!noNeedUnReach){
            float start = progressX+mTextOffset/2+textWidth;
            mPaint.setColor(mUnReachColor);
            mPaint.setStrokeWidth(mUnReachHeight);
            canvas.drawLine(start,0,mRealWidth,0,mPaint);
        }

        canvas.restore();
    }

    private int measureHeight(int heightMeasureSpec){
        int result = 0;
        int heightMode  = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if(heightMode== MeasureSpec.EXACTLY){
            result = size;
        }
        else{
            int textHeight = (int)(mPaint.descent()-mPaint.ascent());
            result = getPaddingBottom()+getPaddingTop()+Math.max(Math.max(mReachHeight,mUnReachHeight),Math.abs(textHeight));
            if(heightMode == MeasureSpec.AT_MOST){
                result = Math.min(result,size);
            }

        }
        return result;
    }
}
