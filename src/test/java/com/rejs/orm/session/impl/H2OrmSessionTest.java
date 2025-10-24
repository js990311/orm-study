package com.rejs.orm.session.impl;

import com.rejs.orm.domain.user.entity.User;
import com.rejs.orm.session.OrmSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class H2OrmSessionTest {
    private final OrmSession ormSession = new H2OrmSession();

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
                "SELECT user_name FROM users WHERE id = ?",
                String.class,
                id
        );

        assertEquals(username, name);
    }
}