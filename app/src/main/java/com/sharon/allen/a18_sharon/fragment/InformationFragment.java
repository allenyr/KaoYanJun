package com.sharon.allen.a18_sharon.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.activity.FileSelectorActivity;
import com.sharon.allen.a18_sharon.activity.MainActivity;
import com.sharon.allen.a18_sharon.adapter.FriendCircleAdapter;
import com.sharon.allen.a18_sharon.adapter.InformationAdapter;
import com.sharon.allen.a18_sharon.base.BaseFragment;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.model.HotItem;
import com.sharon.allen.a18_sharon.model.Infom;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.MySharePreference;
import com.sharon.allen.a18_sharon.utils.ShareSdkUtils;
import com.sharon.allen.a18_sharon.utils.ToastUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/16.
 */
public class InformationFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private ImageView iv_titlebar_camera;
    private TextView tv_titlebar_title;
    private InformationAdapter adapter;
    public static final int REFRESH_FILE_LIST = 14;
    public static final int GET_FILE_LIST_SUCCESS = 15;
    public static final int BACK_FILE_LIST = 16;
    public static final int WHAT_FILE_INFO = 17;
    public static final int DOWNLOAD_PAY = 19;


    private int mPayMoney = 2;
    private List<Infom> infomList = new ArrayList<>();
    private List<Infom> infomListTemp = new ArrayList<>();
    private AlertDialog downloadDialog;
    private AlertDialog loadingDialog;
    private NumberProgressBar pb_dialog_progress;
    private String downloadPath = Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_DOWNLOAD_PATH;
    private String mFilePath = "";
    private UserDataManager userDataManager;
    private MyOkHttp myOkHttp;
    private ArrayList<String> informNameList = new ArrayList<String>();
    private RelativeLayout rl_titlebar_back;
    private ArrayList<String> okhttpParamList = new ArrayList<String>();
    private PullRefreshLayout mPullToRefreshView;
    private PullRefreshLayout mPullRefreshLayout;

    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case BACK_FILE_LIST:
                    loadingDialog.dismiss();
                    infomListTemp = (List<Infom>) pullJson((String) msg.obj);
                    refreshMenu(infomListTemp);
                    break;

                case REFRESH_FILE_LIST:
                    mPullRefreshLayout.setRefreshing(false);
                    infomListTemp = (List<Infom>) pullJson((String) msg.obj);
                    refreshMenu(infomListTemp);
                    break;

                case GET_FILE_LIST_SUCCESS:
                    String jsonData = (String) msg.obj;
                    MySharePreference.putSP(mContext,"informCache",jsonData);
                    infomListTemp = (List<Infom>) pullJson(jsonData);
                    if ((informNameList.size()-1) != 0){  //首次加载是在子线程里，所以不操作UI
                        loadingDialog.dismiss();
                    }
                    refreshMenu(infomListTemp);
                    break;
                //Adapter返回的message
                case WHAT_FILE_INFO:
                    int informPosition = msg.arg2;
                    String fileName = infomListTemp.get(informPosition).getName();
                    informNameList.add("/" + fileName);
                    getFile(msg.arg1,(Boolean) msg.obj);
                    break;
                case DOWNLOAD_PAY:
                    downloadDialog();
                    myOkHttp.downLoadFile(Constant.Server.PATH+mFilePath,downloadPath,getNameFromPath(mFilePath));
                    break;
                case 404:
                    if ((informNameList.size()-1) != 0){
                        loadingDialog.dismiss();
                    }
                    mPullRefreshLayout.setRefreshing(false);
                    ToastUtils.Toast(mContext,"连接失败");
                    break;
            }
        }
    };
    private Context mContext;


    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //填充布局，返回view对象
        View view = inflater.inflate(R.layout.fragment_information,null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.lv_database_Menus);
        iv_titlebar_camera = (ImageView) view.findViewById(R.id.iv_titlebar_camera);
        tv_titlebar_title = (TextView) view.findViewById(R.id.tv_titlebar_title);
        rl_titlebar_back = (RelativeLayout) view.findViewById(R.id.rl_titlebar_back);

        iv_titlebar_camera.setBackgroundResource(R.drawable.ico_upload);
        iv_titlebar_camera.setVisibility(View.INVISIBLE);
        tv_titlebar_title.setText("资源");
        mPullRefreshLayout = (PullRefreshLayout) view.findViewById(R.id.prl_database);
        mPullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               LogUtils.i("刷新");
                getFile(2,false);
            }
        });

        return view;
    }

    @Override
    public void initListener() {
        rl_titlebar_back.setOnClickListener(this);
        iv_titlebar_camera.setOnClickListener(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.i("InformationFragment_onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.i("InformationFragment_onDestroyView");
    }

    @Override
    public void initData()  {
        mContext = getContext();
        userDataManager = UserDataManager.getInstance(getActivity().getApplicationContext());
        myOkHttp = new MyOkHttp();
        myOkHttp.setOnOkhttpListener(new MyOkHttp.OnOkhttpListener() {
            @Override
            public void onProgress(int progress) {
                pb_dialog_progress.setProgress(progress);
            }

            @Override
            public void onSuccess(String msg) {
                downloadDialog.dismiss();
                ToastUtils.Toast(mContext,"下载完成,存放在"+msg);
            }

            @Override
            public void onError(String msg) {
                pb_dialog_progress.setProgress(0);
                downloadDialog.dismiss();
                ToastUtils.Toast(mContext,"下载出错");
            }
        });
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
        adapter = new InformationAdapter(mContext,infomList,handler,userDataManager);
        adapter.setOnRecyclerViewListener(new InformationAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                Message message = handler.obtainMessage();
                message.arg1 = Integer.parseInt(infomList.get(position).getNature());
                message.arg2 = position;
                message.obj = false;
                message.what = InformationFragment.WHAT_FILE_INFO;
                handler.sendMessage(message);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }

            @Override
            public void onLoadMoreData(int position) {

            }
        });
        mRecyclerView.setAdapter(adapter);

        //获取本地缓存
        String informCache = MySharePreference.getSP(mContext,"informCache","");
        LogUtils.i("informCache="+informCache);
        if (!informCache.isEmpty()){
            infomList = pullJson(informCache);
            adapter.refresh(infomList);
        }

        informNameList.add("/info");
        //等待查询数据
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                while (!MainActivity.InformationInstantStart);
                //查询服务器文件
                getFile(0,false);
            }
        };
        thread.start();

    }

    @Override
    public void processClick(View v) {
        switch (v.getId()){
            case R.id.rl_titlebar_back:
                backFile();
                break;
            case R.id.iv_titlebar_camera:
                Intent intent = new Intent(mContext, FileSelectorActivity.class);
                startActivity(intent);
                break;
        }
    }

    //刷新菜单
    public void refreshMenu(List<Infom> list){
        infomList.clear();
        for (Infom infom :list){
            LogUtils.i(infom.getName());
            infomList.add(new Infom(infom.getName(),infom.getNature()));
        }
        adapter.refresh(infomList);
    }

    private void downloadDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_download, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        pb_dialog_progress = (NumberProgressBar)view.findViewById(R.id.pb_dialog_progress);
        downloadDialog = builder.show();
    }

    //解析Json数据
    private List pullJson(String jsonData) {
        if (jsonData!=null&&!jsonData.isEmpty()){
            try {
                Gson gson = new Gson();
                return gson.fromJson(jsonData, new TypeToken<List<Infom>>() {}.getType());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    //获取文件名
    static String getNameFromPath(String path) {
        //返回“/”最后一次出现的索引
        int index = path.lastIndexOf("/");
        //返回一个新的字符串，包含字符串中从index+1后的所有字符
        String name = path.substring(index+1);
        LogUtils.i("name="+name);
        return name;
    }

    //返回上一级目录
    private void backFile(){
        loadingDialog();
        if ((informNameList.size()-1) > 0){
            informNameList.remove(informNameList.size()-1);
        }
        if ((informNameList.size()-1)==0){
            rl_titlebar_back.setVisibility(View.INVISIBLE);
        }

        mFilePath = listToString(informNameList);
        LogUtils.i(Constant.Server.PATH+mFilePath);

        okhttpParamList.clear();
        okhttpParamList.add(mFilePath);
        myOkHttp.okhttpGet(handler,BACK_FILE_LIST,Constant.Server.GET_PATH,okhttpParamList,30);
    }

    //获取相应目录的文件
    private void getFile(int nature,boolean isShare){
        //判断是否显示返回键
        if ((informNameList.size()-1) > 0){
            rl_titlebar_back.setVisibility(View.VISIBLE);
        }
        mFilePath = listToString(informNameList);
        LogUtils.i(Constant.Server.PATH+mFilePath);

        //返回列表或者下载
        if (nature == 0){       //如果为文件夹，则加载下一层文件
            if ((informNameList.size()-1) > 0){  //首次加载是在子线程里，所以不操作UI
                loadingDialog();
            }
            okhttpParamList.clear();
            okhttpParamList.add(mFilePath);
            myOkHttp.okhttpGet(handler,GET_FILE_LIST_SUCCESS, Constant.Server.GET_PATH,okhttpParamList,30);
        }else if(nature == 1){  //如果为文件则分享或者下载
            if(isShare){//分享
                ShareSdkUtils.showShare(mContext, "考研君", Constant.Server.PATH+mFilePath,informNameList.get(informNameList.size()-1).substring(1),userDataManager, handler,0);
            }else { //下载
                //第二次消费不扣积分,还没实现
                File file = new File(Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_DOWNLOAD_PATH,getNameFromPath(mFilePath));
                if(!file.exists()) {
                    confirmDialog();
                }else {
                    ToastUtils.Toast(mContext,"文件已保存在"+Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_DOWNLOAD_PATH+getNameFromPath(mFilePath));
                }

            }
            removeFileName();
        }else if(nature == 2){//刷新
            okhttpParamList.clear();
            okhttpParamList.add(mFilePath);
            myOkHttp.okhttpGet(handler,REFRESH_FILE_LIST, Constant.Server.GET_PATH, okhttpParamList,30);
        }else {

        }

        if(mFilePath.contains("公共上传区")){
            iv_titlebar_camera.setVisibility(View.VISIBLE);
        }
    }

    //确认下载弹窗
    protected void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("扣除2积分，是否下载文件？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(userDataManager.getMoney() >= mPayMoney){
                    userDataManager.setMoney(userDataManager.getMoney()-mPayMoney);

                    okhttpParamList.clear();
                    okhttpParamList.add(userDataManager.getId()+"");
                    okhttpParamList.add(mPayMoney+"");
                    //先扣费后下载
                    myOkHttp.okhttpGet(handler,DOWNLOAD_PAY,Constant.Server.GET_PATH,okhttpParamList,25);
                }else {
                    ToastUtils.Toast(mContext,"积分不足");
                }
                }
            });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                }
            });
        builder.setCancelable(false);
        builder.create().show();
    }

    //加载弹窗
    private void loadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        TextView tv_dialog_title = (TextView)view.findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText("加载中...");
        loadingDialog = builder.show();

    }

    //出去文件名
    private void removeFileName(){
        informNameList.remove(informNameList.size()-1);
        LogUtils.i("removeFileName="+listToString(informNameList));
    }

    private String listToString(List<String> list){
        //返回list列表
        StringBuffer stringBuffer =  new StringBuffer();
        for (int i=0;i<list.size();i++) {
            stringBuffer.append(list.get(i).toString());
        }
        return stringBuffer.toString();
    }

}
