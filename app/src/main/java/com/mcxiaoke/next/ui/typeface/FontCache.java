package com.mcxiaoke.next.ui.typeface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.mcxiaoke.next.ui.R;
import java.util.HashMap;
import java.util.Map;

public class FontCache {
    private Map<String, Typeface> mCache;

    static class SingletonHolder {
        public static final FontCache INSTANCE;

        SingletonHolder() {
        }

        static {
            INSTANCE = new FontCache();
        }
    }

    private FontCache() {
        this.mCache = new HashMap();
    }

    public static FontCache getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void setFont(TextView tv, AttributeSet attrs) {
        TypedArray a = tv.getContext().obtainStyledAttributes(attrs, R.styleable.FontFaceStyle);
        setFont(tv, a.getString(R.styleable.FontFaceStyle_font_path), a.getBoolean(R.styleable.FontFaceStyle_font_use_cache, false));
        a.recycle();
    }

    public void setFont(TextView tv, String fontName) {
        setFont(tv, fontName, true);
    }

    public void setFont(TextView tv, String fontName, boolean useCache) {
        if (!TextUtils.isEmpty(fontName)) {
            Typeface typeface;
            Context context = tv.getContext();
            if (useCache) {
                typeface = (Typeface) this.mCache.get(fontName);
                if (typeface == null) {
                    typeface = Typeface.createFromAsset(context.getAssets(), fontName);
                    this.mCache.put(fontName, typeface);
                }
            } else {
                typeface = Typeface.createFromAsset(tv.getContext().getAssets(), fontName);
            }
            tv.setTypeface(typeface);
        }
    }

    public void clear() {
        this.mCache.clear();
    }
}
