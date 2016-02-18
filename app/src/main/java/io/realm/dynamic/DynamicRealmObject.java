package io.realm.dynamic;

import com.alipay.sdk.protocol.h;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.internal.CheckedRow;
import io.realm.internal.ColumnType;
import io.realm.internal.InvalidRow;
import io.realm.internal.LinkView;
import io.realm.internal.Row;
import io.realm.internal.Table;
import io.realm.internal.UncheckedRow;
import java.util.Date;
import java.util.Iterator;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public class DynamicRealmObject extends RealmObject {
    Realm realm;
    Row row;

    /* renamed from: io.realm.dynamic.DynamicRealmObject.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$io$realm$internal$ColumnType;

        static {
            $SwitchMap$io$realm$internal$ColumnType = new int[ColumnType.values().length];
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.LINK.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.LINK_LIST.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.BOOLEAN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.INTEGER.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.FLOAT.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.DOUBLE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.STRING.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.BINARY.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.DATE.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.TABLE.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.MIXED.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
        }
    }

    public DynamicRealmObject(RealmObject obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Non-null object must be provided.");
        }
        Row row = RealmObject.getRow(obj);
        if (row == null) {
            throw new IllegalArgumentException("A object managed by Realm must be provided. This is a standalone object.");
        }
        this.realm = RealmObject.getRealm(obj);
        this.row = row instanceof CheckedRow ? (CheckedRow) row : ((UncheckedRow) row).convertToChecked();
    }

    DynamicRealmObject(Realm realm, CheckedRow row) {
        this.realm = realm;
        this.row = row;
    }

    public boolean getBoolean(String fieldName) {
        return this.row.getBoolean(this.row.getColumnIndex(fieldName));
    }

    public int getInt(String fieldName) {
        return (int) getLong(fieldName);
    }

    public short getShort(String fieldName) {
        return (short) ((int) getLong(fieldName));
    }

    public long getLong(String fieldName) {
        return this.row.getLong(this.row.getColumnIndex(fieldName));
    }

    public byte getByte(String fieldName) {
        return (byte) ((int) this.row.getLong(this.row.getColumnIndex(fieldName)));
    }

    public float getFloat(String fieldName) {
        return this.row.getFloat(this.row.getColumnIndex(fieldName));
    }

    public double getDouble(String fieldName) {
        return this.row.getDouble(this.row.getColumnIndex(fieldName));
    }

    public byte[] getBlob(String fieldName) {
        return this.row.getBinaryByteArray(this.row.getColumnIndex(fieldName));
    }

    public String getString(String fieldName) {
        return this.row.getString(this.row.getColumnIndex(fieldName));
    }

    public Date getDate(String fieldName) {
        return this.row.getDate(this.row.getColumnIndex(fieldName));
    }

    public DynamicRealmObject getObject(String fieldName) {
        long columnIndex = this.row.getColumnIndex(fieldName);
        if (this.row.isNullLink(columnIndex)) {
            return null;
        }
        return new DynamicRealmObject(this.realm, this.row.getTable().getCheckedRow(this.row.getLink(columnIndex)));
    }

    public DynamicRealmList getList(String fieldName) {
        return new DynamicRealmList(this.row.getLinkList(this.row.getColumnIndex(fieldName)), this.realm);
    }

    public boolean isNull(String fieldName) {
        long columnIndex = this.row.getColumnIndex(fieldName);
        switch (AnonymousClass1.$SwitchMap$io$realm$internal$ColumnType[this.row.getColumnType(columnIndex).ordinal()]) {
            case dx.b /*1*/:
            case dx.c /*2*/:
                return this.row.isNullLink(columnIndex);
            default:
                return false;
        }
    }

    public boolean hasField(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return false;
        }
        return this.row.hasColumn(fieldName);
    }

    public String[] getFieldNames() {
        String[] keys = new String[((int) this.row.getColumnCount())];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = this.row.getColumnName((long) i);
        }
        return keys;
    }

    public void setBoolean(String fieldName, boolean value) {
        this.row.setBoolean(this.row.getColumnIndex(fieldName), value);
    }

    public void setShort(String fieldName, short value) {
        this.row.setLong(this.row.getColumnIndex(fieldName), (long) value);
    }

    public void setInt(String fieldName, int value) {
        this.row.setLong(this.row.getColumnIndex(fieldName), (long) value);
    }

    public void setLong(String fieldName, long value) {
        this.row.setLong(this.row.getColumnIndex(fieldName), value);
    }

    public void setByte(String fieldName, byte value) {
        this.row.setLong(this.row.getColumnIndex(fieldName), (long) value);
    }

    public void setFloat(String fieldName, float value) {
        this.row.setFloat(this.row.getColumnIndex(fieldName), value);
    }

    public void setDouble(String fieldName, double value) {
        this.row.setDouble(this.row.getColumnIndex(fieldName), value);
    }

    public void setString(String fieldName, String value) {
        this.row.setString(this.row.getColumnIndex(fieldName), value);
    }

    public void setBlob(String fieldName, byte[] value) {
        this.row.setBinaryByteArray(this.row.getColumnIndex(fieldName), value);
    }

    public void setDate(String fieldName, Date value) {
        this.row.setDate(this.row.getColumnIndex(fieldName), value);
    }

    public void setObject(String fieldName, DynamicRealmObject value) {
        long columnIndex = this.row.getColumnIndex(fieldName);
        if (value == null) {
            this.row.nullifyLink(columnIndex);
        } else if (value.realm == null || value.row == null) {
            throw new IllegalArgumentException("Cannot link to objects that are not part of the Realm.");
        } else if (this.realm.getConfiguration().equals(value.realm.getConfiguration())) {
            if (this.row.getTable().hasSameSchema(value.row.getTable())) {
                this.row.setLink(columnIndex, value.row.getIndex());
            } else {
                throw new IllegalArgumentException(String.format("Type of object is wrong. Was %s, expected %s", new Object[]{value.row.getTable().getName(), this.row.getTable().getName()}));
            }
        } else {
            throw new IllegalArgumentException("Cannot add an object from another Realm");
        }
    }

    public void setList(String fieldName, DynamicRealmList list) {
        LinkView links = this.row.getLinkList(this.row.getColumnIndex(fieldName));
        links.clear();
        Iterator i$ = list.iterator();
        while (i$.hasNext()) {
            links.add(((DynamicRealmObject) i$.next()).row.getIndex());
        }
    }

    public void removeFromRealm() {
        this.row.getTable().moveLastOver(this.row.getIndex());
        this.row = InvalidRow.INSTANCE;
    }

    public String getType() {
        return this.row.getTable().getName().substring(Table.TABLE_PREFIX.length());
    }

    public int hashCode() {
        int hashCode;
        int i = 0;
        String realmName = this.realm.getPath();
        String tableName = this.row.getTable().getName();
        long rowIndex = this.row.getIndex();
        if (realmName != null) {
            hashCode = realmName.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode = (hashCode + 527) * 31;
        if (tableName != null) {
            i = tableName.hashCode();
        }
        return ((hashCode + i) * 31) + ((int) ((rowIndex >>> 32) ^ rowIndex));
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DynamicRealmObject other = (DynamicRealmObject) o;
        String path = this.realm.getPath();
        String otherPath = other.realm.getPath();
        if (path != null) {
            if (!path.equals(otherPath)) {
                return false;
            }
        } else if (otherPath != null) {
            return false;
        }
        String tableName = this.row.getTable().getName();
        String otherTableName = other.row.getTable().getName();
        if (tableName != null) {
            if (!tableName.equals(otherTableName)) {
                return false;
            }
        } else if (otherTableName != null) {
            return false;
        }
        if (this.row.getIndex() != other.row.getIndex()) {
            z = false;
        }
        return z;
    }

    public String toString() {
        if (this.row == null || !this.row.isAttached()) {
            return "Invalid object";
        }
        StringBuilder sb = new StringBuilder(this.row.getTable().getName() + " = [");
        for (String field : getFieldNames()) {
            long columnIndex = this.row.getColumnIndex(field);
            ColumnType type = this.row.getColumnType(columnIndex);
            sb.append("{");
            switch (AnonymousClass1.$SwitchMap$io$realm$internal$ColumnType[type.ordinal()]) {
                case dx.b /*1*/:
                    if (!this.row.isNullLink(columnIndex)) {
                        sb.append(field + ": " + this.row.getTable().getLinkTarget(columnIndex).getName());
                        break;
                    }
                    sb.append("null");
                    break;
                case dx.c /*2*/:
                    String targetType = this.row.getTable().getLinkTarget(columnIndex).getName();
                    sb.append(String.format("%s: RealmList<%s>[%s]", new Object[]{field, targetType, Long.valueOf(this.row.getLinkList(columnIndex).size())}));
                    break;
                case dx.d /*3*/:
                    sb.append(field + ": " + this.row.getBoolean(columnIndex));
                    break;
                case dx.e /*4*/:
                    sb.append(field + ": " + this.row.getLong(columnIndex));
                    break;
                case dj.f /*5*/:
                    sb.append(field + ": " + this.row.getFloat(columnIndex));
                    break;
                case ci.g /*6*/:
                    sb.append(field + ": " + this.row.getDouble(columnIndex));
                    break;
                case ci.h /*7*/:
                    sb.append(field + ": " + this.row.getString(columnIndex));
                    break;
                case h.g /*8*/:
                    sb.append(field + ": " + this.row.getBinaryByteArray(columnIndex));
                    break;
                case h.h /*9*/:
                    sb.append(field + ": " + this.row.getDate(columnIndex));
                    break;
                default:
                    sb.append(field + ": ?");
                    break;
            }
            sb.append("}, ");
        }
        sb.replace(sb.length() - 2, sb.length(), Table.STRING_DEFAULT_VALUE);
        sb.append("]");
        return sb.toString();
    }
}
