package com.sharon.allen.a18_sharon.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.adapter.CommentAdapter;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.utils.FileUtils;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;
import com.sharon.allen.a18_sharon.view.CircleImageView.CircleImageView;
import com.sharon.allen.a18_sharon.model.Comment;
import com.sharon.allen.a18_sharon.utils.LogUtils;

import com.sharon.allen.a18_sharon.utils.MySharePreference;
import com.sharon.allen.a18_sharon.utils.TimeUtils;
import com.sharon.allen.a18_sharon.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/26.
 */
public class CommentActivity extends BaseActivity {

    private CircleImageView civ_comment_activity_head;
    private TextView tv_comment_activity_username;
    private TextView tv_comment_activity_time;
    private TextView tv_comment_activity_mood;
    private ImageView iv_comment_activity_img;
    private ListView lv_comment_activity_list;
    private LinearLayout ll_thank;
    private TextView tv_titlebar_title;
    private RelativeLayout rl_titlebar_back;
    private Button bt_comment_public;
    private EditText et_comment;
    private ImageView iv_comment_activity_thank;
    private ImageView iv_titlebar_camera;
    private ImageView iv_comment_activity_sex;

    private String mReplyName;
    private int mReplyUserId;
    private String mType;
    private String mHeadUrl;
    private String mUserName;
    private String mTime;
    private String mMood;
    private String mImgUrl;
    private int mThank;
//    private int mCommentCount;
    private int mDynamicId;
    private int mReceiveridId;
    private int mReceiveridIdTemp;
    private String mETComment;
    private String mSex;
    private Context mContext;

    private List<Comment> mCommentList = new ArrayList<>();
    private CommentAdapter mCommentAdapter;
    private UserDataManager userDataManager;

    private static final int WHAT_UPLOAD_COMMENT = 1;
    private static final int WHAT_UPDATE_THANK = 2;
    private static final int WHAT_PUBLIC_COMMENT = 3;
    public static final int WHAT_COMMENT_FOR_SOMEONE = 7;

    private String savePath = Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_LIKE_PATH+"PNG_"+TimeUtils.getCurrentTime()+".png";
    private String phoneName = TimeUtils.getPhotoFileName();
    private String likeDir = Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_LIKE_PATH;


