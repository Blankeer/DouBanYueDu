package com.path.android.jobqueue.nonPersistentQueue;

import com.path.android.jobqueue.JobHolder;
import java.util.Collection;

public interface JobSet {
    void clear();

    CountWithGroupIdsResult countReadyJobs(long j, Collection<String> collection);

    CountWithGroupIdsResult countReadyJobs(Collection<String> collection);

    JobHolder findById(long j);

    boolean offer(JobHolder jobHolder);

    JobHolder peek(Collection<String> collection);

    JobHolder poll(Collection<String> collection);

    boolean remove(JobHolder jobHolder);

    int size();
}
