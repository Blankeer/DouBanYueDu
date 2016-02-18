package com.alipay.security.mobile.module.commonutils.crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class e {
    private static final f a;

    static {
        a = new f();
    }

    private static int a(String str, OutputStream outputStream) {
        return a.a(str, outputStream);
    }

    private static int a(byte[] bArr, int i, int i2, OutputStream outputStream) {
        return a.a(bArr, i, i2, outputStream);
    }

    private static int a(byte[] bArr, OutputStream outputStream) {
        return a.a(bArr, 0, bArr.length, outputStream);
    }

    public static byte[] a(String str) {
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            a.a(str, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("exception decoding Hex string: " + e);
        }
    }

    private static byte[] a(byte[] bArr) {
        return a(bArr, bArr.length);
    }

    private static byte[] a(byte[] bArr, int i) {
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            a.a(bArr, 0, i, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("exception encoding Hex string: " + e);
        }
    }

    private static byte[] b(byte[] bArr) {
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            a.a(bArr, bArr.length, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("exception decoding Hex string: " + e);
        }
    }
}
