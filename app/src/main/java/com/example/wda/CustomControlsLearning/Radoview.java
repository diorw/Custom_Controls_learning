package com.example.wda.CustomControlsLearning;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wda on 2017/7/20.
 * 取自 http://blog.csdn.net/u014044853/article/details/50752799
 * 可复用的蜘蛛雷达图
 * path paint 的简单运用
 */
public class Radoview extends View {
    private int count = 6;//几边型
    private int[] radius = new int[]{100,200,300,400,500};
    private int maxradius = radius[radius.length-1];
    private int[] marks = new int[count];
    private String[] keys = new String[count];
    LinkedHashMap<String,Integer> map = new LinkedHashMap<>();
    private Paint paintline;
    private Paint painttext;
    private Paint paintMarkPoint;
    private Paint paintMarkLine;
    private double x;
    private double y;
    private double lastX;
    private double lastY;

    public Radoview(Context context) {
        super(context);
    }

    public Radoview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Radoview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        double littleAngle = 360/count;
        map.put("辅助",200);
        map.put("打钱",300);
        map.put("多样性",320);
        map.put("生存",350);
        map.put("推进",400);
        map.put("综合",420);

        Iterator iterator = map.entrySet().iterator();
        int j = 0;
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            String key = (String)entry.getKey();
            Integer value = (Integer)entry.getValue();
            marks[j] = value;
            keys[j] = key;
            j++;
        }

        paintline = new Paint();
        paintline.setColor(Color.BLACK);
        paintline.setStyle(Paint.Style.STROKE);
        paintline.setStrokeWidth(3);

        painttext = new Paint();
        painttext.setColor(Color.BLACK);
        painttext.setTextSize(25);

        for(int i = 0;i<radius.length;i++){
            //绘制每一级的多边形
            drawStrok(canvas,littleAngle,radius[i]);
        }
        paintMarkPoint = new Paint();
        paintMarkPoint.setColor(Color.parseColor("#7e3fb5"));
        paintMarkPoint.setStyle(Paint.Style.FILL);

        paintMarkLine = new Paint();
        paintMarkLine.setAntiAlias(true);
        paintMarkLine.setColor(Color.parseColor("#3f51b5"));
        paintMarkLine.setStyle(Paint.Style.FILL_AND_STROKE);
        paintMarkLine.setAlpha(87);

        Path path = new Path();
        path.reset();

        for(int i = 0;i<marks.length;i++){
            x = getPointX(littleAngle*i,marks[i]);
            y=  getPointY(littleAngle*i,marks[i]);
            canvas.drawCircle((float)x,(float)y,10,paintMarkPoint);
            if(i==0){
                path.moveTo((float)x,(float)y);
            }else{
                path.lineTo((float)x,(float)y);

            }
            lastX=x;
            lastY=y;
        }
        canvas.drawPath(path,paintMarkLine);

    }
    public void drawStrok(Canvas canvas,double littleAngle,double radius){
        for(int i = 0;i<count;i++){
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            x = getPointX(littleAngle*i,radius);
            y = getPointY(littleAngle*i,radius);
            canvas.drawPoint((float)x,(float)y,paint);
            canvas.drawLine((float)maxradius,(float)maxradius,(float)x,(float)y,paintline);
            if(i>0){
                canvas.drawLine((float)lastX,(float)lastY,(float)x,(float)y,paintline);

            }
            if(i==count-1){
                canvas.drawLine((float)x,(float)y,(float)getPointX(0,radius),(float)getPointY(0,radius),paintline);

            }
            lastX = x;
            lastY = y;
            if(radius==maxradius){
                float textsize = painttext.measureText(keys[i]);

                canvas.drawText(keys[i],(float)(lastX-25),(float)lastY+25,painttext);
            }

        }
    }
    public double getPointX(double angle,double radius){
        double res=0;
        double newAngle = getAngle(angle);
        double width = radius*Math.cos(newAngle/180*Math.PI);
        int qr = getQr(angle);
        switch (qr){
            case 1:
            case 2:
                res = maxradius+width;
                break;
            case 3:
            case 4:
                res = maxradius-width;
                break;
            default:
                break;

        }
        return res;

    }
    public double getPointY(double angle,double radius){
        double res=0;
        double newAngle = getAngle(angle);
        double width = radius*Math.sin(newAngle/180*Math.PI);
        int qr = getQr(angle);
        switch (qr){
            case 1:
            case 4:
                res = maxradius+width;
                break;
            case 2:
            case 3:
                res = maxradius-width;
                break;
            default:
                break;

        }
        return res;

    }
    private int getQr(double angle)
    {
        int res = 0 ;
        if(angle>=0&&angle<=90){
            res = 1;
        }else if(angle>90&&angle<=180){
            res = 2;
        }else if(angle>180&&angle<=270){
            res = 3;
        }else if(angle>270&&angle<=360){
            res = 4;
        }
        return res;

    }
    private double getAngle(double angle){
        double res = angle;
        if(angle>=0&&angle<=90){
            res = 90-angle;
        }else if(angle>90&&angle<=180){
            res = angle - 90;

        }else if(angle>180&&angle<=270){
            res = 270 - angle;
        }else if(angle>270&&angle<=360){
            res = angle - 270;
        }
        return res;
    }
}
