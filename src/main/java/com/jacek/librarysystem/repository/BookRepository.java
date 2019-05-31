package com.jacek.librarysystem.repository;

import com.jacek.librarysystem.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleIgnoreCaseAndAuthorIgnoreCase(String title, String author);
    List<Book> findByTitleIgnoreCaseContainingAndAuthorIgnoreCaseContaining(String title, String author);
}
