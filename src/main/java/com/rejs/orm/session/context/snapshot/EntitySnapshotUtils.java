package com.rejs.orm.session.context.snapshot;

import com.rejs.orm.session.metadata.ColumnMetadata;
import com.rejs.orm.session.metadata.EntityMetadata;
import com.rejs.orm.session.registry.EntityMetadataRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class EntitySnapshotUtils {
    private final EntityMetadataRegistry entityMetadataRegistry;

    public Object[] copySnapshot(Class<?> clazz, Object entity){
        EntityMetadata metadata = entityMetadataRegistry.get(clazz);

        List<ColumnMetadata> fields = metadata.getFields();

        Object[] snapshots = new Object[fields.size()];

        for(int i=0; i<fields.size(); i++){
            ColumnMetadata columnMetadata = fields.get(i);
            snapshots[i] = columnMetadata.get(entity);
        }
        return snapshots;
    }

    public List<UpdatedColumn> dirtyCheck(Class<?> clazz, Object[] snapshots, Object entity){
        EntityMetadata metadata = entityMetadataRegistry.get(clazz);
        List<ColumnMetadata> fields = metadata.getFields();
        List<UpdatedColumn> changedColumns = new ArrayList<>();

        for(int i=0; i<fields.size(); i++){
            ColumnMetadata columnMetadata = fields.get(i);
            Object value = columnMetadata.get(entity);
            if(snapshots[i] == null && value != null){
                changedColumns.add(new UpdatedColumn(columnMetadata.getColumnName(), value));
            }else if(!snapshots[i].equals(value)){
                changedColumns.add(new UpdatedColumn(columnMetadata.getColumnName(), value));
            }
        }
        return changedColumns;
    }
}
