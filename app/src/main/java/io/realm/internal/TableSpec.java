package io.realm.internal;

import java.util.ArrayList;
import java.util.List;

public class TableSpec {
    private List<ColumnInfo> columnInfos;

    public static class ColumnInfo {
        protected final String name;
        protected final TableSpec tableSpec;
        protected final ColumnType type;

        public ColumnInfo(ColumnType type, String name) {
            this.name = name;
            this.type = type;
            this.tableSpec = type == ColumnType.TABLE ? new TableSpec() : null;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((((this.name == null ? 0 : this.name.hashCode()) + 31) * 31) + (this.tableSpec == null ? 0 : this.tableSpec.hashCode())) * 31;
            if (this.type != null) {
                i = this.type.hashCode();
            }
            return hashCode + i;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ColumnInfo other = (ColumnInfo) obj;
            if (this.name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!this.name.equals(other.name)) {
                return false;
            }
            if (this.tableSpec == null) {
                if (other.tableSpec != null) {
                    return false;
                }
            } else if (!this.tableSpec.equals(other.tableSpec)) {
                return false;
            }
            if (this.type != other.type) {
                return false;
            }
            return true;
        }
    }

    public TableSpec() {
        this.columnInfos = new ArrayList();
    }

    public void addColumn(ColumnType type, String name) {
        if (name.length() > 63) {
            throw new IllegalArgumentException("Column names are currently limited to max 63 characters.");
        }
        this.columnInfos.add(new ColumnInfo(type, name));
    }

    protected void addColumn(int colTypeIndex, String name) {
        addColumn(ColumnType.fromNativeValue(colTypeIndex), name);
    }

    public TableSpec addSubtableColumn(String name) {
        if (name.length() > 63) {
            throw new IllegalArgumentException("Column names are currently limited to max 63 characters.");
        }
        ColumnInfo columnInfo = new ColumnInfo(ColumnType.TABLE, name);
        this.columnInfos.add(columnInfo);
        return columnInfo.tableSpec;
    }

    public TableSpec getSubtableSpec(long columnIndex) {
        return ((ColumnInfo) this.columnInfos.get((int) columnIndex)).tableSpec;
    }

    public long getColumnCount() {
        return (long) this.columnInfos.size();
    }

    public ColumnType getColumnType(long columnIndex) {
        return ((ColumnInfo) this.columnInfos.get((int) columnIndex)).type;
    }

    public String getColumnName(long columnIndex) {
        return ((ColumnInfo) this.columnInfos.get((int) columnIndex)).name;
    }

    public long getColumnIndex(String name) {
        for (int i = 0; i < this.columnInfos.size(); i++) {
            if (((ColumnInfo) this.columnInfos.get(i)).name.equals(name)) {
                return (long) i;
            }
        }
        return -1;
    }

    public int hashCode() {
        return (this.columnInfos == null ? 0 : this.columnInfos.hashCode()) + 31;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TableSpec other = (TableSpec) obj;
        if (this.columnInfos == null) {
            if (other.columnInfos != null) {
                return false;
            }
            return true;
        } else if (this.columnInfos.equals(other.columnInfos)) {
            return true;
        } else {
            return false;
        }
    }
}
