package com.rejs.orm.session.metadata;

import com.rejs.orm.annotations.Column;
import com.rejs.orm.annotations.Id;
import com.rejs.orm.session.metadata.utils.NamingUtils;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
public class EntityMetadata {
    private String tableName;
    private Field idField;
    private String idColumnName;
    private List<Field> fields;

    public EntityMetadata(String tableName, Field idField, String idColumnName, List<Field> fields) {
        this.tableName = tableName;
        this.idField = idField;
        this.idColumnName = idColumnName;
        this.fields = fields;
    }

    public String buildInsertSql(){
        StringBuilder sb = new StringBuilder();
        sb
                .append("INSERT INTO ")
                .append(tableName);

        StringBuilder columnString = new StringBuilder(" (");
        StringBuilder valuesString = new StringBuilder("VALUES (");

        for(int i=0;i<fields.size();i++){
            columnString.append(NamingUtils.camelToSnake(fields.get(i).getName()));
            valuesString.append("?");

            if(i != fields.size()-1){
                columnString.append(",");
                valuesString.append(",");
            }
        }

        columnString.append(") ");
        valuesString.append(")");
        sb.append(columnString).append(valuesString);
        return sb.toString();
    }

    public String buildSelectSql(){
        StringBuilder sb = new StringBuilder("SELECT ");
        StringBuilder columnString = new StringBuilder();

        columnString.append(idColumnName).append(",");
        for(int i=0;i<fields.size();i++){
            columnString.append(NamingUtils.camelToSnake(fields.get(i).getName()));

            if(i != fields.size()-1){
                columnString.append(",");
            }
        }

        sb
                .append(columnString)
                .append(" FROM ").append(tableName)
                .append(" WHERE ").append(idColumnName).append(" = ?");
        return sb.toString();
    }

    public static EntityMetadata from(Class<?> clazz){
        String tableName = NamingUtils.camelToSnake(clazz.getSimpleName()) + "s";

        Field[] fields = clazz.getDeclaredFields();
        List<Field> columns = new ArrayList<>();
        Field idField = null;

        for (Field field : fields){
            if(field.isAnnotationPresent(Id.class)){
                idField = field;
            }
            else if(field.isAnnotationPresent(Column.class)){
                columns.add(field);
            }
        }

        String idColumnName = NamingUtils.camelToSnake(idField.getName());
        return new EntityMetadata(tableName, idField, idColumnName, columns);
    }

    public static Object getFieldValue(Field field, Object entity){
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }finally {
            field.setAccessible(false);
        }
    }

    public static void setFieldValue(Field field, Object entity, Object value){
        try {
            field.setAccessible(true);
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }finally {
            field.setAccessible(false);
        }
    }
}
