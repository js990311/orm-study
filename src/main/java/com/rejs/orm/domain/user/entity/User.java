package com.rejs.orm.domain.user.entity;

import com.rejs.orm.annotations.Column;
import com.rejs.orm.annotations.Entity;
import com.rejs.orm.annotations.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class User {
    @Id
    @Column
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
