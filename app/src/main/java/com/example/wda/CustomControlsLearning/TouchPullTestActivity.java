package com.example.wda.CustomControlsLearning;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;


public class TouchPullTestActivity extends AppCompatActivity {
    private float mTouchMoveStartY;
    private static final float TOUCH_MOVE_Y_MAX = 800;
    private TouchPullView touchPullView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_of_touchpull_layout);
        touchPullView = (TouchPullView)findViewById(R.id.touchpull);

        findViewById(R.id.touchLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //得到用户意图
                int action = event.getActionMasked();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        mTouchMoveStartY = event.getY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float y = event.getY();
                        if(y>=mTouchMoveStartY){
                            float moveSize = y-mTouchMoveStartY;
                            float progress = moveSize>=TOUCH_MOVE_Y_MAX
                                    ?1:moveSize/TOUCH_MOVE_Y_MAX;
                            touchPullView.setProgress(progress);
                        }
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });

    }
}
