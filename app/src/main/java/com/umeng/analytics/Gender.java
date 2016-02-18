package com.umeng.analytics;

import java.util.Locale;
import u.aly.ay;
import u.aly.dx;

public enum Gender {
    Male(1) {
        public String toString() {
            return String.format(Locale.US, "Male:%d", new Object[]{Integer.valueOf(this.value)});
        }
    },
    Female(2) {
        public String toString() {
            return String.format(Locale.US, "Female:%d", new Object[]{Integer.valueOf(this.value)});
        }
    },
    Unknown(0) {
        public String toString() {
            return String.format(Locale.US, "Unknown:%d", new Object[]{Integer.valueOf(this.value)});
        }
    };
    
    public int value;

    /* renamed from: com.umeng.analytics.Gender.4 */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] a;

        static {
            a = new int[Gender.values().length];
            try {
                a[Gender.Male.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                a[Gender.Female.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                a[Gender.Unknown.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private Gender(int i) {
        this.value = i;
    }

    public int value() {
        return this.value;
    }

    public static Gender getGender(int i) {
        switch (i) {
            case dx.b /*1*/:
                return Male;
            case dx.c /*2*/:
                return Female;
            default:
                return Unknown;
        }
    }

    public static ay transGender(Gender gender) {
        switch (AnonymousClass4.a[gender.ordinal()]) {
            case dx.b /*1*/:
                return ay.MALE;
            case dx.c /*2*/:
                return ay.FEMALE;
            default:
                return ay.UNKNOWN;
        }
    }
}
