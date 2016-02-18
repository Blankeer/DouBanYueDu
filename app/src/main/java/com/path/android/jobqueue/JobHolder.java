package com.path.android.jobqueue;

public class JobHolder {
    transient BaseJob baseJob;
    protected long createdNs;
    protected long delayUntilNs;
    protected String groupId;
    protected Long id;
    protected int priority;
    protected boolean requiresNetwork;
    protected int runCount;
    protected long runningSessionId;

    public JobHolder(Long id, int priority, String groupId, int runCount, BaseJob baseJob, long createdNs, long delayUntilNs, long runningSessionId) {
        this.id = id;
        this.priority = priority;
        this.groupId = groupId;
        this.runCount = runCount;
        this.createdNs = createdNs;
        this.delayUntilNs = delayUntilNs;
        this.baseJob = baseJob;
        this.runningSessionId = runningSessionId;
        this.requiresNetwork = baseJob.requiresNetwork();
    }

    public JobHolder(int priority, BaseJob baseJob, long runningSessionId) {
        this(null, priority, null, 0, baseJob, System.nanoTime(), Long.MIN_VALUE, runningSessionId);
    }

    public JobHolder(int priority, BaseJob baseJob, long delayUntilNs, long runningSessionId) {
        this(null, priority, baseJob.getRunGroupId(), 0, baseJob, System.nanoTime(), delayUntilNs, runningSessionId);
    }

    public final boolean safeRun(int currentRunCount) {
        return this.baseJob.safeRun(currentRunCount);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean requiresNetwork() {
        return this.requiresNetwork;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getRunCount() {
        return this.runCount;
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }

    public long getCreatedNs() {
        return this.createdNs;
    }

    public void setCreatedNs(long createdNs) {
        this.createdNs = createdNs;
    }

    public long getRunningSessionId() {
        return this.runningSessionId;
    }

    public void setRunningSessionId(long runningSessionId) {
        this.runningSessionId = runningSessionId;
    }

    public long getDelayUntilNs() {
        return this.delayUntilNs;
    }

    public BaseJob getBaseJob() {
        return this.baseJob;
    }

    public void setBaseJob(BaseJob baseJob) {
        this.baseJob = baseJob;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public int hashCode() {
        if (this.id == null) {
            return super.hashCode();
        }
        return this.id.intValue();
    }

    public boolean equals(Object o) {
        if (!(o instanceof JobHolder)) {
            return false;
        }
        JobHolder other = (JobHolder) o;
        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }
}
