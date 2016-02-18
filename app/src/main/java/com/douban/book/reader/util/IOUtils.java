package com.douban.book.reader.util;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.json.JSONException;
import org.json.JSONObject;

public class IOUtils {

    public interface ProgressListener {
        void onNewProgress(long j, long j2);
    }

    public static void writeStringToFile(File file, String string) throws IOException {
        FileWriter out = new FileWriter(file);
        try {
            out.write(string);
        } finally {
            out.close();
        }
    }

    public static void writeStringToFile(String fileName, String string) throws IOException {
        FileWriter out = new FileWriter(fileName);
        try {
            out.write(string);
        } finally {
            out.close();
        }
    }

    public static void writePartlyStreamToFile(File file, InputStream stream, long maxBytes) throws IOException, InterruptedException {
        writeStreamToFile(file, stream, maxBytes, false, 0, 0, 0, null);
    }

    public static void writeStreamToFileAndClose(File file, InputStream stream, boolean append, long bytesInterval, long bytesDone, long bytesTotal, ProgressListener listener) throws IOException, InterruptedException {
        try {
            writeStreamToFile(file, stream, Long.MAX_VALUE, append, bytesInterval, bytesDone, bytesTotal, listener);
        } finally {
            closeSilently(stream);
        }
    }

    private static void writeStreamToFile(File file, InputStream stream, long maxBytes, boolean append, long bytesInterval, long bytesDone, long bytesTotal, ProgressListener listener) throws IOException, InterruptedException {
        Throwable th;
        BufferedOutputStream outputStream = null;
        try {
            File folder = file.getParentFile();
            if (folder.isDirectory() || folder.mkdirs()) {
                byte[] buffer = new byte[AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD];
                long totalBytesWritten = bytesDone;
                long lastTotalBytesWritten = bytesDone;
                long remainedBytes = maxBytes;
                BufferedOutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(file, append));
                while (remainedBytes > 0) {
                    try {
                        int len = stream.read(buffer, 0, (int) Math.min((long) buffer.length, remainedBytes));
                        if (len == -1) {
                            break;
                        }
                        remainedBytes -= (long) len;
                        outputStream2.write(buffer, 0, len);
                        totalBytesWritten += (long) len;
                        if (listener != null && (totalBytesWritten - lastTotalBytesWritten >= bytesInterval || totalBytesWritten >= bytesTotal)) {
                            listener.onNewProgress(totalBytesWritten, bytesTotal);
                            lastTotalBytesWritten = totalBytesWritten;
                        }
                        if (Thread.interrupted()) {
                            throw new InterruptedException();
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        outputStream = outputStream2;
                    }
                }
                closeSilently(outputStream2);
                return;
            }
            throw new IOException("file creation failed.");
        } catch (Throwable th3) {
            th = th3;
            closeSilently(outputStream);
            throw th;
        }
    }

    public static String streamToString(InputStream stream) throws IOException {
        if (stream == null) {
            return null;
        }
        byte[] buffer = new byte[CodedOutputStream.DEFAULT_BUFFER_SIZE];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while (true) {
            try {
                int len = stream.read(buffer);
                if (len == -1) {
                    break;
                }
                outputStream.write(buffer, 0, len);
            } finally {
                closeSilently(outputStream);
            }
        }
        String byteArrayOutputStream = outputStream.toString(HttpRequest.CHARSET_UTF8);
        return byteArrayOutputStream;
    }

    public static JSONObject streamToJSONObject(InputStream stream) throws IOException, JSONException {
        return new JSONObject(streamToString(stream));
    }

    public static JSONObject fileToJSONObject(File file) throws IOException, JSONException {
        Throwable th;
        BufferedInputStream in = null;
        try {
            BufferedInputStream in2 = new BufferedInputStream(new FileInputStream(file));
            try {
                JSONObject streamToJSONObject = streamToJSONObject(in2);
                closeSilently(in2);
                return streamToJSONObject;
            } catch (Throwable th2) {
                th = th2;
                in = in2;
                closeSilently(in);
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            closeSilently(in);
            throw th;
        }
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable e) {
                Logger.e(Tag.GENERAL, e);
            }
        }
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD];
        while (true) {
            int len = in.read(buffer, 0, buffer.length);
            if (len != -1) {
                out.write(buffer, 0, len);
            } else {
                return;
            }
        }
    }
}
