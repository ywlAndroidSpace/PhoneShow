
package com.likebamboo.phoneshow.widget.overlay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;

public abstract class Overlay {
    public static ViewGroup mOverlay;

    protected static final Object monitor = new Object();

    protected static ViewGroup init(Context context, int layout, WindowManager.LayoutParams params) {
        WindowManager wm = (WindowManager)context.getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        if (mOverlay != null) {
            try {
                wm.removeView(mOverlay);
                mOverlay = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewGroup overlay = (ViewGroup)inflater.inflate(layout, null);
        mOverlay = overlay;

        wm.addView(overlay, params);
        return overlay;
    }

    public static void show(Context context, String number) {
        //
    }

    public static void hide(Context context) {
        //
    }
}
