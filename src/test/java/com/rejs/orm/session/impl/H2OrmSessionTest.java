package com.rejs.orm.session.impl;

import com.rejs.orm.domain.user.entity.User;
import com.rejs.orm.session.OrmSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class H2OrmSessionTest {
    @Autowired
    private OrmSession ormSession;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void create() {
        // g
        String username = "username";
        String password = "password";
        User user = new User(username, password);

        assertNull(user.getId());

        // w
        ormSession.create(user);

        // t
        Long id = user.getId();
        assertNotNull(id);

        String name = jdbcTemplate.queryForObject(
                "SELECT username FROM users WHERE id = ?",
                String.class,
                id
        );

        assertEquals(username, name);
    }

    @Test
    void readById(){
        // g
        String username = "username";
        String password = "password";
        User user = new User(username, password);
        ormSession.create(user);
        Long id = user.getId();

        // w
        User readUser = ormSession.readById(User.class, id);

        // t
        assertNotNull(readUser);
        assertEquals(user.getId(), readUser.getId());
        assertEquals(user.getUsername(), readUser.getUsername());
        assertEquals(user.getPassword(), readUser.getPassword());
    }

    @DisplayName("readById에서 null이 있는 경우")
    @Test
    void readByIdButNullCase(){
        // g
        String username = "username";
        String password = "password";
        User user = new User(username, password);
        ormSession.create(user);
        Long id = user.getId();

        // w
        User readUser = ormSession.readById(User.class, id+1L); // 존재하지 않는 ID 조회

        // t
        assertNull(readUser);
    }
}