package com.path.android.jobqueue.nonPersistentQueue;

import com.path.android.jobqueue.JobHolder;
import com.path.android.jobqueue.log.JqLog;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class NonPersistentJobSet implements JobSet {
    private final Map<String, Integer> existingGroups;
    private final Map<Long, JobHolder> idCache;
    private final TreeSet<JobHolder> set;

    public NonPersistentJobSet(Comparator<JobHolder> comparator) {
        this.set = new TreeSet(comparator);
        this.existingGroups = new HashMap();
        this.idCache = new HashMap();
    }

    private JobHolder safeFirst() {
        if (this.set.size() < 1) {
            return null;
        }
        return (JobHolder) this.set.first();
    }

    public JobHolder peek(Collection<String> excludeGroupIds) {
        if (excludeGroupIds == null || excludeGroupIds.size() == 0) {
            return safeFirst();
        }
        Iterator i$ = this.set.iterator();
        while (i$.hasNext()) {
            JobHolder holder = (JobHolder) i$.next();
            if (holder.getGroupId() == null || !excludeGroupIds.contains(holder.getGroupId())) {
                return holder;
            }
        }
        return null;
    }

    private JobHolder safePeek() {
        if (this.set.size() == 0) {
            return null;
        }
        return safeFirst();
    }

    public JobHolder poll(Collection<String> excludeGroupIds) {
        JobHolder peek = peek(excludeGroupIds);
        if (peek != null) {
            remove(peek);
        }
        return peek;
    }

    public JobHolder findById(long id) {
        return (JobHolder) this.idCache.get(Long.valueOf(id));
    }

    public boolean offer(JobHolder holder) {
        if (holder.getId() == null) {
            throw new RuntimeException("cannot add job holder w/o an ID");
        }
        boolean result = this.set.add(holder);
        if (!result) {
            remove(holder);
            result = this.set.add(holder);
        }
        if (result) {
            this.idCache.put(holder.getId(), holder);
            if (holder.getGroupId() != null) {
                incGroupCount(holder.getGroupId());
            }
        }
        return result;
    }

    private void incGroupCount(String groupId) {
        if (this.existingGroups.containsKey(groupId)) {
            this.existingGroups.put(groupId, Integer.valueOf(((Integer) this.existingGroups.get(groupId)).intValue() + 1));
        } else {
            this.existingGroups.put(groupId, Integer.valueOf(1));
        }
    }

    private void decGroupCount(String groupId) {
        Integer val = (Integer) this.existingGroups.get(groupId);
        if (val == null || val.intValue() == 0) {
            JqLog.e("detected inconsistency in NonPersistentJobSet's group id hash", new Object[0]);
        } else if (Integer.valueOf(val.intValue() - 1).intValue() == 0) {
            this.existingGroups.remove(groupId);
        }
    }

    public boolean remove(JobHolder holder) {
        boolean removed = this.set.remove(holder);
        if (removed) {
            this.idCache.remove(holder.getId());
            if (holder.getGroupId() != null) {
                decGroupCount(holder.getGroupId());
            }
        }
        return removed;
    }

    public void clear() {
        this.set.clear();
        this.existingGroups.clear();
        this.idCache.clear();
    }

    public int size() {
        return this.set.size();
    }

    public CountWithGroupIdsResult countReadyJobs(long now, Collection<String> excludeGroups) {
        int total = 0;
        int groupCnt = this.existingGroups.keySet().size();
        Set<String> groupIdSet = null;
        if (groupCnt > 0) {
            groupIdSet = new HashSet();
        }
        Iterator i$ = this.set.iterator();
        while (i$.hasNext()) {
            JobHolder holder = (JobHolder) i$.next();
            if (holder.getDelayUntilNs() < now) {
                if (holder.getGroupId() == null) {
                    total++;
                } else if ((excludeGroups == null || !excludeGroups.contains(holder.getGroupId())) && groupCnt > 0 && groupIdSet.add(holder.getGroupId())) {
                    total++;
                }
            }
        }
        return new CountWithGroupIdsResult(total, groupIdSet);
    }

    public CountWithGroupIdsResult countReadyJobs(Collection<String> excludeGroups) {
        if (this.existingGroups.size() == 0) {
            return new CountWithGroupIdsResult(this.set.size(), null);
        }
        int total = 0;
        Set<String> existingGroupIds = null;
        Iterator i$ = this.set.iterator();
        while (i$.hasNext()) {
            JobHolder holder = (JobHolder) i$.next();
            if (holder.getGroupId() != null) {
                if (excludeGroups == null || !excludeGroups.contains(holder.getGroupId())) {
                    if (existingGroupIds == null) {
                        existingGroupIds = new HashSet();
                        existingGroupIds.add(holder.getGroupId());
                    } else if (!existingGroupIds.add(holder.getGroupId())) {
                    }
                }
            }
            total++;
        }
        return new CountWithGroupIdsResult(total, existingGroupIds);
    }
}
