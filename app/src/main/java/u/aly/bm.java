package u.aly;

/* compiled from: SDKType */
public enum bm implements cn {
    ANDROID(0),
    IOS(1),
    WINDOWS_PHONE(2),
    WINDOWS_RT(3);
    
    private final int e;

    private bm(int i) {
        this.e = i;
    }

    public int a() {
        return this.e;
    }

    public static bm a(int i) {
        switch (i) {
            case dx.a /*0*/:
                return ANDROID;
            case dx.b /*1*/:
                return IOS;
            case dx.c /*2*/:
                return WINDOWS_PHONE;
            case dx.d /*3*/:
                return WINDOWS_RT;
            default:
                return null;
        }
    }
}
