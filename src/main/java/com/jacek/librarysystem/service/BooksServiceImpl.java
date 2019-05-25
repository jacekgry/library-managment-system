package com.jacek.librarysystem.service;

import com.jacek.librarysystem.dto.ReadingStats;
import com.jacek.librarysystem.model.Book;
import com.jacek.librarysystem.model.BookInLibrary;
import com.jacek.librarysystem.model.Reading;
import com.jacek.librarysystem.model.User;
import com.jacek.librarysystem.repository.BookInLibraryRepository;
import com.jacek.librarysystem.repository.BookRepository;
import com.jacek.librarysystem.repository.ReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BooksServiceImpl implements BooksService {

    private final BookRepository bookRepository;

    private final BookInLibraryRepository bookInLibraryRepository;

    private final ReadingRepository readingRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public List<BookInLibrary> getAllBooksInLibrary(User user) {
        return bookInLibraryRepository.findAllByLibraryOwner(user);
    }

    @Override
    @Transactional
    public void addBookToLibrary(User loggedUser, Long bookId) {
        Book book = bookRepository.findById(bookId).get();
        BookInLibrary bookInLibrary = BookInLibrary.builder().book(book).libraryOwner(loggedUser).build();
        bookInLibraryRepository.save(bookInLibrary);
    }

    @Override
    public ReadingStats calcStats(User user) {
        List<Reading> readings = readingRepository.getAllByUser(user);

        int pages = readings.stream()
                .filter(r -> r.getEndDate() != null)
                .map(r -> r.getBook().getBook().getPages())
                .mapToInt(i -> i.intValue())
                .sum();

        long books = readings.stream()
                .filter(r -> r.getEndDate() != null)
                .count();

        return ReadingStats.builder()
                .avgBooksPerMonth(user.getMonthsSinceRegistration() == 0 ? BigDecimal.ZERO : new BigDecimal(books / user.getMonthsSinceRegistration()).setScale(2))
                .avgBooksPerYear(user.getYearsSinceRegistration() == 0 ? BigDecimal.ZERO : new BigDecimal(books / user.getYearsSinceRegistration()).setScale(2))
                .avgPagesPerDay(user.getDaysSinceRegistration() == 0 ? BigDecimal.ZERO : new BigDecimal(pages / user.getDaysSinceRegistration()).setScale(2))
                .build();
    }

    @Override
    public Book getLastReadBookOfUser(User user) {
        List<Reading> readings = readingRepository.getAllByUser(user);
        Optional<Reading> lastReading = readings.stream()
                .filter(r -> r.getEndDate() != null)
                .sorted(Comparator.comparing(Reading::getEndDate))
                .findFirst();

        if (lastReading.isPresent()) {
            return lastReading.get()
                    .getBook()
                    .getBook();
        } else {
            return null;
        }
    }

}