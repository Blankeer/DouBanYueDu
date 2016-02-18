package com.douban.book.reader.lib.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class CropImageView extends ImageView {
    private int mCropAlign;
    private int mCropType;

    public static final class CropAlign {
        public static final int ALIGN_BOTTOM = 1;
        public static final int ALIGN_CENTER = 2;
        public static final int ALIGN_LEFT = 3;
        public static final int ALIGN_RIGHT = 4;
        public static final int ALIGN_TOP = 0;
    }

    public static final class CropType {
        public static final int FIT_FILL = 1;
        public static final int FIT_HEIGHT = 2;
        public static final int FIT_WIDTH = 0;
    }

    public CropImageView(Context context) {
        super(context);
        this.mCropType = -1;
        this.mCropAlign = 0;
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCropType = -1;
        this.mCropAlign = 0;
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCropType = -1;
        this.mCropAlign = 0;
    }

    public void setCropType(int cropType) {
        this.mCropType = cropType;
        if (this.mCropType > -1) {
            setScaleType(ScaleType.MATRIX);
        }
    }

    public void setCropAlign(int cropAlign) {
        this.mCropAlign = cropAlign;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d != null) {
            int i = this.mCropType;
            if (r0 > -1) {
                float scale;
                int dw = d.getIntrinsicWidth();
                int dh = d.getIntrinsicHeight();
                int dx = 0;
                int dy = 0;
                int msWidth = 0;
                int msHeight = 0;
                int theoryw = 0;
                int theoryh = 0;
                float scalew = 0.0f;
                float scaleh = 0.0f;
                i = this.mCropType;
                if (r0 <= 1) {
                    msWidth = MeasureSpec.getSize(widthMeasureSpec);
                    i = getLayoutParams().width;
                    if (r0 == -2 && dw < msWidth) {
                        msWidth = dw;
                    }
                    scalew = ((float) msWidth) / ((float) dw);
                    theoryh = (int) (((float) dh) * scalew);
                }
                i = this.mCropType;
                if (r0 >= 1) {
                    msHeight = MeasureSpec.getSize(heightMeasureSpec);
                    i = getLayoutParams().height;
                    if (r0 == -2 && dh < msHeight) {
                        msHeight = dh;
                    }
                    scaleh = ((float) msHeight) / ((float) dh);
                    theoryw = (int) (((float) dw) * scaleh);
                }
                if (scalew > scaleh) {
                    scale = scalew;
                    int maxHeight = 0;
                    if (VERSION.SDK_INT >= 16) {
                        maxHeight = getMaxHeight();
                    }
                    msHeight = getLayoutParams().height;
                    if (msHeight >= -1) {
                        if (msHeight == -1) {
                            msHeight = MeasureSpec.getSize(heightMeasureSpec);
                        }
                        if (msHeight < maxHeight) {
                            maxHeight = msHeight;
                        }
                    }
                    if (theoryh > maxHeight) {
                        msHeight = maxHeight;
                    } else {
                        msHeight = theoryh;
                    }
                    i = this.mCropAlign;
                    if (r0 >= 2) {
                        dy = (int) ((((float) (msHeight - theoryh)) * 0.5f) + 0.5f);
                    } else {
                        i = this.mCropAlign;
                        if (r0 == 1) {
                            dy = (int) ((((float) (msHeight - theoryh)) * 1.0f) + 0.5f);
                        }
                    }
                } else {
                    scale = scaleh;
                    int maxWidth = 0;
                    if (VERSION.SDK_INT >= 16) {
                        maxWidth = getMaxWidth();
                    }
                    msWidth = getLayoutParams().width;
                    if (msWidth >= -1) {
                        if (msWidth == -1) {
                            msWidth = MeasureSpec.getSize(widthMeasureSpec);
                        }
                        if (msWidth < maxWidth) {
                            maxWidth = msWidth;
                        }
                    }
                    msWidth = theoryw > maxWidth ? maxWidth : theoryw;
                    i = this.mCropAlign;
                    if (r0 <= 2) {
                        dx = (int) ((((float) (msWidth - theoryw)) * 0.5f) + 0.5f);
                    } else {
                        i = this.mCropAlign;
                        if (r0 == 4) {
                            dx = (int) ((((float) (msWidth - theoryw)) * 1.0f) + 0.5f);
                        }
                    }
                }
                Matrix matrix = getImageMatrix();
                matrix.setScale(scale, scale);
                matrix.postTranslate((float) dx, (float) dy);
                setImageMatrix(matrix);
                setMeasuredDimension(msWidth, msHeight);
                return;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
