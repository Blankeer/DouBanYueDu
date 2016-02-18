package com.douban.book.reader.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.UserInfo;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.ImageLoaderUtils;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;

public class UserAvatarView extends ImageView {
    private Bitmap mBitmap;
    private boolean mShowEditBtnWhenEmpty;

    public UserAvatarView(Context context) {
        super(context);
    }

    public UserAvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserAvatarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void displayUserAvatar(UserInfo userInfo) {
        if (userInfo == null || (userInfo.isMe() && UserManager.getInstance().isAnonymousUser())) {
            setImageResource(R.drawable.ic_anonymous);
        } else {
            ImageLoaderUtils.displayImage(userInfo.avatar, this);
        }
    }

    public UserAvatarView showEditButtonWhenEmpty() {
        this.mShowEditBtnWhenEmpty = true;
        return this;
    }

    protected void onDraw(Canvas canvas) {
        float centerX = ((float) getWidth()) * 0.5f;
        float centerY = ((float) getHeight()) * 0.5f;
        float radius = Math.min(centerX, centerY);
        Drawable drawable = getDrawable();
        if (drawable == null) {
            Paint paint = PaintUtils.obtainStrokePaint(Res.getColor(R.array.btn_stroke_color));
            float strokeWidth = (float) Utils.dp2pixel(1.0f);
            paint.setStrokeWidth(strokeWidth);
            canvas.drawCircle(centerX, centerY, radius - (strokeWidth / 2.0f), paint);
            PaintUtils.recyclePaint(paint);
            if (this.mShowEditBtnWhenEmpty) {
                CanvasUtils.drawDrawableCenteredInArea(canvas, Res.getDrawableWithTint((int) R.drawable.v_camera, (int) R.array.light_blue), CanvasUtils.rectFromCenterAndRadius(centerX, centerY, radius / 2.0f));
                return;
            }
            return;
        }
        setBounds(drawable, getWidth(), getHeight());
        Bitmap bitmap = getCacheBitmap();
        if (bitmap == null) {
            Path path = new Path();
            path.addCircle(centerX, centerY, radius, Direction.CCW);
            canvas.clipPath(path);
            drawable.draw(canvas);
            return;
        }
        Canvas bitmapCanvas = new Canvas(bitmap);
        bitmapCanvas.drawColor(0);
        drawable.draw(bitmapCanvas);
        Shader shader = new BitmapShader(this.mBitmap, TileMode.CLAMP, TileMode.CLAMP);
        paint = PaintUtils.obtainPaint();
        paint.setShader(shader);
        canvas.drawCircle(centerX, centerY, radius, paint);
        PaintUtils.recyclePaint(paint);
    }

    @Nullable
    private Bitmap getCacheBitmap() {
        int width = getWidth();
        int height = getHeight();
        try {
            if (this.mBitmap == null || this.mBitmap.getWidth() < width || this.mBitmap.getHeight() < height) {
                this.mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            }
            return this.mBitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void setBounds(Drawable d, int w, int h) {
        if (d != null && w > 0 && h > 0) {
            float drawableWidth = (float) d.getIntrinsicWidth();
            float drawableHeight = (float) d.getIntrinsicHeight();
            if (drawableWidth <= 0.0f) {
                drawableWidth = (float) w;
            }
            if (drawableHeight <= 0.0f) {
                drawableHeight = (float) h;
            }
            float displayWidth;
            if (drawableWidth / ((float) w) > drawableHeight / ((float) h)) {
                float displayHeight = (float) h;
                displayWidth = (displayHeight * drawableWidth) / drawableHeight;
                float padding = (displayWidth - ((float) w)) * 0.5f;
                d.setBounds(Math.round(0.0f - padding), 0, Math.round(displayWidth + padding), Math.round(displayHeight));
                return;
            }
            displayWidth = (float) w;
            d.setBounds(0, 0, Math.round(displayWidth), Math.round((displayWidth * drawableHeight) / drawableWidth));
        }
    }
}
