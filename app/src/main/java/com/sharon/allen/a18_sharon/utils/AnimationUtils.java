package com.sharon.allen.a18_sharon.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;

import com.sharon.allen.a18_sharon.R;

/**
 * Created by Administrator on 2016/11/3.
 */

public class AnimationUtils {

    private ObjectAnimator oa1;
    private ObjectAnimator oa2;
    private ObjectAnimator oa3;
    private ObjectAnimator oa4;

    public void translate(View view){
        oa1 = ObjectAnimator.ofFloat(view, "translationX", 0, 70, 30, 100);
        //执行时间
        oa1.setDuration(2000);
        //循环播放次数
        oa1.setRepeatCount(1);
        //循环播放模式，RESTART：重新播，和REVERSE：倒序播放
        oa1.setRepeatMode(ValueAnimator.REVERSE);
        //播放
        oa1.start();
    }

    public void scale(View view){
        oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0.2f, 2, 1, 2.5f);
        oa2.setDuration(2000);
        oa2.setRepeatCount(1);
        oa2.setRepeatMode(ValueAnimator.REVERSE);
        oa2.start();
    }

    public void alpha(View view){
        oa3 = ObjectAnimator.ofFloat(view, "alpha", 0.2f, 1);
        oa3.setDuration(2000);
        oa3.setRepeatCount(1);
        oa3.setRepeatMode(ValueAnimator.REVERSE);
        oa3.start();
    }

    public void rotate(View view){
        oa4 = ObjectAnimator.ofFloat(view, "rotation", 0, 360, 180, 720);
        oa4.setDuration(2000);
        oa4.setRepeatCount(1);
        oa4.setRepeatMode(ValueAnimator.REVERSE);
        oa4.start();
    }

    public void fly(View view){
        //创建动画师集合
        AnimatorSet set = new AnimatorSet();
        //排队飞
//		set.playSequentially(oa1, oa2, oa3, oa4);
        //一起飞
        set.playTogether(oa1, oa2, oa3, oa4);
        //设置属性动画师操作的对象
        set.setTarget(view);
        set.start();
    }

    public static void startXmlAnimation(Context context, View view,int id){
        //使用动画师填充器把xml资源文件填充成属性动画对象
        Animator animator = AnimatorInflater.loadAnimator(context,id);
        animator.setTarget(view);
        animator.start();
    }
}
