package io.realm.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class RealmCore {
    private static final String BINARIES_PATH;
    private static final String FILE_SEP;
    private static final String JAVA_LIBRARY_PATH = "java.library.path";
    private static final String PATH_SEP;
    private static AtomicBoolean libraryIsLoaded;

    static {
        FILE_SEP = File.separator;
        PATH_SEP = File.pathSeparator;
        BINARIES_PATH = "lib" + PATH_SEP + ".." + FILE_SEP + "lib";
        libraryIsLoaded = new AtomicBoolean(false);
    }

    public static boolean osIsWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.getDefault()).contains("win");
    }

    public static byte[] serialize(Serializable value) {
        try {
            ByteArrayOutputStream mem = new ByteArrayOutputStream();
            ObjectOutputStream output = new ObjectOutputStream(mem);
            output.writeObject(value);
            output.close();
            return mem.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Cannot serialize the object!", e);
        }
    }

    public static Serializable deserialize(ByteBuffer buf) {
        return deserialize(buf.array());
    }

    public static Serializable deserialize(byte[] value) {
        try {
            ObjectInputStream output = new ObjectInputStream(new ByteArrayInputStream(value));
            Object obj = output.readObject();
            output.close();
            return (Serializable) obj;
        } catch (Exception e) {
            throw new RuntimeException("Cannot deserialize the object!", e);
        }
    }

    public static void gcGuaranteed() {
        WeakReference<Object> ref = new WeakReference(new Object());
        while (ref.get() != null) {
            System.gc();
        }
    }

    public static void gcOnExit() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                RealmCore.gcGuaranteed();
            }
        });
    }

    private static void init() {
        gcOnExit();
    }

    public static void loadLibrary() {
        if (!libraryIsLoaded.get()) {
            init();
            if (osIsWindows()) {
                loadLibraryWindows();
            } else {
                String jnilib;
                String debug = System.getenv("REALM_JAVA_DEBUG");
                if (debug == null || debug.isEmpty()) {
                    jnilib = "realm-jni";
                } else {
                    jnilib = "realm-jni-dbg";
                }
                System.loadLibrary(jnilib);
            }
            libraryIsLoaded.set(true);
            Version.coreLibVersionCompatible(true);
        }
    }

    private static String loadLibraryWindows() {
        try {
            addNativeLibraryPath(BINARIES_PATH);
            resetLibraryPath();
        } catch (Throwable th) {
        }
        String jnilib = loadCorrectLibrary("realm_jni32d", "realm_jni64d");
        if (jnilib != null) {
            System.out.println("!!! Realm debug version loaded. !!!\n");
        } else {
            jnilib = loadCorrectLibrary("realm_jni32", "realm_jni64");
            if (jnilib == null) {
                System.err.println("Searched java.library.path=" + System.getProperty(JAVA_LIBRARY_PATH));
                throw new RuntimeException("Couldn't load the Realm JNI library 'realm_jni32.dll or realm_jni64.dll'. Please include the directory to the library in java.library.path.");
            }
        }
        return jnilib;
    }

    private static String loadCorrectLibrary(String... libraryCandidateNames) {
        String[] arr$ = libraryCandidateNames;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            String libraryCandidateName = arr$[i$];
            try {
                System.loadLibrary(libraryCandidateName);
                return libraryCandidateName;
            } catch (Throwable th) {
                i$++;
            }
        }
        return null;
    }

    public static void addNativeLibraryPath(String path) {
        try {
            System.setProperty(JAVA_LIBRARY_PATH, System.getProperty(JAVA_LIBRARY_PATH) + PATH_SEP + path + PATH_SEP);
        } catch (Exception e) {
            throw new RuntimeException("Cannot set the library path!", e);
        }
    }

    private static void resetLibraryPath() {
        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException("Cannot reset the library path!", e);
        }
    }
}
