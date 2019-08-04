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
@Table(name = "reading")
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Temporal(value = TemporalType.TIMESTAMP)
    @NotNull
    private Date startDate;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date endDate;
}
