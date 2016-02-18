package com.douban.book.reader.content.paragraph;

import android.graphics.Canvas;
import android.text.TextUtils;
import com.douban.book.reader.R;
import com.douban.book.reader.helper.WorksListUri;
import com.douban.book.reader.theme.Theme;
import com.sina.weibo.sdk.component.ShareRequestParam;
import org.json.JSONObject;

public class CodeParagraph extends Paragraph {
    public CodeParagraph() {
        setType(1);
        setPaddingTop((float) VERTICAL_MARGIN_NORMAL);
        setPaddingBottom((float) VERTICAL_MARGIN_NORMAL);
    }

    public static CodeParagraph parse(JSONObject json) {
        return parse(json, null);
    }

    public static CodeParagraph parse(JSONObject json, JSONObject defaultFormat) {
        CodeParagraph paragraph = new CodeParagraph();
        paragraph.setId(json.optInt(WorksListUri.KEY_ID));
        paragraph.setText(json.optJSONObject(ShareRequestParam.RESP_UPLOAD_PIC_PARAM_DATA).optString("text"));
        return paragraph;
    }

    public int getOffsetByPoint(float x, float y, boolean jumpByWord, boolean isOffsetAfterThisWord, int startLine, int endLine) {
        return 0;
    }

    public boolean canPinStop() {
        return false;
    }

    public CharSequence getPrintableText(int startOffset, int endOffset) {
        if (TextUtils.isEmpty(this.mPrintableText)) {
            generatePrintableText();
        }
        return this.mPrintableText.subSequence(Math.min(startOffset, this.mPrintableText.length()), Math.min(endOffset, this.mPrintableText.length()));
    }

    protected void onDraw(Canvas canvas, float offsetX, float offsetY, int startLine, int endLine) {
        this.mTextColor = Theme.getColor(R.array.reader_text_color);
        this.mCodeBlock.setTextColor(this.mTextColor);
        this.mCodeBlock.draw(canvas, offsetX, offsetY, startLine, endLine);
    }

    protected float calculateHeight(int startLine) {
        if (needRegenerate()) {
            generate();
        }
        return this.mCodeBlock.getHeight(startLine);
    }
}
