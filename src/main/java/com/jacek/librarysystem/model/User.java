package com.jacek.librarysystem.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    private String username;

    @Email
    @Column(unique = true)
    private String email;

    @NotNull
    private String password;

    private boolean confirmed;

    private boolean ownsLibrary;

    @OneToMany(mappedBy = "guest")
    private Set<InvitationToLibrary> invitationsReceived;

    @OneToMany(mappedBy = "owner")
    private Set<InvitationToLibrary> invitationsSent;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date registrationDate;

    public Set<User> getAccessibleUsers() {
        return
                invitationsReceived.stream()
                        .filter(InvitationToLibrary::isConfirmed)
                        .map(InvitationToLibrary::getOwner)
                        .collect(Collectors.toSet());
    }

    public LocalDate getRegisteredAsLocalDate() {
        return new java.sql.Date(getRegistrationDate().getTime()).toLocalDate();
    }

    public long getDaysSinceRegistration() {
//        return Period.between(getRegisteredAsLocalDate(), LocalDate.now()).
        long days = ChronoUnit.DAYS.between(getRegisteredAsLocalDate(), LocalDate.now());
        if (days == 0) return 1;
        return days;
    }

    public int getMonthsSinceRegistration() {
        int months = Period.between(getRegisteredAsLocalDate(), LocalDate.now()).getMonths();
        if (months == 0) return 1;
        return months;
    }

    public int getYearsSinceRegistration() {
        int years = Period.between(getRegisteredAsLocalDate(), LocalDate.now()).getYears();
        if (years == 0) return 1;
        return years;
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
