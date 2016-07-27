package com.tuxan.holytime.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.tuxan.holytime.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SunsetView extends View {

    Paint mPaint;

    int horizonColor;
    int timelineColor;
    int taglineColor;
    int nightColor;
    int dayColor;
    int sunColor;

    int width;
    int height;

    Path curvePath;
    Path nightPath;
    double segmentByPixel;

    SunriseSunsetCalculator mSSCalculator;

    public SunsetView(Context context) {
        super(context);
        init();
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

        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            setLayerType(LAYER_TYPE_SOFTWARE, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;

        curvePath = new Path();
        curvePath.moveTo(0, height);

        segmentByPixel = (2 * Math.PI) / width;

        for (int x = 0; x <= width; x++) {
            curvePath.lineTo(x, getY(x, segmentByPixel, (int)(height * 0.9f)));
        }

        nightPath = new Path(curvePath);
        nightPath.setLastPoint(width, height);
        nightPath.lineTo(width, 0);
        nightPath.lineTo(0, 0);
        nightPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float current = getCurrentTime();

        mPaint.setStyle(Paint.Style.FILL);

        // draw fill of day
        mPaint.setColor(nightColor);
        canvas.clipRect(0, height * 0.75f, width * current, height);
        canvas.drawPath(nightPath, mPaint);

        // draw fill of night
        mPaint.setColor(dayColor);
        canvas.clipRect(0, 0, width, height, Region.Op.REPLACE);
        canvas.clipRect(0, 0, width * current, height * 0.75f);
        canvas.drawPath(curvePath, mPaint);

        // draw time curve
        canvas.clipRect(0, 0, width, height, Region.Op.REPLACE);
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(timelineColor);
        canvas.drawPath(curvePath, mPaint);

        // draw horizon line
        mPaint.setColor(horizonColor);
        canvas.drawLine(0, height * 0.75f, width, height * 0.75f, mPaint);

        // draw sunset and sunrise tag line indicator
        mPaint.setColor(taglineColor);
        mPaint.setStrokeWidth(2);
        canvas.drawLine(width * 0.17f, height * 0.2f, width * 0.17f, height * 0.7f, mPaint);
        canvas.drawLine(width * 0.83f, height * 0.2f, width * 0.83f, height * 0.7f, mPaint);

        // draw sun
        if (current >= 0.17f && current <= 0.83f) {
            mPaint.setColor(sunColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setShadowLayer(1.0f, 1.0f, 2.0f, 0x33000000);
            canvas.drawCircle(width * current, getY((int) (width * current), segmentByPixel, (int) (height * 0.9f)), height * 0.08f, mPaint);
            mPaint.clearShadowLayer();
        }

    }

    private float getY(int x, double segment, int height) {
        double cos = (Math.cos(-Math.PI + (x * segment)) + 1) / 2;
        return height - (height * (float)cos) + (height * 0.1f);
    }

    public void setSunriseSunsetCalculator(SunriseSunsetCalculator calculator) {
        this.mSSCalculator = calculator;
        postInvalidate();
    }

    private float getCurrentTime() {
        if (mSSCalculator == null)
            return 0;

        Calendar cCurrent = Calendar.getInstance();
        Calendar cSunrise = mSSCalculator.getOfficialSunriseCalendarForDate(cCurrent);
        Calendar cSunset = mSSCalculator.getOfficialSunsetCalendarForDate(cCurrent);

        long sunrise = cSunrise.getTimeInMillis();
        long sunset = cSunset.getTimeInMillis();

        long noon = sunrise + ((sunset - sunrise) / 2);
        long start = noon - (12 * 60 * 60 * 1000);

        // recalculate from cero
        sunrise = sunrise - start;
        noon = noon - start;
        sunset = sunset - start;
        long end = noon + (12 * 60 * 60 * 1000);

        long current = cCurrent.getTimeInMillis()- start;

        float c = 0;

        if (current <= sunrise) {
            c = ((float)current / sunrise) * 0.17f;
        } else if (current <= sunset) {
            c = (((float) (current - sunrise) / (sunset - sunrise)) * 0.66f) + 0.17f;
        } else if (current <= end) {
            c = (((float) (current - sunset) / (end - sunset)) * 0.17f) + 0.17f + 0.66f;
        }

        return c;
    }
}
