package com.jacek.librarysystem.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@Table(name = "reading")
public class Reading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_in_library")
    private BookInLibrary book;

    @Temporal(value = TemporalType.DATE)
    @NotNull
    private Date startDate;

    @Temporal(value = TemporalType.DATE)
    private Date endDate;
}
