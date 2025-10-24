package com.rejs.orm.session.impl;

import com.rejs.orm.annotations.Column;
import com.rejs.orm.annotations.Id;
import com.rejs.orm.session.OrmSession;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class H2OrmSession implements OrmSession {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void create(Object entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        Class<?> clazz = entity.getClass();

        String tableName = camel2Snake(clazz.getSimpleName()) + "s";

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

        String idColName = camel2Snake(idField.getName());

        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO " + tableName + columnToSting(columns), new String[]{idColName}
                    );
                    for(int i=0;i<columns.size();i++) {
                        Field col = columns.get(i);
                        col.setAccessible(true);
                        try {
                            ps.setObject(i+1, col.get(entity));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }finally {
                            col.setAccessible(false);
                        }
                    }
                    return ps;
                },
                keyHolder
        );

        Long id = keyHolder.getKey().longValue();
        try {
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }finally {
            idField.setAccessible(false);
        }
    }

    private String columnToSting(List<Field> columns){
        String columnString = "(";
        String valuesString = "VALUES (";

        for(int i=0;i<columns.size();i++){
            columnString += camel2Snake(columns.get(i).getName());
            valuesString += "?";
            if(i != columns.size()-1){
                columnString += ",";
                valuesString += ",";
            }
        }

        columnString += ") ";
        valuesString += ")";
        return columnString + valuesString;
    }

    private String camel2Snake(String str){
        if(str == null || str.isEmpty()){
            return str;
        }
        return str.replace("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}
