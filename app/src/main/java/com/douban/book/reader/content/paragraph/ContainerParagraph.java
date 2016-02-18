package com.douban.book.reader.content.paragraph;

import android.graphics.Canvas;
import android.text.SpannableStringBuilder;
import com.douban.book.reader.content.Format.Align;
import com.douban.book.reader.helper.WorksListUri;
import com.douban.book.reader.util.JsonUtils;
import com.sina.weibo.sdk.component.ShareRequestParam;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

public class ContainerParagraph extends Paragraph {
    private static final String TAG = "ContainerParagraph";
    private boolean mIsCatalog;
    private int mParagraphLimit;
    ArrayList<Paragraph> mParagraphList;

    public ContainerParagraph() {
        this.mParagraphList = new ArrayList();
        this.mIsCatalog = false;
        this.mParagraphLimit = 0;
        setType(5);
    }

    public static ContainerParagraph parse(JSONObject json) {
        ContainerParagraph paragraph = new ContainerParagraph();
        paragraph.setId(json.optInt(WorksListUri.KEY_ID));
        parseData(paragraph, json.optJSONObject(ShareRequestParam.RESP_UPLOAD_PIC_PARAM_DATA));
        return paragraph;
    }

    private static void parseData(ContainerParagraph paragraph, JSONObject data) {
        paragraph.setType(5);
        JSONObject formatObj = data.optJSONObject("format");
        paragraph.setIsCatalog(data.optBoolean("is_anchor", false));
        JSONArray styleList = data.optJSONArray("style");
        if (styleList != null && JsonUtils.toArrayList(styleList).contains("header")) {
            paragraph.setPaddingTop((float) VERTICAL_MARGIN_NORMAL);
            paragraph.setPaddingBottom((float) VERTICAL_MARGIN_LARGE);
        }
        JSONArray container = data.optJSONArray("paragraphs");
        if (container != null) {
            for (int i = 0; i < container.length(); i++) {
                paragraph.appendParagraph(Paragraph.parse(container.optJSONObject(i), formatObj));
            }
        }
    }

    public void setIsCatalog(boolean isCatalog) {
        this.mIsCatalog = isCatalog;
    }

    public boolean isCatalog() {
        return this.mIsCatalog;
    }

    public void appendParagraph(Paragraph paragraph) {
        if (this.mParagraphList == null) {
            this.mParagraphList = new ArrayList();
        }
        this.mParagraphList.add(paragraph);
    }

    public void setLineHeightArray(int lineHeightResId) {
        super.setLineHeightArray(lineHeightResId);
        Iterator it = this.mParagraphList.iterator();
        while (it.hasNext()) {
            ((Paragraph) it.next()).setLineHeightArray(lineHeightResId);
        }
    }

    public void setTextSizes(int size) {
        super.setTextSizes(size);
        Iterator it = this.mParagraphList.iterator();
        while (it.hasNext()) {
            ((Paragraph) it.next()).setTextSizes(size);
        }
    }

    public void setTextColorResId(int color) {
        super.setTextColorResId(color);
        Iterator it = this.mParagraphList.iterator();
        while (it.hasNext()) {
            ((Paragraph) it.next()).setTextColorResId(color);
        }
    }

    public void setAlign(Align align) {
        super.setAlign(align);
        Iterator it = this.mParagraphList.iterator();
        while (it.hasNext()) {
            ((Paragraph) it.next()).setAlign(align);
        }
    }

    public void setWidth(float width) {
        super.setWidth(width);
        Iterator it = this.mParagraphList.iterator();
        while (it.hasNext()) {
            ((Paragraph) it.next()).setWidth(width);
        }
    }

    public synchronized void generate() {
        if (this.mParagraphList != null) {
            Iterator it = this.mParagraphList.iterator();
            while (it.hasNext()) {
                ((Paragraph) it.next()).generate();
            }
        }
    }

    protected float calculateHeight(int startLine) {
        float height = 0.0f;
        int count = 0;
        if (this.mParagraphList != null) {
            Iterator it = this.mParagraphList.iterator();
            while (it.hasNext()) {
                Paragraph paragraph = (Paragraph) it.next();
                height += count == 0 ? paragraph.getHeight(startLine) : paragraph.getHeight();
                count++;
                if (this.mParagraphLimit != 0 && count == this.mParagraphLimit) {
                    break;
                }
            }
        }
        return height;
    }

    protected float calculateMinHeight() {
        float height = this.mPaddingTop + this.mPaddingBottom;
        int count = 0;
        if (this.mParagraphList != null) {
            Iterator it = this.mParagraphList.iterator();
            while (it.hasNext()) {
                height += ((Paragraph) it.next()).getMinHeight();
                count++;
                if (this.mParagraphLimit != 0 && count == this.mParagraphLimit) {
                    break;
                }
            }
        }
        return height;
    }

    public int getLineCount() {
        int lineCount = 0;
        if (this.mParagraphList != null) {
            Iterator it = this.mParagraphList.iterator();
            while (it.hasNext()) {
                lineCount += ((Paragraph) it.next()).getLineCount();
            }
        }
        return lineCount;
    }

    public void setLineLimit(int count) {
        if (this.mLineLimit != count) {
            this.mLineLimit = count;
            int measuredLineCount = 0;
            boolean breakNext = false;
            if (this.mParagraphList != null) {
                Iterator it = this.mParagraphList.iterator();
                while (it.hasNext()) {
                    Paragraph paragraph = (Paragraph) it.next();
                    paragraph.setLineLimit(0);
                    int lineCount = paragraph.getLineCount();
                    if (measuredLineCount + lineCount >= this.mLineLimit) {
                        lineCount = this.mLineLimit - measuredLineCount;
                        paragraph.setLineLimit(lineCount);
                        breakNext = true;
                    }
                    measuredLineCount += lineCount;
                    this.mParagraphLimit++;
                    if (breakNext) {
                        return;
                    }
                }
            }
        }
    }

    public void onDraw(Canvas canvas, float offsetX, float offsetY, int startLine, int endLine) {
        if (this.mParagraphList != null) {
            float penY = offsetY;
            int count = 0;
            Iterator it = this.mParagraphList.iterator();
            while (it.hasNext()) {
                Paragraph paragraph = (Paragraph) it.next();
                paragraph.draw(canvas, offsetX, penY, startLine, endLine);
                penY += paragraph.getHeight();
                count++;
                if (this.mParagraphLimit != 0 && count == this.mParagraphLimit) {
                    return;
                }
            }
        }
    }

    public int getOffsetByPoint(float x, float y, boolean jumpByWord, boolean isOffsetAfterThisWord, int startLine, int endLine) {
        return 0;
    }

    public CharSequence getPrintableText(int startOffset, int endOffset) {
        SpannableStringBuilder builder = new SpannableStringBuilder(Table.STRING_DEFAULT_VALUE);
        if (this.mParagraphList == null) {
            return builder;
        }
        int i = 0;
        int count = this.mParagraphList.size() - 1;
        Iterator it = this.mParagraphList.iterator();
        while (it.hasNext()) {
            builder.append(((Paragraph) it.next()).getPrintableText());
            if (i < count) {
                builder.append("\n");
            }
            i++;
        }
        return builder.subSequence(Math.min(startOffset, builder.length()), Math.min(endOffset, builder.length()));
    }

    public Paragraph[] getChildrenParagraph() {
        if (this.mParagraphList == null) {
            return null;
        }
        return (Paragraph[]) this.mParagraphList.toArray(new Paragraph[0]);
    }
}
