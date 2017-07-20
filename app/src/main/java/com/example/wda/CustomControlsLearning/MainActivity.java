package com.example.wda.CustomControlsLearning;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ViewPager mviewpager;
    private int[] imgs = new int[]{R.drawable.boss,R.drawable.monstor};
    private ImageView[] mImageview = new ImageView[imgs.length];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vp);
        mviewpager=(ViewPager)findViewById(R.id.mviewpager);
        mviewpager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ZoomImageview imageview = new ZoomImageview(getApplicationContext());
                imageview.setImageResource(imgs[position]);
                container.addView(imageview);
                mImageview[position]=imageview;
                return imageview;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mImageview[position]);
            }

            @Override
            public int getCount() {
                return mImageview.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view ==object;
            }
        });
    }
}
