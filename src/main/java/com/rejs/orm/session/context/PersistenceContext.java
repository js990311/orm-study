package com.rejs.orm.session.context;

import com.rejs.orm.session.context.id.EntityKey;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class PersistenceContext {
    private HashMap<EntityKey, Object> entityCache;

    public PersistenceContext() {
        this.entityCache = new HashMap<>();
    }

    public Object get(Class<?> clazz, Long id){
        EntityKey entityKey = new EntityKey(clazz, id);
        if(!entityCache.containsKey(entityKey)){
            return null;
        }
        return entityCache.get(entityKey);
    }

    public boolean contain(Class<?> clazz, Long id){
        EntityKey entityKey = new EntityKey(clazz, id);
        return entityCache.containsKey(entityKey);
    }

    public void put(Class<?> clazz, Long id, Object entity){
        EntityKey entityKey = new EntityKey(clazz, id);
        entityCache.put(entityKey, entity);
    }

    public void remove(Class<?> clazz, Long id){
        entityCache.remove(new EntityKey(clazz, id));
    }

    public void clear(){
        entityCache.clear();
    }

    public int size(){
        return entityCache.size();
    }
}
