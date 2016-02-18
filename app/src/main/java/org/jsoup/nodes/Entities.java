package org.jsoup.nodes;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.alipay.security.mobile.module.commonutils.crypto.a;
import com.douban.book.reader.constant.Char;
import com.tencent.connect.share.QQShare;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.Properties;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.parser.Parser;
import u.aly.dx;

public class Entities {
    private static final Map<String, Character> base;
    private static final Map<Character, String> baseByVal;
    private static final Map<String, Character> full;
    private static final Map<Character, String> fullByVal;
    private static final Object[][] xhtmlArray;
    private static final Map<Character, String> xhtmlByVal;

    /* renamed from: org.jsoup.nodes.Entities.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jsoup$nodes$Entities$CoreCharset;

        static {
            $SwitchMap$org$jsoup$nodes$Entities$CoreCharset = new int[CoreCharset.values().length];
            try {
                $SwitchMap$org$jsoup$nodes$Entities$CoreCharset[CoreCharset.ascii.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jsoup$nodes$Entities$CoreCharset[CoreCharset.utf.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private enum CoreCharset {
        ascii,
        utf,
        fallback;

        private static CoreCharset byName(String name) {
            if (name.equals(a.b)) {
                return ascii;
            }
            if (name.startsWith("UTF-")) {
                return utf;
            }
            return fallback;
        }
    }

    public enum EscapeMode {
        xhtml(Entities.xhtmlByVal),
        base(Entities.baseByVal),
        extended(Entities.fullByVal);
        
        private Map<Character, String> map;

        private EscapeMode(Map<Character, String> map) {
            this.map = map;
        }

        public Map<Character, String> getMap() {
            return this.map;
        }
    }

    private Entities() {
    }

    public static boolean isNamedEntity(String name) {
        return full.containsKey(name);
    }

    public static boolean isBaseNamedEntity(String name) {
        return base.containsKey(name);
    }

    public static Character getCharacterByName(String name) {
        return (Character) full.get(name);
    }

    static String escape(String string, OutputSettings out) {
        StringBuilder accum = new StringBuilder(string.length() * 2);
        escape(accum, string, out, false, false, false);
        return accum.toString();
    }

    static void escape(StringBuilder accum, String string, OutputSettings out, boolean inAttribute, boolean normaliseWhite, boolean stripLeadingWhite) {
        boolean lastWasWhite = false;
        boolean reachedNonWhite = false;
        EscapeMode escapeMode = out.escapeMode();
        CharsetEncoder encoder = out.encoder();
        CoreCharset coreCharset = CoreCharset.byName(encoder.charset().name());
        Map<Character, String> map = escapeMode.getMap();
        int length = string.length();
        int offset = 0;
        while (offset < length) {
            int codePoint = string.codePointAt(offset);
            if (normaliseWhite) {
                if (StringUtil.isWhitespace(codePoint)) {
                    if ((!stripLeadingWhite || reachedNonWhite) && !lastWasWhite) {
                        accum.append(Char.SPACE);
                        lastWasWhite = true;
                    }
                    offset += Character.charCount(codePoint);
                } else {
                    lastWasWhite = false;
                    reachedNonWhite = true;
                }
            }
            if (codePoint < AccessibilityNodeInfoCompat.ACTION_CUT) {
                char c = (char) codePoint;
                switch (c) {
                    case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                        if (!inAttribute) {
                            accum.append(c);
                            break;
                        } else {
                            accum.append("&quot;");
                            break;
                        }
                    case HeaderMapDB.HASHER_DOUBLE_ARRAY /*38*/:
                        accum.append("&amp;");
                        break;
                    case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                        if (inAttribute && escapeMode != EscapeMode.xhtml) {
                            accum.append(c);
                            break;
                        } else {
                            accum.append("&lt;");
                            break;
                        }
                    case Header.LONG_14 /*62*/:
                        if (!inAttribute) {
                            accum.append("&gt;");
                            break;
                        } else {
                            accum.append(c);
                            break;
                        }
                    case Header.ARRAYLIST_PACKED_LONG /*160*/:
                        if (escapeMode == EscapeMode.xhtml) {
                            accum.append("&#xa0;");
                            break;
                        } else {
                            accum.append("&nbsp;");
                            break;
                        }
                    default:
                        if (!canEncode(coreCharset, c, encoder)) {
                            if (!map.containsKey(Character.valueOf(c))) {
                                accum.append("&#x").append(Integer.toHexString(codePoint)).append(';');
                                break;
                            } else {
                                accum.append('&').append((String) map.get(Character.valueOf(c))).append(';');
                                break;
                            }
                        }
                        accum.append(c);
                        break;
                }
            }
            String c2 = new String(Character.toChars(codePoint));
            if (encoder.canEncode(c2)) {
                accum.append(c2);
            } else {
                accum.append("&#x").append(Integer.toHexString(codePoint)).append(';');
            }
            offset += Character.charCount(codePoint);
        }
    }

    static String unescape(String string) {
        return unescape(string, false);
    }

    static String unescape(String string, boolean strict) {
        return Parser.unescapeEntities(string, strict);
    }

    private static boolean canEncode(CoreCharset charset, char c, CharsetEncoder fallback) {
        switch (AnonymousClass1.$SwitchMap$org$jsoup$nodes$Entities$CoreCharset[charset.ordinal()]) {
            case dx.b /*1*/:
                if (c >= '\u0080') {
                    return false;
                }
                return true;
            case dx.c /*2*/:
                return true;
            default:
                return fallback.canEncode(c);
        }
    }

    static {
        r2 = new Object[4][];
        r2[0] = new Object[]{"quot", Integer.valueOf(34)};
        r2[1] = new Object[]{"amp", Integer.valueOf(38)};
        r2[2] = new Object[]{"lt", Integer.valueOf(60)};
        r2[3] = new Object[]{"gt", Integer.valueOf(62)};
        xhtmlArray = r2;
        xhtmlByVal = new HashMap();
        base = loadEntities("entities-base.properties");
        baseByVal = toCharacterKey(base);
        full = loadEntities("entities-full.properties");
        fullByVal = toCharacterKey(full);
        for (Object[] entity : xhtmlArray) {
            xhtmlByVal.put(Character.valueOf((char) ((Integer) entity[1]).intValue()), (String) entity[0]);
        }
    }

    private static Map<String, Character> loadEntities(String filename) {
        Properties properties = new Properties();
        Map<String, Character> entities = new HashMap();
        try {
            InputStream in = Entities.class.getResourceAsStream(filename);
            properties.load(in);
            in.close();
            for (Entry entry : properties.entrySet()) {
                entities.put((String) entry.getKey(), Character.valueOf((char) Integer.parseInt((String) entry.getValue(), 16)));
            }
            return entities;
        } catch (IOException e) {
            throw new MissingResourceException("Error loading entities resource: " + e.getMessage(), "Entities", filename);
        }
    }

    private static Map<Character, String> toCharacterKey(Map<String, Character> inMap) {
        Map<Character, String> outMap = new HashMap();
        for (Entry<String, Character> entry : inMap.entrySet()) {
            Character character = (Character) entry.getValue();
            String name = (String) entry.getKey();
            if (!outMap.containsKey(character)) {
                outMap.put(character, name);
            } else if (name.toLowerCase().equals(name)) {
                outMap.put(character, name);
            }
        }
        return outMap;
    }
}
