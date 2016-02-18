package io.fabric.sdk.android.services.network;

import android.text.TextUtils;
import com.j256.ormlite.stmt.query.SimpleComparison;
import io.realm.internal.Table;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.TreeMap;

public final class UrlUtils {
    public static final String UTF8 = "UTF8";

    private UrlUtils() {
    }

    public static TreeMap<String, String> getQueryParams(URI uri, boolean decode) {
        return getQueryParams(uri.getRawQuery(), decode);
    }

    public static TreeMap<String, String> getQueryParams(String paramsString, boolean decode) {
        TreeMap<String, String> params = new TreeMap();
        if (paramsString != null) {
            for (String nameValuePairString : paramsString.split("&")) {
                String[] nameValuePair = nameValuePairString.split(SimpleComparison.EQUAL_TO_OPERATION);
                if (nameValuePair.length == 2) {
                    if (decode) {
                        params.put(urlDecode(nameValuePair[0]), urlDecode(nameValuePair[1]));
                    } else {
                        params.put(nameValuePair[0], nameValuePair[1]);
                    }
                } else if (!TextUtils.isEmpty(nameValuePair[0])) {
                    if (decode) {
                        params.put(urlDecode(nameValuePair[0]), Table.STRING_DEFAULT_VALUE);
                    } else {
                        params.put(nameValuePair[0], Table.STRING_DEFAULT_VALUE);
                    }
                }
            }
        }
        return params;
    }

    public static String urlEncode(String s) {
        if (s == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        try {
            return URLEncoder.encode(s, UTF8);
        } catch (UnsupportedEncodingException unlikely) {
            throw new RuntimeException(unlikely.getMessage(), unlikely);
        }
    }

    public static String urlDecode(String s) {
        if (s == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        try {
            return URLDecoder.decode(s, UTF8);
        } catch (UnsupportedEncodingException unlikely) {
            throw new RuntimeException(unlikely.getMessage(), unlikely);
        }
    }

    public static String percentEncode(String s) {
        if (s == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        StringBuilder sb = new StringBuilder();
        String encoded = urlEncode(s);
        int encodedLength = encoded.length();
        int i = 0;
        while (i < encodedLength) {
            char c = encoded.charAt(i);
            if (c == '*') {
                sb.append("%2A");
            } else if (c == '+') {
                sb.append("%20");
            } else if (c == '%' && i + 2 < encodedLength && encoded.charAt(i + 1) == '7' && encoded.charAt(i + 2) == 'E') {
                sb.append('~');
                i += 2;
            } else {
                sb.append(c);
            }
            i++;
        }
        return sb.toString();
    }
}
