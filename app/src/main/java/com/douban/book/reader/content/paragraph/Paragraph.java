package com.douban.book.reader.content.paragraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.internal.view.SupportMenu;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.Format.Align;
import com.douban.book.reader.content.HotArea;
import com.douban.book.reader.content.PageMetrics;
import com.douban.book.reader.content.paragraph.decorator.BulletDecorator;
import com.douban.book.reader.content.paragraph.decorator.Decorator;
import com.douban.book.reader.content.paragraph.decorator.LineQuoteDecorator;
import com.douban.book.reader.content.touchable.Touchable;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.util.BreakIteratorUtils;
import com.douban.book.reader.util.CharUtils;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.SpanUtils;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.view.ParagraphView;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import com.tencent.open.SocialConstants;
import io.realm.internal.Table;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

public abstract class Paragraph {
    public static final float CODE_TEXTSIZE_RATIO = 0.8f;
    public static final String ELLIPSIS;
    private static final float SELECTION_PIN_LINE_WIDTH;
    private static final String TAG = "Paragraph";
    protected static final int VERTICAL_MARGIN_LARGE;
    protected static final int VERTICAL_MARGIN_NORMAL;
    private static final char[] c;
    public static final Pattern sEmailPattern;
    public static final Pattern sNonBmpPattern;
    public static final Pattern sUrlPattern;
    public static final Pattern sWesternPattern;
    Align mAlign;
    private float mAllocatedX;
    private App mApp;
    private boolean mAutoAdjustLeftMargin;
    float mBaseLeftMargin;
    float mBlockIndentRatio;
    private boolean mBoldFlag;
    private boolean mChanged;
    protected Code mCodeBlock;
    private Decorator mDecorator;
    boolean mEllipsed;
    private EmphasizeSpan mEmphasizeSpan;
    private float mEnglishSpaceWidth;
    private boolean mFirstLineIndentFlag;
    private int mFootnoteLength;
    private int mGeneratedScale;
    private boolean mIndentFlag;
    private boolean mJustifyFlag;
    float mLeftMargin;
    List<Line> mLineArray;
    private float mLineAscent;
    private float mLineDescent;
    float mLineHeight;
    int mLineHeightResId;
    int mLineLimit;
    float mLineSpacing;
    float mPaddingBottom;
    float mPaddingTop;
    private int mParagraphId;
    private int mParsedTextPos;
    protected SpannableString mPrintableText;
    private boolean mQuoteFlag;
    private RegularScriptSpan mRegularScriptSpan;
    private SpannableString mText;
    float mTextAreaLeftMargin;
    float mTextAreaWidth;
    int mTextColor;
    int mTextColorArray;
    float mTextSize;
    int mTextSizeArrayResId;
    float mTextSizeRatio;
    private int mType;
    protected ParagraphView mView;
    private float mWidth;

    public static abstract class BaseSpan {
    }

    public interface NoStretch {
    }

    public static class Type {
        public static final int CODE = 1;
        public static final int CONTAINER = 5;
        public static final int ILLUSTRATION = 2;
        public static final int PAGEBREAK = 3;
        public static final int PARAGRAPH = 0;
    }

    public static abstract class ContentSpan extends BaseSpan {
    }

    public static class FootnoteSpan extends BaseSpan implements NoStretch {
    }

    public static abstract class StyleSpan extends BaseSpan {
    }

    public static class CodeSpan extends ContentSpan implements NoStretch {
    }

    public static class EmphasizeSpan extends StyleSpan {
    }

    public static class EnglishSpan extends StyleSpan {
    }

    public static class LatexSpan extends ContentSpan implements NoStretch {
    }

    public static class LinkSpan extends StyleSpan implements NoStretch {
    }

    public static class RegularScriptSpan extends StyleSpan {
    }

    public static class StrikeThroughSpan extends StyleSpan {
    }

    public static class EmailSpan extends LinkSpan {
    }

    public static class UrlSpan extends LinkSpan {
    }

    protected abstract float calculateHeight(int i);

    protected abstract void onDraw(Canvas canvas, float f, float f2, int i, int i2);

    static {
        VERTICAL_MARGIN_NORMAL = Utils.dp2pixel(35.0f);
        VERTICAL_MARGIN_LARGE = Utils.dp2pixel(85.0f);
        SELECTION_PIN_LINE_WIDTH = (float) Utils.dp2pixel(3.0f);
        c = new char[]{'\u2026'};
        ELLIPSIS = new String(c);
        sWesternPattern = Pattern.compile("([\\u2018|\\u201c]*[\\p{ASCII}|\\u00a0-\\u017f|\\u0180-\\u0237|\\u0384-\\u03f6|\\u20a0-\\u20bf|\\u2200-\\u22ff|\\u2460-\\u2468|\\u24b6-\\u24ea]+[\\u2019|\\u201d]*)+");
        sNonBmpPattern = Pattern.compile("[^\\u0000-\\uffff]+");
        sUrlPattern = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
        sEmailPattern = Pattern.compile("[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})");
    }

    public Paragraph(ParagraphView view) {
        this(view.getContext());
        this.mView = view;
        this.mAutoAdjustLeftMargin = false;
    }

    public Paragraph() {
        this(App.get());
    }

    public Paragraph(Context context) {
        this.mType = 0;
        this.mAlign = Align.LEFT;
        this.mBoldFlag = false;
        this.mIndentFlag = false;
        this.mQuoteFlag = false;
        this.mFirstLineIndentFlag = true;
        this.mJustifyFlag = true;
        this.mTextColorArray = R.array.reader_text_color;
        this.mLineLimit = 0;
        this.mEllipsed = false;
        this.mText = new SpannableString(Table.STRING_DEFAULT_VALUE);
        this.mPrintableText = new SpannableString(Table.STRING_DEFAULT_VALUE);
        this.mEmphasizeSpan = new EmphasizeSpan();
        this.mRegularScriptSpan = new RegularScriptSpan();
        this.mLineArray = Collections.synchronizedList(new ArrayList());
        this.mAllocatedX = SELECTION_PIN_LINE_WIDTH;
        this.mParsedTextPos = 0;
        this.mFootnoteLength = 0;
        this.mBaseLeftMargin = -1.0f;
        this.mAutoAdjustLeftMargin = true;
        this.mTextSizeArrayResId = R.array.font_size_content;
        this.mTextSizeRatio = 1.0f;
        this.mLineHeightResId = R.array.line_height_content;
        this.mGeneratedScale = -1;
        this.mChanged = true;
        this.mView = null;
        this.mApp = (App) context.getApplicationContext();
    }

