package com.douban.book.reader.content.cipher;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.exception.CipherException;
import com.douban.book.reader.util.Base64;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.SecurityUtils;
import com.douban.book.reader.util.StringUtils;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class ArkKeyPair {
    private static final String TAG;
    private static PrivateKey sPrivateKey;
    private static String sPublicKey;

    static {
        TAG = ArkKeyPair.class.getSimpleName();
    }

    public static String getPublic() throws CipherException {
        if (StringUtils.isNotEmpty(doGetPublic())) {
            return doGetPublic();
        }
        genKeyPair();
        return doGetPublic();
    }

    public static PrivateKey getPrivate() throws CipherException {
        try {
            PrivateKey privateKey = doGetPrivate();
            if (privateKey != null) {
                return privateKey;
            }
        } catch (CipherException e) {
        }
        genKeyPair();
        return doGetPrivate();
    }

    private static String doGetPublic() {
        if (StringUtils.isNotEmpty(sPublicKey)) {
            return sPublicKey;
        }
        return Pref.ofApp().getString(Key.APP_PUBLIC_KEY);
    }

    private static PrivateKey doGetPrivate() throws CipherException {
        if (sPrivateKey != null) {
            return sPrivateKey;
        }
        if (StringUtils.isNotEmpty(Pref.ofApp().getString(Key.APP_PRIVATE_KEY))) {
            try {
                sPrivateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(SecurityUtils.decrypt(Pref.ofApp().getString(Key.APP_PRIVATE_KEY)), 0)));
            } catch (Throwable e) {
                throw new CipherException(e);
            }
        }
        return sPrivateKey;
    }

    private static void genKeyPair() throws CipherException {
        try {
            Logger.dc(TAG, "generating key pair...", new Object[0]);
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
            KeyPair keyPair = keyGen.generateKeyPair();
            sPublicKey = Base64.encodeToString(keyPair.getPublic().getEncoded(), 2);
            sPrivateKey = keyPair.getPrivate();
            String encryptedPrivateKey = SecurityUtils.encrypt(Base64.encodeToString(sPrivateKey.getEncoded(), 2));
            Pref.ofApp().set(Key.APP_PUBLIC_KEY, sPublicKey);
            Pref.ofApp().set(Key.APP_PRIVATE_KEY, encryptedPrivateKey);
        } catch (Throwable e) {
            throw new CipherException(e);
        }
    }
}
