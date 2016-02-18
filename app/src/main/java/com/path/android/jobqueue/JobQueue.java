package com.path.android.jobqueue;

import java.util.Collection;

public interface JobQueue {
    void clear();

    int count();

    int countReadyJobs(boolean z, Collection<String> collection);

    JobHolder findJobById(long j);

    Long getNextJobDelayUntilNs(boolean z);

    long insert(JobHolder jobHolder);

    long insertOrReplace(JobHolder jobHolder);

    JobHolder nextJobAndIncRunCount(boolean z, Collection<String> collection);

    void remove(JobHolder jobHolder);
}
