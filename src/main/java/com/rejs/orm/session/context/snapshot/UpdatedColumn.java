package com.rejs.orm.session.context.snapshot;

import lombok.Getter;

@Getter
public class UpdatedColumn {
    private final String columnName;
    private final Object columnValue;

    public UpdatedColumn(String columnName, Object columnValue) {
        this.columnName = columnName;
        this.columnValue = columnValue;
    }
}
