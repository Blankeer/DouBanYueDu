package com.path.android.jobqueue;

import android.content.Context;

public interface QueueFactory {
    JobQueue createNonPersistent(Context context, Long l, String str);

    JobQueue createPersistentQueue(Context context, Long l, String str);
}
