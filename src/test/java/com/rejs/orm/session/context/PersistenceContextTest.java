package com.rejs.orm.session.context;

import com.rejs.orm.domain.user.entity.User;
import com.rejs.orm.session.impl.H2OrmSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PersistenceContextTest {
    @Autowired
    private PersistenceContext persistenceContext;

    @Autowired
    private H2OrmSession session;

    @AfterEach
    void afterEach(){
        persistenceContext.clear();
    }

    @DisplayName("put 되어 있으므로 오브젝트 반환")
    @Test
    void get() {
        // g
        String username = "username";
        String password = "password";
        User user = new User(username, password);
        session.create(user);

        Long id = user.getId();
        persistenceContext.put(User.class, id, user);

        // w
        User cachedUser = (User) persistenceContext.get(User.class, id);

        // t
        assertNotNull(cachedUser);
        assertEquals(user, cachedUser);
    }

    @Test
    void getTwice() {
        // g
        String username = "username";
        String password = "password";
        User user = new User(username, password);
        session.create(user);

        Long id = user.getId();
        persistenceContext.put(User.class, id, user);

        // w
        User cachedUser1 = (User) persistenceContext.get(User.class, id);
        User cachedUser2 = (User) persistenceContext.get(User.class, id);

        // t
        assertEquals(user, cachedUser1);
        assertEquals(user, cachedUser2);
        assertEquals(cachedUser1, cachedUser2);
    }


    @DisplayName("put되지 않은 경우 NULL")
    @Test
    void getNull() {
        // g
        String username = "username";
        String password = "password";
        User user = new User(username, password);
        session.create(user);

        Long id = user.getId();

        // w
        Object cachedUser = persistenceContext.get(User.class, id);

        // t
        assertNull(cachedUser);
    }


    @Test
    void containTrue() {
        // g
        String username = "username";
        String password = "password";
        User user = new User(username, password);
        session.create(user);

        Long id = user.getId();
        persistenceContext.put(User.class, id, user);

        // w
        boolean isContain = persistenceContext.contain(User.class, id);

        // t
        assertTrue(isContain);
    }

    @Test
    void containFalse() {
        // g
        String username = "username";
        String password = "password";
        User user = new User(username, password);
        session.create(user);

        Long id = user.getId();

        // w
        boolean isContain = persistenceContext.contain(User.class, id);

        // t
        assertFalse(isContain);
    }

    @Test
    void size(){
        // g
        String username = "username";
        String password = "password";
        User user = new User(username, password);
        session.create(user);

        Long id = user.getId();
        persistenceContext.put(User.class, id, user);

        // w
        int size = persistenceContext.size();

        // t
        assertEquals(1, size);
    }

    @Test
    void remove(){
        // g
        String username = "username";
        String password = "password";
        User user = new User(username, password);
        session.create(user);

        Long id = user.getId();
        persistenceContext.put(User.class, id, user);

        // w
        persistenceContext.remove(User.class, id);
        boolean isContain = persistenceContext.contain(User.class, id);

        // t
        assertFalse(isContain);
    }

    @Test
    void clear(){
        // g
        String username = "username";
        String password = "password";
        User user = new User(username, password);
        session.create(user);

        Long id = user.getId();
        persistenceContext.put(User.class, id, user);

        // w
        persistenceContext.clear();
        boolean isContain = persistenceContext.contain(User.class, id);

        // t
        assertFalse(isContain);
    }

}