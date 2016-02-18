package com.alipay.android.phone.mrpc.core;

import java.io.Closeable;
import java.io.IOException;

public final class y {
    public static void a(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }
}
