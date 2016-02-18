package com.mcxiaoke.next.ui.list;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import com.mcxiaoke.next.ui.internal.AdapterWrapper;
import com.mcxiaoke.next.ui.list.ListViewExtend.OnRefreshListener;
import com.mcxiaoke.next.ui.view.SimpleProgressView;

class AdapterExtend extends AdapterWrapper {
    private static final boolean DEBUG = false;
    private static final String TAG;
    private Context mContext;
    private boolean mEnableRefreshing;
    private SimpleProgressView mFooter;
    private OnRefreshListener mRefreshListener;
    private boolean mRefreshing;

    static {
        TAG = AdapterExtend.class.getSimpleName();
    }

    public AdapterExtend(Context context, ListAdapter wrapped) {
        super(wrapped);
        this.mContext = context;
        this.mEnableRefreshing = true;
        this.mRefreshing = false;
        this.mFooter = new SimpleProgressView(context);
    }

    public void setRefreshListener(OnRefreshListener listener) {
        this.mRefreshListener = listener;
    }

    public boolean isRefreshing() {
        return this.mRefreshing;
    }

    public boolean isEnableRefreshing() {
        return this.mEnableRefreshing;
    }

    public void setEnableRefreshing(boolean value) {
        this.mEnableRefreshing = value;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.mFooter.setOnClickListener(listener);
    }

    public int getCount() {
        if (isEnableRefreshing()) {
            return super.getCount() + 1;
        }
        return super.getCount();
    }

    public int getItemViewType(int position) {
        if (position == getWrappedAdapter().getCount() && isEnableRefreshing()) {
            return -1;
        }
        return super.getItemViewType(position);
    }

    public int getViewTypeCount() {
        if (isEnableRefreshing()) {
            return super.getViewTypeCount() + 1;
        }
        return super.getViewTypeCount();
    }

    public Object getItem(int position) {
        if (position >= super.getCount()) {
            return null;
        }
        return super.getItem(position);
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int position) {
        if (position >= super.getCount()) {
            return false;
        }
        return super.isEnabled(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == super.getCount() && isEnableRefreshing()) {
            return this.mFooter;
        }
        return super.getView(position, convertView, parent);
    }

    public void onStateChanged(boolean refreshing, boolean needNotify) {
        boolean stateChanged = refreshing != this.mRefreshing;
        this.mRefreshing = refreshing;
        if (stateChanged && needNotify && this.mRefreshListener != null) {
            this.mRefreshListener.onRefresh(null);
        }
    }

    protected Context getContext() {
        return this.mContext;
    }

    public void setRefreshing(boolean needNotify) {
        if (!isRefreshing()) {
            onStateChanged(true, needNotify);
            this.mFooter.showProgress();
        }
    }

    public void showFooterEmpty() {
        onStateChanged(false, true);
        this.mFooter.showEmpty();
    }

    public void showFooterText(int resId) {
        showFooterText(getContext().getString(resId));
    }

    public void showFooterText(CharSequence text) {
        onStateChanged(false, true);
        this.mFooter.showText(text);
    }
}
