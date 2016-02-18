package u.aly;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.path.android.jobqueue.JobManager;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import io.realm.internal.Table;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.http.Header;

/* compiled from: Helper */
public class cf {
    public static final String a;
    private static final String b = "helper";

    static {
        a = System.getProperty("line.separator");
    }

    public static String a(String str) {
        if (str == null) {
            return null;
        }
        try {
            byte[] bytes = str.getBytes();
            MessageDigest instance = MessageDigest.getInstance(CommonUtils.MD5_INSTANCE);
            instance.reset();
            instance.update(bytes);
            bytes = instance.digest();
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                stringBuffer.append(String.format("%02X", new Object[]{Byte.valueOf(bytes[i])}));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            return str.replaceAll("[^[a-z][A-Z][0-9][.][_]]", Table.STRING_DEFAULT_VALUE);
        }
    }

    public static String b(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance(CommonUtils.MD5_INSTANCE);
            instance.update(str.getBytes());
            byte[] digest = instance.digest();
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : digest) {
                stringBuffer.append(Integer.toHexString(b & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            bt.a(b, "getMD5 error", e);
            return Table.STRING_DEFAULT_VALUE;
        }
    }

    public static String a(File file) {
        byte[] bArr = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
        try {
            if (!file.isFile()) {
                return Table.STRING_DEFAULT_VALUE;
            }
            MessageDigest instance = MessageDigest.getInstance(CommonUtils.MD5_INSTANCE);
            FileInputStream fileInputStream = new FileInputStream(file);
            while (true) {
                int read = fileInputStream.read(bArr, 0, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
                if (read == -1) {
                    fileInputStream.close();
                    BigInteger bigInteger = new BigInteger(1, instance.digest());
                    return String.format("%1$032x", new Object[]{bigInteger});
                }
                instance.update(bArr, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String a(Context context, long j) {
        String str = Table.STRING_DEFAULT_VALUE;
        if (j < 1000) {
            return ((int) j) + "B";
        }
        if (j < JobManager.NS_PER_MS) {
            return new StringBuilder(String.valueOf(Math.round(((double) ((float) j)) / 1000.0d))).append("K").toString();
        }
        if (j < 1000000000) {
            return new StringBuilder(String.valueOf(new DecimalFormat("#0.0").format(((double) ((float) j)) / 1000000.0d))).append("M").toString();
        }
        return new StringBuilder(String.valueOf(new DecimalFormat("#0.00").format(((double) ((float) j)) / 1.0E9d))).append("G").toString();
    }

    public static String c(String str) {
        String str2 = Table.STRING_DEFAULT_VALUE;
        try {
            long longValue = Long.valueOf(str).longValue();
            if (longValue < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
                return ((int) longValue) + "B";
            }
            if (longValue < 1048576) {
                return new StringBuilder(String.valueOf(new DecimalFormat("#0.00").format(((double) ((float) longValue)) / 1024.0d))).append("K").toString();
            }
            if (longValue < 1073741824) {
                return new StringBuilder(String.valueOf(new DecimalFormat("#0.00").format(((double) ((float) longValue)) / 1048576.0d))).append("M").toString();
            }
            return new StringBuilder(String.valueOf(new DecimalFormat("#0.00").format(((double) ((float) longValue)) / 1.073741824E9d))).append("G").toString();
        } catch (NumberFormatException e) {
            return str;
        }
    }

    public static void a(Context context, String str) {
        context.startActivity(context.getPackageManager().getLaunchIntentForPackage(str));
    }

    public static boolean b(Context context, String str) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean d(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean e(String str) {
        if (d(str)) {
            return false;
        }
        String toLowerCase = str.trim().toLowerCase(Locale.US);
        if (toLowerCase.startsWith("http://") || toLowerCase.startsWith("https://")) {
            return true;
        }
        return false;
    }

    public static String a() {
        return a(new Date());
    }

    public static String a(Date date) {
        if (date == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date);
    }

    public static String a(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        char[] cArr = new char[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
        StringWriter stringWriter = new StringWriter();
        while (true) {
            int read = inputStreamReader.read(cArr);
            if (-1 == read) {
                return stringWriter.toString();
            }
            stringWriter.write(cArr, 0, read);
        }
    }

    public static byte[] b(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
        while (true) {
            int read = inputStream.read(bArr);
            if (-1 == read) {
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(bArr, 0, read);
        }
    }

    public static void a(File file, byte[] bArr) throws IOException {
        OutputStream fileOutputStream = new FileOutputStream(file);
        try {
            fileOutputStream.write(bArr);
            fileOutputStream.flush();
        } finally {
            a(fileOutputStream);
        }
    }

    public static void a(File file, String str) throws IOException {
        a(file, str.getBytes());
    }

    public static void c(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Exception e) {
            }
        }
    }

    public static void a(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (Exception e) {
            }
        }
    }

    public static boolean a(Header header, String str) {
        if (header == null || header.getValue() == null) {
            return false;
        }
        for (String equalsIgnoreCase : header.getValue().split(";")) {
            if (str.equalsIgnoreCase(equalsIgnoreCase)) {
                return true;
            }
        }
        return false;
    }
}
