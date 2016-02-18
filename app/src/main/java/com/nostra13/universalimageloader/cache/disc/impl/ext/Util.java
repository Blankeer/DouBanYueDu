package com.nostra13.universalimageloader.cache.disc.impl.ext;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.alipay.security.mobile.module.commonutils.crypto.a;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;

final class Util {
    static final Charset US_ASCII;
    static final Charset UTF_8;

    static {
        US_ASCII = Charset.forName(a.b);
        UTF_8 = Charset.forName(HttpRequest.CHARSET_UTF8);
    }

    private Util() {
    }

    static String readFully(Reader reader) throws IOException {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
            while (true) {
                int count = reader.read(buffer);
                if (count == -1) {
                    break;
                }
                writer.write(buffer, 0, count);
            }
            String stringWriter = writer.toString();
            return stringWriter;
        } finally {
            reader.close();
        }
    }

    static void deleteContents(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new IOException("not a readable directory: " + dir);
        }
        File[] arr$ = files;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            File file = arr$[i$];
            if (file.isDirectory()) {
                deleteContents(file);
            }
            if (file.delete()) {
                i$++;
            } else {
                throw new IOException("failed to delete file: " + file);
            }
        }
    }

    static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception e) {
            }
        }
    }
}
