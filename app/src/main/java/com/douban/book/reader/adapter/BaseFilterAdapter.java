package com.douban.book.reader.adapter;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import com.mcxiaoke.next.ui.widget.ArrayAdapterCompat;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class BaseFilterAdapter<T> extends ArrayAdapterCompat<T> {
    private static final String TAG;
    private BaseFilter mFilter;

    private class BaseFilter extends Filter {
        private BaseFilter() {
        }

        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (BaseFilterAdapter.this.mOriginalValues == null) {
                synchronized (BaseFilterAdapter.this.mLock) {
                    BaseFilterAdapter.this.mOriginalValues = new ArrayList(BaseFilterAdapter.this.mObjects);
                }
            }
            if (constraint == null || constraint.length() == 0) {
                ArrayList<T> list;
                synchronized (BaseFilterAdapter.this.mLock) {
                    list = new ArrayList(BaseFilterAdapter.this.mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                ArrayList<T> values;
                synchronized (BaseFilterAdapter.this.mLock) {
                    values = new ArrayList(BaseFilterAdapter.this.mOriginalValues);
                }
                ArrayList<T> newValues = new ArrayList();
                Iterator it = values.iterator();
                while (it.hasNext()) {
                    T value = it.next();
                    if (BaseFilterAdapter.this.filter(constraint, value)) {
                        newValues.add(value);
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        protected void publishResults(CharSequence constraint, FilterResults results) {
            BaseFilterAdapter.this.mObjects = (List) results.values;
            if (results.count > 0) {
                BaseFilterAdapter.this.notifyDataSetChanged();
            } else {
                BaseFilterAdapter.this.notifyDataSetInvalidated();
            }
        }
    }

    public abstract boolean filter(CharSequence charSequence, T t);

    static {
        TAG = BaseFilterAdapter.class.getSimpleName();
    }

    public BaseFilterAdapter(Context context) {
        super(context);
    }

    public Filter getFilter() {
        if (this.mFilter == null) {
            this.mFilter = new BaseFilter();
        }
        return this.mFilter;
    }

    public void add(T object) {
        super.add(object);
        getFilter().filter(Table.STRING_DEFAULT_VALUE);
    }

    public void clear() {
        super.clear();
        getFilter().filter(Table.STRING_DEFAULT_VALUE);
    }

    public void remove(T object) {
        super.remove(object);
        getFilter().filter(Table.STRING_DEFAULT_VALUE);
    }

    public boolean hasStableIds() {
        return true;
    }
}
