
package com.likebamboo.phoneshow.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 与xml数据文件相关的操作
 * 
 * @author likebamboo
 */
public class ShowPref {
    public static final String SHOW_TYPE = "show_type";

    public static final String TYPE_HALF_VALUE = "type_half_value";

    public static final int TYPE_ACTIVITY = 0x10000;

    public static final int TYPE_FULL_DIALOG = 0x10001;

    public static final int TYPE_HALF_DIALOG = 0x10002;

    public static final int TYPE_HALF_DIALOG_DEFAULT = 50;

    private static ShowPref mInstance = null;

    private Context mContext = null;

    private static String PREF_NAME = "phone_show_pref";

    private SharedPreferences mSettings = null;

    private SharedPreferences.Editor mEditor = null;

    private ShowPref(Context ctx) {
        mContext = ctx.getApplicationContext();
        mSettings = mContext.getSharedPreferences(PREF_NAME, 0);
        mEditor = mSettings.edit();
    }

    public static ShowPref getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ShowPref(context);
        }
        return mInstance;
    }

    /**
     * 判断是否有某一个值
     * 
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return mSettings.contains(key);
    }

    /**
     * 获取字符串
     * 
     * @param key key值
     * @return
     */
    public String loadString(String key) {
        return mSettings.getString(key, "");
    }

    /**
     * 保存字符串
     * 
     * @param key key值
     * @param value value值
     * @return
     */
    public boolean putString(String key, String value) {
        mEditor.putString(key, value);
        return mEditor.commit();
    }

    /**
     * 获取整型值
     * 
     * @param key key值
     * @return
     */
    public int loadInt(String key) {
        return mSettings.getInt(key, -1);
    }

    /**
     * 获取整型值
     * 
     * @param key key值
     * @return
     */
    public int loadInt(String key, int defValue) {
        return mSettings.getInt(key, defValue);
    }

    /**
     * 保存整型值
     * 
     * @param key key值
     * @param value value值
     * @return
     */
    public boolean putInt(String key, int value) {
        mEditor.putInt(key, value);
        return mEditor.commit();
    }

}
