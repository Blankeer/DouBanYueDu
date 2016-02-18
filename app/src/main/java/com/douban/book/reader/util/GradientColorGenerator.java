package com.douban.book.reader.util;

import android.graphics.Color;
import android.support.v4.view.ViewCompat;

public class GradientColorGenerator {
    private int mEndColor;
    private float mEndLevel;
    private int mStartColor;
    private float mStartLevel;

    public GradientColorGenerator(float startLevel, float endLevel, int startColor, int endColor) {
        this.mStartLevel = 0.0f;
        this.mEndLevel = 100.0f;
        this.mStartColor = ViewCompat.MEASURED_STATE_MASK;
        this.mEndColor = -1;
        this.mStartLevel = startLevel;
        this.mEndLevel = endLevel;
        this.mStartColor = startColor;
        this.mEndColor = endColor;
    }

    public int getColorForLevel(float level) {
        return Color.argb(intValueForLevel(level, (float) Color.alpha(this.mStartColor), (float) Color.alpha(this.mEndColor)), intValueForLevel(level, (float) Color.red(this.mStartColor), (float) Color.red(this.mEndColor)), intValueForLevel(level, (float) Color.green(this.mStartColor), (float) Color.green(this.mEndColor)), intValueForLevel(level, (float) Color.blue(this.mStartColor), (float) Color.blue(this.mEndColor)));
    }

    private float valueForLevel(float level, float startValue, float endValue) {
        return startValue + ((endValue - startValue) * ((level - this.mStartLevel) / (this.mEndLevel - this.mStartLevel)));
    }

    private int intValueForLevel(float level, float startValue, float endValue) {
        return Math.round(valueForLevel(level, startValue, endValue));
    }
}
