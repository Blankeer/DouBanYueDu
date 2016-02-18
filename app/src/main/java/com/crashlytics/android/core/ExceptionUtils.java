package com.crashlytics.android.core;

import android.content.Context;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.realm.internal.Table;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

final class ExceptionUtils {
    private ExceptionUtils() {
    }

    public static void writeStackTraceIfNotNull(Throwable ex, OutputStream os) {
        if (os != null) {
            writeStackTrace(ex, os);
        }
    }

    public static void writeStackTrace(Context ctx, Throwable ex, String filename) {
        Writer writer;
        Exception e;
        Throwable th;
        PrintWriter writer2 = null;
        try {
            Writer writer3 = new PrintWriter(ctx.openFileOutput(filename, 0));
            try {
                writeStackTrace(ex, writer3);
                CommonUtils.closeOrLog(writer3, "Failed to close stack trace writer.");
                writer = writer3;
            } catch (Exception e2) {
                e = e2;
                writer = writer3;
                try {
                    Fabric.getLogger().e(CrashlyticsCore.TAG, "Failed to create PrintWriter", e);
                    CommonUtils.closeOrLog(writer2, "Failed to close stack trace writer.");
                } catch (Throwable th2) {
                    th = th2;
                    CommonUtils.closeOrLog(writer2, "Failed to close stack trace writer.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                writer = writer3;
                CommonUtils.closeOrLog(writer2, "Failed to close stack trace writer.");
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            Fabric.getLogger().e(CrashlyticsCore.TAG, "Failed to create PrintWriter", e);
            CommonUtils.closeOrLog(writer2, "Failed to close stack trace writer.");
        }
    }

    private static void writeStackTrace(Throwable ex, OutputStream os) {
        Writer writer;
        Exception e;
        Throwable th;
        PrintWriter writer2 = null;
        try {
            Writer writer3 = new PrintWriter(os);
            try {
                writeStackTrace(ex, writer3);
                CommonUtils.closeOrLog(writer3, "Failed to close stack trace writer.");
                writer = writer3;
            } catch (Exception e2) {
                e = e2;
                writer = writer3;
                try {
                    Fabric.getLogger().e(CrashlyticsCore.TAG, "Failed to create PrintWriter", e);
                    CommonUtils.closeOrLog(writer2, "Failed to close stack trace writer.");
                } catch (Throwable th2) {
                    th = th2;
                    CommonUtils.closeOrLog(writer2, "Failed to close stack trace writer.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                writer = writer3;
                CommonUtils.closeOrLog(writer2, "Failed to close stack trace writer.");
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            Fabric.getLogger().e(CrashlyticsCore.TAG, "Failed to create PrintWriter", e);
            CommonUtils.closeOrLog(writer2, "Failed to close stack trace writer.");
        }
    }

    private static void writeStackTrace(Throwable ex, Writer writer) {
        boolean first = true;
        while (ex != null) {
            try {
                String message = getMessage(ex);
                if (message == null) {
                    message = Table.STRING_DEFAULT_VALUE;
                }
                writer.write((first ? Table.STRING_DEFAULT_VALUE : "Caused by: ") + ex.getClass().getName() + ": " + message + "\n");
                first = false;
                for (StackTraceElement element : ex.getStackTrace()) {
                    writer.write("\tat " + element.toString() + "\n");
                }
                ex = ex.getCause();
            } catch (Exception e) {
                Fabric.getLogger().e(CrashlyticsCore.TAG, "Could not write stack trace", e);
                return;
            }
        }
    }

    private static String getMessage(Throwable t) {
        String message = t.getLocalizedMessage();
        if (message == null) {
            return null;
        }
        return message.replaceAll("(\r\n|\n|\f)", " ");
    }
}
