package io.realm.internal;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;

public class Context {
    private List<Long> abandonedQueries;
    private List<Long> abandonedTableViews;
    private List<Long> abandonedTables;
    private boolean isFinalized;
    ReferenceQueue<NativeObject> referenceQueue;
    List<Reference<?>> rowReferences;

    public Context() {
        this.abandonedTables = new ArrayList();
        this.abandonedTableViews = new ArrayList();
        this.abandonedQueries = new ArrayList();
        this.rowReferences = new ArrayList();
        this.referenceQueue = new ReferenceQueue();
        this.isFinalized = false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void executeDelayedDisposal() {
        /*
        r4 = this;
        monitor-enter(r4);
        r0 = 0;
    L_0x0002:
        r1 = r4.abandonedTables;	 Catch:{ all -> 0x0066 }
        r1 = r1.size();	 Catch:{ all -> 0x0066 }
        if (r0 >= r1) goto L_0x001c;
    L_0x000a:
        r1 = r4.abandonedTables;	 Catch:{ all -> 0x0066 }
        r1 = r1.get(r0);	 Catch:{ all -> 0x0066 }
        r1 = (java.lang.Long) r1;	 Catch:{ all -> 0x0066 }
        r2 = r1.longValue();	 Catch:{ all -> 0x0066 }
        io.realm.internal.Table.nativeClose(r2);	 Catch:{ all -> 0x0066 }
        r0 = r0 + 1;
        goto L_0x0002;
    L_0x001c:
        r1 = r4.abandonedTables;	 Catch:{ all -> 0x0066 }
        r1.clear();	 Catch:{ all -> 0x0066 }
        r0 = 0;
    L_0x0022:
        r1 = r4.abandonedTableViews;	 Catch:{ all -> 0x0066 }
        r1 = r1.size();	 Catch:{ all -> 0x0066 }
        if (r0 >= r1) goto L_0x003c;
    L_0x002a:
        r1 = r4.abandonedTableViews;	 Catch:{ all -> 0x0066 }
        r1 = r1.get(r0);	 Catch:{ all -> 0x0066 }
        r1 = (java.lang.Long) r1;	 Catch:{ all -> 0x0066 }
        r2 = r1.longValue();	 Catch:{ all -> 0x0066 }
        io.realm.internal.TableView.nativeClose(r2);	 Catch:{ all -> 0x0066 }
        r0 = r0 + 1;
        goto L_0x0022;
    L_0x003c:
        r1 = r4.abandonedTableViews;	 Catch:{ all -> 0x0066 }
        r1.clear();	 Catch:{ all -> 0x0066 }
        r0 = 0;
    L_0x0042:
        r1 = r4.abandonedQueries;	 Catch:{ all -> 0x0066 }
        r1 = r1.size();	 Catch:{ all -> 0x0066 }
        if (r0 >= r1) goto L_0x005c;
    L_0x004a:
        r1 = r4.abandonedQueries;	 Catch:{ all -> 0x0066 }
        r1 = r1.get(r0);	 Catch:{ all -> 0x0066 }
        r1 = (java.lang.Long) r1;	 Catch:{ all -> 0x0066 }
        r2 = r1.longValue();	 Catch:{ all -> 0x0066 }
        io.realm.internal.TableQuery.nativeClose(r2);	 Catch:{ all -> 0x0066 }
        r0 = r0 + 1;
        goto L_0x0042;
    L_0x005c:
        r1 = r4.abandonedQueries;	 Catch:{ all -> 0x0066 }
        r1.clear();	 Catch:{ all -> 0x0066 }
        r4.cleanRows();	 Catch:{ all -> 0x0066 }
        monitor-exit(r4);	 Catch:{ all -> 0x0066 }
        return;
    L_0x0066:
        r1 = move-exception;
        monitor-exit(r4);	 Catch:{ all -> 0x0066 }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.realm.internal.Context.executeDelayedDisposal():void");
    }

    public void cleanRows() {
        NativeObjectReference reference = (NativeObjectReference) this.referenceQueue.poll();
        while (reference != null) {
            UncheckedRow.nativeClose(reference.nativePointer);
            this.rowReferences.remove(reference);
            reference = (NativeObjectReference) this.referenceQueue.poll();
        }
    }

    public void asyncDisposeTable(long nativePointer, boolean isRoot) {
        if (isRoot || this.isFinalized) {
            Table.nativeClose(nativePointer);
        } else {
            this.abandonedTables.add(Long.valueOf(nativePointer));
        }
    }

    public void asyncDisposeTableView(long nativePointer) {
        if (this.isFinalized) {
            TableView.nativeClose(nativePointer);
        } else {
            this.abandonedTableViews.add(Long.valueOf(nativePointer));
        }
    }

    public void asyncDisposeQuery(long nativePointer) {
        if (this.isFinalized) {
            TableQuery.nativeClose(nativePointer);
        } else {
            this.abandonedQueries.add(Long.valueOf(nativePointer));
        }
    }

    public void asyncDisposeGroup(long nativePointer) {
        Group.nativeClose(nativePointer);
    }

    public void asyncDisposeSharedGroup(long nativePointer) {
        SharedGroup.nativeClose(nativePointer);
    }

    protected void finalize() {
        synchronized (this) {
            this.isFinalized = true;
        }
        executeDelayedDisposal();
    }
}
