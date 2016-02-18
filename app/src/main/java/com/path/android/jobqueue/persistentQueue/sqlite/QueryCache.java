package com.path.android.jobqueue.persistentQueue.sqlite;

import com.douban.book.reader.entity.Annotation.Privacy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class QueryCache {
    private static final String KEY_EMPTY_WITHOUT_NETWORK = "wo_n";
    private static final String KEY_EMPTY_WITH_NETWORK = "w_n";
    private final Map<String, String> cache;
    private final StringBuilder reusedBuilder;

    public QueryCache() {
        this.reusedBuilder = new StringBuilder();
        this.cache = new HashMap();
    }

    public synchronized String get(boolean hasNetwork, Collection<String> excludeGroups) {
        return (String) this.cache.get(cacheKey(hasNetwork, excludeGroups));
    }

    public synchronized void set(String query, boolean hasNetwork, Collection<String> excludeGroups) {
        this.cache.put(cacheKey(hasNetwork, excludeGroups), query);
    }

    private String cacheKey(boolean hasNetwork, Collection<String> excludeGroups) {
        if (excludeGroups == null || excludeGroups.size() == 0) {
            return hasNetwork ? KEY_EMPTY_WITH_NETWORK : KEY_EMPTY_WITHOUT_NETWORK;
        } else {
            this.reusedBuilder.setLength(0);
            this.reusedBuilder.append(hasNetwork ? Privacy.PRIVATE : "Y");
            for (String group : excludeGroups) {
                this.reusedBuilder.append("-").append(group);
            }
            return this.reusedBuilder.toString();
        }
    }

    public synchronized void clear() {
        this.cache.clear();
    }
}
