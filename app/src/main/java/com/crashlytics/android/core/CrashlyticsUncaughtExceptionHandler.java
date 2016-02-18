package com.crashlytics.android.core;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import com.crashlytics.android.core.internal.models.SessionEventData;
import com.tencent.connect.common.Constants;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Logger;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.DeliveryMechanism;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.persistence.FileStore;
import io.fabric.sdk.android.services.settings.SessionSettingsData;
import io.fabric.sdk.android.services.settings.Settings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CrashlyticsUncaughtExceptionHandler implements UncaughtExceptionHandler {
    private static final int ANALYZER_VERSION = 1;
    static final FilenameFilter ANY_SESSION_FILENAME_FILTER;
    private static final String EVENT_TYPE_CRASH = "crash";
    private static final String EVENT_TYPE_LOGGED = "error";
    private static final String GENERATOR_FORMAT = "Crashlytics Android SDK/%s";
    private static final String[] INITIAL_SESSION_PART_TAGS;
    static final String INVALID_CLS_CACHE_DIR = "invalidClsFiles";
    static final Comparator<File> LARGEST_FILE_NAME_FIRST;
    private static final int MAX_COMPLETE_SESSIONS_COUNT = 4;
    private static final int MAX_LOCAL_LOGGED_EXCEPTIONS = 64;
    static final int MAX_OPEN_SESSIONS = 8;
    private static final Map<String, String> SEND_AT_CRASHTIME_HEADER;
    static final String SESSION_APP_TAG = "SessionApp";
    static final String SESSION_BEGIN_TAG = "BeginSession";
    static final String SESSION_DEVICE_TAG = "SessionDevice";
    static final String SESSION_FATAL_TAG = "SessionCrash";
    static final FilenameFilter SESSION_FILE_FILTER;
    private static final Pattern SESSION_FILE_PATTERN;
    private static final int SESSION_ID_LENGTH = 35;
    static final String SESSION_NON_FATAL_TAG = "SessionEvent";
    static final String SESSION_OS_TAG = "SessionOS";
    static final String SESSION_USER_TAG = "SessionUser";
    static final Comparator<File> SMALLEST_FILE_NAME_FIRST;
    private final CrashlyticsCore crashlyticsCore;
    private final UncaughtExceptionHandler defaultHandler;
    private final DevicePowerStateListener devicePowerStateListener;
    private final AtomicInteger eventCounter;
    private final CrashlyticsExecutorServiceWrapper executorServiceWrapper;
    private final FileStore fileStore;
    private final IdManager idManager;
    private final AtomicBoolean isHandlingException;
    private final LogFileManager logFileManager;
    private final String unityVersion;

    /* renamed from: com.crashlytics.android.core.CrashlyticsUncaughtExceptionHandler.13 */
    class AnonymousClass13 implements FilenameFilter {
        final /* synthetic */ String val$sessionId;

        AnonymousClass13(String str) {
            this.val$sessionId = str;
        }

        public boolean accept(File f, String name) {
            return name.startsWith(this.val$sessionId);
        }
    }

    /* renamed from: com.crashlytics.android.core.CrashlyticsUncaughtExceptionHandler.5 */
    class AnonymousClass5 implements Callable<Void> {
        final /* synthetic */ Throwable val$ex;
        final /* synthetic */ Date val$now;
        final /* synthetic */ Thread val$thread;

        AnonymousClass5(Date date, Thread thread, Throwable th) {
            this.val$now = date;
            this.val$thread = thread;
            this.val$ex = th;
        }

        public Void call() throws Exception {
            CrashlyticsUncaughtExceptionHandler.this.handleUncaughtException(this.val$now, this.val$thread, this.val$ex);
            return null;
        }
    }

    /* renamed from: com.crashlytics.android.core.CrashlyticsUncaughtExceptionHandler.6 */
    class AnonymousClass6 implements Callable<Void> {
        final /* synthetic */ String val$msg;
        final /* synthetic */ long val$timestamp;

        AnonymousClass6(long j, String str) {
            this.val$timestamp = j;
            this.val$msg = str;
        }

        public Void call() throws Exception {
            if (!CrashlyticsUncaughtExceptionHandler.this.isHandlingException.get()) {
                CrashlyticsUncaughtExceptionHandler.this.logFileManager.writeToLog(this.val$timestamp, this.val$msg);
            }
            return null;
        }
    }

    /* renamed from: com.crashlytics.android.core.CrashlyticsUncaughtExceptionHandler.7 */
    class AnonymousClass7 implements Runnable {
        final /* synthetic */ Throwable val$ex;
        final /* synthetic */ Date val$now;
        final /* synthetic */ Thread val$thread;

        AnonymousClass7(Date date, Thread thread, Throwable th) {
            this.val$now = date;
            this.val$thread = thread;
            this.val$ex = th;
        }

        public void run() {
            if (!CrashlyticsUncaughtExceptionHandler.this.isHandlingException.get()) {
                CrashlyticsUncaughtExceptionHandler.this.doWriteNonFatal(this.val$now, this.val$thread, this.val$ex);
            }
        }
    }

    /* renamed from: com.crashlytics.android.core.CrashlyticsUncaughtExceptionHandler.8 */
    class AnonymousClass8 implements Callable<Void> {
        final /* synthetic */ String val$userEmail;
        final /* synthetic */ String val$userId;
        final /* synthetic */ String val$userName;

        AnonymousClass8(String str, String str2, String str3) {
            this.val$userId = str;
            this.val$userName = str2;
            this.val$userEmail = str3;
        }

        public Void call() throws Exception {
            new MetaDataStore(CrashlyticsUncaughtExceptionHandler.this.getFilesDir()).writeUserData(CrashlyticsUncaughtExceptionHandler.this.getCurrentSessionId(), new UserMetaData(this.val$userId, this.val$userName, this.val$userEmail));
            return null;
        }
    }

    /* renamed from: com.crashlytics.android.core.CrashlyticsUncaughtExceptionHandler.9 */
    class AnonymousClass9 implements Callable<Void> {
        final /* synthetic */ Map val$keyData;

        AnonymousClass9(Map map) {
            this.val$keyData = map;
        }

        public Void call() throws Exception {
            new MetaDataStore(CrashlyticsUncaughtExceptionHandler.this.getFilesDir()).writeKeyData(CrashlyticsUncaughtExceptionHandler.this.getCurrentSessionId(), this.val$keyData);
            return null;
        }
    }

    private static class AnySessionPartFileFilter implements FilenameFilter {
        private AnySessionPartFileFilter() {
        }

        public boolean accept(File file, String fileName) {
            return !CrashlyticsUncaughtExceptionHandler.SESSION_FILE_FILTER.accept(file, fileName) && CrashlyticsUncaughtExceptionHandler.SESSION_FILE_PATTERN.matcher(fileName).matches();
        }
    }

    static class FileNameContainsFilter implements FilenameFilter {
        private final String string;

        public FileNameContainsFilter(String s) {
            this.string = s;
        }

        public boolean accept(File dir, String filename) {
            return filename.contains(this.string) && !filename.endsWith(ClsFileOutputStream.IN_PROGRESS_SESSION_FILE_EXTENSION);
        }
    }

    private static final class SendSessionRunnable implements Runnable {
        private final CrashlyticsCore crashlyticsCore;
        private final File fileToSend;

        public SendSessionRunnable(CrashlyticsCore crashlyticsCore, File fileToSend) {
            this.crashlyticsCore = crashlyticsCore;
            this.fileToSend = fileToSend;
        }

        public void run() {
            if (CommonUtils.canTryConnection(this.crashlyticsCore.getContext())) {
                Fabric.getLogger().d(CrashlyticsCore.TAG, "Attempting to send crash report at time of crash...");
                CreateReportSpiCall call = this.crashlyticsCore.getCreateReportSpiCall(Settings.getInstance().awaitSettingsData());
                if (call != null) {
                    new ReportUploader(call).forceUpload(new SessionReport(this.fileToSend, CrashlyticsUncaughtExceptionHandler.SEND_AT_CRASHTIME_HEADER));
                }
            }
        }
    }

    static class SessionPartFileFilter implements FilenameFilter {
        private final String sessionId;

        public SessionPartFileFilter(String sessionId) {
            this.sessionId = sessionId;
        }

        public boolean accept(File file, String fileName) {
            if (fileName.equals(this.sessionId + ClsFileOutputStream.SESSION_FILE_EXTENSION) || !fileName.contains(this.sessionId) || fileName.endsWith(ClsFileOutputStream.IN_PROGRESS_SESSION_FILE_EXTENSION)) {
                return false;
            }
            return true;
        }
    }

    static {
        SESSION_FILE_FILTER = new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.length() == ClsFileOutputStream.SESSION_FILE_EXTENSION.length() + CrashlyticsUncaughtExceptionHandler.SESSION_ID_LENGTH && filename.endsWith(ClsFileOutputStream.SESSION_FILE_EXTENSION);
            }
        };
        LARGEST_FILE_NAME_FIRST = new Comparator<File>() {
            public int compare(File file1, File file2) {
                return file2.getName().compareTo(file1.getName());
            }
        };
        SMALLEST_FILE_NAME_FIRST = new Comparator<File>() {
            public int compare(File file1, File file2) {
                return file1.getName().compareTo(file2.getName());
            }
        };
        ANY_SESSION_FILENAME_FILTER = new FilenameFilter() {
            public boolean accept(File file, String filename) {
                return CrashlyticsUncaughtExceptionHandler.SESSION_FILE_PATTERN.matcher(filename).matches();
            }
        };
        SESSION_FILE_PATTERN = Pattern.compile("([\\d|A-Z|a-z]{12}\\-[\\d|A-Z|a-z]{4}\\-[\\d|A-Z|a-z]{4}\\-[\\d|A-Z|a-z]{12}).+");
        SEND_AT_CRASHTIME_HEADER = Collections.singletonMap("X-CRASHLYTICS-SEND-FLAGS", Constants.VIA_TO_TYPE_QQ_GROUP);
        String[] strArr = new String[MAX_COMPLETE_SESSIONS_COUNT];
        strArr[0] = SESSION_USER_TAG;
        strArr[ANALYZER_VERSION] = SESSION_APP_TAG;
        strArr[2] = SESSION_OS_TAG;
        strArr[3] = SESSION_DEVICE_TAG;
        INITIAL_SESSION_PART_TAGS = strArr;
    }

    CrashlyticsUncaughtExceptionHandler(UncaughtExceptionHandler handler, CrashlyticsExecutorServiceWrapper executorServiceWrapper, IdManager idManager, UnityVersionProvider unityVersionProvider, FileStore fileStore, CrashlyticsCore crashlyticsCore) {
        this.eventCounter = new AtomicInteger(0);
        this.defaultHandler = handler;
        this.executorServiceWrapper = executorServiceWrapper;
        this.idManager = idManager;
        this.crashlyticsCore = crashlyticsCore;
        this.unityVersion = unityVersionProvider.getUnityVersion();
        this.fileStore = fileStore;
        this.isHandlingException = new AtomicBoolean(false);
        Context context = crashlyticsCore.getContext();
        this.logFileManager = new LogFileManager(context, fileStore);
        this.devicePowerStateListener = new DevicePowerStateListener(context);
    }

    public synchronized void uncaughtException(Thread thread, Throwable ex) {
        this.isHandlingException.set(true);
        try {
            Fabric.getLogger().d(CrashlyticsCore.TAG, "Crashlytics is handling uncaught exception \"" + ex + "\" from thread " + thread.getName());
            this.devicePowerStateListener.dispose();
            this.executorServiceWrapper.executeSyncLoggingException(new AnonymousClass5(new Date(), thread, ex));
            Fabric.getLogger().d(CrashlyticsCore.TAG, "Crashlytics completed exception processing. Invoking default exception handler.");
            this.defaultHandler.uncaughtException(thread, ex);
            this.isHandlingException.set(false);
        } catch (Exception e) {
            Fabric.getLogger().e(CrashlyticsCore.TAG, "An error occurred in the uncaught exception handler", e);
            Fabric.getLogger().d(CrashlyticsCore.TAG, "Crashlytics completed exception processing. Invoking default exception handler.");
            this.defaultHandler.uncaughtException(thread, ex);
            this.isHandlingException.set(false);
        } catch (Throwable th) {
            Fabric.getLogger().d(CrashlyticsCore.TAG, "Crashlytics completed exception processing. Invoking default exception handler.");
            this.defaultHandler.uncaughtException(thread, ex);
            this.isHandlingException.set(false);
        }
    }

    private void handleUncaughtException(Date time, Thread thread, Throwable ex) throws Exception {
        this.crashlyticsCore.createCrashMarker();
        writeFatal(time, thread, ex);
        doCloseSessions();
        doOpenSession();
        trimSessionFiles();
        if (!this.crashlyticsCore.shouldPromptUserBeforeSendingCrashReports()) {
            sendSessionReports();
        }
    }

    boolean isHandlingException() {
        return this.isHandlingException.get();
    }

    void writeToLog(long timestamp, String msg) {
        this.executorServiceWrapper.executeAsync(new AnonymousClass6(timestamp, msg));
    }

    void writeNonFatalException(Thread thread, Throwable ex) {
        this.executorServiceWrapper.executeAsync(new AnonymousClass7(new Date(), thread, ex));
    }

    void cacheUserData(String userId, String userName, String userEmail) {
        this.executorServiceWrapper.executeAsync(new AnonymousClass8(userId, userName, userEmail));
    }

    void cacheKeyData(Map<String, String> keyData) {
        this.executorServiceWrapper.executeAsync(new AnonymousClass9(keyData));
    }

    void openSession() {
        this.executorServiceWrapper.executeAsync(new Callable<Void>() {
            public Void call() throws Exception {
                CrashlyticsUncaughtExceptionHandler.this.doOpenSession();
                return null;
            }
        });
    }

    private String getCurrentSessionId() {
        File[] sessionBeginFiles = listSortedSessionBeginFiles();
        return sessionBeginFiles.length > 0 ? getSessionIdFromSessionFile(sessionBeginFiles[0]) : null;
    }

    private String getPreviousSessionId() {
        File[] sessionBeginFiles = listSortedSessionBeginFiles();
        return sessionBeginFiles.length > ANALYZER_VERSION ? getSessionIdFromSessionFile(sessionBeginFiles[ANALYZER_VERSION]) : null;
    }

    private String getSessionIdFromSessionFile(File sessionFile) {
        return sessionFile.getName().substring(0, SESSION_ID_LENGTH);
    }

    boolean hasOpenSession() {
        return listSessionBeginFiles().length > 0;
    }

    boolean finalizeSessions() {
        return ((Boolean) this.executorServiceWrapper.executeSyncLoggingException(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                if (CrashlyticsUncaughtExceptionHandler.this.isHandlingException.get()) {
                    Fabric.getLogger().d(CrashlyticsCore.TAG, "Skipping session finalization because a crash has already occurred.");
                    return Boolean.FALSE;
                }
                Fabric.getLogger().d(CrashlyticsCore.TAG, "Finalizing previously open sessions.");
                SessionEventData crashEventData = CrashlyticsUncaughtExceptionHandler.this.crashlyticsCore.getExternalCrashEventData();
                if (crashEventData != null) {
                    CrashlyticsUncaughtExceptionHandler.this.writeExternalCrashEvent(crashEventData);
                }
                CrashlyticsUncaughtExceptionHandler.this.doCloseSessions(true);
                Fabric.getLogger().d(CrashlyticsCore.TAG, "Closed all previously open sessions");
                return Boolean.TRUE;
            }
        })).booleanValue();
    }

    private void doOpenSession() throws Exception {
        Date startedAt = new Date();
        String sessionIdentifier = new CLSUUID(this.idManager).toString();
        Fabric.getLogger().d(CrashlyticsCore.TAG, "Opening an new session with ID " + sessionIdentifier);
        writeBeginSession(sessionIdentifier, startedAt);
        writeSessionApp(sessionIdentifier);
        writeSessionOS(sessionIdentifier);
        writeSessionDevice(sessionIdentifier);
        this.logFileManager.setCurrentSession(sessionIdentifier);
    }

    void doCloseSessions() throws Exception {
        doCloseSessions(false);
    }

    private void doCloseSessions(boolean excludeCurrent) throws Exception {
        int offset = excludeCurrent ? ANALYZER_VERSION : 0;
        trimOpenSessions(offset + MAX_OPEN_SESSIONS);
        File[] sessionBeginFiles = listSortedSessionBeginFiles();
        if (sessionBeginFiles.length <= offset) {
            Fabric.getLogger().d(CrashlyticsCore.TAG, "No open sessions to be closed.");
            return;
        }
        writeSessionUser(getSessionIdFromSessionFile(sessionBeginFiles[offset]));
        CrashlyticsCore crashlyticsCore = this.crashlyticsCore;
        SessionSettingsData settingsData = CrashlyticsCore.getSessionSettingsData();
        if (settingsData == null) {
            Fabric.getLogger().d(CrashlyticsCore.TAG, "Unable to close session. Settings are not loaded.");
        } else {
            closeOpenSessions(sessionBeginFiles, offset, settingsData.maxCustomExceptionEvents);
        }
    }

    private void closeOpenSessions(File[] sessionBeginFiles, int beginIndex, int maxLoggedExceptionsCount) {
        Fabric.getLogger().d(CrashlyticsCore.TAG, "Closing open sessions.");
        for (int i = beginIndex; i < sessionBeginFiles.length; i += ANALYZER_VERSION) {
            File sessionBeginFile = sessionBeginFiles[i];
            String sessionIdentifier = getSessionIdFromSessionFile(sessionBeginFile);
            Fabric.getLogger().d(CrashlyticsCore.TAG, "Closing session: " + sessionIdentifier);
            writeSessionPartsToSessionFile(sessionBeginFile, sessionIdentifier, maxLoggedExceptionsCount);
        }
    }

    private void closeWithoutRenamingOrLog(ClsFileOutputStream fos) {
        if (fos != null) {
            try {
                fos.closeInProgressStream();
            } catch (IOException ex) {
                Fabric.getLogger().e(CrashlyticsCore.TAG, "Error closing session file stream in the presence of an exception", ex);
            }
        }
    }

    private void deleteSessionPartFilesFor(String sessionId) {
        File[] arr$ = listSessionPartFilesFor(sessionId);
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$ += ANALYZER_VERSION) {
            arr$[i$].delete();
        }
    }

    private File[] listSessionPartFilesFor(String sessionId) {
        return listFilesMatching(new SessionPartFileFilter(sessionId));
    }

    private File[] listCompleteSessionFiles() {
        return listFilesMatching(SESSION_FILE_FILTER);
    }

    File[] listSessionBeginFiles() {
        return listFilesMatching(new FileNameContainsFilter(SESSION_BEGIN_TAG));
    }

    private File[] listSortedSessionBeginFiles() {
        File[] sessionBeginFiles = listSessionBeginFiles();
        Arrays.sort(sessionBeginFiles, LARGEST_FILE_NAME_FIRST);
        return sessionBeginFiles;
    }

    private File[] listFilesMatching(FilenameFilter filter) {
        return ensureFileArrayNotNull(getFilesDir().listFiles(filter));
    }

    private File[] ensureFileArrayNotNull(File[] files) {
        return files == null ? new File[0] : files;
    }

    private void trimSessionEventFiles(String sessionId, int limit) {
        Utils.capFileCount(getFilesDir(), new FileNameContainsFilter(sessionId + SESSION_NON_FATAL_TAG), limit, SMALLEST_FILE_NAME_FIRST);
    }

    void trimSessionFiles() {
        Utils.capFileCount(getFilesDir(), SESSION_FILE_FILTER, MAX_COMPLETE_SESSIONS_COUNT, SMALLEST_FILE_NAME_FIRST);
    }

    private void trimOpenSessions(int maxOpenSessionCount) {
        Set<String> sessionIdsToKeep = new HashSet();
        File[] beginSessionFiles = listSortedSessionBeginFiles();
        int count = Math.min(maxOpenSessionCount, beginSessionFiles.length);
        for (int i = 0; i < count; i += ANALYZER_VERSION) {
            sessionIdsToKeep.add(getSessionIdFromSessionFile(beginSessionFiles[i]));
        }
        this.logFileManager.discardOldLogFiles(sessionIdsToKeep);
        File[] arr$ = listFilesMatching(new AnySessionPartFileFilter());
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$ += ANALYZER_VERSION) {
            File sessionPartFile = arr$[i$];
            String fileName = sessionPartFile.getName();
            Matcher matcher = SESSION_FILE_PATTERN.matcher(fileName);
            matcher.matches();
            if (!sessionIdsToKeep.contains(matcher.group(ANALYZER_VERSION))) {
                Fabric.getLogger().d(CrashlyticsCore.TAG, "Trimming open session file: " + fileName);
                sessionPartFile.delete();
            }
        }
    }

    private File[] getTrimmedNonFatalFiles(String sessionId, File[] nonFatalFiles, int maxLoggedExceptionsCount) {
        if (nonFatalFiles.length <= maxLoggedExceptionsCount) {
            return nonFatalFiles;
        }
        Logger logger = Fabric.getLogger();
        String str = CrashlyticsCore.TAG;
        Object[] objArr = new Object[ANALYZER_VERSION];
        objArr[0] = Integer.valueOf(maxLoggedExceptionsCount);
        logger.d(str, String.format(Locale.US, "Trimming down to %d logged exceptions.", objArr));
        trimSessionEventFiles(sessionId, maxLoggedExceptionsCount);
        return listFilesMatching(new FileNameContainsFilter(sessionId + SESSION_NON_FATAL_TAG));
    }

    void cleanInvalidTempFiles() {
        this.executorServiceWrapper.executeAsync(new Runnable() {
            public void run() {
                CrashlyticsUncaughtExceptionHandler.this.doCleanInvalidTempFiles(CrashlyticsUncaughtExceptionHandler.this.listFilesMatching(ClsFileOutputStream.TEMP_FILENAME_FILTER));
            }
        });
    }

    void doCleanInvalidTempFiles(File[] invalidFiles) {
        deleteLegacyInvalidCacheDir();
        File[] fileArr = invalidFiles;
        int len$ = fileArr.length;
        for (int i = 0; i < len$; i += ANALYZER_VERSION) {
            File invalidFile = fileArr[i];
            Fabric.getLogger().d(CrashlyticsCore.TAG, "Found invalid session part file: " + invalidFile);
            String sessionId = getSessionIdFromSessionFile(invalidFile);
            FilenameFilter sessionFilter = new AnonymousClass13(sessionId);
            Fabric.getLogger().d(CrashlyticsCore.TAG, "Deleting all part files for invalid session: " + sessionId);
            File[] arr$ = listFilesMatching(sessionFilter);
            int len$2 = arr$.length;
            for (int i$ = 0; i$ < len$2; i$ += ANALYZER_VERSION) {
                File sessionFile = arr$[i$];
                Fabric.getLogger().d(CrashlyticsCore.TAG, "Deleting session file: " + sessionFile);
                sessionFile.delete();
            }
        }
    }

    private void deleteLegacyInvalidCacheDir() {
        File cacheDir = new File(this.crashlyticsCore.getSdkDirectory(), INVALID_CLS_CACHE_DIR);
        if (cacheDir.exists()) {
            if (cacheDir.isDirectory()) {
                File[] arr$ = cacheDir.listFiles();
                int len$ = arr$.length;
                for (int i$ = 0; i$ < len$; i$ += ANALYZER_VERSION) {
                    arr$[i$].delete();
                }
            }
            cacheDir.delete();
        }
    }

    private void writeFatal(Date time, Thread thread, Throwable ex) {
        Exception e;
        Throwable th;
        ClsFileOutputStream fos = null;
        CodedOutputStream cos = null;
        try {
            String currentSessionId = getCurrentSessionId();
            if (currentSessionId == null) {
                Fabric.getLogger().e(CrashlyticsCore.TAG, "Tried to write a fatal exception while no session was open.", null);
                CommonUtils.flushOrLog(null, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(null, "Failed to close fatal exception file output stream.");
                return;
            }
            CrashlyticsCore.recordFatalExceptionEvent(currentSessionId);
            OutputStream fos2 = new ClsFileOutputStream(getFilesDir(), currentSessionId + SESSION_FATAL_TAG);
            OutputStream outputStream;
            try {
                cos = CodedOutputStream.newInstance(fos2);
                writeSessionEvent(cos, time, thread, ex, EVENT_TYPE_CRASH, true);
                CommonUtils.flushOrLog(cos, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(fos2, "Failed to close fatal exception file output stream.");
                outputStream = fos2;
            } catch (Exception e2) {
                e = e2;
                outputStream = fos2;
                try {
                    Fabric.getLogger().e(CrashlyticsCore.TAG, "An error occurred in the fatal exception logger", e);
                    ExceptionUtils.writeStackTraceIfNotNull(e, fos);
                    CommonUtils.flushOrLog(cos, "Failed to flush to session begin file.");
                    CommonUtils.closeOrLog(fos, "Failed to close fatal exception file output stream.");
                } catch (Throwable th2) {
                    th = th2;
                    CommonUtils.flushOrLog(cos, "Failed to flush to session begin file.");
                    CommonUtils.closeOrLog(fos, "Failed to close fatal exception file output stream.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                outputStream = fos2;
                CommonUtils.flushOrLog(cos, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(fos, "Failed to close fatal exception file output stream.");
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            Fabric.getLogger().e(CrashlyticsCore.TAG, "An error occurred in the fatal exception logger", e);
            ExceptionUtils.writeStackTraceIfNotNull(e, fos);
            CommonUtils.flushOrLog(cos, "Failed to flush to session begin file.");
            CommonUtils.closeOrLog(fos, "Failed to close fatal exception file output stream.");
        }
    }

    private void writeExternalCrashEvent(SessionEventData crashEventData) throws IOException {
        OutputStream outputStream;
        Exception e;
        Throwable th;
        ClsFileOutputStream fos = null;
        CodedOutputStream cos = null;
        try {
            String previousSessionId = getPreviousSessionId();
            if (previousSessionId == null) {
                Fabric.getLogger().e(CrashlyticsCore.TAG, "Tried to write a native crash while no session was open.", null);
                CommonUtils.flushOrLog(null, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(null, "Failed to close fatal exception file output stream.");
                return;
            }
            CrashlyticsCore.recordFatalExceptionEvent(previousSessionId);
            OutputStream fos2 = new ClsFileOutputStream(getFilesDir(), previousSessionId + SESSION_FATAL_TAG);
            try {
                cos = CodedOutputStream.newInstance(fos2);
                NativeCrashWriter.writeNativeCrash(crashEventData, new LogFileManager(this.crashlyticsCore.getContext(), this.fileStore, previousSessionId), new MetaDataStore(getFilesDir()).readKeyData(previousSessionId), cos);
                CommonUtils.flushOrLog(cos, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(fos2, "Failed to close fatal exception file output stream.");
                outputStream = fos2;
            } catch (Exception e2) {
                e = e2;
                outputStream = fos2;
                try {
                    Fabric.getLogger().e(CrashlyticsCore.TAG, "An error occurred in the native crash logger", e);
                    ExceptionUtils.writeStackTraceIfNotNull(e, fos);
                    CommonUtils.flushOrLog(cos, "Failed to flush to session begin file.");
                    CommonUtils.closeOrLog(fos, "Failed to close fatal exception file output stream.");
                } catch (Throwable th2) {
                    th = th2;
                    CommonUtils.flushOrLog(cos, "Failed to flush to session begin file.");
                    CommonUtils.closeOrLog(fos, "Failed to close fatal exception file output stream.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                outputStream = fos2;
                CommonUtils.flushOrLog(cos, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(fos, "Failed to close fatal exception file output stream.");
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            Fabric.getLogger().e(CrashlyticsCore.TAG, "An error occurred in the native crash logger", e);
            ExceptionUtils.writeStackTraceIfNotNull(e, fos);
            CommonUtils.flushOrLog(cos, "Failed to flush to session begin file.");
            CommonUtils.closeOrLog(fos, "Failed to close fatal exception file output stream.");
        }
    }

    private void doWriteNonFatal(Date time, Thread thread, Throwable ex) {
        Exception e;
        Throwable th;
        String currentSessionId = getCurrentSessionId();
        if (currentSessionId == null) {
            Fabric.getLogger().e(CrashlyticsCore.TAG, "Tried to write a non-fatal exception while no session was open.", null);
            return;
        }
        CrashlyticsCore.recordLoggedExceptionEvent(currentSessionId);
        ClsFileOutputStream fos = null;
        CodedOutputStream cos = null;
        try {
            Fabric.getLogger().d(CrashlyticsCore.TAG, "Crashlytics is logging non-fatal exception \"" + ex + "\" from thread " + thread.getName());
            OutputStream fos2 = new ClsFileOutputStream(getFilesDir(), currentSessionId + SESSION_NON_FATAL_TAG + CommonUtils.padWithZerosToMaxIntWidth(this.eventCounter.getAndIncrement()));
            OutputStream outputStream;
            try {
                cos = CodedOutputStream.newInstance(fos2);
                writeSessionEvent(cos, time, thread, ex, EVENT_TYPE_LOGGED, false);
                CommonUtils.flushOrLog(cos, "Failed to flush to non-fatal file.");
                CommonUtils.closeOrLog(fos2, "Failed to close non-fatal file output stream.");
                outputStream = fos2;
            } catch (Exception e2) {
                e = e2;
                outputStream = fos2;
                try {
                    Fabric.getLogger().e(CrashlyticsCore.TAG, "An error occurred in the non-fatal exception logger", e);
                    ExceptionUtils.writeStackTraceIfNotNull(e, fos);
                    CommonUtils.flushOrLog(cos, "Failed to flush to non-fatal file.");
                    CommonUtils.closeOrLog(fos, "Failed to close non-fatal file output stream.");
                    trimSessionEventFiles(currentSessionId, MAX_LOCAL_LOGGED_EXCEPTIONS);
                } catch (Throwable th2) {
                    th = th2;
                    CommonUtils.flushOrLog(cos, "Failed to flush to non-fatal file.");
                    CommonUtils.closeOrLog(fos, "Failed to close non-fatal file output stream.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                outputStream = fos2;
                CommonUtils.flushOrLog(cos, "Failed to flush to non-fatal file.");
                CommonUtils.closeOrLog(fos, "Failed to close non-fatal file output stream.");
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            Fabric.getLogger().e(CrashlyticsCore.TAG, "An error occurred in the non-fatal exception logger", e);
            ExceptionUtils.writeStackTraceIfNotNull(e, fos);
            CommonUtils.flushOrLog(cos, "Failed to flush to non-fatal file.");
            CommonUtils.closeOrLog(fos, "Failed to close non-fatal file output stream.");
            trimSessionEventFiles(currentSessionId, MAX_LOCAL_LOGGED_EXCEPTIONS);
        }
        try {
            trimSessionEventFiles(currentSessionId, MAX_LOCAL_LOGGED_EXCEPTIONS);
        } catch (Exception e4) {
            Fabric.getLogger().e(CrashlyticsCore.TAG, "An error occurred when trimming non-fatal files.", e4);
        }
    }

    private void writeBeginSession(String sessionId, Date startedAt) throws Exception {
        Exception e;
        OutputStream fos;
        Throwable th;
        FileOutputStream fos2 = null;
        CodedOutputStream cos = null;
        try {
            OutputStream fos3 = new ClsFileOutputStream(getFilesDir(), sessionId + SESSION_BEGIN_TAG);
            try {
                cos = CodedOutputStream.newInstance(fos3);
                Locale locale = Locale.US;
                String str = GENERATOR_FORMAT;
                Object[] objArr = new Object[ANALYZER_VERSION];
                objArr[0] = this.crashlyticsCore.getVersion();
                SessionProtobufHelper.writeBeginSession(cos, sessionId, String.format(locale, str, objArr), startedAt.getTime() / 1000);
                CommonUtils.flushOrLog(cos, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(fos3, "Failed to close begin session file.");
            } catch (Exception e2) {
                e = e2;
                fos = fos3;
                try {
                    ExceptionUtils.writeStackTraceIfNotNull(e, fos2);
                    throw e;
                } catch (Throwable th2) {
                    th = th2;
                    CommonUtils.flushOrLog(cos, "Failed to flush to session begin file.");
                    CommonUtils.closeOrLog(fos2, "Failed to close begin session file.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                fos = fos3;
                CommonUtils.flushOrLog(cos, "Failed to flush to session begin file.");
                CommonUtils.closeOrLog(fos2, "Failed to close begin session file.");
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            ExceptionUtils.writeStackTraceIfNotNull(e, fos2);
            throw e;
        }
    }

    private void writeSessionApp(String sessionId) throws Exception {
        Exception e;
        OutputStream fos;
        Throwable th;
        FileOutputStream fos2 = null;
        CodedOutputStream cos = null;
        try {
            OutputStream fos3 = new ClsFileOutputStream(getFilesDir(), sessionId + SESSION_APP_TAG);
            try {
                cos = CodedOutputStream.newInstance(fos3);
                SessionProtobufHelper.writeSessionApp(cos, this.idManager.getAppIdentifier(), this.crashlyticsCore.getApiKey(), this.crashlyticsCore.getVersionCode(), this.crashlyticsCore.getVersionName(), this.idManager.getAppInstallIdentifier(), DeliveryMechanism.determineFrom(this.crashlyticsCore.getInstallerPackageName()).getId(), this.unityVersion);
                CommonUtils.flushOrLog(cos, "Failed to flush to session app file.");
                CommonUtils.closeOrLog(fos3, "Failed to close session app file.");
            } catch (Exception e2) {
                e = e2;
                fos = fos3;
                try {
                    ExceptionUtils.writeStackTraceIfNotNull(e, fos2);
                    throw e;
                } catch (Throwable th2) {
                    th = th2;
                    CommonUtils.flushOrLog(cos, "Failed to flush to session app file.");
                    CommonUtils.closeOrLog(fos2, "Failed to close session app file.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                fos = fos3;
                CommonUtils.flushOrLog(cos, "Failed to flush to session app file.");
                CommonUtils.closeOrLog(fos2, "Failed to close session app file.");
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            ExceptionUtils.writeStackTraceIfNotNull(e, fos2);
            throw e;
        }
    }

    private void writeSessionOS(String sessionId) throws Exception {
        Exception e;
        OutputStream fos;
        Throwable th;
        FileOutputStream fos2 = null;
        CodedOutputStream cos = null;
        try {
            OutputStream fos3 = new ClsFileOutputStream(getFilesDir(), sessionId + SESSION_OS_TAG);
            try {
                cos = CodedOutputStream.newInstance(fos3);
                SessionProtobufHelper.writeSessionOS(cos, CommonUtils.isRooted(this.crashlyticsCore.getContext()));
                CommonUtils.flushOrLog(cos, "Failed to flush to session OS file.");
                CommonUtils.closeOrLog(fos3, "Failed to close session OS file.");
            } catch (Exception e2) {
                e = e2;
                fos = fos3;
                try {
                    ExceptionUtils.writeStackTraceIfNotNull(e, fos2);
                    throw e;
                } catch (Throwable th2) {
                    th = th2;
                    CommonUtils.flushOrLog(cos, "Failed to flush to session OS file.");
                    CommonUtils.closeOrLog(fos2, "Failed to close session OS file.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                fos = fos3;
                CommonUtils.flushOrLog(cos, "Failed to flush to session OS file.");
                CommonUtils.closeOrLog(fos2, "Failed to close session OS file.");
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            ExceptionUtils.writeStackTraceIfNotNull(e, fos2);
            throw e;
        }
    }

    private void writeSessionDevice(String sessionId) throws Exception {
        Exception e;
        OutputStream fos;
        Throwable th;
        FileOutputStream fos2 = null;
        CodedOutputStream cos = null;
        try {
            OutputStream clsFileOutputStream = new ClsFileOutputStream(getFilesDir(), sessionId + SESSION_DEVICE_TAG);
            try {
                cos = CodedOutputStream.newInstance(clsFileOutputStream);
                Context context = this.crashlyticsCore.getContext();
                StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
                SessionProtobufHelper.writeSessionDevice(cos, this.idManager.getDeviceUUID(), CommonUtils.getCpuArchitectureInt(), Build.MODEL, Runtime.getRuntime().availableProcessors(), CommonUtils.getTotalRamInBytes(), ((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize()), CommonUtils.isEmulator(context), this.idManager.getDeviceIdentifiers(), CommonUtils.getDeviceState(context), Build.MANUFACTURER, Build.PRODUCT);
                CommonUtils.flushOrLog(cos, "Failed to flush session device info.");
                CommonUtils.closeOrLog(clsFileOutputStream, "Failed to close session device file.");
            } catch (Exception e2) {
                e = e2;
                fos = clsFileOutputStream;
                try {
                    ExceptionUtils.writeStackTraceIfNotNull(e, fos2);
                    throw e;
                } catch (Throwable th2) {
                    th = th2;
                    CommonUtils.flushOrLog(cos, "Failed to flush session device info.");
                    CommonUtils.closeOrLog(fos2, "Failed to close session device file.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                fos = clsFileOutputStream;
                CommonUtils.flushOrLog(cos, "Failed to flush session device info.");
                CommonUtils.closeOrLog(fos2, "Failed to close session device file.");
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            ExceptionUtils.writeStackTraceIfNotNull(e, fos2);
            throw e;
        }
    }

    private void writeSessionUser(String sessionId) throws Exception {
        Exception e;
        OutputStream fos;
        Throwable th;
        FileOutputStream fos2 = null;
        CodedOutputStream cos = null;
        try {
            OutputStream fos3 = new ClsFileOutputStream(getFilesDir(), sessionId + SESSION_USER_TAG);
            try {
                cos = CodedOutputStream.newInstance(fos3);
                UserMetaData userMetaData = getUserMetaData(sessionId);
                if (userMetaData.isEmpty()) {
                    CommonUtils.flushOrLog(cos, "Failed to flush session user file.");
                    CommonUtils.closeOrLog(fos3, "Failed to close session user file.");
                    return;
                }
                SessionProtobufHelper.writeSessionUser(cos, userMetaData.id, userMetaData.name, userMetaData.email);
                CommonUtils.flushOrLog(cos, "Failed to flush session user file.");
                CommonUtils.closeOrLog(fos3, "Failed to close session user file.");
            } catch (Exception e2) {
                e = e2;
                fos = fos3;
                try {
                    ExceptionUtils.writeStackTraceIfNotNull(e, fos2);
                    throw e;
                } catch (Throwable th2) {
                    th = th2;
                    CommonUtils.flushOrLog(cos, "Failed to flush session user file.");
                    CommonUtils.closeOrLog(fos2, "Failed to close session user file.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                fos = fos3;
                CommonUtils.flushOrLog(cos, "Failed to flush session user file.");
                CommonUtils.closeOrLog(fos2, "Failed to close session user file.");
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            ExceptionUtils.writeStackTraceIfNotNull(e, fos2);
            throw e;
        }
    }

    private void writeSessionEvent(CodedOutputStream cos, Date time, Thread thread, Throwable ex, String eventType, boolean includeAllThreads) throws Exception {
        Thread[] threads;
        Map<String, String> attributes;
        Context context = this.crashlyticsCore.getContext();
        long eventTime = time.getTime() / 1000;
        float batteryLevel = CommonUtils.getBatteryLevel(context);
        int batteryVelocity = CommonUtils.getBatteryVelocity(context, this.devicePowerStateListener.isPowerConnected());
        boolean proximityEnabled = CommonUtils.getProximitySensorEnabled(context);
        int orientation = context.getResources().getConfiguration().orientation;
        long usedRamBytes = CommonUtils.getTotalRamInBytes() - CommonUtils.calculateFreeRamInBytes(context);
        long diskUsedBytes = CommonUtils.calculateUsedDiskSpaceInBytes(Environment.getDataDirectory().getPath());
        RunningAppProcessInfo runningAppProcessInfo = CommonUtils.getAppProcessInfo(context.getPackageName(), context);
        List<StackTraceElement[]> stacks = new LinkedList();
        StackTraceElement[] exceptionStack = ex.getStackTrace();
        String buildId = this.crashlyticsCore.getBuildId();
        String appIdentifier = this.idManager.getAppIdentifier();
        if (includeAllThreads) {
            Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
            threads = new Thread[allStackTraces.size()];
            int i = 0;
            for (Entry<Thread, StackTraceElement[]> entry : allStackTraces.entrySet()) {
                threads[i] = (Thread) entry.getKey();
                stacks.add(entry.getValue());
                i += ANALYZER_VERSION;
            }
        } else {
            threads = new Thread[0];
        }
        if (CommonUtils.getBooleanResourceValue(context, "com.crashlytics.CollectCustomKeys", true)) {
            attributes = this.crashlyticsCore.getAttributes();
            if (attributes != null && attributes.size() > ANALYZER_VERSION) {
                attributes = new TreeMap(attributes);
            }
        } else {
            attributes = new TreeMap();
        }
        SessionProtobufHelper.writeSessionEvent(cos, eventTime, eventType, ex, thread, exceptionStack, threads, stacks, attributes, this.logFileManager, runningAppProcessInfo, orientation, appIdentifier, buildId, batteryLevel, batteryVelocity, proximityEnabled, usedRamBytes, diskUsedBytes);
    }

    private void writeSessionPartsToSessionFile(File sessionBeginFile, String sessionId, int maxLoggedExceptionsCount) {
        Fabric.getLogger().d(CrashlyticsCore.TAG, "Collecting session parts for ID " + sessionId);
        File[] fatalFiles = listFilesMatching(new FileNameContainsFilter(sessionId + SESSION_FATAL_TAG));
        boolean hasFatal = fatalFiles != null && fatalFiles.length > 0;
        Fabric.getLogger().d(CrashlyticsCore.TAG, String.format(Locale.US, "Session %s has fatal exception: %s", new Object[]{sessionId, Boolean.valueOf(hasFatal)}));
        File[] nonFatalFiles = listFilesMatching(new FileNameContainsFilter(sessionId + SESSION_NON_FATAL_TAG));
        boolean hasNonFatal = nonFatalFiles != null && nonFatalFiles.length > 0;
        Fabric.getLogger().d(CrashlyticsCore.TAG, String.format(Locale.US, "Session %s has non-fatal exceptions: %s", new Object[]{sessionId, Boolean.valueOf(hasNonFatal)}));
        if (hasFatal || hasNonFatal) {
            synthesizeSessionFile(sessionBeginFile, sessionId, getTrimmedNonFatalFiles(sessionId, nonFatalFiles, maxLoggedExceptionsCount), hasFatal ? fatalFiles[0] : null);
        } else {
            Fabric.getLogger().d(CrashlyticsCore.TAG, "No events present for session ID " + sessionId);
        }
        Fabric.getLogger().d(CrashlyticsCore.TAG, "Removing session part files for ID " + sessionId);
        deleteSessionPartFilesFor(sessionId);
    }

    private void synthesizeSessionFile(File sessionBeginFile, String sessionId, File[] nonFatalFiles, File fatalFile) {
        Exception e;
        Throwable th;
        boolean hasFatal = fatalFile != null;
        ClsFileOutputStream fos = null;
        try {
            OutputStream fos2 = new ClsFileOutputStream(getFilesDir(), sessionId);
            OutputStream outputStream;
            try {
                CodedOutputStream cos = CodedOutputStream.newInstance(fos2);
                Fabric.getLogger().d(CrashlyticsCore.TAG, "Collecting SessionStart data for session ID " + sessionId);
                writeToCosFromFile(cos, sessionBeginFile);
                cos.writeUInt64(MAX_COMPLETE_SESSIONS_COUNT, new Date().getTime() / 1000);
                cos.writeBool(5, hasFatal);
                writeInitialPartsTo(cos, sessionId);
                writeNonFatalEventsTo(cos, nonFatalFiles, sessionId);
                if (hasFatal) {
                    writeToCosFromFile(cos, fatalFile);
                }
                cos.writeUInt32(11, ANALYZER_VERSION);
                cos.writeEnum(12, 3);
                CommonUtils.flushOrLog(cos, "Error flushing session file stream");
                if (null != null) {
                    closeWithoutRenamingOrLog(fos2);
                    outputStream = fos2;
                    return;
                }
                CommonUtils.closeOrLog(fos2, "Failed to close CLS file");
                outputStream = fos2;
            } catch (Exception e2) {
                e = e2;
                outputStream = fos2;
                try {
                    Fabric.getLogger().e(CrashlyticsCore.TAG, "Failed to write session file for session ID: " + sessionId, e);
                    ExceptionUtils.writeStackTraceIfNotNull(e, fos);
                    CommonUtils.flushOrLog(null, "Error flushing session file stream");
                    if (true) {
                        closeWithoutRenamingOrLog(fos);
                    } else {
                        CommonUtils.closeOrLog(fos, "Failed to close CLS file");
                    }
                } catch (Throwable th2) {
                    th = th2;
                    CommonUtils.flushOrLog(null, "Error flushing session file stream");
                    if (null == null) {
                        CommonUtils.closeOrLog(fos, "Failed to close CLS file");
                    } else {
                        closeWithoutRenamingOrLog(fos);
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                outputStream = fos2;
                CommonUtils.flushOrLog(null, "Error flushing session file stream");
                if (null == null) {
                    closeWithoutRenamingOrLog(fos);
                } else {
                    CommonUtils.closeOrLog(fos, "Failed to close CLS file");
                }
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            Fabric.getLogger().e(CrashlyticsCore.TAG, "Failed to write session file for session ID: " + sessionId, e);
            ExceptionUtils.writeStackTraceIfNotNull(e, fos);
            CommonUtils.flushOrLog(null, "Error flushing session file stream");
            if (true) {
                closeWithoutRenamingOrLog(fos);
            } else {
                CommonUtils.closeOrLog(fos, "Failed to close CLS file");
            }
        }
    }

    private static void writeNonFatalEventsTo(CodedOutputStream cos, File[] nonFatalFiles, String sessionId) {
        Arrays.sort(nonFatalFiles, CommonUtils.FILE_MODIFIED_COMPARATOR);
        File[] arr$ = nonFatalFiles;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$ += ANALYZER_VERSION) {
            File nonFatalFile = arr$[i$];
            try {
                Fabric.getLogger().d(CrashlyticsCore.TAG, String.format(Locale.US, "Found Non Fatal for session ID %s in %s ", new Object[]{sessionId, nonFatalFile.getName()}));
                writeToCosFromFile(cos, nonFatalFile);
            } catch (Exception e) {
                Fabric.getLogger().e(CrashlyticsCore.TAG, "Error writting non-fatal to session.", e);
            }
        }
    }

    private void writeInitialPartsTo(CodedOutputStream cos, String sessionId) throws IOException {
        String[] arr$ = INITIAL_SESSION_PART_TAGS;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$ += ANALYZER_VERSION) {
            String tag = arr$[i$];
            File[] sessionPartFiles = listFilesMatching(new FileNameContainsFilter(sessionId + tag));
            if (sessionPartFiles.length == 0) {
                Fabric.getLogger().e(CrashlyticsCore.TAG, "Can't find " + tag + " data for session ID " + sessionId, null);
            } else {
                Fabric.getLogger().d(CrashlyticsCore.TAG, "Collecting " + tag + " data for session ID " + sessionId);
                writeToCosFromFile(cos, sessionPartFiles[0]);
            }
        }
    }

    private static void writeToCosFromFile(CodedOutputStream cos, File file) throws IOException {
        Throwable th;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                FileInputStream fis2 = new FileInputStream(file);
                try {
                    copyToCodedOutputStream(fis2, cos, (int) file.length());
                    CommonUtils.closeOrLog(fis2, "Failed to close file input stream.");
                    return;
                } catch (Throwable th2) {
                    th = th2;
                    fis = fis2;
                    CommonUtils.closeOrLog(fis, "Failed to close file input stream.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                CommonUtils.closeOrLog(fis, "Failed to close file input stream.");
                throw th;
            }
        }
        Fabric.getLogger().e(CrashlyticsCore.TAG, "Tried to include a file that doesn't exist: " + file.getName(), null);
    }

    private static void copyToCodedOutputStream(InputStream inStream, CodedOutputStream cos, int bufferLength) throws IOException {
        byte[] buffer = new byte[bufferLength];
        int offset = 0;
        while (offset < buffer.length) {
            int numRead = inStream.read(buffer, offset, buffer.length - offset);
            if (numRead < 0) {
                break;
            }
            offset += numRead;
        }
        cos.writeRawBytes(buffer);
    }

    private UserMetaData getUserMetaData(String sessionId) {
        return isHandlingException() ? new UserMetaData(this.crashlyticsCore.getUserIdentifier(), this.crashlyticsCore.getUserName(), this.crashlyticsCore.getUserEmail()) : new MetaDataStore(getFilesDir()).readUserData(sessionId);
    }

    private void sendSessionReports() {
        File[] arr$ = listCompleteSessionFiles();
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$ += ANALYZER_VERSION) {
            this.executorServiceWrapper.executeAsync(new SendSessionRunnable(this.crashlyticsCore, arr$[i$]));
        }
    }

    private File getFilesDir() {
        return this.fileStore.getFilesDir();
    }
}
