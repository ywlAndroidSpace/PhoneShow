
package com.likebamboo.phoneshow.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.likebamboo.phoneshow.OverLayActivity;
import com.likebamboo.phoneshow.entities.City;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Utils {

    /**
     * 直接显示Toast
     * 
     * @param context 当前环境上下文对象
     * @param text 内容
     * @param isShort 是否短时间显示（false则为长时间显示）
     */
    public static void showToast(Context context, String text, boolean isShort) {
        if (isShort) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 直接显示Toast
     * 
     * @param context 当前环境上下文对象
     * @param text 内容
     * @param isShort 是否短时间显示（false则为长时间显示）
     */
    public static void showToast(Context context, int resId, boolean isShort) {
        if (isShort) {
            Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 直接显示Toast
     * 
     * @param context 当前环境上下文对象
     * @param resId 字符串资源id
     */
    public static void showToast(Context context, int resId) {
        showToast(context, resId, true);
    }

    /**
     * 直接显示Toast
     * 
     * @param context 当前环境上下文对象
     * @param text 内容
     */
    public static void showToast(Context context, String text) {
        showToast(context, text, true);
    }

    /**
     * 关闭输入法
     */
    public static void closeEditer(Activity context) {
        View view = context.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager)context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 判断网络是否可用
     */
    public static boolean CheckNetworkConnection(Context context) {
        ConnectivityManager con = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = con.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            // 当前网络不可用
            return false;
        }
        return true;
    }

    /**
     * 判断wifi网络是否可用
     */
    public static boolean IsWifiEnable(Context context) {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * Get the external app cache directory.
     * 
     * @param context The context to use
     * @return The external cache dir
     */
    @TargetApi(8)
    public static File getExternalFileDir(Context context) {
        if (hasFroyo()) {
            final File file = context.getExternalFilesDir(null);
            return file;
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String fileDir = "/Android/data/" + context.getPackageName() + "/files";
        return new File(Environment.getExternalStorageDirectory().getPath() + fileDir);
    }

    public static File getCacheDir(Context context) {
        File cacheDir = null;
        try {
            cacheDir = getExternalFileDir(context);
        } catch (Exception e) {
            e.printStackTrace();
            cacheDir = context.getFilesDir();
        }
        return cacheDir;
    }

    /**
     * >= android 2.2 版本
     * 
     * @return
     */
    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * >= android 2.3 版本
     * 
     * @return
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * >= android 3.0 版本
     * 
     * @return
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2;
    }

    /**
     * 收起输入法
     * 
     * @param ctx
     * @param view
     */
    public static void HideKeyboard(Context ctx, View view) {
        if (null == view)
            return;
        InputMethodManager imm = (InputMethodManager)ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 字符串转Html
     * 
     * @param text 字符串
     * @return 格式化的html。
     */
    public static Spanned formatHtml(String text) {
        return Html.fromHtml(text);
    }

    /**
     * 解析json 数据
     * 
     * @param ret
     * @param json
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean parseData(ArrayList<City> ret, String json) {
        if (TextUtils.isEmpty(json)) {
            return false;
        }
        if (ret == null) {
            ret = new ArrayList<City>();
        }
        ret.clear();
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<City>>() {
            }.getType();

            ret.addAll((ArrayList<? extends City>)gson.fromJson(json, type));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 发送挂断电话的广播
     * 
     * @param ctx 上下文对象
     */
    public static void sendEndCallBroadCast(Context ctx) {
        Intent i = new Intent();
        i.setAction(OverLayActivity.ACTION_END_CALL);
        ctx.sendBroadcast(i);
    }

    /**
     * 挂断电话
     */
    public static synchronized void endCall(Context ctx) {
        TelephonyManager mTelMgr = (TelephonyManager)ctx
                .getSystemService(Service.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        try {
            Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[])null);
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = null;
            System.out.println("End call.");
            iTelephony = (ITelephony)getITelephonyMethod.invoke(mTelMgr, (Object[])null);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Fail to answer ring call.");
        }
    }

    /**
     * 接听电话
     */
    public static synchronized void answerRingingCall(Context ctx) {
        // 据说该方法只能用于Android2.3及2.3以上的版本上，但本人在2.2上测试可以使用
        try {
            // 插耳机
            Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
            localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            localIntent1.putExtra("state", 1);
            localIntent1.putExtra("microphone", 1);
            localIntent1.putExtra("name", "Headset");
            ctx.sendOrderedBroadcast(localIntent1, "android.permission.CALL_PRIVILEGED");

            // 按下耳机按钮
            Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
            KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_HEADSETHOOK);
            localIntent2.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent1);
            ctx.sendOrderedBroadcast(localIntent2, "android.permission.CALL_PRIVILEGED");

            // 放开耳机按钮
            Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
            KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
            localIntent3.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent2);
            ctx.sendOrderedBroadcast(localIntent3, "android.permission.CALL_PRIVILEGED");

            // 拔出耳机
            Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
            localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            localIntent4.putExtra("state", 0);
            localIntent4.putExtra("microphone", 1);
            localIntent4.putExtra("name", "Headset");
            ctx.sendOrderedBroadcast(localIntent4, "android.permission.CALL_PRIVILEGED");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接听电话
     */
    public static synchronized void answerCall(Context ctx) {
        TelephonyManager mTelMgr = (TelephonyManager)ctx
                .getSystemService(Service.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        try {
            Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[])null);
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = null;
            iTelephony = (ITelephony)getITelephonyMethod.invoke(mTelMgr, (Object[])null);
            iTelephony.answerRingingCall();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Fail to answer ring call.");
        }
    }
}
