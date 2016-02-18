package com.douban.book.reader.content.paragraph;

import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.SparseArray;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.util.CharUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.PaintUtils;
import com.j256.ormlite.stmt.query.SimpleComparison;
import io.fabric.sdk.android.services.events.EventsFilesManager;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.internal.Table;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Formula {
    private static final int MAX_SYMBOL_SIZE = 4;
    private static final int MIN_SYMBOL_SIZE = 2;
    public static final float SIZE2_RATIO = 1.95f;
    public static final float SIZE3_RATIO = 2.5f;
    public static final float SIZE4_RATIO = 3.11f;
    private static final String TAG = "Formula";
    private static HashMap<String, String> sCodes;
    private static Typeface sMathTypeface;
    private static SparseArray<Typeface> sSizedTypefaces;
    private float mBaseTextSize;
    private boolean mIsRootFormula;
    private List<Node> mNodeList;
    private float mScale;
    private int mTextColor;

    public static class Node {
        public static final int SUB = -1;
        public static final int SUP = -2;
        private static ArrayList<String> sSymbolsNeedMargin;
        public float mBaseStrokeWidth;
        private float mBaseTextSize;
        private String mData;
        private float mDisplayRatio;
        private List<Formula> mParamList;
        private float mScale;
        private Formula mSub;
        private Formula mSup;
        private int mTextColor;
        private String mType;
        private int mTypefaceSize;

        public Node() {
            this.mType = Table.STRING_DEFAULT_VALUE;
            this.mData = Table.STRING_DEFAULT_VALUE;
            this.mTypefaceSize = 1;
            this.mSub = null;
            this.mSup = null;
            this.mParamList = null;
            this.mScale = 1.0f;
            this.mDisplayRatio = 1.0f;
            this.mBaseTextSize = 40.0f;
            this.mBaseStrokeWidth = 2.0f;
            this.mTextColor = 0;
        }

        public void setType(String type) {
            this.mType = type;
        }

        public void setData(String data) {
            this.mData = data;
        }

        public void setTypefaceSize(int size) {
            if (size >= Formula.MIN_SYMBOL_SIZE && size <= Formula.MAX_SYMBOL_SIZE) {
                this.mTypefaceSize = size;
            }
        }

        public int getTypefaceSize() {
            return this.mTypefaceSize;
        }

        public void setScale(float scale) {
            this.mScale = scale;
        }

        public float getScale() {
            return this.mScale;
        }

        public void setBaseTextSize(float baseTextSize) {
            this.mBaseTextSize = baseTextSize;
            this.mBaseStrokeWidth = Math.max(1.0f, this.mBaseTextSize / 20.0f);
            getParam(SUB).setBaseTextSize(baseTextSize);
            getParam(SUP).setBaseTextSize(baseTextSize);
            if (this.mParamList != null) {
                for (Formula param : this.mParamList) {
                    param.setBaseTextSize(baseTextSize);
                }
            }
        }

        public void setTextColor(int color) {
            this.mTextColor = color;
            getParam(SUB).setTextColor(color);
            getParam(SUP).setTextColor(color);
            if (this.mParamList != null) {
                for (Formula param : this.mParamList) {
                    param.setTextColor(color);
                }
            }
        }

        public void setDisplayRatio(float ratio) {
            this.mDisplayRatio = ratio;
            getParam(SUB).setDisplayRatio(ratio);
            getParam(SUP).setDisplayRatio(ratio);
            if (this.mParamList != null) {
                for (Formula param : this.mParamList) {
                    param.setDisplayRatio(ratio);
                }
            }
        }

        public void addSub(Formula sub) {
            this.mSub = sub;
        }

        public void addSup(Formula sup) {
            this.mSup = sup;
        }

        public void addParam(Formula param) {
            if (this.mParamList == null) {
                this.mParamList = new ArrayList();
            }
            this.mParamList.add(param);
        }

        public Formula getParam() {
            return getParam(0);
        }

        public Formula getParam(int index) {
            Formula ret = null;
            if (index == SUB) {
                ret = this.mSub;
            } else if (index == SUP) {
                ret = this.mSup;
            } else if (this.mParamList != null && this.mParamList.size() > index) {
                ret = (Formula) this.mParamList.get(index);
            }
            if (ret == null) {
                return new Formula();
            }
            return ret;
        }

        private float getTextWidth() {
            float margin = 0.0f;
            String str = this.mData;
            if (TextUtils.isEmpty(str)) {
                str = this.mType;
                if (getParam(SUB).isEmpty() && getParam(SUP).isEmpty()) {
                    margin = (this.mBaseTextSize * this.mScale) * 0.1f;
                }
            }
            if (TextUtils.isEmpty(str)) {
                return 0.0f;
            }
            Paint paint = PaintUtils.obtainPaint();
            paint.setTextSize(getTextSize());
            if (getTypefaceSize() > 1) {
                paint.setTypeface(Formula.getSizedTypeface(getTypefaceSize()));
            } else {
                paint.setTypeface(Formula.sMathTypeface);
            }
            float result = paint.measureText(str) + margin;
            PaintUtils.recyclePaint(paint);
            return result;
        }

        private float getTextHeight() {
            float textHeight = this.mBaseTextSize * this.mScale;
            if (getTypefaceSize() == Formula.MAX_SYMBOL_SIZE) {
                return textHeight * Formula.SIZE4_RATIO;
            }
            if (getTypefaceSize() == 3) {
                return textHeight * Formula.SIZE3_RATIO;
            }
            if (getTypefaceSize() == Formula.MIN_SYMBOL_SIZE) {
                return textHeight * Formula.SIZE2_RATIO;
            }
            return textHeight;
        }

        static {
            sSymbolsNeedMargin = new ArrayList(Arrays.asList(new String[]{"+", "-", "/", SimpleComparison.EQUAL_TO_OPERATION, SimpleComparison.GREATER_THAN_OPERATION, SimpleComparison.LESS_THAN_OPERATION, "times", "cdot", ",", "approx", "approxeq", "leqq", "leq", "le", "geqq", "geq", "ge"}));
        }

        public float getMargin() {
            if (sSymbolsNeedMargin.contains(this.mData) || sSymbolsNeedMargin.contains(this.mType)) {
                return (this.mBaseTextSize * this.mScale) * 0.1f;
            }
            return 0.0f;
        }

        public float getWidth() {
            float width0;
            float width1;
            if (this.mType.equals("frac")) {
                width0 = getParam(0).getWidth();
                width1 = getParam(1).getWidth();
                if (width0 <= width1) {
                    width0 = width1;
                }
                return width0;
            } else if (this.mType.equals("sum")) {
                maxWidth = getTextWidth();
                subWidth = getParam(SUB).getWidth();
                if (subWidth > maxWidth) {
                    maxWidth = subWidth;
                }
                supWidth = getParam(SUP).getWidth();
                if (supWidth > maxWidth) {
                    return supWidth;
                }
                return maxWidth;
            } else if (this.mType.equals("sqrt")) {
                float width = getParam(0).getWidth() + getTextWidth();
                supWidth = getParam(SUP).getWidth();
                if (supWidth > getTextWidth() / 1.5f) {
                    width += supWidth - (getTextWidth() / 1.5f);
                }
                return width;
            } else if (this.mType.equals("overline")) {
                return getParam(0).getWidth();
            } else {
                if (this.mType.equals("int")) {
                    float textWidth = getTextWidth();
                    subWidth = getParam(SUB).getWidth() + (textWidth * 1.5f);
                    supWidth = getParam(SUP).getWidth() + textWidth;
                    if (subWidth > supWidth) {
                        maxWidth = subWidth;
                    } else {
                        maxWidth = supWidth;
                    }
                    return maxWidth;
                }
                width0 = getParam(SUB).getWidth();
                width1 = getParam(SUP).getWidth();
                if (width0 > width1) {
                    maxWidth = width0;
                } else {
                    maxWidth = width1;
                }
                if (!(this.mParamList == null || this.mParamList.isEmpty())) {
                    for (Formula param : this.mParamList) {
                        maxWidth += param.getWidth();
                    }
                }
                return (getTextWidth() + maxWidth) + (getMargin() * 2.0f);
            }
        }

        public float getHeight() {
            if (this.mType.equals("frac")) {
                return (getParam(0).getHeight() + getParam(1).getHeight()) + 5.0f;
            }
            if (this.mType.equals("sum")) {
                return (getParam(0).getHeight() + getParam(1).getHeight()) + (getTextHeight() * 1.2f);
            }
            if (this.mType.equals("overline")) {
                return getParam().getHeight();
            }
            if (this.mType.equals("sqrt")) {
                return getParam().getHeight() * 1.2f;
            }
            float maxHeight = getTextHeight();
            if (this.mParamList == null || this.mParamList.isEmpty()) {
                return maxHeight;
            }
            for (Formula param : this.mParamList) {
                float paramHeight = param.getHeight();
                if (paramHeight > maxHeight) {
                    maxHeight = paramHeight;
                }
            }
            return maxHeight;
        }

        private float getTextSize() {
            float textSize = (this.mBaseTextSize * this.mScale) * this.mDisplayRatio;
            if (textSize < 10.0f) {
                return 10.0f;
            }
            return textSize;
        }

        private float getStrokeWidth() {
            return (this.mBaseStrokeWidth * this.mScale) * this.mDisplayRatio;
        }

        public String enbrace(String str) {
            if (str.length() > Formula.MIN_SYMBOL_SIZE && str.charAt(0) == '{' && str.charAt(str.length() + SUB) == '}') {
                return str;
            }
            return "{" + str + "}";
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            int typefaceSize = getTypefaceSize();
            if (typefaceSize == Formula.MAX_SYMBOL_SIZE) {
                builder.append("\\bigg");
            } else if (typefaceSize == 3) {
                builder.append("\\Big");
            } else if (typefaceSize == Formula.MIN_SYMBOL_SIZE) {
                builder.append("\\big");
            }
            if (TextUtils.isEmpty(this.mType)) {
                builder.append(this.mData);
            } else {
                builder.append("\\").append(this.mType).append(" ");
            }
            if (getParam(SUP).getNodeCount() > 0) {
                if (this.mType.equals("sqrt")) {
                    builder.append("[").append(getParam(SUP).toString()).append("]");
                } else {
                    builder.append(EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR).append(enbrace(getParam(SUP).toString()));
                }
            }
            if (getParam(SUB).getNodeCount() > 0) {
                builder.append("^").append(enbrace(getParam(SUB).toString()));
            }
            if (!(this.mParamList == null || this.mParamList.isEmpty())) {
                for (Formula param : this.mParamList) {
                    builder.append(enbrace(param.toString()));
                }
            }
            return builder.toString();
        }

        public void draw(Canvas canvas, float offsetX, float offsetY) {
            Paint paint = PaintUtils.obtainPaint();
            paint.setTypeface(Formula.sMathTypeface);
            paint.setTextSize(getTextSize());
            paint.setStrokeWidth(getStrokeWidth());
            paint.setColor(this.mTextColor);
            offsetX += getMargin();
            float sizedFontAdjustmentY = 0.0f;
            if (getTypefaceSize() > 1) {
                paint.setTypeface(Formula.getSizedTypeface(getTypefaceSize()));
                sizedFontAdjustmentY = 0.0f + (getTextHeight() * 0.15f);
            }
            float y0;
            Canvas canvas2;
            if (this.mType.equals("frac")) {
                getParam(0).draw(canvas, offsetX + ((getWidth() - getParam(0).getWidth()) / 2.0f), offsetY - (getParam(0).getHeight() * 0.65f));
                getParam(1).draw(canvas, offsetX + ((getWidth() - getParam(1).getWidth()) / 2.0f), offsetY + (getParam(1).getHeight() * 0.75f));
                y0 = offsetY - (getTextHeight() * 0.25f);
                canvas2 = canvas;
                canvas2.drawLine(offsetX, y0, offsetX + getWidth(), y0, paint);
            } else {
                if (this.mType.equals("overline")) {
                    y0 = offsetY - getParam().getHeight();
                    canvas2 = canvas;
                    canvas2.drawLine(offsetX, y0, offsetX + getParam().getWidth(), y0, paint);
                    getParam().draw(canvas, offsetX, offsetY);
                } else {
                    if (this.mType.equals("int")) {
                        getParam(SUB).draw(canvas, offsetX + (getTextWidth() * 1.5f), offsetY - getParam(SUB).getHeight());
                        getParam(SUP).draw(canvas, offsetX + getTextWidth(), offsetY + (getParam(SUP).getHeight() / 2.0f));
                        canvas.drawText(this.mData, offsetX, offsetY, paint);
                    } else {
                        if (this.mType.equals("sum")) {
                            getParam(SUB).draw(canvas, offsetX + ((getWidth() - getParam(SUB).getWidth()) / 2.0f), (offsetY - (getTextHeight() * CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO)) - getParam(SUB).getHeight());
                            getParam(SUP).draw(canvas, offsetX + ((getWidth() - getParam(SUP).getWidth()) / 2.0f), ((getTextHeight() * 0.2f) + offsetY) + getParam(SUP).getHeight());
                            canvas.drawText(this.mData, ((getWidth() - getTextWidth()) / 2.0f) + offsetX, offsetY, paint);
                        } else {
                            if (this.mType.equals("sqrt")) {
                                float supWidth = getParam(SUP).getWidth();
                                if (supWidth > getTextWidth() / 1.5f) {
                                    getParam(SUP).draw(canvas, offsetX, offsetY - (getTextHeight() / 2.0f));
                                    offsetX += supWidth - (getTextWidth() / 1.5f);
                                } else {
                                    getParam(SUP).draw(canvas, ((getTextWidth() / 1.5f) + offsetX) - supWidth, offsetY - (getTextHeight() / 2.0f));
                                }
                                float x0 = offsetX + getTextWidth();
                                y0 = (offsetY - getTextHeight()) + sizedFontAdjustmentY;
                                canvas.drawLine(x0, y0, x0 + getParam().getWidth(), y0, paint);
                                canvas.drawText(this.mData, offsetX, offsetY + sizedFontAdjustmentY, paint);
                                getParam().draw(canvas, getTextWidth() + offsetX, offsetY);
                            } else {
                                String str = this.mData;
                                if (TextUtils.isEmpty(this.mData)) {
                                    str = this.mType;
                                }
                                canvas.drawText(str, offsetX, offsetY + sizedFontAdjustmentY, paint);
                                offsetX += getTextWidth();
                                getParam(SUB).draw(canvas, offsetX, offsetY - (getTextHeight() * 0.5f));
                                getParam(SUP).draw(canvas, offsetX, (getTextHeight() * 0.15f) + offsetY);
                                if (!(this.mParamList == null || this.mParamList.isEmpty())) {
                                    float maxWidth;
                                    float width0 = getParam(SUB).getWidth();
                                    float width1 = getParam(SUP).getWidth();
                                    if (width0 > width1) {
                                        maxWidth = width0;
                                    } else {
                                        maxWidth = width1;
                                    }
                                    offsetX += maxWidth;
                                    for (Formula param : this.mParamList) {
                                        param.draw(canvas, offsetX, offsetY);
                                        offsetX += param.getWidth();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            PaintUtils.recyclePaint(paint);
        }
    }

    private static class Parser {
        int cursor;
        String latex;

        public Parser(String latex) {
            this.cursor = 0;
            this.latex = Table.STRING_DEFAULT_VALUE;
            this.latex = latex;
            this.cursor = 0;
        }

        public Formula parse() {
            return parse(null, 1.0f);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.douban.book.reader.content.paragraph.Formula parse(java.lang.String r14, float r15) {
            /*
            r13 = this;
            r1 = new com.douban.book.reader.content.paragraph.Formula;
            r1.<init>();
            r1.setScale(r15);
            r2 = new com.douban.book.reader.content.paragraph.Formula$Node;
            r2.<init>();
            r1.addNode(r2);
        L_0x0010:
            r8 = r13.getNextToken();
            if (r8 != 0) goto L_0x0017;
        L_0x0016:
            return r1;
        L_0x0017:
            r11 = r8.length();
            r12 = 1;
            if (r11 <= r12) goto L_0x0182;
        L_0x001e:
            r11 = 0;
            r11 = r8.charAt(r11);
            r12 = 92;
            if (r11 != r12) goto L_0x0182;
        L_0x0027:
            r11 = 1;
            r9 = r8.substring(r11);
            r11 = "left";
            r11 = r9.equals(r11);
            if (r11 == 0) goto L_0x0079;
        L_0x0034:
            r6 = r13.getNextNode(r15);
            r1.addNode(r6);
            r11 = "left";
            r7 = r13.parse(r11, r15);
            r6.addParam(r7);
            r0 = r13.getNextNode(r15);
            r1.addNode(r0);
            r10 = 0;
            r11 = r7.getHeight();
            r12 = r6.getTextSize();
            r5 = r11 / r12;
            r11 = 1078397501; // 0x40470a3d float:3.11 double:5.32799158E-315;
            r11 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1));
            if (r11 <= 0) goto L_0x0068;
        L_0x005d:
            r10 = 4;
        L_0x005e:
            r11 = 1;
            if (r10 <= r11) goto L_0x0010;
        L_0x0061:
            r6.setTypefaceSize(r10);
            r0.setTypefaceSize(r10);
            goto L_0x0010;
        L_0x0068:
            r11 = 1075838976; // 0x40200000 float:2.5 double:5.315350785E-315;
            r11 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1));
            if (r11 <= 0) goto L_0x0070;
        L_0x006e:
            r10 = 3;
            goto L_0x005e;
        L_0x0070:
            r11 = 1073322394; // 0x3ff9999a float:1.95 double:5.30291722E-315;
            r11 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1));
            if (r11 <= 0) goto L_0x005e;
        L_0x0077:
            r10 = 2;
            goto L_0x005e;
        L_0x0079:
            r11 = "right";
            r11 = r9.equals(r11);
            if (r11 == 0) goto L_0x008c;
        L_0x0081:
            if (r14 == 0) goto L_0x0010;
        L_0x0083:
            r11 = "left";
            r11 = r14.equals(r11);
            if (r11 == 0) goto L_0x0010;
        L_0x008b:
            goto L_0x0016;
        L_0x008c:
            r11 = "Bigg";
            r11 = r9.equals(r11);
            if (r11 != 0) goto L_0x00ac;
        L_0x0094:
            r11 = "bigg";
            r11 = r9.equals(r11);
            if (r11 != 0) goto L_0x00ac;
        L_0x009c:
            r11 = "big";
            r11 = r9.equals(r11);
            if (r11 != 0) goto L_0x00ac;
        L_0x00a4:
            r11 = "Big";
            r11 = r9.equals(r11);
            if (r11 == 0) goto L_0x00e5;
        L_0x00ac:
            r2 = r13.getNextNode(r15);
            r1.addNode(r2);
            r11 = "Bigg";
            r11 = r9.equals(r11);
            if (r11 != 0) goto L_0x00c3;
        L_0x00bb:
            r11 = "bigg";
            r11 = r9.equals(r11);
            if (r11 == 0) goto L_0x00c9;
        L_0x00c3:
            r11 = 4;
            r2.setTypefaceSize(r11);
            goto L_0x0010;
        L_0x00c9:
            r11 = "Big";
            r11 = r9.equals(r11);
            if (r11 == 0) goto L_0x00d7;
        L_0x00d1:
            r11 = 3;
            r2.setTypefaceSize(r11);
            goto L_0x0010;
        L_0x00d7:
            r11 = "big";
            r11 = r9.equals(r11);
            if (r11 == 0) goto L_0x0010;
        L_0x00df:
            r11 = 2;
            r2.setTypefaceSize(r11);
            goto L_0x0010;
        L_0x00e5:
            r2 = new com.douban.book.reader.content.paragraph.Formula$Node;
            r2.<init>();
            r2.setType(r9);
            r11 = com.douban.book.reader.content.paragraph.Formula.sCodes;
            r11 = r11.containsKey(r9);
            if (r11 == 0) goto L_0x00fe;
        L_0x00f7:
            r11 = com.douban.book.reader.content.paragraph.Formula.getCodeStr(r9);
            r2.setData(r11);
        L_0x00fe:
            r1.addNode(r2);
            r11 = "sqrt";
            r11 = r9.equals(r11);
            if (r11 == 0) goto L_0x0149;
        L_0x0109:
            r11 = r13.peekNextChar();
            r12 = 91;
            if (r11 != r12) goto L_0x0121;
        L_0x0111:
            r11 = 1;
            r13.skip(r11);
            r11 = "[";
            r12 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
            r12 = r12 * r15;
            r11 = r13.parse(r11, r12);
            r2.addSup(r11);
        L_0x0121:
            r3 = new com.douban.book.reader.content.paragraph.Formula;
            r3.<init>();
            r4 = r13.getNextNode(r15);
            r3.addNode(r4);
            r2.addParam(r3);
            r10 = 0;
            r11 = r3.getHeight();
            r12 = r4.getTextSize();
            r5 = r11 / r12;
            r11 = 1078397501; // 0x40470a3d float:3.11 double:5.32799158E-315;
            r11 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1));
            if (r11 <= 0) goto L_0x0171;
        L_0x0142:
            r10 = 4;
        L_0x0143:
            r11 = 1;
            if (r10 <= r11) goto L_0x0149;
        L_0x0146:
            r2.setTypefaceSize(r10);
        L_0x0149:
            if (r14 == 0) goto L_0x0010;
        L_0x014b:
            r11 = "^";
            r11 = r14.equals(r11);
            if (r11 != 0) goto L_0x015b;
        L_0x0153:
            r11 = "_";
            r11 = r14.equals(r11);
            if (r11 == 0) goto L_0x0010;
        L_0x015b:
            r11 = r13.peekNextChar();
            r12 = 123; // 0x7b float:1.72E-43 double:6.1E-322;
            if (r11 != r12) goto L_0x0016;
        L_0x0163:
            r11 = 1;
            r13.skip(r11);
            r11 = "{";
            r11 = r13.parse(r11, r15);
            r2.addParam(r11);
            goto L_0x015b;
        L_0x0171:
            r11 = 1075838976; // 0x40200000 float:2.5 double:5.315350785E-315;
            r11 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1));
            if (r11 <= 0) goto L_0x0179;
        L_0x0177:
            r10 = 3;
            goto L_0x0143;
        L_0x0179:
            r11 = 1073322394; // 0x3ff9999a float:1.95 double:5.30291722E-315;
            r11 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1));
            if (r11 <= 0) goto L_0x0143;
        L_0x0180:
            r10 = 2;
            goto L_0x0143;
        L_0x0182:
            r11 = 0;
            r11 = r8.charAt(r11);
            r12 = 123; // 0x7b float:1.72E-43 double:6.1E-322;
            if (r11 != r12) goto L_0x0196;
        L_0x018b:
            r11 = "{";
            r11 = r13.parse(r11, r15);
            r2.addParam(r11);
            goto L_0x0010;
        L_0x0196:
            r11 = 0;
            r11 = r8.charAt(r11);
            r12 = 125; // 0x7d float:1.75E-43 double:6.2E-322;
            if (r11 != r12) goto L_0x01ab;
        L_0x019f:
            if (r14 == 0) goto L_0x0010;
        L_0x01a1:
            r11 = "{";
            r11 = r14.equals(r11);
            if (r11 == 0) goto L_0x0010;
        L_0x01a9:
            goto L_0x0016;
        L_0x01ab:
            r11 = 0;
            r11 = r8.charAt(r11);
            r12 = 93;
            if (r11 != r12) goto L_0x01dd;
        L_0x01b4:
            if (r14 == 0) goto L_0x01be;
        L_0x01b6:
            r11 = "[";
            r11 = r14.equals(r11);
            if (r11 != 0) goto L_0x0016;
        L_0x01be:
            r2 = new com.douban.book.reader.content.paragraph.Formula$Node;
            r2.<init>();
            r2.setData(r8);
            r1.addNode(r2);
            if (r14 == 0) goto L_0x0010;
        L_0x01cb:
            r11 = "^";
            r11 = r14.equals(r11);
            if (r11 != 0) goto L_0x0016;
        L_0x01d3:
            r11 = "_";
            r11 = r14.equals(r11);
            if (r11 == 0) goto L_0x0010;
        L_0x01db:
            goto L_0x0016;
        L_0x01dd:
            r11 = 0;
            r11 = r8.charAt(r11);
            r12 = 94;
            if (r11 != r12) goto L_0x020e;
        L_0x01e6:
            r11 = r13.peekNextChar();
            r12 = 123; // 0x7b float:1.72E-43 double:6.1E-322;
            if (r11 != r12) goto L_0x0200;
        L_0x01ee:
            r11 = 1;
            r13.skip(r11);
            r11 = "{";
            r12 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
            r12 = r12 * r15;
            r11 = r13.parse(r11, r12);
            r2.addSub(r11);
            goto L_0x0010;
        L_0x0200:
            r11 = "^";
            r12 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
            r12 = r12 * r15;
            r11 = r13.parse(r11, r12);
            r2.addSub(r11);
            goto L_0x0010;
        L_0x020e:
            r11 = 0;
            r11 = r8.charAt(r11);
            r12 = 95;
            if (r11 != r12) goto L_0x023f;
        L_0x0217:
            r11 = r13.peekNextChar();
            r12 = 123; // 0x7b float:1.72E-43 double:6.1E-322;
            if (r11 != r12) goto L_0x0231;
        L_0x021f:
            r11 = 1;
            r13.skip(r11);
            r11 = "{";
            r12 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
            r12 = r12 * r15;
            r11 = r13.parse(r11, r12);
            r2.addSup(r11);
            goto L_0x0010;
        L_0x0231:
            r11 = "_";
            r12 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
            r12 = r12 * r15;
            r11 = r13.parse(r11, r12);
            r2.addSup(r11);
            goto L_0x0010;
        L_0x023f:
            r2 = new com.douban.book.reader.content.paragraph.Formula$Node;
            r2.<init>();
            r2.setData(r8);
            r1.addNode(r2);
            if (r14 == 0) goto L_0x0010;
        L_0x024c:
            r11 = "^";
            r11 = r14.equals(r11);
            if (r11 != 0) goto L_0x0016;
        L_0x0254:
            r11 = "_";
            r11 = r14.equals(r11);
            if (r11 == 0) goto L_0x0010;
        L_0x025c:
            goto L_0x0016;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.douban.book.reader.content.paragraph.Formula.Parser.parse(java.lang.String, float):com.douban.book.reader.content.paragraph.Formula");
        }

        private Node getNextNode(float scale) {
            Node node = new Node();
            String token = getNextToken();
            if (token.equals("{")) {
                node.addParam(parse("{", scale));
            } else if (token.length() <= 1 || token.charAt(0) != '\\') {
                node.setData(token);
            } else {
                String type = token.substring(1);
                node.setType(type);
                if (Formula.sCodes.containsKey(type)) {
                    node.setData(Formula.getCodeStr(type));
                }
            }
            char next = peekNextChar();
            if (next == '^' || next == Char.UNDERLINE) {
                skip(1);
                if (next == '^') {
                    if (peekNextChar() == '{') {
                        skip(1);
                        node.addSub(parse("{", scale * 0.5f));
                    } else {
                        node.addSub(parse("^", scale * 0.5f));
                    }
                } else if (next == Char.UNDERLINE) {
                    if (peekNextChar() == '{') {
                        skip(1);
                        node.addSup(parse("{", scale * 0.5f));
                    } else {
                        node.addSup(parse(EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR, scale * 0.5f));
                    }
                }
            }
            return node;
        }

        public String getNextToken() {
            int len = 0;
            String ret = null;
            while (len == 0 && this.cursor < this.latex.length()) {
                char c = this.latex.charAt(this.cursor);
                if (c == '\\') {
                    len = 0;
                    if (this.cursor < this.latex.length() - 1) {
                        len = 1;
                    }
                    char nextc;
                    do {
                        len++;
                        if (this.cursor + len < this.latex.length()) {
                            nextc = this.latex.charAt(this.cursor + len);
                        } else {
                            nextc = Char.SPACE;
                        }
                    } while (Character.isLetter(nextc));
                } else if (Character.isWhitespace(c)) {
                    len = 0;
                    this.cursor++;
                } else {
                    len = 1;
                }
            }
            if (len > 0) {
                ret = this.latex.substring(this.cursor, this.cursor + len);
            }
            this.cursor += len;
            return ret;
        }

        public char peekNextChar() {
            if (this.cursor >= this.latex.length()) {
                return Char.SPACE;
            }
            Character ch = Character.valueOf(this.latex.charAt(this.cursor));
            while (Character.isWhitespace(ch.charValue())) {
                int i = this.cursor + 1;
                this.cursor = i;
                if (i >= this.latex.length()) {
                    return Char.SPACE;
                }
                ch = Character.valueOf(this.latex.charAt(this.cursor));
            }
            return ch.charValue();
        }

        public void skip(int len) {
            this.cursor += len;
        }
    }

    static {
        sMathTypeface = null;
        sSizedTypefaces = new SparseArray();
        AssetManager assetManager = App.get().getAssets();
        try {
            sMathTypeface = Typeface.createFromAsset(assetManager, "formula/font/STIXMath.otf");
        } catch (Exception e) {
            Logger.e(TAG, e);
            sMathTypeface = Typeface.SERIF;
        }
        for (int i = MIN_SYMBOL_SIZE; i <= MAX_SYMBOL_SIZE; i++) {
            if (sSizedTypefaces.get(i) == null) {
                Typeface typeface;
                try {
                    typeface = Typeface.createFromAsset(assetManager, "formula/font/STIXSize" + i + "Sym.otf");
                } catch (Exception e2) {
                    Logger.e(TAG, e2);
                    typeface = sMathTypeface;
                }
                sSizedTypefaces.put(i, typeface);
            }
        }
        try {
            sCodes = new HashMap();
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("formula/symbols"), Charset.forName(HttpRequest.CHARSET_UTF8)));
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    try {
                        String[] cols = line.split("\\s+");
                        sCodes.put(cols[0].substring(1), cols[1]);
                    } catch (Exception e3) {
                        Logger.d(TAG, "initSymbolMap failed in line: " + line, new Object[0]);
                    }
                } else {
                    return;
                }
            }
        } catch (Exception e22) {
            Logger.e(TAG, e22);
        }
    }

    public Formula() {
        this.mNodeList = new ArrayList();
        this.mBaseTextSize = 40.0f;
        this.mTextColor = 0;
        this.mScale = 1.0f;
        this.mIsRootFormula = false;
    }

    public static Formula parseLatex(String latex) {
        Formula result = new Parser(latex).parse();
        result.mIsRootFormula = true;
        return result;
    }

    public Formula subFormula(int start) {
        return subFormula(start, this.mNodeList.size());
    }

    public Formula subFormula(int start, int end) {
        Formula result = new Formula();
        result.setScale(getScale());
        if (end > start) {
            if (start < 0) {
                start = 0;
            }
            if (end > this.mNodeList.size()) {
                end = this.mNodeList.size();
            }
            int i = start;
            while (i < end) {
                try {
                    result.addNode((Node) this.mNodeList.get(i));
                    i++;
                } catch (Exception e) {
                }
            }
        }
        return result;
    }

    public boolean isEmpty() {
        return getNodeCount() == 0;
    }

    public float getMargin() {
        if (this.mIsRootFormula) {
            return (this.mBaseTextSize * this.mScale) * 0.1f;
        }
        return 0.0f;
    }

    public float getWidth() {
        float result = 0.0f;
        for (Node node : this.mNodeList) {
            result += node.getWidth();
        }
        if (result > 0.0f) {
            return result + (getMargin() * 2.0f);
        }
        return result;
    }

    public float getHeight() {
        float result = 0.0f;
        for (Node node : this.mNodeList) {
            float nodeHeight = node.getHeight();
            if (result < nodeHeight) {
                result = nodeHeight;
            }
        }
        return result;
    }

    public void setScale(float scale) {
        this.mScale = scale;
        for (Node node : this.mNodeList) {
            node.setScale(scale);
        }
    }

    public float getScale() {
        return this.mScale;
    }

    public void setDisplayRatio(float ratio) {
        for (Node node : this.mNodeList) {
            node.setDisplayRatio(ratio);
        }
    }

    public void addNode(Node node) {
        if (this.mScale != 1.0f) {
            node.setScale(this.mScale);
        }
        this.mNodeList.add(node);
    }

    public int getNodeCount() {
        return this.mNodeList.size();
    }

    public void setBaseTextSize(float textSize) {
        if (this.mBaseTextSize != textSize) {
            this.mBaseTextSize = textSize;
            for (Node node : this.mNodeList) {
                node.setBaseTextSize(this.mBaseTextSize);
            }
        }
    }

    public void setTextColor(int color) {
        if (this.mTextColor != color) {
            this.mTextColor = color;
            for (Node node : this.mNodeList) {
                node.setTextColor(color);
            }
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Node node : this.mNodeList) {
            builder.append(node.toString());
        }
        return builder.toString();
    }

    public int getSubFormulaOffsetByWidth(float limitWidth) {
        return getSubFormulaOffsetByWidth(limitWidth, false);
    }

    public int getSubFormulaOffsetByWidth(float limitWidth, boolean canExceed) {
        if (getWidth() <= limitWidth) {
            return getNodeCount();
        }
        float formulaWidth = getMargin();
        float width = 0.0f;
        int offset = 0;
        int subcount = 0;
        for (Node node : this.mNodeList) {
            width += node.getWidth();
            subcount++;
            String type = node.mType;
            String data = node.mData;
            if (data.equals("+") || data.equals(SimpleComparison.EQUAL_TO_OPERATION) || data.equals(",") || type.equals("leqq") || type.equals("leq") || type.equals("le") || type.equals("geqq") || type.equals("geq") || type.equals("ge") || type.equals("approx") || type.equals("approxeq")) {
                if (formulaWidth + width >= limitWidth) {
                    if (canExceed) {
                        offset++;
                    }
                    if (offset != 0 && canExceed) {
                        return getNodeCount();
                    }
                }
                offset += subcount;
                subcount = 0;
                formulaWidth += width;
                width = 0.0f;
            }
        }
        return offset != 0 ? offset : offset;
    }

    public void draw(Canvas canvas, float offsetX, float offsetY) {
        offsetX += getMargin();
        for (Node node : this.mNodeList) {
            node.draw(canvas, offsetX, offsetY);
            offsetX += node.getWidth();
        }
    }

    private static Typeface getSizedTypeface(int size) {
        return (Typeface) sSizedTypefaces.get(size, sMathTypeface);
    }

    private static String getCodeStr(String name) {
        return !sCodes.containsKey(name) ? name : (String) sCodes.get(name);
    }
}
