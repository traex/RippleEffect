/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Robin Chutaux
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.andexert.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.RelativeLayout;


/**
 * RippleView custom layout
 *
 * Custom Layout that allows to use Ripple UI pattern above API 21
 *
 * @author Chutaux Robin
 * @version 2015.0512
 */
public class RippleView extends RelativeLayout {

    private int WIDTH;
    private int HEIGHT;
    private int frameRate = 10;
    private int rippleDuration = 400;
    private int rippleAlpha = 90;
    private Handler canvasHandler;
    private float radiusMax = 0;
    private boolean animationRunning = false;
    private int timer = 0;
    private int timerEmpty = 0;
    private int durationEmpty = -1;
    private float x = -1;
    private float y = -1;
    private int zoomDuration;
    private float zoomScale;
    private ScaleAnimation scaleAnimation;
    private Boolean hasToZoom;
    private Boolean isCentered;
    private Integer rippleType;
    private Paint paint;
    private Bitmap originBitmap;
    private int rippleColor;
    private int ripplePadding;
    private GestureDetector gestureDetector;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    private OnRippleCompleteListener onCompletionListener;

    public RippleView(Context context) {
        super(context);
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * Method that initializes all fields and sets listeners
     *
     * @param context Context used to create this view
     * @param attrs Attribute used to initialize fields
     */
    private void init(final Context context, final AttributeSet attrs) {
        if (isInEditMode())
            return;

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleView);
        rippleColor = typedArray.getColor(R.styleable.RippleView_rv_color, getResources().getColor(R.color.rippelColor));
        rippleType = typedArray.getInt(R.styleable.RippleView_rv_type, 0);
        hasToZoom = typedArray.getBoolean(R.styleable.RippleView_rv_zoom, false);
        isCentered = typedArray.getBoolean(R.styleable.RippleView_rv_centered, false);
        rippleDuration = typedArray.getInteger(R.styleable.RippleView_rv_rippleDuration, rippleDuration);
        frameRate = typedArray.getInteger(R.styleable.RippleView_rv_framerate, frameRate);
        rippleAlpha = typedArray.getInteger(R.styleable.RippleView_rv_alpha, rippleAlpha);
        ripplePadding = typedArray.getDimensionPixelSize(R.styleable.RippleView_rv_ripplePadding, 0);
        canvasHandler = new Handler();
        zoomScale = typedArray.getFloat(R.styleable.RippleView_rv_zoomScale, 1.03f);
        zoomDuration = typedArray.getInt(R.styleable.RippleView_rv_zoomDuration, 200);
        typedArray.recycle();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(rippleColor);
        paint.setAlpha(rippleAlpha);
        this.setWillNotDraw(false);

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent event) {
                super.onLongPress(event);
                animateRipple(event);
                sendClickEvent(true);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        this.setDrawingCacheEnabled(true);
        this.setClickable(true);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (animationRunning) {
            canvas.save();
            if (rippleDuration <= timer * frameRate) {
                animationRunning = false;
                timer = 0;
                durationEmpty = -1;
                timerEmpty = 0;
                // There is problem on Android M where canvas.restore() seems to be called automatically
                // For now, don't call canvas.restore() manually on Android M and higher (API 23+)
                if(Build.VERSION.SDK_INT < 23) {
                    canvas.restore();
                }
                invalidate();
                if (onCompletionListener != null) onCompletionListener.onComplete(this);
                return;
            } else
                canvasHandler.postDelayed(runnable, frameRate);

            if (timer == 0)
                canvas.save();


            canvas.drawCircle(x, y, (radiusMax * (((float) timer * frameRate) / rippleDuration)), paint);

            paint.setColor(Color.parseColor("#ffff4444"));

            if (rippleType == 1 && originBitmap != null && (((float) timer * frameRate) / rippleDuration) > 0.4f) {
                if (durationEmpty == -1)
                    durationEmpty = rippleDuration - timer * frameRate;

                timerEmpty++;
                final Bitmap tmpBitmap = getCircleBitmap((int) ((radiusMax) * (((float) timerEmpty * frameRate) / (durationEmpty))));
                canvas.drawBitmap(tmpBitmap, 0, 0, paint);
                tmpBitmap.recycle();
            }

            paint.setColor(rippleColor);

            if (rippleType == 1) {
                if ((((float) timer * frameRate) / rippleDuration) > 0.6f)
                    paint.setAlpha((int) (rippleAlpha - ((rippleAlpha) * (((float) timerEmpty * frameRate) / (durationEmpty)))));
                else
                    paint.setAlpha(rippleAlpha);
            }
            else
                paint.setAlpha((int) (rippleAlpha - ((rippleAlpha) * (((float) timer * frameRate) / rippleDuration))));

            timer++;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        WIDTH = w;
        HEIGHT = h;

        scaleAnimation = new ScaleAnimation(1.0f, zoomScale, 1.0f, zoomScale, w / 2, h / 2);
        scaleAnimation.setDuration(zoomDuration);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(1);
    }

    /**
     * Launch Ripple animation for the current view with a MotionEvent
     *
     * @param event MotionEvent registered by the Ripple gesture listener
     */
    public void animateRipple(MotionEvent event) {
        createAnimation(event.getX(), event.getY());
    }

    /**
     * Launch Ripple animation for the current view centered at x and y position
     *
     * @param x Horizontal position of the ripple center
     * @param y Vertical position of the ripple center
     */
    public void animateRipple(final float x, final float y) {
        createAnimation(x, y);
    }

    /**
     * Create Ripple animation centered at x, y
     *
     * @param x Horizontal position of the ripple center
     * @param y Vertical position of the ripple center
     */
    private void createAnimation(final float x, final float y) {
        if (this.isEnabled() && !animationRunning) {
            if (hasToZoom)
                this.startAnimation(scaleAnimation);

            radiusMax = Math.max(WIDTH, HEIGHT);

            if (rippleType != 2)
                radiusMax /= 2;

            radiusMax -= ripplePadding;

            if (isCentered || rippleType == 1) {
                this.x = getMeasuredWidth() / 2;
                this.y = getMeasuredHeight() / 2;
            } else {
                this.x = x;
                this.y = y;
            }

            animationRunning = true;

            if (rippleType == 1 && originBitmap == null)
                originBitmap = getDrawingCache(true);

            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            animateRipple(event);
            sendClickEvent(false);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        this.onTouchEvent(event);
        return super.onInterceptTouchEvent(event);
    }

    /**
     * Send a click event if parent view is a Listview instance
     *
     * @param isLongClick Is the event a long click ?
     */
    private void sendClickEvent(final Boolean isLongClick) {
        if (getParent() instanceof AdapterView) {
            final AdapterView adapterView = (AdapterView) getParent();
            final int position = adapterView.getPositionForView(this);
            final long id = adapterView.getItemIdAtPosition(position);
            if (isLongClick) {
                if (adapterView.getOnItemLongClickListener() != null)
                    adapterView.getOnItemLongClickListener().onItemLongClick(adapterView, this, position, id);
            } else {
                if (adapterView.getOnItemClickListener() != null)
                    adapterView.getOnItemClickListener().onItemClick(adapterView, this, position, id);
            }
        }
    }

    private Bitmap getCircleBitmap(final int radius) {
        final Bitmap output = Bitmap.createBitmap(originBitmap.getWidth(), originBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect((int)(x - radius), (int)(y - radius), (int)(x + radius), (int)(y + radius));

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(x, y, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(originBitmap, rect, rect, paint);

        return output;
    }

    /**
     * Set Ripple color, default is #FFFFFF
     *
     * @param rippleColor New color resource
     */
    @ColorRes
	public void setRippleColor(int rippleColor) {
		this.rippleColor = getResources().getColor(rippleColor);
	}

	public int getRippleColor() {
		return rippleColor;
	}

    public RippleType getRippleType()
    {
        return RippleType.values()[rippleType];
    }

    /**
     * Set Ripple type, default is RippleType.SIMPLE
     *
     * @param rippleType New Ripple type for next animation
     */
    public void setRippleType(final RippleType rippleType)
    {
        this.rippleType = rippleType.ordinal();
    }

    public Boolean isCentered()
    {
        return isCentered;
    }

    /**
     * Set if ripple animation has to be centered in its parent view or not, default is False
     *
     * @param isCentered
     */
    public void setCentered(final Boolean isCentered)
    {
        this.isCentered = isCentered;
    }

    public int getRipplePadding()
    {
        return ripplePadding;
    }

    /**
     * Set Ripple padding if you want to avoid some graphic glitch
     *
     * @param ripplePadding New Ripple padding in pixel, default is 0px
     */
    public void setRipplePadding(int ripplePadding)
    {
        this.ripplePadding = ripplePadding;
    }

    public Boolean isZooming()
    {
        return hasToZoom;
    }

    /**
     * At the end of Ripple effect, the child views has to zoom
     *
     * @param hasToZoom Do the child views have to zoom ? default is False
     */
    public void setZooming(Boolean hasToZoom)
    {
        this.hasToZoom = hasToZoom;
    }

    public float getZoomScale()
    {
        return zoomScale;
    }

    /**
     * Scale of the end animation
     *
     * @param zoomScale Value of scale animation, default is 1.03f
     */
    public void setZoomScale(float zoomScale)
    {
        this.zoomScale = zoomScale;
    }

    public int getZoomDuration()
    {
        return zoomDuration;
    }

    /**
     * Duration of the ending animation in ms
     *
     * @param zoomDuration Duration, default is 200ms
     */
    public void setZoomDuration(int zoomDuration)
    {
        this.zoomDuration = zoomDuration;
    }

    public int getRippleDuration()
    {
        return rippleDuration;
    }

    /**
     * Duration of the Ripple animation in ms
     *
     * @param rippleDuration Duration, default is 400ms
     */
    public void setRippleDuration(int rippleDuration)
    {
        this.rippleDuration = rippleDuration;
    }

    public int getFrameRate()
    {
        return frameRate;
    }

    /**
     * Set framerate for Ripple animation
     *
     * @param frameRate New framerate value, default is 10
     */
    public void setFrameRate(int frameRate)
    {
        this.frameRate = frameRate;
    }

    public int getRippleAlpha()
    {
        return rippleAlpha;
    }

    /**
     * Set alpha for ripple effect color
     *
     * @param rippleAlpha Alpha value between 0 and 255, default is 90
     */
    public void setRippleAlpha(int rippleAlpha)
    {
        this.rippleAlpha = rippleAlpha;
    }

    public void setOnRippleCompleteListener(OnRippleCompleteListener listener) {
        this.onCompletionListener = listener;
    }

    /**
     * Defines a callback called at the end of the Ripple effect
     */
    public interface OnRippleCompleteListener {
        void onComplete(RippleView rippleView);
    }

    public enum RippleType {
        SIMPLE(0),
        DOUBLE(1),
        RECTANGLE(2);

        int type;

        RippleType(int type)
        {
            this.type = type;
        }
    }
}
