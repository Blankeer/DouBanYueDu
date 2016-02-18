package com.douban.book.reader.content.paragraph;

import android.graphics.Canvas;
import com.douban.book.reader.helper.WorksListUri;
import org.json.JSONObject;

public class PageBreakParagraph extends Paragraph {
    public PageBreakParagraph() {
        setType(3);
    }

    public static PageBreakParagraph parse(JSONObject json) {
        PageBreakParagraph paragraph = new PageBreakParagraph();
        paragraph.setId(json.optInt(WorksListUri.KEY_ID));
        return paragraph;
    }

    public int getOffsetByPoint(float x, float y, boolean jumpByWord, boolean isOffsetAfterThisWord, int startLine, int endLine) {
        return 0;
    }

    protected float calculateHeight(int startLine) {
        return 0.0f;
    }

    protected void onDraw(Canvas canvas, float offsetX, float offsetY, int startLine, int endLine) {
    }
}
