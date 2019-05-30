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
@Table(name="hires")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY  )
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
