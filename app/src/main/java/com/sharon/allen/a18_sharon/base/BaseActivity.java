package com.sharon.allen.a18_sharon.base;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;

/**
 * Created by Allen on 2016/5/27.
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //设置转场动画
//        Slide slide = new Slide();
//        slide.setSlideEdge(Gravity.RIGHT);
//        slide.setDuration(50);
//        getWindow().setEnterTransition(slide);
//        getWindow().setExitTransition(new Explode().setDuration(500));


        initView();
        initListener();
        initData();
    }

    public abstract void initView();
    public abstract void initListener();
    public abstract void initData();
    public abstract void processClick(View view);

    public void onClick(View view) {
        processClick(view);
    }
}
