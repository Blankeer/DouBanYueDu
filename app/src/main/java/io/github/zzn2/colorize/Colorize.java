package io.github.zzn2.colorize;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.github.zzn2.colorize.drawable.ColorStateDrawable;

public class Colorize {
    public static void applyTo(View view) {
        if (view != null) {
            int i;
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                Drawable[] drawables = textView.getCompoundDrawables();
                for (i = 0; i < 4; i++) {
                    Drawable drawable = drawables[i];
                    if (drawable != null) {
                        if (drawable instanceof ColorStateDrawable) {
                            ((ColorStateDrawable) drawables[i]).updateColor(textView.getTextColors());
                        } else {
                            drawables[i] = new ColorStateDrawable(drawable, textView.getTextColors());
                        }
                    }
                }
                textView.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawables[2], drawables[3]);
            } else if (view instanceof ViewGroup) {
                for (i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    applyTo(((ViewGroup) view).getChildAt(i));
                }
            }
        }
    }
}
