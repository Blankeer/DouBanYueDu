package com.path.android.jobqueue.nonPersistentQueue;

import java.util.Set;

public class CountWithGroupIdsResult {
    private int count;
    private Set<String> groupIds;

    public CountWithGroupIdsResult(int count, Set<String> groupIds) {
        this.count = count;
        this.groupIds = groupIds;
    }

    public int getCount() {
        return this.count;
    }

    public Set<String> getGroupIds() {
        return this.groupIds;
    }

    public CountWithGroupIdsResult mergeWith(CountWithGroupIdsResult other) {
        if (this.groupIds == null || other.groupIds == null) {
            this.count += other.count;
            if (this.groupIds == null) {
                this.groupIds = other.groupIds;
            }
        } else {
            int sharedGroups = 0;
            for (String groupId : other.groupIds) {
                if (!this.groupIds.add(groupId)) {
                    sharedGroups++;
                }
            }
            this.count = (this.count + other.count) - sharedGroups;
        }
        return this;
    }
}
