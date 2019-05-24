package com.jacek.librarysystem.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@Table(name="hires")
public class Hire {

    @Id
    private long id;

    @ManyToOne
    @JoinColumn(name = "book")
    private BookInLibrary bookInLibrary;

    private boolean outside;

    @ManyToOne
    @JoinColumn(name = "borrower")
    private User borrower;

    @NotNull
    @Temporal(value = TemporalType.DATE)
    private Date startDate;

    @Temporal(value = TemporalType.DATE)
    private Date endDate;

}
