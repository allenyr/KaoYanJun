package com.sharon.allen.a18_sharon.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.activity.PersonalActivity;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.fragment.FriendCircleFragment;
import com.sharon.allen.a18_sharon.utils.FileUtils;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.view.CircleImageView.CircleImageView;
import com.sharon.allen.a18_sharon.model.HotItem;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.TimeUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Allen on 2016/5/27.
 */
public class FriendCircleAdapter extends RecyclerView.Adapter{

    List<HotItem> hotlist;
    private Context context;
    private Handler handler;
    private LayoutInflater inflater;
    private MyOkHttp myOkHttp;
    private String photoName;
    private File downloadImgTemp;
    private File downloadHeadTemp;

    public FriendCircleAdapter(Context context, List<HotItem> hotlist,Handler handler){
        this.context = context;
        this.hotlist = hotlist;
        this.inflater = LayoutInflater.from(context);
        this.handler = handler;
        myOkHttp = new MyOkHttp();
        myOkHttp.setOnOkhttpListener(new MyOkHttp.OnOkhttpListener() {
            @Override
            public void onProgress(int progress) {
                LogUtils.i("gif下载进度="+progress);
            }

            @Override
            public void onSuccess(String msg) {
                notifyDataSetChanged();
            }

            @Override
            public void onError(String msg) {

            }
        });
    }

    //-----------------------回调------------------------
    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
        void onLoadMoreData(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }
    //-------------------------------------------------

    //外部调用，listview改变通知刷新adapter
    public void refresh(List<HotItem> list) {
        hotlist = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_moments, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new FrienCircleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        FrienCircleViewHolder viewHolder = (FrienCircleViewHolder) holder;
        viewHolder.position = position;
        final HotItem hotItem = hotlist.get(position);
        viewHolder.tv_moments_mood.setText(hotItem.getMood());

        //配图
        //缓存
//        downloadImgTemp = new File(Environment.getExternalStorageDirectory()+ Constant.SdCard.SAVE_IMAGE_PATH, FileUtils.getNameFromPath(hotItem.getImageurl()));
//        if(!downloadImgTemp.exists()) {
//            LogUtils.i("图片不存在");
//            myOkHttp.downLoadFile(Constant.Server.PATH + hotItem.getImageurl(), Environment.getExternalStorageDirectory() + Constant.SdCard.SAVE_IMAGE_PATH, FileUtils.getNameFromPath(hotItem.getImageurl()));
//            Glide.with(context).load(Constant.Server.PATH + hotItem.getImageurl()).into(viewHolder.siv_moments_img);
//        }else {
//            LogUtils.i("图片存在");
//            if (FileUtils.getFileSuffix(Constant.Server.PATH+hotItem.getImageurl()).equals("gif")){
//                Glide.with(context).load(downloadImgTemp).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(viewHolder.siv_moments_img);
//            }else {
//                Glide.with(context).load(downloadImgTemp).into(viewHolder.siv_moments_img);
//            }
//        }

        if (FileUtils.getFileSuffix(hotItem.getHeadurl()).equals("gif")){
            Glide.with(context)
                    .load(Constant.Server.PATH + hotItem.getImageurl())
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(viewHolder.siv_moments_img);
        }else {
            Glide.with(context)
                    .load(Constant.Server.PATH + hotItem.getImageurl())
                    .into(viewHolder.siv_moments_img);
        }


        //头像
        //缓存
//        downloadImgTemp = new File(Environment.getExternalStorageDirectory()+ Constant.SdCard.SAVE_AVATAR_PATH, FileUtils.getNameFromPath(hotItem.getHeadurl()));
//        if(!downloadImgTemp.exists()) {
//            LogUtils.i("图片不存在");
//            myOkHttp.downLoadFile(Constant.Server.PATH + hotItem.getHeadurl(), Environment.getExternalStorageDirectory() + Constant.SdCard.SAVE_AVATAR_PATH, FileUtils.getNameFromPath(hotItem.getHeadurl()));
//            Glide.with(context).load(Constant.Server.PATH + hotItem.getHeadurl()).into(viewHolder.civ_moments_head);
//        }else {
//            LogUtils.i("图片存在");
//            if (FileUtils.getFileSuffix(Constant.Server.PATH+hotItem.getHeadurl()).equals("gif")){
//                Glide.with(context).load(downloadImgTemp).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(viewHolder.civ_moments_head);
//            }else {
//                Glide.with(context).load(downloadImgTemp).into(viewHolder.civ_moments_head);
//            }
//        }
        Glide.with(context).load(Constant.Server.PATH + hotItem.getHeadurl()).into(viewHolder.civ_moments_head);

        viewHolder.tv_moments_thank.setText(hotItem.getThank()+"");
        viewHolder.tv_moments_username.setText(hotItem.getUsername());
        viewHolder.tv_moments_comment.setText(hotItem.getCommentcount()+"");

        if(hotItem.getSex().equals("男")){
            viewHolder.iv_moments_sex.setBackgroundResource(R.drawable.ico_sex_male);
        }else if (hotItem.getSex().equals("女")){
            viewHolder.iv_moments_sex.setBackgroundResource(R.drawable.ico_sex_female);
        }else {
            viewHolder.iv_moments_sex.setVisibility(View.GONE);
        }

        //显示时间
        String time = null;
        try {
            time = TimeUtils.getTimeGap(hotItem.getTime(),TimeUtils.getCurrentTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        viewHolder.tv_moments_time.setText(time);

        final FrienCircleViewHolder finalViewHolder = viewHolder;
        viewHolder.civ_moments_head.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                startPersonalData(hotItem.getUserId(), finalViewHolder.civ_moments_head);
            }
        });

