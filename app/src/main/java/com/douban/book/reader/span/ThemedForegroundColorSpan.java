package com.douban.book.reader.span;

import android.support.annotation.ArrayRes;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;
import com.douban.book.reader.util.Res;

public class ThemedForegroundColorSpan extends CharacterStyle implements UpdateAppearance {
    private final int mColorArrayResId;

    public ThemedForegroundColorSpan(@ArrayRes int colorArrayResId) {
        this.mColorArrayResId = colorArrayResId;
    }

    public void updateDrawState(TextPaint tp) {
        tp.setColor(Res.getColor(this.mColorArrayResId));
    }
}
