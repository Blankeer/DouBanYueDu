package org.mapdb;

public interface Engine {
    public static final long CATALOG_RECID = 1;
    public static final long CHECK_RECORD = 3;
    public static final long CLASS_INFO_RECID = 2;
    public static final long LAST_RESERVED_RECID = 7;

    boolean canRollback();

    boolean canSnapshot();

    void clearCache();

    void close();

    void closeListenerRegister(Runnable runnable);

    void closeListenerUnregister(Runnable runnable);

    void commit();

    void compact();

    <A> boolean compareAndSwap(long j, A a, A a2, Serializer<A> serializer);

    <A> void delete(long j, Serializer<A> serializer);

    <A> A get(long j, Serializer<A> serializer);

    @Deprecated
    SerializerPojo getSerializerPojo();

    boolean isClosed();

    boolean isReadOnly();

    long preallocate();

    void preallocate(long[] jArr);

    <A> long put(A a, Serializer<A> serializer);

    void rollback() throws UnsupportedOperationException;

    Engine snapshot() throws UnsupportedOperationException;

    <A> void update(long j, A a, Serializer<A> serializer);
}
