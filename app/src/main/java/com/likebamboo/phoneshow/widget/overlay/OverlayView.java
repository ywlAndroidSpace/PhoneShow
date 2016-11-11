
package com.likebamboo.phoneshow.widget.overlay;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.likebamboo.phoneshow.R;
import com.likebamboo.phoneshow.adapter.CityAdapter;
import com.likebamboo.phoneshow.entities.City;
import com.likebamboo.phoneshow.task.GetInfoTask;
import com.likebamboo.phoneshow.util.Utils;
import com.likebamboo.phoneshow.widget.Title;

import java.util.ArrayList;

/**
 * 半屏显示
 * 
 * @author likebamboo
 */
public class OverlayView extends Overlay {

    /**
     * 网络操作结果
     */
    public static final int MSG_OK = 0x1000;

    /**
     * 网络操作结果
     */
    public static final int MSG_FAILED = 0x1001;

    /**
     * 来电号码extra
     */
    public static final String EXTRA_PHONE_NUM = "phoneNum";

    private static Context mContext = null;

    /**
     * 标题栏
     */
    private static Title mTitle = null;

    /**
     * 正在加载布局
     */
    private static LinearLayout mLoadingLayout = null;

    /**
     * 正在加载文字显示
     */
    private static TextView mLoadingTv = null;

    /**
     * 网络操作结果界面。
     */
    private static LinearLayout mRetLayout = null;

    /**
     * 城市结果列表
     */
    private static ListView mCityList = null;

    /**
     * 城市适配器
     */
    private static CityAdapter mCityAdapter = null;

    /**
     * 挂电话按钮
     */
    private static Button mEndCallBt = null;

    /**
     * 接听电话按钮
     */
    private static Button mAnswerCallBt = null;

    /**
     * 异步查询任务
     */
    private static GetInfoTask getInfoTask = null;

