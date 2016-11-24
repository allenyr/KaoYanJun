package com.sharon.allen.a18_sharon.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Allen on 2016/5/27.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    //返回一个view对象，这个对象作为fragment的显示内容
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        onCreateViewInit();
        return initView(inflater,container,savedInstanceState);

    }

    //创建时调用
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListener();
        initData();
    }

    public void onCreateViewInit() {

    }

    public abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    public abstract void initListener();
    public abstract void initData();
    public abstract void processClick(View v);


    @Override
    public void onClick(View v) {
        processClick(v);
    }
}