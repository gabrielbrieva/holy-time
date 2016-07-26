package com.tuxan.holytime.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.tuxan.holytime.R;

public class SunsetView extends View {

    Paint mPaint;

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

        // TODO: load attributes from layout...

        typedArray.recycle();

        init(context);
    }

    private void init(Context context) {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xFF000000);
        mPaint.setStrokeWidth(10);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(0, 0, 300, 100, mPaint);
    }
}
