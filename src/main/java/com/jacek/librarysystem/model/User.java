package com.jacek.librarysystem.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    private String name;

    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    private boolean confirmed;

    private boolean ownsLibrary;

    @ManyToMany( cascade = CascadeType.ALL)
    @JoinTable(name="lib_right",
                joinColumns = {@JoinColumn(name="owner")},
                inverseJoinColumns = {@JoinColumn(name="guest")})
    private Set<User> usersWhoGaveAccessToTheirLibrary;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="lib_right",
            joinColumns = {@JoinColumn(name="guest")},
            inverseJoinColumns = {@JoinColumn(name="owner")})
    private Set<User> usersGivenAccess;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", ownsLibrary=" + ownsLibrary +
                '}';
    }
}
