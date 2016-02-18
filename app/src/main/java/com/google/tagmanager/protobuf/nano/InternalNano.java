package com.google.tagmanager.protobuf.nano;

import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.UnsupportedEncodingException;

public class InternalNano {
    public static final String stringDefaultValue(String bytes) {
        try {
            return new String(bytes.getBytes("ISO-8859-1"), HttpRequest.CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Java VM does not support a standard character set.", e);
        }
    }

    public static final byte[] bytesDefaultValue(String bytes) {
        try {
            return bytes.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Java VM does not support a standard character set.", e);
        }
    }

    public static final byte[] copyFromUtf8(String text) {
        try {
            return text.getBytes(HttpRequest.CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported?");
        }
    }
}
