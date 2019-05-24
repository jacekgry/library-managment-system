package com.jacek.librarysystem.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "comments")
@Data
public class Comment {

    @Id
    private long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name="user")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name="book")
    private BookInLibrary bookInLibrary;

    @NotBlank
    private String content;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;
}
