package com.path.android.jobqueue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@Deprecated
public abstract class BaseJob implements Serializable {
    public static final int DEFAULT_RETRY_LIMIT = 20;
    private transient int currentRunCount;
    private String groupId;
    private boolean persistent;
    private boolean requiresNetwork;

    public abstract void onAdded();

    protected abstract void onCancel();

    public abstract void onRun() throws Throwable;

    protected abstract boolean shouldReRunOnThrowable(Throwable th);

    protected BaseJob(boolean requiresNetwork) {
        this(requiresNetwork, false, null);
    }

    protected BaseJob(String groupId) {
        this(false, false, groupId);
    }

    protected BaseJob(boolean requiresNetwork, String groupId) {
        this(requiresNetwork, false, groupId);
    }

    public BaseJob(boolean requiresNetwork, boolean persistent) {
        this(requiresNetwork, persistent, null);
    }

    protected BaseJob(boolean requiresNetwork, boolean persistent, String groupId) {
        this.requiresNetwork = requiresNetwork;
        this.persistent = persistent;
        this.groupId = groupId;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeBoolean(this.requiresNetwork);
        oos.writeObject(this.groupId);
        oos.writeBoolean(this.persistent);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        this.requiresNetwork = ois.readBoolean();
        this.groupId = (String) ois.readObject();
        this.persistent = ois.readBoolean();
    }

    public final boolean isPersistent() {
        return this.persistent;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean safeRun(int r11) {
        /*
        r10 = this;
        r5 = 1;
        r4 = 0;
        r10.currentRunCount = r11;
        r6 = com.path.android.jobqueue.log.JqLog.isDebugEnabled();
        if (r6 == 0) goto L_0x001b;
    L_0x000a:
        r6 = "running job %s";
        r7 = new java.lang.Object[r5];
        r8 = r10.getClass();
        r8 = r8.getSimpleName();
        r7[r4] = r8;
        com.path.android.jobqueue.log.JqLog.d(r6, r7);
    L_0x001b:
        r1 = 0;
        r0 = 0;
        r10.onRun();	 Catch:{ Throwable -> 0x0043 }
        r6 = com.path.android.jobqueue.log.JqLog.isDebugEnabled();	 Catch:{ Throwable -> 0x0043 }
        if (r6 == 0) goto L_0x0039;
    L_0x0026:
        r6 = "finished job %s";
        r7 = 1;
        r7 = new java.lang.Object[r7];	 Catch:{ Throwable -> 0x0043 }
        r8 = 0;
        r9 = r10.getClass();	 Catch:{ Throwable -> 0x0043 }
        r9 = r9.getSimpleName();	 Catch:{ Throwable -> 0x0043 }
        r7[r8] = r9;	 Catch:{ Throwable -> 0x0043 }
        com.path.android.jobqueue.log.JqLog.d(r6, r7);	 Catch:{ Throwable -> 0x0043 }
    L_0x0039:
        if (r1 == 0) goto L_0x003c;
    L_0x003b:
        return r4;
    L_0x003c:
        if (r0 == 0) goto L_0x0041;
    L_0x003e:
        r10.onCancel();	 Catch:{ Throwable -> 0x0079 }
    L_0x0041:
        r4 = r5;
        goto L_0x003b;
    L_0x0043:
        r2 = move-exception;
        r0 = 1;
        r6 = "error while executing job";
        r7 = 0;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0070 }
        com.path.android.jobqueue.log.JqLog.e(r2, r6, r7);	 Catch:{ all -> 0x0070 }
        r6 = r10.getRetryLimit();	 Catch:{ all -> 0x0070 }
        if (r11 >= r6) goto L_0x0064;
    L_0x0053:
        r1 = r5;
    L_0x0054:
        if (r1 == 0) goto L_0x005a;
    L_0x0056:
        r1 = r10.shouldReRunOnThrowable(r2);	 Catch:{ Throwable -> 0x0066 }
    L_0x005a:
        if (r1 != 0) goto L_0x003b;
    L_0x005c:
        if (r0 == 0) goto L_0x0041;
    L_0x005e:
        r10.onCancel();	 Catch:{ Throwable -> 0x0062 }
        goto L_0x0041;
    L_0x0062:
        r4 = move-exception;
        goto L_0x0041;
    L_0x0064:
        r1 = r4;
        goto L_0x0054;
    L_0x0066:
        r3 = move-exception;
        r6 = "shouldReRunOnThrowable did throw an exception";
        r7 = 0;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0070 }
        com.path.android.jobqueue.log.JqLog.e(r3, r6, r7);	 Catch:{ all -> 0x0070 }
        goto L_0x005a;
    L_0x0070:
        r5 = move-exception;
        if (r1 != 0) goto L_0x003b;
    L_0x0073:
        if (r0 == 0) goto L_0x0078;
    L_0x0075:
        r10.onCancel();	 Catch:{ Throwable -> 0x007b }
    L_0x0078:
        throw r5;
    L_0x0079:
        r4 = move-exception;
        goto L_0x0041;
    L_0x007b:
        r4 = move-exception;
        goto L_0x0078;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.path.android.jobqueue.BaseJob.safeRun(int):boolean");
    }

    protected int getCurrentRunCount() {
        return this.currentRunCount;
    }

    public final boolean requiresNetwork() {
        return this.requiresNetwork;
    }

    public final String getRunGroupId() {
        return this.groupId;
    }

    protected int getRetryLimit() {
        return DEFAULT_RETRY_LIMIT;
    }
}
