package com.sharon.allen.a18_sharon.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sevenheaven.segmentcontrol.SegmentControl;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.activity.CreatMoodActivity;
import com.sharon.allen.a18_sharon.activity.CreateQuestionActivity;
import com.sharon.allen.a18_sharon.activity.MainActivity;
import com.sharon.allen.a18_sharon.activity.ReplyActivity;
import com.sharon.allen.a18_sharon.adapter.QuestionAdapter;
import com.sharon.allen.a18_sharon.base.BaseFragment;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.model.Question;
import com.sharon.allen.a18_sharon.utils.AnimationUtils;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.MySharePreference;
import com.sharon.allen.a18_sharon.utils.ShareSdkUtils;
import com.sharon.allen.a18_sharon.utils.ToastUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 */

public class QuestionFragment extends BaseFragment {

    private TextView tv_titlebar_title;
    private ImageView iv_titlebar_camera;
    private SegmentControl seg_question_switch;
    private RecyclerView mRecyclerView;
    private QuestionAdapter adapter;
    private List<Question> questionList = new ArrayList<Question>();
    private List<Question> questionListTemp = new ArrayList<Question>();
    private MyOkHttp myOkHttp;
    private UserDataManager userDataManager;

    private static final int REFRESH_COMPLETE  = 1;
    private static final int UPLOAD_COMPLETE  = 2;
    public static final int WHAT_QUESTION_TYPE = 3;
    public static final int WHAT_QUESTION_TYPE_COMPLETE = 4;
    public static final int WHAT_DELETE_COMPLETE = 5;
    private Context mContext;
    private ImageView fab_question;
    private PullRefreshLayout mPullRefreshLayout;

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REFRESH_COMPLETE:
                    String jsonData = (String) msg.obj;
                    MySharePreference.putSP(mContext,"questionCache",jsonData);
                    questionList = pullJson(jsonData);
                    if (questionList!=null&&!questionList.isEmpty()){
                        adapter.refresh(questionList);
                    }
                    mPullRefreshLayout.setRefreshing(false);
                    break;
                case UPLOAD_COMPLETE:
                    questionListTemp = pullJson((String) msg.obj);
                    if (questionListTemp!=null&&questionListTemp.size()!=0){
                        for(Question question : questionListTemp){
                            questionList.add(question);
                        }
                        adapter.refresh(questionList);
                    }
                    break;
                case WHAT_QUESTION_TYPE:
                    ArrayList<String> list = new ArrayList<String>();
                    int type =  msg.arg1;
                    int questionPosition =  msg.arg2;
                    LogUtils.i("type="+type+",itemId="+questionPosition);
                    switch (type){
                        case 0:
                            list.add(type+"");
                            list.add(questionList.get(questionPosition).getId()+"");
                            myOkHttp.okhttpGet(handler,WHAT_QUESTION_TYPE_COMPLETE, Constant.Server.GET_PATH,list,40);
                            questionList.get(questionPosition).setType("0");
                            break;
                        case 1:
                            list.add(type+"");
                            list.add(questionList.get(questionPosition).getId()+"");
                            myOkHttp.okhttpGet(handler,WHAT_QUESTION_TYPE_COMPLETE,Constant.Server.GET_PATH,list,40);
                            questionList.get(questionPosition).setType("1");
                            break;
                        case 2:
                            list.add(questionList.get(questionPosition).getId()+"");
                            myOkHttp.okhttpGet(handler,WHAT_DELETE_COMPLETE,Constant.Server.GET_PATH,list,41);
                            questionList.remove(questionPosition);
                            adapter.refresh(questionList);
                            break;
                    }
                    break;
                case WHAT_QUESTION_TYPE_COMPLETE:
                    LogUtils.i("修改成功");
                    break;
                case WHAT_DELETE_COMPLETE:
                    ToastUtils.Toast(mContext, (String) msg.obj);
                    break;
                case ShareSdkUtils.SHARESDK_CALLBACK_SUCCESS:
                    LogUtils.i("问题分享成功");
                    break;
                case 404:
                    ToastUtils.Toast(mContext,"连接失败");
                    break;
            }
        }
    };

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question,null);
        tv_titlebar_title = (TextView) view.findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText("问答");
        iv_titlebar_camera = (ImageView) view.findViewById(R.id.iv_titlebar_camera);
        iv_titlebar_camera.setBackgroundResource(R.drawable.ico_creat_question);
        seg_question_switch = (SegmentControl) view.findViewById(R.id.seg_question_switch);
        seg_question_switch.setVisibility(View.GONE);
        iv_titlebar_camera.setVisibility(View.GONE);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.lv_question);
        fab_question = (ImageView) view.findViewById(R.id.fab_question);
        mPullRefreshLayout = (PullRefreshLayout) view.findViewById(R.id.prl_question);

        return view;
    }

    @Override
    public void initListener() {
        //侦听刷新
        mPullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LogUtils.i("刷新");
                getUnsolvedQuestion();
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    AnimationUtils.startXmlAnimation(getActivity().getApplication(), fab_question,R.animator.translation_y_up);
                }
                else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    AnimationUtils.startXmlAnimation(getActivity().getApplication(), fab_question,R.animator.translation_y_down);
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        fab_question.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.i("QuestionFragment_onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.i("QuestionFragment_onDestroyView");
    }

    @Override
    public void initData() {
        mContext = getContext();
        userDataManager = UserDataManager.getInstance(getActivity().getApplicationContext());
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
                upLoadUnsolvedQuestion(position+1);
            }
        });
        mRecyclerView.setAdapter(adapter);

        //获取本地缓存
        String questionCache = MySharePreference.getSP(mContext,"questionCache","");
        LogUtils.i("questionCache="+questionCache);
        if (!questionCache.isEmpty()){
            questionList = pullJson(questionCache);
            adapter.refresh(questionList);
        }

        //等待查询数据
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                while (!MainActivity.questionInstantStart);
                getUnsolvedQuestion();
            }
        };
        thread.start();


    }

    @Override
    public void processClick(View v) {
        switch (v.getId()){
            case R.id.fab_question:
                Intent intent = new Intent(mContext, CreateQuestionActivity.class);
                startActivity(intent);
                break;
        }
    }

    //解析Json数据
    private List<Question> pullJson(String jsonData) {
        if (jsonData!=null&&!jsonData.isEmpty()){
            try {
                Gson gson = new Gson();
                return gson.fromJson(jsonData, new TypeToken<List<Question>>() {}.getType());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    //获取未解决的问题
    public void getUnsolvedQuestion(){
        ArrayList<String> list = new ArrayList<String>();
        list = null;
        myOkHttp.okhttpGet(handler,REFRESH_COMPLETE, Constant.Server.GET_PATH,list,34);
    }

    //加载未解决的问题
    public void upLoadUnsolvedQuestion(int listviewSize){
        ArrayList<String> list = new ArrayList<String>();
        list.add(listviewSize+"");
        myOkHttp.okhttpGet(handler,UPLOAD_COMPLETE, Constant.Server.GET_PATH,list,35);
    }

}