    public static Paragraph parse(JSONObject json) {
        return parse(json, null);
    }

    public static Paragraph parse(JSONObject json, JSONObject defaultFormat) {
        Paragraph paragraph = null;
        if (json != null) {
            String type = json.optString(SocialConstants.PARAM_TYPE);
            if (BaseShareEditFragment.CONTENT_TYPE_ILLUS.equals(type)) {
                paragraph = IllusParagraph.parse(json, defaultFormat);
            } else if ("container".equals(type)) {
                paragraph = ContainerParagraph.parse(json);
            } else if ("pagebreak".equals(type)) {
                paragraph = PageBreakParagraph.parse(json);
            } else if ("headline".equals(type)) {
                paragraph = RichTextParagraph.parse(json);
            } else if (SelectCountryActivity.EXTRA_COUNTRY_CODE.equals(type)) {
                paragraph = CodeParagraph.parse(json, defaultFormat);
            } else {
                paragraph = RichTextParagraph.parse(json, defaultFormat);
            }
        }
        if (paragraph == null) {
            return new ContainerParagraph();
        }
        return paragraph;
    }

    public void setView(ParagraphView view) {
        this.mView = view;
        this.mAutoAdjustLeftMargin = false;
    }

    private void initTextSize() {
        if (this.mView == null) {
            this.mTextSize = Res.getScaledDimension(this.mTextSizeArrayResId) * this.mTextSizeRatio;
            this.mLineSpacing = Res.getScaledDimension(this.mLineHeightResId) - this.mTextSize;
        } else {
            this.mTextSize = this.mView.getTextSize();
            this.mLineSpacing = ((float) this.mView.getLineHeight()) - this.mView.getTextSize();
        }
        this.mLineHeight = this.mTextSize + this.mLineSpacing;
        Paint paint = PaintUtils.obtainPaint(this.mTextSize);
        this.mLineDescent = paint.descent();
        this.mLineAscent = paint.ascent();
        paint.setTypeface(Font.ENGLISH);
        this.mEnglishSpaceWidth = paint.measureText(" ");
        PaintUtils.recyclePaint(paint);
    }

    private void initMarginSize() {
        initTextSize();
        if (this.mBaseLeftMargin < SELECTION_PIN_LINE_WIDTH) {
            this.mLeftMargin = (float) Res.getDimensionPixelSize(R.dimen.reader_horizontal_padding);
        } else {
            this.mLeftMargin = this.mBaseLeftMargin;
        }
        this.mTextAreaWidth = this.mWidth - (this.mLeftMargin * 2.0f);
        if (this.mAutoAdjustLeftMargin) {
            this.mTextAreaWidth -= this.mTextAreaWidth % Res.getScaledDimension(R.array.font_size_content);
            this.mLeftMargin = (this.mWidth - this.mTextAreaWidth) / 2.0f;
        }
        this.mTextAreaLeftMargin = getTextAreaLeftMargin();
        this.mTextAreaWidth -= this.mTextAreaLeftMargin;
    }

    private float getTextAreaLeftMargin() {
        float result = SELECTION_PIN_LINE_WIDTH;
        if (this.mDecorator != null) {
            result = SELECTION_PIN_LINE_WIDTH + this.mDecorator.getInsetLeft();
        }
        if (this.mIndentFlag) {
            result += this.mTextSize * 2.0f;
        }
        return result + (this.mTextSize * this.mBlockIndentRatio);
    }

    public void setId(int id) {
        this.mParagraphId = id;
    }

    public int getId() {
        return this.mParagraphId;
    }

    public void setWidth(float width) {
        if (Math.abs(this.mWidth - width) > 0.1f) {
            this.mWidth = width;
            initMarginSize();
            this.mChanged = true;
        }
    }

    public void setTextSizes(int sizeResId) {
        if (this.mTextSizeArrayResId != sizeResId) {
            this.mTextSizeArrayResId = sizeResId;
            this.mChanged = true;
        }
    }

    public void setLineHeightArray(int lineHeightResId) {
        if (this.mLineHeightResId != lineHeightResId) {
            this.mLineHeightResId = lineHeightResId;
            this.mChanged = true;
        }
    }

    public void setPaddingTop(float paddingTop) {
        if (this.mPaddingTop != paddingTop) {
            this.mPaddingTop = paddingTop;
            this.mChanged = true;
        }
    }

    public void setPaddingBottom(float paddingBottom) {
        if (this.mPaddingBottom != paddingBottom) {
            this.mPaddingBottom = paddingBottom;
            this.mChanged = true;
        }
    }

    public float getPaddingTop() {
        return this.mPaddingTop;
    }

    public float getPaddingBottom() {
        return this.mPaddingBottom;
    }

    public float getHorizontalMargin() {
        return this.mLeftMargin;
    }

    public float getLineHeight() {
        return this.mLineHeight;
    }

    public float getLineSpacing() {
        return this.mLineSpacing;
    }

    public float getTextSize() {
        return this.mTextSize;
    }

    public int getTextColor() {
        if (this.mView != null) {
            return this.mView.getCurrentTextColor();
        }
        return Theme.getColor(this.mTextColorArray);
    }

    public void setTextColorResId(int resId) {
        if (resId > 0) {
            this.mTextColorArray = resId;
        }
    }

    public void setTextSizeRatio(float ratio) {
        if (this.mTextSizeRatio != ratio) {
            this.mTextSizeRatio = ratio;
            this.mChanged = true;
        }
    }

    public float getTextSizeRatio() {
        return this.mTextSizeRatio;
    }

