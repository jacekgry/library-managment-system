package com.jacek.librarysystem.repository;

import com.jacek.librarysystem.model.BookInLibrary;
import com.jacek.librarysystem.model.Hire;
import com.jacek.librarysystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HireRepository extends JpaRepository<Hire, Long> {
    List<Hire> findAllByBorrower(User borrower);
    List<Hire> findAllByBorrowerAndEndDateIsNull(User borrower);
    List<Hire> findAllByBookInLibrary(BookInLibrary book);
}
