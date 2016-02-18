package com.path.android.jobqueue.nonPersistentQueue;

import com.path.android.jobqueue.JobHolder;
import java.util.Comparator;

public class TimeAwareComparator implements Comparator<JobHolder> {
    final Comparator<JobHolder> baseComparator;

    public TimeAwareComparator(Comparator<JobHolder> baseComparator) {
        this.baseComparator = baseComparator;
    }

    public int compare(JobHolder jobHolder, JobHolder jobHolder2) {
        boolean job1Valid;
        boolean job2Valid;
        long now = System.nanoTime();
        if (jobHolder.getDelayUntilNs() <= now) {
            job1Valid = true;
        } else {
            job1Valid = false;
        }
        if (jobHolder2.getDelayUntilNs() <= now) {
            job2Valid = true;
        } else {
            job2Valid = false;
        }
        if (job1Valid) {
            if (job2Valid) {
                return this.baseComparator.compare(jobHolder, jobHolder2);
            }
            return -1;
        } else if (job2Valid) {
            if (job1Valid) {
                return this.baseComparator.compare(jobHolder, jobHolder2);
            }
            return 1;
        } else if (jobHolder.getDelayUntilNs() < jobHolder2.getDelayUntilNs()) {
            return -1;
        } else {
            if (jobHolder.getDelayUntilNs() <= jobHolder2.getDelayUntilNs()) {
                return this.baseComparator.compare(jobHolder, jobHolder2);
            }
            return 1;
        }
    }
}