    Handler handler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //加载评论完成
                case WHAT_UPLOAD_COMMENT:
                    mCommentList = pullJson((String) msg.obj);
                    for (Comment comment:mCommentList){
                        LogUtils.i(comment.getComment());
                    }
                    mCommentAdapter.refresh(mCommentList);
                    break;
                //点赞
                case WHAT_UPDATE_THANK:
                    userDataManager.setMoney(userDataManager.getMoney()+1);
                    ToastUtils.Toast(getApplicationContext(),"积分+1");
                    break;
                //评论成功
                case WHAT_PUBLIC_COMMENT:
                    mCommentList =  pullJson((String) msg.obj);
                    mCommentAdapter.refresh(mCommentList);
                    userDataManager.setMoney(userDataManager.getMoney()+1);
                    ToastUtils.Toast(getApplicationContext(),"评论成功！积分+1");
                    break;
                case MyOkHttp.DOWNLOAD_SUCCESS:
                    ToastUtils.Toast(getApplicationContext(),"保存成功！");
                    break;
                case WHAT_COMMENT_FOR_SOMEONE:
                    mReplyName = "@"+msg.obj+":";
                    mReplyUserId = msg.arg1;
                    et_comment.setText(mReplyName);
                    et_comment.setSelection(et_comment.getText().toString().length());
                    break;
            }
        }
    };
    private MyOkHttp myOkHttp;
    private String fileSuffix;
    private String gifName;
    private File downloadFileTemp;

    @Override
    public void initView() {
        setContentView(R.layout.activity_comment);
        civ_comment_activity_head = (CircleImageView) findViewById(R.id.civ_comment_activity_head);
        tv_comment_activity_username = (TextView) findViewById(R.id.tv_comment_activity_username);
        iv_comment_activity_sex = (ImageView) findViewById(R.id.iv_comment_activity_sex);
        tv_comment_activity_time = (TextView) findViewById(R.id.tv_comment_activity_time);
        tv_comment_activity_mood = (TextView) findViewById(R.id.tv_comment_activity_mood);
        iv_comment_activity_img = (ImageView) findViewById(R.id.iv_comment_activity_img);
        lv_comment_activity_list = (ListView) findViewById(R.id.lv_comment_activity_list);
        ll_thank = (LinearLayout) findViewById(R.id.ll_thank);
        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        rl_titlebar_back = (RelativeLayout) findViewById(R.id.rl_titlebar_back);
        iv_titlebar_camera = (ImageView) findViewById(R.id.iv_titlebar_camera);
        bt_comment_public = (Button) findViewById(R.id.bt_comment_public);
        et_comment = (EditText) findViewById(R.id.et_comment);
        iv_comment_activity_thank = (ImageView) findViewById(R.id.iv_comment_activity_thank);

        tv_titlebar_title.setText("评论");
        rl_titlebar_back.setVisibility(View.VISIBLE);
        iv_titlebar_camera.setVisibility(View.INVISIBLE);
        
        SystemBarTintUtils.setActionBar(this);
    }

    @Override
    public void initListener() {
        rl_titlebar_back.setOnClickListener(this);
        ll_thank.setOnClickListener(this);
        bt_comment_public.setOnClickListener(this);
        civ_comment_activity_head.setOnClickListener(this);
        iv_comment_activity_img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                saveDialog();
                LogUtils.i("保存-------------");
                return true;

            }
        });
    }

    @Override
    public void initData() {
        mContext = getApplicationContext();
        userDataManager = UserDataManager.getInstance(mContext);
        myOkHttp = new MyOkHttp();
        myOkHttp.setOnOkhttpListener(new MyOkHttp.OnOkhttpListener() {
            @Override
            public void onProgress(int progress) {
                LogUtils.i("下载progress="+progress);
            }

            @Override
            public void onSuccess(String msg) {
                LogUtils.i("下载完成");
                Glide.with(mContext).load(downloadFileTemp).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv_comment_activity_img);
//                Glide.with(mContext).load(Constant.Server.PATH+mImgUrl).into(iv_comment_activity_img);
            }

            @Override
            public void onError(String msg) {

            }
        });
        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收imageurl值
        mHeadUrl = bundle.getString("headurl");
        mUserName = bundle.getString("username");
        mTime = bundle.getString("time");
        mMood = bundle.getString("mood");
        mImgUrl = bundle.getString("imgurl");
        mReceiveridIdTemp = bundle.getInt("receiverid");
        mDynamicId = bundle.getInt("id");
        mSex = bundle.getString("sex");

        LogUtils.i("id="+mDynamicId);
        if(mSex.equals("男")){
            iv_comment_activity_sex.setBackgroundResource(R.drawable.ico_sex_male);
        }else if (mSex.equals("女")){
            iv_comment_activity_sex.setBackgroundResource(R.drawable.ico_sex_female);
        }else {
            iv_comment_activity_sex.setVisibility(View.GONE);
        }

        Glide.with(this).load(Constant.Server.PATH + mHeadUrl).into(civ_comment_activity_head);
        tv_comment_activity_username.setText(mUserName);
        tv_comment_activity_time.setText(mTime);
        tv_comment_activity_mood.setText(mMood);

        // 显示图片
//        fileSuffix = FileUtils.getFileSuffix(mImgUrl);
//        gifName = FileUtils.getNameFromPath(mImgUrl);
//        downloadFileTemp = new File(Environment.getExternalStorageDirectory()+ Constant.SdCard.SAVE_IMAGE_PATH, gifName);
//        if(!downloadFileTemp.exists()) {
//            LogUtils.i("图片不存在");
//            myOkHttp.downLoadFile(Constant.Server.PATH + mImgUrl,Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_IMAGE_PATH, gifName);
//            Glide.with(mContext).load(Constant.Server.PATH + mImgUrl).into(iv_comment_activity_img);
//        }else {
//            LogUtils.i("图片存在");
//            if (fileSuffix.equals("gif")){
//                Glide.with(mContext).load(downloadFileTemp).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv_comment_activity_img);
//            }else {
//                Glide.with(mContext).load(downloadFileTemp).into(iv_comment_activity_img);
//            }
//        }
        LogUtils.i("mImgUrl="+mImgUrl);
        if (FileUtils.getFileSuffix(mImgUrl).equals("gif")){
            Glide.with(mContext)
                    .load(Constant.Server.PATH + mImgUrl)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(iv_comment_activity_img);
        }else {
            Glide.with(mContext)
                    .load(Constant.Server.PATH + mImgUrl)
                    .into(iv_comment_activity_img);
        }
