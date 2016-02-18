package se.emilsjolander.stickylistheaders;

import java.util.HashMap;

class DualHashMap<TKey, TValue> {
    HashMap<TKey, TValue> mKeyToValue;
    HashMap<TValue, TKey> mValueToKey;

    DualHashMap() {
        this.mKeyToValue = new HashMap();
        this.mValueToKey = new HashMap();
    }

    public void put(TKey t1, TValue t2) {
        remove(t1);
        removeByValue(t2);
        this.mKeyToValue.put(t1, t2);
        this.mValueToKey.put(t2, t1);
    }

    public TKey getKey(TValue value) {
        return this.mValueToKey.get(value);
    }

    public TValue get(TKey key) {
        return this.mKeyToValue.get(key);
    }

    public void remove(TKey key) {
        if (get(key) != null) {
            this.mValueToKey.remove(get(key));
        }
        this.mKeyToValue.remove(key);
    }

    public void removeByValue(TValue value) {
        if (getKey(value) != null) {
            this.mKeyToValue.remove(getKey(value));
        }
        this.mValueToKey.remove(value);
    }
}
