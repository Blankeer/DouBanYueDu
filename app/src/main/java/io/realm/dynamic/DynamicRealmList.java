package io.realm.dynamic;

import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import io.realm.Realm;
import io.realm.internal.LinkView;
import java.util.AbstractList;

public class DynamicRealmList extends AbstractList<DynamicRealmObject> {
    private final LinkView linkView;
    private final Realm realm;

    DynamicRealmList(LinkView linkView, Realm realm) {
        this.linkView = linkView;
        this.realm = realm;
    }

    public boolean add(DynamicRealmObject object) {
        checkIsValidObject(object);
        this.linkView.add(object.row.getIndex());
        return true;
    }

    public void clear() {
        this.linkView.clear();
    }

    public DynamicRealmObject get(int location) {
        checkValidIndex(location);
        return new DynamicRealmObject(this.realm, this.linkView.getCheckedRow((long) location));
    }

    public DynamicRealmObject remove(int location) {
        DynamicRealmObject removedItem = get(location);
        this.linkView.remove((long) location);
        return removedItem;
    }

    public DynamicRealmObject set(int location, DynamicRealmObject object) {
        checkIsValidObject(object);
        checkValidIndex(location);
        this.linkView.set((long) location, object.row.getIndex());
        return object;
    }

    public int size() {
        long size = this.linkView.size();
        return size < 2147483647L ? (int) size : AdvancedShareActionProvider.WEIGHT_MAX;
    }

    private void checkIsValidObject(DynamicRealmObject object) {
        if (object == null) {
            throw new IllegalArgumentException("DynamicRealmList does not accept null values");
        } else if (!this.realm.getConfiguration().equals(object.realm.getConfiguration())) {
            throw new IllegalArgumentException("Cannot add an object belonging to another Realm");
        } else if (!this.linkView.getTable().hasSameSchema(object.row.getTable())) {
            throw new IllegalArgumentException("Object is of type " + object.row.getTable().getName() + ". Expected " + this.linkView.getTable().getName());
        }
    }

    private void checkValidIndex(int index) {
        long size = this.linkView.size();
        if (index < 0 || ((long) index) >= size) {
            throw new IndexOutOfBoundsException(String.format("Invalid index: %d. Valid range is [%d, %d]", new Object[]{Integer.valueOf(index), Integer.valueOf(0), Long.valueOf(size - 1)}));
        }
    }
}
