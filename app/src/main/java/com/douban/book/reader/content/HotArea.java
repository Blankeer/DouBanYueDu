package com.douban.book.reader.content;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.AutoScrollHelper;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Dimen;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.GraphicUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HotArea {
    private static final float BOUNDARY_ICON_SIZE;
    private static final int UNDERLINE_STROKE_WIDTH;
    private RectF mBounds;
    private float mHalfLineSpacing;
    private boolean mHasLeftEdge;
    private boolean mHasRightEdge;
    private Path mLeftEdgePath;
    private Path mPath;
    private List<RectF> mRectList;
    private Path mRightEdgePath;

    static {
        UNDERLINE_STROKE_WIDTH = Utils.dp2pixel(1.0f) + 1;
        BOUNDARY_ICON_SIZE = (float) Utils.dp2pixel(18.0f);
    }

    public HotArea() {
        this.mRectList = new ArrayList();
    }

    public HotArea(RectF rect) {
        this.mRectList = new ArrayList();
        add(rect);
    }

    public HotArea(Rect rect) {
        this(new RectF(rect));
    }

    public HotArea clone() {
        HotArea result = new HotArea();
        result.union(this);
        result.setHasLeftEdge(this.mHasLeftEdge);
        result.setHasRightEdge(this.mHasRightEdge);
        result.setHalfLineSpacing(this.mHalfLineSpacing);
        return result;
    }

    public void addAll(Collection<? extends RectF> rectFs) {
        this.mRectList.addAll(rectFs);
        invalidateBounds();
    }

    public void add(RectF rectF) {
        this.mRectList.add(rectF);
        invalidateBounds();
    }

    public void add(Rect rect) {
        add(new RectF(rect));
    }

    public void union(HotArea hotArea) {
        addAll(hotArea.getRectList());
        setHalfLineSpacing(Math.max(this.mHalfLineSpacing, hotArea.getHalfLineSpacing()));
    }

    public void clear() {
        this.mRectList.clear();
        invalidateBounds();
    }

    List<RectF> getRectList() {
        return this.mRectList;
    }

    public float getTop() {
        return getBounds().top;
    }

    public float getBottom() {
        return getBounds().bottom;
    }

    public float getCenterXOnTop() {
        RectF firstRect = getFirstRect();
        if (firstRect != null) {
            return firstRect.centerX();
        }
        return 0.0f;
    }

    public float getCenterXOnBottom() {
        RectF lastRect = getLastRect();
        if (lastRect != null) {
            return lastRect.centerX();
        }
        return 0.0f;
    }

    @Nullable
    private RectF getFirstRect() {
        float minY = AutoScrollHelper.NO_MAX;
        RectF firstRect = null;
        for (RectF rect : this.mRectList) {
            if (rect.bottom < minY) {
                minY = rect.top;
                firstRect = rect;
            }
        }
        return firstRect;
    }

    @Nullable
    private RectF getLastRect() {
        float maxY = 0.0f;
        RectF lastRect = null;
        for (RectF rect : this.mRectList) {
            if (rect.bottom > maxY) {
                maxY = rect.bottom;
                lastRect = rect;
            }
        }
        return lastRect;
    }

    @Nullable
    public PointF getPointInArea() {
        RectF firstRect = getFirstRect();
        if (firstRect == null || firstRect.isEmpty()) {
            return null;
        }
        return new PointF(firstRect.centerX(), firstRect.centerY());
    }

    private void invalidateBounds() {
        this.mBounds = null;
        if (this.mPath != null) {
            this.mPath.reset();
        }
    }

    private RectF getBounds() {
        if (this.mBounds == null) {
            this.mBounds = new RectF();
        }
        if (this.mBounds.isEmpty()) {
            getPath().computeBounds(this.mBounds, true);
        }
        return this.mBounds;
    }

    public float centerX() {
        return getBounds().centerX();
    }

    public float centerY() {
        return getBounds().centerY();
    }

    public boolean isEmpty() {
        return this.mRectList.size() <= 0;
    }

    public boolean isSimpleArea() {
        return this.mRectList.size() == 1;
    }

    public void setHalfLineSpacing(float lineSpacing) {
        this.mHalfLineSpacing = lineSpacing;
    }

    private float getHalfLineSpacing() {
        return this.mHalfLineSpacing;
    }

    public boolean contains(float x, float y) {
        for (RectF rect : this.mRectList) {
            if (GraphicUtils.containsOrCloseTo(rect, x, y, this.mHalfLineSpacing)) {
                return true;
            }
        }
        if (this.mHasRightEdge) {
            RectF rightBounds = new RectF();
            Path rightEdgePath = getRightEdgePath();
            if (rightEdgePath != null) {
                rightEdgePath.computeBounds(rightBounds, true);
                if (GraphicUtils.containsOrCloseTo(rightBounds, x, y, this.mHalfLineSpacing)) {
                    return true;
                }
            }
        }
        if (this.mHasLeftEdge) {
            RectF leftBounds = new RectF();
            Path leftEdgePath = getLeftEdgePath();
            if (leftEdgePath != null) {
                leftEdgePath.computeBounds(leftBounds, true);
                if (GraphicUtils.containsOrCloseTo(leftBounds, x, y, this.mHalfLineSpacing)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void draw(Canvas canvas, Paint paint) {
        for (RectF rect : this.mRectList) {
            canvas.drawRect(rect, paint);
        }
    }

    public void drawWithLineSpacing(Canvas canvas, Paint paint) {
        for (RectF rect : this.mRectList) {
            canvas.drawRect(rect.left, rect.top - this.mHalfLineSpacing, rect.right, this.mHalfLineSpacing + rect.bottom, paint);
        }
    }

    public void drawUnderline(Canvas canvas, Paint paint) {
        paint.setStrokeWidth((float) UNDERLINE_STROKE_WIDTH);
        float offset = paint.getStrokeWidth() / 2.0f;
        for (RectF rect : this.mRectList) {
            canvas.drawLine(rect.left, rect.bottom - offset, rect.right, rect.bottom - offset, paint);
        }
    }

    public void drawUpDownLine(Canvas canvas, Paint paint) {
        for (RectF rect : this.mRectList) {
            canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
            canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
        }
    }

    public void drawLeftEdge(Canvas canvas, Paint paint) {
        if (this.mHasLeftEdge) {
            Path path = getLeftEdgePath();
            if (path != null) {
                canvas.drawPath(path, paint);
            }
        }
    }

    public void drawRightEdge(Canvas canvas, Paint paint) {
        if (this.mHasRightEdge) {
            Path path = getRightEdgePath();
            if (path != null) {
                canvas.drawPath(path, paint);
                Drawable drawable = Res.getDrawableWithTint((int) R.drawable.v_note, (int) R.array.invert_text_color);
                RectF bounds = GraphicUtils.getPathBounds(path);
                CanvasUtils.drawDrawableCenteredInArea(canvas, drawable, new RectF(bounds.left + ((float) Utils.dp2pixel(2.0f)), bounds.top + ((float) Utils.dp2pixel(1.0f)), bounds.right - ((float) Utils.dp2pixel(2.0f)), bounds.top + BOUNDARY_ICON_SIZE));
            }
        }
    }

    public void setHasLeftEdge(boolean hasLeftEdge) {
        this.mHasLeftEdge = hasLeftEdge;
        invalidateBounds();
    }

    public void setHasRightEdge(boolean hasRightEdge) {
        this.mHasRightEdge = hasRightEdge;
        invalidateBounds();
    }

    public Path getPath() {
        if (this.mPath == null || this.mPath.isEmpty()) {
            Path path = new Path();
            for (RectF rect : this.mRectList) {
                path.addRect(rect, Direction.CCW);
            }
            if (this.mHasLeftEdge && getLeftEdgePath() != null) {
                path.addPath(getLeftEdgePath());
            }
            if (this.mHasRightEdge && getRightEdgePath() != null) {
                path.addPath(getRightEdgePath());
            }
            this.mPath = path;
        }
        return this.mPath;
    }

    private Path getLeftEdgePath() {
        if (this.mLeftEdgePath == null) {
            RectF rect = getFirstRect();
            if (rect != null) {
                this.mLeftEdgePath = new Path();
                this.mLeftEdgePath.moveTo(rect.left, rect.top);
                this.mLeftEdgePath.arcTo(CanvasUtils.rectFromCenterAndRadius(rect.left, rect.top + Dimen.CORNER_RADIUS, Dimen.CORNER_RADIUS), 270.0f, -90.0f, false);
                this.mLeftEdgePath.lineTo(rect.left - Dimen.CORNER_RADIUS, rect.bottom - Dimen.CORNER_RADIUS);
                this.mLeftEdgePath.arcTo(CanvasUtils.rectFromCenterAndRadius(rect.left, rect.bottom - Dimen.CORNER_RADIUS, Dimen.CORNER_RADIUS), 180.0f, -90.0f, false);
                this.mLeftEdgePath.close();
            }
        }
        return this.mLeftEdgePath;
    }

    private Path getRightEdgePath() {
        if (this.mRightEdgePath == null) {
            RectF rect = getLastRect();
            if (rect != null) {
                this.mRightEdgePath = new Path();
                this.mRightEdgePath.moveTo(rect.right, rect.top);
                this.mRightEdgePath.lineTo(rect.right + BOUNDARY_ICON_SIZE, rect.top);
                this.mRightEdgePath.arcTo(CanvasUtils.rectFromCenterAndRadius(rect.right + BOUNDARY_ICON_SIZE, rect.top + Dimen.CORNER_RADIUS, Dimen.CORNER_RADIUS), 270.0f, 90.0f, false);
                this.mRightEdgePath.lineTo((rect.right + BOUNDARY_ICON_SIZE) + Dimen.CORNER_RADIUS, (rect.top + BOUNDARY_ICON_SIZE) - Dimen.CORNER_RADIUS);
                this.mRightEdgePath.arcTo(CanvasUtils.rectFromCenterAndRadius(rect.right + BOUNDARY_ICON_SIZE, (rect.top + BOUNDARY_ICON_SIZE) - Dimen.CORNER_RADIUS, Dimen.CORNER_RADIUS), 0.0f, 90.0f, false);
                this.mRightEdgePath.lineTo(rect.right + Dimen.CORNER_RADIUS, rect.top + BOUNDARY_ICON_SIZE);
                this.mRightEdgePath.lineTo(rect.right + Dimen.CORNER_RADIUS, rect.bottom - Dimen.CORNER_RADIUS);
                this.mRightEdgePath.arcTo(CanvasUtils.rectFromCenterAndRadius(rect.right, rect.bottom - Dimen.CORNER_RADIUS, Dimen.CORNER_RADIUS), 0.0f, 90.0f, false);
                this.mRightEdgePath.close();
            }
        }
        return this.mRightEdgePath;
    }

    public HotArea offset(float dx, float dy) {
        for (RectF rect : this.mRectList) {
            rect.offset(dx, dy);
        }
        invalidateBounds();
        return this;
    }
}
