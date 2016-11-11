
package com.likebamboo.phoneshow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.likebamboo.phoneshow.R;

public class Title extends RelativeLayout {

    /**
     * 上下文对象
     */
    private Context mContext = null;

    /**
     * 返回按钮
     */
    private Button mBackBt = null;

    /**
     * 标题
     */
    private TextView mTitleTv = null;

    /**
     * 确定按钮
     */
    private Button mSureBt = null;

    public Title(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public Title(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        mContext = context;
        init();
    }

    public Title(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.title_layout, this, true);
    }

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        // super.onFinishInflate();
        mBackBt = (Button)findViewById(R.id.title_back_bt);
        mTitleTv = (TextView)findViewById(R.id.title_title_tv);
        mSureBt = (Button)findViewById(R.id.title_sure_bt);
    }

    /**
     * 设置返回按钮的监听器
     * 
     * @param listener 监听器
     */
    public void setBackListener(OnClickListener listener) {
        mBackBt.setVisibility(View.VISIBLE);
        mBackBt.setOnClickListener(listener);
        setTitlePadding();
    }

    /**
     * 设置标题栏标题
     * 
     * @param text 标题
     */
    public void setTitle(CharSequence text) {
        mTitleTv.setText(text);
    }

    /**
     * 设置标题栏标题
     * 
     * @param text 标题
     */
    public void setTitle(int resId) {
        mTitleTv.setText(resId);
    }

    /**
     * 设置确定按钮监听器
     * 
     * @param listener
     */
    public void setSureListener(String text, OnClickListener listener) {
        mSureBt.setVisibility(View.VISIBLE);
        mSureBt.setText(text);
        mSureBt.setOnClickListener(listener);
        setTitlePadding();
    }

    /**
     * 设置边距。当有左边图标或者右边图标的时候需要设置
     */
    private void setTitlePadding() {
        mTitleTv.setPadding(0, 0, 0, dp2px(mContext, 8.5F));
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     * 
     * @param dipValue
     * @param scale （DisplayMetrics类中属性density）
     * @return
     */
    private int dp2px(Context ctx, float dipValue) {
        return (int)(dipValue * ctx.getResources().getDisplayMetrics().density + 0.5F);
    }
}
