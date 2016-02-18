package com.douban.book.reader.content.paragraph;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.support.v4.internal.view.SupportMenu;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.Format;
import com.douban.book.reader.content.paragraph.Paragraph.CodeSpan;
import com.douban.book.reader.content.paragraph.Paragraph.EmphasizeSpan;
import com.douban.book.reader.content.paragraph.Paragraph.FootnoteSpan;
import com.douban.book.reader.content.paragraph.Paragraph.LatexSpan;
import com.douban.book.reader.content.paragraph.Paragraph.RegularScriptSpan;
import com.douban.book.reader.content.paragraph.Paragraph.StrikeThroughSpan;
import com.douban.book.reader.helper.WorksListUri;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.JsonUtils;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.view.ParagraphView;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.component.WidgetRequestParam;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import com.tencent.open.SocialConstants;
import org.json.JSONArray;
import org.json.JSONObject;

public class RichTextParagraph extends Paragraph {
    public RichTextParagraph() {
        setType(0);
    }

    public RichTextParagraph(ParagraphView view) {
        super(view);
        setType(0);
    }

    public static RichTextParagraph parse(JSONObject json) {
        return parse(json, null);
    }

    public static RichTextParagraph parse(JSONObject json, JSONObject defaultFormat) {
        RichTextParagraph paragraph = new RichTextParagraph();
        paragraph.setId(json.optInt(WorksListUri.KEY_ID));
        boolean isHeadline = false;
        if ("headline".equals(json.optString(SocialConstants.PARAM_TYPE))) {
            isHeadline = true;
        }
        parseData(paragraph, json.optJSONObject(ShareRequestParam.RESP_UPLOAD_PIC_PARAM_DATA), defaultFormat, isHeadline);
        return paragraph;
    }

    private static void parseData(Paragraph paragraph, JSONObject data, JSONObject defaultFormat, boolean isHeadline) {
        paragraph.setType(0);
        paragraph.setText(getContent(data.opt("text")));
        Format format = new Format();
        if (isHeadline) {
            format.textSizeArrayResId = R.array.font_size_headline;
            format.textLineHeightArrayResId = R.array.line_height_headline;
        }
        JSONObject formatObj = data.optJSONObject("format");
        if (formatObj == null) {
            formatObj = defaultFormat;
        }
        if (formatObj != null) {
            format = format.applyFormat(formatObj);
        }
        JSONArray styleList = data.optJSONArray("style");
        if (styleList != null) {
            format = format.applyStyleList(JsonUtils.toArrayList(styleList));
        }
        paragraph.setBold(format.bold);
        paragraph.setIndent(format.indent);
        paragraph.setQuote(format.quote);
        paragraph.setFirstLineIndent(format.firstLineIndent);
        paragraph.setAlign(format.align);
        paragraph.setTextSizeRatio(format.textRatio);
        paragraph.setTextSizes(format.textSizeArrayResId);
    }

    private static SpannableStringBuilder getContent(Object textObj) {
        SpannableStringBuilder result = new SpannableStringBuilder();
        if (textObj instanceof String) {
            result.append((CharSequence) textObj);
        } else if (textObj instanceof JSONArray) {
            JSONArray snippetArray = (JSONArray) textObj;
            for (int i = 0; i < snippetArray.length(); i++) {
                JSONObject snippetObj = snippetArray.optJSONObject(i);
                Object content = snippetObj.opt(WidgetRequestParam.REQ_PARAM_COMMENT_CONTENT);
                SpannableStringBuilder snippet = null;
                if (content instanceof String) {
                    snippet = new SpannableStringBuilder((CharSequence) content);
                } else if (content instanceof JSONArray) {
                    snippet = getContent(content);
                }
                Object obj = null;
                String kind = snippetObj.optString("kind");
                if ("footnote".equals(kind)) {
                    obj = new FootnoteSpan();
                } else if ("emphasize".equals(kind)) {
                    obj = new EmphasizeSpan();
                } else if ("regular_script".equals(kind)) {
                    obj = new RegularScriptSpan();
                } else if ("strikethrough".equals(kind)) {
                    obj = new StrikeThroughSpan();
                } else if (SelectCountryActivity.EXTRA_COUNTRY_CODE.equals(kind)) {
                    obj = new CodeSpan();
                } else if ("latex".equals(kind)) {
                    obj = new LatexSpan();
                }
                if (!(obj == null || snippet == null)) {
                    snippet.setSpan(obj, 0, snippet.length(), 33);
                }
                result.append(snippet);
            }
        }
        return result;
    }

