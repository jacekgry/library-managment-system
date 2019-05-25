package com.jacek.librarysystem.service;


import com.jacek.librarysystem.dto.ReadingStats;
import com.jacek.librarysystem.model.Book;
import com.jacek.librarysystem.model.BookInLibrary;
import com.jacek.librarysystem.model.User;

import java.util.List;

public interface BooksService {

    List<Book> getAllBooks();
    List<BookInLibrary> getAllBooksInLibrary(User user);
    void addBookToLibrary(User loggedUser, Long bookId);
    ReadingStats calcStats(User user);
    Book getLastReadBookOfUser(User user);
}