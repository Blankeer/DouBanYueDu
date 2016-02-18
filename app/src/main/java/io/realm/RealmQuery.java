package io.realm;

import com.douban.book.reader.constant.Char;
import io.realm.internal.ColumnType;
import io.realm.internal.LinkView;
import io.realm.internal.Table;
import io.realm.internal.TableQuery;
import io.realm.internal.TableView;
import io.realm.internal.TableView.Order;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealmQuery<E extends RealmObject> {
    public static final boolean CASE_INSENSITIVE = false;
    public static final boolean CASE_SENSITIVE = true;
    private static final String LINK_NOT_SUPPORTED_METHOD = "'%s' is not supported for link queries";
    private Class<E> clazz;
    private Map<String, Long> columns;
    private TableQuery query;
    private Realm realm;
    private Table table;
    private LinkView view;

    public RealmQuery(Realm realm, Class<E> clazz) {
        this.columns = new HashMap();
        this.realm = realm;
        this.clazz = clazz;
        this.table = realm.getTable(clazz);
        this.query = this.table.where();
        this.columns = realm.columnIndices.getClassFields(clazz);
    }

    public RealmQuery(RealmResults realmList, Class<E> clazz) {
        this.columns = new HashMap();
        this.realm = realmList.getRealm();
        this.clazz = clazz;
        this.table = this.realm.getTable(clazz);
        this.query = realmList.getTable().where();
        this.columns = this.realm.columnIndices.getClassFields(clazz);
    }

    RealmQuery(Realm realm, LinkView view, Class<E> clazz) {
        this.columns = new HashMap();
        this.realm = realm;
        this.clazz = clazz;
        this.query = view.where();
        this.view = view;
        this.table = realm.getTable(clazz);
        this.columns = realm.columnIndices.getClassFields(clazz);
    }

    private boolean containsDot(String s) {
        return s.indexOf(46) != -1 ? CASE_SENSITIVE : CASE_INSENSITIVE;
    }

    private String[] splitString(String s) {
        int i;
        int n = 0;
        for (i = 0; i < s.length(); i++) {
            if (s.charAt(i) == Char.DOT) {
                n++;
            }
        }
        String[] arr = new String[(n + 1)];
        i = 0;
        n = 0;
        int j = s.indexOf(46);
        while (j != -1) {
            arr[n] = s.substring(i, j);
            i = j + 1;
            j = s.indexOf(46, i);
            n++;
        }
        arr[n] = s.substring(s.lastIndexOf(46) + 1);
        return arr;
    }

    private long[] getColumnIndices(String fieldName, ColumnType fieldType) {
        Table table = this.table;
        if (containsDot(fieldName)) {
            String[] names = splitString(fieldName);
            long[] jArr = new long[names.length];
            int i = 0;
            while (i < names.length - 1) {
                long index = table.getColumnIndex(names[i]);
                if (index < 0) {
                    throw new IllegalArgumentException("Invalid query: " + names[i] + " does not refer to a class.");
                }
                ColumnType type = table.getColumnType(index);
                if (type == ColumnType.LINK || type == ColumnType.LINK_LIST) {
                    table = table.getLinkTarget(index);
                    jArr[i] = index;
                    i++;
                } else {
                    throw new IllegalArgumentException("Invalid query: " + names[i] + " does not refer to a class.");
                }
            }
            jArr[names.length - 1] = table.getColumnIndex(names[names.length - 1]);
            if (fieldType == table.getColumnType(jArr[names.length - 1])) {
                return jArr;
            }
            throw new IllegalArgumentException(String.format("Field '%s': type mismatch.", new Object[]{names[names.length - 1]}));
        } else if (this.columns.get(fieldName) == null) {
            throw new IllegalArgumentException(String.format("Field '%s' does not exist.", new Object[]{fieldName}));
        } else {
            if (fieldType != table.getColumnType(((Long) this.columns.get(fieldName)).longValue())) {
                throw new IllegalArgumentException(String.format("Field '%s': type mismatch. Was %s, expected %s.", new Object[]{fieldName, fieldType, table.getColumnType(((Long) this.columns.get(fieldName)).longValue())}));
            }
            return new long[]{((Long) this.columns.get(fieldName)).longValue()};
        }
    }

    public RealmQuery<E> isNull(String fieldName) {
        if (containsDot(fieldName)) {
            throw new IllegalArgumentException("Checking for null in nested objects is not supported.");
        }
        this.query.isNull(((Long) this.columns.get(fieldName)).longValue());
        return this;
    }

    public RealmQuery<E> isNotNull(String fieldName) {
        return beginGroup().not().isNull(fieldName).endGroup();
    }

    public RealmQuery<E> equalTo(String fieldName, String value) {
        return equalTo(fieldName, value, CASE_SENSITIVE);
    }

    public RealmQuery<E> equalTo(String fieldName, String value, boolean caseSensitive) {
        this.query.equalTo(getColumnIndices(fieldName, ColumnType.STRING), value, caseSensitive);
        return this;
    }

    public RealmQuery<E> equalTo(String fieldName, int value) {
        this.query.equalTo(getColumnIndices(fieldName, ColumnType.INTEGER), (long) value);
        return this;
    }

    public RealmQuery<E> equalTo(String fieldName, long value) {
        this.query.equalTo(getColumnIndices(fieldName, ColumnType.INTEGER), value);
        return this;
    }

    public RealmQuery<E> equalTo(String fieldName, double value) {
        this.query.equalTo(getColumnIndices(fieldName, ColumnType.DOUBLE), value);
        return this;
    }

    public RealmQuery<E> equalTo(String fieldName, float value) {
        this.query.equalTo(getColumnIndices(fieldName, ColumnType.FLOAT), value);
        return this;
    }

    public RealmQuery<E> equalTo(String fieldName, boolean value) {
        this.query.equalTo(getColumnIndices(fieldName, ColumnType.BOOLEAN), value);
        return this;
    }

    public RealmQuery<E> equalTo(String fieldName, Date value) {
        this.query.equalTo(getColumnIndices(fieldName, ColumnType.DATE), value);
        return this;
    }

    public RealmQuery<E> notEqualTo(String fieldName, String value) {
        return notEqualTo(fieldName, value, CASE_SENSITIVE);
    }

    public RealmQuery<E> notEqualTo(String fieldName, String value, boolean caseSensitive) {
        long[] columnIndices = getColumnIndices(fieldName, ColumnType.STRING);
        if (columnIndices.length <= 1 || caseSensitive) {
            this.query.notEqualTo(columnIndices, value, caseSensitive);
            return this;
        }
        throw new IllegalArgumentException("Link queries cannot be case insensitive - coming soon.");
    }

    public RealmQuery<E> notEqualTo(String fieldName, int value) {
        this.query.notEqualTo(getColumnIndices(fieldName, ColumnType.INTEGER), (long) value);
        return this;
    }

    public RealmQuery<E> notEqualTo(String fieldName, long value) {
        this.query.notEqualTo(getColumnIndices(fieldName, ColumnType.INTEGER), value);
        return this;
    }

    public RealmQuery<E> notEqualTo(String fieldName, double value) {
        this.query.notEqualTo(getColumnIndices(fieldName, ColumnType.DOUBLE), value);
        return this;
    }

    public RealmQuery<E> notEqualTo(String fieldName, float value) {
        this.query.notEqualTo(getColumnIndices(fieldName, ColumnType.FLOAT), value);
        return this;
    }

    public RealmQuery<E> notEqualTo(String fieldName, boolean value) {
        this.query.equalTo(getColumnIndices(fieldName, ColumnType.BOOLEAN), !value ? CASE_SENSITIVE : CASE_INSENSITIVE);
        return this;
    }

    public RealmQuery<E> notEqualTo(String fieldName, Date value) {
        this.query.notEqualTo(getColumnIndices(fieldName, ColumnType.DATE), value);
        return this;
    }

    public RealmQuery<E> greaterThan(String fieldName, int value) {
        this.query.greaterThan(getColumnIndices(fieldName, ColumnType.INTEGER), (long) value);
        return this;
    }

    public RealmQuery<E> greaterThan(String fieldName, long value) {
        this.query.greaterThan(getColumnIndices(fieldName, ColumnType.INTEGER), value);
        return this;
    }

    public RealmQuery<E> greaterThan(String fieldName, double value) {
        this.query.greaterThan(getColumnIndices(fieldName, ColumnType.DOUBLE), value);
        return this;
    }

    public RealmQuery<E> greaterThan(String fieldName, float value) {
        this.query.greaterThan(getColumnIndices(fieldName, ColumnType.FLOAT), value);
        return this;
    }

    public RealmQuery<E> greaterThan(String fieldName, Date value) {
        this.query.greaterThan(getColumnIndices(fieldName, ColumnType.DATE), value);
        return this;
    }

    public RealmQuery<E> greaterThanOrEqualTo(String fieldName, int value) {
        this.query.greaterThanOrEqual(getColumnIndices(fieldName, ColumnType.INTEGER), (long) value);
        return this;
    }

    public RealmQuery<E> greaterThanOrEqualTo(String fieldName, long value) {
        this.query.greaterThanOrEqual(getColumnIndices(fieldName, ColumnType.INTEGER), value);
        return this;
    }

    public RealmQuery<E> greaterThanOrEqualTo(String fieldName, double value) {
        this.query.greaterThanOrEqual(getColumnIndices(fieldName, ColumnType.DOUBLE), value);
        return this;
    }

    public RealmQuery<E> greaterThanOrEqualTo(String fieldName, float value) {
        this.query.greaterThanOrEqual(getColumnIndices(fieldName, ColumnType.FLOAT), value);
        return this;
    }

    public RealmQuery<E> greaterThanOrEqualTo(String fieldName, Date value) {
        this.query.greaterThanOrEqual(getColumnIndices(fieldName, ColumnType.DATE), value);
        return this;
    }

    public RealmQuery<E> lessThan(String fieldName, int value) {
        this.query.lessThan(getColumnIndices(fieldName, ColumnType.INTEGER), (long) value);
        return this;
    }

    public RealmQuery<E> lessThan(String fieldName, long value) {
        this.query.lessThan(getColumnIndices(fieldName, ColumnType.INTEGER), value);
        return this;
    }

    public RealmQuery<E> lessThan(String fieldName, double value) {
        this.query.lessThan(getColumnIndices(fieldName, ColumnType.DOUBLE), value);
        return this;
    }

    public RealmQuery<E> lessThan(String fieldName, float value) {
        this.query.lessThan(getColumnIndices(fieldName, ColumnType.FLOAT), value);
        return this;
    }

    public RealmQuery<E> lessThan(String fieldName, Date value) {
        this.query.lessThan(getColumnIndices(fieldName, ColumnType.DATE), value);
        return this;
    }

    public RealmQuery<E> lessThanOrEqualTo(String fieldName, int value) {
        this.query.lessThanOrEqual(getColumnIndices(fieldName, ColumnType.INTEGER), (long) value);
        return this;
    }

    public RealmQuery<E> lessThanOrEqualTo(String fieldName, long value) {
        this.query.lessThanOrEqual(getColumnIndices(fieldName, ColumnType.INTEGER), value);
        return this;
    }

    public RealmQuery<E> lessThanOrEqualTo(String fieldName, double value) {
        this.query.lessThanOrEqual(getColumnIndices(fieldName, ColumnType.DOUBLE), value);
        return this;
    }

    public RealmQuery<E> lessThanOrEqualTo(String fieldName, float value) {
        this.query.lessThanOrEqual(getColumnIndices(fieldName, ColumnType.FLOAT), value);
        return this;
    }

    public RealmQuery<E> lessThanOrEqualTo(String fieldName, Date value) {
        this.query.lessThanOrEqual(getColumnIndices(fieldName, ColumnType.DATE), value);
        return this;
    }

    public RealmQuery<E> between(String fieldName, int from, int to) {
        this.query.between(getColumnIndices(fieldName, ColumnType.INTEGER), (long) from, (long) to);
        return this;
    }

    public RealmQuery<E> between(String fieldName, long from, long to) {
        this.query.between(getColumnIndices(fieldName, ColumnType.INTEGER), from, to);
        return this;
    }

    public RealmQuery<E> between(String fieldName, double from, double to) {
        this.query.between(getColumnIndices(fieldName, ColumnType.DOUBLE), from, to);
        return this;
    }

    public RealmQuery<E> between(String fieldName, float from, float to) {
        this.query.between(getColumnIndices(fieldName, ColumnType.FLOAT), from, to);
        return this;
    }

    public RealmQuery<E> between(String fieldName, Date from, Date to) {
        this.query.between(getColumnIndices(fieldName, ColumnType.DATE), from, to);
        return this;
    }

    public RealmQuery<E> contains(String fieldName, String value) {
        return contains(fieldName, value, CASE_SENSITIVE);
    }

    public RealmQuery<E> contains(String fieldName, String value, boolean caseSensitive) {
        this.query.contains(getColumnIndices(fieldName, ColumnType.STRING), value, caseSensitive);
        return this;
    }

    public RealmQuery<E> beginsWith(String fieldName, String value) {
        return beginsWith(fieldName, value, CASE_SENSITIVE);
    }

    public RealmQuery<E> beginsWith(String fieldName, String value, boolean caseSensitive) {
        this.query.beginsWith(getColumnIndices(fieldName, ColumnType.STRING), value, caseSensitive);
        return this;
    }

    public RealmQuery<E> endsWith(String fieldName, String value) {
        return endsWith(fieldName, value, CASE_SENSITIVE);
    }

    public RealmQuery<E> endsWith(String fieldName, String value, boolean caseSensitive) {
        this.query.endsWith(getColumnIndices(fieldName, ColumnType.STRING), value, caseSensitive);
        return this;
    }

    public RealmQuery<E> beginGroup() {
        this.query.group();
        return this;
    }

    public RealmQuery<E> endGroup() {
        this.query.endGroup();
        return this;
    }

    public RealmQuery<E> or() {
        this.query.or();
        return this;
    }

    public RealmQuery<E> not() {
        this.query.not();
        return this;
    }

    public long sumInt(String fieldName) {
        return this.query.sumInt(((Long) this.columns.get(fieldName)).longValue());
    }

    public double sumDouble(String fieldName) {
        return this.query.sumDouble(((Long) this.columns.get(fieldName)).longValue());
    }

    public double sumFloat(String fieldName) {
        return this.query.sumFloat(((Long) this.columns.get(fieldName)).longValue());
    }

    public double averageInt(String fieldName) {
        return this.query.averageInt(((Long) this.columns.get(fieldName)).longValue());
    }

    public double averageDouble(String fieldName) {
        return this.query.averageDouble(((Long) this.columns.get(fieldName)).longValue());
    }

    public double averageFloat(String fieldName) {
        return this.query.averageFloat(((Long) this.columns.get(fieldName)).longValue());
    }

    public long minimumInt(String fieldName) {
        return this.query.minimumInt(((Long) this.columns.get(fieldName)).longValue());
    }

    public double minimumDouble(String fieldName) {
        return this.query.minimumDouble(((Long) this.columns.get(fieldName)).longValue());
    }

    public float minimumFloat(String fieldName) {
        return this.query.minimumFloat(((Long) this.columns.get(fieldName)).longValue());
    }

    public Date minimumDate(String fieldName) {
        return this.query.minimumDate(((Long) this.columns.get(fieldName)).longValue());
    }

    public long maximumInt(String fieldName) {
        return this.query.maximumInt(((Long) this.columns.get(fieldName)).longValue());
    }

    public double maximumDouble(String fieldName) {
        return this.query.maximumDouble(((Long) this.columns.get(fieldName)).longValue());
    }

    public float maximumFloat(String fieldName) {
        return this.query.maximumFloat(((Long) this.columns.get(fieldName)).longValue());
    }

    public Date maximumDate(String fieldName) {
        return this.query.maximumDate(((Long) this.columns.get(fieldName)).longValue());
    }

    public long count() {
        return this.query.count();
    }

    public RealmResults<E> findAll() {
        return new RealmResults(this.realm, this.query.findAll(), this.clazz);
    }

    public RealmResults<E> findAllSorted(String fieldName, boolean sortAscending) {
        TableView tableView = this.query.findAll();
        Order order = sortAscending ? Order.ascending : Order.descending;
        Long columnIndex = (Long) this.columns.get(fieldName);
        if (columnIndex == null || columnIndex.longValue() < 0) {
            throw new IllegalArgumentException(String.format("Field name '%s' does not exist.", new Object[]{fieldName}));
        }
        tableView.sort(columnIndex.longValue(), order);
        return new RealmResults(this.realm, tableView, this.clazz);
    }

    public RealmResults<E> findAllSorted(String fieldName) {
        return findAllSorted(fieldName, (boolean) CASE_SENSITIVE);
    }

    public RealmResults<E> findAllSorted(String[] fieldNames, boolean[] sortAscending) {
        if (fieldNames == null) {
            throw new IllegalArgumentException("fieldNames cannot be 'null'.");
        } else if (sortAscending == null) {
            throw new IllegalArgumentException("sortAscending cannot be 'null'.");
        } else if (fieldNames.length == 0) {
            throw new IllegalArgumentException("At least one field name must be specified.");
        } else if (fieldNames.length != sortAscending.length) {
            throw new IllegalArgumentException(String.format("Number of field names (%d) and sort orders (%d) does not match.", new Object[]{Integer.valueOf(fieldNames.length), Integer.valueOf(sortAscending.length)}));
        } else if (fieldNames.length == 1 && sortAscending.length == 1) {
            return findAllSorted(fieldNames[0], sortAscending[0]);
        } else {
            TableView tableView = this.query.findAll();
            List columnIndices = new ArrayList();
            List orders = new ArrayList();
            for (String fieldName : fieldNames) {
                Long columnIndex = (Long) this.columns.get(fieldName);
                if (columnIndex == null || columnIndex.longValue() < 0) {
                    throw new IllegalArgumentException(String.format("Field name '%s' does not exist.", new Object[]{fieldName}));
                }
                columnIndices.add(columnIndex);
            }
            for (boolean z : sortAscending) {
                orders.add(z ? Order.ascending : Order.descending);
            }
            tableView.sort(columnIndices, orders);
            return new RealmResults(this.realm, tableView, this.clazz);
        }
    }

    public RealmResults<E> findAllSorted(String fieldName1, boolean sortAscending1, String fieldName2, boolean sortAscending2) {
        return findAllSorted(new String[]{fieldName1, fieldName2}, new boolean[]{sortAscending1, sortAscending2});
    }

    public RealmResults<E> findAllSorted(String fieldName1, boolean sortAscending1, String fieldName2, boolean sortAscending2, String fieldName3, boolean sortAscending3) {
        return findAllSorted(new String[]{fieldName1, fieldName2, fieldName3}, new boolean[]{sortAscending1, sortAscending2, sortAscending3});
    }

    public E findFirst() {
        long rowIndex = this.query.find();
        if (rowIndex < 0) {
            return null;
        }
        Realm realm = this.realm;
        Class cls = this.clazz;
        if (this.view != null) {
            rowIndex = this.view.getTargetRowIndex(rowIndex);
        }
        return realm.get(cls, rowIndex);
    }
}