        if((hotItem.getUserId() == UserDataManager.getInstance(context).getId())||(61 == UserDataManager.getInstance(context).getId())){
            viewHolder.iv_moments_delete.setVisibility(View.VISIBLE);
        }else {
            viewHolder.iv_moments_delete.setVisibility(View.GONE);
        }

        viewHolder.iv_moments_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog(hotItem.getId(),position);
            }
        });
        //底部判断
        if(position == hotlist.size()-1){
            LogUtils.i("position"+position);
            onRecyclerViewListener.onLoadMoreData(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        int size = 0;
        try {
            size = hotlist.size();
        }catch (Exception e){
            e.printStackTrace();
        }
        return size;
    }

    //把条目需要使用到的所有组件封装在这个类中
    class FrienCircleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public CircleImageView civ_moments_head;
        public TextView tv_moments_username;
        public TextView tv_moments_mood;
        public TextView tv_moments_time;
        public ImageView siv_moments_img;
        public ImageView iv_moments_sex;
        public TextView tv_moments_thank;
        public TextView tv_moments_comment;
        public ImageView iv_moments_delete;
        public View rootView;
        public int position;

        public FrienCircleViewHolder(View itemView) {
            super(itemView);
            tv_moments_mood = (TextView) itemView.findViewById(R.id.tv_moments_mood);
            siv_moments_img = (ImageView) itemView.findViewById(R.id.siv_moments_img);
            iv_moments_sex = (ImageView) itemView.findViewById(R.id.iv_moments_sex);
            tv_moments_thank = (TextView) itemView.findViewById(R.id.tv_moments_thank);
            civ_moments_head = (CircleImageView) itemView.findViewById(R.id.civ_moments_head);
            tv_moments_username = (TextView) itemView.findViewById(R.id.tv_moments_username);
            tv_moments_time = (TextView) itemView.findViewById(R.id.tv_moments_time);
            tv_moments_comment = (TextView) itemView.findViewById(R.id.tv_moments_comment);
            iv_moments_delete = (ImageView) itemView.findViewById(R.id.iv_moments_delete);
            rootView = itemView.findViewById(R.id.rootview_moments);
            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(position);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(null != onRecyclerViewListener){
                return onRecyclerViewListener.onItemLongClick(position);
            }
            return false;
        }
    }


    //跳转到CommentActivity
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startPersonalData(int userid, View view){
        Intent intent=new Intent();
        intent.setClass(context, PersonalActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("userid",userid);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) context,view,"share_head").toBundle());
        }else {
            context.startActivity(intent);
        }
    }

    //删除弹窗
    private void confirmDialog(final int item, final int position){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("是否删除？"); //设置内容
        builder.setIcon(R.drawable.focus_delete_icon);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogUtils.i(context,"申请删除请求");
                Message message = handler.obtainMessage();
                message.arg1 = item;
                message.arg2 = position;
                message.what = FriendCircleFragment.DELETE_REQUEST;
                handler.sendMessage(message);
                dialog.dismiss(); //关闭dialog
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogUtils.i(context,"取消删除请求");
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }

}