    public int getPagableLineNum(int startLine, float desiredOffset) {
        if (this.mType == 2) {
            return 0;
        }
        if (this.mType == 1) {
            return this.mCodeBlock.getPagableLineNum(startLine, desiredOffset);
        }
        float offset = SELECTION_PIN_LINE_WIDTH;
        int lineNum = startLine;
        for (int i = startLine; i < this.mLineArray.size(); i++) {
            Line line = (Line) this.mLineArray.get(i);
            if (line.lineHeight + offset > desiredOffset) {
                return lineNum;
            }
            lineNum++;
            offset += line.lineHeight;
        }
        return lineNum;
    }

    public boolean isPagable() {
        return true;
    }

    public final float getHeight() {
        return getHeight(0);
    }

    public final float getHeight(int startLine) {
        return (calculateHeight(startLine) + this.mPaddingTop) + this.mPaddingBottom;
    }

    public final float getMinHeight() {
        return (calculateMinHeight() + this.mPaddingTop) + this.mPaddingBottom;
    }

    protected float calculateMinHeight() {
        return calculateHeight(0);
    }

    public float getMinWidth() {
        if (needRegenerate()) {
            generate();
        }
        if (this.mType == 0) {
            int lineCount = getLineCount();
            if (lineCount == 0) {
                return this.mLeftMargin * 2.0f;
            }
            if (lineCount == 1) {
                float width = (this.mLeftMargin * 2.0f) + this.mTextAreaLeftMargin;
                try {
                    return width + ((Line) this.mLineArray.get(0)).getMinWidth();
                } catch (Exception e) {
                    return width;
                }
            }
        }
        return this.mWidth;
    }

    public int getLineCount() {
        if (needRegenerate()) {
            generate();
        }
        return this.mLineArray.size();
    }

    public void setType(int type) {
        this.mType = type;
    }

    public int getType() {
        return this.mType;
    }

    public void setText(CharSequence text) {
        this.mText = new SpannableString(text);
        Matcher matcher = sWesternPattern.matcher(text);
        while (matcher.find()) {
            this.mText.setSpan(new EnglishSpan(), matcher.start(), matcher.end(), 33);
        }
        matcher = sNonBmpPattern.matcher(text);
        while (matcher.find()) {
            this.mText.setSpan(new EnglishSpan(), matcher.start(), matcher.end(), 33);
        }
        matcher = sUrlPattern.matcher(text);
        while (matcher.find()) {
            this.mText.setSpan(new UrlSpan(), matcher.start(), matcher.end(), 33);
        }
        matcher = sEmailPattern.matcher(text);
        while (matcher.find()) {
            this.mText.setSpan(new EmailSpan(), matcher.start(), matcher.end(), 33);
        }
        this.mChanged = true;
        generatePrintableText();
    }

    public String toString() {
        return String.format("[%d %s]", new Object[]{Integer.valueOf(this.mParagraphId), StringUtils.truncate(getText(), 20)});
    }

    public CharSequence getText() {
        return this.mText;
    }

    public CharSequence getPrintableText() {
        return getPrintableText(0);
    }

    public CharSequence getPrintableText(int charOffset) {
        return getPrintableText(charOffset, AdvancedShareActionProvider.WEIGHT_MAX);
    }

    public CharSequence getPrintableText(int startOffset, int endOffset) {
        return Table.STRING_DEFAULT_VALUE;
    }

