package com.jacek.librarysystem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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
    private User libraryOwner;

    @ManyToOne
    @JoinColumn(name = "book")
    private Book book;

    @OneToMany(mappedBy = "bookInLibrary")
    private List<Comment> comments;

    @OneToMany(mappedBy = "bookInLibrary")
    private List<Hire> hires;

}
