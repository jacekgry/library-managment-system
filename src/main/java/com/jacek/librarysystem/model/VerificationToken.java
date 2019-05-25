package com.jacek.librarysystem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@Table(name="verification_token")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(targetEntity = User.class, fetch=FetchType.EAGER)
    @JoinColumn(name="user")
    @NotNull
    private User user;

    @Column(unique = true)
    private String token;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date expiryDate;

}
