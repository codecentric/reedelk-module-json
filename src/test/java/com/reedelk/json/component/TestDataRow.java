package com.reedelk.json.component;

import com.reedelk.runtime.api.commons.ImmutableMap;
import com.reedelk.runtime.api.exception.PlatformException;
import com.reedelk.runtime.api.message.content.DataRow;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestDataRow implements DataRow<Serializable> {

    private final List<String> columns;
    private final List<Serializable> values;

    static DataRow<Serializable> create(List<String> columns, List<Serializable> values) {
        return new TestDataRow(columns, values);
    }

    private TestDataRow(List<String> columns, List<Serializable> values) {
        this.columns = columns;
        this.values = values;
    }

    @Override
    public Map<String, Serializable> attributes() {
        return ImmutableMap.of();
    }

    @Override
    public Serializable attribute(String name) {
        return null;
    }

    @Override
    public int columnCount() {
        return columns.size();
    }

    @Override
    public String columnName(int i) {
        return columns.get(i - 1);
    }

    @Override
    public List<String> columnNames() {
        return columns;
    }

    @Override
    public Serializable get(int i) {
        return values.get(i - 1);
    }

    @Override
    public Serializable getByColumnName(String columnName) {
        for (int i = 1; i <= columns.size(); i++) {
            if (columns.get(i).equals(columnName)) {
                return i;
            }
        }
        throw new PlatformException("Could not find column with name: " + columnName);
    }

    @Override
    public List<Serializable> values() {
        return Collections.unmodifiableList(values);
    }
}
