package com.douban.book.reader.content.paragraph;

import android.graphics.Canvas;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.net.Uri;
import com.douban.book.reader.R;
import com.douban.book.reader.content.HotArea;
import com.douban.book.reader.content.paragraph.Paragraph.BaseSpan;
import com.douban.book.reader.content.paragraph.Paragraph.EmailSpan;
import com.douban.book.reader.content.paragraph.Paragraph.LinkSpan;
import com.douban.book.reader.content.touchable.LinkTouchable;
import com.douban.book.reader.content.touchable.Touchable;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.SpanUtils;
import com.douban.book.reader.util.StringUtils;
import java.util.List;

public class LinkTextRun extends TextRun {
    private static final int HOTAREA_PADDING_Y = 10;
    private RectF mRect;

    public LinkTextRun(List<BaseSpan> styles) {
        super(styles);
        this.mRect = null;
    }

    public void draw(Canvas canvas, float x, float y, float stretch, float textSize, FontMetrics metrics) {
        super.draw(canvas, x, y, stretch, textSize, Res.getColor(R.array.reader_link_text_color), metrics);
        if (this.mRect == null) {
            this.mRect = new RectF();
        }
        this.mRect.left = x;
        this.mRect.top = (metrics.ascent + y) - 10.0f;
        this.mRect.bottom = (metrics.descent + y) + 10.0f;
        this.mRect.right = this.width + x;
    }

    public Touchable getTouchable() {
        LinkTouchable touchable = null;
        if (this.mRect != null) {
            touchable = new LinkTouchable();
            touchable.hotArea = new HotArea(this.mRect);
            BaseSpan span = SpanUtils.getSpan(styles(), LinkSpan.class);
            CharSequence link = SpanUtils.getSubStringWithSpan(this.mText, span);
            if (span instanceof EmailSpan) {
                link = String.format("mailto:%s", new Object[]{link});
            }
            if (StringUtils.isNotEmpty(link)) {
                touchable.link = Uri.parse(String.valueOf(link));
            }
        }
        return touchable;
    }
}
