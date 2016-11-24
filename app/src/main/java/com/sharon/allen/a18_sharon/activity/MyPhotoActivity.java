package com.sharon.allen.a18_sharon.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.adapter.FriendCircleAdapter;
import com.sharon.allen.a18_sharon.adapter.MessageAdapter;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.model.HotItem;
import com.sharon.allen.a18_sharon.utils.AnimationUtils;
import com.sharon.allen.a18_sharon.utils.LogUtils;

import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allen on 2016/5/27.
 */
public class MyPhotoActivity extends BaseActivity{

    private FriendCircleAdapter adapter;
    private RecyclerView mRecyclerView;
    private List<HotItem> hotlist = new ArrayList<HotItem>();
    private List<HotItem> hotlisttemp = new ArrayList<HotItem>();
    private int hotlisttempLength;
    private UserDataManager userDataManager;

    private static final int REFRESH_COMPLETE = 6;
    private static final int UPLOAD_COMPLETE = 8;
    private static final int NET_ERROR = 9;
    public static final int DELETE_REQUEST = 10;
    public static final int DELETE_COMPLETE = 11;

    private ImageView iv_titlebar_camera;
    public  TextView tv_titlebar_title;
    private RelativeLayout rl_titlebar_back;
    private Context mContext;
    private int mLastItem;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    hotlist = (List<HotItem>) pullHotMicroBlogJson((String) msg.obj);
                    adapter.refresh(hotlist);
                    mPullRefreshLayout.setRefreshing(false);
                    break;
                case UPLOAD_COMPLETE:
                    hotlisttemp = (List<HotItem>) pullHotMicroBlogJson((String) msg.obj);
                    if (hotlisttemp.size()!=0){
                        for(HotItem hotItem : hotlisttemp){
                            hotlist.add(hotItem);
                        }
                        adapter.refresh(hotlist);
                    }
                    break;
                case NET_ERROR:
                    mPullRefreshLayout.setRefreshing(false);
                    Toast.makeText(mContext,"网络连接失败",Toast.LENGTH_SHORT).show();
                    break;
                case DELETE_REQUEST:
                    LogUtils.i("收到删除请求"+(int)msg.arg1);
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(msg.arg1+"");
                    myOkHttp.okhttpGet(handler,DELETE_COMPLETE,Constant.Server.GET_PATH,list,19);
                    hotlist.remove(msg.arg2);
                    adapter.refresh(hotlist);
                    break;
                case DELETE_COMPLETE:
                    Toast.makeText(mContext,"删除成功",Toast.LENGTH_SHORT).show();
                    break;
            }
        };
    };
    private MyOkHttp myOkHttp;
    private FloatingActionButton fab_my_photo;
    private PullRefreshLayout mPullRefreshLayout;

    //显示
    @Override
    public void initView() {
        //填充布局，返回view对象
        setContentView(R.layout.activity_my_photo);
        fab_my_photo = (FloatingActionButton)findViewById(R.id.fab_my_photo);
        iv_titlebar_camera = (ImageView) findViewById(R.id.iv_titlebar_camera);
        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText("我的相册");
        rl_titlebar_back = (RelativeLayout) findViewById(R.id.rl_titlebar_back);
        rl_titlebar_back.setVisibility(View.VISIBLE);
        iv_titlebar_camera.setVisibility(View.INVISIBLE);
        mRecyclerView = (RecyclerView) findViewById(R.id.lv_my_photo);

        mPullRefreshLayout = (PullRefreshLayout) findViewById(R.id.prl_my_photo);

        SystemBarTintUtils.setActionBar(this);
    }

    //设置侦听
    @Override
    public void initListener() {
        rl_titlebar_back.setOnClickListener(this);
        fab_my_photo.setOnClickListener(this);
        //刷新上下拉侦听
        mPullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LogUtils.i("刷新");
                getHotMicroBlog();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    AnimationUtils.startXmlAnimation(mContext, fab_my_photo,R.animator.translation_y_up);
                }
                else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    AnimationUtils.startXmlAnimation(mContext, fab_my_photo,R.animator.translation_y_down);
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    //初始化
    @Override
    public void initData() {
        mContext = MyPhotoActivity.this;
        userDataManager = UserDataManager.getInstance(getApplicationContext());
        myOkHttp = new MyOkHttp();
        //mRecyclerView初始化
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(mContext)
//                        .color(R.color.colorGrey)
                        .sizeResId(R.dimen.divider)
                        .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                        .build());
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        adapter = new FriendCircleAdapter(mContext,hotlist,handler);
        adapter.setOnRecyclerViewListener(new FriendCircleAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent=new Intent();
                intent.setClass(mContext, CommentActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("headurl",hotlist.get(position).getHeadurl());
                bundle.putString("username",hotlist.get(position).getUsername());
                bundle.putString("time",hotlist.get(position).getTime());
                bundle.putString("mood",hotlist.get(position).getMood());
                bundle.putString("imgurl",hotlist.get(position).getImageurl());
                bundle.putInt("receiverid",hotlist.get(position).getUserId());
                bundle.putInt("commentcount",hotlist.get(position).getCommentcount());
                bundle.putInt("id",hotlist.get(position).getId());
                bundle.putString("sex",hotlist.get(position).getSex());
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }

            @Override
            public void onLoadMoreData(int position) {
                upLoadHotMicroBlog(position+1);
                LogUtils.i("onLoadMoreData");
            }
        });

        mRecyclerView.setAdapter(adapter);
        //获取朋友圈信息
        getHotMicroBlog();
    }

    //点击侦听
    @Override
    public void processClick(View v) {
        switch (v.getId()){
            case R.id.rl_titlebar_back:
                //返回
                finish();
                break;
            case R.id.fab_my_photo:
                Intent intent = new Intent(mContext, CreatMoodActivity.class);
                startActivity(intent);
                break;
        }
    }

    //获取人气条目Json数据
    private List pullHotMicroBlogJson(String jsonData) {
        Gson gson = new Gson();
        return gson.fromJson(jsonData, new TypeToken<List<HotItem>>() {}.getType());
    }

    //刷新我的相册
    public void getHotMicroBlog(){
        ArrayList<String> list = new ArrayList<String>();
        list.add(userDataManager.getId()+"");
        myOkHttp.okhttpGet(handler,REFRESH_COMPLETE,Constant.Server.GET_PATH,list,17);
    }

    //加载我的相册
    public void upLoadHotMicroBlog(int listviewSize){
        ArrayList<String> list = new ArrayList<String>();
        list.add(listviewSize+"");
        list.add(userDataManager.getId()+"");
        myOkHttp.okhttpGet(handler,UPLOAD_COMPLETE, Constant.Server.GET_PATH,list,18);
    }



}
