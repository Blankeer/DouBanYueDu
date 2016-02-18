package natalya.codec;

import com.tencent.connect.common.Constants;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtils {
    public static String md5Hex(String str) {
        try {
            return md5Hex(str.getBytes(HttpRequest.CHARSET_UTF8));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String md5Hex(byte[] data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance(CommonUtils.MD5_INSTANCE);
            md5.update(data);
            byte[] messageDigest = md5.digest();
            StringBuffer hexString = new StringBuffer();
            for (byte b : messageDigest) {
                String t = Integer.toHexString(b & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
                if (t.length() == 1) {
                    hexString.append(Constants.VIA_RESULT_SUCCESS + t);
                } else {
                    hexString.append(t);
                }
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
