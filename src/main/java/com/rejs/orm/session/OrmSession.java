package com.rejs.orm.session;

public interface OrmSession {
    void create(Object entity);
    <T> T readById(Class<T> clazz, Long id);
}
