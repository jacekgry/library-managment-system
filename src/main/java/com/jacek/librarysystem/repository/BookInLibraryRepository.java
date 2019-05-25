package com.jacek.librarysystem.repository;

import com.jacek.librarysystem.model.Book;
import com.jacek.librarysystem.model.BookInLibrary;
import com.jacek.librarysystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookInLibraryRepository extends JpaRepository<BookInLibrary, Long> {

    List<BookInLibrary> findAllByLibraryOwner(User libraryOwner);

}
