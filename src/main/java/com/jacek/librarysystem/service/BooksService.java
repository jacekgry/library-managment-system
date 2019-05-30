package com.jacek.librarysystem.service;


import com.jacek.librarysystem.dto.ReadingStats;
import com.jacek.librarysystem.model.Book;
import com.jacek.librarysystem.model.BookInLibrary;
import com.jacek.librarysystem.model.Hire;
import com.jacek.librarysystem.model.User;

import java.util.List;
import java.util.Map;

public interface BooksService {
    List<Book> getAllBooks();
    List<BookInLibrary> getAllBooksInLibrary(User user);
    void addBookToLibrary(User loggedUser, Long bookId);
    ReadingStats calcStats(User user);
    Book getLastReadBookOfUser(User user);
    List<BookInLibrary> getBorrowedBooks(User user);
    BookInLibrary getBookInLibraryById(Long id);
    List<Hire> getHiringHistory(BookInLibrary book);
    void toggleBook(User user, Long bookId);
    void addComment(BookInLibrary book, String content);
}