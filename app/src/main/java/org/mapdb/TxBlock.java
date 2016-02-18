package org.mapdb;

public interface TxBlock {
    void tx(DB db) throws TxRollbackException;
}
