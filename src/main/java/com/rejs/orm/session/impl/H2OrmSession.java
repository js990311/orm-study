package com.rejs.orm.session.impl;

import com.rejs.orm.annotations.Column;
import com.rejs.orm.annotations.Id;
import com.rejs.orm.session.OrmSession;
import com.rejs.orm.session.metadata.EntityMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
        EntityMetadata metadata = EntityMetadata.from(clazz);

        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            metadata.buildInsertSql(), new String[]{metadata.getIdColumnName()}
                    );
                    for(int i=0;i<metadata.getFields().size();i++) {
                        Field col = metadata.getFields().get(i);
                        ps.setObject(i+1, EntityMetadata.getFieldValue(col, entity));
                    }
                    return ps;
                },
                keyHolder
        );

        Long id = keyHolder.getKey().longValue();
        EntityMetadata.setFieldValue(metadata.getIdField(), entity, id);
    }

    @Override
    public <T> T readById(Class<T> clazz, Long id) {
        EntityMetadata metadata = EntityMetadata.from(clazz);

        String sql = metadata.buildSelectSql();

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                T entity = null;
                try {
                    entity = clazz.getDeclaredConstructor().newInstance();
                    List<Field> fields = metadata.getFields();

                    EntityMetadata.setFieldValue(metadata.getIdField(), entity, rs.getObject(metadata.getIdColumnName()));
                    for(Field field : fields){
                        Object object = rs.getObject(EntityMetadata.camel2Snake(field.getName()));
                        EntityMetadata.setFieldValue(field, entity, object);
                    }
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                return entity;
            }, id);
        }catch (EmptyResultDataAccessException ex){
            return null;
        }
    }
}
