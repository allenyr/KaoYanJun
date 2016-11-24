package com.sharon.allen.a18_sharon.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.sharon.allen.a18_sharon.R;

/**
 * Created by Allen on 2016/11/8.
 */

public class CircleButton extends TextView {

    private Paint mPaint;
    private static int WHITH;
    private static int HIGHT;
    private static final String NAME_SPACE = "http://schemas.android.com/apk/res-auto";
    private int backgroundColor;
    private String text;
    private Boolean enable;
    private Context mContext;

    //----------------------------------------------------------------------------
    //接口
    public interface OnStateListener{
        public void onState(View view, String state);
    }
    //初始化接口变量
    private OnStateListener mListener = null;
    //自定义控件的自定义事件
    public void setOnStateListener(OnStateListener listener){
        mListener = listener;
    }
    //----------------------------------------------------------------------------

    public CircleButton(Context context) {
        super(context);
        initView();
    }

    public CircleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();

        /*这里取得declare-styleable集合*/
        TypedArray typeArray = context.obtainStyledAttributes(attrs,R.styleable.ImageButton);
                 /*这里从集合里取出相对应的属性值,第二参数是如果使用者没用配置该属性时所用的默认值*/
        backgroundColor = typeArray.getColor(R.styleable.ImageButton_backgroundColor,0XFFFFFFFF);
        text = typeArray.getString(R.styleable.ImageButton_text);
        enable = typeArray.getBoolean(R.styleable.ImageButton_enable,false);
        WHITH = (int) typeArray.getDimension(R.styleable.ImageButton_layout_width,100);
        HIGHT = (int) typeArray.getDimension(R.styleable.ImageButton_layout_height,100);

        mPaint.setColor(backgroundColor);
        if (text == null){
            text = "";
        }
            /*关闭资源*/
        typeArray.recycle();
    }

    public CircleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {

        mPaint = new Paint();
        //画笔颜色
        mPaint.setColor(getResources().getColor(R.color.colorRed));
        // 设置alpha不透明度，范围为0~255
        mPaint.setAlpha(255);
        //设置画笔粗细
        mPaint.setStrokeWidth(10);
        // 是否抗锯齿
        mPaint.setAntiAlias(true);
    }

    //设置画布大大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int max = Math.max(measuredWidth, measuredHeight);
        setMeasuredDimension(max, max);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(WHITH/2,HIGHT/2,WHITH/2,mPaint);
        super.draw(canvas);
        if (enable){
            CircleButton.this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    typeDialog();
                }
            });
        }

    }

    public void setUnsolved(){
        mPaint.setColor(getResources().getColor(R.color.colorRed));
        text = "未解决";
        invalidate();
    }

    public void setsolved(){
        mPaint.setColor(getResources().getColor(R.color.colorGreen));
        text = "已解决";
        invalidate();
    }


    public void setEnable(Context context,Boolean enable){
        mContext = context;
        this.enable = enable;
        invalidate();
    }

    private void typeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("类型");
        final String[] type = {"未解决", "已解决","删除"};
        builder.setSingleChoiceItems(type, 0, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int type)
            {
                switch (type){
                    case 0:
                        setUnsolved();
                        mListener.onState(CircleButton.this,"未解决");
                        break;
                    case 1:
                        setsolved();
                        mListener.onState(CircleButton.this,"已解决");
                        break;
                    case 2:
                        mListener.onState(CircleButton.this,"删除");
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
