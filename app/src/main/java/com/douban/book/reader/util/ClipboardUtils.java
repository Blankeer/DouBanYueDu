package com.douban.book.reader.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import com.douban.book.reader.app.App;

public class ClipboardUtils {
    public static void copy(CharSequence text) {
        ((ClipboardManager) App.get().getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("selectedText", text));
    }
}
