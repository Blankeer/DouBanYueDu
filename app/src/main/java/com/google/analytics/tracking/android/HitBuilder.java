package com.google.analytics.tracking.android;

import android.text.TextUtils;
import com.j256.ormlite.stmt.query.SimpleComparison;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class HitBuilder {
    HitBuilder() {
    }

    static Map<String, String> generateHitParams(Map<String, String> hit) {
        Map<String, String> params = new HashMap();
        for (Entry<String, String> entry : hit.entrySet()) {
            if (((String) entry.getKey()).startsWith("&") && entry.getValue() != null) {
                String urlParam = ((String) entry.getKey()).substring(1);
                if (!TextUtils.isEmpty(urlParam)) {
                    params.put(urlParam, entry.getValue());
                }
            }
        }
        return params;
    }

    static String postProcessHit(Hit hit, long currentTimeMillis) {
        StringBuilder builder = new StringBuilder();
        builder.append(hit.getHitParams());
        if (hit.getHitTime() > 0) {
            long queueTime = currentTimeMillis - hit.getHitTime();
            if (queueTime >= 0) {
                builder.append("&qt").append(SimpleComparison.EQUAL_TO_OPERATION).append(queueTime);
            }
        }
        builder.append("&z").append(SimpleComparison.EQUAL_TO_OPERATION).append(hit.getHitId());
        return builder.toString();
    }

    static String encode(String input) {
        try {
            return URLEncoder.encode(input, HttpRequest.CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("URL encoding failed for: " + input);
        }
    }
}
