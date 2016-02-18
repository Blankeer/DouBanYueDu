package se.emilsjolander.stickylistheaders;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.List;

class ExpandableStickyListHeadersAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    List<Long> mCollapseHeaderIds;
    DistinctMultiHashMap<Integer, View> mHeaderIdToViewMap;
    private final StickyListHeadersAdapter mInnerAdapter;
    DualHashMap<View, Long> mViewToItemIdMap;

    ExpandableStickyListHeadersAdapter(StickyListHeadersAdapter innerAdapter) {
        this.mViewToItemIdMap = new DualHashMap();
        this.mHeaderIdToViewMap = new DistinctMultiHashMap();
        this.mCollapseHeaderIds = new ArrayList();
        this.mInnerAdapter = innerAdapter;
    }

    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        return this.mInnerAdapter.getHeaderView(position, convertView, parent);
    }

    public long getHeaderId(int position) {
        return this.mInnerAdapter.getHeaderId(position);
    }

    public boolean areAllItemsEnabled() {
        return this.mInnerAdapter.areAllItemsEnabled();
    }

    public boolean isEnabled(int i) {
        return this.mInnerAdapter.isEnabled(i);
    }

    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        this.mInnerAdapter.registerDataSetObserver(dataSetObserver);
    }

    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        this.mInnerAdapter.unregisterDataSetObserver(dataSetObserver);
    }

    public int getCount() {
        return this.mInnerAdapter.getCount();
    }

    public Object getItem(int i) {
        return this.mInnerAdapter.getItem(i);
    }

    public long getItemId(int i) {
        return this.mInnerAdapter.getItemId(i);
    }

    public boolean hasStableIds() {
        return this.mInnerAdapter.hasStableIds();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView = this.mInnerAdapter.getView(i, view, viewGroup);
        this.mViewToItemIdMap.put(convertView, Long.valueOf(getItemId(i)));
        this.mHeaderIdToViewMap.add(Integer.valueOf((int) getHeaderId(i)), convertView);
        if (this.mCollapseHeaderIds.contains(Long.valueOf(getHeaderId(i)))) {
            convertView.setVisibility(8);
        } else {
            convertView.setVisibility(0);
        }
        return convertView;
    }

    public int getItemViewType(int i) {
        return this.mInnerAdapter.getItemViewType(i);
    }

    public int getViewTypeCount() {
        return this.mInnerAdapter.getViewTypeCount();
    }

    public boolean isEmpty() {
        return this.mInnerAdapter.isEmpty();
    }

    public List<View> getItemViewsByHeaderId(long headerId) {
        return this.mHeaderIdToViewMap.get(Integer.valueOf((int) headerId));
    }

    public boolean isHeaderCollapsed(long headerId) {
        return this.mCollapseHeaderIds.contains(Long.valueOf(headerId));
    }

    public void expand(long headerId) {
        if (isHeaderCollapsed(headerId)) {
            this.mCollapseHeaderIds.remove(Long.valueOf(headerId));
        }
    }

    public void collapse(long headerId) {
        if (!isHeaderCollapsed(headerId)) {
            this.mCollapseHeaderIds.add(Long.valueOf(headerId));
        }
    }

    public View findViewByItemId(long itemId) {
        return (View) this.mViewToItemIdMap.getKey(Long.valueOf(itemId));
    }

    public long findItemIdByView(View view) {
        return ((Long) this.mViewToItemIdMap.get(view)).longValue();
    }
}
