package com.douban.book.reader.manager;

import android.net.Uri;
import com.douban.book.reader.network.param.QueryString;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.stmt.query.SimpleComparison;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DataFilter {
    Map<String, Object> mMap;

    public DataFilter() {
        this.mMap = new HashMap();
    }

    public static DataFilter fromUri(Uri uri) {
        DataFilter dataFilter = new DataFilter();
        if (uri != null) {
            for (String key : uri.getQueryParameterNames()) {
                dataFilter.append(key, uri.getQueryParameter(key));
            }
        }
        return dataFilter;
    }

    public DataFilter append(String key, Object value) {
        this.mMap.put(key, value);
        return this;
    }

    public DataFilter appendIfNotNull(String key, Object value) {
        if (value != null) {
            append(key, value);
        }
        return this;
    }

    public String toWhereStatement() {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, Object> entry : this.mMap.entrySet()) {
            if (builder.length() > 0) {
                builder.append(" and ");
            }
            builder.append(String.format("%s = ?", new Object[]{entry.getKey()}));
        }
        return builder.toString();
    }

    public Object[] getWhereArgs() {
        List<Object> list = new ArrayList();
        for (Entry<String, Object> entry : this.mMap.entrySet()) {
            list.add(entry.getValue());
        }
        return list.toArray();
    }

    public <T, ID> void applyToQueryBuilder(QueryBuilder<T, ID> queryBuilder) throws SQLException {
        Where<T, ID> where = queryBuilder.where();
        boolean first = true;
        for (Entry<String, Object> entry : this.mMap.entrySet()) {
            if (!first) {
                where.and();
            }
            first = false;
            where.rawComparison((String) entry.getKey(), SimpleComparison.EQUAL_TO_OPERATION, entry.getValue());
        }
    }

    public QueryString toQueryString() {
        QueryString queryString = new QueryString();
        for (Entry<String, Object> entry : this.mMap.entrySet()) {
            queryString.append((String) entry.getKey(), entry.getValue());
        }
        return queryString;
    }

    public Object get(String key) {
        return this.mMap.get(key);
    }

    public String toString() {
        return toQueryString().toString();
    }
}
