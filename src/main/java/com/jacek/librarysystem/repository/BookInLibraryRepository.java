package com.jacek.librarysystem.repository;

import com.jacek.librarysystem.model.BookInLibrary;
import com.jacek.librarysystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookInLibraryRepository extends JpaRepository<BookInLibrary, Long> {
    List<BookInLibrary> findAllByBookOwner(User bookOwner);
    List<BookInLibrary> findByBookOwnerAndBookTitleIgnoreCaseContainingAndBookAuthorIgnoreCaseContaining(User bookOwner, String title, String author);
}
