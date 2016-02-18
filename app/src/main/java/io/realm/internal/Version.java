package io.realm.internal;

public class Version {
    static final int CORE_MIN_MAJOR = 0;
    static final int CORE_MIN_MINOR = 1;
    static final int CORE_MIN_PATCH = 6;
    static final int REQUIRED_JNI_VERSION = 23;

    public enum Feature {
        Feature_Debug(Version.CORE_MIN_MAJOR),
        Feature_Replication(Version.CORE_MIN_MINOR);
        
        private final int nativeFeature;

        private Feature(int nativeValue) {
            this.nativeFeature = nativeValue;
        }
    }

    static native int nativeGetAPIVersion();

    static native String nativeGetVersion();

    static native boolean nativeHasFeature(int i);

    static native boolean nativeIsAtLeast(int i, int i2, int i3);

    public static String getCoreVersion() {
        return nativeGetVersion();
    }

    public static String getVersion() {
        return getCoreVersion();
    }

    public static boolean hasFeature(Feature feature) {
        return nativeHasFeature(feature.ordinal());
    }

    public static boolean coreLibVersionCompatible(boolean throwIfNot) {
        String errTxt = Table.STRING_DEFAULT_VALUE;
        if (nativeIsAtLeast(CORE_MIN_MAJOR, CORE_MIN_MINOR, CORE_MIN_PATCH)) {
            boolean compatible = nativeGetAPIVersion() == REQUIRED_JNI_VERSION;
            if (!compatible) {
                errTxt = "Native lib API is version " + nativeGetAPIVersion() + " != " + REQUIRED_JNI_VERSION + " which is expected by the jar.";
                if (throwIfNot) {
                    throw new RuntimeException(errTxt);
                }
                System.err.println(errTxt);
            }
            return compatible;
        }
        errTxt = "Version mismatch between realm.jar (0.1.6) and native core library (" + getCoreVersion() + ")";
        if (throwIfNot) {
            throw new RuntimeException(errTxt);
        }
        System.err.println(errTxt);
        return false;
    }
}
