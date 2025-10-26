package com.rejs.orm.session.impl;

import com.rejs.orm.annotations.Column;
import com.rejs.orm.annotations.Id;
import com.rejs.orm.session.OrmSession;
import com.rejs.orm.session.metadata.ColumnMetadata;
import com.rejs.orm.session.metadata.EntityMetadata;
import com.rejs.orm.session.metadata.utils.NamingUtils;
import com.rejs.orm.session.registry.EntityMetadataRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.parser.Entity;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class H2OrmSession implements OrmSession {
    private final EntityMetadataRegistry registry;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void create(Object entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Class<?> clazz = entity.getClass();
        EntityMetadata metadata = registry.get(clazz);

        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            metadata.buildInsertSql(), new String[]{metadata.getIdField().getColumnName()}
                    );
                    int idx=1;
                    for(ColumnMetadata column : metadata.getFields()) {
                        if(column.isId()){
                            continue;
                        }
                        ps.setObject(idx, column.get(entity));
                        idx++;
                    }
                    return ps;
                },
                keyHolder
        );

        Long id = keyHolder.getKey().longValue();
        metadata.getIdField().set(entity, id);
    }

    @Override
    public <T> T readById(Class<T> clazz, Long id) {
        EntityMetadata metadata = registry.get(clazz);

        String sql = metadata.buildSelectSql();

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                T entity = null;
                try {
                    entity = clazz.getDeclaredConstructor().newInstance();
                    List<ColumnMetadata> fields = metadata.getFields();
                    for(ColumnMetadata field : fields){
                        Object object = rs.getObject(field.getColumnName());
                        field.set(entity, object);
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
