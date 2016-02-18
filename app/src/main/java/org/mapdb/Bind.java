package org.mapdb;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import org.mapdb.Atomic.Long;
import org.mapdb.Fun.Function2;
import org.mapdb.Fun.Tuple2;

public final class Bind {

    public interface MapListener<K, V> {
        void update(K k, V v, V v2);
    }

    public interface MapWithModificationListener<K, V> extends Map<K, V> {
        void modificationListenerAdd(MapListener<K, V> mapListener);

        void modificationListenerRemove(MapListener<K, V> mapListener);

        long sizeLong();
    }

    /* renamed from: org.mapdb.Bind.1 */
    static class AnonymousClass1 implements MapListener<K, V> {
        final /* synthetic */ Long val$sizeCounter;

        AnonymousClass1(Long longR) {
            this.val$sizeCounter = longR;
        }

        public void update(K k, V oldVal, V newVal) {
            if (oldVal == null && newVal != null) {
                this.val$sizeCounter.incrementAndGet();
            } else if (oldVal != null && newVal == null) {
                this.val$sizeCounter.decrementAndGet();
            }
        }
    }

    /* renamed from: org.mapdb.Bind.2 */
    static class AnonymousClass2 implements MapListener<K, V> {
        final /* synthetic */ Function2 val$fun;
        final /* synthetic */ Map val$secondary;

        AnonymousClass2(Map map, Function2 function2) {
            this.val$secondary = map;
            this.val$fun = function2;
        }

        public void update(K key, V v, V newVal) {
            if (newVal == null) {
                this.val$secondary.remove(key);
            } else {
                this.val$secondary.put(key, this.val$fun.run(key, newVal));
            }
        }
    }

    /* renamed from: org.mapdb.Bind.3 */
    static class AnonymousClass3 implements MapListener<K, V> {
        final /* synthetic */ Function2 val$fun;
        final /* synthetic */ Set val$secondary;

        AnonymousClass3(Function2 function2, Set set) {
            this.val$fun = function2;
            this.val$secondary = set;
        }

        public void update(K key, V oldVal, V newVal) {
            Object[] v;
            if (newVal == null) {
                v = (Object[]) this.val$fun.run(key, oldVal);
                if (v != null) {
                    for (V2 v2 : v) {
                        this.val$secondary.remove(Fun.t2(key, v2));
                    }
                }
            } else if (oldVal == null) {
                v = (Object[]) this.val$fun.run(key, newVal);
                if (v != null) {
                    for (V2 v22 : v) {
                        this.val$secondary.add(Fun.t2(key, v22));
                    }
                }
            } else {
                Object[] oldv = (Object[]) this.val$fun.run(key, oldVal);
                Object[] newv = (Object[]) this.val$fun.run(key, newVal);
                if (oldv == null) {
                    if (newv != null) {
                        for (V2 v3 : newv) {
                            this.val$secondary.add(Fun.t2(key, v3));
                        }
                    }
                } else if (newv == null) {
                    for (V2 v32 : oldv) {
                        this.val$secondary.remove(Fun.t2(key, v32));
                    }
                } else {
                    Set<V2> hashes = new HashSet();
                    Collections.addAll(hashes, oldv);
                    for (V2 v322 : newv) {
                        if (!hashes.contains(v322)) {
                            this.val$secondary.add(Fun.t2(key, v322));
                        }
                    }
                    for (V2 v3222 : newv) {
                        hashes.remove(v3222);
                    }
                    for (V2 v32222 : hashes) {
                        this.val$secondary.remove(Fun.t2(key, v32222));
                    }
                }
            }
        }
    }

    /* renamed from: org.mapdb.Bind.4 */
    static class AnonymousClass4 implements MapListener<K, V> {
        final /* synthetic */ Function2 val$fun;
        final /* synthetic */ Set val$secondary;

        AnonymousClass4(Set set, Function2 function2) {
            this.val$secondary = set;
            this.val$fun = function2;
        }

