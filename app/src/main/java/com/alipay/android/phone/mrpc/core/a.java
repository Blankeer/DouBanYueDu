package com.alipay.android.phone.mrpc.core;

public final class a extends Exception {
    public static final int a = 0;
    public static final int b = 1;
    public static final int c = 2;
    public static final int d = 3;
    public static final int e = 4;
    public static final int f = 5;
    public static final int g = 6;
    public static final int h = 7;
    public static final int i = 8;
    public static final int j = 9;
    private static final long m = -6320569206365033676L;
    int k;
    String l;

    public a(Integer num, String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Http Transport error");
        if (num != null) {
            stringBuilder.append("[").append(num).append("]");
        }
        stringBuilder.append(" : ");
        if (str != null) {
            stringBuilder.append(str);
        }
        super(stringBuilder.toString());
        this.k = num.intValue();
        this.l = str;
    }

    private a(String str) {
        super(str);
        this.k = a;
        this.l = str;
    }

    private int a() {
        return this.k;
    }

    private String b() {
        return this.l;
    }
}
