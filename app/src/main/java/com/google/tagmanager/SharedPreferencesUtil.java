package com.google.tagmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;

class SharedPreferencesUtil {

    /* renamed from: com.google.tagmanager.SharedPreferencesUtil.1 */
    static class AnonymousClass1 implements Runnable {
        final /* synthetic */ Editor val$editor;

        AnonymousClass1(Editor editor) {
            this.val$editor = editor;
        }

        public void run() {
            this.val$editor.commit();
        }
    }

    SharedPreferencesUtil() {
    }

    static void saveEditorAsync(Editor editor) {
        if (VERSION.SDK_INT >= 9) {
            editor.apply();
        } else {
            new Thread(new AnonymousClass1(editor)).start();
        }
    }

    @SuppressLint({"CommitPrefEdits"})
    static void saveAsync(Context context, String sharedPreferencesName, String key, String value) {
        Editor editor = context.getSharedPreferences(sharedPreferencesName, 0).edit();
        editor.putString(key, value);
        saveEditorAsync(editor);
    }
}
