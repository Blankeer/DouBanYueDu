package com.douban.book.reader.util;

import com.douban.book.reader.constant.Constants;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtils {
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String DELIMITER = "]";
    private static final int ITERATION_COUNT = 1000;
    public static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 8;
    private static SecureRandom random;

    static {
        random = new SecureRandom();
    }

    public static String encrypt(String plaintext) throws Exception {
        byte[] salt = generateSalt();
        return encrypt(plaintext, getKey(salt, getPassword()), salt);
    }

    private static String encrypt(String plaintext, SecretKey key, byte[] salt) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
            cipher.init(1, key, new IvParameterSpec(generateIv(cipher.getBlockSize())));
            byte[] cipherText = cipher.doFinal(plaintext.getBytes(HttpRequest.CHARSET_UTF8));
            if (salt != null) {
                return String.format("%s%s%s%s%s", new Object[]{new String(Base64.encode(salt, 2)), DELIMITER, new String(Base64.encode(iv, 2)), DELIMITER, new String(Base64.encode(cipherText, 2))});
            }
            return String.format("%s%s%s", new Object[]{new String(Base64.encode(iv, 2)), DELIMITER, new String(Base64.encode(cipherText, 2))});
        } catch (Throwable e) {
            Exception exception = new Exception("Error while encryption", e);
        }
    }

    public static String decrypt(String ciphertext) throws Exception {
        return decrypt(ciphertext, getPassword());
    }

    private static String decrypt(String ciphertext, String password) throws Exception {
        String[] fields = ciphertext.split(DELIMITER);
        if (fields.length != 3) {
            throw new IllegalArgumentException("Invalid encypted text format");
        }
        try {
            byte[] salt = Base64.decode(fields[0], 2);
            byte[] iv = Base64.decode(fields[1], 2);
            byte[] cipherBytes = Base64.decode(fields[2], 2);
            SecretKey key = getKey(salt, password);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
            cipher.init(2, key, new IvParameterSpec(iv));
            return new String(cipher.doFinal(cipherBytes), HttpRequest.CHARSET_UTF8);
        } catch (Throwable e) {
            Exception exception = new Exception("Error while decryption", e);
        }
    }

    private static String getPassword() {
        return Constants.MAGIC;
    }

    private static SecretKey getKey(byte[] salt, String password) throws Exception {
        try {
            return new SecretKeySpec(SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM, "BC").generateSecret(new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)).getEncoded(), "AES");
        } catch (Throwable e) {
            Exception exception = new Exception("Error while generating key", e);
        }
    }

    private static byte[] generateIv(int length) {
        byte[] b = new byte[length];
        random.nextBytes(b);
        return b;
    }

    private static byte[] generateSalt() {
        byte[] b = new byte[SALT_LENGTH];
        random.nextBytes(b);
        return b;
    }
}
