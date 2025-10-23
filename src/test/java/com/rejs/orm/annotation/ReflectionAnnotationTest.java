package com.rejs.orm.annotation;

import com.rejs.orm.annotations.Column;
import com.rejs.orm.annotations.Entity;
import com.rejs.orm.annotations.Id;
import com.rejs.orm.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReflectionAnnotationTest {

    @DisplayName("User class에 대한 리플렉션")
    @Test
    void reflectionUser(){
        Class<User> clazz = User.class;

        assertTrue(clazz.isAnnotationPresent(Entity.class), "@Entity 어노테이션 확인");

        Field[] fields = clazz.getDeclaredFields();
        List<Field> annotatedField = new ArrayList<>();
        Field idField = null;

        for (Field field : fields){
            if(field.isAnnotationPresent(Column.class)){
                annotatedField.add(field);
            }
            if(field.isAnnotationPresent(Id.class)){
                idField = field;
            }
        }
        assertEquals(3, annotatedField.size());

        assertNotNull(idField);
    }
}
