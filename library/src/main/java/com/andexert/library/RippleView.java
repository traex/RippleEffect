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
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

/**
 * Author :    Chutaux Robin
 * Date :      10/8/2014
 */
public class RippleView extends RelativeLayout
{
    private int WIDTH;
    private int HEIGHT;
    private int FRAME_RATE = 10;
    private int DURATION = 400;
    private int PAINT_ALPHA = 90;
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
    private View childView;
    private int ripplePadding;
    private GestureDetector gestureDetector;
    private Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            invalidate();
        }
    };

    public RippleView(Context context)
    {
        super(context);
    }

    public RippleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs)
    {
        if (isInEditMode())
            return;

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleView);
        rippleColor = typedArray.getColor(R.styleable.RippleView_rv_color, getResources().getColor(R.color.rippelColor));
        rippleType = typedArray.getInt(R.styleable.RippleView_rv_type, 0);
        hasToZoom = typedArray.getBoolean(R.styleable.RippleView_rv_zoom, false);
        isCentered = typedArray.getBoolean(R.styleable.RippleView_rv_centered, false);
        DURATION = typedArray.getInteger(R.styleable.RippleView_rv_rippleDuration, DURATION);
        FRAME_RATE = typedArray.getInteger(R.styleable.RippleView_rv_framerate, FRAME_RATE);
        PAINT_ALPHA = typedArray.getInteger(R.styleable.RippleView_rv_alpha, PAINT_ALPHA);
        ripplePadding = typedArray.getDimensionPixelSize(R.styleable.RippleView_rv_ripplePadding, 0);
        canvasHandler = new Handler();
        zoomScale = typedArray.getFloat(R.styleable.RippleView_rv_zoomScale, 1.03f);
        zoomDuration = typedArray.getInt(R.styleable.RippleView_rv_zoomDuration, 200);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(rippleColor);
        paint.setAlpha(PAINT_ALPHA);
        this.setWillNotDraw(false);

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e)
            {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e)
            {
                return true;
            }
        });

        this.setDrawingCacheEnabled(true);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params)
    {
        childView = child;
        super.addView(child, index, params);
    }

    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);
        if (animationRunning)
        {
            if (DURATION <= timer * FRAME_RATE)
            {
                animationRunning = false;
                timer = 0;
                durationEmpty = -1;
                timerEmpty = 0;
                canvas.restore();
                invalidate();
                return;
            }
            else
                canvasHandler.postDelayed(runnable, FRAME_RATE);

            if (timer == 0)
                canvas.save();


            canvas.drawCircle(x, y, (radiusMax * (((float) timer * FRAME_RATE) / DURATION)), paint);

            paint.setColor(getResources().getColor(android.R.color.holo_red_light));

            if (rippleType == 1 && originBitmap != null && (((float) timer * FRAME_RATE) / DURATION) > 0.4f)
            {
                if (durationEmpty == -1)
                    durationEmpty = DURATION - timer * FRAME_RATE;

                timerEmpty++;
                final Bitmap tmpBitmap = getCircleBitmap((int) ((radiusMax) * (((float) timerEmpty * FRAME_RATE) / (durationEmpty))));
                canvas.drawBitmap(tmpBitmap, 0, 0, paint);
                tmpBitmap.recycle();
            }

            paint.setColor(rippleColor);

            if (rippleType == 1)
            {
                if ((((float) timer * FRAME_RATE) / DURATION) > 0.6f)
                    paint.setAlpha((int) (PAINT_ALPHA - ((PAINT_ALPHA) * (((float) timerEmpty * FRAME_RATE) / (durationEmpty)))));
                else
                    paint.setAlpha(PAINT_ALPHA);
            }
            else
                paint.setAlpha((int) (PAINT_ALPHA - ((PAINT_ALPHA) * (((float) timer * FRAME_RATE) / DURATION))));

            timer++;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        WIDTH = w;
        HEIGHT = h;

        scaleAnimation = new ScaleAnimation(1.0f, zoomScale, 1.0f, zoomScale, w / 2, h / 2);
        scaleAnimation.setDuration(zoomDuration);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (gestureDetector.onTouchEvent(event) && !animationRunning)
        {
            if (hasToZoom)
                this.startAnimation(scaleAnimation);

            radiusMax = Math.max(WIDTH, HEIGHT);

            if (rippleType != 2)
                radiusMax /= 2;

            radiusMax -= ripplePadding;

            if (isCentered || rippleType == 1)
            {
                this.x = getMeasuredWidth() / 2;
                this.y = getMeasuredHeight() / 2;
            }
            else
            {
                this.x = event.getX();
                this.y = event.getY();
            }

            animationRunning = true;

            if (rippleType == 1 && originBitmap == null)
                originBitmap = getDrawingCache(true);

            invalidate();
            this.performClick();
        }

        childView.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
       return true;
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
}