        public void update(K key, V oldVal, V newVal) {
            if (newVal == null) {
                this.val$secondary.remove(Fun.t2(this.val$fun.run(key, oldVal), key));
            } else if (oldVal == null) {
                this.val$secondary.add(Fun.t2(this.val$fun.run(key, newVal), key));
            } else {
                K2 oldKey = this.val$fun.run(key, oldVal);
                K2 newKey = this.val$fun.run(key, newVal);
                if (oldKey != newKey && !oldKey.equals(newKey)) {
                    this.val$secondary.remove(Fun.t2(oldKey, key));
                    this.val$secondary.add(Fun.t2(newKey, key));
                }
            }
        }
    }

    /* renamed from: org.mapdb.Bind.5 */
    static class AnonymousClass5 implements MapListener<K, V> {
        final /* synthetic */ Function2 val$fun;
        final /* synthetic */ Map val$secondary;

        AnonymousClass5(Map map, Function2 function2) {
            this.val$secondary = map;
            this.val$fun = function2;
        }

        public void update(K key, V oldVal, V newVal) {
            if (newVal == null) {
                this.val$secondary.remove(this.val$fun.run(key, oldVal));
            } else if (oldVal == null) {
                this.val$secondary.put(this.val$fun.run(key, newVal), key);
            } else {
                K2 oldKey = this.val$fun.run(key, oldVal);
                K2 newKey = this.val$fun.run(key, newVal);
                if (oldKey != newKey && !oldKey.equals(newKey)) {
                    this.val$secondary.remove(oldKey);
                    this.val$secondary.put(newKey, key);
                }
            }
        }
    }

    /* renamed from: org.mapdb.Bind.6 */
    static class AnonymousClass6 implements MapListener<K, V> {
        final /* synthetic */ Function2 val$fun;
        final /* synthetic */ Set val$secondary;

        AnonymousClass6(Function2 function2, Set set) {
            this.val$fun = function2;
            this.val$secondary = set;
        }

        public void update(K key, V oldVal, V newVal) {
            Object[] k2;
            if (newVal == null) {
                k2 = (Object[]) this.val$fun.run(key, oldVal);
                if (k2 != null) {
                    for (K2 k22 : k2) {
                        this.val$secondary.remove(Fun.t2(k22, key));
                    }
                }
            } else if (oldVal == null) {
                k2 = (Object[]) this.val$fun.run(key, newVal);
                if (k2 != null) {
                    for (K2 k222 : k2) {
                        this.val$secondary.add(Fun.t2(k222, key));
                    }
                }
            } else {
                Object[] oldk = (Object[]) this.val$fun.run(key, oldVal);
                Object[] newk = (Object[]) this.val$fun.run(key, newVal);
                if (oldk == null) {
                    if (newk != null) {
                        for (K2 k2222 : newk) {
                            this.val$secondary.add(Fun.t2(k2222, key));
                        }
                    }
                } else if (newk == null) {
                    for (K2 k22222 : oldk) {
                        this.val$secondary.remove(Fun.t2(k22222, key));
                    }
                } else {
                    Set<K2> hashes = new HashSet();
                    Collections.addAll(hashes, oldk);
                    for (K2 k23 : newk) {
                        if (!hashes.contains(k23)) {
                            this.val$secondary.add(Fun.t2(k23, key));
                        }
                    }
                    for (K2 k232 : newk) {
                        hashes.remove(k232);
                    }
                    for (K2 k2322 : hashes) {
                        this.val$secondary.remove(Fun.t2(k2322, key));
                    }
                }
            }
        }
    }

    /* renamed from: org.mapdb.Bind.9 */
    static class AnonymousClass9 implements MapListener<K, V> {
        final /* synthetic */ Function2 val$entryToCategory;
        final /* synthetic */ ConcurrentMap val$histogram;

        AnonymousClass9(Function2 function2, ConcurrentMap concurrentMap) {
            this.val$entryToCategory = function2;
            this.val$histogram = concurrentMap;
        }

        public void update(K key, V oldVal, V newVal) {
            if (newVal == null) {
                incrementHistogram(this.val$entryToCategory.run(key, oldVal), -1);
            } else if (oldVal == null) {
                incrementHistogram(this.val$entryToCategory.run(key, newVal), 1);
            } else {
                C oldCat = this.val$entryToCategory.run(key, oldVal);
                C newCat = this.val$entryToCategory.run(key, newVal);
                if (oldCat != newCat && !oldCat.equals(newCat)) {
                    incrementHistogram(oldCat, -1);
                    incrementHistogram(oldCat, 1);
                }
            }
        }

