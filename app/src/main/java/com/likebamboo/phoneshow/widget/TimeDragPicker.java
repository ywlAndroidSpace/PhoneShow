
package com.likebamboo.phoneshow.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.likebamboo.phoneshow.R;

import java.util.Arrays;

/**
 * <b>类功能描述：</b><div style="margin-left:40px;margin-top:-10px"> 一个滑动选择(时,分)的控件
 * 有兴趣的童鞋可以加入布局文件自定义选项,我就懒得加了 ╮(╯▽╰)╭ </div>
 * 
 * @author <a href="mailto:184618345@qq.com">017</a>
 * @version 1.0 </p> 修改时间：</br> 修改备注：</br>
 */
@SuppressLint("HandlerLeak")
public class TimeDragPicker extends RelativeLayout {
    public static final int TYPE_HOUR = 1;

    public static final int TYPE_MINUTE = 2;

    private static final int ANIMATION_FRAME_DURATION = 1000 / 60;// 动画帧速率

    private static int[] markLongToDraw_HOUR = {
            0, 5, 10, 15, 20
    };

    private static int[] markShortToDraw_HOUR = {
            1, 2, 3, 4, 6, 7, 8, 9, 11, 12, 13, 14, 16, 17, 18, 19, 21, 22, 23
    };

    private static int[] markLongToDraw_MINUTE = {
            0, 10, 20, 30, 40, 50
    };

    private static int[] markShortToDraw_MINUTE = {
            5, 15, 25, 35, 45, 55
    };

    private TextView typeName;

    private LayoutParams lp;

    private static final int HIGHT = 50;// 控件默认高度

    private Bitmap selecter;

    private Bitmap selected;

    private int selecterWidth, selecterHeight, selectedWidth, selectedHeight, textViewWidth,
            textViewHeight;

    private int markPaddingLeft, markPaddingRight;

    private int[] markShortToDraw = markShortToDraw_HOUR;

    private float[] markCollectionShort = new float[markShortToDraw.length];

    private int[] markLongToDraw = markLongToDraw_HOUR;

    private float[] markCollectionLong = new float[markLongToDraw.length];

    private int[] digitCollectionAll = new int[markLongToDraw.length + markShortToDraw.length];

    private float[] markCollectionAll = new float[markLongToDraw.length + markShortToDraw.length];

    private int lengthOfMark;// 长刻度长度,短刻度为他的一半

    private Bitmap markBg;// 刻度背景,生成一次即缓存起来费事次次画

    private int selecting;// 当前选中的数字

    private int location;// 当前选中的位置

    /*--setter and getter--*/
    private boolean isDrawShortDigit = true;// 是否绘制小刻度数字

    private boolean isPopupRemind = true;// 是否气泡提醒

    private Paint digitBig;

    private Paint digitSmall;

    private Paint digitSelected;

    private Paint markLine;

    private int currentType = TYPE_HOUR;// -1表示自定义刻度

    /*--End--*/

    public TimeDragPicker(Context context) {
        this(context, null);
    }

