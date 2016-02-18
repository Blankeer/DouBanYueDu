package com.path.android.jobqueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

public class CopyOnWriteGroupSet {
    private final TreeSet<String> internalSet;
    private ArrayList<String> publicClone;

    public CopyOnWriteGroupSet() {
        this.internalSet = new TreeSet();
    }

    public synchronized Collection<String> getSafe() {
        if (this.publicClone == null) {
            this.publicClone = new ArrayList(this.internalSet);
        }
        return this.publicClone;
    }

    public synchronized void add(String group) {
        if (this.internalSet.add(group)) {
            this.publicClone = null;
        }
    }

    public synchronized void remove(String group) {
        if (this.internalSet.remove(group)) {
            this.publicClone = null;
        }
    }

    public synchronized void clear() {
        this.internalSet.clear();
        this.publicClone = null;
    }
}
