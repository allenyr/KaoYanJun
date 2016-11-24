package com.sharon.allen.a18_sharon.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.adapter.MainPagerAdapter;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.fragment.InformationFragment;
import com.sharon.allen.a18_sharon.fragment.FriendCircleFragment;
import com.sharon.allen.a18_sharon.fragment.MeFragment;
import com.sharon.allen.a18_sharon.fragment.QuestionFragment;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allen on 2016/5/27.
 */
public class MainActivity extends BaseActivity {

    private ViewPager viewPager;
    private MainPagerAdapter adapter;
    private LinearLayout ll_tab_hot;
    private LinearLayout ll_tab_setting;
    private LinearLayout ll_tab_database;
    private ImageView im_hot;
    private ImageView im_setting;
    private ImageView im_database;
    private FriendCircleFragment friendCircleFragment;
    private QuestionFragment questionFragment;
    private InformationFragment informationFragment;
    private MeFragment meFragment;
    public static Activity Main_Activity = null;
    private LinearLayout ll_tab_question;
    private ImageView im_question;
    public static boolean questionInstantStart = false;
    public static boolean InformationInstantStart = false;
    private long mExitTime;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.vp_main);
        ll_tab_hot = (LinearLayout) findViewById(R.id.ll_tab_hot);
        ll_tab_question = (LinearLayout) findViewById(R.id.ll_tab_question);
        ll_tab_database = (LinearLayout) findViewById(R.id.ll_tab_database);
        ll_tab_setting = (LinearLayout) findViewById(R.id.ll_tab_setting);
        im_hot = (ImageView) findViewById(R.id.im_hot);
        im_question = (ImageView) findViewById(R.id.im_question);
        im_database = (ImageView) findViewById(R.id.im_database);
        im_setting = (ImageView) findViewById(R.id.im_setting);
        SystemBarTintUtils.setActionBar(this);
    }

    @Override
    public void initListener() {
        //viewPager界面切换时会触发
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //切换时不断调用
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            //切换状态完成后调用
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        im_hot.setBackgroundResource(R.drawable.ico_message);
                        im_question.setBackgroundResource(R.drawable.ico_question_n);
                        im_database.setBackgroundResource(R.drawable.ico_find_n);
                        im_setting.setBackgroundResource(R.drawable.ico_me_n);
                        break;
                    case 1:
                        im_hot.setBackgroundResource(R.drawable.ico_message_n);
                        im_question.setBackgroundResource(R.drawable.ico_question);
                        im_database.setBackgroundResource(R.drawable.ico_find_n);
                        im_setting.setBackgroundResource(R.drawable.ico_me_n);
                        if(!questionInstantStart){
                            questionInstantStart = true;
                        }

                        break;
                    case 2:
                        im_hot.setBackgroundResource(R.drawable.ico_message_n);
                        im_question.setBackgroundResource(R.drawable.ico_question_n);
                        im_database.setBackgroundResource(R.drawable.ico_find);
                        im_setting.setBackgroundResource(R.drawable.ico_me_n);
                        if(!InformationInstantStart){
                            InformationInstantStart = true;
                        }
                        break;
                    case 3:
                        im_hot.setBackgroundResource(R.drawable.ico_message_n);
                        im_question.setBackgroundResource(R.drawable.ico_question_n);
                        im_database.setBackgroundResource(R.drawable.ico_find_n);
                        im_setting.setBackgroundResource(R.drawable.ico_me);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //给三个选项卡设置点击事件
        ll_tab_hot.setOnClickListener(this);
        ll_tab_question.setOnClickListener(this);
        ll_tab_database.setOnClickListener(this);
        ll_tab_setting.setOnClickListener(this);
    }

    @Override
    public void initData() {
        Main_Activity = this;
        List<Fragment> fragments= new ArrayList<Fragment>();
        //创建fragment对象，存入集合
        friendCircleFragment = new FriendCircleFragment();
        questionFragment = new QuestionFragment();
        informationFragment = new InformationFragment();
        meFragment = new MeFragment();
        fragments.add(friendCircleFragment);
        fragments.add(questionFragment);
        fragments.add(informationFragment);
        fragments.add(meFragment);
        //创建adapter对象
        adapter = new MainPagerAdapter(getSupportFragmentManager(),fragments);
        //为viewPager设置adapter
        adapter.notifyDataSetChanged();
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);//设置缓存view 的个数（实际有n个，缓存n-1个+正在显示的1个）

    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.ll_tab_hot:
                viewPager.setCurrentItem(0);
                break;
            case R.id.ll_tab_question:
                viewPager.setCurrentItem(1);
                break;
            case R.id.ll_tab_database:
                viewPager.setCurrentItem(2);
                break;
            case R.id.ll_tab_setting:
                viewPager.setCurrentItem(3);
                break;

        }
    }
    //按两次返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
//                finish();
                backHome();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //返回桌面
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent home = new Intent(Intent.ACTION_MAIN);
//            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            home.addCategory(Intent.CATEGORY_HOME);
//            startActivity(home);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
    private boolean backHome(){
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
        return true;
    }

}
