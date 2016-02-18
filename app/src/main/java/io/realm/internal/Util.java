package io.realm.internal;

import io.realm.RealmObject;

public class Util {

    public enum Testcase {
        Exception_ClassNotFound(0),
        Exception_NoSuchField(1),
        Exception_NoSuchMethod(2),
        Exception_IllegalArgument(3),
        Exception_IOFailed(4),
        Exception_FileNotFound(5),
        Exception_FileAccessError(6),
        Exception_IndexOutOfBounds(7),
        Exception_TableInvalid(8),
        Exception_UnsupportedOperation(9),
        Exception_OutOfMemory(10),
        Exception_FatalError(11),
        Exception_RuntimeError(12),
        Exception_RowInvalid(13);
        
        private final int nativeTestcase;

        private Testcase(int nativeValue) {
            this.nativeTestcase = nativeValue;
        }

        public String expectedResult(long parm1) {
            return Util.nativeTestcase(this.nativeTestcase, false, parm1);
        }

        public String execute(long parm1) {
            return Util.nativeTestcase(this.nativeTestcase, true, parm1);
        }
    }

    static native long nativeGetMemUsage();

    static native void nativeSetDebugLevel(int i);

    static native String nativeTestcase(int i, boolean z, long j);

    static {
        RealmCore.loadLibrary();
    }

    public static long getNativeMemUsage() {
        return nativeGetMemUsage();
    }

    public static void setDebugLevel(int level) {
        nativeSetDebugLevel(level);
    }

    static void javaPrint(String txt) {
        System.out.print(txt);
    }

    public static Class<? extends RealmObject> getOriginalModelClass(Class<? extends RealmObject> clazz) {
        Class<? extends RealmObject> superclass = clazz.getSuperclass();
        if (superclass.equals(RealmObject.class)) {
            return clazz;
        }
        return superclass;
    }
}
