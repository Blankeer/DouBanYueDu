package com.mcxiaoke.next.ui.widget;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import android.widget.Filterable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public abstract class ArrayAdapterCompat<T> extends BaseAdapter implements Filterable {
    private Context mContext;
    protected Filter mFilter;
    protected final Object mLock;
    private boolean mNotifyOnChange;
    protected List<T> mObjects;
    protected List<T> mOriginalValues;

    private class ArrayFilter extends Filter {
        private ArrayFilter() {
        }

        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (ArrayAdapterCompat.this.mOriginalValues == null) {
                synchronized (ArrayAdapterCompat.this.mLock) {
                    ArrayAdapterCompat.this.mOriginalValues = new ArrayList(ArrayAdapterCompat.this.mObjects);
                }
            }
            if (prefix == null || prefix.length() == 0) {
                ArrayList<T> list;
                synchronized (ArrayAdapterCompat.this.mLock) {
                    list = new ArrayList(ArrayAdapterCompat.this.mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                ArrayList<T> values;
                String prefixString = ArrayAdapterCompat.toLowerCase(prefix.toString());
                synchronized (ArrayAdapterCompat.this.mLock) {
                    values = new ArrayList(ArrayAdapterCompat.this.mOriginalValues);
                }
                ArrayList<T> newValues = new ArrayList();
                Iterator it = values.iterator();
                while (it.hasNext()) {
                    T value = it.next();
                    String valueText = ArrayAdapterCompat.toLowerCase(value.toString());
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        String[] words = valueText.split(" ");
                        int wordCount = words.length;
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayAdapterCompat.this.mObjects = (List) results.values;
            if (results.count > 0) {
                ArrayAdapterCompat.this.notifyDataSetChanged();
            } else {
                ArrayAdapterCompat.this.notifyDataSetInvalidated();
            }
        }
    }

    public ArrayAdapterCompat(Context context) {
        this.mLock = new Object();
        this.mNotifyOnChange = true;
        init(context, new ArrayList());
    }

    public ArrayAdapterCompat(Context context, T[] objects) {
        this.mLock = new Object();
        this.mNotifyOnChange = true;
        init(context, Arrays.asList(objects));
    }

    public ArrayAdapterCompat(Context context, List<T> objects) {
        this.mLock = new Object();
        this.mNotifyOnChange = true;
        init(context, objects);
    }

    public void add(T object) {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                this.mOriginalValues.add(object);
            } else {
                this.mObjects.add(object);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void add(int index, T object) {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                this.mOriginalValues.add(index, object);
            } else {
                this.mObjects.add(index, object);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void set(int index, T object) {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                this.mOriginalValues.set(index, object);
            } else {
                this.mObjects.set(index, object);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void setAll(Collection<? extends T> collection) {
        clear();
        addAll((Collection) collection);
    }

    public void setAll(T... items) {
        clear();
        addAll((Object[]) items);
    }

    public void addAll(Collection<? extends T> collection) {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                this.mOriginalValues.addAll(collection);
            } else {
                this.mObjects.addAll(collection);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void addAll(T... items) {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                Collections.addAll(this.mOriginalValues, items);
            } else {
                Collections.addAll(this.mObjects, items);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void addAll(int index, Collection<? extends T> collection) {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                this.mOriginalValues.addAll(index, collection);
            } else {
                this.mObjects.addAll(index, collection);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void addAll(int index, T... items) {
        List<T> collection = Arrays.asList(items);
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                this.mOriginalValues.addAll(index, collection);
            } else {
                this.mObjects.addAll(index, collection);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public List<T> getAllItems() {
        if (this.mOriginalValues != null) {
            return this.mOriginalValues;
        }
        return this.mObjects;
    }

    public void insert(T object, int index) {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                this.mOriginalValues.add(index, object);
            } else {
                this.mObjects.add(index, object);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public boolean contains(T object) {
        boolean contains;
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                contains = this.mOriginalValues.contains(object);
            } else {
                contains = this.mObjects.contains(object);
            }
        }
        return contains;
    }

    public int indexOf(T object) {
        int indexOf;
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                indexOf = this.mOriginalValues.indexOf(object);
            } else {
                indexOf = this.mObjects.indexOf(object);
            }
        }
        return indexOf;
    }

    public void remove(T object) {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                this.mOriginalValues.remove(object);
            } else {
                this.mObjects.remove(object);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void removeAt(int index) {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                this.mOriginalValues.remove(index);
            } else {
                this.mObjects.remove(index);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public boolean removeAll(Collection<?> collection) {
        boolean result = false;
        synchronized (this.mLock) {
            Iterator<?> it;
            if (this.mOriginalValues != null) {
                it = this.mOriginalValues.iterator();
            } else {
                it = this.mObjects.iterator();
            }
            while (it.hasNext()) {
                if (collection.contains(it.next())) {
                    it.remove();
                    result = true;
                }
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
        return result;
    }

    public void clear() {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                this.mOriginalValues.clear();
            } else {
                this.mObjects.clear();
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void sort(Comparator<? super T> comparator) {
        synchronized (this.mLock) {
            if (this.mOriginalValues != null) {
                Collections.sort(this.mOriginalValues, comparator);
            } else {
                Collections.sort(this.mObjects, comparator);
            }
        }
        if (this.mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.mNotifyOnChange = true;
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        this.mNotifyOnChange = notifyOnChange;
    }

    private void init(Context context, List<T> objects) {
        this.mContext = context;
        this.mObjects = objects;
    }

    public Context getContext() {
        return this.mContext;
    }

    public int getCount() {
        return this.mObjects.size();
    }

    public T getItem(int position) {
        return this.mObjects.get(position);
    }

    public int getPosition(T item) {
        return this.mObjects.indexOf(item);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public Filter getFilter() {
        if (this.mFilter == null) {
            this.mFilter = new ArrayFilter();
        }
        return this.mFilter;
    }

    public void setFilter(Filter filter) {
        this.mFilter = filter;
    }

    static String toLowerCase(String text) {
        return text == null ? null : text.toLowerCase(Locale.US);
    }
}