    @SuppressLint("HandlerLeak")
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // super.handleMessage(msg);
            switch (msg.what) {
                case MSG_OK:
                    String json = msg.getData().getString("data");
                    ArrayList<City> data = new ArrayList<City>();
                    if (Utils.parseData(data, json)) {
                        mCityAdapter = new CityAdapter(mContext, data);
                        mCityList.setAdapter(mCityAdapter);
                        mCityAdapter.notifyDataSetChanged();
                    }

                    mLoadingLayout.setVisibility(View.GONE);
                    mRetLayout.setVisibility(View.VISIBLE);
                    break;
                case MSG_FAILED:
                    mLoadingTv.setText(msg.obj + "");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 显示
     * 
     * @param context 上下文对象
     * @param number
     */
    public static void show(final Context context, final String number, final int percentScreen) {
        synchronized (monitor) {
            mContext = context;

            init(context, number, R.layout.call_over_layout, percentScreen);
            InputMethodManager imm = (InputMethodManager)context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            // 启动查询
            startSearch();
        }
    }

    /**
     * 启动查询
     */
    @SuppressLint("NewApi")
    private static void startSearch() {
        synchronized (monitor) {
            if (getInfoTask != null && getInfoTask.isRunning()) {
                getInfoTask.cancel();
                getInfoTask = null;
            }
            // TODO Auto-generated method stub
            getInfoTask = new GetInfoTask(mContext, new GetInfoTask.IOnResultListener() {
                @Override
                public void onResult(boolean success, String error, String result) {
                    // TODO Auto-generated method stub
                    Message msg = handler.obtainMessage();
                    if (success) {
                        msg.what = MSG_OK;
                        msg.getData().putString("data", result);
                    } else {
                        msg.what = MSG_FAILED;
                        msg.obj = error;
                    }
                    handler.sendMessage(msg);
                }
            });

            if (Utils.hasHoneycomb()) {
                getInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                getInfoTask.execute();
            }
        }
    }

    /**
     * 隐藏
     * 
     * @param context
     */
    public static void hide(Context context) {
        synchronized (monitor) {
            if (mOverlay != null) {
                try {
                    WindowManager wm = (WindowManager)context
                            .getSystemService(Context.WINDOW_SERVICE);
                    // Remove view from WindowManager
                    wm.removeView(mOverlay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mOverlay = null;
            }
        }
    }

    /**
     * 初始化布局
     * 
     * @param context 上下文对象
     * @param number 电话号码
     * @param layout 布局文件
     * @return 布局
     */
    private static ViewGroup init(Context context, String number, int layout, int percentScreen) {
        WindowManager.LayoutParams params = getShowingParams();
        int height = getHeight(context, percentScreen);
        params.height = height;
        ViewGroup overlay = init(context, layout, params);

        initView(overlay, number, percentScreen);

        return overlay;
    }

    /**
     * 初始化界面
     */
    private static void initView(View v, String phoneNum, int percentScreen) {
        // 标题栏
        mTitle = (Title)v.findViewById(R.id.overlay_title);
        mTitle.setTitle(R.string.call_ringing);

        // 显示来电电话
        ((TextView)v.findViewById(R.id.overlay_result_msg))
                .setText(Utils.formatHtml(mContext.getString(R.string.call_ringing_msg,
                        "<font color='red'>" + phoneNum + "</font>")));

        // 初始化各个控件
        mLoadingLayout = (LinearLayout)v.findViewById(R.id.overlay_loading_layout);
        mLoadingTv = (TextView)v.findViewById(R.id.overlay_loading_tv);
        mRetLayout = (LinearLayout)v.findViewById(R.id.overlay_result_layout);
        mCityList = (ListView)v.findViewById(R.id.overlay_result_list);

        // 显示正在加载数据。
        mLoadingLayout.setVisibility(View.VISIBLE);
        v.findViewById(R.id.overlay_loading_pb).setVisibility(View.VISIBLE);
        mRetLayout.setVisibility(View.GONE);

        if (percentScreen == 100) {
            // 接听电话与挂断电话
            mEndCallBt = (Button)v.findViewById(R.id.overlay_end_call_bt);
            mAnswerCallBt = (Button)v.findViewById(R.id.overlay_answer_call_bt);
            v.findViewById(R.id.overlay_dealwith_layout).setVisibility(View.VISIBLE);
            addListener();
        }
    }

    /**
     * 添加监听器
     */
    private static void addListener() {
        mEndCallBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Utils.endCall(mContext);
            }
        });
        mAnswerCallBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (Utils.hasGingerbread()) {
                    Utils.answerRingingCall(mContext);
                } else {
                    Utils.answerRingingCall(mContext);
                }
            }
        });
    }

    /**
     * 获取显示参数
     * 
     * @return
     */
    private static WindowManager.LayoutParams getShowingParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        // TYPE_TOAST TYPE_SYSTEM_OVERLAY 在其他应用上层 在通知栏下层 位置不能动鸟
        // TYPE_PHONE 在其他应用上层 在通知栏下层
        // TYPE_PRIORITY_PHONE TYPE_SYSTEM_ALERT 在其他应用上层 在通知栏上层 没试出来区别是啥
        // TYPE_SYSTEM_ERROR 最顶层(通过对比360和天天动听歌词得出)
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.x = 0;
        params.y = 0;
        params.format = PixelFormat.RGBA_8888;// value = 1
        params.gravity = Gravity.TOP;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        return params;
    }

    /**
     * 获取界面显示的高度 ，默认为手机高度的2/3
     * 
     * @param context 上下文对象
     * @return
     */
    private static int getHeight(Context context, int percentScreen) {
        return getLarger(context) * percentScreen / 100;
    }

    @SuppressWarnings("deprecation")
    private static int getLarger(Context context) {
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int height = 0;
        if (Utils.hasHoneycombMR2()) {
            height = getLarger(display);
        } else {
            height = display.getHeight() > display.getWidth() ? display.getHeight() : display
                    .getWidth();
        }
        System.out.println("getLarger: " + height);
        return height;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private static int getLarger(Display display) {
        Point size = new Point();
        display.getSize(size);
        return size.y > size.x ? size.y : size.x;
    }

}
