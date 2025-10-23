package com.rejs.orm.domain.user.entity;

import com.rejs.orm.annotations.Column;
import com.rejs.orm.annotations.Entity;
import com.rejs.orm.annotations.Id;

@Entity
public class User {
    @Id
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
