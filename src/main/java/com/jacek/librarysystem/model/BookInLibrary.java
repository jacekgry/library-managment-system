package com.jacek.librarysystem.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "book_in_library")
public class BookInLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private User libraryOwner;

    private Book book;

    @OneToMany
    private List<Comment> comments;





}
