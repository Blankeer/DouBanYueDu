package com.ta.utdid2.core.persistent;

import android.util.Xml;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.entity.DbCacheEntity.Column;
import com.google.analytics.tracking.android.HitTypes;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import com.tencent.connect.common.Constants;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

class XmlUtils {
    XmlUtils() {
    }

    public static void skipCurrentTag(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && xmlPullParser.getDepth() <= depth) {
                return;
            }
        }
    }

    public static final int convertValueToList(CharSequence charSequence, String[] strArr, int i) {
        if (charSequence != null) {
            for (int i2 = 0; i2 < strArr.length; i2++) {
                if (charSequence.equals(strArr[i2])) {
                    return i2;
                }
            }
        }
        return i;
    }

    public static final boolean convertValueToBoolean(CharSequence charSequence, boolean z) {
        boolean z2 = false;
        if (charSequence == null) {
            return z;
        }
        if (charSequence.equals(Constants.VIA_TO_TYPE_QQ_GROUP) || charSequence.equals("true") || charSequence.equals("TRUE")) {
            z2 = true;
        }
        return z2;
    }

    public static final int convertValueToInt(CharSequence charSequence, int i) {
        int i2 = 1;
        if (charSequence == null) {
            return i;
        }
        int i3;
        int i4;
        String obj = charSequence.toString();
        int length = obj.length();
        if (Char.HYPHEN == obj.charAt(0)) {
            i3 = -1;
        } else {
            i3 = 1;
            i2 = 0;
        }
        if ('0' == obj.charAt(i2)) {
            if (i2 == length - 1) {
                return 0;
            }
            char charAt = obj.charAt(i2 + 1);
            if ('x' == charAt || 'X' == charAt) {
                i4 = i2 + 2;
                i2 = 16;
            } else {
                i4 = i2 + 1;
                i2 = 8;
            }
        } else if ('#' == obj.charAt(i2)) {
            i4 = i2 + 1;
            i2 = 16;
        } else {
            i4 = i2;
            i2 = 10;
        }
        return Integer.parseInt(obj.substring(i4), i2) * i3;
    }

    public static final int convertValueToUnsignedInt(String str, int i) {
        return str == null ? i : parseUnsignedIntAttribute(str);
    }

    public static final int parseUnsignedIntAttribute(CharSequence charSequence) {
        int i;
        int i2 = 16;
        String obj = charSequence.toString();
        int length = obj.length();
        if ('0' == obj.charAt(0)) {
            if (length - 1 == 0) {
                return 0;
            }
            char charAt = obj.charAt(1);
            if ('x' == charAt || 'X' == charAt) {
                i = 2;
            } else {
                i2 = 8;
                i = 1;
            }
        } else if ('#' == obj.charAt(0)) {
            i = 1;
        } else {
            i2 = 10;
            i = 0;
        }
        return (int) Long.parseLong(obj.substring(i), i2);
    }

    public static final void writeMapXml(Map map, OutputStream outputStream) throws XmlPullParserException, IOException {
        XmlSerializer fastXmlSerializer = new FastXmlSerializer();
        fastXmlSerializer.setOutput(outputStream, "utf-8");
        fastXmlSerializer.startDocument(null, Boolean.valueOf(true));
        fastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        writeMapXml(map, null, fastXmlSerializer);
        fastXmlSerializer.endDocument();
    }

    public static final void writeListXml(List list, OutputStream outputStream) throws XmlPullParserException, IOException {
        XmlSerializer newSerializer = Xml.newSerializer();
        newSerializer.setOutput(outputStream, "utf-8");
        newSerializer.startDocument(null, Boolean.valueOf(true));
        newSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        writeListXml(list, null, newSerializer);
        newSerializer.endDocument();
    }

    public static final void writeMapXml(Map map, String str, XmlSerializer xmlSerializer) throws XmlPullParserException, IOException {
        if (map == null) {
            xmlSerializer.startTag(null, "null");
            xmlSerializer.endTag(null, "null");
            return;
        }
        xmlSerializer.startTag(null, "map");
        if (str != null) {
            xmlSerializer.attribute(null, SelectCountryActivity.EXTRA_COUNTRY_NAME, str);
        }
        for (Entry entry : map.entrySet()) {
            writeValueXml(entry.getValue(), (String) entry.getKey(), xmlSerializer);
        }
        xmlSerializer.endTag(null, "map");
    }

    public static final void writeListXml(List list, String str, XmlSerializer xmlSerializer) throws XmlPullParserException, IOException {
        if (list == null) {
            xmlSerializer.startTag(null, "null");
            xmlSerializer.endTag(null, "null");
            return;
        }
        xmlSerializer.startTag(null, "list");
        if (str != null) {
            xmlSerializer.attribute(null, SelectCountryActivity.EXTRA_COUNTRY_NAME, str);
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            writeValueXml(list.get(i), null, xmlSerializer);
        }
        xmlSerializer.endTag(null, "list");
    }

    public static final void writeByteArrayXml(byte[] bArr, String str, XmlSerializer xmlSerializer) throws XmlPullParserException, IOException {
        if (bArr == null) {
            xmlSerializer.startTag(null, "null");
            xmlSerializer.endTag(null, "null");
            return;
        }
        xmlSerializer.startTag(null, "byte-array");
        if (str != null) {
            xmlSerializer.attribute(null, SelectCountryActivity.EXTRA_COUNTRY_NAME, str);
        }
        xmlSerializer.attribute(null, "num", Integer.toString(r2));
        StringBuilder stringBuilder = new StringBuilder(bArr.length * 2);
        for (byte b : bArr) {
            int i = b >> 4;
            stringBuilder.append(i >= 10 ? (i + 97) - 10 : i + 48);
            i = b & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
            if (i >= 10) {
                i = (i + 97) - 10;
            } else {
                i += 48;
            }
            stringBuilder.append(i);
        }
        xmlSerializer.text(stringBuilder.toString());
        xmlSerializer.endTag(null, "byte-array");
    }

    public static final void writeIntArrayXml(int[] iArr, String str, XmlSerializer xmlSerializer) throws XmlPullParserException, IOException {
        if (iArr == null) {
            xmlSerializer.startTag(null, "null");
            xmlSerializer.endTag(null, "null");
            return;
        }
        xmlSerializer.startTag(null, "int-array");
        if (str != null) {
            xmlSerializer.attribute(null, SelectCountryActivity.EXTRA_COUNTRY_NAME, str);
        }
        xmlSerializer.attribute(null, "num", Integer.toString(r1));
        for (int num : iArr) {
            xmlSerializer.startTag(null, HitTypes.ITEM);
            xmlSerializer.attribute(null, Column.VALUE, Integer.toString(num));
            xmlSerializer.endTag(null, HitTypes.ITEM);
        }
        xmlSerializer.endTag(null, "int-array");
    }

    public static final void writeValueXml(Object obj, String str, XmlSerializer xmlSerializer) throws XmlPullParserException, IOException {
        if (obj == null) {
            xmlSerializer.startTag(null, "null");
            if (str != null) {
                xmlSerializer.attribute(null, SelectCountryActivity.EXTRA_COUNTRY_NAME, str);
            }
            xmlSerializer.endTag(null, "null");
        } else if (obj instanceof String) {
            xmlSerializer.startTag(null, "string");
            if (str != null) {
                xmlSerializer.attribute(null, SelectCountryActivity.EXTRA_COUNTRY_NAME, str);
            }
            xmlSerializer.text(obj.toString());
            xmlSerializer.endTag(null, "string");
        } else {
            String str2;
            if (obj instanceof Integer) {
                str2 = "int";
            } else if (obj instanceof Long) {
                str2 = "long";
            } else if (obj instanceof Float) {
                str2 = "float";
            } else if (obj instanceof Double) {
                str2 = "double";
            } else if (obj instanceof Boolean) {
                str2 = "boolean";
            } else if (obj instanceof byte[]) {
                writeByteArrayXml((byte[]) obj, str, xmlSerializer);
                return;
            } else if (obj instanceof int[]) {
                writeIntArrayXml((int[]) obj, str, xmlSerializer);
                return;
            } else if (obj instanceof Map) {
                writeMapXml((Map) obj, str, xmlSerializer);
                return;
            } else if (obj instanceof List) {
                writeListXml((List) obj, str, xmlSerializer);
                return;
            } else if (obj instanceof CharSequence) {
                xmlSerializer.startTag(null, "string");
                if (str != null) {
                    xmlSerializer.attribute(null, SelectCountryActivity.EXTRA_COUNTRY_NAME, str);
                }
                xmlSerializer.text(obj.toString());
                xmlSerializer.endTag(null, "string");
                return;
            } else {
                throw new RuntimeException("writeValueXml: unable to write value " + obj);
            }
            xmlSerializer.startTag(null, str2);
            if (str != null) {
                xmlSerializer.attribute(null, SelectCountryActivity.EXTRA_COUNTRY_NAME, str);
            }
            xmlSerializer.attribute(null, Column.VALUE, obj.toString());
            xmlSerializer.endTag(null, str2);
        }
    }

    public static final HashMap readMapXml(InputStream inputStream) throws XmlPullParserException, IOException {
        XmlPullParser newPullParser = Xml.newPullParser();
        newPullParser.setInput(inputStream, null);
        return (HashMap) readValueXml(newPullParser, new String[1]);
    }

    public static final ArrayList readListXml(InputStream inputStream) throws XmlPullParserException, IOException {
        XmlPullParser newPullParser = Xml.newPullParser();
        newPullParser.setInput(inputStream, null);
        return (ArrayList) readValueXml(newPullParser, new String[1]);
    }

    public static final HashMap readThisMapXml(XmlPullParser xmlPullParser, String str, String[] strArr) throws XmlPullParserException, IOException {
        HashMap hashMap = new HashMap();
        int eventType = xmlPullParser.getEventType();
        do {
            if (eventType == 2) {
                Object readThisValueXml = readThisValueXml(xmlPullParser, strArr);
                if (strArr[0] != null) {
                    hashMap.put(strArr[0], readThisValueXml);
                } else {
                    throw new XmlPullParserException("Map value without name attribute: " + xmlPullParser.getName());
                }
            } else if (eventType == 3) {
                if (xmlPullParser.getName().equals(str)) {
                    return hashMap;
                }
                throw new XmlPullParserException("Expected " + str + " end tag at: " + xmlPullParser.getName());
            }
            eventType = xmlPullParser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + str + " end tag");
    }

    public static final ArrayList readThisListXml(XmlPullParser xmlPullParser, String str, String[] strArr) throws XmlPullParserException, IOException {
        ArrayList arrayList = new ArrayList();
        int eventType = xmlPullParser.getEventType();
        do {
            if (eventType == 2) {
                arrayList.add(readThisValueXml(xmlPullParser, strArr));
            } else if (eventType == 3) {
                if (xmlPullParser.getName().equals(str)) {
                    return arrayList;
                }
                throw new XmlPullParserException("Expected " + str + " end tag at: " + xmlPullParser.getName());
            }
            eventType = xmlPullParser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + str + " end tag");
    }

    public static final int[] readThisIntArrayXml(XmlPullParser xmlPullParser, String str, String[] strArr) throws XmlPullParserException, IOException {
        try {
            int[] iArr = new int[Integer.parseInt(xmlPullParser.getAttributeValue(null, "num"))];
            int i = 0;
            int eventType = xmlPullParser.getEventType();
            do {
                if (eventType == 2) {
                    if (xmlPullParser.getName().equals(HitTypes.ITEM)) {
                        try {
                            iArr[i] = Integer.parseInt(xmlPullParser.getAttributeValue(null, Column.VALUE));
                        } catch (NullPointerException e) {
                            throw new XmlPullParserException("Need value attribute in item");
                        } catch (NumberFormatException e2) {
                            throw new XmlPullParserException("Not a number in value attribute in item");
                        }
                    }
                    throw new XmlPullParserException("Expected item tag at: " + xmlPullParser.getName());
                } else if (eventType == 3) {
                    if (xmlPullParser.getName().equals(str)) {
                        return iArr;
                    }
                    if (xmlPullParser.getName().equals(HitTypes.ITEM)) {
                        i++;
                    } else {
                        throw new XmlPullParserException("Expected " + str + " end tag at: " + xmlPullParser.getName());
                    }
                }
                eventType = xmlPullParser.next();
            } while (eventType != 1);
            throw new XmlPullParserException("Document ended before " + str + " end tag");
        } catch (NullPointerException e3) {
            throw new XmlPullParserException("Need num attribute in byte-array");
        } catch (NumberFormatException e4) {
            throw new XmlPullParserException("Not a number in num attribute in byte-array");
        }
    }

    public static final Object readValueXml(XmlPullParser xmlPullParser, String[] strArr) throws XmlPullParserException, IOException {
        int eventType = xmlPullParser.getEventType();
        while (eventType != 2) {
            if (eventType == 3) {
                throw new XmlPullParserException("Unexpected end tag at: " + xmlPullParser.getName());
            } else if (eventType == 4) {
                throw new XmlPullParserException("Unexpected text: " + xmlPullParser.getText());
            } else {
                try {
                    eventType = xmlPullParser.next();
                    if (eventType == 1) {
                        throw new XmlPullParserException("Unexpected end of document");
                    }
                } catch (Exception e) {
                    throw new XmlPullParserException("Unexpected call next(): " + xmlPullParser.getName());
                }
            }
        }
        return readThisValueXml(xmlPullParser, strArr);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static final java.lang.Object readThisValueXml(org.xmlpull.v1.XmlPullParser r9, java.lang.String[] r10) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r8 = 3;
        r7 = 2;
        r6 = 1;
        r5 = 0;
        r0 = 0;
        r1 = "name";
        r2 = r9.getAttributeValue(r0, r1);
        r3 = r9.getName();
        r1 = "null";
        r1 = r3.equals(r1);
        if (r1 == 0) goto L_0x002c;
    L_0x0017:
        r1 = r9.next();
        if (r1 == r6) goto L_0x01d0;
    L_0x001d:
        if (r1 != r8) goto L_0x0185;
    L_0x001f:
        r1 = r9.getName();
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0162;
    L_0x0029:
        r10[r5] = r2;
    L_0x002b:
        return r0;
    L_0x002c:
        r1 = "string";
        r1 = r3.equals(r1);
        if (r1 == 0) goto L_0x00a2;
    L_0x0034:
        r0 = "";
    L_0x0036:
        r1 = r9.next();
        if (r1 == r6) goto L_0x009a;
    L_0x003c:
        if (r1 != r8) goto L_0x0066;
    L_0x003e:
        r1 = r9.getName();
        r3 = "string";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x004d;
    L_0x004a:
        r10[r5] = r2;
        goto L_0x002b;
    L_0x004d:
        r0 = new org.xmlpull.v1.XmlPullParserException;
        r1 = new java.lang.StringBuilder;
        r2 = "Unexpected end tag in <string>: ";
        r1.<init>(r2);
        r2 = r9.getName();
        r1 = r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0066:
        r3 = 4;
        if (r1 != r3) goto L_0x007f;
    L_0x0069:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r0 = r1.append(r0);
        r1 = r9.getText();
        r0 = r0.append(r1);
        r0 = r0.toString();
        goto L_0x0036;
    L_0x007f:
        if (r1 != r7) goto L_0x0036;
    L_0x0081:
        r0 = new org.xmlpull.v1.XmlPullParserException;
        r1 = new java.lang.StringBuilder;
        r2 = "Unexpected start tag in <string>: ";
        r1.<init>(r2);
        r2 = r9.getName();
        r1 = r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x009a:
        r0 = new org.xmlpull.v1.XmlPullParserException;
        r1 = "Unexpected end of document in <string>";
        r0.<init>(r1);
        throw r0;
    L_0x00a2:
        r1 = "int";
        r1 = r3.equals(r1);
        if (r1 == 0) goto L_0x00ba;
    L_0x00aa:
        r1 = "value";
        r0 = r9.getAttributeValue(r0, r1);
        r0 = java.lang.Integer.parseInt(r0);
        r0 = java.lang.Integer.valueOf(r0);
        goto L_0x0017;
    L_0x00ba:
        r1 = "long";
        r1 = r3.equals(r1);
        if (r1 == 0) goto L_0x00ce;
    L_0x00c2:
        r1 = "value";
        r0 = r9.getAttributeValue(r0, r1);
        r0 = java.lang.Long.valueOf(r0);
        goto L_0x0017;
    L_0x00ce:
        r1 = "float";
        r1 = r3.equals(r1);
        if (r1 == 0) goto L_0x00e4;
    L_0x00d6:
        r1 = new java.lang.Float;
        r4 = "value";
        r0 = r9.getAttributeValue(r0, r4);
        r1.<init>(r0);
        r0 = r1;
        goto L_0x0017;
    L_0x00e4:
        r1 = "double";
        r1 = r3.equals(r1);
        if (r1 == 0) goto L_0x00fa;
    L_0x00ec:
        r1 = new java.lang.Double;
        r4 = "value";
        r0 = r9.getAttributeValue(r0, r4);
        r1.<init>(r0);
        r0 = r1;
        goto L_0x0017;
    L_0x00fa:
        r1 = "boolean";
        r1 = r3.equals(r1);
        if (r1 == 0) goto L_0x010e;
    L_0x0102:
        r1 = "value";
        r0 = r9.getAttributeValue(r0, r1);
        r0 = java.lang.Boolean.valueOf(r0);
        goto L_0x0017;
    L_0x010e:
        r0 = "int-array";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0123;
    L_0x0116:
        r9.next();
        r0 = "int-array";
        r0 = readThisIntArrayXml(r9, r0, r10);
        r10[r5] = r2;
        goto L_0x002b;
    L_0x0123:
        r0 = "map";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0138;
    L_0x012b:
        r9.next();
        r0 = "map";
        r0 = readThisMapXml(r9, r0, r10);
        r10[r5] = r2;
        goto L_0x002b;
    L_0x0138:
        r0 = "list";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x014d;
    L_0x0140:
        r9.next();
        r0 = "list";
        r0 = readThisListXml(r9, r0, r10);
        r10[r5] = r2;
        goto L_0x002b;
    L_0x014d:
        r0 = new org.xmlpull.v1.XmlPullParserException;
        r1 = new java.lang.StringBuilder;
        r2 = "Unknown tag: ";
        r1.<init>(r2);
        r1 = r1.append(r3);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0162:
        r0 = new org.xmlpull.v1.XmlPullParserException;
        r1 = new java.lang.StringBuilder;
        r2 = "Unexpected end tag in <";
        r1.<init>(r2);
        r1 = r1.append(r3);
        r2 = ">: ";
        r1 = r1.append(r2);
        r2 = r9.getName();
        r1 = r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0185:
        r4 = 4;
        if (r1 != r4) goto L_0x01ab;
    L_0x0188:
        r0 = new org.xmlpull.v1.XmlPullParserException;
        r1 = new java.lang.StringBuilder;
        r2 = "Unexpected text in <";
        r1.<init>(r2);
        r1 = r1.append(r3);
        r2 = ">: ";
        r1 = r1.append(r2);
        r2 = r9.getName();
        r1 = r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x01ab:
        if (r1 != r7) goto L_0x0017;
    L_0x01ad:
        r0 = new org.xmlpull.v1.XmlPullParserException;
        r1 = new java.lang.StringBuilder;
        r2 = "Unexpected start tag in <";
        r1.<init>(r2);
        r1 = r1.append(r3);
        r2 = ">: ";
        r1 = r1.append(r2);
        r2 = r9.getName();
        r1 = r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x01d0:
        r0 = new org.xmlpull.v1.XmlPullParserException;
        r1 = new java.lang.StringBuilder;
        r2 = "Unexpected end of document in <";
        r1.<init>(r2);
        r1 = r1.append(r3);
        r2 = ">";
        r1 = r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ta.utdid2.core.persistent.XmlUtils.readThisValueXml(org.xmlpull.v1.XmlPullParser, java.lang.String[]):java.lang.Object");
    }

    public static final void beginDocument(XmlPullParser xmlPullParser, String str) throws XmlPullParserException, IOException {
        int next;
        do {
            next = xmlPullParser.next();
            if (next == 2) {
                break;
            }
        } while (next != 1);
        if (next != 2) {
            throw new XmlPullParserException("No start tag found");
        } else if (!xmlPullParser.getName().equals(str)) {
            throw new XmlPullParserException("Unexpected start tag: found " + xmlPullParser.getName() + ", expected " + str);
        }
    }

    public static final void nextElement(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int next;
        do {
            next = xmlPullParser.next();
            if (next == 2) {
                return;
            }
        } while (next != 1);
    }
}
