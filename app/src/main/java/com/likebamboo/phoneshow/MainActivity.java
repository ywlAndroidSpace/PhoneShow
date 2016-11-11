
package com.likebamboo.phoneshow;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.likebamboo.phoneshow.config.ShowPref;
import com.likebamboo.phoneshow.util.Utils;
import com.likebamboo.phoneshow.widget.TimeDragPicker;
import com.likebamboo.phoneshow.widget.Title;

/**
 * 主界面/设置界面
 * 
 * @author likebamboo
 */
public class MainActivity extends Activity {
    /**
     * 标题栏
     */
    private Title mTitle = null;

    /**
     * 全屏 DiaLog显示
     */
    private View mShowAsFullDialog = null;

    /**
     * 半屏显示
     */
    private View mShowAsHalfDialog = null;

    /**
     * 以Activity形式显示
     */
    private View mShowAsActivity = null;

    /**
     * 百分比选择器
     */
    private TimeDragPicker mTimePicker = null;

    /**
     * 配置信息
     */
    private ShowPref pref = null;

    /**
     * 来电秀显示的形式
     */
    private int mShowType = ShowPref.TYPE_HALF_DIALOG_DEFAULT;

    private int[] markLong = new int[] {
            30, 40, 50, 60, 70
    };

    private int[] markShort = new int[] {
            25, 35, 45, 55, 65, 75
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        pref = ShowPref.getInstance(this);
        mShowType = pref.loadInt(ShowPref.SHOW_TYPE);

        initView();
        addListener();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        mTitle = (Title)findViewById(R.id.main_title);
        mTitle.setTitle(R.string.app_name);

        mShowAsActivity = findViewById(R.id.main_activity_layout);
        mShowAsFullDialog = findViewById(R.id.main_full_dialog_layout);
        mShowAsHalfDialog = findViewById(R.id.main_half_dialog_layout);

        mTimePicker = (TimeDragPicker)findViewById(R.id.main_time_picker);

        mTimePicker.defineMark(markShort, markLong);
        mTimePicker.setTextView(Utils.formatHtml("<b>" + getString(R.string.percent) + "</b>"));
        // mTimePicker.enablePopupRemind(false);
        if (mShowType == ShowPref.TYPE_ACTIVITY) {// 以Activity形式显示
            showIcon(R.id.main_activity_layout);
        } else if (mShowType == ShowPref.TYPE_FULL_DIALOG) {
            showIcon(R.id.main_full_dialog_layout);
        } else {
            showIcon(R.id.main_half_dialog_layout);
        }
    }

    /**
     * 获取选中的项目
     * 
     * @param longMark
     * @param shorMark
     * @param value
     */
    private int setSelect(int[] longMark, int[] shorMark, int value) {
        for (int i = 0; i < longMark.length; i++) {
            if (longMark[i] == value) {
                return i * 2 + 1;
            }
        }
        for (int i = 0; i < shorMark.length; i++) {
            if (shorMark[i] == value) {
                return i * 2;
            }
        }
        return (longMark.length + shorMark.length) / 2;
    }

    /**
     * 添加监听器
     */
    private void addListener() {
        mShowAsActivity.setOnClickListener(new ShowOnClickListener());
        mShowAsFullDialog.setOnClickListener(new ShowOnClickListener());
        mShowAsHalfDialog.setOnClickListener(new ShowOnClickListener());

        mTimePicker.setOnDigitSelectListener(new TimeDragPicker.OnDigitSelectListener() {
            @Override
            public void onDigitConfirm(TimeDragPicker v, int select) {
                // TODO Auto-generated method stub
                pref.putInt(ShowPref.TYPE_HALF_VALUE, select);
            }

            @Override
            public void onDigitChange(TimeDragPicker v, int select, int index, int[] collection) {
                // TODO Auto-generated method stub
            }
        });
    }

    private class ShowOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.main_activity_layout:// 以activity形式显示
                    pref.putInt(ShowPref.SHOW_TYPE, ShowPref.TYPE_ACTIVITY);
                    break;
                case R.id.main_full_dialog_layout:// 以全屏Dialog形式显示
                    pref.putInt(ShowPref.SHOW_TYPE, ShowPref.TYPE_FULL_DIALOG);
                    break;
                case R.id.main_half_dialog_layout:// 以半屏Dialog形式显示
                    pref.putInt(ShowPref.SHOW_TYPE, ShowPref.TYPE_HALF_DIALOG);
                    break;
                default:
                    break;
            }
            showIcon(v.getId());
        }
    }

    /**
     * 显示选中项图标
     * 
     * @param resId
     */
    private void showIcon(int resId) {
        switch (resId) {
            case R.id.main_activity_layout:
                findViewById(R.id.main_activity_iv).setVisibility(View.VISIBLE);
                findViewById(R.id.main_full_dialog_iv).setVisibility(View.INVISIBLE);
                findViewById(R.id.main_half_dialog_iv).setVisibility(View.INVISIBLE);
                mTimePicker.setVisibility(View.GONE);
                break;
            case R.id.main_full_dialog_layout:
                findViewById(R.id.main_activity_iv).setVisibility(View.INVISIBLE);
                findViewById(R.id.main_full_dialog_iv).setVisibility(View.VISIBLE);
                findViewById(R.id.main_half_dialog_iv).setVisibility(View.INVISIBLE);
                mTimePicker.setVisibility(View.GONE);
                break;
            case R.id.main_half_dialog_layout:
                findViewById(R.id.main_activity_iv).setVisibility(View.INVISIBLE);
                findViewById(R.id.main_full_dialog_iv).setVisibility(View.INVISIBLE);
                findViewById(R.id.main_half_dialog_iv).setVisibility(View.VISIBLE);
                mTimePicker.setVisibility(View.VISIBLE);
                mTimePicker.setSeleted(setSelect(markLong, markShort,
                        pref.loadInt(ShowPref.TYPE_HALF_VALUE, ShowPref.TYPE_HALF_DIALOG_DEFAULT)));
                break;
            default:
                break;
        }
    }
}
