package org.mapdb;

import android.support.v4.media.session.PlaybackStateCompat;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.constant.DeviceType;
import io.realm.internal.Table;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.mapdb.Atomic.Boolean;
import org.mapdb.Atomic.Integer;
import org.mapdb.Atomic.Long;
import org.mapdb.Atomic.String;
import org.mapdb.Atomic.Var;
import org.mapdb.BTreeKeySerializer.BasicKeySerializer;
import org.mapdb.BTreeKeySerializer.Tuple2KeySerializer;
import org.mapdb.BTreeKeySerializer.Tuple3KeySerializer;
import org.mapdb.BTreeKeySerializer.Tuple4KeySerializer;
import org.mapdb.BTreeKeySerializer.Tuple5KeySerializer;
import org.mapdb.BTreeKeySerializer.Tuple6KeySerializer;
import org.mapdb.EngineWrapper.ReadOnlyEngine;
import org.mapdb.Fun.Function1;
import org.mapdb.Fun.Tuple2;
import org.mapdb.Queues.CircularQueue;
import org.mapdb.Queues.Queue;
import org.mapdb.Queues.Stack;

public class DB {
    static final /* synthetic */ boolean $assertionsDisabled;
    protected SortedMap<String, Object> catalog;
    protected Engine engine;
    protected Map<String, WeakReference<?>> namesInstanciated;
    protected Map<IdentityWrapper, String> namesLookup;
    protected final boolean strictDBGet;

    /* renamed from: org.mapdb.DB.1 */
    class AnonymousClass1 implements Comparator {
        final /* synthetic */ BTreeMapMaker val$m;

        AnonymousClass1(BTreeMapMaker bTreeMapMaker) {
            this.val$m = bTreeMapMaker;
        }

        public int compare(Object o1, Object o2) {
            return -this.val$m.comparator.compare(this.val$m.pumpKeyExtractor.run(o1), this.val$m.pumpKeyExtractor.run(o2));
        }
    }

    public class BTreeMapMaker {
        protected Comparator comparator;
        protected boolean counter;
        protected BTreeKeySerializer keySerializer;
        protected final String name;
        protected int nodeSize;
        protected boolean pumpIgnoreDuplicates;
        protected Function1 pumpKeyExtractor;
        protected int pumpPresortBatchSize;
        protected Iterator pumpSource;
        protected Function1 pumpValueExtractor;
        protected Serializer valueSerializer;
        protected boolean valuesOutsideNodes;

        public BTreeMapMaker(String name) {
            this.nodeSize = 32;
            this.valuesOutsideNodes = false;
            this.counter = false;
            this.pumpPresortBatchSize = -1;
            this.pumpIgnoreDuplicates = false;
            this.name = name;
        }

        public BTreeMapMaker nodeSize(int nodeSize) {
            this.nodeSize = nodeSize;
            return this;
        }

        public BTreeMapMaker valuesOutsideNodesEnable() {
            this.valuesOutsideNodes = true;
            return this;
        }

        public BTreeMapMaker counterEnable() {
            this.counter = true;
            return this;
        }

        public BTreeMapMaker keySerializer(BTreeKeySerializer<?> keySerializer) {
            this.keySerializer = keySerializer;
            return this;
        }

        public BTreeMapMaker keySerializerWrap(Serializer<?> serializer) {
            this.keySerializer = new BasicKeySerializer(serializer);
            return this;
        }

        public BTreeMapMaker valueSerializer(Serializer<?> valueSerializer) {
            this.valueSerializer = valueSerializer;
            return this;
        }

        public BTreeMapMaker comparator(Comparator<?> comparator) {
            this.comparator = comparator;
            return this;
        }

        public <K, V> BTreeMapMaker pumpSource(Iterator<K> keysSource, Function1<V, K> valueExtractor) {
            this.pumpSource = keysSource;
            this.pumpKeyExtractor = Fun.extractNoTransform();
            this.pumpValueExtractor = valueExtractor;
            return this;
        }

        public <K, V> BTreeMapMaker pumpSource(Iterator<Tuple2<K, V>> entriesSource) {
            this.pumpSource = entriesSource;
            this.pumpKeyExtractor = Fun.extractKey();
            this.pumpValueExtractor = Fun.extractValue();
            return this;
        }

        public BTreeMapMaker pumpPresort(int batchSize) {
            this.pumpPresortBatchSize = batchSize;
            return this;
        }

        public <K> BTreeMapMaker pumpIgnoreDuplicates() {
            this.pumpIgnoreDuplicates = true;
            return this;
        }

        public <K, V> BTreeMap<K, V> make() {
            return DB.this.createTreeMap(this);
        }

        public <K, V> BTreeMap<K, V> makeOrGet() {
            BTreeMap<K, V> make;
            synchronized (DB.this) {
                make = DB.this.catGet(new StringBuilder().append(this.name).append(".type").toString()) == null ? make() : DB.this.getTreeMap(this.name);
            }
            return make;
        }

        public <V> BTreeMap<String, V> makeStringMap() {
            this.keySerializer = BTreeKeySerializer.STRING;
            return make();
        }

        public <V> BTreeMap<Long, V> makeLongMap() {
            this.keySerializer = BTreeKeySerializer.ZERO_OR_POSITIVE_LONG;
            return make();
        }
    }

    public class BTreeSetMaker {
        protected Comparator<?> comparator;
        protected boolean counter;
        protected final String name;
        protected int nodeSize;
        protected boolean pumpIgnoreDuplicates;
        protected int pumpPresortBatchSize;
        protected Iterator<?> pumpSource;
        protected BTreeKeySerializer<?> serializer;

        public BTreeSetMaker(String name) {
            this.nodeSize = 32;
            this.counter = false;
            this.pumpPresortBatchSize = -1;
            this.pumpIgnoreDuplicates = false;
            this.name = name;
        }

        public BTreeSetMaker nodeSize(int nodeSize) {
            this.nodeSize = nodeSize;
            return this;
        }

        public BTreeSetMaker counterEnable() {
            this.counter = true;
            return this;
        }

        public BTreeSetMaker serializer(BTreeKeySerializer<?> serializer) {
            this.serializer = serializer;
            return this;
        }

        public BTreeSetMaker comparator(Comparator<?> comparator) {
            this.comparator = comparator;
            return this;
        }

        public BTreeSetMaker pumpSource(Iterator<?> source) {
            this.pumpSource = source;
            return this;
        }

        public <K> BTreeSetMaker pumpIgnoreDuplicates() {
            this.pumpIgnoreDuplicates = true;
            return this;
        }

        public BTreeSetMaker pumpPresort(int batchSize) {
            this.pumpPresortBatchSize = batchSize;
            return this;
        }

        public <K> NavigableSet<K> make() {
            return DB.this.createTreeSet(this);
        }

        public <K> NavigableSet<K> makeOrGet() {
            NavigableSet<K> make;
            synchronized (DB.this) {
                make = DB.this.catGet(new StringBuilder().append(this.name).append(".type").toString()) == null ? make() : DB.this.getTreeSet(this.name);
            }
            return make;
        }

        public NavigableSet<String> makeStringSet() {
            this.serializer = BTreeKeySerializer.STRING;
            return make();
        }