    public void generatePrintableText() {
        if (TextUtils.isEmpty(this.mText)) {
            this.mPrintableText = new SpannableString(Table.STRING_DEFAULT_VALUE);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(this.mText);
        for (BaseSpan[] spans = (BaseSpan[]) builder.getSpans(0, builder.length(), FootnoteSpan.class); spans.length > 0; spans = (BaseSpan[]) builder.getSpans(0, builder.length(), FootnoteSpan.class)) {
            builder.delete(builder.getSpanStart(spans[0]), builder.getSpanEnd(spans[0]));
        }
        if (getType() == 1) {
            builder.setSpan(new CodeSpan(), 0, builder.length(), 33);
        }
        if (this.mQuoteFlag) {
            builder.setSpan(new RegularScriptSpan(), 0, builder.length(), 33);
        }
        if (this.mBoldFlag) {
            builder.setSpan(new EmphasizeSpan(), 0, builder.length(), 33);
        }
        this.mPrintableText = new SpannableString(builder);
    }

    public int getCharOffsetByLineNum(int lineNum) {
        if (needRegenerate()) {
            generate();
        }
        if (this.mType == 1) {
            return this.mCodeBlock.getCharOffsetByLineNum(lineNum);
        }
        if (this.mType == 0) {
            try {
                Line line = (Line) this.mLineArray.get(lineNum);
                if (!(line == null || line.textrunList.isEmpty())) {
                    return ((TextRun) line.textrunList.get(0)).start;
                }
            } catch (Exception e) {
                Logger.e(TAG, e);
            }
        }
        return 0;
    }

    public int getLineNumByCharOffset(int charOffset) {
        if (needRegenerate()) {
            generate();
        }
        int linenum = 0;
        while (linenum < this.mLineArray.size() && getCharOffsetByLineNum(linenum) <= charOffset) {
            linenum++;
        }
        return linenum - 1;
    }

    @Nullable
    public Line getLine(int lineNum) {
        try {
            return (Line) this.mLineArray.get(lineNum);
        } catch (Throwable th) {
            return null;
        }
    }

    public void setBold(boolean bold) {
        if (this.mBoldFlag != bold) {
            this.mBoldFlag = bold;
            this.mChanged = true;
            generatePrintableText();
        }
    }

    public void setIndent(boolean indent) {
        if (this.mIndentFlag != indent) {
            this.mIndentFlag = indent;
            this.mChanged = true;
        }
    }

    public void setQuote(boolean quote) {
        if (this.mQuoteFlag != quote) {
            this.mQuoteFlag = quote;
            this.mChanged = true;
            generatePrintableText();
        }
    }

    public void setBlockQuote(boolean blockQuote, @ColorRes @ArrayRes int quoteLineColorRes) {
        if (blockQuote) {
            setDecorator(new LineQuoteDecorator(quoteLineColorRes));
        }
    }

    public void setBlockQuote(boolean blockQuote) {
        setBlockQuote(blockQuote, -1);
    }

    public void setIsBulletItem(boolean isBulletItem) {
        if (isBulletItem) {
            setDecorator(new BulletDecorator());
        }
    }

    public void setDecorator(Decorator decorator) {
        if (this.mDecorator == null || !this.mDecorator.equals(decorator)) {
            decorator.setParagraph(this);
            this.mDecorator = decorator;
            this.mChanged = true;
        }
    }

    public void setAlign(Align align) {
        if (this.mAlign != align) {
            this.mAlign = align;
            this.mChanged = true;
        }
    }

    public void setGravity(int gravity) {
        int xShift = (gravity >> 0) & 6;
        if (xShift == 2) {
            setAlign(Align.LEFT);
        } else if (xShift == 4) {
            setAlign(Align.RIGHT);
        } else {
            setAlign(Align.CENTER);
        }
    }

    public void setFirstLineIndent(boolean indent) {
        if (this.mFirstLineIndentFlag != indent) {
            this.mFirstLineIndentFlag = indent;
            this.mChanged = true;
        }
    }

    public void setJustify(boolean justify) {
        if (this.mJustifyFlag != justify) {
            this.mJustifyFlag = justify;
            this.mChanged = true;
        }
    }

    public void setLineLimit(int count) {
        if (this.mLineLimit != count) {
            this.mLineLimit = count;
            this.mChanged = true;
        }
    }

    public boolean getEllipsed() {
        return this.mEllipsed;
    }

    public final void draw(Canvas canvas, float offsetY) {
        draw(canvas, SELECTION_PIN_LINE_WIDTH, offsetY, 0, 0);
    }

    public final void draw(Canvas canvas, float offsetX, float offsetY) {
        draw(canvas, offsetX, offsetY, 0, 0);
    }

    public final void draw(Canvas canvas, float offsetX, float offsetY, int startLine, int endLine) {
        if (needRegenerate()) {
            generate();
        }
        if (DebugSwitch.on(Key.APP_DEBUG_SHOW_PARAGRAPH_MARGINS)) {
            Paint marginPaint = new Paint();
            marginPaint.setColor(hashCode() & -285212673);
            marginPaint.setTextSize(14.0f);
            marginPaint.setAntiAlias(true);
            marginPaint.setTypeface(Typeface.MONOSPACE);
            canvas.drawRect(this.mTextAreaLeftMargin + this.mLeftMargin, offsetY, (this.mLeftMargin + this.mTextAreaLeftMargin) + this.mTextAreaWidth, offsetY + getHeight(startLine), marginPaint);
            PaintUtils.recyclePaint(marginPaint);
        }
        if (DebugSwitch.on(Key.APP_DEBUG_SHOW_PARAGRAPH_IDS)) {
            Paint sectionIdPaint = PaintUtils.obtainPaint();
            sectionIdPaint.setTypeface(Typeface.MONOSPACE);
            sectionIdPaint.setColor(SupportMenu.CATEGORY_MASK);
            sectionIdPaint.setTextSize(10.0f);
            canvas.drawText(String.valueOf(this.mParagraphId), this.mLeftMargin + 2.0f, 10.0f + offsetY, sectionIdPaint);
            PaintUtils.recyclePaint(sectionIdPaint);
        }
        if (this.mDecorator != null) {
            canvas.save();
            canvas.translate(((this.mLeftMargin + offsetX) + this.mTextAreaLeftMargin) - this.mDecorator.getInsetLeft(), offsetY);
            this.mDecorator.draw(canvas, startLine, endLine);
            canvas.restore();
        }
        onDraw(canvas, offsetX, offsetY + this.mPaddingTop, startLine, endLine);
    }

    public synchronized void generate() {
        try {
            generateWithThrow();
        } catch (InterruptedException e) {
            Logger.e(TAG, e);
        }
    }

    public synchronized void generateWithThrow() throws InterruptedException {
        if (needRegenerate()) {
            if (shouldShowTypesettingLog()) {
                Object[] objArr = new Object[1];
                objArr[0] = Integer.valueOf(this.mParagraphId);
                Logger.d(Tag.TYPESETTING, "--- generate paragraph %s start ---", objArr);
            }
            if (this.mWidth <= SELECTION_PIN_LINE_WIDTH) {
                setWidth(PageMetrics.getLast().width);
            }
            initMarginSize();
            this.mEllipsed = false;
            if (this.mType == 1) {
                this.mCodeBlock = new Code();
                this.mCodeBlock.setText(this.mText.toString());
                this.mCodeBlock.setLeftMargin(this.mLeftMargin);
                this.mCodeBlock.setWidth(this.mTextAreaWidth);
                this.mCodeBlock.setTextSize(this.mTextSize * CODE_TEXTSIZE_RATIO);
            } else {
                if (this.mFirstLineIndentFlag && this.mAlign == Align.LEFT) {
                    this.mAllocatedX = this.mTextSize * 2.0f;
                } else {
                    this.mAllocatedX = SELECTION_PIN_LINE_WIDTH;
                }
                if (this.mAlign == Align.LEFT) {
                    try {
                        if (CharUtils.isFullWidthStartPunctuation(this.mText.charAt(0))) {
                            this.mAllocatedX -= this.mTextSize * CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO;
                        }
                    } catch (Exception e) {
                    }
                }
                this.mLineArray.clear();
                Line line = appendNewLine();
                line.lineHeight = this.mLineHeight;
                line.x = this.mAllocatedX;
                this.mParsedTextPos = 0;
                this.mFootnoteLength = 0;
                int limit = this.mText.length();
                int end;
                for (int start = 0; start < limit; start = end) {
                    throwIfInterrupted();
                    end = this.mText.nextSpanTransition(start, limit, BaseSpan.class);
                    if (end > start) {
                        CharSequence text = this.mText.subSequence(start, end);
                        List<BaseSpan> styleList = appendParagraphStyles(Arrays.asList(this.mText.getSpans(start, end, BaseSpan.class)));
                        if (shouldShowTypesettingLog()) {
                            StringBuilder builder = new StringBuilder();
                            builder.append("--- text = ").append(text).append("\n").append("--- styleList =");
                            for (BaseSpan style : styleList) {
                                builder.append(" ").append(style.getClass().getSimpleName());
                            }
                            Logger.d(Tag.TYPESETTING, builder.toString(), new Object[0]);
                        }
                        BaseSpan span;
                        if (SpanUtils.hasSpan(styleList, FootnoteSpan.class)) {
                            span = SpanUtils.getSpan(styleList, FootnoteSpan.class);
                            if (span instanceof FootnoteSpan) {
                                end = this.mText.getSpanEnd(span);
                                generateFootnote(this.mText.subSequence(start, end));
                            }
                        } else if (SpanUtils.hasSpan(styleList, ContentSpan.class)) {
                            for (BaseSpan span2 : styleList) {
                                end = this.mText.getSpanEnd(span2);
                                text = this.mText.subSequence(start, end);
                                if (!(span2 instanceof LatexSpan)) {
                                    if (span2 instanceof CodeSpan) {
                                        generateCode(text.toString());
                                        break;
                                    }
                                }
                                generateLatex(text.toString());
                                break;
                            }
                        } else {
                            generateLines(text.toString(), styleList);
                        }
                    }
                }
                float remainedWidth = this.mTextAreaWidth - this.mAllocatedX;
                if (!this.mLineArray.isEmpty()) {
                    line = (Line) this.mLineArray.get(this.mLineArray.size() - 1);
                    if (remainedWidth < SELECTION_PIN_LINE_WIDTH) {
                        try {
                            TextRun lastTextRun = (TextRun) line.textrunList.get(line.textrunList.size() - 1);
                            if (!SpanUtils.hasSpan(lastTextRun.styles(), FootnoteSpan.class)) {
                                char lastChar = this.mText.charAt((lastTextRun.start + lastTextRun.len) - 1);
                                if (Character.isWhitespace(lastChar)) {
                                    remainedWidth += this.mEnglishSpaceWidth;
                                }
                                if (CharUtils.isFullWidthEndPunctuation(lastChar)) {
                                    remainedWidth += this.mTextSize * CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO;
                                }
                            }
                        } catch (Exception e2) {
                        }
                    }
                    line.totalSpace = remainedWidth;
                    if (remainedWidth < SELECTION_PIN_LINE_WIDTH) {
                        calculateStretchForLine(line);
                    }
                    if (this.mAlign == Align.RIGHT) {
                        line.x += line.totalSpace;
                    } else if (this.mAlign == Align.CENTER) {
                        line.x += line.totalSpace / 2.0f;
                    }
                }
            }
            this.mGeneratedScale = this.mApp.getScale();
            this.mChanged = false;
        }
    }

    private void generateLines(String text, List<BaseSpan> styleList) throws InterruptedException {
        if (shouldShowTypesettingLog()) {
            Logger.d(Tag.TYPESETTING, "--- generateLines for %s", text);
        }
        Line line = getLastLine();
        TextRun textrun = null;
        int end = text.length();
        int mark = 0;
        boolean isLink = SpanUtils.hasSpan(styleList, LinkSpan.class);
        boolean isEnglish = SpanUtils.hasSpan(styleList, EnglishSpan.class);
        Paint paint = PaintUtils.obtainPaint(this.mTextSize, styleList);
        float hyphenWidth = paint.measureText("-");
        BreakIterator iterator = BreakIteratorUtils.getLineBreakIterator();
        iterator.setText(text);
        int start = iterator.first();
        while (true) {
            String str;
            if (end >= mark) {
                end = iterator.next();
                if (end == -1) {
                    PaintUtils.recyclePaint(paint);
                    BreakIteratorUtils.recycleLineBreakIterator(iterator);
                    return;
                }
            }
            if (end < mark && start == end) {
                end = mark;
            }
            int wordLen = end - start;
            try {
                str = text.substring(start, end);
            } catch (Exception e) {
                Logger.e(TAG, e);
                str = Table.STRING_DEFAULT_VALUE;
            }
            throwIfInterrupted();
            if (textrun == null) {
                textrun = TextRun.newInstance(styleList);
                textrun.mText = this.mPrintableText;
                line.addTextRun(textrun);
                textrun.start = this.mParsedTextPos;
                textrun.start -= this.mFootnoteLength;
                textrun.len = 0;
                textrun.width = SELECTION_PIN_LINE_WIDTH;
            }
            float wordWidth = paint.measureText(str);
            if (!canFixInCurrentLine(wordWidth, str)) {
                int index = CharUtils.getBreakDownIndex(str);
                if (index < str.length()) {
                    mark = end;
                    end = start + index;
                } else {
                    if (isEnglish && !isLink && CharUtils.shouldHyphenate(str)) {
                        index = CharUtils.getHyphenateIndexByWidth(str, paint, (this.mTextAreaWidth - this.mAllocatedX) - hyphenWidth);
                        if (index < str.length()) {
                            mark = end;
                            end = start + index;
                            textrun.mark = 1;
                        }
                    }
                    if (wordWidth > this.mTextAreaWidth) {
                        mark = end;
                        end = start + PaintUtils.breakText(paint, text.substring(start), this.mTextAreaWidth);
                    } else if (wordLen == 1 && Character.isWhitespace(text.charAt(start))) {
                        start++;
                        this.mParsedTextPos++;
                    } else {
                        int i;
                        boolean isAfterHyphen;
                        float remainedWidth = this.mTextAreaWidth - this.mAllocatedX;
                        try {
                            char c = textrun.lastChar();
                            if (Character.isWhitespace(c)) {
                                remainedWidth += this.mEnglishSpaceWidth;
                            }
                            if (!isEnglish && CharUtils.isFullWidthEndPunctuation(c)) {
                                remainedWidth += this.mTextSize * CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO;
                            }
                        } catch (Exception e2) {
                        }
                        if (this.mJustifyFlag) {
                            if (this.mLineLimit > 0 && this.mLineArray.size() == this.mLineLimit) {
                                remainedWidth -= this.mTextSize * 1.5f;
                                this.mEllipsed = true;
                            }
                            i = textrun.mark;
                            if (r0 == 1) {
                                remainedWidth -= hyphenWidth;
                            }
                            line.totalSpace = remainedWidth;
                        }
                        calculateStretchForLine(line);
                        i = textrun.mark;
                        if (r0 == 1) {
                            isAfterHyphen = true;
                        } else {
                            isAfterHyphen = false;
                        }
                        if (shouldShowTypesettingLog()) {
                            Logger.d(Tag.TYPESETTING, textrun.toString(), new Object[0]);
                        }
                        line = appendNewLine();
                        line.lineHeight = this.mLineHeight;
                        textrun = TextRun.newInstance(styleList);
                        textrun.mText = this.mPrintableText;
                        line.addTextRun(textrun);
                        textrun.start = this.mParsedTextPos;
                        textrun.start -= this.mFootnoteLength;
                        textrun.len = 0;
                        textrun.width = SELECTION_PIN_LINE_WIDTH;
                        if (isAfterHyphen) {
                            textrun.mark = 2;
                        }
                        this.mAllocatedX = SELECTION_PIN_LINE_WIDTH;
                        try {
                            if (CharUtils.isFullWidthStartPunctuation(textrun.firstChar())) {
                                this.mAllocatedX -= this.mTextSize * CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO;
                                line.x -= this.mTextSize * CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO;
                            }
                        } catch (Exception e3) {
                        }
                    }
                }
            }
            textrun.len += wordLen;
            textrun.width += wordWidth;
            this.mAllocatedX += wordWidth;
            this.mParsedTextPos += wordLen;
            if (shouldShowTypesettingLog()) {
                Logger.d(Tag.TYPESETTING, textrun.toString(), new Object[0]);
            }
            start = end;
        }
    }

    private void generateFootnote(CharSequence text) {
        if (shouldShowTypesettingLog()) {
            Logger.d(Tag.TYPESETTING, "--- generateFootnote for %s", text);
        }
        Line line = getLastLine();
        FootnoteTextRun textrun = new FootnoteTextRun();
        textrun.mText = this.mText;
        line.addTextRun(textrun);
        float bmWidth = textrun.getWidth();
        textrun.start = this.mParsedTextPos;
        textrun.len = text.length();
        textrun.width += bmWidth;
        this.mAllocatedX += bmWidth;
        this.mParsedTextPos += textrun.len;
        this.mFootnoteLength += textrun.len;
        if (shouldShowTypesettingLog()) {
            Logger.d(Tag.TYPESETTING, textrun.toString(), new Object[0]);
        }
        float remainedWidth = this.mTextAreaWidth - this.mAllocatedX;
        if (this.mJustifyFlag) {
            if (this.mLineLimit > 0 && this.mLineArray.size() == this.mLineLimit) {
                remainedWidth -= this.mTextSize * 1.5f;
                this.mEllipsed = true;
            }
            if (remainedWidth < SELECTION_PIN_LINE_WIDTH) {
                line.totalSpace = remainedWidth;
                calculateStretchForLine(line);
            }
        }
    }

    private void generateLatex(String text) throws InterruptedException {
        if (shouldShowTypesettingLog()) {
            Logger.d(Tag.TYPESETTING, "--- generateLatex for %s", text);
        }
        Line line = getLastLine();
        Formula formula = Formula.parseLatex(text);
        formula.setBaseTextSize(this.mTextSize);
        float remainedWidth = this.mTextAreaWidth - this.mAllocatedX;
        int totalStart = this.mParsedTextPos - this.mFootnoteLength;
        int totalLen = text.length();
        this.mParsedTextPos += totalLen;
        int subSeqFormula = 0;
        while (!formula.isEmpty()) {
            boolean needShrink = false;
            throwIfInterrupted();
            int offset = formula.getSubFormulaOffsetByWidth(remainedWidth);
            if (offset == 0) {
                if (this.mJustifyFlag) {
                    if (this.mLineLimit > 0) {
                        int size = this.mLineArray.size();
                        int i = this.mLineLimit;
                        if (size == r0) {
                            remainedWidth -= this.mTextSize * 1.5f;
                            this.mEllipsed = true;
                        }
                    }
                    line.totalSpace = remainedWidth;
                }
                calculateStretchForLine(line);
                line = appendNewLine();
                this.mAllocatedX = SELECTION_PIN_LINE_WIDTH;
                remainedWidth = this.mTextAreaWidth;
                offset = formula.getSubFormulaOffsetByWidth(remainedWidth);
                if (offset == 0) {
                    offset = formula.getSubFormulaOffsetByWidth(remainedWidth, true);
                    needShrink = true;
                }
            }
            Formula subFormula = formula;
            if (offset < formula.getNodeCount()) {
                subFormula = formula.subFormula(0, offset);
            }
            float displayRatio = SELECTION_PIN_LINE_WIDTH;
            if (needShrink) {
                displayRatio = this.mTextAreaWidth / subFormula.getWidth();
                subFormula.setDisplayRatio(displayRatio);
            }
            float formulaWidth = subFormula.getWidth();
            float formulaHeight = subFormula.getHeight();
            LatexTextRun textrun = new LatexTextRun();
            textrun.mText = this.mPrintableText;
            line.addTextRun(textrun);
            float newHeight = formulaHeight + this.mLineSpacing;
            if (newHeight > line.lineHeight) {
                line.lineHeight = newHeight;
            }
            textrun.totalStart = totalStart;
            textrun.totalLen = totalLen;
            textrun.start = totalStart + subSeqFormula;
            textrun.len = 1;
            subSeqFormula++;
            textrun.latex = subFormula.toString();
            textrun.width += formulaWidth;
            textrun.displayRatio = displayRatio;
            this.mAllocatedX += formulaWidth;
            remainedWidth = this.mTextAreaWidth - this.mAllocatedX;
            formula = formula.subFormula(offset);
        }
    }

    private void generateCode(String text) throws InterruptedException {
        if (shouldShowTypesettingLog()) {
            Logger.d(Tag.TYPESETTING, "--- generateCode for %s", text);
        }
        Line line = getLastLine();
        Code code = new Code();
        code.setInlineMode(true);
        code.setTextSize(this.mTextSize * CODE_TEXTSIZE_RATIO);
        code.setText(text);
        float remainedWidth = this.mTextAreaWidth - this.mAllocatedX;
        int totalStart = this.mParsedTextPos - this.mFootnoteLength;
        int totalLen = text.length();
        while (!TextUtils.isEmpty(text)) {
            throwIfInterrupted();
            code.setWidth(remainedWidth);
            int offset = code.getBreakUpPositionByWidth(text, remainedWidth);
            if (offset == 0) {
                if (this.mJustifyFlag) {
                    if (this.mLineLimit > 0 && this.mLineArray.size() == this.mLineLimit) {
                        remainedWidth -= this.mTextSize * 1.5f;
                        this.mEllipsed = true;
                    }
                    line.totalSpace = remainedWidth;
                }
                calculateStretchForLine(line);
                line = appendNewLine();
                this.mAllocatedX = SELECTION_PIN_LINE_WIDTH;
                remainedWidth = this.mTextAreaWidth;
                offset = code.getBreakUpPositionByWidth(text, remainedWidth);
                if (offset == 0) {
                    offset = code.getBreakUpPositionByWidth(text, remainedWidth, false);
                }
            }
            code.setText(text.substring(0, offset));
            float codeWidth = code.getWidth();
            CodeTextRun textrun = new CodeTextRun();
            textrun.mText = this.mPrintableText;
            line.addTextRun(textrun);
            line.lineHeight = this.mLineHeight;
            textrun.totalStart = totalStart;
            textrun.totalLen = totalLen;
            textrun.start = this.mParsedTextPos;
            textrun.start -= this.mFootnoteLength;
            textrun.len = offset;
            textrun.width += codeWidth;
            this.mParsedTextPos += textrun.len;
            this.mAllocatedX += codeWidth;
            remainedWidth = this.mTextAreaWidth - this.mAllocatedX;
            text = text.substring(offset);
        }
    }

    private List<BaseSpan> appendParagraphStyles(List<BaseSpan> list) {
        if (!this.mQuoteFlag && !this.mBoldFlag) {
            return list;
        }
        ArrayList<BaseSpan> styleList = new ArrayList(list);
        if (this.mQuoteFlag) {
            styleList.add(this.mRegularScriptSpan);
        }
        if (this.mBoldFlag) {
            styleList.add(this.mEmphasizeSpan);
        }
        return styleList;
    }

    private Line getLastLine() {
        if (!this.mLineArray.isEmpty()) {
            return (Line) this.mLineArray.get(this.mLineArray.size() - 1);
        }
        Line line = appendNewLine();
        line.lineHeight = this.mLineHeight;
        return line;
    }

    private Line appendNewLine() {
        Line line = new Line();
        this.mLineArray.add(line);
        if (shouldShowTypesettingLog()) {
            Logger.d(Tag.TYPESETTING, "------------ new line ---------", new Object[0]);
        }
        return line;
    }

    private void calculateStretchForLine(Line line) {
        if (line.getStretchPointCount() > 0) {
            line.stretch = line.totalSpace / ((float) line.getStretchPointCount());
        } else {
            line.stretch = SELECTION_PIN_LINE_WIDTH;
        }
    }

    private boolean canFixInCurrentLine(float wordWidth, String str) {
        if (str.length() == 0) {
            return true;
        }
        float textAreaWidth = this.mTextAreaWidth;
        if (this.mLineLimit > 0 && this.mLineArray.size() == this.mLineLimit) {
            textAreaWidth -= this.mTextSize * 1.5f;
        }
        if (this.mAllocatedX + wordWidth <= textAreaWidth) {
            return true;
        }
        if (this.mJustifyFlag && this.mAllocatedX + wordWidth <= this.mTextSize + textAreaWidth) {
            switch (Character.getType(str.charAt(str.length() - 1))) {
                case se.emilsjolander.stickylistheaders.R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                case se.emilsjolander.stickylistheaders.R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                case se.emilsjolander.stickylistheaders.R.styleable.StickyListHeadersListView_android_cacheColorHint /*14*/:
                case se.emilsjolander.stickylistheaders.R.styleable.StickyListHeadersListView_android_fastScrollAlwaysVisible /*20*/:
                case se.emilsjolander.stickylistheaders.R.styleable.StickyListHeadersListView_stickyListHeadersListViewStyle /*22*/:
                case se.emilsjolander.stickylistheaders.R.styleable.StickyListHeadersListView_hasStickyHeaders /*23*/:
                case se.emilsjolander.stickylistheaders.R.styleable.StickyListHeadersListView_isDrawingListUnderStickyHeader /*24*/:
                case HeaderMapDB.SERIALIZER_INT_ARRAY /*30*/:
                    return true;
            }
        }
        return false;
    }

    boolean needRegenerate() {
        if (!this.mChanged && this.mGeneratedScale == this.mApp.getScale()) {
            return false;
        }
        return true;
    }

    public void setBaseLeftMarginDip(float margin) {
        this.mBaseLeftMargin = (float) Utils.dp2pixel(margin);
    }

    public void setBaseLeftMargin(float margin) {
        this.mBaseLeftMargin = margin;
    }

    public void setBlockIndentRatio(float indentRatio) {
        this.mBlockIndentRatio = indentRatio;
    }

    public ArrayList<Touchable> getTouchableArray() {
        return getTouchableArray(0, getLineCount());
    }

    public ArrayList<Touchable> getTouchableArray(int startLine, int endLine) {
        ArrayList<Touchable> result = new ArrayList();
        if (getType() == 2) {
            result.add(((IllusParagraph) this).getTouchable(this.mWidth));
        } else {
            if (startLine < 0 || startLine >= this.mLineArray.size()) {
                startLine = 0;
            }
            if (endLine <= 0 || endLine > this.mLineArray.size()) {
                endLine = this.mLineArray.size();
            }
            for (int i = startLine; i < endLine; i++) {
                Line line = (Line) this.mLineArray.get(i);
                synchronized (line.mTextRunLock) {
                    for (TextRun textrun : line.textrunList) {
                        Touchable touchable = textrun.getTouchable();
                        if (touchable != null) {
                            result.add(touchable);
                        }
                    }
                }
            }
        }
        return result;
    }

    private int getLineNumByPoint(float y, int startLine, int endLine) {
        y -= this.mLineSpacing / 2.0f;
        if (y < SELECTION_PIN_LINE_WIDTH) {
            return startLine - 1;
        }
        float totalHeight = SELECTION_PIN_LINE_WIDTH;
        for (int i = startLine; i < endLine; i++) {
            totalHeight += ((Line) this.mLineArray.get(i)).getLineHeight();
            if (totalHeight >= y) {
                return i;
            }
        }
        if (this.mLineHeight + totalHeight >= y) {
            endLine--;
        }
        return endLine;
    }

    public RectF getLineRect(int lineNum, int startLine) {
        if (lineNum < startLine) {
            return new RectF();
        }
        float totalHeight = SELECTION_PIN_LINE_WIDTH;
        RectF rect = new RectF();
        for (int i = startLine; i < lineNum; i++) {
            totalHeight += ((Line) this.mLineArray.get(i)).getLineHeight();
        }
        Line line = (Line) this.mLineArray.get(lineNum);
        float baseLine = ((line.getLineHeight() + totalHeight) - this.mLineDescent) - ((line.lineHeight - this.mLineHeight) / 2.0f);
        rect.top = this.mLineAscent + baseLine;
        rect.bottom = this.mLineDescent + baseLine;
        float centerY = rect.centerY();
        float verticalRadius = (line.lineHeight - (this.mLineSpacing * 0.5f)) * 0.5f;
        rect.top = centerY - verticalRadius;
        rect.bottom = centerY + verticalRadius;
        rect.left = (this.mLeftMargin + this.mTextAreaLeftMargin) + line.x;
        rect.right = rect.left + line.getLineWidth();
        if (CharUtils.isFullWidthStartPunctuation(line.firstChar())) {
            rect.left += this.mTextSize * CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO;
        }
        if (!CharUtils.isFullWidthEndPunctuation(line.lastChar())) {
            return rect;
        }
        rect.right -= this.mTextSize * CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO;
        return rect;
    }

    public int getOffsetByPoint(float x, float y, boolean jumpByWord, boolean isOffsetAfterThisWord, int startLine, int endLine) {
        int count = this.mLineArray.size();
        if (endLine == 0 || endLine >= count) {
            endLine = count;
        }
        int lineNum = getLineNumByPoint(y, startLine, endLine);
        if (lineNum < startLine) {
            return ((Line) this.mLineArray.get(startLine)).startOffset();
        }
        if (lineNum >= endLine) {
            return ((Line) this.mLineArray.get(endLine - 1)).endOffset();
        }
        Line line = (Line) this.mLineArray.get(lineNum);
        return line.getOffsetByPoint(this.mTextSize, ((x - this.mLeftMargin) - this.mTextAreaLeftMargin) - line.x, jumpByWord, isOffsetAfterThisWord);
    }

    public RectF getPinRectByOffset(int offset, boolean isEndPin, int startLine, int endLine) {
        int lineNum = getLineNumByCharOffset(offset);
        if (endLine == 0) {
            endLine = this.mLineArray.size();
        }
        if (lineNum < startLine || lineNum >= endLine) {
            return Constants.DUMMY_RECT;
        }
        Line line = (Line) this.mLineArray.get(lineNum);
        RectF rect = getLineRect(lineNum, startLine);
        float posX = ((this.mLeftMargin + this.mTextAreaLeftMargin) + line.x) + line.getPinStopByOffset(this.mTextSize, offset, isEndPin);
        float pinHalfWidth = SELECTION_PIN_LINE_WIDTH * 0.5f;
        rect.left = posX - pinHalfWidth;
        rect.right = posX + pinHalfWidth;
        return rect;
    }

    public HotArea getHotAreaByOffsetRange(int startOffset, int endOffset, int startLine, int endLine) {
        int startLineNum = getLineNumByCharOffset(startOffset);
        int endLineNum = getLineNumByCharOffset(endOffset);
        if (startLineNum >= this.mLineArray.size() || endLineNum >= this.mLineArray.size()) {
            return null;
        }
        HotArea hotArea = new HotArea();
        if (endLine == 0) {
            endLine = this.mLineArray.size();
        }
        int i = Math.max(startLine, startLineNum);
        while (i <= Math.min(endLineNum, endLine - 1) && i < this.mLineArray.size()) {
            Line line = (Line) this.mLineArray.get(i);
            RectF rect = getLineRect(i, startLine);
            if (i == startLineNum) {
                rect.left = ((this.mLeftMargin + this.mTextAreaLeftMargin) + line.x) + line.getPinStopByOffset(this.mTextSize, startOffset, false);
            }
            if (i == endLineNum) {
                rect.right = ((this.mLeftMargin + this.mTextAreaLeftMargin) + line.x) + line.getPinStopByOffset(this.mTextSize, endOffset, true);
            }
            if (!rect.isEmpty()) {
                hotArea.add(rect);
            }
            i++;
        }
        hotArea.setHalfLineSpacing(getLineSpacing() / 2.0f);
        return hotArea;
    }

    public RectF getRectByOffset(int offset, float offsetX, float offsetY, int startLine) {
        RectF rect = getLineRect(getLineNumByCharOffset(offset), startLine);
        if (rect != null) {
            rect.offset(offsetX, offsetY);
        }
        return rect;
    }

    public boolean canPinStop() {
        return (getId() == 0 || TextUtils.isEmpty(this.mText)) ? false : true;
    }

    private void throwIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException(String.format("generating for %s was interrupted.", new Object[]{this}));
        }
    }

    private boolean shouldShowTypesettingLog() {
        return false;
    }
}
