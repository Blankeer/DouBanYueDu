package com.douban.book.reader.content.cipher;

import com.douban.book.reader.exception.CipherException;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Tag;
import java.security.InvalidKeyException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherFactory {
    private static final ThreadLocal<Cipher> CIPHER_POOL;
    private IvParameterSpec mIv;
    private String mIvStr;
    private SecretKeySpec mKey;
    private String mKeyStr;

    static {
        CIPHER_POOL = new ThreadLocal<Cipher>() {
            protected Cipher initialValue() {
                try {
                    return Cipher.getInstance("AES/CBC/PKCS5Padding");
                } catch (Exception e) {
                    Logger.e(Tag.MANIFEST, e);
                    return null;
                }
            }
        };
    }

    public CipherFactory(String key, String iv) {
        this.mIv = null;
        this.mKey = null;
        this.mKeyStr = null;
        this.mIvStr = null;
        this.mKeyStr = key;
        this.mIvStr = iv;
    }

    private void init() throws CipherException {
        try {
            if (StringUtils.isEmpty(this.mKeyStr) || StringUtils.isEmpty(this.mIvStr)) {
                throw new InvalidKeyException(String.format("Key or Iv is empty. key=%s, iv=%s", new Object[]{this.mKeyStr, this.mIvStr}));
            }
            byte[] key = hexStringToByteArray(this.mKeyStr);
            byte[] iv = hexStringToByteArray(this.mIvStr);
            Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
            cipher.init(2, ArkKeyPair.getPrivate());
            byte[] decipheredKey = cipher.doFinal(key);
            byte[] decipheredIv = cipher.doFinal(iv);
            this.mKey = new SecretKeySpec(decipheredKey, "AES");
            this.mIv = new IvParameterSpec(decipheredIv, 0, 16);
        } catch (Throwable e) {
            throw new CipherException(e);
        }
    }

    public Cipher getCipher() throws CipherException {
        try {
            if (this.mKey == null || this.mIv == null) {
                init();
            }
            Cipher cipher = (Cipher) CIPHER_POOL.get();
            if (cipher != null) {
                cipher.init(2, this.mKey, this.mIv);
            }
            return cipher;
        } catch (Throwable e) {
            throw new CipherException(e);
        }
    }

    private static byte[] hexStringToByteArray(CharSequence hexStr) {
        int len = hexStr.length();
        byte[] data = new byte[(len / 2)];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4) + Character.digit(hexStr.charAt(i + 1), 16));
        }
        return data;
    }
}
