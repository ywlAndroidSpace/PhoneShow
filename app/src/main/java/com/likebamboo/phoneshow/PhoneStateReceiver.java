
package com.likebamboo.phoneshow;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.likebamboo.phoneshow.config.ShowPref;
import com.likebamboo.phoneshow.util.Utils;
import com.likebamboo.phoneshow.widget.overlay.OverlayView;

public class PhoneStateReceiver extends BroadcastReceiver {

    /**
     * 电话管理
     */
    private TelephonyManager telMgr = null;

    private static final Object monitor = new Object();

    @Override
    public void onReceive(Context context, Intent intent) {
        final Context ctx = context;
        final ShowPref pref = ShowPref.getInstance(ctx);
        /**
         * 以什么方式显示界面
         */
        final int showType = pref.loadInt(ShowPref.SHOW_TYPE);

        telMgr = (TelephonyManager)ctx.getSystemService(Service.TELEPHONY_SERVICE);
        switch (telMgr.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:// 来电响铃
                System.out.println("....................主人，那家伙又来电话了....................");
                final String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                System.out.println("number:" + number);

                if (number.length() == 11) {
                    synchronized (monitor) {
                        switch (showType) {
                            case ShowPref.TYPE_ACTIVITY:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        showActivity(ctx, number);
                                    }
                                }, 1000);
                                break;
                            case ShowPref.TYPE_FULL_DIALOG:
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        showWindow(ctx, number, 100);
                                    }
                                }, 100);
                                break;
                            case ShowPref.TYPE_HALF_DIALOG:// 非满屏Dialog
                            default:// 默认显示半屏dialog
                                int value = pref.loadInt(ShowPref.TYPE_HALF_VALUE,
                                        ShowPref.TYPE_HALF_DIALOG_DEFAULT);
                                final int percent = value >= 25 ? (value <= 75 ? value : 75) : 25;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        showWindow(ctx, number, percent);
                                    }
                                }, 100);
                                break;
                        }
                    }
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:// 接听电话
                break;
            case TelephonyManager.CALL_STATE_IDLE:// 挂断电话
                synchronized (monitor) {
                    switch (showType) {
                        case ShowPref.TYPE_ACTIVITY:
                            Utils.sendEndCallBroadCast(ctx);
                            break;
                        case ShowPref.TYPE_FULL_DIALOG:
                        case ShowPref.TYPE_HALF_DIALOG:
                        default:// 默认会显示半屏的dialog
                            closeWindow(ctx);
                            break;
                    }
                }
                break;
            default:
                break;
        }

    }

    /**
     * 显示来电Activity
     * 
     * @param ctx
     * @param number
     */
    private void showActivity(Context ctx, String number) {
        Intent intent = new Intent(ctx, OverLayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(OverLayActivity.EXTRA_PHONE_NUM, number);
        ctx.startActivity(intent);
    }

    /**
     * 显示来电弹窗
     * 
     * @param ctx 上下文对象
     * @param number 电话号码
     */
    private void showWindow(Context ctx, String number, int percentScreen) {
        OverlayView.show(ctx, number, percentScreen);
    }

    /**
     * 关闭来电弹窗
     * 
     * @param ctx 上下文对象
     */
    private void closeWindow(Context ctx) {
        OverlayView.hide(ctx);
    }

}
