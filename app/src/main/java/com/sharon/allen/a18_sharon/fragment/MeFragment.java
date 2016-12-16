package com.sharon.allen.a18_sharon.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.activity.MessageActivity;
import com.sharon.allen.a18_sharon.activity.MoneyStoreActivity;
import com.sharon.allen.a18_sharon.activity.MyPhotoActivity;
import com.sharon.allen.a18_sharon.activity.MyQuestionActivity;
import com.sharon.allen.a18_sharon.activity.PersonalSettingActivity;
import com.sharon.allen.a18_sharon.activity.SettingActivity;
import com.sharon.allen.a18_sharon.base.BaseFragment;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.dao.UserSQLite;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.MySharePreference;
import com.sharon.allen.a18_sharon.utils.ShareSdkUtils;
import com.sharon.allen.a18_sharon.utils.TimeUtils;
import com.sharon.allen.a18_sharon.utils.ToastUtils;
import com.sharon.allen.a18_sharon.view.ImageButton;

import java.text.ParseException;

import cn.iwgang.countdownview.CountdownView;
import cn.nekocode.badge.BadgeDrawable;

/**
 * Created by Administrator on 2016/8/7.
 */
public class MeFragment extends BaseFragment {

    private LinearLayout ly_photo;
    private LinearLayout ly_setting;
    private UserSQLite userSQLite;
    private ImageView civ_head;

//    public static final int SETTING_RESULT = 1;
    public static final int SETTING_REQUEST = 1;
    public static final int WHAT_COMPLETE = 3;
    private LinearLayout ll_money;
    private LinearLayout ll_message;
    private CountdownView mCvCountdownView;
    private LinearLayout ll_infom_daojishi;
    private TextView tv_infom_time;

    private UserDataManager userDataManager;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ShareSdkUtils.SHARESDK_CALLBACK_SUCCESS:
                    LogUtils.i("签到分享成功");
                    break;
            }
        }
    };
    private MyOkHttp myOkHttp;
    private TextView tv_me_readed;
    private BadgeDrawable badgeDrawable;
    private SpannableString spannableString;
    private int messageRead;
    private LinearLayout ly_question;
    private Context mContext;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //填充布局，返回view对象

        View view = inflater.inflate(R.layout.fragment_me,null);
        ly_photo = (LinearLayout) view.findViewById(R.id.ly_photo);
        ly_question = (LinearLayout) view.findViewById(R.id.ly_question);
        ly_setting = (LinearLayout) view.findViewById(R.id.ly_setting);
        ll_money = (LinearLayout) view.findViewById(R.id.ll_money);
        ll_message = (LinearLayout) view.findViewById(R.id.ll_message);
        civ_head = (ImageView) view.findViewById(R.id.civ_head);
        mCvCountdownView = (CountdownView)view.findViewById(R.id.cdv_database_countdown);
        ll_infom_daojishi = (LinearLayout) view.findViewById(R.id.ll_infom_daojishi);
        tv_infom_time = (TextView) view.findViewById(R.id.tv_infom_time);
        tv_me_readed = (TextView) view.findViewById(R.id.tv_me_readed);

        //倒计时
        try {
            long countdown = TimeUtils.countdown(TimeUtils.getCurrentTime(),"20161224_083000");
            mCvCountdownView.start(countdown); // Millisecond
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return view;

    }

    @Override
    public void initListener() {
        civ_head.setOnClickListener(this);
        ly_photo.setOnClickListener(this);
        ly_question.setOnClickListener(this);
        ly_setting.setOnClickListener(this);
        ll_money.setOnClickListener(this);
        ll_message.setOnClickListener(this);
        ll_infom_daojishi.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mContext = getContext();
        userDataManager = UserDataManager.getInstance(getActivity().getApplicationContext());
        myOkHttp = new MyOkHttp();
        //获取数据库对象
        userSQLite = UserSQLite.getInstance(mContext);
        Glide.with(mContext).load(Constant.Server.PATH+ userDataManager.getHeadUrl()).into(civ_head);

        setBadge();
    }

    @Override
    public void processClick(View v) {
        switch (v.getId()){
            case R.id.ll_infom_daojishi:
                //分享
                String time = "";
                try {
                    time = TimeUtils.countdownForString(TimeUtils.getCurrentTime(),"20161224_083000");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ShareSdkUtils.showShare(mContext,"考研君",Constant.Server.APP_STORE_URL,"签到成功,"+time,userDataManager,handler,2);

                break;
            case R.id.civ_head:
                startActivityForResult(new Intent(mContext,PersonalSettingActivity.class),SETTING_REQUEST);
                break;
            case R.id.ly_photo:
                startActivity(new Intent(mContext,MyPhotoActivity.class));
                break;
            case R.id.ly_question:
                startActivity(new Intent(mContext,MyQuestionActivity.class));
                break;
            case R.id.ll_message:
                startActivityForResult(new Intent(mContext,MessageActivity.class),2);
                break;
            case R.id.ll_money:
                startActivity(new Intent(mContext,MoneyStoreActivity.class));
                break;
            case R.id.ly_setting:
                startActivity(new Intent(mContext,SettingActivity.class));
                break;

        }
    }

    //接收上一个activity返回数据
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SETTING_REQUEST:
                    Glide.with(mContext).load(Constant.Server.PATH+ userDataManager.getHeadUrl()).into(civ_head);
                break;
            case 2:
                setBadge();
                break;
        }
    }

    //设置徽章
    public void setBadge(){
//        messageRead = userDataManager.getMessagenum() - MySharePreference.getSP(mContext,"message_readed",0);
        messageRead = userDataManager.getMessagenum();
        LogUtils.i(messageRead);
        if(messageRead >0){
            tv_me_readed.setVisibility(View.VISIBLE);
        }else {
            tv_me_readed.setVisibility(View.INVISIBLE);
        }
        badgeDrawable = new BadgeDrawable.Builder().type(BadgeDrawable.TYPE_NUMBER).textSize(50).number(messageRead).build();
        spannableString = new SpannableString(TextUtils.concat("", badgeDrawable.toSpannable()));
        tv_me_readed.setText(spannableString);
    }



}