        private void incrementHistogram(C category, long i) {
            while (true) {
                Long oldCount = (Long) this.val$histogram.get(category);
                if (oldCount != null) {
                    if (this.val$histogram.replace(category, oldCount, Long.valueOf(oldCount.longValue() + i))) {
                        return;
                    }
                } else if (this.val$histogram.putIfAbsent(category, Long.valueOf(i)) == null) {
                    return;
                }
            }
        }
    }

    private Bind() {
    }

    public static <K, V> void size(MapWithModificationListener<K, V> map, Long sizeCounter) {
        if (sizeCounter.get() == 0) {
            long size = map.sizeLong();
            if (sizeCounter.get() != size) {
                sizeCounter.set(size);
            }
        }
        map.modificationListenerAdd(new AnonymousClass1(sizeCounter));
    }

    public static <K, V, V2> void secondaryValue(MapWithModificationListener<K, V> map, Map<K, V2> secondary, Function2<V2, K, V> fun) {
        if (secondary.isEmpty()) {
            for (Entry<K, V> e : map.entrySet()) {
                secondary.put(e.getKey(), fun.run(e.getKey(), e.getValue()));
            }
        }
        map.modificationListenerAdd(new AnonymousClass2(secondary, fun));
    }

    public static <K, V, V2> void secondaryValues(MapWithModificationListener<K, V> map, Set<Tuple2<K, V2>> secondary, Function2<V2[], K, V> fun) {
        if (secondary.isEmpty()) {
            for (Entry<K, V> e : map.entrySet()) {
                Object[] v = (Object[]) fun.run(e.getKey(), e.getValue());
                if (v != null) {
                    for (V2 v2 : v) {
                        secondary.add(Fun.t2(e.getKey(), v2));
                    }
                }
            }
        }
        map.modificationListenerAdd(new AnonymousClass3(fun, secondary));
    }

    public static <K, V, K2> void secondaryKey(MapWithModificationListener<K, V> map, Set<Tuple2<K2, K>> secondary, Function2<K2, K, V> fun) {
        if (secondary.isEmpty()) {
            for (Entry<K, V> e : map.entrySet()) {
                secondary.add(Fun.t2(fun.run(e.getKey(), e.getValue()), e.getKey()));
            }
        }
        map.modificationListenerAdd(new AnonymousClass4(secondary, fun));
    }

    public static <K, V, K2> void secondaryKey(MapWithModificationListener<K, V> map, Map<K2, K> secondary, Function2<K2, K, V> fun) {
        if (secondary.isEmpty()) {
            for (Entry<K, V> e : map.entrySet()) {
                secondary.put(fun.run(e.getKey(), e.getValue()), e.getKey());
            }
        }
        map.modificationListenerAdd(new AnonymousClass5(secondary, fun));
    }

    public static <K, V, K2> void secondaryKeys(MapWithModificationListener<K, V> map, Set<Tuple2<K2, K>> secondary, Function2<K2[], K, V> fun) {
        if (secondary.isEmpty()) {
            for (Entry<K, V> e : map.entrySet()) {
                Object[] k2 = (Object[]) fun.run(e.getKey(), e.getValue());
                if (k2 != null) {
                    for (K2 k22 : k2) {
                        secondary.add(Fun.t2(k22, e.getKey()));
                    }
                }
            }
        }
        map.modificationListenerAdd(new AnonymousClass6(fun, secondary));
    }

    public static <K, V> void mapInverse(MapWithModificationListener<K, V> primary, Set<Tuple2<V, K>> inverse) {
        secondaryKey((MapWithModificationListener) primary, (Set) inverse, new Function2<V, K, V>() {
            public V run(K k, V value) {
                return value;
            }
        });
    }

    public static <K, V> void mapInverse(MapWithModificationListener<K, V> primary, Map<V, K> inverse) {
        secondaryKey((MapWithModificationListener) primary, (Map) inverse, new Function2<V, K, V>() {
            public V run(K k, V value) {
                return value;
            }
        });
    }

    public static <K, V, C> void histogram(MapWithModificationListener<K, V> primary, ConcurrentMap<C, Long> histogram, Function2<C, K, V> entryToCategory) {
        primary.modificationListenerAdd(new AnonymousClass9(entryToCategory, histogram));
    }
}
