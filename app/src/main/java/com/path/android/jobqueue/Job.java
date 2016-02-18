package com.path.android.jobqueue;

import java.io.Serializable;

public abstract class Job extends BaseJob implements Serializable {
    private static final long serialVersionUID = 1;
    private transient long delayInMs;
    private transient int priority;

    protected Job(Params params) {
        super(params.doesRequireNetwork(), params.isPersistent(), params.getGroupId());
        this.priority = params.getPriority();
        this.delayInMs = params.getDelayMs();
    }

    public final int getPriority() {
        return this.priority;
    }

    public final long getDelayInMs() {
        return this.delayInMs;
    }
}
