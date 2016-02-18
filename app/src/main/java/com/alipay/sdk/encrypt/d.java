package com.alipay.sdk.encrypt;

import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public final class d {
    public static final String a = "SHA1WithRSA";
    private static final String b = "RSA";

    private static PublicKey b(String str, String str2) throws NoSuchAlgorithmException, Exception {
        return KeyFactory.getInstance(str).generatePublic(new X509EncodedKeySpec(a.a(str2)));
    }

    public static String a(String str, String str2) {
        ByteArrayOutputStream byteArrayOutputStream;
        Throwable th;
        ByteArrayOutputStream byteArrayOutputStream2 = null;
        try {
            String str3 = b;
            Key generatePublic = KeyFactory.getInstance(str3).generatePublic(new X509EncodedKeySpec(a.a(str2)));
            Cipher instance = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            instance.init(1, generatePublic);
            byte[] bytes = str.getBytes(HttpRequest.CHARSET_UTF8);
            int blockSize = instance.getBlockSize();
            ByteArrayOutputStream byteArrayOutputStream3 = new ByteArrayOutputStream();
            int i = 0;
            while (i < bytes.length) {
                try {
                    int length;
                    if (bytes.length - i < blockSize) {
                        length = bytes.length - i;
                    } else {
                        length = blockSize;
                    }
                    byteArrayOutputStream3.write(instance.doFinal(bytes, i, length));
                    i += blockSize;
                } catch (Exception e) {
                    byteArrayOutputStream = byteArrayOutputStream3;
                } catch (Throwable th2) {
                    th = th2;
                    byteArrayOutputStream2 = byteArrayOutputStream3;
                }
            }
            str3 = new String(a.a(byteArrayOutputStream3.toByteArray()));
            try {
                byteArrayOutputStream3.close();
                return str3;
            } catch (IOException e2) {
                return str3;
            }
        } catch (Exception e3) {
            byteArrayOutputStream = null;
            if (byteArrayOutputStream == null) {
                return null;
            }
            try {
                byteArrayOutputStream.close();
                return null;
            } catch (IOException e4) {
                return null;
            }
        } catch (Throwable th3) {
            th = th3;
            if (byteArrayOutputStream2 != null) {
                try {
                    byteArrayOutputStream2.close();
                } catch (IOException e5) {
                }
            }
            throw th;
        }
    }

    private static String c(String str, String str2) {
        ByteArrayOutputStream byteArrayOutputStream;
        Throwable th;
        ByteArrayOutputStream byteArrayOutputStream2 = null;
        try {
            Key generatePrivate = KeyFactory.getInstance(b).generatePrivate(new PKCS8EncodedKeySpec(a.a(str2)));
            Cipher instance = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            instance.init(2, generatePrivate);
            byte[] a = a.a(str);
            int blockSize = instance.getBlockSize();
            ByteArrayOutputStream byteArrayOutputStream3 = new ByteArrayOutputStream();
            int i = 0;
            while (i < a.length) {
                try {
                    int length;
                    if (a.length - i < blockSize) {
                        length = a.length - i;
                    } else {
                        length = blockSize;
                    }
                    byteArrayOutputStream3.write(instance.doFinal(a, i, length));
                    i += blockSize;
                } catch (Exception e) {
                    byteArrayOutputStream = byteArrayOutputStream3;
                } catch (Throwable th2) {
                    th = th2;
                    byteArrayOutputStream2 = byteArrayOutputStream3;
                }
            }
            String str3 = new String(byteArrayOutputStream3.toByteArray());
            try {
                byteArrayOutputStream3.close();
                return str3;
            } catch (IOException e2) {
                return str3;
            }
        } catch (Exception e3) {
            byteArrayOutputStream = null;
            if (byteArrayOutputStream == null) {
                return null;
            }
            try {
                byteArrayOutputStream.close();
                return null;
            } catch (IOException e4) {
                return null;
            }
        } catch (Throwable th3) {
            th = th3;
            if (byteArrayOutputStream2 != null) {
                try {
                    byteArrayOutputStream2.close();
                } catch (IOException e5) {
                }
            }
            throw th;
        }
    }

    private static String d(String str, String str2) {
        String str3 = "utf-8";
        try {
            PrivateKey generatePrivate = KeyFactory.getInstance(b).generatePrivate(new PKCS8EncodedKeySpec(a.a(str2)));
            Signature instance = Signature.getInstance(a);
            instance.initSign(generatePrivate);
            instance.update(str.getBytes(str3));
            return a.a(instance.sign());
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean a(String str, String str2, String str3) {
        try {
            PublicKey generatePublic = KeyFactory.getInstance(b).generatePublic(new X509EncodedKeySpec(a.a(str3)));
            Signature instance = Signature.getInstance(a);
            instance.initVerify(generatePublic);
            instance.update(str.getBytes("utf-8"));
            return instance.verify(a.a(str2));
        } catch (Exception e) {
            return false;
        }
    }
}
