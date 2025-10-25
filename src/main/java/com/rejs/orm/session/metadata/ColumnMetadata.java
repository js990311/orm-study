package com.rejs.orm.session.metadata;

import com.rejs.orm.annotations.Column;
import com.rejs.orm.annotations.Id;
import com.rejs.orm.session.metadata.utils.NamingUtils;
import lombok.Getter;

import java.lang.reflect.Field;

@Getter
public class ColumnMetadata {
    private Field field;
    private String columnName;
    private boolean isId;

    public Object get(Object entity){
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }finally {
            field.setAccessible(false);
        }
    }

    public void set(Object entity, Object value){
        try {
            field.setAccessible(true);
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }finally {
            field.setAccessible(false);
        }
    }

    public ColumnMetadata(Field field, String columnName, boolean isId) {
        this.field = field;
        this.columnName = columnName;
        this.isId = isId;
    }

    public static ColumnMetadata from(Field field){
        boolean isId = false;
        if(field.isAnnotationPresent(Id.class)){
            isId = true;
        }
        String columnName = NamingUtils.camelToSnake(field.getName());
        return new ColumnMetadata(field, columnName, isId);
    }
}
