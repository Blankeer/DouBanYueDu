package com.nostra13.universalimageloader.core.assist;

import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import u.aly.dj;
import u.aly.dx;

public enum ViewScaleType {
    FIT_INSIDE,
    CROP;

    /* renamed from: com.nostra13.universalimageloader.core.assist.ViewScaleType.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$widget$ImageView$ScaleType;

        static {
            $SwitchMap$android$widget$ImageView$ScaleType = new int[ScaleType.values().length];
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_XY.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_START.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_END.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER_INSIDE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.MATRIX.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER_CROP.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    public static ViewScaleType fromImageView(ImageView imageView) {
        switch (AnonymousClass1.$SwitchMap$android$widget$ImageView$ScaleType[imageView.getScaleType().ordinal()]) {
            case dx.b /*1*/:
            case dx.c /*2*/:
            case dx.d /*3*/:
            case dx.e /*4*/:
            case dj.f /*5*/:
                return FIT_INSIDE;
            default:
                return CROP;
        }
    }
}
