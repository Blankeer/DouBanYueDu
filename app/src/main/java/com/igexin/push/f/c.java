package com.igexin.push.f;

import com.igexin.push.config.k;
import java.util.Random;

public class c {
    private static final String b;
    private static char[] c;
    public int a;
    private Random d;

    static {
        b = k.a;
        c = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz`~!@#$%^&*()-_=+[{}];:'/?.>,<".toCharArray();
    }

    public c() {
        this.a = 16;
        this.d = new Random(System.currentTimeMillis());
    }
}
