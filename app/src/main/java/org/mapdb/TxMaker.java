package org.mapdb;

import org.mapdb.Fun.Function1;

public class TxMaker {
    protected static final Object DELETED;
    protected Engine engine;
    private final boolean strictDBGet;
    private final boolean txSnapshotsEnabled;

    static {
        DELETED = new Object();
    }

    public TxMaker(Engine engine) {
        this(engine, false, false);
    }

    public TxMaker(Engine engine, boolean strictDBGet, boolean txSnapshotsEnabled) {
        if (engine == null) {
            throw new IllegalArgumentException();
        } else if (!engine.canSnapshot()) {
            throw new IllegalArgumentException("Snapshot must be enabled for TxMaker");
        } else if (engine.isReadOnly()) {
            throw new IllegalArgumentException("TxMaker can not be used with read-only Engine");
        } else {
            this.engine = engine;
            this.strictDBGet = strictDBGet;
            this.txSnapshotsEnabled = txSnapshotsEnabled;
        }
    }

    public DB makeTx() {
        Engine snapshot = this.engine.snapshot();
        if (this.txSnapshotsEnabled) {
            snapshot = new TxEngine(snapshot, false);
        }
        return new DB(snapshot, this.strictDBGet, false);
    }

    public void close() {
        this.engine.close();
        this.engine = null;
    }

    public void execute(TxBlock txBlock) {
        DB tx;
        while (true) {
            tx = makeTx();
            try {
                break;
            } catch (TxRollbackException e) {
                if (!tx.isClosed()) {
                    tx.close();
                }
            }
        }
        txBlock.tx(tx);
        if (!tx.isClosed()) {
            tx.commit();
        }
    }

    public <A> A execute(Function1<A, DB> txBlock) {
        DB tx;
        while (true) {
            tx = makeTx();
            try {
                break;
            } catch (TxRollbackException e) {
                if (!tx.isClosed()) {
                    tx.close();
                }
            }
        }
        A a = txBlock.run(tx);
        if (!tx.isClosed()) {
            tx.commit();
        }
        return a;
    }
}
