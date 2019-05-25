package com.jacek.librarysystem.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    private String username;

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

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date registrationDate;

    public LocalDate getRegisteredAsLocalDate(){
        return new java.sql.Date(getRegistrationDate().getTime()).toLocalDate();
    }

    public int getDaysSinceRegistration(){
        return Period.between(LocalDate.now(), getRegisteredAsLocalDate()).getDays();
    }

    public int getMonthsSinceRegistration(){
        return Period.between(LocalDate.now(), getRegisteredAsLocalDate()).getMonths();
    }

    public int getYearsSinceRegistration(){
        return Period.between(LocalDate.now(), getRegisteredAsLocalDate()).getYears();
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", ownsLibrary=" + ownsLibrary +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
