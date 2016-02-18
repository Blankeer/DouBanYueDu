package se.emilsjolander.stickylistheaders;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import java.util.LinkedList;
import java.util.List;

class AdapterWrapper extends BaseAdapter implements StickyListHeadersAdapter {
    private final Context mContext;
    private DataSetObserver mDataSetObserver;
    StickyListHeadersAdapter mDelegate;
    private Drawable mDivider;
    private int mDividerHeight;
    private final List<View> mHeaderCache;
    private OnHeaderClickListener mOnHeaderClickListener;

    /* renamed from: se.emilsjolander.stickylistheaders.AdapterWrapper.2 */
    class AnonymousClass2 implements OnClickListener {
        final /* synthetic */ int val$position;

        AnonymousClass2(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            if (AdapterWrapper.this.mOnHeaderClickListener != null) {
                AdapterWrapper.this.mOnHeaderClickListener.onHeaderClick(v, this.val$position, AdapterWrapper.this.mDelegate.getHeaderId(this.val$position));
            }
        }
    }

    interface OnHeaderClickListener {
        void onHeaderClick(View view, int i, long j);
    }

    AdapterWrapper(Context context, StickyListHeadersAdapter delegate) {
        this.mHeaderCache = new LinkedList();
        this.mDataSetObserver = new DataSetObserver() {
            public void onInvalidated() {
                AdapterWrapper.this.mHeaderCache.clear();
                super.notifyDataSetInvalidated();
            }

            public void onChanged() {
                super.notifyDataSetChanged();
            }
        };
        this.mContext = context;
        this.mDelegate = delegate;
        delegate.registerDataSetObserver(this.mDataSetObserver);
    }

    void setDivider(Drawable divider, int dividerHeight) {
        this.mDivider = divider;
        this.mDividerHeight = dividerHeight;
        notifyDataSetChanged();
    }

    public boolean areAllItemsEnabled() {
        return this.mDelegate.areAllItemsEnabled();
    }

    public boolean isEnabled(int position) {
        return this.mDelegate.isEnabled(position);
    }

    public int getCount() {
        return this.mDelegate.getCount();
    }

    public Object getItem(int position) {
        return this.mDelegate.getItem(position);
    }

    public long getItemId(int position) {
        return this.mDelegate.getItemId(position);
    }

    public boolean hasStableIds() {
        return this.mDelegate.hasStableIds();
    }

    public int getItemViewType(int position) {
        return this.mDelegate.getItemViewType(position);
    }

    public int getViewTypeCount() {
        return this.mDelegate.getViewTypeCount();
    }

    public boolean isEmpty() {
        return this.mDelegate.isEmpty();
    }

    private void recycleHeaderIfExists(WrapperView wv) {
        View header = wv.mHeader;
        if (header != null) {
            header.setVisibility(0);
            this.mHeaderCache.add(header);
        }
    }

    private View configureHeader(WrapperView wv, int position) {
        View header = this.mDelegate.getHeaderView(position, wv.mHeader == null ? popHeader() : wv.mHeader, wv);
        if (header == null) {
            throw new NullPointerException("Header view must not be null.");
        }
        header.setClickable(true);
        header.setOnClickListener(new AnonymousClass2(position));
        return header;
    }

    private View popHeader() {
        if (this.mHeaderCache.size() > 0) {
            return (View) this.mHeaderCache.remove(0);
        }
        return null;
    }

    private boolean previousPositionHasSameHeader(int position) {
        return position != 0 && this.mDelegate.getHeaderId(position) == this.mDelegate.getHeaderId(position - 1);
    }

    public WrapperView getView(int position, View convertView, ViewGroup parent) {
        WrapperView wv = convertView == null ? new WrapperView(this.mContext) : (WrapperView) convertView;
        View item = this.mDelegate.getView(position, wv.mItem, parent);
        View header = null;
        if (previousPositionHasSameHeader(position)) {
            recycleHeaderIfExists(wv);
        } else {
            header = configureHeader(wv, position);
        }
        if ((item instanceof Checkable) && !(wv instanceof CheckableWrapperView)) {
            wv = new CheckableWrapperView(this.mContext);
        } else if (!(item instanceof Checkable) && (wv instanceof CheckableWrapperView)) {
            wv = new WrapperView(this.mContext);
        }
        wv.update(item, header, this.mDivider, this.mDividerHeight);
        return wv;
    }

    public void setOnHeaderClickListener(OnHeaderClickListener onHeaderClickListener) {
        this.mOnHeaderClickListener = onHeaderClickListener;
    }

    public boolean equals(Object o) {
        return this.mDelegate.equals(o);
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return ((BaseAdapter) this.mDelegate).getDropDownView(position, convertView, parent);
    }

    public int hashCode() {
        return this.mDelegate.hashCode();
    }

    public void notifyDataSetChanged() {
        ((BaseAdapter) this.mDelegate).notifyDataSetChanged();
    }

    public void notifyDataSetInvalidated() {
        ((BaseAdapter) this.mDelegate).notifyDataSetInvalidated();
    }

    public String toString() {
        return this.mDelegate.toString();
    }

    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        return this.mDelegate.getHeaderView(position, convertView, parent);
    }

    public long getHeaderId(int position) {
        return this.mDelegate.getHeaderId(position);
    }
}
