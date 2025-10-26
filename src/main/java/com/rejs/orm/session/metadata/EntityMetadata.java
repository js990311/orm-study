package com.rejs.orm.session.metadata;

import com.rejs.orm.annotations.Column;
import com.rejs.orm.session.metadata.utils.NamingUtils;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
public class EntityMetadata {
    private String tableName;
    private ColumnMetadata idField;
    private List<ColumnMetadata> fields;

    public EntityMetadata(String tableName, ColumnMetadata idField, List<ColumnMetadata> fields) {
        this.tableName = tableName;
        this.idField = idField;
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
            if(fields.get(i).isId()){
                continue;
            }
            columnString.append(fields.get(i).getColumnName());
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

        columnString.append(idField.getColumnName()).append(",");
        for(int i=0;i<fields.size();i++){
            columnString.append(fields.get(i).getColumnName());

            if(i != fields.size()-1){
                columnString.append(",");
            }
        }

        sb
                .append(columnString)
                .append(" FROM ").append(tableName)
                .append(" WHERE ").append(idField.getColumnName()).append(" = ?");
        return sb.toString();
    }

    public static EntityMetadata from(Class<?> clazz){
        String tableName = NamingUtils.camelToSnake(clazz.getSimpleName()) + "s";

        Field[] fields = clazz.getDeclaredFields();
        List<ColumnMetadata> columns = new ArrayList<>();
        ColumnMetadata idField = null;

        for (Field field : fields){
            if(field.isAnnotationPresent(Column.class)){
                ColumnMetadata columnMetadata = ColumnMetadata.from(field);
                if(columnMetadata.isId()){
                    idField = columnMetadata;
                }
                columns.add(columnMetadata);
            }
        }

        return new EntityMetadata(tableName, idField, columns);
    }
}
