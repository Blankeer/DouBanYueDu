package com.path.android.jobqueue;

public class Params {
    private long delayMs;
    private String groupId;
    private boolean persistent;
    private int priority;
    private boolean requiresNetwork;

    public Params(int priority) {
        this.requiresNetwork = false;
        this.groupId = null;
        this.persistent = false;
        this.priority = priority;
    }

    public Params requireNetwork() {
        this.requiresNetwork = true;
        return this;
    }

    public Params groupBy(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public Params persist() {
        this.persistent = true;
        return this;
    }

    public Params delayInMs(long delayMs) {
        this.delayMs = delayMs;
        return this;
    }

    public Params setRequiresNetwork(boolean requiresNetwork) {
        this.requiresNetwork = requiresNetwork;
        return this;
    }

    public Params setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public Params setPersistent(boolean persistent) {
        this.persistent = persistent;
        return this;
    }

    public Params setDelayMs(long delayMs) {
        this.delayMs = delayMs;
        return this;
    }

    public boolean doesRequireNetwork() {
        return this.requiresNetwork;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    public int getPriority() {
        return this.priority;
    }

    public long getDelayMs() {
        return this.delayMs;
    }
}
