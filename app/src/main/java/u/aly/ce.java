package u.aly;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/* compiled from: DeflaterHelper */
public class ce {
    public static int a;

    public static byte[] a(String str, String str2) throws IOException {
        if (cf.d(str)) {
            return null;
        }
        return a(str.getBytes(str2));
    }

    public static byte[] a(byte[] bArr) throws IOException {
        Throwable th;
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        Deflater deflater = new Deflater();
        deflater.setInput(bArr);
        deflater.finish();
        byte[] bArr2 = new byte[AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD];
        a = 0;
        ByteArrayOutputStream byteArrayOutputStream;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            while (!deflater.finished()) {
                try {
                    int deflate = deflater.deflate(bArr2);
                    a += deflate;
                    byteArrayOutputStream.write(bArr2, 0, deflate);
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            deflater.end();
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Throwable th3) {
            Throwable th4 = th3;
            byteArrayOutputStream = null;
            th = th4;
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }
            throw th;
        }
    }

    public static String a(byte[] bArr, String str) throws UnsupportedEncodingException, DataFormatException {
        byte[] b = b(bArr);
        if (b != null) {
            return new String(b, str);
        }
        return null;
    }

    public static byte[] b(byte[] bArr) throws UnsupportedEncodingException, DataFormatException {
        int i = 0;
        if (bArr == null || bArr.length == 0) {
            return null;
        }
        Inflater inflater = new Inflater();
        inflater.setInput(bArr, 0, bArr.length);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr2 = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
        while (!inflater.needsInput()) {
            int inflate = inflater.inflate(bArr2);
            byteArrayOutputStream.write(bArr2, i, inflate);
            i += inflate;
        }
        inflater.end();
        return byteArrayOutputStream.toByteArray();
    }
}
