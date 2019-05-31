package com.jacek.librarysystem.service;


import com.jacek.librarysystem.dto.ReadingStats;
import com.jacek.librarysystem.model.*;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

public interface BooksService {
    List<Book> findBooksByTitleAndAuthor(String title, String author);
    List<BookInLibrary> getAllBooksInLibrary(User user, String title, String author);
    void addBookToLibrary(User loggedUser, Long bookId, boolean outside);
    ReadingStats calcStats(User user);
    Book getLastReadBookOfUser(User user);
    List<BookInLibrary> getBorrowedBooks(User user);
    BookInLibrary getBookInLibraryById(Long id);
    List<Hire> getHiringHistory(BookInLibrary book);
    void toggleBook(User user, Long bookId) throws AccessDeniedException;
    void addComment(BookInLibrary book, String content);
    void lentBook(BookInLibrary book, String username, boolean outside);
    void returnBook(User user, Long bookId) throws AccessDeniedException;
    List<Reading> getCurrentReads(User loggedInUser);
    List<BookInLibrary> getLentBooks(User loggedInUser);
    void createBook(Book book);
    List<Book> searchForPossibleDuplicate(Book book);
}