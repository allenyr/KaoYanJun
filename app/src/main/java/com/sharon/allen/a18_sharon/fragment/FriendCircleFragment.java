package com.sharon.allen.a18_sharon.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.activity.CommentActivity;
import com.sharon.allen.a18_sharon.activity.CreatMoodActivity;
import com.sharon.allen.a18_sharon.adapter.FriendCircleAdapter;
import com.sharon.allen.a18_sharon.base.BaseFragment;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.model.HotItem;
import com.sharon.allen.a18_sharon.utils.AnimationUtils;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.MySharePreference;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/7.
 */
public class FriendCircleFragment extends BaseFragment {

    private TextView tv_titlebar_title;
    private ImageView iv_titlebar_camera;
    private Context mContext;
    private PullRefreshLayout mPullRefreshLayout;
    private MyOkHttp myOkHttp;
    private FriendCircleAdapter adapter;
    private List<HotItem> hotlist = new ArrayList<HotItem>();
    private List<HotItem> hotlisttemp = new ArrayList<HotItem>();
    private RecyclerView mRecyclerView;
    private static final int REFRESH_COMPLETE = 6;
    private static final int UPLOAD_COMPLETE = 8;
    private static final int NET_ERROR = 9;
    public static final int DELETE_REQUEST = 10;
    public static final int DELETE_COMPLETE = 11;
    private FloatingActionButton fab_friend_circle;
    private ArrayList<String> okhttpParamList = new ArrayList<String>();

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    String jsonData = (String) msg.obj;
                    MySharePreference.putSP(mContext,"friendCircleCache",jsonData);
                    hotlist =  pullHotMicroBlogJson(jsonData);
                    if (hotlist!=null&&!hotlist.isEmpty()){
                        adapter.refresh(hotlist);
                    }
                    mPullRefreshLayout.setRefreshing(false);
                    break;
                case UPLOAD_COMPLETE:
                    hotlisttemp = pullHotMicroBlogJson((String) msg.obj);
                    if (hotlisttemp!=null&&hotlisttemp.size()!=0){
                        for(HotItem hotItem : hotlisttemp){
                            hotlist.add(hotItem);
                        }
                        adapter.refresh(hotlist);
                    }
                    break;
                case DELETE_REQUEST:
                    LogUtils.i("收到删除请求"+(int)msg.arg1);
                    okhttpParamList.clear();
                    okhttpParamList.add(msg.arg1+"");
                    myOkHttp.okhttpGet(handler,DELETE_COMPLETE,Constant.Server.GET_PATH, okhttpParamList,19);
                    hotlist.remove(msg.arg2);
                    adapter.refresh(hotlist);
                    break;
                case DELETE_COMPLETE:
                    Toast.makeText(mContext,"删除成功",Toast.LENGTH_SHORT).show();
                    break;
                case NET_ERROR:
                    mPullRefreshLayout.setRefreshing(false);
                    Toast.makeText(mContext,"网络连接失败",Toast.LENGTH_SHORT).show();
                    break;

            }
        };
    };

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_circle,null);
        tv_titlebar_title = (TextView) view.findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText("动态");
        iv_titlebar_camera = (ImageView) view.findViewById(R.id.iv_titlebar_camera);
        iv_titlebar_camera.setVisibility(View.GONE);
        // 获取 PullToRefreshListView View
        mRecyclerView = (RecyclerView) view.findViewById(R.id.lv_hot);
        fab_friend_circle = (FloatingActionButton) view.findViewById(R.id.fab_friend_circle);
        mPullRefreshLayout = (PullRefreshLayout) view.findViewById(R.id.prl_friend_circle);

        return view;
    }

    @Override
    public void initListener() {
        iv_titlebar_camera.setOnClickListener(this);
        //侦听刷新
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
                //SCROLL_STATE_IDLE:滑动停下不动了
                //SCROLL_STATE_TOUCH_SCROLL:触发滑动
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    AnimationUtils.startXmlAnimation(getActivity().getApplication(), fab_friend_circle,R.animator.translation_y_up);
                }
                else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    AnimationUtils.startXmlAnimation(getActivity().getApplication(), fab_friend_circle,R.animator.translation_y_down);
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        fab_friend_circle.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.i("FriendCircleFragment_onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.i("FriendCircleFragment_onDestroyView");
    }

    @Override
    public void initData() {
        mContext = getContext();
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
                LogUtils.i("点击事件");
                //该position由1开始
                LogUtils.i("position="+position);
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
//                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }

            @Override
            public boolean onItemLongClick(int position) {
                LogUtils.i("长按事件");
                return false;
            }

            @Override
            public void onLoadMoreData(int position) {
                LogUtils.i("底部加载事件");
                upLoadHotMicroBlog(position+1);
            }
        });
        mRecyclerView.setAdapter(adapter);
        String friendCircleCache = MySharePreference.getSP(mContext,"friendCircleCache","");
        LogUtils.i("friendCircleCache="+friendCircleCache);
        if (!friendCircleCache.isEmpty()){
            hotlist = pullHotMicroBlogJson(friendCircleCache);
            adapter.refresh(hotlist);
        }
        //获取朋友圈信息
        getHotMicroBlog();

    }

    @Override
    public void processClick(View v) {
        switch (v.getId()){
            case R.id.fab_friend_circle:
                Intent intent = new Intent(mContext, CreatMoodActivity.class);
                startActivity(intent);
                break;
        }
    }

    //获取人气条目Json数据
    private List<HotItem> pullHotMicroBlogJson(String jsonData) {
        if (jsonData!=null&&!jsonData.isEmpty()){
            try {
                Gson gson = new Gson();
                return gson.fromJson(jsonData, new TypeToken<List<HotItem>>() {}.getType());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    //刷新最新朋友圈
    public void getHotMicroBlog(){
        okhttpParamList.clear();
        myOkHttp.okhttpGet(handler,REFRESH_COMPLETE, Constant.Server.GET_PATH,okhttpParamList,4);
    }

    //加载最新朋友圈
    public void upLoadHotMicroBlog(int listviewSize){
        okhttpParamList.clear();
        okhttpParamList.add(listviewSize+"");
        myOkHttp.okhttpGet(handler,UPLOAD_COMPLETE, Constant.Server.GET_PATH,okhttpParamList,15);
    }

}
