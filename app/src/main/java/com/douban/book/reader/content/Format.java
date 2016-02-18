package com.douban.book.reader.content;

import com.douban.book.reader.R;
import com.douban.book.reader.util.StringUtils;
import java.util.List;
import org.json.JSONObject;

public class Format {
    private static final String STYLE_AUTHOR = "author";
    private static final String STYLE_SUBTITLE = "subtitle";
    private static final String STYLE_TITLE = "title";
    private static final String STYLE_TRANSLATOR = "translator";
    private static final float TEXTSIZE_RATIO_LARGE = 1.1f;
    private static final float TEXTSIZE_RATIO_NORMAL = 1.0f;
    private static final float TEXTSIZE_RATIO_X_LARGE = 1.2f;
    public Align align;
    public boolean bold;
    public boolean firstLineIndent;
    public boolean indent;
    public boolean quote;
    public int textLineHeightArrayResId;
    public float textRatio;
    public int textSizeArrayResId;

    public enum Align {
        LEFT,
        CENTER,
        RIGHT
    }

    private static class Builder {
        private Format mFormat;

        public Builder(Format buildUpOn) {
            this.mFormat = buildUpOn;
        }

        public Builder indent(boolean indent) {
            this.mFormat.indent = indent;
            return this;
        }

        public Builder firstLineIndent(boolean firstLineIndent) {
            this.mFormat.firstLineIndent = firstLineIndent;
            return this;
        }

        public Builder quote(boolean quote) {
            this.mFormat.quote = quote;
            return this;
        }

        public Builder bold(boolean bold) {
            this.mFormat.bold = bold;
            return this;
        }

        public Builder align(Align align) {
            this.mFormat.align = align;
            return this;
        }

        public Builder textRatio(float textRatio) {
            this.mFormat.textRatio = textRatio;
            return this;
        }

        public Builder textSizeArray(int resId) {
            this.mFormat.textSizeArrayResId = resId;
            return this;
        }

        public Builder textLineHeightArray(int resId) {
            this.mFormat.textLineHeightArrayResId = resId;
            return this;
        }

        public Format build() {
            return this.mFormat;
        }
    }

    public Format() {
        this.indent = false;
        this.firstLineIndent = true;
        this.quote = false;
        this.bold = false;
        this.align = Align.LEFT;
        this.textRatio = TEXTSIZE_RATIO_NORMAL;
        this.textSizeArrayResId = R.array.font_size_content;
        this.textLineHeightArrayResId = R.array.line_height_content;
    }

    public Format applyFormat(JSONObject json) {
        if (json == null) {
            return this;
        }
        Builder builder = new Builder(this).bold(json.optBoolean("p_bold")).indent(json.optBoolean("p_indent")).quote(json.optBoolean("p_quote")).firstLineIndent(!json.optBoolean("t_indent"));
        String align = json.optString("p_align");
        if ("right".equals(align)) {
            builder.align(Align.RIGHT);
        } else if ("center".equals(align)) {
            builder.align(Align.CENTER);
        }
        String textsize = json.optString("p_textsize");
        if ("large".equals(textsize)) {
            builder.textRatio(TEXTSIZE_RATIO_LARGE);
        } else if ("x-large".equals(textsize)) {
            builder.textRatio(TEXTSIZE_RATIO_X_LARGE);
        }
        float fontLevel = (float) json.optDouble("p_fontsize", 0.0d);
        if (fontLevel != 0.0f) {
            builder.textRatio(TEXTSIZE_RATIO_NORMAL + (0.1f * fontLevel));
        }
        return builder.build();
    }

    public Format applyStyle(String styleName) {
        Builder builder = new Builder(this);
        if (StringUtils.inList(styleName, STYLE_TITLE, STYLE_SUBTITLE, STYLE_AUTHOR, STYLE_TRANSLATOR)) {
            builder.indent(false);
            builder.firstLineIndent(false);
        }
        if (StringUtils.inList(styleName, STYLE_TITLE, STYLE_SUBTITLE)) {
            builder.bold(true);
        }
        if (StringUtils.inList(styleName, STYLE_TITLE)) {
            builder.textSizeArray(R.array.font_size_title);
            builder.textLineHeightArray(R.array.line_height_title);
        } else {
            if (StringUtils.inList(styleName, STYLE_SUBTITLE)) {
                builder.textSizeArray(R.array.font_size_subtitle);
                builder.textLineHeightArray(R.array.line_height_subtitle);
            } else {
                if (StringUtils.inList(styleName, STYLE_AUTHOR, STYLE_TRANSLATOR)) {
                    builder.textSizeArray(R.array.font_size_author);
                    builder.textLineHeightArray(R.array.line_height_author);
                }
            }
        }
        return this;
    }

    public Format applyStyleList(List<String> styleList) {
        for (String style : styleList) {
            applyStyle(style);
        }
        return this;
    }

    public boolean isLeftAligned() {
        return this.align == Align.LEFT;
    }

    public boolean isCenterAligned() {
        return this.align == Align.CENTER;
    }

    public boolean isRightAligned() {
        return this.align == Align.RIGHT;
    }
}