    public TimeDragPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScale = context.getResources().getDisplayMetrics().density;
        setBackgroundColor(0xFFEAEAEA);
        setPadding(0, 0, 0, 0);
        selecter = BitmapFactory.decodeResource(getResources(), R.drawable.btn_time_choose);
        selected = BitmapFactory.decodeResource(getResources(), R.drawable.btn_time_choose_bg);
        selecterWidth = selecter.getWidth();
        selecterHeight = selecter.getHeight();
        selectedWidth = selected.getWidth();
        selectedHeight = selected.getHeight();
        markPaddingLeft = markPaddingRight = (int)(10 * mScale);
        typeName = new TextView(context);
        typeName.setText("小时");
        typeName.setGravity(Gravity.CENTER);
        typeName.setBackgroundColor(0xffBAD4EC);
        typeName.setId(184618345);
        typeName.setTextSize(20);
        typeName.setTextColor(0xff5B6F81);
        textViewWidth = context.getResources().getDisplayMetrics().widthPixels / 6;
        textViewHeight = (int)(HIGHT * mScale);
        lp = new LayoutParams(textViewWidth, textViewHeight);
        lp.addRule(ALIGN_PARENT_LEFT);
        addView(typeName, lp);
        setPaint();
        b = selected.copy(Config.ARGB_4444, true);
        Canvas cv = new Canvas(b);
        /**
         * 修复drawText在不同分辨率设备中不能对齐的bug。
         * <p>
         * 需要说明的是 当设置了digitSelected.setTextAlign = Paint.Align.CENTER之后
         * <p>
         * drawText 中x，y参数表示的意思为：
         * <p>
         * x: 所要绘制的字符串的<b>中心</b>的x坐标
         * <p>
         * y: 所要绘制的字符串的<b>基线(底边)</b>的y坐标
         */
        if (markLongToDraw != null && markLongToDraw.length > 1) {
            selecting = markLongToDraw[markLongToDraw.length / 2];
        }
        Rect r = new Rect();
        digitSelected.getTextBounds(selecting + "", 0, (selecting + "").length(), r);
        cv.drawText(selecting + "", b.getWidth() / 2, (b.getHeight() + r.bottom - r.top) / 2,
                digitSelected);
    }

    /*--设置画B--*/
    private void setPaint() {
        digitBig = new Paint();
        digitBig.setColor(0xFF848484);
        digitBig.setAntiAlias(true);
        digitBig.setStyle(Paint.Style.FILL_AND_STROKE);
        digitBig.setStrokeWidth(0.5f);
        digitBig.setTextAlign(Paint.Align.CENTER);
        digitBig.setTextSize(17);
        digitBig.setStrokeCap(Paint.Cap.ROUND);
        digitSmall = new Paint();
        digitSmall.setColor(0xFF848484);
        digitSmall.setAntiAlias(true);
        digitSmall.setStyle(Paint.Style.FILL_AND_STROKE);
        digitSmall.setStrokeWidth(0.3f);
        digitSmall.setTextAlign(Paint.Align.CENTER);
        digitSmall.setTextSize(8);
        digitSmall.setStrokeCap(Paint.Cap.ROUND);
        digitSelected = new Paint();
        digitSelected.setColor(0xFFFDFDFD);
        digitSelected.setAntiAlias(true);
        digitSelected.setStyle(Paint.Style.FILL_AND_STROKE);
        digitSelected.setStrokeWidth(0.5f);
        /**
         * 是否设置TextAlign属性对字符串定位有很大的影响
         */
        digitSelected.setTextAlign(Paint.Align.CENTER);
        digitSelected.setTextSize(16);
        digitSelected.setStrokeCap(Paint.Cap.ROUND);
        markLine = new Paint();
        markLine.setColor(0xFFB4B4B4);
        markLine.setAntiAlias(true);
        markLine.setStyle(Paint.Style.FILL_AND_STROKE);
        markLine.setStrokeWidth(1.7f);
        markLine.setStrokeCap(Paint.Cap.ROUND);
    }

    private float touchX;// 触点の横坐标

    private int viewWidth;// 自己の宽度

    private int viewheight;// 自己の高度

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {// 为了加入对齐手指动画,记录下上次的坐标
            beginTouchX = touchX;
        }
        touchX = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                onDigitConfirm(this, selecting);// 看好了,这里不加break~~
            case MotionEvent.ACTION_CANCEL:
                selecter = BitmapFactory.decodeResource(getResources(), R.drawable.btn_time_choose);
                stopPopupRemind();
                alignMark();
                break;
            case MotionEvent.ACTION_DOWN:
                startPopupRemind();
                selecter = BitmapFactory.decodeResource(getResources(),
                        R.drawable.btn_time_choose_on);
                // alignTouchX();//还是不加这功能了,对付APM500+的神对手无力回天了(暴力测试+~>_<~+)
                break;
        }
        if (isAlignTouchX) {// 为了防止神一般意识的点击操作导致圈圈数字闪动...TouchX必须矜持啊~~
            touchX = (1 - (float)(animationStopTime - SystemClock.uptimeMillis()) / 300)
                    * (finalTouchX - beginTouchX) + beginTouchX;
        } else {
            invalidate();
        }
        return true;
    }

    private long animationStopTime = -1;// 计算动画结束时间

    private static final int MSG_ALIGNMARK = 520;

    private static final int MSG_ALIGNTOUCHX = 1314;

    private boolean isAlignTouchX;// AlignTouchX优先执行,防止两个对齐同时进行

    private float finalTouchX;// 最后的手指位置

    private float beginTouchX;// 手指点击前圈圈数字的位置

    /*--让圈圈与刻度对齐--*/
    private void alignMark() {
        if (isAlignTouchX) {
            animationHandler.sendMessageDelayed(animationHandler.obtainMessage(MSG_ALIGNMARK),
                    ANIMATION_FRAME_DURATION);
            return;
        }
        if (animationStopTime == -1) {
            finalTouchX = touchX;
            animationStopTime = SystemClock.uptimeMillis() + 150;// 对齐动画0.15s
            animationHandler.sendMessage(animationHandler.obtainMessage(MSG_ALIGNMARK));
        } else if (SystemClock.uptimeMillis() < animationStopTime) {
            touchX = ((1 - (float)(animationStopTime - SystemClock.uptimeMillis()) / 300) * (markCollectionAll[location]
                    + textViewWidth + markPaddingLeft - finalTouchX))
                    + finalTouchX;
            animationHandler.sendMessageDelayed(animationHandler.obtainMessage(MSG_ALIGNMARK),
                    ANIMATION_FRAME_DURATION);
            invalidate();
        } else {
            touchX = markCollectionAll[location] + textViewWidth + markPaddingLeft;
            animationStopTime = -1;
            invalidate();
        }
    }

    /*--让圈圈与手指对齐--*/
    private void alignTouchX() {
        if (animationStopTime == -1) {
            isAlignTouchX = true;
            finalTouchX = touchX;
            touchX = beginTouchX;
            animationStopTime = SystemClock.uptimeMillis() + 200;// 对齐动画0.2s
            animationHandler.sendMessage(animationHandler.obtainMessage(MSG_ALIGNTOUCHX));
        } else if (SystemClock.uptimeMillis() < animationStopTime) {
            touchX = (1 - (float)(animationStopTime - SystemClock.uptimeMillis()) / 300)
                    * (finalTouchX - beginTouchX) + beginTouchX;
            animationHandler.sendMessageDelayed(animationHandler.obtainMessage(MSG_ALIGNTOUCHX),
                    ANIMATION_FRAME_DURATION);
            invalidate();
        } else {
            animationStopTime = -1;
            isAlignTouchX = false;
            touchX = finalTouchX;
            invalidate();
        }
    }

    /*--绘画线程--*/
    private Handler animationHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_ALIGNMARK:
                    alignMark();
                    break;
                case MSG_ALIGNTOUCHX:
                    alignTouchX();
                    break;
            }
        };
    };

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        try {
            drawMark(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        validTouchX();
        canvas.drawBitmap(selecter, touchX - selecterWidth / 2, (viewheight - selecterHeight) / 2,
                null);// 学过点点数学的淫应该知道怎么让圈圈为正手指位置吧
        drawDigit(canvas);
        drawSelected(canvas);
    }

    private static int pixelCorrect = 3;// 用于纠正像素,否则画第一条刻度会只有一半~

    /*--绘制刻度--*/
    private void drawMark(Canvas canvas) throws Exception {
        lengthOfMark = viewheight / 4;
        if (markBg == null) {
            markBg = Bitmap.createBitmap(viewWidth - textViewWidth - markPaddingLeft
                    - markPaddingRight, lengthOfMark, Config.ARGB_4444);
            Canvas cv = new Canvas(markBg);
            int s = 0, l = 0;
            float space = (float)markBg.getWidth()
                    / (markShortToDraw.length + markLongToDraw.length);
            // 以下将两数组统计到绘制刻度的信息
            for (int i = 0; i < markShortToDraw.length + markLongToDraw.length; i++) {
                if (markShortToDraw[s] < markLongToDraw[l]) {
                    if (s < markShortToDraw.length - 1) {
                        markCollectionShort[s] = i * space + pixelCorrect;
                        digitCollectionAll[i] = markShortToDraw[s];
                        markCollectionAll[i] = i * space + pixelCorrect;
                        s++;
                    } else if (markCollectionShort[s] == 0) {
                        markCollectionShort[s] = i * space + pixelCorrect;
                        digitCollectionAll[i] = markShortToDraw[s];
                        markCollectionAll[i] = i * space + pixelCorrect;
                    } else {
                        markCollectionLong[l] = i * space + pixelCorrect;
                        digitCollectionAll[i] = markLongToDraw[l];
                        markCollectionAll[i] = i * space + pixelCorrect;
                        l++;
                    }
                } else if (markShortToDraw[s] > markLongToDraw[l]) {
                    if (l < markLongToDraw.length - 1) {
                        markCollectionLong[l] = i * space + pixelCorrect;
                        digitCollectionAll[i] = markLongToDraw[l];
                        markCollectionAll[i] = i * space + pixelCorrect;
                        l++;
                    } else if (markCollectionLong[l] == 0) {
                        markCollectionLong[l] = i * space + pixelCorrect;
                        digitCollectionAll[i] = markLongToDraw[l];
                        markCollectionAll[i] = i * space + pixelCorrect;
                    } else {
                        markCollectionShort[s] = i * space + pixelCorrect;
                        digitCollectionAll[i] = markShortToDraw[s];
                        markCollectionAll[i] = i * space + pixelCorrect;
                        s++;
                    }
                } else {
                    throw new Exception("Contain same value");
                }
            }
            // 看到上面的算法我很蛋疼啊,为什么当初设计是大小刻度分开为两个数组,而不是所有刻度与大刻度...囧rz
            for (float element : markCollectionShort) {
                cv.drawLine(element, lengthOfMark / 2, element, markBg.getHeight(), markLine);
            }
            for (float element : markCollectionLong) {
                cv.drawLine(element, 0, element, markBg.getHeight(), markLine);
            }

            // 首次绘制
            if (isFirstDraw) {
                if (mFirstLocation >= 0) {
                    touchX = markCollectionAll[mFirstLocation] + textViewWidth + markPaddingLeft;
                }
                isFirstDraw = false;
            }

        }
        canvas.drawBitmap(markBg, textViewWidth + markPaddingLeft, viewheight - lengthOfMark, null);
    }

    /*--画刻度上面的数字--*/
    private void drawDigit(Canvas canvas) {
        for (int i = 0; i < markCollectionLong.length; i++) {
            canvas.drawText(markLongToDraw[i] + "", textViewWidth + markPaddingLeft
                    + markCollectionLong[i], (viewheight + digitBig.getTextSize() / 2) / 2,
                    digitBig);
        }
        if (isDrawShortDigit) {
            for (int i = 0; i < markCollectionShort.length; i++) {
                canvas.drawText(markShortToDraw[i] + "", textViewWidth + markPaddingLeft
                        + markCollectionShort[i], (viewheight + digitSmall.getTextSize() / 2) / 2,
                        digitSmall);
            }
        }
    }

    private Bitmap b;// 临时用的,生成圈圈数字

    /*--画选中的那个数字--*/
    private void drawSelected(Canvas canvas) {
        float tx = touchX - textViewWidth - markPaddingLeft;
        float m = Math.max(markCollectionShort[markCollectionShort.length - 1],
                markCollectionLong[markCollectionLong.length - 1]);
        float scale = tx / m * (digitCollectionAll.length - 1);
        if (selecting != digitCollectionAll[(int)Math.rint(scale)]) {
            location = (int)Math.rint(scale);
            selecting = digitCollectionAll[location];
            onDigitChange(this, selecting, location, digitCollectionAll);
            if (remindView != null) {
                remindView.setText(selecting + "");
            }
            b = selected.copy(Config.ARGB_4444, true);
            Canvas cv = new Canvas(b);
            /**
             * 修复drawText在不同分辨率设备中不能对其的bug。
             * <p>
             * 需要说明的是 当设置了digitSelected.setTextAlign = Paint.Align.CENTER之后
             * <p>
             * drawText 中x，y参数表示的意思为：
             * <p>
             * x: 所要绘制的字符串的<b>中心</b>的x坐标
             * <p>
             * y: 所要绘制的字符串的<b>基线(底边)</b>的y坐标
             */
            Rect r = new Rect();
            digitSelected.getTextBounds(selecting + "", 0, (selecting + "").length(), r);
            cv.drawText(selecting + "", b.getWidth() / 2, (b.getHeight() + r.bottom - r.top) / 2,
                    digitSelected);
        }
        if (remindView != null) {
            dragView();
        }
        canvas.drawBitmap(b, touchX - selectedWidth / 2, (viewheight - selectedHeight) / 2, null);// 学过点点数学的淫应该知道怎么让圈圈为正手指位置吧
    }

    /*--计算有效的手指位置(刻度最左最右)--*/
    private void validTouchX() {
        touchX -= textViewWidth + markPaddingLeft;
        if (touchX < Math.min(markCollectionShort[0], markCollectionLong[0])) {
            touchX = Math.min(markCollectionShort[0], markCollectionLong[0]);
        }
        if (touchX > Math.max(markCollectionShort[markCollectionShort.length - 1],
                markCollectionLong[markCollectionLong.length - 1])) {
            touchX = Math.max(markCollectionShort[markCollectionShort.length - 1],
                    markCollectionLong[markCollectionLong.length - 1]);
        }
        touchX += textViewWidth + markPaddingLeft;
    }

    private WindowManager.LayoutParams mWindowParams;

    private WindowManager mWindowManager;

    private TextView remindView;

    private int viewTop;// 控件位置Top坐标

    /*--弹出气泡提示--*/
    private void startPopupRemind() {
        if (!isPopupRemind || isAlignTouchX) {// isAlignTouchX在这里也作为判断不想它乱闪
            return;
        }

        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
        mWindowParams.y = viewTop - selectedHeight;

        // 透明度
        mWindowParams.dimAmount = 0.3F;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;

        TextView tv = new TextView(getContext());
        tv.setBackgroundResource(R.drawable.btn_time_choose_pop);
        tv.setGravity(Gravity.CENTER);
        tv.setText(selecting + "");
        tv.setTextColor(0xFFFFFFFF);
        tv.setTextSize(23);
        tv.setPadding(0, 0, 0, 16);
        mWindowManager = (WindowManager)getContext().getSystemService("window");
        mWindowManager.addView(tv, mWindowParams);
        remindView = tv;
    }

    /*--关闭气泡提示--*/
    @SuppressWarnings("deprecation")
    private void stopPopupRemind() {
        if (remindView != null) {
            WindowManager wm = (WindowManager)getContext().getSystemService("window");
            wm.removeView(remindView);
            remindView.setBackgroundDrawable(null);
            remindView = null;
        }
    }

    /*--移动气泡提示--*/
    private void dragView() {
        mWindowParams.x = (int)Math.rint(touchX) - remindView.getWidth() / 2;
        mWindowManager.updateViewLayout(remindView, mWindowParams);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        viewWidth = r - l;
        viewheight = b - t;
        viewTop = t;
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 刻度是否显示小数字
     */
    public boolean isDrawShortDigit() {
        return isDrawShortDigit;
    }

    /**
     * 刻度是否显示小数字
     */
    public void setDrawShortDigit(boolean isDrawShortDigit) {
        this.isDrawShortDigit = isDrawShortDigit;
        invalidate();
    }

    /**
     * 刻度数字(大)画笔
     */
    public void setDigitBig(Paint digitBig) {
        this.digitBig = digitBig;
    }

    /**
     * 刻度数字(小)画笔
     */
    public void setDigitSmall(Paint digitSmall) {
        this.digitSmall = digitSmall;
    }

    /**
     * 刻度画笔
     */
    public void setMarkLine(Paint markLine) {
        this.markLine = markLine;
    }

    /**
     * 刻度数字(大)画笔
     */
    public Paint getDigitBig() {
        return digitBig;
    }

    /**
     * 刻度数字(小)画笔
     */
    public Paint getDigitSmall() {
        return digitSmall;
    }

    /**
     * 刻度数字选中
     */
    public Paint getDigitSelected() {
        return digitSelected;
    }

    /**
     * 刻度数字选中
     */
    public void setDigitSelected(Paint digitSelected) {
        this.digitSelected = digitSelected;
    }

    /**
     * 刻度画笔
     */
    public Paint getMarkLine() {
        return markLine;
    }

    /**
     * 左侧显示的文字
     */
    public void setTextView(CharSequence name) {
        typeName.setText(name);
    }

    /**
     * 左侧显示的文字
     */
    public void setTextView(int resId) {
        typeName.setText(resId);
    }

    /**
     * 左侧显示文字的控件
     */
    public TextView getTextView() {
        return typeName;
    }

    private boolean isFirstDraw = true;

    private int mFirstLocation = 0;

    /**
     * 设置选中的刻度位置
     */
    public void setSeleted(int location) {
        mFirstLocation = location < 0 ? 0
                : location >= markCollectionAll.length ? markCollectionAll.length - 1 : location;
    }

    /**
     * 是否使用气泡提醒
     */
    public void enablePopupRemind(boolean enable) {
        isPopupRemind = enable;
    }

    /**
     * 是否使用气泡提醒
     */
    public boolean isPopupRemind() {
        return isPopupRemind;
    }

    /**
     * 默认有显示小时,分钟两种
     */
    public void changeDefaultType(int type) {
        if (type == currentType) {
            return;
        }
        if (type == TYPE_HOUR) {
            markShortToDraw = markShortToDraw_HOUR;
            markCollectionShort = new float[markShortToDraw.length];
            markLongToDraw = markLongToDraw_HOUR;
            markCollectionLong = new float[markLongToDraw.length];
            digitCollectionAll = new int[markLongToDraw.length + markShortToDraw.length];
            markCollectionAll = new float[markLongToDraw.length + markShortToDraw.length];
            typeName.setText("小时");
            currentType = TYPE_HOUR;
        } else if (type == TYPE_MINUTE) {
            markShortToDraw = markShortToDraw_MINUTE;
            markCollectionShort = new float[markShortToDraw.length];
            markLongToDraw = markLongToDraw_MINUTE;
            markCollectionLong = new float[markLongToDraw.length];
            digitCollectionAll = new int[markLongToDraw.length + markShortToDraw.length];
            markCollectionAll = new float[markLongToDraw.length + markShortToDraw.length];
            typeName.setText("分钟");
            currentType = TYPE_MINUTE;
        }
        markBg = null;
        invalidate();
    }

    /**
     * -1表示自定义,另外还有TYPE_HOUR和TYPE_MINUTE免费提供
     */
    public int getCurrentType() {
        return currentType;
    }

    /**
     * 使用您定义的刻度集合(自动排序)
     */
    public void defineMark(int[] markShort, int[] markLong) {
        markShortToDraw = markShort;
        Arrays.sort(markShortToDraw);
        markCollectionShort = new float[markShortToDraw.length];
        markLongToDraw = markLong;
        Arrays.sort(markLongToDraw);
        markCollectionLong = new float[markLongToDraw.length];
        digitCollectionAll = new int[markLongToDraw.length + markShortToDraw.length];
        markCollectionAll = new float[markLongToDraw.length + markShortToDraw.length];
        currentType = -1;// -1表示自定义刻度
        markBg = null;
        invalidate();
    }

    /**
     * 获取当前选中的值
     */
    public int getCurrentSeleted() {
        return selecting;
    }

    /**
     * 获取当前位置
     */
    public int getCurrentLocation() {
        return location;
    }

    /**
     * 获取所有刻度
     */
    public int[] getDigitAll() {
        return digitCollectionAll;
    }

    public interface OnDigitSelectListener {
        /** 用户在纠结地滚啊滚... */
        public void onDigitChange(TimeDragPicker v, int select, int index, int[] collection);

        /** 用户抬起手来,就确定啦~~ */
        public void onDigitConfirm(TimeDragPicker v, int select);
    }

    private OnDigitSelectListener listener;

    private float mScale;

    /**
     * 这个你应该懂得
     */
    public void setOnDigitSelectListener(OnDigitSelectListener listener) {
        this.listener = listener;
    }

    private void onDigitChange(TimeDragPicker v, int select, int index, int[] collection) {
        if (listener != null) {
            listener.onDigitChange(v, select, index, collection);
        }
    }

    private void onDigitConfirm(TimeDragPicker v, int select) {
        if (listener != null) {
            listener.onDigitConfirm(v, select);
        }
    }
}
