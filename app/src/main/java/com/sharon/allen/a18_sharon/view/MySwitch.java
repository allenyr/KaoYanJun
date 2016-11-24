package com.sharon.allen.a18_sharon.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sharon.allen.a18_sharon.R;

/**
 * //measure->layout->draw
 * Created by Allen on 2016/11/7.
 */

public class MySwitch extends View {

    private Paint mPaint;
    private Bitmap mBitmapBg;
    private Bitmap mBitmapSlide;
    private int MAX_LEFT_LENG;
    private int mSlideLeft;
    private boolean isOpen;
    private int startX;
    private int endX;
    private int moveX;
    private Boolean isClick;
    private static final String NAME_SPACE = "http://schemas.android.com/apk/res-auto";

    //----------------------------------------------------------------------------
    //接口
    public interface OnCheckChangeListener{
        public void onCheckChanged(View view, boolean isChanged);
    }
    //初始化接口变量
    private OnCheckChangeListener mListener = null;
    //自定义控件的自定义事件
    public void setOnCheckChangeListener(OnCheckChangeListener listener){
        mListener = listener;
    }
    //----------------------------------------------------------------------------

    public MySwitch(Context context) {
        super(context);
        initView();
    }

    public MySwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        //获取属性值
        isOpen = attrs.getAttributeBooleanValue(NAME_SPACE,"checked",false);
        //加载自定义滑块图片
        int bitmapSlide = attrs.getAttributeResourceValue(NAME_SPACE,"slide",-1);
        if (bitmapSlide !=-1){
            setSlideImage(bitmapSlide);
        }
        if(isOpen){
            mSlideLeft = MAX_LEFT_LENG;
        }else {
            mSlideLeft = 0;
        }
        invalidate();
    }

    public MySwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        mPaint = new Paint();
        //画笔颜色
        mPaint.setColor(Color.RED);
        // 设置alpha不透明度，范围为0~255
        mPaint.setAlpha(255);
        // 是否抗锯齿
        mPaint.setAntiAlias(true);
        //获取资源文件
        mBitmapBg = BitmapFactory.decodeResource(getResources(), R.drawable.ico_switch_bg);
        setSlideImage(R.drawable.ico_switch);

        MAX_LEFT_LENG = mBitmapBg.getWidth() - mBitmapSlide.getWidth();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick){
                    if(isOpen){
                        isOpen = false;
                        mSlideLeft = 0;
                    }else {
                        isOpen = true;
                        mSlideLeft = MAX_LEFT_LENG;
                    }
                    //view重绘方法，刷新view,相当于重新调用onDraw方法
                    invalidate();

                    //当前开关状态为空
                    if (mListener !=null){
                        mListener.onCheckChanged(MySwitch.this,isOpen);
                    }
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //1.记录当前的x坐标
                //相对于当前控件的x坐标
                startX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //2.记录移动后的x坐标
                endX = (int) event.getX();
                //3.记录x偏移量
                int dx  = endX - startX;
                moveX += Math.abs(dx);

                //4.根据偏移量，更新mSlideLeft
                mSlideLeft += dx;
                if (mSlideLeft < 0){
                    mSlideLeft = 0;
                }
                if (mSlideLeft > MAX_LEFT_LENG){
                    mSlideLeft = MAX_LEFT_LENG;
                }
                //5.刷新界面
                invalidate();
                //6.重新初始化起点坐标
                startX = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                //根据位移判断是点击事件还是触摸移动事件
                if (moveX < 5){
                    //单机事件
                    isClick = true;
                }else {
                    //移动事件
                    isClick = false;
                }
                moveX = 0;
                if (!isClick){
                    //根据当前位置，切换开关
                    if (mSlideLeft < MAX_LEFT_LENG/2){
                        mSlideLeft = 0;
                        isOpen = false;
                    }else {
                        mSlideLeft = MAX_LEFT_LENG;
                        isOpen = true;
                    }

                    invalidate();
                    //当前开关状态为空
                    if (mListener !=null){
                        mListener.onCheckChanged(MySwitch.this,isOpen);
                    }
                }

                break;
            default:
                break;
        }
        return super.onTouchEvent(event);

    }

    //设置画布大大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mBitmapBg.getWidth(),mBitmapBg.getHeight());
    }

    //画笔绘画
    @Override
    protected void onDraw(Canvas canvas) {
         /*
         * 方法 说明 drawRect 绘制矩形 drawCircle 绘制圆形 drawOval 绘制椭圆 drawPath 绘制任意多边形
         * drawLine 绘制直线 drawPoin 绘制点
         */

//        if (isOpen){
//            //画圆角矩形
//            mPaint.setColor(Color.GRAY);
//            RectF rectF = new RectF(0, 0, 300, 100);// 设置个新的长方形
//            canvas.drawRoundRect(rectF, 20, 20, mPaint);//第二个参数是x半径，第三个参数是y半径
//
//            mPaint.setColor(Color.WHITE);
//            RectF rectF2 = new RectF(135, 5, 295, 95);// 设置个新的长方形
//            canvas.drawRoundRect(rectF2, 20, 20, mPaint);//第二个参数是x半径，第三个参数是y半径
//        }else {
//            //画圆角矩形
//            mPaint.setColor(Color.GREEN);
//            RectF rectF = new RectF(0, 0, 300, 100);// 设置个新的长方形
//            canvas.drawRoundRect(rectF, 20, 20, mPaint);//第二个参数是x半径，第三个参数是y半径
//
//            mPaint.setColor(Color.WHITE);
//            RectF rectF2 = new RectF(5, 5, 165, 95);// 设置个新的长方形
//            canvas.drawRoundRect(rectF2, 20, 20, mPaint);//第二个参数是x半径，第三个参数是y半径
//        }


        canvas.drawBitmap(mBitmapBg,0,0,mPaint);
        canvas.drawBitmap(mBitmapSlide,mSlideLeft,0,mPaint);

    }

    public void setSlideImage(int id){
        mBitmapSlide = BitmapFactory.decodeResource(getResources(), id);
    }

}
