package com.alipay.android.phone.mrpc.core;

public final class c extends RuntimeException {
    private static final long b = -2875437994101380406L;
    String a;
    private int c;
    private String d;

    public c(Integer num, String str) {
        super(a(num, str));
        this.c = num.intValue();
        this.d = str;
    }

    public c(Integer num, String str, Throwable th) {
        super(a(num, str), th);
        this.c = num.intValue();
        this.d = str;
    }

    private c(Integer num, Throwable th) {
        super(th);
        this.c = num.intValue();
    }

    private c(String str) {
        super(str);
        this.c = 0;
        this.d = str;
    }

    private String a() {
        return this.a;
    }

    private static String a(Integer num, String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("RPCException: ");
        if (num != null) {
            stringBuilder.append("[").append(num).append("]");
        }
        stringBuilder.append(" : ");
        if (str != null) {
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }

    private void a(String str) {
        this.a = str;
    }

    private int b() {
        return this.c;
    }

    private String c() {
        return this.d;
    }
}
