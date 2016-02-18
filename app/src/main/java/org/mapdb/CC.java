package org.mapdb;

public interface CC {
    public static final int ASYNC_RECID_PREALLOC_QUEUE_SIZE = 128;
    public static final int ASYNC_WRITE_FLUSH_DELAY = 100;
    public static final int ASYNC_WRITE_QUEUE_SIZE = 32000;
    public static final int CONCURRENCY = 128;
    public static final String DEFAULT_CACHE = "hashTable";
    public static final int DEFAULT_CACHE_SIZE = 32768;
    public static final int DEFAULT_FREE_SPACE_RECLAIM_Q = 5;
    public static final boolean FAIR_LOCKS = false;
    public static final boolean LOG_STORE = false;
    public static final boolean PARANOID = false;
    public static final int VOLUME_CHUNK_SHIFT = 20;
}
