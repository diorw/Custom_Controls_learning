package com.example.wda.CustomControlsLearning;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by wda on 2017/7/24.
 * 张鸿洋慕课 打造个性化进度条
 */
public class ProgressTestActivity extends Activity {
    private static final int MSG_UPDATE = 0x110;
    private HorizontalProgressbarWithProgress mProgress;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message org){
            int progress = mProgress.getProgress();
            mProgress.setProgress(++progress);
            if(progress>=100){
                mHandler.removeMessages(MSG_UPDATE);

            }
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE,100);
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_of_progressbar_layout);
//        mProgress = (HorizontalProgressbarWithProgress)findViewById(R.id.progress1);
//        mHandler.sendEmptyMessage(MSG_UPDATE);

    }
}
