package de.greenrobot.event.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ExceptionToResourceMapping {
    public final Map<Class<? extends Throwable>, Integer> throwableToMsgIdMap;

    public ExceptionToResourceMapping() {
        this.throwableToMsgIdMap = new HashMap();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Integer mapThrowable(java.lang.Throwable r7) {
        /*
        r6 = this;
        r2 = r7;
        r0 = 20;
    L_0x0003:
        r1 = r6.mapThrowableFlat(r2);
        if (r1 == 0) goto L_0x000a;
    L_0x0009:
        return r1;
    L_0x000a:
        r2 = r2.getCause();
        r0 = r0 + -1;
        if (r0 <= 0) goto L_0x0016;
    L_0x0012:
        if (r2 == r7) goto L_0x0016;
    L_0x0014:
        if (r2 != 0) goto L_0x0003;
    L_0x0016:
        r3 = de.greenrobot.event.EventBus.TAG;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "No specific message ressource ID found for ";
        r4 = r4.append(r5);
        r4 = r4.append(r7);
        r4 = r4.toString();
        android.util.Log.d(r3, r4);
        r1 = 0;
        goto L_0x0009;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.event.util.ExceptionToResourceMapping.mapThrowable(java.lang.Throwable):java.lang.Integer");
    }

    protected Integer mapThrowableFlat(Throwable throwable) {
        Class<? extends Throwable> throwableClass = throwable.getClass();
        Integer resId = (Integer) this.throwableToMsgIdMap.get(throwableClass);
        if (resId == null) {
            Class<? extends Throwable> closestClass = null;
            for (Entry<Class<? extends Throwable>, Integer> mapping : this.throwableToMsgIdMap.entrySet()) {
                Class<? extends Throwable> candidate = (Class) mapping.getKey();
                if (candidate.isAssignableFrom(throwableClass) && (closestClass == null || closestClass.isAssignableFrom(candidate))) {
                    closestClass = candidate;
                    resId = (Integer) mapping.getValue();
                }
            }
        }
        return resId;
    }

    public ExceptionToResourceMapping addMapping(Class<? extends Throwable> clazz, int msgId) {
        this.throwableToMsgIdMap.put(clazz, Integer.valueOf(msgId));
        return this;
    }
}
