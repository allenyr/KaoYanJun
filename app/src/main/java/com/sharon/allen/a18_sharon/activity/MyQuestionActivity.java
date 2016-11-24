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

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.adapter.QuestionAdapter;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.model.Question;
import com.sharon.allen.a18_sharon.utils.AnimationUtils;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;
import com.sharon.allen.a18_sharon.utils.ToastUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/25.
 */

public class MyQuestionActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private QuestionAdapter adapter;
    private List<Question> questionList = new ArrayList<Question>();
    private List<Question> questionListTemp = new ArrayList<Question>();
    private int questionListTempLength;
    private MyOkHttp myOkHttp;
    private UserDataManager userDataManager;
    private int mLastItem;
    private static final int REFRESH_COMPLETE  = 1;
    private static final int UPLOAD_COMPLETE  = 2;
    public static final int WHAT_QUESTION_TYPE = 3;
    public static final int WHAT_QUESTION_TYPE_COMPLETE = 4;

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REFRESH_COMPLETE:
                    questionList = pullJson((String) msg.obj);
                    adapter.refresh(questionList);
                    mPullRefreshLayout.setRefreshing(false);
                    break;
                case UPLOAD_COMPLETE:
                    questionListTemp = pullJson((String) msg.obj);
                    if (questionListTemp.size()!=0){
                        for(Question question : questionListTemp){
                            questionList.add(question);
                        }
                        adapter.refresh(questionList);
                    }

                    break;
                case WHAT_QUESTION_TYPE:
                    ArrayList<String> list = new ArrayList<String>();
                    int type =  msg.arg1;
                    int questionId =  msg.arg2;
                    LogUtils.i("type="+type+",itemId="+questionId);
                    switch (type){
                        case 0:
                            list.add(type+"");
                            list.add(questionList.get(questionId).getId()+"");
                            myOkHttp.okhttpGet(handler,WHAT_QUESTION_TYPE_COMPLETE, Constant.Server.GET_PATH,list,40);
                            questionList.get(questionId).setType("0");
                            break;
                        case 1:
                            list.add(type+"");
                            list.add(questionList.get(questionId).getId()+"");
                            myOkHttp.okhttpGet(handler,WHAT_QUESTION_TYPE_COMPLETE,Constant.Server.GET_PATH,list,40);
                            questionList.get(questionId).setType("1");
                            break;
                        case 2:
                            list.add(questionList.get(questionId).getId()+"");
                            myOkHttp.okhttpGet(handler,WHAT_QUESTION_TYPE_COMPLETE,Constant.Server.GET_PATH,list,41);
                            questionList.remove(questionId);
                            adapter.refresh(questionList);
                            break;
                    }
                    break;
                case WHAT_QUESTION_TYPE_COMPLETE:
//                    questionList =  pullJson((String) msg.obj);
//                    adapter.refresh(questionList);
                    LogUtils.i("修改成功");
                    break;
                case 404:
                    ToastUtils.Toast(mContext,"网络连接失败");
                    break;
            }
        }
    };
    private Context mContext;
    private FloatingActionButton fab_my_question;
    private RelativeLayout rl_titlebar_back;
    private ImageView iv_titlebar_camera;
    private TextView tv_titlebar_title;
    private PullRefreshLayout mPullRefreshLayout;

    @Override
    public void initView() {
        setContentView(R.layout.activity_my_question);
        mRecyclerView = (RecyclerView) findViewById(R.id.lv_my_question);
        fab_my_question = (FloatingActionButton) findViewById(R.id.fab_my_question);

        rl_titlebar_back = (RelativeLayout) findViewById(R.id.rl_titlebar_back);
        iv_titlebar_camera = (ImageView) findViewById(R.id.iv_titlebar_camera);
        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        iv_titlebar_camera.setVisibility(View.INVISIBLE);
        rl_titlebar_back.setVisibility(View.VISIBLE);
        tv_titlebar_title.setText("我的提问");

        mPullRefreshLayout = (PullRefreshLayout) findViewById(R.id.prl_my_question);
        SystemBarTintUtils.setActionBar(this);
    }

    @Override
    public void initListener() {
        //刷新上下拉侦听
        mPullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LogUtils.i("刷新");
                getMyQuestion();
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    AnimationUtils.startXmlAnimation(mContext, fab_my_question,R.animator.translation_y_up);
                }
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    AnimationUtils.startXmlAnimation(mContext, fab_my_question,R.animator.translation_y_down);
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        fab_my_question.setOnClickListener(this);
        rl_titlebar_back.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mContext = MyQuestionActivity.this;
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
        // 创建一个BaseAdapter对象
        adapter = new QuestionAdapter(mContext,questionList,handler,userDataManager);
        adapter.setOnRecyclerViewListener(new QuestionAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent=new Intent();
                intent.setClass(mContext, ReplyActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("headurl",questionList.get(position).getHeadurl());
                bundle.putString("username",questionList.get(position).getUsername());
                bundle.putString("question",questionList.get(position).getQuestion());
                bundle.putString("time",questionList.get(position).getTime());
                bundle.putString("sex",questionList.get(position).getSex());
                bundle.putInt("userid",questionList.get(position).getUserid());
                bundle.putInt("id",questionList.get(position).getId());
                bundle.putString("type",questionList.get(position).getType());
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(int position) {
                LogUtils.i("onItemLongClick");
                return false;
            }

            @Override
            public void onLoadMoreData(int position) {
                LogUtils.i("底部加载事件");
                upLoadMyQuestion(position+1);
            }
        });
        mRecyclerView.setAdapter(adapter);

        getMyQuestion();

    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.fab_my_question:
                Intent intent = new Intent(mContext, CreateQuestionActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_titlebar_back:
                finish();
                break;
        }
    }

    //解析Json数据
    private List<Question> pullJson(String jsonData) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonData, new TypeToken<List<Question>>() {}.getType());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //获取未解决的问题
    public void getMyQuestion(){
        ArrayList<String> list = new ArrayList<String>();
        list.add(userDataManager.getId()+"");
        myOkHttp.okhttpGet(handler,REFRESH_COMPLETE, Constant.Server.GET_PATH,list,44);
    }

    //加载未解决的问题
    public void upLoadMyQuestion(int listviewSize){
        ArrayList<String> list = new ArrayList<String>();
        list.add(listviewSize+"");
        list.add(userDataManager.getId()+"");
        myOkHttp.okhttpGet(handler,UPLOAD_COMPLETE, Constant.Server.GET_PATH,list,45);
    }

}
