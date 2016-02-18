package se.emilsjolander.stickylistheaders;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

class DistinctMultiHashMap<TKey, TItemValue> {
    private IDMapper<TKey, TItemValue> mIDMapper;
    LinkedHashMap<Object, List<TItemValue>> mKeyToValuesMap;
    LinkedHashMap<Object, TKey> mValueToKeyIndexer;

    interface IDMapper<TKey, TItemValue> {
        TKey keyIdToKey(Object obj);

        Object keyToKeyId(TKey tKey);

        TItemValue valueIdToValue(Object obj);

        Object valueToValueId(TItemValue tItemValue);
    }

    DistinctMultiHashMap() {
        this(new IDMapper<TKey, TItemValue>() {
            public Object keyToKeyId(TKey key) {
                return key;
            }

            public TKey keyIdToKey(Object keyId) {
                return keyId;
            }

            public Object valueToValueId(TItemValue value) {
                return value;
            }

            public TItemValue valueIdToValue(Object valueId) {
                return valueId;
            }
        });
    }

    DistinctMultiHashMap(IDMapper<TKey, TItemValue> idMapper) {
        this.mKeyToValuesMap = new LinkedHashMap();
        this.mValueToKeyIndexer = new LinkedHashMap();
        this.mIDMapper = idMapper;
    }

    public List<TItemValue> get(TKey key) {
        return (List) this.mKeyToValuesMap.get(this.mIDMapper.keyToKeyId(key));
    }

    public TKey getKey(TItemValue value) {
        return this.mValueToKeyIndexer.get(this.mIDMapper.valueToValueId(value));
    }

    public void add(TKey key, TItemValue value) {
        Object keyId = this.mIDMapper.keyToKeyId(key);
        if (this.mKeyToValuesMap.get(keyId) == null) {
            this.mKeyToValuesMap.put(keyId, new ArrayList());
        }
        TKey keyForValue = getKey(value);
        if (keyForValue != null) {
            ((List) this.mKeyToValuesMap.get(this.mIDMapper.keyToKeyId(keyForValue))).remove(value);
        }
        this.mValueToKeyIndexer.put(this.mIDMapper.valueToValueId(value), key);
        if (!containsValue((List) this.mKeyToValuesMap.get(this.mIDMapper.keyToKeyId(key)), value)) {
            ((List) this.mKeyToValuesMap.get(this.mIDMapper.keyToKeyId(key))).add(value);
        }
    }

    public void removeKey(TKey key) {
        if (this.mKeyToValuesMap.get(this.mIDMapper.keyToKeyId(key)) != null) {
            for (TItemValue value : (List) this.mKeyToValuesMap.get(this.mIDMapper.keyToKeyId(key))) {
                this.mValueToKeyIndexer.remove(this.mIDMapper.valueToValueId(value));
            }
            this.mKeyToValuesMap.remove(this.mIDMapper.keyToKeyId(key));
        }
    }

    public void removeValue(TItemValue value) {
        if (getKey(value) != null) {
            List<TItemValue> itemValues = (List) this.mKeyToValuesMap.get(this.mIDMapper.keyToKeyId(getKey(value)));
            if (itemValues != null) {
                itemValues.remove(value);
            }
        }
        this.mValueToKeyIndexer.remove(this.mIDMapper.valueToValueId(value));
    }

    public void clear() {
        this.mValueToKeyIndexer.clear();
        this.mKeyToValuesMap.clear();
    }

    public void clearValues() {
        for (Entry<Object, List<TItemValue>> entry : entrySet()) {
            if (entry.getValue() != null) {
                ((List) entry.getValue()).clear();
            }
        }
        this.mValueToKeyIndexer.clear();
    }

    public Set<Entry<Object, List<TItemValue>>> entrySet() {
        return this.mKeyToValuesMap.entrySet();
    }

    public Set<Entry<Object, TKey>> reverseEntrySet() {
        return this.mValueToKeyIndexer.entrySet();
    }

    public int size() {
        return this.mKeyToValuesMap.size();
    }

    public int valuesSize() {
        return this.mValueToKeyIndexer.size();
    }

    protected boolean containsValue(List<TItemValue> list, TItemValue value) {
        for (TItemValue itemValue : list) {
            if (this.mIDMapper.valueToValueId(itemValue).equals(this.mIDMapper.valueToValueId(value))) {
                return true;
            }
        }
        return false;
    }

    public TItemValue getValueByPosition(int position) {
        Object[] vauleIdArray = this.mValueToKeyIndexer.keySet().toArray();
        if (position > vauleIdArray.length) {
            throw new IndexOutOfBoundsException();
        }
        return this.mIDMapper.valueIdToValue(vauleIdArray[position]);
    }
}
