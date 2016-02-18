package com.google.analytics.tracking.android;

import android.text.TextUtils;
import com.j256.ormlite.stmt.query.SimpleComparison;
import com.tencent.connect.common.Constants;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class Utils {
    private static final char[] HEXBYTES;

    Utils() {
    }

    static {
        HEXBYTES = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    }

    public static Map<String, String> parseURLParameters(String parameterString) {
        Map<String, String> parameters = new HashMap();
        for (String s : parameterString.split("&")) {
            String[] ss = s.split(SimpleComparison.EQUAL_TO_OPERATION);
            if (ss.length > 1) {
                parameters.put(ss[0], ss[1]);
            } else if (ss.length == 1 && ss[0].length() != 0) {
                parameters.put(ss[0], null);
            }
        }
        return parameters;
    }

    public static double safeParseDouble(String s) {
        return safeParseDouble(s, 0.0d);
    }

    public static double safeParseDouble(String s, double defaultValue) {
        if (s != null) {
            try {
                defaultValue = Double.parseDouble(s);
            } catch (NumberFormatException e) {
            }
        }
        return defaultValue;
    }

    public static long safeParseLong(String s) {
        long j = 0;
        if (s != null) {
            try {
                j = Long.parseLong(s);
            } catch (NumberFormatException e) {
            }
        }
        return j;
    }

    public static boolean safeParseBoolean(String s, boolean defaultValue) {
        if (s == null) {
            return defaultValue;
        }
        if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase(Constants.VIA_TO_TYPE_QQ_GROUP)) {
            return true;
        }
        if (s.equalsIgnoreCase("false") || s.equalsIgnoreCase("no") || s.equalsIgnoreCase(Constants.VIA_RESULT_SUCCESS)) {
            return false;
        }
        return defaultValue;
    }

    public static String filterCampaign(String campaign) {
        if (TextUtils.isEmpty(campaign)) {
            return null;
        }
        String urlParameters = campaign;
        if (campaign.contains("?")) {
            String[] urlParts = campaign.split("[\\?]");
            if (urlParts.length > 1) {
                urlParameters = urlParts[1];
            }
        }
        if (urlParameters.contains("%3D")) {
            try {
                urlParameters = URLDecoder.decode(urlParameters, HttpRequest.CHARSET_UTF8);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        } else if (!urlParameters.contains(SimpleComparison.EQUAL_TO_OPERATION)) {
            return null;
        }
        Map<String, String> paramsMap = parseURLParameters(urlParameters);
        String[] validParameters = new String[]{"dclid", "utm_source", "gclid", "utm_campaign", "utm_medium", "utm_term", "utm_content", "utm_id", "gmob_t"};
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < validParameters.length; i++) {
            if (!TextUtils.isEmpty((CharSequence) paramsMap.get(validParameters[i]))) {
                if (params.length() > 0) {
                    params.append("&");
                }
                params.append(validParameters[i]).append(SimpleComparison.EQUAL_TO_OPERATION).append((String) paramsMap.get(validParameters[i]));
            }
        }
        return params.toString();
    }

    static String getLanguage(Locale locale) {
        if (locale == null || TextUtils.isEmpty(locale.getLanguage())) {
            return null;
        }
        StringBuilder lang = new StringBuilder();
        lang.append(locale.getLanguage().toLowerCase());
        if (!TextUtils.isEmpty(locale.getCountry())) {
            lang.append("-").append(locale.getCountry().toLowerCase());
        }
        return lang.toString();
    }

    static String hexEncode(byte[] bytes) {
        char[] out = new char[(bytes.length * 2)];
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
            out[i * 2] = HEXBYTES[b >> 4];
            out[(i * 2) + 1] = HEXBYTES[b & 15];
        }
        return new String(out);
    }

    static int fromHexDigit(char hexDigit) {
        int value = hexDigit - 48;
        if (value > 9) {
            return value - 7;
        }
        return value;
    }

    static byte[] hexDecode(String s) {
        byte[] bytes = new byte[(s.length() / 2)];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((fromHexDigit(s.charAt(i * 2)) << 4) | fromHexDigit(s.charAt((i * 2) + 1)));
        }
        return bytes;
    }

    public static void putIfAbsent(Map<String, String> hit, String key, String value) {
        if (!hit.containsKey(key)) {
            hit.put(key, value);
        }
    }
}