    public CharSequence getPrintableText(int startOffset, int endOffset) {
        if (TextUtils.isEmpty(this.mPrintableText)) {
            generatePrintableText();
        }
        return this.mPrintableText.subSequence(Math.min(startOffset, this.mPrintableText.length()), Math.min(endOffset, this.mPrintableText.length()));
    }

    public float getPaddingTop() {
        if (needRegenerate()) {
            generate();
        }
        return super.getPaddingTop() + this.mLineSpacing;
    }

    protected float calculateHeight(int startLine) {
        if (needRegenerate()) {
            generate();
        }
        int lineCount = this.mLineArray.size();
        if (lineCount <= 0) {
            return this.mLineHeight;
        }
        if (this.mLineLimit > 0 && lineCount > this.mLineLimit) {
            lineCount = this.mLineLimit;
        }
        float height = 0.0f;
        for (int i = startLine; i < lineCount; i++) {
            height += ((Line) this.mLineArray.get(i)).lineHeight;
        }
        return height;
    }

    protected void onDraw(Canvas canvas, float offsetX, float offsetY, int startLine, int endLine) {
        this.mTextColor = getTextColor();
        Paint generalPaint = PaintUtils.obtainPaint(this.mTextSize);
        generalPaint.setTypeface(Font.SANS_SERIF);
        generalPaint.setColor(this.mTextColor);
        FontMetrics metrics = generalPaint.getFontMetrics();
        float x = (this.mLeftMargin + this.mTextAreaLeftMargin) + offsetX;
        float y = offsetY - metrics.bottom;
        float yy = y;
        if (endLine <= 0 || endLine > this.mLineArray.size()) {
            endLine = this.mLineArray.size();
        }
        int i = startLine;
        while (i < endLine) {
            Line line = (Line) this.mLineArray.get(i);
            y += line.lineHeight;
            yy = y - ((line.lineHeight - this.mLineHeight) / 2.0f);
            float stretch = line.stretch;
            float drawnX = x + line.x;
            synchronized (line.mTextRunLock) {
                for (TextRun textrun : line.textrunList) {
                    if (textrun instanceof FootnoteTextRun) {
                        ((FootnoteTextRun) textrun).draw(canvas, drawnX, yy, generalPaint);
                    } else if (textrun instanceof LatexTextRun) {
                        ((LatexTextRun) textrun).draw(canvas, drawnX, yy, this.mTextSize, this.mTextColor);
                    } else if (textrun instanceof CodeTextRun) {
                        ((CodeTextRun) textrun).draw(canvas, drawnX, yy, this.mTextSize, this.mTextColor, metrics);
                    } else if (textrun instanceof LinkTextRun) {
                        ((LinkTextRun) textrun).draw(canvas, drawnX, yy, stretch, this.mTextSize, metrics);
                    } else {
                        Canvas canvas2 = canvas;
                        float f = drawnX;
                        float f2 = yy;
                        textrun.draw(canvas2, f, f2, stretch, this.mTextSize, this.mTextColor, metrics);
                    }
                    drawnX += (textrun.width + (((float) textrun.getStretchPointCount()) * stretch)) + stretch;
                }
            }
            if (this.mLineLimit > 0 && i == this.mLineLimit - 1 && i < this.mLineArray.size() - 1) {
                Canvas canvas3 = canvas;
                canvas3.drawText(ELLIPSIS, (this.mTextAreaWidth + x) - this.mTextSize, yy, generalPaint);
                break;
            }
            if (DebugSwitch.on(Key.APP_DEBUG_SHOW_LINE_BASELINE)) {
                CanvasUtils.drawHorizontalLine(canvas, y, SupportMenu.CATEGORY_MASK);
                CanvasUtils.drawHorizontalLine(canvas, yy, -16776961);
            }
            i++;
        }
        PaintUtils.recyclePaint(generalPaint);
    }
}
