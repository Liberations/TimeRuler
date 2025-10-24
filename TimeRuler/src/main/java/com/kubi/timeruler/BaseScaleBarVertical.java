package com.kubi.timeruler;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Scroller;


import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import com.kubi.timeruler.utils.SizeUtils;

import java.util.Calendar;

public class BaseScaleBarVertical extends View implements ScaleGestureDetector.OnScaleGestureListener,
        GestureDetector.OnGestureListener {
    private static final String TAG = "BaseScaleBarVertical";
    /* 缩放比例*/
    private float mScaleRatio = 1.0f;
    /* 关键刻度宽度*/
    private float mKeyTickWidth;
    /* 刻度宽度*/
    private float mTickWidth;
    /* 普通刻度线与关键刻度线的比*/
    private float mNormalTickAndKeyTickRatio;
    /* 刻度间距*/
    private float mTickSpacing;
    private ScaleGestureDetector mScaleGestureDetector;
    private Paint mScalePaint;
    private ScaleMode mScaleInfo;
    private float unitPixel;
    //
    private long minScreenSpanValue;
    private long maxScreenSpanValue;
    private long mCursorValue;
    private GestureDetectorCompat mGestureDetectorCompat;
    private boolean scrollHappened;
    private float mCursorPosition;
    private float mCursorPositionProportion = 0.5f;
    // 刻度尺纵线位置
    private float mBaselinePosition;
    private float mBaselinePositionProportion = 0.67f;
    private float maxUnitPixel;
    private float minUnitPixel;
    private int tickColor;
    private boolean tickDirectionLeft;//刻度线方向
    private final int cursorLineColor;
    private boolean showCursorLine;
    private boolean showTickValue;
    private boolean showTickLine;
    private Scroller mScroller;
    /**
     * 状态
     */
    private int status = STATUS_NONE;
    /**
     * 默认状态
     */
    public static final int STATUS_NONE = 0;

    /**
     * 按下
     */
    public static final int STATUS_DOWN = 1;
    /**
     * 拖拽滚动
     */
    public static final int STATUS_SCROLL = STATUS_DOWN + 1;
    /**
     * 甩动滚动(惯性)
     */
    public static final int STATUS_SCROLL_FLING = STATUS_SCROLL + 1;
    /**
     * 缩放
     */
    public static final int STATUS_ZOOM = STATUS_SCROLL_FLING + 1;
    /* 刻度值 最大规格*/
    private float maxScaleValueSize;
    /*文字偏移量*/
    private float tickValueOffset;

    public BaseScaleBarVertical(Context context) {
        this(context, null);
    }

    public BaseScaleBarVertical(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseScaleBarVertical);
        mKeyTickWidth = typedArray.getDimension(R.styleable.BaseScaleBarVertical_keyTickWidthVertical, SizeUtils.dp2px(getContext(), 10));
        tickValueOffset = typedArray.getDimension(R.styleable.BaseScaleBarVertical_tickValueOffsetVertical, SizeUtils.dp2px(getContext(), -30));
        tickDirectionLeft = typedArray.getBoolean(R.styleable.BaseScaleBarVertical_tickDirectionLeftVertical, true);
        mNormalTickAndKeyTickRatio = typedArray.getFloat(R.styleable.BaseScaleBarVertical_normalTickRatioVertical, 0.67f);

        tickColor = typedArray.getColor(R.styleable.BaseScaleBarVertical_tickColorVertical, Color.BLACK);
        cursorLineColor = typedArray.getColor(R.styleable.BaseScaleBarVertical_cursorLineColorVertical, Color.YELLOW);
        showCursorLine = typedArray.getBoolean(R.styleable.BaseScaleBarVertical_showCursorLineVertical, true);
        showTickValue = typedArray.getBoolean(R.styleable.BaseScaleBarVertical_showTickValueVertical, true);
        showTickLine = typedArray.getBoolean(R.styleable.BaseScaleBarVertical_showTickLineVertical, true);
        maxScaleValueSize = typedArray.getDimension(R.styleable.BaseScaleBarVertical_maxScaleValueSizeVertical, SizeUtils.sp2px(getContext(), 15));

        float position = typedArray.getFloat(R.styleable.BaseScaleBarVertical_cursorPositionVertical, 0.5f);
        if (position != 0) {
            mCursorPositionProportion = position;
        }
        position = typedArray.getFloat(R.styleable.BaseScaleBarVertical_baselinePositionVertical, 0.67f);
        if (position != 0) {
            mBaselinePositionProportion = position;
        }
        // 释放
        typedArray.recycle();

        init();
    }

    private void init() {
        mScalePaint = new Paint();
        mScalePaint.setAntiAlias(true);
        mScalePaint.setStrokeWidth(1.0f);
        mScalePaint.setDither(true);
        mScalePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        minScreenSpanValue = 30 * 60 * 1000;
        maxScreenSpanValue = 24 * 60 * 60 * 1000;
        mScaleInfo = new ScaleMode();
        mScaleInfo.unitValue = 60000;
        mScaleInfo.keyScaleRange = 5 * 60 * 1000;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        mScaleInfo.startValue = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        mScaleInfo.endValue = calendar.getTimeInMillis();
        mCursorValue = mScaleInfo.startValue;
    }

    protected void setScaleRatio(@FloatRange(from = 0.0f, to = 1.0f, fromInclusive = false) float scaleRatio) {
        this.mScaleRatio = scaleRatio;
    }

    protected long getMaxScreenSpanValue() {
        return maxScreenSpanValue;
    }

    protected long getMinScreenSpanValue() {
        return minScreenSpanValue;
    }

    protected float getKeyTickWidth() {
        return mKeyTickWidth;
    }

    protected float getCursorPosition() {
        return mCursorPosition;
    }

    /**
     * 获取当前游标值
     * @return 游标时间值（毫秒时间戳）
     */
    public long getCursorValue() {
        return mCursorValue;
    }

    protected float getUnitPixel() {
        return unitPixel;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mTickWidth = mKeyTickWidth * mNormalTickAndKeyTickRatio;
        mCursorPosition = h * mCursorPositionProportion;
        mBaselinePosition = w * mBaselinePositionProportion;

        maxUnitPixel = h * 1.0f / minScreenSpanValue;
        minUnitPixel = h * 1.0f / maxScreenSpanValue;

        unitPixel = maxUnitPixel * mScaleRatio;
        mTickSpacing = mScaleInfo.unitValue * unitPixel;
    }

    /**
     * 设置时间范围
     * @param start 开始时间（毫秒时间戳）
     * @param end 结束时间（毫秒时间戳）
     */
    public void setRange(long start, long end) {
        if (start >= end) {
            return;
        }
        mScaleInfo.startValue = start;
        mScaleInfo.endValue = end;
        mCursorValue = start;
        invalidate();
    }

    /**
     * 设置游标值
     * @param cursorValue 游标时间值（毫秒时间戳）
     */
    public void setCursorValue(long cursorValue) {
        if (status != STATUS_NONE) {
            return;
        }
        if (cursorValue < mScaleInfo.startValue || cursorValue > mScaleInfo.endValue) {
            return;
        }
        this.mCursorValue = cursorValue;
        if (mOnCursorListener != null) mOnCursorListener.onProgressChanged(mCursorValue, false);
        invalidate();
    }

    protected void updateScaleInfo(long keyScaleRange, long unitValue) {
        mScaleInfo.keyScaleRange = keyScaleRange;
        mScaleInfo.unitValue = unitValue;
    }

    protected float getBaselinePosition() {
        return mBaselinePosition;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getWidthSize(getSuggestedMinimumWidth(), widthMeasureSpec), getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    private int getWidthSize(int size, int widthMeasureSpec) {
        int result = size;
        int contentWidth = calcContentWidth(mBaselinePositionProportion);
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size > contentWidth ? size : contentWidth;
                break;
            case MeasureSpec.AT_MOST:
                result = contentWidth;
                break;
            case MeasureSpec.EXACTLY:
                result = specSize > contentWidth ? specSize : contentWidth;
                break;
        }
        return result;
    }

    protected int calcContentWidth(float baselinePositionProportion) {
        int tickValueWidth = 0;
        if (showTickValue && null != mTickMarkStrategy) {
            mScalePaint.setTextSize(maxScaleValueSize);
            Paint.FontMetrics fontMetrics = mScalePaint.getFontMetrics();
            double ceil = Math.ceil(fontMetrics.bottom - fontMetrics.top);
            tickValueWidth = (int) (ceil + tickValueOffset);
        }
        return (int) ((mKeyTickWidth + tickValueWidth) / baselinePositionProportion + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 保存画布状态
        canvas.save();
        
        // 旋转画布90度，使纵向滚动变为横向绘制
        canvas.rotate(90);
        canvas.translate(0, -getWidth());

        float baselinePosition = getBaselinePosition();
        mScalePaint.setColor(tickColor);
        if (showTickLine) {
            canvas.drawLine(getScrollY(), baselinePosition, getScrollY() + getHeight(), baselinePosition, mScalePaint);
        }
        long leftRange = mCursorValue - mScaleInfo.startValue;
        long leftNeighborOffest = leftRange % mScaleInfo.unitValue;
        long leftNeighborTickValue = mCursorValue - leftNeighborOffest;
        float leftNeighborPosition = mCursorPosition - leftNeighborOffest * unitPixel;
        int leftCount = (int) (mCursorPosition / mTickSpacing + 0.5f);
        float onDrawTickPosition;
        long onDrawTickValue;
        for (int i = 0; i < leftCount; i++) {
            onDrawTickValue = leftNeighborTickValue - mScaleInfo.unitValue * i;
            if (onDrawTickValue < mScaleInfo.startValue) {
                break;
            }
            onDrawTickPosition = leftNeighborPosition - mTickSpacing * i;
            if (tickDirectionLeft) {
                if ((onDrawTickValue - mScaleInfo.startValue) % mScaleInfo.keyScaleRange == 0) {
                    canvas.drawLine(onDrawTickPosition, baselinePosition, onDrawTickPosition, baselinePosition - mKeyTickWidth, mScalePaint);
                    drawTickValue(canvas, onDrawTickPosition, baselinePosition - mKeyTickWidth, onDrawTickValue, true);
                } else {
                    canvas.drawLine(onDrawTickPosition, baselinePosition - mTickWidth, onDrawTickPosition, baselinePosition, mScalePaint);
                    drawTickValue(canvas, onDrawTickPosition, baselinePosition - mTickWidth, onDrawTickValue, false);
                }
            } else {
                if ((onDrawTickValue - mScaleInfo.startValue) % mScaleInfo.keyScaleRange == 0) {
                    canvas.drawLine(onDrawTickPosition, baselinePosition, onDrawTickPosition, baselinePosition + mKeyTickWidth, mScalePaint);
                    drawTickValue(canvas, onDrawTickPosition, baselinePosition - mKeyTickWidth, onDrawTickValue, true);
                } else {
                    canvas.drawLine(onDrawTickPosition, baselinePosition, onDrawTickPosition, baselinePosition + mTickWidth, mScalePaint);
                    drawTickValue(canvas, onDrawTickPosition, baselinePosition - mTickWidth, onDrawTickValue, false);
                }
            }

        }

        long rightNeighborTickValue = leftNeighborTickValue + mScaleInfo.unitValue;
        float rightNeighborPosition = leftNeighborPosition + mTickSpacing;
        int rightCount = (int) ((getHeight() - mCursorPosition) / mTickSpacing + 0.5f);
        for (int i = 0; i < rightCount; i++) {
            onDrawTickValue = rightNeighborTickValue + mScaleInfo.unitValue * i;
            if (onDrawTickValue > mScaleInfo.endValue) {
                break;
            }
            onDrawTickPosition = rightNeighborPosition + mTickSpacing * i;
            if (tickDirectionLeft) {
                if ((onDrawTickValue - mScaleInfo.startValue) % mScaleInfo.keyScaleRange == 0) {
                    canvas.drawLine(onDrawTickPosition, baselinePosition, onDrawTickPosition, baselinePosition - mKeyTickWidth, mScalePaint);
                    drawTickValue(canvas, onDrawTickPosition, baselinePosition - mKeyTickWidth, onDrawTickValue, true);
                } else {
                    canvas.drawLine(onDrawTickPosition, baselinePosition, onDrawTickPosition, baselinePosition - mTickWidth, mScalePaint);
                    drawTickValue(canvas, onDrawTickPosition, baselinePosition - mTickWidth, onDrawTickValue, false);
                }
            } else {
                if ((onDrawTickValue - mScaleInfo.startValue) % mScaleInfo.keyScaleRange == 0) {
                    canvas.drawLine(onDrawTickPosition, baselinePosition, onDrawTickPosition, baselinePosition + mKeyTickWidth, mScalePaint);
                    drawTickValue(canvas, onDrawTickPosition, baselinePosition - mKeyTickWidth, onDrawTickValue, true);
                } else {
                    canvas.drawLine(onDrawTickPosition, baselinePosition, onDrawTickPosition, baselinePosition + mTickWidth, mScalePaint);
                    drawTickValue(canvas, onDrawTickPosition, baselinePosition - mTickWidth, onDrawTickValue, false);
                }
            }

        }

        onEndTickDraw(canvas);

        drawCursor(canvas, mCursorPosition, mCursorValue);
        
        // 恢复画布状态
        canvas.restore();
    }

    protected void onEndTickDraw(Canvas canvas) {
    }

    /**
     * 绘制游标
     *
     * @param canvas
     * @param cursorPosition
     */
    protected void drawCursor(Canvas canvas, float cursorPosition, long cursorValue) {
        if (showCursorLine) {
            mScalePaint.setColor(cursorLineColor);
            canvas.drawLine(0, cursorPosition, getWidth(), cursorPosition, mScalePaint);
        }
    }

    /**
     * 绘制刻度线描述
     *
     * @param canvas
     * @param x
     * @param y
     * @param scaleValue
     * @param keyScale
     */
    private void drawTickValue(Canvas canvas, float x, float y, long scaleValue, boolean keyScale) {
        if (showTickValue) {
            if (null != mTickMarkStrategy) {
                if (mTickMarkStrategy.disPlay(scaleValue, keyScale)) {
                    mScalePaint.setColor(mTickMarkStrategy.getColor(scaleValue, keyScale));
                    mScalePaint.setTextAlign(Paint.Align.CENTER);
                    float size = mTickMarkStrategy.getSize(scaleValue, keyScale, maxScaleValueSize);
                    size = Math.min(maxScaleValueSize, size);
                    mScalePaint.setTextSize(size);
                    canvas.drawText(mTickMarkStrategy.getScaleValue(scaleValue, keyScale), x, y - tickValueOffset, mScalePaint);
                }
            }
        }
    }

    ScaleGestureDetector getScaleGestureDetect() {
        if (null == mScaleGestureDetector) {
            mScaleGestureDetector = new ScaleGestureDetector(getContext(), this);
        }
        return mScaleGestureDetector;
    }

    GestureDetectorCompat getGestureDetectorCompat() {
        if (null == mGestureDetectorCompat) {
            mGestureDetectorCompat = new GestureDetectorCompat(getContext(), this);
        }
        return mGestureDetectorCompat;
    }

    Scroller getScroller() {
        if (null == mScroller) {
            mScroller = new Scroller(getContext());
        }
        return mScroller;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getScaleGestureDetect().onTouchEvent(event);
        getGestureDetectorCompat().onTouchEvent(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                if (scrollHappened && status != STATUS_SCROLL_FLING) {
                    if (null != mOnCursorListener) {
                        mOnCursorListener.onStopTrackingTouch(mCursorValue);
                    }
                }
                scrollHappened = false;
                if (status == STATUS_DOWN
                        || status == STATUS_SCROLL
                        || status == STATUS_ZOOM) {
                    status = STATUS_NONE;
                }
                break;
        }
        return true;
    }

    @Override
    public final boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        Log.d(TAG, "onScalescaleFactor: " + scaleFactor);
        status = STATUS_ZOOM;
        unitPixel *= scaleFactor;
        if (unitPixel > maxUnitPixel) {
            unitPixel = maxUnitPixel;
            scaleFactor = 1.0f;
        } else if (unitPixel < minUnitPixel) {
            unitPixel = minUnitPixel;
            scaleFactor = 1.0f;
        }
        onScale(mScaleInfo, unitPixel);
        mScaleRatio *= scaleFactor;
        mTickSpacing = mScaleInfo.unitValue * unitPixel;
        Log.d(TAG, mScaleRatio + "onScale:mTickSpacing " + mTickSpacing);
        invalidate();
        return unitPixel < maxUnitPixel || unitPixel > minUnitPixel;
    }

    private float lastScale = 0;

    /*0~1000之间*/
    /**
     * 设置缩放比例
     * @param scale 缩放值，范围0-1000
     */
    public void setScale(float scale) {
        float scaleFactor = 0.9f + scale / 10000;
        if (lastScale < scale) {
            scaleFactor = 1 + scale / 10000;
        }
        status = STATUS_ZOOM;
        unitPixel *= scaleFactor;
        if (unitPixel > maxUnitPixel) {
            unitPixel = maxUnitPixel;
            scaleFactor = 1.0f;
        } else if (unitPixel < minUnitPixel) {
            unitPixel = minUnitPixel;
            scaleFactor = 1.0f;
        }
        Log.d(TAG, unitPixel + "onScalescaleFactor: " + scaleFactor);
        if (scale == 1000) {
            scaleFactor = 1;
            unitPixel = (float) (6.0f * Math.pow(10, -4));
        }
        if (scale == 0) {
            scaleFactor = 1;
            unitPixel = (float) (1.25f * Math.pow(10, -5));
        }
        onScale(mScaleInfo, unitPixel);
        mScaleRatio *= scaleFactor;
        mTickSpacing = mScaleInfo.unitValue * unitPixel;
        Log.d(TAG, mScaleRatio + "onScale:mTickSpacing " + mTickSpacing);
        invalidate();
        lastScale = scale;
        status = STATUS_NONE;
    }

    protected void onScale(ScaleMode info, float unitPixel) {
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    @Override
    public final boolean onDown(MotionEvent e) {
        if (status == STATUS_SCROLL_FLING) {
            getScroller().forceFinished(true);
        } else {
            scrollHappened = false;
        }
        status = STATUS_DOWN;
        // 返回出 拦截事件
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // do nothing
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // do nothing
        return false;
    }

    @Override
    public final boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (e2.getPointerCount() > 1) {
            return false;
        }
        if (getScaleGestureDetect().isInProgress()) {
            return false;
        }
        // TODO: 处理第一次触发滚动产生的距离过大,呈现滚动突兀不友好体验问题 ------ 待优化
        if (!scrollHappened) {
            scrollHappened = true;
            if (null != mOnCursorListener) {
                mOnCursorListener.onStartTrackingTouch(mCursorValue);
            }
            return true;
        }
        status = STATUS_SCROLL;
        // 游标刻度值增量 - 纵向滚动使用distanceY
        long courseIncrement = (long) (distanceY / unitPixel);
        mCursorValue += courseIncrement;
        boolean result = true;
        if (mCursorValue < mScaleInfo.startValue) {
            mCursorValue = mScaleInfo.startValue;
            result = false;
        } else if (mCursorValue > mScaleInfo.endValue) {
            mCursorValue = mScaleInfo.endValue;
            result = false;
        }
        if (null != mOnCursorListener) {
            mOnCursorListener.onProgressChanged(mCursorValue, true);
        }
        invalidate();
        return result;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // do nothing
    }

    @Override
    public void computeScroll() {
        if (getScroller().computeScrollOffset()) {
            int currY = getScroller().getCurrY();
            mCursorValue = mScaleInfo.startValue + (long) (currY / unitPixel);
            if (mCursorValue < mScaleInfo.startValue) {
                mCursorValue = mScaleInfo.startValue;
            } else if (mCursorValue > mScaleInfo.endValue) {
                mCursorValue = mScaleInfo.endValue;
            }
            invalidate();
        } else {
            if (status == STATUS_SCROLL_FLING) {
                status = STATUS_NONE;
                if (null != mOnCursorListener) {
                    mOnCursorListener.onStopTrackingTouch(mCursorValue);
                }
            }
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        status = STATUS_SCROLL_FLING;
        int startY = (int) ((mCursorValue - mScaleInfo.startValue) * unitPixel);
        int maY = (int) ((mScaleInfo.endValue - mScaleInfo.startValue) * unitPixel);
        getScroller().fling(0,
                startY,
                0,
                -(int) velocityY,
                0,
                0,
                0,
                maY);
        invalidate();
        return true;
    }

    /**
     * 设置刻度线方向
     * @param isLeft true向左延伸，false向右延伸
     */
    public void setTickDirection(boolean isLeft) {
        tickDirectionLeft = isLeft;
        invalidate();
    }

    protected class ScaleMode {
        public long unitValue;
        public long startValue;
        public long endValue;
        public long keyScaleRange;
    }

    private OnCursorListener mOnCursorListener;

    /**
     * 设置游标监听器
     * @param l 游标监听器
     */
    public void setOnCursorListener(OnCursorListener l) {
        this.mOnCursorListener = l;
    }

    public interface OnCursorListener {
        void onStartTrackingTouch(long cursorValue);

        void onProgressChanged(long cursorValue, boolean isFromUser);

        void onStopTrackingTouch(long cursorValue);
    }

    private TickMarkStrategy mTickMarkStrategy;

    /**
     * 设置刻度标记策略
     * @param tickMarkStrategy 刻度标记策略
     */
    public void setTickMarkStrategy(TickMarkStrategy tickMarkStrategy) {
        this.mTickMarkStrategy = tickMarkStrategy;
    }

    public interface TickMarkStrategy {
        /**
         * 是否显示刻度值
         *
         * @param scaleValue 刻度值
         * @param keyScale   是否是关键刻度
         * @return
         */
        boolean disPlay(long scaleValue, boolean keyScale);

        /**
         * 获取显示的刻度值
         *
         * @param scaleValue
         * @param keyScale
         * @return
         */
        @NonNull
        String getScaleValue(long scaleValue, boolean keyScale);

        /**
         * 获取当前刻度值显示颜色
         *
         * @param scaleValue
         * @param keyScale
         * @return
         */
        @ColorInt
        int getColor(long scaleValue, boolean keyScale);

        /**
         * 获取当前刻度值显示大小
         *
         * @param scaleValue
         * @param keyScale
         * @param maxScaleValueSize
         * @return
         */
        @Dimension
        float getSize(long scaleValue, boolean keyScale, float maxScaleValueSize);
    }

    public static int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
