
package com.likebamboo.phoneshow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.likebamboo.phoneshow.adapter.CityAdapter;
import com.likebamboo.phoneshow.entities.City;
import com.likebamboo.phoneshow.task.GetInfoTask;
import com.likebamboo.phoneshow.util.Utils;
import com.likebamboo.phoneshow.widget.Title;

import java.util.ArrayList;

/**
 * 来电显示界面
 * 
 * @author likebmaboo
 */
public class OverLayActivity extends Activity {

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

    /**
     * 挂断电话action
     */
    public static final String ACTION_END_CALL = "com.likebamboo.phoneshow.ACTION_END_CALL";

    /**
     * 标题栏
     */
    private Title mTitle = null;

    /**
     * 正在加载布局
     */
    private LinearLayout mLoadingLayout = null;

    /**
     * 正在加载文字显示
     */
    private TextView mLoadingTv = null;

    /**
     * 网络操作结果界面。
     */
    private LinearLayout mRetLayout = null;

    /**
     * 城市结果列表
     */
    private ListView mCityList = null;

    /**
     * 城市适配器
     */
    private CityAdapter mCityAdapter = null;

    /**
     * 挂电话按钮
     */
    private Button mEndCallBt = null;

    /**
     * 接听电话按钮
     */
    private Button mAnswerCallBt = null;

    /**
     * 来电号码
     */
    private String phoneNum = "";

    /**
     * 点亮屏幕Intent
     */
    private Intent scrOnIntent = null;

    /**
     * 获取信息异步线程
     */
    private GetInfoTask getInfoTask = null;

    /**
     * 电话挂断广播接收器
     */
    private BroadcastReceiver mEndCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent != null && intent.getAction().equals(ACTION_END_CALL)) {
                finish();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // super.handleMessage(msg);
            switch (msg.what) {
                case MSG_OK:
                    String json = msg.getData().getString("data");
                    ArrayList<City> data = new ArrayList<City>();
                    if (Utils.parseData(data, json)) {
                        mCityAdapter = new CityAdapter(OverLayActivity.this, data);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.call_over_layout);

        // 点亮屏幕
        scrOnIntent = new Intent(this, ScreenService.class);
        startService(scrOnIntent);

        if (getIntent().hasExtra(EXTRA_PHONE_NUM)) {
            phoneNum = getIntent().getStringExtra(EXTRA_PHONE_NUM);
        }

        // 初始化界面
        initView();
        // 添加监听器
        addListener();

        // 启动查询
        startSearch();

        registerReceiver(mEndCallReceiver, new IntentFilter(ACTION_END_CALL));
    }

    /**
     * 初始化界面
     */
    private void initView() {
        // 标题栏
        mTitle = (Title)findViewById(R.id.overlay_title);
        mTitle.setTitle(R.string.call_ringing);

        // 显示来电电话
        ((TextView)findViewById(R.id.overlay_result_msg)).setText(Utils.formatHtml(getString(
                R.string.call_ringing_msg, "<font color='red'>" + phoneNum + "</font>")));

        // 初始化各个控件
        mLoadingLayout = (LinearLayout)findViewById(R.id.overlay_loading_layout);
        mLoadingTv = (TextView)findViewById(R.id.overlay_loading_tv);
        mRetLayout = (LinearLayout)findViewById(R.id.overlay_result_layout);
        mCityList = (ListView)findViewById(R.id.overlay_result_list);

        // 显示正在加载数据。
        mLoadingLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.overlay_loading_pb).setVisibility(View.VISIBLE);
        mRetLayout.setVisibility(View.GONE);

        // 接听电话与挂断电话
        mEndCallBt = (Button)findViewById(R.id.overlay_end_call_bt);
        mAnswerCallBt = (Button)findViewById(R.id.overlay_answer_call_bt);
        findViewById(R.id.overlay_dealwith_layout).setVisibility(View.VISIBLE);
    }

    /**
     * 添加监听器
     */
    private void addListener() {
        mEndCallBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Utils.endCall(OverLayActivity.this);
            }
        });
        mAnswerCallBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (Utils.hasGingerbread()) {
                    Utils.answerRingingCall(OverLayActivity.this);
                } else {
                    Utils.answerRingingCall(OverLayActivity.this);
                }
            }
        });
    }

    /**
     * 启动查询
     */
    @SuppressLint("NewApi")
    private void startSearch() {
        if (getInfoTask != null && getInfoTask.isRunning()) {
            getInfoTask.cancel();
            getInfoTask = null;
        }
        // TODO Auto-generated method stub
        getInfoTask = new GetInfoTask(this, new GetInfoTask.IOnResultListener() {
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

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        stopService(scrOnIntent);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        try {
            unregisterReceiver(mEndCallReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();

    }
}
