package com.mcxiaoke.next.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import com.mcxiaoke.next.ui.R;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import java.lang.reflect.Field;

public class AspectRatioImageView extends ImageView {
    private static final int STRETCH_HORIZONTAL = 0;
    private static final int STRETCH_UNDEFINED = -1;
    private static final int STRETCH_VERTICAL = 1;
    private static final String TAG;
    int mMaxHeight;
    int mMaxWidth;
    private int mStretch;

    static {
        TAG = AspectRatioImageView.class.getSimpleName();
    }

    public AspectRatioImageView(Context context) {
        super(context);
        this.mMaxWidth = AdvancedShareActionProvider.WEIGHT_MAX;
        this.mMaxHeight = AdvancedShareActionProvider.WEIGHT_MAX;
        init(context, null);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMaxWidth = AdvancedShareActionProvider.WEIGHT_MAX;
        this.mMaxHeight = AdvancedShareActionProvider.WEIGHT_MAX;
        init(context, attrs);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mMaxWidth = AdvancedShareActionProvider.WEIGHT_MAX;
        this.mMaxHeight = AdvancedShareActionProvider.WEIGHT_MAX;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        try {
            Field maxWidthField = ImageView.class.getDeclaredField("mMaxWidth");
            Field maxHeightField = ImageView.class.getDeclaredField("mMaxHeight");
            maxWidthField.setAccessible(true);
            maxHeightField.setAccessible(true);
            this.mMaxWidth = ((Integer) maxWidthField.get(this)).intValue();
            this.mMaxHeight = ((Integer) maxHeightField.get(this)).intValue();
        } catch (SecurityException e) {
        } catch (NoSuchFieldException e2) {
        } catch (IllegalArgumentException e3) {
        } catch (IllegalAccessException e4) {
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView);
        this.mStretch = ta.getInt(R.styleable.AspectRatioImageView_ari_stretch, STRETCH_UNDEFINED);
        ta.recycle();
        setAdjustViewBounds(true);
    }

    public void setMaxWidth(int maxWidth) {
        super.setMaxWidth(maxWidth);
        this.mMaxWidth = maxWidth;
    }

    public void setMaxHeight(int maxHeight) {
        super.setMaxHeight(maxHeight);
        this.mMaxHeight = maxHeight;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Drawable drawable = getDrawable();
        boolean setMeasuredDimension = false;
        if (drawable != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            if (1073741824 == MeasureSpec.getMode(heightMeasureSpec) && (height == 0 || STRETCH_VERTICAL == this.mStretch)) {
                float diw = (float) drawable.getIntrinsicWidth();
                if (diw > 0.0f) {
                    setMeasuredDimension(width, (int) Math.max((float) getSuggestedMinimumHeight(), Math.min(((float) width) * (((float) drawable.getIntrinsicHeight()) / diw), (float) this.mMaxHeight)));
                    setMeasuredDimension = true;
                }
            } else if (1073741824 == MeasureSpec.getMode(widthMeasureSpec) && (width == 0 || this.mStretch == 0)) {
                float dih = (float) drawable.getIntrinsicHeight();
                if (dih > 0.0f) {
                    setMeasuredDimension((int) Math.max((float) getSuggestedMinimumWidth(), Math.min(((float) height) * (((float) drawable.getIntrinsicWidth()) / dih), (float) this.mMaxWidth)), height);
                    setMeasuredDimension = true;
                }
            }
        }
        if (!setMeasuredDimension) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