        public NavigableSet<Long> makeLongSet() {
            this.serializer = BTreeKeySerializer.ZERO_OR_POSITIVE_LONG;
            return make();
        }
    }

    public class HTreeMapMaker {
        protected boolean counter;
        protected long expire;
        protected long expireAccess;
        protected long expireMaxSize;
        protected long expireStoreSize;
        protected Hasher<?> hasher;
        protected Serializer<?> keySerializer;
        protected final String name;
        protected Function1<?, ?> valueCreator;
        protected Serializer<?> valueSerializer;

        public HTreeMapMaker(String name) {
            this.counter = false;
            this.keySerializer = null;
            this.valueSerializer = null;
            this.expireMaxSize = 0;
            this.expire = 0;
            this.expireAccess = 0;
            this.hasher = null;
            this.valueCreator = null;
            this.name = name;
        }

        public HTreeMapMaker counterEnable() {
            this.counter = true;
            return this;
        }

        public HTreeMapMaker keySerializer(Serializer<?> keySerializer) {
            this.keySerializer = keySerializer;
            return this;
        }

        public HTreeMapMaker valueSerializer(Serializer<?> valueSerializer) {
            this.valueSerializer = valueSerializer;
            return this;
        }

        public HTreeMapMaker expireMaxSize(long maxSize) {
            this.expireMaxSize = maxSize;
            this.counter = true;
            return this;
        }

        public HTreeMapMaker expireAfterWrite(long interval, TimeUnit timeUnit) {
            this.expire = timeUnit.toMillis(interval);
            return this;
        }

        public HTreeMapMaker expireAfterWrite(long interval) {
            this.expire = interval;
            return this;
        }

        public HTreeMapMaker expireAfterAccess(long interval, TimeUnit timeUnit) {
            this.expireAccess = timeUnit.toMillis(interval);
            return this;
        }

        public HTreeMapMaker expireAfterAccess(long interval) {
            this.expireAccess = interval;
            return this;
        }

        public HTreeMapMaker expireStoreSize(double maxStoreSize) {
            this.expireStoreSize = (long) (((maxStoreSize * 1024.0d) * 1024.0d) * 1024.0d);
            return this;
        }

        public HTreeMapMaker valueCreator(Function1<?, ?> valueCreator) {
            this.valueCreator = valueCreator;
            return this;
        }

        public HTreeMapMaker hasher(Hasher<?> hasher) {
            this.hasher = hasher;
            return this;
        }

        public <K, V> HTreeMap<K, V> make() {
            if (this.expireMaxSize != 0) {
                this.counter = true;
            }
            return DB.this.createHashMap(this);
        }

        public <K, V> HTreeMap<K, V> makeOrGet() {
            HTreeMap<K, V> make;
            synchronized (DB.this) {
                make = DB.this.catGet(new StringBuilder().append(this.name).append(".type").toString()) == null ? make() : DB.this.getHashMap(this.name);
            }
            return make;
        }
    }

    public class HTreeSetMaker {
        protected boolean counter;
        protected long expire;
        protected long expireAccess;
        protected long expireMaxSize;
        protected long expireStoreSize;
        protected Hasher<?> hasher;
        protected final String name;
        protected Serializer<?> serializer;

        public HTreeSetMaker(String name) {
            this.counter = false;
            this.serializer = null;
            this.expireMaxSize = 0;
            this.expireStoreSize = 0;
            this.expire = 0;
            this.expireAccess = 0;
            this.hasher = null;
            this.name = name;
        }

        public HTreeSetMaker counterEnable() {
            this.counter = true;
            return this;
        }

        public HTreeSetMaker serializer(Serializer<?> serializer) {
            this.serializer = serializer;
            return this;
        }

        public HTreeSetMaker expireMaxSize(long maxSize) {
            this.expireMaxSize = maxSize;
            this.counter = true;
            return this;
        }

        public HTreeSetMaker expireStoreSize(double maxStoreSize) {
            this.expireStoreSize = (long) (((maxStoreSize * 1024.0d) * 1024.0d) * 1024.0d);
            return this;
        }

        public HTreeSetMaker expireAfterWrite(long interval, TimeUnit timeUnit) {
            this.expire = timeUnit.toMillis(interval);
            return this;
        }

        public HTreeSetMaker expireAfterWrite(long interval) {
            this.expire = interval;
            return this;
        }

        public HTreeSetMaker expireAfterAccess(long interval, TimeUnit timeUnit) {
            this.expireAccess = timeUnit.toMillis(interval);
            return this;
        }

        public HTreeSetMaker expireAfterAccess(long interval) {
            this.expireAccess = interval;
            return this;
        }

        public HTreeSetMaker hasher(Hasher<?> hasher) {
            this.hasher = hasher;
            return this;
        }

        public <K> Set<K> make() {
            if (this.expireMaxSize != 0) {
                this.counter = true;
            }
            return DB.this.createHashSet(this);
        }

        public <K> Set<K> makeOrGet() {
            Set<K> make;
            synchronized (DB.this) {
                make = DB.this.catGet(new StringBuilder().append(this.name).append(".type").toString()) == null ? make() : DB.this.getHashSet(this.name);
            }
            return make;
        }
    }

    protected static class IdentityWrapper {
        final Object o;

        public IdentityWrapper(Object o) {
            this.o = o;
        }

        public int hashCode() {
            return System.identityHashCode(this.o);
        }

        public boolean equals(Object v) {
            return ((IdentityWrapper) v).o == this.o;
        }
    }

    static {
        $assertionsDisabled = !DB.class.desiredAssertionStatus();
    }

    public DB(Engine engine) {
        this(engine, false, false);
    }

    public DB(Engine engine, boolean strictDBGet, boolean disableLocks) {
        this.namesInstanciated = new HashMap();
        this.namesLookup = Collections.synchronizedMap(new HashMap());
        if (!(engine instanceof EngineWrapper)) {
            engine = new EngineWrapper(engine);
        }
        this.engine = engine;
        this.strictDBGet = strictDBGet;
        engine.getSerializerPojo().setDb(this);
        reinit();
    }

    protected void reinit() {
        this.catalog = BTreeMap.preinitCatalog(this);
    }

    public <A> A catGet(String name, A init) {
        if ($assertionsDisabled || Thread.holdsLock(this)) {
            A ret = this.catalog.get(name);
            return ret != null ? ret : init;
        } else {
            throw new AssertionError();
        }
    }

    public <A> A catGet(String name) {
        if ($assertionsDisabled || Thread.holdsLock(this)) {
            return this.catalog.get(name);
        }
        throw new AssertionError();
    }

    public <A> A catPut(String name, A value) {
        if ($assertionsDisabled || Thread.holdsLock(this)) {
            this.catalog.put(name, value);
            return value;
        }
        throw new AssertionError();
    }

