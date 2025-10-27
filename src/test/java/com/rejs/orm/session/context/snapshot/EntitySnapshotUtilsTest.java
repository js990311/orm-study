package com.rejs.orm.session.context.snapshot;

import com.rejs.orm.domain.user.entity.User;
import com.rejs.orm.session.metadata.ColumnMetadata;
import com.rejs.orm.session.metadata.EntityMetadata;
import com.rejs.orm.session.registry.EntityMetadataRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EntitySnapshotUtilsTest {

    @Autowired
    private EntitySnapshotUtils snapshotUtils;
    @Autowired
    private EntityMetadataRegistry registry;

    @Test
    void copySnapshot() {
        // g
        String username = "username";
        String password = "password";
        User user = new User(username, password);

        // w
        Object[] snapshots = snapshotUtils.copySnapshot(User.class, user);

        // t
        EntityMetadata metadata = registry.get(User.class);

        List<ColumnMetadata> fields = metadata.getFields();

        assertEquals(fields.size(), snapshots.length);
        for(int i=0;i<fields.size();i++){
            assertEquals(
                    fields.get(i).get(user),
                    snapshots[i]
            );
        }
    }

    @DisplayName(value = "null에서 다른 value로 변했을 때")
    @Test
    void dirtyCheckBeforeNull() {
        // g
        String username = "username";
        String password = "password";
        User user = new User(username, password);
        EntityMetadata metadata = registry.get(User.class);

        Long updatedId = 10L; // 실제로는 id가 업데이트되지는 않겠지만

        // w
        Object[] snapshots = snapshotUtils.copySnapshot(User.class, user);
        metadata.getIdField().set(user, updatedId);

        // t
        List<UpdatedColumn> updatedColumns = snapshotUtils.dirtyCheck(User.class, snapshots, user);

        assertEquals(1, updatedColumns.size());
        assertEquals(metadata.getIdField().getColumnName(), updatedColumns.get(0).getColumnName());
        assertEquals(updatedId, updatedColumns.get(0).getColumnValue());
    }

    @DisplayName(value = "value가 null로 변경되었을 때")
    @Test
    void dirtyCheckAfterNull() {
        // g
        String username = "username";
        String password = "password";
        User user = new User(username, password);
        EntityMetadata metadata = registry.get(User.class);
        Long originalId = 123L;
        metadata.getIdField().set(user, originalId);

        // w
        Object[] snapshots = snapshotUtils.copySnapshot(User.class, user);
        metadata.getIdField().set(user, null);

        // t
        List<UpdatedColumn> updatedColumns = snapshotUtils.dirtyCheck(User.class, snapshots, user);

        assertEquals(1, updatedColumns.size());
        assertEquals(metadata.getIdField().getColumnName(), updatedColumns.get(0).getColumnName());
        assertNull(updatedColumns.get(0).getColumnValue());
    }

    @DisplayName(value = "value가 다른 값으로 변경되었을 때")
    @Test
    void dirtyCheckValueChange() {
        // g
        String username = "username";
        String password = "password";
        User user = new User(username, password);
        EntityMetadata metadata = registry.get(User.class);
        Long originalId = 123L;
        Long updatedId = 456L;
        metadata.getIdField().set(user, originalId);

        // w
        Object[] snapshots = snapshotUtils.copySnapshot(User.class, user);
        metadata.getIdField().set(user, updatedId);

        // t
        List<UpdatedColumn> updatedColumns = snapshotUtils.dirtyCheck(User.class, snapshots, user);

        assertEquals(1, updatedColumns.size());
        assertEquals(metadata.getIdField().getColumnName(), updatedColumns.get(0).getColumnName());
        assertEquals(updatedId, updatedColumns.get(0).getColumnValue());
    }

}