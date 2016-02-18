package io.realm;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

public abstract class RealmBaseAdapter<T extends RealmObject> extends BaseAdapter {
    protected Context context;
    protected LayoutInflater inflater;
    private final RealmChangeListener listener;
    protected RealmResults<T> realmResults;

    public RealmBaseAdapter(Context context, RealmResults<T> realmResults, boolean automaticUpdate) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
        this.realmResults = realmResults;
        this.inflater = LayoutInflater.from(context);
        this.listener = !automaticUpdate ? null : new RealmChangeListener() {
            public void onChange() {
                RealmBaseAdapter.this.notifyDataSetChanged();
            }
        };
        if (this.listener != null && realmResults != null) {
            realmResults.getRealm().addChangeListener(this.listener);
        }
    }

    public int getCount() {
        if (this.realmResults == null) {
            return 0;
        }
        return this.realmResults.size();
    }

    public T getItem(int i) {
        if (this.realmResults == null) {
            return null;
        }
        return this.realmResults.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public void updateRealmResults(RealmResults<T> queryResults) {
        if (this.listener != null) {
            if (this.realmResults != null) {
                this.realmResults.getRealm().removeChangeListener(this.listener);
            }
            if (queryResults != null) {
                queryResults.getRealm().addChangeListener(this.listener);
            }
        }
        this.realmResults = queryResults;
        notifyDataSetChanged();
    }
}
