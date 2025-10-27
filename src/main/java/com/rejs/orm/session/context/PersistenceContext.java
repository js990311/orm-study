package com.rejs.orm.session.context;

import com.rejs.orm.session.context.id.EntityKey;
import com.rejs.orm.session.context.snapshot.EntitySnapshotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@RequiredArgsConstructor
@Component
public class PersistenceContext {
    private HashMap<EntityKey, Object> entityCache = new HashMap<>();
    private HashMap<EntityKey, Object[]> entitySnapshot = new HashMap<>();
    private final EntitySnapshotUtils snapshotUtils;

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

        // 스냅샷 저장
        Object[] snapshots = snapshotUtils.copySnapshot(clazz, entity);
        entitySnapshot.put(entityKey, snapshots);
    }

    public void remove(Class<?> clazz, Long id){
        EntityKey entityKey = new EntityKey(clazz, id);
        entityCache.remove(entityKey);

        // 스냅샷 제거
        entitySnapshot.remove(entityKey);
    }

    public void clear(){
        entityCache.clear();
        // 스냅샷 초기화
        entitySnapshot.clear();
    }

    public int size(){
        return entityCache.size();
    }
}