//        Glide.with(mContext).load(Constant.Server.PATH + mImgUrl).into(iv_comment_activity_img);

        //显示时间
        try {
            tv_comment_activity_time.setText(TimeUtils.getTimeGap(mTime,TimeUtils.getCurrentTime()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //点赞
        if(MySharePreference.getSP(mContext, mDynamicId +" "+ userDataManager.getId(),false)){
            iv_comment_activity_thank.setBackgroundResource(R.drawable.ico_like_n);
        }

        mCommentAdapter = new CommentAdapter(mContext,mCommentList,handler);
        lv_comment_activity_list.setAdapter(mCommentAdapter);
        getCommentList(mDynamicId +"");
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.rl_titlebar_back:
                onBackPressed();
                break;
            case R.id.civ_comment_activity_head:
                startPersonalData(mReceiveridIdTemp);
                break;
            case R.id.ll_thank:
                if(!MySharePreference.getSP(mContext, mDynamicId +" "+ userDataManager.getId(),false)){
                    MySharePreference.putSP(mContext, mDynamicId +" "+ userDataManager.getId(),true);
                    iv_comment_activity_thank.setBackgroundResource(R.drawable.ico_like_n);
                    publicThank("赞了你！");
                }
                break;
            case R.id.bt_comment_public:
                mETComment = et_comment.getText().toString();
                if(!mETComment.equals("")&&mETComment != null){
                    publicCmment(mETComment);
                    et_comment.setText("");
                }
                break;
        }
    }


    //解析Json数据
    private List<Comment> pullJson(String jsonData) {
        try{
            Gson gson = new Gson();
            return gson.fromJson(jsonData, new TypeToken<List<Comment>>() {}.getType());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //获取评论列表
    public void getCommentList(String id){

        ArrayList<String> list = new ArrayList<String>();
        list.add(id);
        myOkHttp.okhttpGet(handler,WHAT_UPLOAD_COMMENT,Constant.Server.GET_PATH,list,21);
//        MyHttp.creatHttpRequest(handler,WHAT_UPLOAD_COMMENT,id,21);
    }

    //评论
    public void publicCmment(String comment){
        mTime = TimeUtils.getCurrentTime();
        //消息类型：
        // 1：回复发帖人，2：回复评论人，3：系统
        mType = "1";
        if(et_comment.getText().toString().contains("@")){
            mReceiveridId = mReplyUserId;
        }else {
            mReceiveridId = mReceiveridIdTemp;
        }
        ArrayList<String> list = new ArrayList<String>();
        list.add(mDynamicId +"");
        list.add(userDataManager.getId()+"");
        list.add(mReceiveridId +"");
        list.add(userDataManager.getUsername());
        list.add(userDataManager.getHeadUrl());
        list.add(userDataManager.getSex());
        list.add(mType);
        list.add(comment);
        list.add(mTime);
        list.add(mImgUrl);
        myOkHttp.okhttpGet(handler,WHAT_PUBLIC_COMMENT,Constant.Server.GET_PATH,list,20);
    }

    //点赞
    public void publicThank(String comment){
        mTime = TimeUtils.getCurrentTime();
        mType = "1";
        mReceiveridId = mReceiveridIdTemp;
        ArrayList<String> list = new ArrayList<String>();
        list.add(mDynamicId +"");
        list.add(userDataManager.getId()+"");
        list.add(mReceiveridId +"");
        list.add(userDataManager.getUsername());
        list.add(userDataManager.getHeadUrl());
        list.add(userDataManager.getSex());
        list.add(mType);
        list.add(comment);
        list.add(mTime);
        list.add(mImgUrl);
        myOkHttp.okhttpGet(handler,WHAT_UPDATE_THANK,Constant.Server.GET_PATH,list,6);

    }

    //保存
    private void saveDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("保存"); //设置标题
        builder.setMessage("是否保存"); //设置内容
        builder.setIcon(R.drawable.ico_xing);//设置图标，图片id即可
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                myOkHttp.downLoadFile(Constant.Server.PATH+mImgUrl,likeDir,phoneName);
//                MyXutils.downLoadFile(Constant.Server.PATH+mImgUrl,savePath,handler);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    //跳转到CommentActivity
    private void startPersonalData(int userid){
        Intent intent=new Intent();
        intent.setClass(mContext, PersonalActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("userid",userid);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

}
