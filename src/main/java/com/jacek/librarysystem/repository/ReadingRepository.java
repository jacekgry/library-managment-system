package com.jacek.librarysystem.repository;

import com.jacek.librarysystem.model.BookInLibrary;
import com.jacek.librarysystem.model.Reading;
import com.jacek.librarysystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReadingRepository extends JpaRepository<Reading, Long> {

    List<Reading> getAllByUser(User user);
    List<Reading> findAllByUserAndBook(User user, BookInLibrary book);
}
