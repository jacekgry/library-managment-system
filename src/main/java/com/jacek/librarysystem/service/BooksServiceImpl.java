package com.jacek.librarysystem.service;

import com.jacek.librarysystem.dto.ReadingStats;
import com.jacek.librarysystem.exception.BookAlreadyLentException;
import com.jacek.librarysystem.exception.BookDoesNotExistException;
import com.jacek.librarysystem.exception.HireNotFoundException;
import com.jacek.librarysystem.exception.HireToNobodyException;
import com.jacek.librarysystem.model.*;
import com.jacek.librarysystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BooksServiceImpl implements BooksService {

    private final BookRepository bookRepository;

    private final BookInLibraryRepository bookInLibraryRepository;

    private final ReadingRepository readingRepository;

    private final HireRepository hireRepository;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    @Override
    public List<Book> findBooksByTitleAndAuthor(String title, String author) {
        return bookRepository.findByTitleIgnoreCaseContainingAndAuthorIgnoreCaseContaining(title, author);
    }

    @Override
    public List<BookInLibrary> getAllBooksInLibrary(User user, String title, String author) {
        return bookInLibraryRepository.findByBookOwnerAndBookTitleIgnoreCaseContainingAndBookAuthorIgnoreCaseContaining(user, title, author);
    }

    @Override
    @Transactional
    public void addBookToLibrary(User loggedUser, Long bookId, boolean outside) {
        Book book = bookRepository.findById(bookId).get();
        BookInLibrary bookInLibrary = BookInLibrary.builder().book(book).bookOwner(outside ? null : loggedUser).build();
        bookInLibraryRepository.save(bookInLibrary);
        if (outside) {
            Hire hire = Hire.builder()
                    .outside(true)
                    .startDate(new Date())
                    .borrower(loggedUser)
                    .bookInLibrary(bookInLibrary)
                    .build();
            hireRepository.save(hire);
        }
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
                .avgBooksPerMonth(user.getMonthsSinceRegistration() == 0 ? BigDecimal.ZERO : new BigDecimal((double) books / user.getMonthsSinceRegistration()).setScale(2, RoundingMode.HALF_UP))
                .avgBooksPerYear(user.getYearsSinceRegistration() == 0 ? BigDecimal.ZERO : new BigDecimal((double) books / user.getYearsSinceRegistration()).setScale(2, RoundingMode.HALF_UP))
                .avgPagesPerDay(user.getDaysSinceRegistration() == 0 ? BigDecimal.ZERO : new BigDecimal((double) pages / user.getDaysSinceRegistration()).setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    @Override
    public Book getLastReadBookOfUser(User user) {
        List<Reading> readings = readingRepository.getAllByUser(user);
        Optional<Reading> lastReading = readings.stream()
                .filter(r -> r.getEndDate() != null)
                .sorted(Comparator.comparing(Reading::getEndDate).reversed())
                .findFirst();

        if (lastReading.isPresent()) {
            return lastReading.get()
                    .getBook()
                    .getBook();
        } else {
            return null;
        }
    }

    @Override
    public List<BookInLibrary> getBorrowedBooks(User user) {
        List<Hire> userHires = hireRepository.findAllByBorrowerAndEndDateIsNull(user);
        List<BookInLibrary> books = userHires
                .stream()
                .map(Hire::getBookInLibrary)
                .collect(Collectors.toList());
        return books;
    }

    @Override
    public BookInLibrary getBookInLibraryById(Long id) {
        return bookInLibraryRepository.findById(id).orElseThrow(() -> new BookDoesNotExistException());
    }

    @Override
    public List<Hire> getHiringHistory(BookInLibrary book) {
        List<Hire> hires = hireRepository.findAllByBookInLibrary(book);
        hires.sort(Comparator.comparing(Hire::getStartDate));
        return hireRepository.findAllByBookInLibrary(book);
    }

    @Override
    @Transactional
    public void toggleBook(User user, Long bookId) throws AccessDeniedException {
        BookInLibrary book = bookInLibraryRepository.findById(bookId).orElseThrow(BookDoesNotExistException::new);
        if (book.isOnOwnersShelf() && book.getBookOwner().equals(user) || book.getLentTo().equals(user)) {
            if (book.isBeingRead()) {
                stopAllReadings(book);
            } else {
                Reading reading = Reading.builder()
                        .book(book)
                        .startDate(new Date())
                        .user(user)
                        .build();
                readingRepository.save(reading);
            }
        } else {
            throw new AccessDeniedException("Book is not on your shelf!");
        }
    }

    @Override
    @Transactional
    public void addComment(BookInLibrary book, String content) {
        Comment comment = Comment.builder()
                .bookInLibrary(book)
                .content(content)
                .date(new Date())
                .build();
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void lentBook(BookInLibrary book, String username, boolean outside) {
        User borrower = userRepository.findByUsername(username).orElse(null);
        if (outside) borrower = null;
        if (borrower == null && !outside) {
            throw new HireToNobodyException("You have to specify who you lent the book to or lent it outside");
        }

        List<Hire> hires = book.getHires()
                .stream()
                .filter(h -> h.getEndDate() == null)
                .collect(Collectors.toList());

        if (hires.size() > 0) {
            throw new BookAlreadyLentException(hires.get(0).getBorrower().getUsername(), hires.get(0).isOutside());
        }

        stopAllReadings(book);

        Hire hire = Hire.builder()
                .bookInLibrary(book)
                .borrower(borrower)
                .startDate(new Date())
                .outside(outside)
                .build();
        hireRepository.save(hire);
    }

    @Override
    @Transactional
    public void returnBook(User user, Long bookId) throws AccessDeniedException {
        BookInLibrary book = bookInLibraryRepository.findById(bookId)
                .orElseThrow(BookDoesNotExistException::new);
        Hire hire = book.getHires().stream()
                .filter(h -> h.getEndDate() == null)
                .findFirst().orElseThrow(HireNotFoundException::new);
        if (!user.equals(book.getBookOwner()) && !user.equals(hire.getBorrower())) {
            throw new AccessDeniedException("You have no right to return this book");
        } else {
            stopAllReadings(book);
            hire.setEndDate(new Date());
            hireRepository.save(hire);
        }
    }

    @Override
    public List<Reading> getCurrentReads(User loggedInUser) {
        return readingRepository.getAllByUser(loggedInUser)
                .stream()
                .filter(r -> r.getEndDate() == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookInLibrary> getLentBooks(User loggedInUser) {
        return bookInLibraryRepository.findAllByBookOwner(loggedInUser)
                .stream()
                .filter(b -> !b.isOnOwnersShelf())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createBook(Book book) {
        bookRepository.save(book);
    }

    @Override
    public List<Book> searchForPossibleDuplicate(Book book) {
        return bookRepository.findByTitleIgnoreCaseAndAuthorIgnoreCase(book.getTitle(), book.getAuthor());
    }

    private void stopAllReadings(BookInLibrary book) {
        book.getReadings()
                .stream()
                .filter(r -> r.getEndDate() == null)
                .forEach(r -> {
                    r.setEndDate(new Date());
                    readingRepository.save(r);
                });
    }
}