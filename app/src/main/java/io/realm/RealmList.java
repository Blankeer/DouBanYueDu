package io.realm;

import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import io.realm.exceptions.RealmException;
import io.realm.internal.LinkView;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RealmList<E extends RealmObject> extends AbstractList<E> {
    private static final String NULL_OBJECTS_NOT_ALLOWED_MESSAGE = "RealmList does not accept null values";
    private static final String ONLY_IN_MANAGED_MODE_MESSAGE = "This method is only available in managed mode";
    private Class<E> clazz;
    private final boolean managedMode;
    private List<E> nonManagedList;
    private Realm realm;
    private LinkView view;

    public RealmList() {
        this.managedMode = false;
        this.nonManagedList = new ArrayList();
    }

    public RealmList(E... objects) {
        if (objects == null) {
            throw new IllegalArgumentException("The objects argument cannot be null");
        }
        this.managedMode = false;
        this.nonManagedList = new ArrayList(objects.length);
        Collections.addAll(this.nonManagedList, objects);
    }

    RealmList(Class<E> clazz, LinkView view, Realm realm) {
        this.managedMode = true;
        this.clazz = clazz;
        this.view = view;
        this.realm = realm;
    }

    public void add(int location, E object) {
        checkValidObject(object);
        if (this.managedMode) {
            this.view.insert((long) location, copyToRealmIfNeeded(object).row.getIndex());
            return;
        }
        this.nonManagedList.add(location, object);
    }

    public boolean add(E object) {
        checkValidObject(object);
        if (this.managedMode) {
            this.view.add(copyToRealmIfNeeded(object).row.getIndex());
        } else {
            this.nonManagedList.add(object);
        }
        return true;
    }

    public E set(int location, E object) {
        checkValidObject(object);
        if (this.managedMode) {
            object = copyToRealmIfNeeded(object);
            this.view.set((long) location, object.row.getIndex());
            return object;
        }
        this.nonManagedList.set(location, object);
        return object;
    }

    private E copyToRealmIfNeeded(E object) {
        if (object.row != null && object.realm.getPath().equals(this.realm.getPath())) {
            return object;
        }
        if (this.realm.getTable(object.getClass()).hasPrimaryKey()) {
            return this.realm.copyToRealmOrUpdate((RealmObject) object);
        }
        return this.realm.copyToRealm((RealmObject) object);
    }

    public void move(int oldPos, int newPos) {
        if (this.managedMode) {
            this.view.move((long) oldPos, (long) newPos);
            return;
        }
        checkIndex(oldPos);
        checkIndex(newPos);
        RealmObject object = (RealmObject) this.nonManagedList.remove(oldPos);
        if (newPos > oldPos) {
            this.nonManagedList.add(newPos - 1, object);
        } else {
            this.nonManagedList.add(newPos, object);
        }
    }

    public void clear() {
        if (this.managedMode) {
            this.view.clear();
        } else {
            this.nonManagedList.clear();
        }
    }

    public E remove(int location) {
        if (!this.managedMode) {
            return (RealmObject) this.nonManagedList.remove(location);
        }
        E removedItem = get(location);
        this.view.remove((long) location);
        return removedItem;
    }

    public E get(int location) {
        if (this.managedMode) {
            return this.realm.get(this.clazz, this.view.getTargetRowIndex((long) location));
        }
        return (RealmObject) this.nonManagedList.get(location);
    }

    public E first() {
        if (this.managedMode && !this.view.isEmpty()) {
            return get(0);
        }
        if (this.nonManagedList == null || this.nonManagedList.size() <= 0) {
            return null;
        }
        return (RealmObject) this.nonManagedList.get(0);
    }

    public E last() {
        if (this.managedMode && !this.view.isEmpty()) {
            return get(((int) this.view.size()) - 1);
        }
        if (this.nonManagedList == null || this.nonManagedList.size() <= 0) {
            return null;
        }
        return (RealmObject) this.nonManagedList.get(this.nonManagedList.size() - 1);
    }

    public int size() {
        if (!this.managedMode) {
            return this.nonManagedList.size();
        }
        long size = this.view.size();
        return size < 2147483647L ? (int) size : AdvancedShareActionProvider.WEIGHT_MAX;
    }

    public RealmQuery<E> where() {
        if (this.managedMode) {
            return new RealmQuery(this.realm, this.view, this.clazz);
        }
        throw new RealmException(ONLY_IN_MANAGED_MODE_MESSAGE);
    }

    private void checkValidObject(E object) {
        if (object == null) {
            throw new IllegalArgumentException(NULL_OBJECTS_NOT_ALLOWED_MESSAGE);
        }
    }

    private void checkIndex(int location) {
        int size = size();
        if (location < 0 || location >= size) {
            throw new IndexOutOfBoundsException("Invalid index " + location + ", size is " + size);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.managedMode ? this.clazz.getSimpleName() : getClass().getSimpleName());
        sb.append("@[");
        for (int i = 0; i < size(); i++) {
            if (this.managedMode) {
                sb.append(get(i).row.getIndex());
            } else {
                sb.append(System.identityHashCode(get(i)));
            }
            if (i < size() - 1) {
                sb.append(',');
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
