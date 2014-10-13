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
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

/**
 * Author :    Chutaux Robin
 * Date :      10/8/2014
 */
public class RippleView extends RelativeLayout
{
    private int WIDTH;
    private int HEIGHT;
    private final int FRAME_RATE = 15;
    private final int DURATION = 500;
    private final int PAINT_ALPHA = 90;
    private Handler canvasHandler;
    private boolean isRounded;
    private float radiusMax = 0;
    private boolean animationRunning = false;
    private int timer = 0;
    private int timerEmpty = 0;
    private int durationEmpty = -1;
    private float x = -1;
    private float y = -1;
    private Animation scaleAnimation;
    private Paint paint;
    private Bitmap originBitmap;
    private int rippleColor;
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
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleView);
        rippleColor = typedArray.getColor(R.styleable.RippleView_color, getResources().getColor(R.color.rippelColor));
        isRounded = typedArray.getBoolean(R.styleable.RippleView_rounded, false);
        canvasHandler = new Handler();

        scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(rippleColor);
        paint.setAlpha(PAINT_ALPHA);
        this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        this.setWillNotDraw(false);


        this.setDrawingCacheEnabled(true);

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

            if (timer == 2  )
                canvas.save();

            canvas.drawCircle(x, y, (radiusMax * (((float) timer * FRAME_RATE) / DURATION)), paint);

            paint.setColor(getResources().getColor(android.R.color.holo_red_light));

            if (originBitmap != null && (((float) timer * FRAME_RATE) / DURATION) > 0.4f)
            {
                if (durationEmpty == -1)
                    durationEmpty = DURATION - timer * FRAME_RATE;

                timerEmpty++;
                final Bitmap tmpBitmap = getCircleBitmap((int) ((radiusMax)* (((float) timerEmpty * FRAME_RATE) / (durationEmpty))));
                canvas.drawBitmap(tmpBitmap, 0, 0, paint);
                tmpBitmap.recycle();
            }

            paint.setColor(rippleColor);

            timer++;

            if ((((float) timer * FRAME_RATE) / DURATION) > 0.6f)
                paint.setAlpha((int) (PAINT_ALPHA - ((PAINT_ALPHA)* (((float) timerEmpty * FRAME_RATE) / (durationEmpty)))));
            else
                paint.setAlpha(PAINT_ALPHA);

        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        WIDTH = MeasureSpec.getSize(widthMeasureSpec);
        HEIGHT = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(WIDTH, HEIGHT);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        if (!animationRunning)
        {
            //this.startAnimation(scaleAnimation);
            radiusMax = Math.max(WIDTH, HEIGHT) / 2f;
            if (isRounded)
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

            originBitmap = getDrawingCache(true);

            invalidate();
        }

        return super.onInterceptTouchEvent(event);
    }

    private Bitmap getCircleBitmap(final int radius) {
        final Bitmap output = Bitmap.createBitmap(originBitmap.getWidth(),
                originBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect((int)(x - radius), (int)(y - radius), (int)(x + radius), (int)(y + radius));

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        //paint.setColor(color);
        canvas.drawCircle(x, y, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(originBitmap, rect, rect, paint);

        return output;
    }
}