    public <A> A catPut(String name, A value, A retValueIfNull) {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        } else if (value == null) {
            return retValueIfNull;
        } else {
            this.catalog.put(name, value);
            return value;
        }
    }

    public String getNameForObject(Object obj) {
        return (String) this.namesLookup.get(new IdentityWrapper(obj));
    }

    public synchronized <K, V> HTreeMap<K, V> getHashMap(String name) {
        return getHashMap(name, null);
    }

    public synchronized <K, V> HTreeMap<K, V> getHashMap(String name, Function1<V, K> valueCreator) {
        HTreeMap<K, V> hTreeMap;
        checkNotClosed();
        HTreeMap<K, V> ret = (HTreeMap) getFromWeakCollection(name);
        if (ret != null) {
            hTreeMap = ret;
        } else {
            String type = (String) catGet(name + ".type", null);
            if (type == null) {
                checkShouldCreate(name);
                if (this.engine.isReadOnly()) {
                    Engine e = new StoreHeap();
                    new DB(e).getHashMap(DeviceType.IPAD);
                    hTreeMap = (HTreeMap) namedPut(name, new DB(new ReadOnlyEngine(e)).getHashMap(DeviceType.IPAD));
                } else if (valueCreator != null) {
                    hTreeMap = createHashMap(name).valueCreator(valueCreator).make();
                } else {
                    hTreeMap = createHashMap(name).make();
                }
            } else {
                checkType(type, "HashMap");
                Function1<V, K> function1 = valueCreator;
                ret = new HTreeMap(this.engine, ((Long) catGet(name + ".counterRecid")).longValue(), ((Integer) catGet(name + ".hashSalt")).intValue(), (long[]) catGet(name + ".segmentRecids"), (Serializer) catGet(name + ".keySerializer", getDefaultSerializer()), (Serializer) catGet(name + ".valueSerializer", getDefaultSerializer()), ((Long) catGet(name + ".expireTimeStart", Long.valueOf(0))).longValue(), ((Long) catGet(name + ".expire", Long.valueOf(0))).longValue(), ((Long) catGet(name + ".expireAccess", Long.valueOf(0))).longValue(), ((Long) catGet(name + ".expireMaxSize", Long.valueOf(0))).longValue(), ((Long) catGet(name + ".expireStoreSize", Long.valueOf(0))).longValue(), (long[]) catGet(name + ".expireHeads", null), (long[]) catGet(name + ".expireTails", null), function1, (Hasher) catGet(name + ".hasher", Hasher.BASIC), false);
                namedPut(name, ret);
                hTreeMap = ret;
            }
        }
        return hTreeMap;
    }

    public <V> V namedPut(String name, Object ret) {
        this.namesInstanciated.put(name, new WeakReference(ret));
        this.namesLookup.put(new IdentityWrapper(ret), name);
        return ret;
    }

    public HTreeMapMaker createHashMap(String name) {
        return new HTreeMapMaker(name);
    }

    protected synchronized <K, V> HTreeMap<K, V> createHashMap(HTreeMapMaker m) {
        HTreeMap<K, V> ret;
        long put;
        String name = m.name;
        checkNameNotExists(name);
        long expireTimeStart = 0;
        long expire = 0;
        long expireAccess = 0;
        long expireMaxSize = 0;
        long expireStoreSize = 0;
        long[] expireHeads = null;
        long[] expireTails = null;
        if (!(m.expire == 0 && m.expireAccess == 0 && m.expireMaxSize == 0 && m.expireStoreSize == 0)) {
            expireTimeStart = ((Long) catPut(name + ".expireTimeStart", Long.valueOf(System.currentTimeMillis()))).longValue();
            expire = ((Long) catPut(name + ".expire", Long.valueOf(m.expire))).longValue();
            expireAccess = ((Long) catPut(name + ".expireAccess", Long.valueOf(m.expireAccess))).longValue();
            expireMaxSize = ((Long) catPut(name + ".expireMaxSize", Long.valueOf(m.expireMaxSize))).longValue();
            expireStoreSize = ((Long) catPut(name + ".expireStoreSize", Long.valueOf(m.expireStoreSize))).longValue();
            expireHeads = new long[16];
            expireTails = new long[16];
            for (int i = 0; i < 16; i++) {
                expireHeads[i] = this.engine.put(Long.valueOf(0), Serializer.LONG);
                expireTails[i] = this.engine.put(Long.valueOf(0), Serializer.LONG);
            }
            catPut(name + ".expireHeads", expireHeads);
            catPut(name + ".expireTails", expireTails);
        }
        if (m.hasher != null) {
            catPut(name + ".hasher", m.hasher);
        }
        Engine engine = this.engine;
        String str = name + ".counterRecid";
        if (m.counter) {
            put = this.engine.put(Long.valueOf(0), Serializer.LONG);
        } else {
            put = 0;
        }
        put = ((Long) catPut(str, Long.valueOf(put))).longValue();
        int intValue = ((Integer) catPut(name + ".hashSalt", Integer.valueOf(new Random().nextInt()))).intValue();
        long[] jArr = (long[]) catPut(name + ".segmentRecids", HTreeMap.preallocateSegments(this.engine));
        Serializer serializer = (Serializer) catPut(name + ".keySerializer", m.keySerializer, getDefaultSerializer());
        String str2 = name + ".valueSerializer";
        Serializer serializer2 = m.valueSerializer;
        Serializer defaultSerializer = getDefaultSerializer();
        ret = new HTreeMap(engine, put, intValue, jArr, serializer, (Serializer) catPut(str2, serializer2, r25), expireTimeStart, expire, expireAccess, expireMaxSize, expireStoreSize, expireHeads, expireTails, m.valueCreator, m.hasher, false);
        this.catalog.put(name + ".type", "HashMap");
        namedPut(name, ret);
        return ret;
    }

    public synchronized <K> Set<K> getHashSet(String name) {
        Set<K> set;
        checkNotClosed();
        Set<K> ret = (Set) getFromWeakCollection(name);
        if (ret != null) {
            set = ret;
        } else {
            String type = (String) catGet(name + ".type", null);
            if (type == null) {
                checkShouldCreate(name);
                if (this.engine.isReadOnly()) {
                    Engine e = new StoreHeap();
                    new DB(e).getHashSet(DeviceType.IPAD);
                    set = (Set) namedPut(name, new DB(new ReadOnlyEngine(e)).getHashSet(DeviceType.IPAD));
                } else {
                    set = createHashSet(name).makeOrGet();
                }
            } else {
                checkType(type, "HashSet");
                ret = new HTreeMap(this.engine, ((Long) catGet(name + ".counterRecid")).longValue(), ((Integer) catGet(name + ".hashSalt")).intValue(), (long[]) catGet(name + ".segmentRecids"), (Serializer) catGet(name + ".serializer", getDefaultSerializer()), null, ((Long) catGet(name + ".expireTimeStart", Long.valueOf(0))).longValue(), ((Long) catGet(name + ".expire", Long.valueOf(0))).longValue(), ((Long) catGet(name + ".expireAccess", Long.valueOf(0))).longValue(), ((Long) catGet(name + ".expireMaxSize", Long.valueOf(0))).longValue(), ((Long) catGet(name + ".expireStoreSize", Long.valueOf(0))).longValue(), (long[]) catGet(name + ".expireHeads", null), (long[]) catGet(name + ".expireTails", null), null, (Hasher) catGet(name + ".hasher", Hasher.BASIC), false).keySet();
                namedPut(name, ret);
                set = ret;
            }
        }
        return set;
    }

    public synchronized HTreeSetMaker createHashSet(String name) {
        return new HTreeSetMaker(name);
    }

    protected synchronized <K> Set<K> createHashSet(HTreeSetMaker m) {
        Set<K> ret2;
        long put;
        String name = m.name;
        checkNameNotExists(name);
        long expireTimeStart = 0;
        long expire = 0;
        long expireAccess = 0;
        long expireMaxSize = 0;
        long expireStoreSize = 0;
        long[] expireHeads = null;
        long[] expireTails = null;
        if (!(m.expire == 0 && m.expireAccess == 0 && m.expireMaxSize == 0)) {
            expireTimeStart = ((Long) catPut(name + ".expireTimeStart", Long.valueOf(System.currentTimeMillis()))).longValue();
            expire = ((Long) catPut(name + ".expire", Long.valueOf(m.expire))).longValue();
            expireAccess = ((Long) catPut(name + ".expireAccess", Long.valueOf(m.expireAccess))).longValue();
            expireMaxSize = ((Long) catPut(name + ".expireMaxSize", Long.valueOf(m.expireMaxSize))).longValue();
            expireStoreSize = ((Long) catPut(name + ".expireStoreSize", Long.valueOf(m.expireStoreSize))).longValue();
            expireHeads = new long[16];
            expireTails = new long[16];
            for (int i = 0; i < 16; i++) {
                expireHeads[i] = this.engine.put(Long.valueOf(0), Serializer.LONG);
                expireTails[i] = this.engine.put(Long.valueOf(0), Serializer.LONG);
            }
            catPut(name + ".expireHeads", expireHeads);
            catPut(name + ".expireTails", expireTails);
        }
        if (m.hasher != null) {
            catPut(name + ".hasher", m.hasher);
        }
        Engine engine = this.engine;
        String str = name + ".counterRecid";
        if (m.counter) {
            put = this.engine.put(Long.valueOf(0), Serializer.LONG);
        } else {
            put = 0;
        }
        put = ((Long) catPut(str, Long.valueOf(put))).longValue();
        int intValue = ((Integer) catPut(name + ".hashSalt", Integer.valueOf(new Random().nextInt()))).intValue();
        long[] jArr = (long[]) catPut(name + ".segmentRecids", HTreeMap.preallocateSegments(this.engine));
        String str2 = name + ".serializer";
        Serializer serializer = m.serializer;
        Serializer defaultSerializer = getDefaultSerializer();
        ret2 = new HTreeMap(engine, put, intValue, jArr, (Serializer) catPut(str2, serializer, r24), null, expireTimeStart, expire, expireAccess, expireMaxSize, expireStoreSize, expireHeads, expireTails, null, m.hasher, false).keySet();
        this.catalog.put(name + ".type", "HashSet");
        namedPut(name, ret2);
        return ret2;
    }

    public synchronized <K, V> BTreeMap<K, V> getTreeMap(String name) {
        BTreeMap<K, V> bTreeMap;
        checkNotClosed();
        BTreeMap<K, V> ret = (BTreeMap) getFromWeakCollection(name);
        if (ret != null) {
            bTreeMap = ret;
        } else {
            String type = (String) catGet(name + ".type", null);
            if (type == null) {
                checkShouldCreate(name);
                if (this.engine.isReadOnly()) {
                    Engine e = new StoreHeap();
                    new DB(e).getTreeMap(DeviceType.IPAD);
                    bTreeMap = (BTreeMap) namedPut(name, new DB(new ReadOnlyEngine(e)).getTreeMap(DeviceType.IPAD));
                } else {
                    bTreeMap = createTreeMap(name).make();
                }
            } else {
                checkType(type, "TreeMap");
                ret = new BTreeMap(this.engine, ((Long) catGet(name + ".rootRecidRef")).longValue(), ((Integer) catGet(name + ".maxNodeSize", Integer.valueOf(32))).intValue(), ((Boolean) catGet(name + ".valuesOutsideNodes", Boolean.valueOf(false))).booleanValue(), ((Long) catGet(name + ".counterRecid", Long.valueOf(0))).longValue(), (BTreeKeySerializer) catGet(name + ".keySerializer", new BasicKeySerializer(getDefaultSerializer())), (Serializer) catGet(name + ".valueSerializer", getDefaultSerializer()), (Comparator) catGet(name + ".comparator", BTreeMap.COMPARABLE_COMPARATOR), ((Integer) catGet(name + ".numberOfNodeMetas", Integer.valueOf(0))).intValue(), false);
                namedPut(name, ret);
                bTreeMap = ret;
            }
        }
        return bTreeMap;
    }

    public BTreeMapMaker createTreeMap(String name) {
        return new BTreeMapMaker(name);
    }

    protected synchronized <K, V> BTreeMap<K, V> createTreeMap(BTreeMapMaker m) {
        BTreeMap<K, V> ret;
        long rootRecidRef;
        String name = m.name;
        checkNameNotExists(name);
        m.keySerializer = fillNulls(m.keySerializer);
        m.keySerializer = (BTreeKeySerializer) catPut(name + ".keySerializer", m.keySerializer, new BasicKeySerializer(getDefaultSerializer()));
        m.valueSerializer = (Serializer) catPut(name + ".valueSerializer", m.valueSerializer, getDefaultSerializer());
        if (m.comparator == null) {
            m.comparator = m.keySerializer.getComparator();
            if (m.comparator == null) {
                m.comparator = BTreeMap.COMPARABLE_COMPARATOR;
            }
        }
        m.comparator = (Comparator) catPut(name + ".comparator", m.comparator);
        if (!(m.pumpPresortBatchSize == -1 || m.pumpSource == null)) {
            m.pumpSource = Pump.sort(m.pumpSource, m.pumpIgnoreDuplicates, m.pumpPresortBatchSize, new AnonymousClass1(m), getDefaultSerializer());
        }
        long counterRecid = !m.counter ? 0 : this.engine.put(Long.valueOf(0), Serializer.LONG);
        if (m.pumpSource == null) {
            rootRecidRef = BTreeMap.createRootRef(this.engine, m.keySerializer, m.valueSerializer, m.comparator, 0);
        } else {
            rootRecidRef = Pump.buildTreeMap(m.pumpSource, this.engine, m.pumpKeyExtractor, m.pumpValueExtractor, m.pumpIgnoreDuplicates, m.nodeSize, m.valuesOutsideNodes, counterRecid, m.keySerializer, m.valueSerializer, m.comparator);
        }
        ret = new BTreeMap(this.engine, ((Long) catPut(name + ".rootRecidRef", Long.valueOf(rootRecidRef))).longValue(), ((Integer) catPut(name + ".maxNodeSize", Integer.valueOf(m.nodeSize))).intValue(), ((Boolean) catPut(name + ".valuesOutsideNodes", Boolean.valueOf(m.valuesOutsideNodes))).booleanValue(), ((Long) catPut(name + ".counterRecid", Long.valueOf(counterRecid))).longValue(), m.keySerializer, m.valueSerializer, m.comparator, ((Integer) catPut(m.name + ".numberOfNodeMetas", Integer.valueOf(0))).intValue(), false);
        this.catalog.put(name + ".type", "TreeMap");
        namedPut(name, ret);
        return ret;
    }

    protected <K> BTreeKeySerializer<K> fillNulls(BTreeKeySerializer<K> keySerializer) {
        if (keySerializer == null) {
            return null;
        }
        if (keySerializer instanceof Tuple2KeySerializer) {
            Tuple2KeySerializer<?, ?> s = (Tuple2KeySerializer) keySerializer;
            return new Tuple2KeySerializer(s.aComparator != null ? s.aComparator : BTreeMap.COMPARABLE_COMPARATOR, s.aSerializer != null ? s.aSerializer : getDefaultSerializer(), s.bSerializer != null ? s.bSerializer : getDefaultSerializer());
        } else if (keySerializer instanceof Tuple3KeySerializer) {
            Tuple3KeySerializer<?, ?, ?> s2 = (Tuple3KeySerializer) keySerializer;
            return new Tuple3KeySerializer(s2.aComparator != null ? s2.aComparator : BTreeMap.COMPARABLE_COMPARATOR, s2.bComparator != null ? s2.bComparator : BTreeMap.COMPARABLE_COMPARATOR, s2.aSerializer != null ? s2.aSerializer : getDefaultSerializer(), s2.bSerializer != null ? s2.bSerializer : getDefaultSerializer(), s2.cSerializer != null ? s2.cSerializer : getDefaultSerializer());
        } else if (keySerializer instanceof Tuple4KeySerializer) {
            Tuple4KeySerializer<?, ?, ?, ?> s3 = (Tuple4KeySerializer) keySerializer;
            return new Tuple4KeySerializer(s3.aComparator != null ? s3.aComparator : BTreeMap.COMPARABLE_COMPARATOR, s3.bComparator != null ? s3.bComparator : BTreeMap.COMPARABLE_COMPARATOR, s3.cComparator != null ? s3.cComparator : BTreeMap.COMPARABLE_COMPARATOR, s3.aSerializer != null ? s3.aSerializer : getDefaultSerializer(), s3.bSerializer != null ? s3.bSerializer : getDefaultSerializer(), s3.cSerializer != null ? s3.cSerializer : getDefaultSerializer(), s3.dSerializer != null ? s3.dSerializer : getDefaultSerializer());
        } else if (keySerializer instanceof Tuple5KeySerializer) {
            Tuple5KeySerializer<?, ?, ?, ?, ?> s4 = (Tuple5KeySerializer) keySerializer;
            return new Tuple5KeySerializer(s4.aComparator != null ? s4.aComparator : BTreeMap.COMPARABLE_COMPARATOR, s4.bComparator != null ? s4.bComparator : BTreeMap.COMPARABLE_COMPARATOR, s4.cComparator != null ? s4.cComparator : BTreeMap.COMPARABLE_COMPARATOR, s4.dComparator != null ? s4.dComparator : BTreeMap.COMPARABLE_COMPARATOR, s4.aSerializer != null ? s4.aSerializer : getDefaultSerializer(), s4.bSerializer != null ? s4.bSerializer : getDefaultSerializer(), s4.cSerializer != null ? s4.cSerializer : getDefaultSerializer(), s4.dSerializer != null ? s4.dSerializer : getDefaultSerializer(), s4.eSerializer != null ? s4.eSerializer : getDefaultSerializer());
        } else if (!(keySerializer instanceof Tuple6KeySerializer)) {
            return keySerializer;
        } else {
            Tuple6KeySerializer<?, ?, ?, ?, ?, ?> s5 = (Tuple6KeySerializer) keySerializer;
            return new Tuple6KeySerializer(s5.aComparator != null ? s5.aComparator : BTreeMap.COMPARABLE_COMPARATOR, s5.bComparator != null ? s5.bComparator : BTreeMap.COMPARABLE_COMPARATOR, s5.cComparator != null ? s5.cComparator : BTreeMap.COMPARABLE_COMPARATOR, s5.dComparator != null ? s5.dComparator : BTreeMap.COMPARABLE_COMPARATOR, s5.eComparator != null ? s5.eComparator : BTreeMap.COMPARABLE_COMPARATOR, s5.aSerializer != null ? s5.aSerializer : getDefaultSerializer(), s5.bSerializer != null ? s5.bSerializer : getDefaultSerializer(), s5.cSerializer != null ? s5.cSerializer : getDefaultSerializer(), s5.dSerializer != null ? s5.dSerializer : getDefaultSerializer(), s5.eSerializer != null ? s5.eSerializer : getDefaultSerializer(), s5.fSerializer != null ? s5.fSerializer : getDefaultSerializer());
        }
    }

    public SortedMap<String, Object> getCatalog() {
        return this.catalog;
    }

    public synchronized <K> NavigableSet<K> getTreeSet(String name) {
        NavigableSet<K> navigableSet;
        checkNotClosed();
        NavigableSet<K> ret = (NavigableSet) getFromWeakCollection(name);
        if (ret != null) {
            navigableSet = ret;
        } else {
            String type = (String) catGet(name + ".type", null);
            if (type == null) {
                checkShouldCreate(name);
                if (this.engine.isReadOnly()) {
                    Engine e = new StoreHeap();
                    new DB(e).getTreeSet(DeviceType.IPAD);
                    navigableSet = (NavigableSet) namedPut(name, new DB(new ReadOnlyEngine(e)).getTreeSet(DeviceType.IPAD));
                } else {
                    navigableSet = createTreeSet(name).make();
                }
            } else {
                checkType(type, "TreeSet");
                ret = new BTreeMap(this.engine, ((Long) catGet(name + ".rootRecidRef")).longValue(), ((Integer) catGet(name + ".maxNodeSize", Integer.valueOf(32))).intValue(), false, ((Long) catGet(name + ".counterRecid", Long.valueOf(0))).longValue(), (BTreeKeySerializer) catGet(name + ".keySerializer", new BasicKeySerializer(getDefaultSerializer())), null, (Comparator) catGet(name + ".comparator", BTreeMap.COMPARABLE_COMPARATOR), ((Integer) catGet(name + ".numberOfNodeMetas", Integer.valueOf(0))).intValue(), false).keySet();
                namedPut(name, ret);
                navigableSet = ret;
            }
        }
        return navigableSet;
    }

    public synchronized BTreeSetMaker createTreeSet(String name) {
        return new BTreeSetMaker(name);
    }

    public synchronized <K> NavigableSet<K> createTreeSet(BTreeSetMaker m) {
        NavigableSet<K> ret;
        long rootRecidRef;
        checkNameNotExists(m.name);
        m.serializer = fillNulls(m.serializer);
        m.serializer = (BTreeKeySerializer) catPut(m.name + ".keySerializer", m.serializer, new BasicKeySerializer(getDefaultSerializer()));
        m.comparator = (Comparator) catPut(m.name + ".comparator", m.comparator, BTreeMap.COMPARABLE_COMPARATOR);
        if (m.pumpPresortBatchSize != -1) {
            m.pumpSource = Pump.sort(m.pumpSource, m.pumpIgnoreDuplicates, m.pumpPresortBatchSize, Collections.reverseOrder(m.comparator), getDefaultSerializer());
        }
        long counterRecid = !m.counter ? 0 : this.engine.put(Long.valueOf(0), Serializer.LONG);
        if (m.pumpSource == null) {
            rootRecidRef = BTreeMap.createRootRef(this.engine, m.serializer, null, m.comparator, 0);
        } else {
            rootRecidRef = Pump.buildTreeMap(m.pumpSource, this.engine, Fun.extractNoTransform(), null, m.pumpIgnoreDuplicates, m.nodeSize, false, counterRecid, m.serializer, null, m.comparator);
        }
        ret = new BTreeMap(this.engine, ((Long) catPut(m.name + ".rootRecidRef", Long.valueOf(rootRecidRef))).longValue(), ((Integer) catPut(m.name + ".maxNodeSize", Integer.valueOf(m.nodeSize))).intValue(), false, ((Long) catPut(m.name + ".counterRecid", Long.valueOf(counterRecid))).longValue(), m.serializer, null, m.comparator, ((Integer) catPut(m.name + ".numberOfNodeMetas", Integer.valueOf(0))).intValue(), false).keySet();
        this.catalog.put(m.name + ".type", "TreeSet");
        namedPut(m.name, ret);
        return ret;
    }

    public synchronized <E> BlockingQueue<E> getQueue(String name) {
        BlockingQueue<E> blockingQueue;
        checkNotClosed();
        Queue<E> ret = (Queue) getFromWeakCollection(name);
        if (ret != null) {
            blockingQueue = ret;
        } else {
            String type = (String) catGet(name + ".type", null);
            if (type == null) {
                checkShouldCreate(name);
                if (this.engine.isReadOnly()) {
                    Engine e = new StoreHeap();
                    new DB(e).getQueue(DeviceType.IPAD);
                    blockingQueue = (BlockingQueue) namedPut(name, new DB(new ReadOnlyEngine(e)).getQueue(DeviceType.IPAD));
                } else {
                    blockingQueue = createQueue(name, null, true);
                }
            } else {
                checkType(type, "Queue");
                ret = new Queue(this.engine, (Serializer) catGet(name + ".serializer", getDefaultSerializer()), ((Long) catGet(name + ".headRecid")).longValue(), ((Long) catGet(name + ".tailRecid")).longValue(), ((Boolean) catGet(name + ".useLocks")).booleanValue());
                namedPut(name, ret);
                Object obj = ret;
            }
        }
        return blockingQueue;
    }

    public synchronized <E> BlockingQueue<E> createQueue(String name, Serializer<E> serializer, boolean useLocks) {
        Queue<E> ret;
        checkNameNotExists(name);
        long node = this.engine.put(Node.EMPTY, new NodeSerializer(serializer));
        Serializer<E> serializer2 = serializer;
        ret = new Queue(this.engine, (Serializer) catPut(name + ".serializer", serializer2, getDefaultSerializer()), ((Long) catPut(name + ".headRecid", Long.valueOf(this.engine.put(Long.valueOf(node), Serializer.LONG)))).longValue(), ((Long) catPut(name + ".tailRecid", Long.valueOf(this.engine.put(Long.valueOf(node), Serializer.LONG)))).longValue(), ((Boolean) catPut(name + ".useLocks", Boolean.valueOf(useLocks))).booleanValue());
        this.catalog.put(name + ".type", "Queue");
        namedPut(name, ret);
        return ret;
    }

    public synchronized <E> BlockingQueue<E> getStack(String name) {
        BlockingQueue<E> blockingQueue;
        checkNotClosed();
        Stack<E> ret = (Stack) getFromWeakCollection(name);
        if (ret != null) {
            blockingQueue = ret;
        } else {
            String type = (String) catGet(name + ".type", null);
            if (type == null) {
                checkShouldCreate(name);
                if (this.engine.isReadOnly()) {
                    Engine e = new StoreHeap();
                    new DB(e).getStack(DeviceType.IPAD);
                    blockingQueue = (BlockingQueue) namedPut(name, new DB(new ReadOnlyEngine(e)).getStack(DeviceType.IPAD));
                } else {
                    blockingQueue = createStack(name, null, true);
                }
            } else {
                checkType(type, "Stack");
                ret = new Stack(this.engine, (Serializer) catGet(name + ".serializer", getDefaultSerializer()), ((Long) catGet(name + ".headRecid")).longValue(), ((Boolean) catGet(name + ".useLocks")).booleanValue());
                namedPut(name, ret);
                Object obj = ret;
            }
        }
        return blockingQueue;
    }

    public synchronized <E> BlockingQueue<E> createStack(String name, Serializer<E> serializer, boolean useLocks) {
        Stack<E> ret;
        checkNameNotExists(name);
        ret = new Stack(this.engine, (Serializer) catPut(name + ".serializer", serializer, getDefaultSerializer()), ((Long) catPut(name + ".headRecid", Long.valueOf(this.engine.put(Long.valueOf(this.engine.put(Node.EMPTY, new NodeSerializer(serializer))), Serializer.LONG)))).longValue(), ((Boolean) catPut(name + ".useLocks", Boolean.valueOf(useLocks))).booleanValue());
        this.catalog.put(name + ".type", "Stack");
        namedPut(name, ret);
        return ret;
    }

    public synchronized <E> BlockingQueue<E> getCircularQueue(String name) {
        BlockingQueue<E> blockingQueue;
        checkNotClosed();
        BlockingQueue<E> ret = (BlockingQueue) getFromWeakCollection(name);
        if (ret != null) {
            blockingQueue = ret;
        } else {
            String type = (String) catGet(name + ".type", null);
            if (type == null) {
                checkShouldCreate(name);
                if (this.engine.isReadOnly()) {
                    Engine e = new StoreHeap();
                    new DB(e).getCircularQueue(DeviceType.IPAD);
                    blockingQueue = (BlockingQueue) namedPut(name, new DB(new ReadOnlyEngine(e)).getCircularQueue(DeviceType.IPAD));
                } else {
                    blockingQueue = createCircularQueue(name, null, PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID);
                }
            } else {
                checkType(type, "CircularQueue");
                ret = new CircularQueue(this.engine, (Serializer) catGet(name + ".serializer", getDefaultSerializer()), ((Long) catGet(name + ".headRecid")).longValue(), ((Long) catGet(name + ".headInsertRecid")).longValue(), ((Long) catGet(name + ".size")).longValue());
                namedPut(name, ret);
                blockingQueue = ret;
            }
        }
        return blockingQueue;
    }

    public synchronized <E> BlockingQueue<E> createCircularQueue(String name, Serializer<E> serializer, long size) {
        CircularQueue<E> ret;
        checkNameNotExists(name);
        if (serializer == null) {
            serializer = getDefaultSerializer();
        }
        long prevRecid = 0;
        long firstRecid = 0;
        Serializer<Node<E>> nodeSerializer = new NodeSerializer(serializer);
        for (long i = 0; i < size; i++) {
            prevRecid = this.engine.put(new Node(prevRecid, null), nodeSerializer);
            if (firstRecid == 0) {
                firstRecid = prevRecid;
            }
        }
        this.engine.update(firstRecid, new Node(prevRecid, null), nodeSerializer);
        ret = new CircularQueue(this.engine, (Serializer) catPut(name + ".serializer", serializer), ((Long) catPut(name + ".headRecid", Long.valueOf(this.engine.put(Long.valueOf(prevRecid), Serializer.LONG)))).longValue(), ((Long) catPut(name + ".headInsertRecid", Long.valueOf(this.engine.put(Long.valueOf(prevRecid), Serializer.LONG)))).longValue(), ((Long) catPut(name + ".size", Long.valueOf(size))).longValue());
        this.catalog.put(name + ".type", "CircularQueue");
        namedPut(name, ret);
        return ret;
    }

    public synchronized Long createAtomicLong(String name, long initValue) {
        Long ret;
        checkNameNotExists(name);
        ret = new Long(this.engine, ((Long) catPut(name + ".recid", Long.valueOf(this.engine.put(Long.valueOf(initValue), Serializer.LONG)))).longValue());
        this.catalog.put(name + ".type", "AtomicLong");
        namedPut(name, ret);
        return ret;
    }

    public synchronized Long getAtomicLong(String name) {
        Long longR;
        checkNotClosed();
        Long ret = (Long) getFromWeakCollection(name);
        if (ret != null) {
            longR = ret;
        } else {
            String type = (String) catGet(name + ".type", null);
            if (type == null) {
                checkShouldCreate(name);
                if (this.engine.isReadOnly()) {
                    Engine e = new StoreHeap();
                    new DB(e).getAtomicLong(DeviceType.IPAD);
                    longR = (Long) namedPut(name, new DB(new ReadOnlyEngine(e)).getAtomicLong(DeviceType.IPAD));
                } else {
                    longR = createAtomicLong(name, 0);
                }
            } else {
                checkType(type, "AtomicLong");
                ret = new Long(this.engine, ((Long) catGet(name + ".recid")).longValue());
                namedPut(name, ret);
                longR = ret;
            }
        }
        return longR;
    }

    public synchronized Integer createAtomicInteger(String name, int initValue) {
        Integer ret;
        checkNameNotExists(name);
        ret = new Integer(this.engine, ((Long) catPut(name + ".recid", Long.valueOf(this.engine.put(Integer.valueOf(initValue), Serializer.INTEGER)))).longValue());
        this.catalog.put(name + ".type", "AtomicInteger");
        namedPut(name, ret);
        return ret;
    }

    public synchronized Integer getAtomicInteger(String name) {
        Integer integer;
        checkNotClosed();
        Integer ret = (Integer) getFromWeakCollection(name);
        if (ret != null) {
            integer = ret;
        } else {
            String type = (String) catGet(name + ".type", null);
            if (type == null) {
                checkShouldCreate(name);
                if (this.engine.isReadOnly()) {
                    Engine e = new StoreHeap();
                    new DB(e).getAtomicInteger(DeviceType.IPAD);
                    integer = (Integer) namedPut(name, new DB(new ReadOnlyEngine(e)).getAtomicInteger(DeviceType.IPAD));
                } else {
                    integer = createAtomicInteger(name, 0);
                }
            } else {
                checkType(type, "AtomicInteger");
                ret = new Integer(this.engine, ((Long) catGet(name + ".recid")).longValue());
                namedPut(name, ret);
                integer = ret;
            }
        }
        return integer;
    }

    public synchronized Boolean createAtomicBoolean(String name, boolean initValue) {
        Boolean ret;
        checkNameNotExists(name);
        ret = new Boolean(this.engine, ((Long) catPut(name + ".recid", Long.valueOf(this.engine.put(Boolean.valueOf(initValue), Serializer.BOOLEAN)))).longValue());
        this.catalog.put(name + ".type", "AtomicBoolean");
        namedPut(name, ret);
        return ret;
    }

    public synchronized Boolean getAtomicBoolean(String name) {
        Boolean booleanR;
        checkNotClosed();
        Boolean ret = (Boolean) getFromWeakCollection(name);
        if (ret != null) {
            booleanR = ret;
        } else {
            String type = (String) catGet(name + ".type", null);
            if (type == null) {
                checkShouldCreate(name);
                if (this.engine.isReadOnly()) {
                    Engine e = new StoreHeap();
                    new DB(e).getAtomicBoolean(DeviceType.IPAD);
                    booleanR = (Boolean) namedPut(name, new DB(new ReadOnlyEngine(e)).getAtomicBoolean(DeviceType.IPAD));
                } else {
                    booleanR = createAtomicBoolean(name, false);
                }
            } else {
                checkType(type, "AtomicBoolean");
                ret = new Boolean(this.engine, ((Long) catGet(name + ".recid")).longValue());
                namedPut(name, ret);
                booleanR = ret;
            }
        }
        return booleanR;
    }

    public void checkShouldCreate(String name) {
        if (this.strictDBGet) {
            throw new NoSuchElementException("No record with this name was found: " + name);
        }
    }

    public synchronized String createAtomicString(String name, String initValue) {
        String ret;
        checkNameNotExists(name);
        if (initValue == null) {
            throw new IllegalArgumentException("initValue may not be null");
        }
        ret = new String(this.engine, ((Long) catPut(name + ".recid", Long.valueOf(this.engine.put(initValue, Serializer.STRING_NOSIZE)))).longValue());
        this.catalog.put(name + ".type", "AtomicString");
        namedPut(name, ret);
        return ret;
    }

    public synchronized String getAtomicString(String name) {
        String string;
        checkNotClosed();
        String ret = (String) getFromWeakCollection(name);
        if (ret != null) {
            string = ret;
        } else {
            String type = (String) catGet(name + ".type", null);
            if (type == null) {
                checkShouldCreate(name);
                if (this.engine.isReadOnly()) {
                    Engine e = new StoreHeap();
                    new DB(e).getAtomicString(DeviceType.IPAD);
                    string = (String) namedPut(name, new DB(new ReadOnlyEngine(e)).getAtomicString(DeviceType.IPAD));
                } else {
                    string = createAtomicString(name, Table.STRING_DEFAULT_VALUE);
                }
            } else {
                checkType(type, "AtomicString");
                ret = new String(this.engine, ((Long) catGet(name + ".recid")).longValue());
                namedPut(name, ret);
                string = ret;
            }
        }
        return string;
    }

    public synchronized <E> Var<E> createAtomicVar(String name, E initValue, Serializer<E> serializer) {
        Var ret;
        checkNameNotExists(name);
        if (serializer == null) {
            serializer = getDefaultSerializer();
        }
        ret = new Var(this.engine, ((Long) catPut(name + ".recid", Long.valueOf(this.engine.put(initValue, serializer)))).longValue(), (Serializer) catPut(name + ".serializer", serializer));
        this.catalog.put(name + ".type", "AtomicVar");
        namedPut(name, ret);
        return ret;
    }

    public synchronized <E> Var<E> getAtomicVar(String name) {
        Var<E> var;
        checkNotClosed();
        Var<E> ret = (Var) getFromWeakCollection(name);
        if (ret != null) {
            var = ret;
        } else {
            String type = (String) catGet(name + ".type", null);
            if (type == null) {
                checkShouldCreate(name);
                if (this.engine.isReadOnly()) {
                    Engine e = new StoreHeap();
                    new DB(e).getAtomicVar(DeviceType.IPAD);
                    var = (Var) namedPut(name, new DB(new ReadOnlyEngine(e)).getAtomicVar(DeviceType.IPAD));
                } else {
                    var = createAtomicVar(name, null, getDefaultSerializer());
                }
            } else {
                checkType(type, "AtomicVar");
                ret = new Var(this.engine, ((Long) catGet(name + ".recid")).longValue(), (Serializer) catGet(name + ".serializer"));
                namedPut(name, ret);
                var = ret;
            }
        }
        return var;
    }

    public synchronized <E> E get(String name) {
        E e;
        String type = (String) catGet(name + ".type");
        if (type == null) {
            e = null;
        } else if ("HashMap".equals(type)) {
            e = getHashMap(name);
        } else if ("HashSet".equals(type)) {
            e = getHashSet(name);
        } else if ("TreeMap".equals(type)) {
            e = getTreeMap(name);
        } else if ("TreeSet".equals(type)) {
            e = getTreeSet(name);
        } else if ("AtomicBoolean".equals(type)) {
            e = getAtomicBoolean(name);
        } else if ("AtomicInteger".equals(type)) {
            e = getAtomicInteger(name);
        } else if ("AtomicLong".equals(type)) {
            e = getAtomicLong(name);
        } else if ("AtomicString".equals(type)) {
            e = getAtomicString(name);
        } else if ("AtomicVar".equals(type)) {
            e = getAtomicVar(name);
        } else if ("Queue".equals(type)) {
            e = getQueue(name);
        } else if ("Stack".equals(type)) {
            e = getStack(name);
        } else if ("CircularQueue".equals(type)) {
            e = getCircularQueue(name);
        } else {
            throw new AssertionError("Unknown type: " + name);
        }
        return e;
    }

    public synchronized boolean exists(String name) {
        return catGet(new StringBuilder().append(name).append(".type").toString()) != null;
    }

    public synchronized void delete(String name) {
        Object r = get(name);
        if (r instanceof Boolean) {
            this.engine.delete(((Boolean) r).recid, Serializer.BOOLEAN);
        } else if (r instanceof Integer) {
            this.engine.delete(((Integer) r).recid, Serializer.INTEGER);
        } else if (r instanceof Long) {
            this.engine.delete(((Long) r).recid, Serializer.LONG);
        } else if (r instanceof String) {
            this.engine.delete(((String) r).recid, Serializer.STRING_NOSIZE);
        } else if (r instanceof Var) {
            this.engine.delete(((Var) r).recid, ((Var) r).serializer);
        } else if (r instanceof java.util.Queue) {
            while (((java.util.Queue) r).poll() != null) {
            }
        } else if ((r instanceof HTreeMap) || (r instanceof KeySet)) {
            HTreeMap m;
            if (r instanceof HTreeMap) {
                HTreeMap hTreeMap = (HTreeMap) r;
            } else {
                m = ((KeySet) r).parent();
            }
            m.clear();
            for (long segmentRecid : m.segmentRecids) {
                this.engine.delete(segmentRecid, HTreeMap.DIR_SERIALIZER);
            }
        } else if ((r instanceof BTreeMap) || (r instanceof KeySet)) {
            BTreeMap m2;
            if (r instanceof BTreeMap) {
                BTreeMap bTreeMap = (BTreeMap) r;
            } else {
                m2 = (BTreeMap) ((KeySet) r).m;
            }
            m2.clear();
            if (m2.counter != null) {
                this.engine.delete(m2.counter.recid, Serializer.LONG);
            }
        }
        for (String n : this.catalog.keySet()) {
            if (n.startsWith(name)) {
                String suffix = n.substring(name.length());
                if (suffix.charAt(0) == Char.DOT && suffix.length() > 1 && !suffix.substring(1).contains(".")) {
                    this.catalog.remove(n);
                }
            }
        }
        this.namesInstanciated.remove(name);
        this.namesLookup.remove(new IdentityWrapper(r));
    }

    public synchronized Map<String, Object> getAll() {
        TreeMap<String, Object> ret;
        ret = new TreeMap();
        for (String name : this.catalog.keySet()) {
            String name2;
            if (name2.endsWith(".type")) {
                name2 = name2.substring(0, name2.length() - 5);
                ret.put(name2, get(name2));
            }
        }
        return Collections.unmodifiableMap(ret);
    }

    public synchronized void rename(String oldName, String newName) {
        if (!oldName.equals(newName)) {
            Map<String, Object> sub = this.catalog.tailMap(oldName);
            List<String> toRemove = new ArrayList();
            for (String param : sub.keySet()) {
                if (!param.startsWith(oldName)) {
                    break;
                }
                this.catalog.put(newName + param.substring(oldName.length()), this.catalog.get(param));
                toRemove.add(param);
            }
            if (toRemove.isEmpty()) {
                throw new NoSuchElementException("Could not rename, name does not exist: " + oldName);
            }
            WeakReference old = (WeakReference) this.namesInstanciated.remove(oldName);
            if (old != null) {
                Object old2 = old.get();
                if (old2 != null) {
                    this.namesLookup.remove(new IdentityWrapper(old2));
                    namedPut(newName, old2);
                }
            }
            for (String param2 : toRemove) {
                this.catalog.remove(param2);
            }
        }
    }

    public void checkNameNotExists(String name) {
        if (this.catalog.get(name + ".type") != null) {
            throw new IllegalArgumentException("Name already used: " + name);
        }
    }

    public synchronized void close() {
        if (this.engine != null) {
            this.engine.close();
            this.engine = EngineWrapper.CLOSED;
            this.namesInstanciated = Collections.unmodifiableMap(new HashMap());
            this.namesLookup = Collections.unmodifiableMap(new HashMap());
        }
    }

    public Object getFromWeakCollection(String name) {
        WeakReference<?> r = (WeakReference) this.namesInstanciated.get(name);
        if (r == null) {
            return null;
        }
        Object o = r.get();
        if (o != null) {
            return o;
        }
        this.namesInstanciated.remove(name);
        return o;
    }

    public void checkNotClosed() {
        if (this.engine == null) {
            throw new IllegalAccessError("DB was already closed");
        }
    }

    public synchronized boolean isClosed() {
        boolean z;
        z = this.engine == null || this.engine.isClosed();
        return z;
    }

    public synchronized void commit() {
        checkNotClosed();
        this.engine.commit();
    }

    public synchronized void rollback() {
        checkNotClosed();
        this.engine.rollback();
    }

    public synchronized void compact() {
        this.engine.compact();
    }

    public synchronized DB snapshot() {
        return new DB(TxEngine.createSnapshotFor(this.engine));
    }

    public Serializer getDefaultSerializer() {
        return this.engine.getSerializerPojo();
    }

    public Engine getEngine() {
        return this.engine;
    }

    public void checkType(String type, String expected) {
        if (!expected.equals(type)) {
            throw new IllegalArgumentException("Wrong type: " + type);
        }
    }
}
