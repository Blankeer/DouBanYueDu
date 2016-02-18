package com.alipay.security.mobile.module.deviceinfo;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

final class c implements FileFilter {
    final /* synthetic */ b a;

    c(b bVar) {
        this.a = bVar;
    }

    public final boolean accept(File file) {
        return Pattern.matches("cpu[0-9]+", file.getName());
    }
}
