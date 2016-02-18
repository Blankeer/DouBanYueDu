package com.mcxiaoke.next.ui.endless;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import com.mcxiaoke.next.ui.internal.AdapterWrapper;

class EndlessAdapter extends AdapterWrapper {
    private Context mContext;
    private View mFooter;
    private OnFooterStateChangeListener mFooterStateChangeListener;
    private FooterState mState;

    public enum FooterState {
        NONE,
        PROGRESS,
        IDLE
    }

    public interface OnFooterStateChangeListener {
        void onFooterStateChanged(FooterState footerState, EndlessAdapter endlessAdapter);
    }

    public EndlessAdapter(Context context, ListAdapter wrapped) {
        super(wrapped);
        this.mState = FooterState.NONE;
        this.mFooter = null;
        this.mContext = context;
    }

    public void setFooterStateChangeListener(OnFooterStateChangeListener listener) {
        this.mFooterStateChangeListener = listener;
    }

    public void setFooterView(View footerView) {
        this.mFooter = footerView;
    }

    public FooterState getState() {
        return this.mState;
    }

    public boolean isRefreshing() {
        return FooterState.PROGRESS.equals(this.mState);
    }

    public boolean isIdle() {
        return FooterState.IDLE.equals(this.mState);
    }

    public boolean isFooterShowing() {
        return true;
    }

    public int getCount() {
        if (isFooterShowing()) {
            return super.getCount() + 1;
        }
        return super.getCount();
    }

    public int getItemViewType(int position) {
        if (position == getWrappedAdapter().getCount()) {
            return -1;
        }
        return super.getItemViewType(position);
    }

    public int getViewTypeCount() {
        return super.getViewTypeCount() + 1;
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
        if (position == super.getCount() && isFooterShowing()) {
            return this.mFooter;
        }
        return super.getView(position, convertView, parent);
    }

    public void setState(FooterState newState, boolean needNotify) {
        boolean stateChanged = !this.mState.equals(newState);
        FooterState oldState = this.mState;
        this.mState = newState;
        if (stateChanged && needNotify && this.mFooterStateChangeListener != null) {
            this.mFooterStateChangeListener.onFooterStateChanged(this.mState, this);
        }
    }

    protected Context getContext() {
        return this.mContext;
    }
}
