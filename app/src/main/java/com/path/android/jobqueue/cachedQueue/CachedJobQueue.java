package com.path.android.jobqueue.cachedQueue;

import com.path.android.jobqueue.JobHolder;
import com.path.android.jobqueue.JobQueue;
import java.util.Collection;

public class CachedJobQueue implements JobQueue {
    private Cache cache;
    JobQueue delegate;

    private static class Cache {
        Integer count;
        DelayUntil delayUntil;

        private static class DelayUntil {
            boolean hasNetwork;
            Long value;

            private DelayUntil(boolean hasNetwork, Long value) {
                this.value = value;
                this.hasNetwork = hasNetwork;
            }

            private boolean isValid(boolean hasNetwork) {
                return this.hasNetwork == hasNetwork;
            }

            public void set(boolean hasNetwork, Long value) {
                this.value = value;
                this.hasNetwork = hasNetwork;
            }
        }

        private Cache() {
        }

        public void invalidateAll() {
            this.count = null;
            this.delayUntil = null;
        }
    }

    public CachedJobQueue(JobQueue delegate) {
        this.delegate = delegate;
        this.cache = new Cache();
    }

    public long insert(JobHolder jobHolder) {
        this.cache.invalidateAll();
        return this.delegate.insert(jobHolder);
    }

    public long insertOrReplace(JobHolder jobHolder) {
        this.cache.invalidateAll();
        return this.delegate.insertOrReplace(jobHolder);
    }

    public void remove(JobHolder jobHolder) {
        this.cache.invalidateAll();
        this.delegate.remove(jobHolder);
    }

    public int count() {
        if (this.cache.count == null) {
            this.cache.count = Integer.valueOf(this.delegate.count());
        }
        return this.cache.count.intValue();
    }

    public int countReadyJobs(boolean hasNetwork, Collection<String> excludeGroups) {
        if (this.cache.count != null && this.cache.count.intValue() < 1) {
            return 0;
        }
        int count = this.delegate.countReadyJobs(hasNetwork, excludeGroups);
        if (count != 0) {
            return count;
        }
        count();
        return count;
    }

    public JobHolder nextJobAndIncRunCount(boolean hasNetwork, Collection<String> excludeGroups) {
        if (this.cache.count != null && this.cache.count.intValue() < 1) {
            return null;
        }
        JobHolder holder = this.delegate.nextJobAndIncRunCount(hasNetwork, excludeGroups);
        if (holder == null) {
            count();
            return holder;
        } else if (this.cache.count == null) {
            return holder;
        } else {
            Cache cache = this.cache;
            Integer num = cache.count;
            cache.count = Integer.valueOf(cache.count.intValue() - 1);
            return holder;
        }
    }

    public Long getNextJobDelayUntilNs(boolean hasNetwork) {
        if (this.cache.delayUntil == null) {
            this.cache.delayUntil = new DelayUntil(this.delegate.getNextJobDelayUntilNs(hasNetwork), null);
        } else if (!this.cache.delayUntil.isValid(hasNetwork)) {
            this.cache.delayUntil.set(hasNetwork, this.delegate.getNextJobDelayUntilNs(hasNetwork));
        }
        return this.cache.delayUntil.value;
    }

    public void clear() {
        this.cache.invalidateAll();
        this.delegate.clear();
    }

    public JobHolder findJobById(long id) {
        return this.delegate.findJobById(id);
    }
}
