package com.igexin.push.d;

import com.igexin.push.core.c;
import com.igexin.push.core.d;

/* synthetic */ class h {
    static final /* synthetic */ int[] a;
    static final /* synthetic */ int[] b;

    static {
        b = new int[d.a().length];
        try {
            b[d.init.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            b[d.prepare.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            b[d.active.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            b[d.passive.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        a = new int[c.a().length];
        try {
            a[c.start.ordinal()] = 1;
        } catch (NoSuchFieldError e5) {
        }
        try {
            a[c.analyze.ordinal()] = 2;
        } catch (NoSuchFieldError e6) {
        }
        try {
            a[c.stop.ordinal()] = 3;
        } catch (NoSuchFieldError e7) {
        }
        try {
            a[c.retire.ordinal()] = 4;
        } catch (NoSuchFieldError e8) {
        }
        try {
            a[c.determine.ordinal()] = 5;
        } catch (NoSuchFieldError e9) {
        }
        try {
            a[c.connectASNL.ordinal()] = 6;
        } catch (NoSuchFieldError e10) {
        }
        try {
            a[c.check.ordinal()] = 7;
        } catch (NoSuchFieldError e11) {
        }
    }
}
