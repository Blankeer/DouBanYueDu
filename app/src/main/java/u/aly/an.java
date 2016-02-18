package u.aly;

/* compiled from: AccessType */
public enum an implements cn {
    ACCESS_TYPE_UNKNOWN(0),
    ACCESS_TYPE_2G_3G(1),
    ACCESS_TYPE_WIFI(2),
    ACCESS_TYPE_ETHERNET(3);
    
    private final int e;

    private an(int i) {
        this.e = i;
    }

    public int a() {
        return this.e;
    }

    public static an a(int i) {
        switch (i) {
            case dx.a /*0*/:
                return ACCESS_TYPE_UNKNOWN;
            case dx.b /*1*/:
                return ACCESS_TYPE_2G_3G;
            case dx.c /*2*/:
                return ACCESS_TYPE_WIFI;
            case dx.d /*3*/:
                return ACCESS_TYPE_ETHERNET;
            default:
                return null;
        }
    }
}
