package com.jacek.librarysystem.repository;

import com.jacek.librarysystem.model.Book;
import com.jacek.librarysystem.model.BookInLibrary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookInLibraryRepository extends JpaRepository<BookInLibrary, Long> {


}
