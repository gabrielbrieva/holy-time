package com.tuxan.holytime.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
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
    int taglineColor;
    int nightColor;
    int dayColor;
    int sunColor;

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
            taglineColor = typedArray.getColor(R.styleable.SunsetView_taglineColor, 0xff000000);
            nightColor = typedArray.getColor(R.styleable.SunsetView_nightColor, 0xff000000);
            dayColor = typedArray.getColor(R.styleable.SunsetView_dayColor, 0xff000000);
            sunColor = typedArray.getColor(R.styleable.SunsetView_sunColor, 0xff000000);
        } finally {
            typedArray.recycle();
        }

        init(context);
    }

    private void init(Context context) {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(3);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            setLayerType(LAYER_TYPE_SOFTWARE, mPaint);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        float current = 0.5f;

        Path curvePath = new Path();

        curvePath.moveTo(0, height);

        double segment = (2 * Math.PI) / width;

        for (int x = 0; x <= width; x++) {
            curvePath.lineTo(x, getY(x, segment, (int)(height * 0.9f)));
        }

        Path nightPath = new Path(curvePath);
        nightPath.setLastPoint(width, height);
        nightPath.lineTo(width, 0);
        nightPath.lineTo(0, 0);
        nightPath.close();


        mPaint.setStyle(Paint.Style.FILL);

        mPaint.setColor(nightColor);
        canvas.clipRect(new RectF(0, height * 0.75f, width* current, height));
        canvas.drawPath(nightPath, mPaint);

        mPaint.setColor(dayColor);
        canvas.clipRect(0, 0, width, height, Region.Op.REPLACE);
        canvas.clipRect(new RectF(0, 0, width* current, height * 0.75f));
        canvas.drawPath(curvePath, mPaint);


        canvas.clipRect(0, 0, width, height, Region.Op.REPLACE);
        mPaint.setStyle(Paint.Style.STROKE);

        mPaint.setColor(timelineColor);
        canvas.drawPath(curvePath, mPaint);

        mPaint.setColor(horizonColor);
        canvas.drawLine(0, height * 0.75f, width, height * 0.75f, mPaint);

        mPaint.setColor(taglineColor);
        mPaint.setStrokeWidth(2);
        canvas.drawLine(width * 0.17f, height * 0.2f, width * 0.17f, height * 0.7f, mPaint);
        canvas.drawLine(width * 0.83f, height * 0.2f, width * 0.83f, height * 0.7f, mPaint);
        mPaint.setStrokeWidth(3);


        mPaint.setColor(sunColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShadowLayer(1.0f, 1.0f, 2.0f, 0x33000000);

        canvas.drawCircle(width * current, getY((int)(width * current), segment, height), height * 0.08f,  mPaint);

        mPaint.clearShadowLayer();
    }

    private float getY(int x, double segment, int height) {
        double cos = (Math.cos(-Math.PI + (x * segment)) + 1) / 2;
        return height - (height * (float)cos) + (height * 0.1f);
    }
}
