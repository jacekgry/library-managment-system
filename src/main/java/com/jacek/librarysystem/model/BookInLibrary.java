package com.jacek.librarysystem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
@Data
@Table(name = "book_in_library")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookInLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user")
    private User bookOwner;

    @ManyToOne
    @JoinColumn(name = "book")
    private Book book;

    @OneToMany(mappedBy = "bookInLibrary")
    private List<Comment> comments;

    @OneToMany(mappedBy = "bookInLibrary")
    private List<Hire> hires;

    @OneToMany(mappedBy="book")
    private List<Reading> readings;

    @Transient
    private boolean borrowedFromOutside;

    @Transient
    private boolean lentToOutside;

    @Transient
    private User borrowedFrom;

    @Transient
    private User lentTo;

    @Transient
    private boolean beingRead;

    public void setUpHires() {
        Optional<Hire> hireOptional = this.hires.stream()
                .filter(h -> h.getEndDate() != null)
                .findFirst();
        if (hireOptional.isPresent()) {
            Hire hire = hireOptional.get();
            if (hire.getBorrower() == null) {
                this.lentToOutside = true;
            } else if (hire.getBorrower().equals(this.bookOwner)) {
                this.borrowedFromOutside = true;
            } else {
                this.lentTo = hire.getBorrower();
            }
        }
    }

    @PostLoad
    public void checkIfIsBeingRead(){
        this.beingRead =
                this.readings.stream()
                .filter(r -> r.getEndDate() == null)
                .count() > 0;
    }
}
