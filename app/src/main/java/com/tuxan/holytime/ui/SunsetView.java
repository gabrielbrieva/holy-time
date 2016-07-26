package com.tuxan.holytime.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tuxan.holytime.R;

public class SunsetView extends View {

    Paint mPaint;

    int horizonColor;
    int timelineColor;
    int nightColor;
    int dayColor;
    int sunColor;
    int moonColor;
    int textColor;
    boolean showText;

    public SunsetView(Context context) {
        super(context);
        init(context);
    }

    public SunsetView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SunsetView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SunsetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SunsetView);

        try {
            horizonColor = typedArray.getColor(R.styleable.SunsetView_horizonColor, 0xff000000);
            timelineColor = typedArray.getColor(R.styleable.SunsetView_timelineColor, 0xff000000);
            nightColor = typedArray.getColor(R.styleable.SunsetView_nightColor, 0xff0000ff);
            dayColor = typedArray.getColor(R.styleable.SunsetView_dayColor, 0xffaaaa00);
            sunColor = typedArray.getColor(R.styleable.SunsetView_sunColor, 0xffffff00);
            moonColor = typedArray.getColor(R.styleable.SunsetView_moonColor, 0xff3333ff);
            textColor = typedArray.getColor(R.styleable.SunsetView_textColor, 0xff000000);
            showText = typedArray.getBoolean(R.styleable.SunsetView_showText, true);
        } finally {
            typedArray.recycle();
        }

        init(context);
    }

    private void init(Context context) {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(3);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        Log.d("SunsetView", "canvas width: " + canvas.getWidth());

        float current = 0.5f;

        Path curvePath = new Path();

        mPaint.setColor(0xff000000);

        curvePath.moveTo(0, height);
        curvePath.cubicTo(width * 0.25f, height, width * 0.25f, height * 0.1f, width * 0.5f, height * 0.1f);
        curvePath.cubicTo(width * 0.75f, height * 0.1f, width * 0.75f, height, width, height);

        Path nightPath = new Path(curvePath);
        nightPath.setLastPoint(width, height);
        nightPath.lineTo(width, 0);
        nightPath.lineTo(0, 0);
        nightPath.close();

        mPaint.setStyle(Paint.Style.FILL);

        mPaint.setColor(nightColor);
        canvas.clipRect(new RectF(0, height * 0.75f, width, height));
        canvas.drawPath(nightPath, mPaint);

        mPaint.setColor(dayColor);
        canvas.clipRect(0, 0, width, height, Region.Op.REPLACE);
        canvas.clipRect(new RectF(0, 0, width, height * 0.75f));
        canvas.drawPath(curvePath, mPaint);

        canvas.clipRect(0, 0, width, height, Region.Op.REPLACE);
        mPaint.setStyle(Paint.Style.STROKE);

        mPaint.setColor(timelineColor);
        canvas.drawPath(curvePath, mPaint);

        mPaint.setColor(horizonColor);
        canvas.drawLine(0, height * 0.75f, width, height * 0.75f, mPaint);

        canvas.drawLine(width * 0.25f, 0, width * 0.25f, height, mPaint);
    }
}
