package com.sharon.allen.a18_sharon.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.adapter.MessageAdapter;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.model.Message;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.MySharePreference;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 */

public class MessageActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private Context mContext;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private static final int WHAT_GET_MESSAGE = 1;
    private static final int WHAT_CLEAR_MESSAGE = 2;
    private UserDataManager userDataManager;
    private TextView tv_titlebar_title;
    private ImageView iv_titlebar_camera;
    private RelativeLayout rl_titlebar_back;
    private MyOkHttp myOkHttp;
    private PullRefreshLayout mPullRefreshLayout;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_GET_MESSAGE:
                    messageList = (List<Message>) pullJson((String) msg.obj);
                    adapter.refresh(messageList);
                    mPullRefreshLayout.setRefreshing(false);
                    clearUnreadMessage();
                    break;
                case WHAT_CLEAR_MESSAGE:

                    break;

            }
        }
    };


    @Override
    public void initView() {
        setContentView(R.layout.activity_message);
        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText("我的消息");
        iv_titlebar_camera = (ImageView) findViewById(R.id.iv_titlebar_camera);
        iv_titlebar_camera.setVisibility(View.INVISIBLE);
        rl_titlebar_back = (RelativeLayout) findViewById(R.id.rl_titlebar_back);
        rl_titlebar_back.setVisibility(View.VISIBLE);
        mRecyclerView = (RecyclerView) findViewById(R.id.lv_message);
        mPullRefreshLayout = (PullRefreshLayout) findViewById(R.id.prl_message);
        SystemBarTintUtils.setActionBar(this);
    }

    @Override
    public void initListener() {
        rl_titlebar_back.setOnClickListener(this);
        //侦听刷新
        mPullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LogUtils.i("刷新");
                getMessage();
            }
        });
    }

    @Override
    public void initData() {
        mContext = MessageActivity.this;
        //获取单例个人数据
        userDataManager = UserDataManager.getInstance(getApplicationContext());
        //获取okhttp对象
        myOkHttp = new MyOkHttp();
        //mRecyclerView初始化
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
//                        .color(R.color.colorGrey)
                        .sizeResId(R.dimen.divider)
                        .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                        .build());
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        adapter = new MessageAdapter(mContext,messageList,handler);
        adapter.setOnRecyclerViewListener(new MessageAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                LogUtils.i("点击事件");
            }

            @Override
            public boolean onItemLongClick(int position) {
                LogUtils.i("长按事件");
                return false;
            }

            @Override
            public void onLoadMoreData(int position) {
                LogUtils.i("底部加载事件");
            }
        });
        mRecyclerView.setAdapter(adapter);

//        MySharePreference.putSP(mContext,"message_readed",userDataManager.getMessagenum());
        getMessage();
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.rl_titlebar_back:
                Intent intent = new Intent();
                setResult(2,intent);
                finish();
                break;
        }
    }

    private void clearUnreadMessage(){
        ArrayList<String> list = new ArrayList<String>();
        list.add(userDataManager.getId()+"");
        myOkHttp.okhttpGet(handler,WHAT_CLEAR_MESSAGE, Constant.Server.GET_PATH,list,48);
        userDataManager.setMessagenum(0);
    }

    private void getMessage(){
        ArrayList<String> list = new ArrayList<String>();
        list.add(userDataManager.getId()+"");
        myOkHttp.okhttpGet(handler,WHAT_GET_MESSAGE, Constant.Server.GET_PATH,list,32);
    }

    //解析Json数据
    private List pullJson(String jsonData) {
        if (jsonData!=null&&!jsonData.isEmpty()){
            try {
                Gson gson = new Gson();
                return gson.fromJson(jsonData, new TypeToken<List<Message>>() {}.getType());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
