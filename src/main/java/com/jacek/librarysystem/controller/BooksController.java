package com.jacek.librarysystem.controller;

import com.jacek.librarysystem.model.Book;
import com.jacek.librarysystem.model.BookInLibrary;
import com.jacek.librarysystem.model.Hire;
import com.jacek.librarysystem.model.User;
import com.jacek.librarysystem.security.SecurityService;
import com.jacek.librarysystem.service.BooksService;
import com.jacek.librarysystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class BooksController {

    private final BooksService booksService;

    private final UserService userService;

    private final SecurityService securityService;

    @GetMapping(value = "/books")
    public String books(Model model,
                        @RequestParam(name = "title", required = false, defaultValue = "") String title,
                        @RequestParam(name= "author", required = false, defaultValue = "") String author) {
        Set<User> users = securityService.findLoggedInUser().getAccessibleUsers();
        users.add(securityService.findLoggedInUser());

        model.addAttribute("books", booksService.findBooksByTitleAndAuthor(title, author));
        model.addAttribute("users", users);

        model.addAttribute("title", title);
        model.addAttribute("author", author);

        return "books";
    }

    @GetMapping(value = "add/book")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "add_book";
    }

    @PostMapping(value = "add/book")
    public String addBook(@ModelAttribute(name = "book") @Valid Book book,
                          BindingResult bindingResult,
                          @RequestParam(name = "force", defaultValue = "false") Boolean force,
                          Model model) {
        if (bindingResult.hasErrors()) {
            return "add_book";
        } else {
            boolean duplicatesFound = false;
            List<Book> duplicates = null;
            if (!force) {
                duplicates = booksService.searchForPossibleDuplicate(book);
                duplicatesFound = !duplicates.isEmpty();
            }
            if (force || !duplicatesFound) {
                booksService.createBook(book);
                return "redirect:/books";
            } else {
                model.addAttribute("book", book);
                model.addAttribute("duplicates", duplicates);
                return "add_book";
            }
        }
    }

    @GetMapping(value = "hiring")
    public String hiringHistory(Model model, @RequestParam(name = "id") Long id) {
        BookInLibrary book = booksService.getBookInLibraryById(id);
        if (securityService.findLoggedInUser().equals(book.getBookOwner())
                || book.getLentTo().equals(securityService.findLoggedInUser())
                || book.getBookOwner().getAccessibleUsers().contains(securityService.findLoggedInUser())) {
            List<Hire> hirings = booksService.getHiringHistory(book);
            model.addAttribute("hires", hirings);
            model.addAttribute("book", book);
            return "hiring_hist";
        }
        throw new AccessDeniedException("You have no right to this book");
    }

    @PostMapping(value = "toggle/book")
    public String toggleBook(@RequestParam(name = "id") Long id) throws AccessDeniedException {
        booksService.toggleBook(securityService.findLoggedInUser(), id);
        return "redirect:/library?owner=" + securityService.findLoggedInUsername();
    }

    @PostMapping(value = "comment")
    public String comment(@RequestParam(name = "bookId") Long bookId, @RequestParam(name = "content") String content) {
        BookInLibrary book = booksService.getBookInLibraryById(bookId);
        if (securityService.findLoggedInUser().equals(book.getBookOwner())) {
            booksService.addComment(book, content);
            return "redirect:/book_in_lib?id=" + bookId;
        }
        throw new AccessDeniedException("You cannot comment this book");
    }

    @GetMapping(value = "/book_in_lib")
    public String bookInLibrary(Model model, @RequestParam(name = "id") Long id) {
        BookInLibrary book = booksService.getBookInLibraryById(id);
        if (securityService.findLoggedInUser().equals(book.getBookOwner())
                || securityService.findLoggedInUser().getAccessibleUsers().contains(book.getBookOwner()) ||
                book.getLentTo().equals(securityService.findLoggedInUser())) {
            model.addAttribute("bookInLibrary", book);
            model.addAttribute("owner", securityService.findLoggedInUser().equals(book.getBookOwner()));
            return "book_in_lib";
        } else {
            throw new AccessDeniedException("You have no access to this book");
        }
    }

    @PostMapping(value = "/lent/book")
    public String lentBook(@RequestParam(name = "username") String username, @RequestParam(name = "bookId") Long bookId, @RequestParam(name = "outside", defaultValue = "false") Boolean outside) {
        BookInLibrary book = booksService.getBookInLibraryById(bookId);
        if (securityService.findLoggedInUser().equals(book.getBookOwner())) {
            booksService.lentBook(book, username, outside);
            return "redirect:/library?owner=" + securityService.findLoggedInUsername();
        }
        throw new AccessDeniedException("You have no right to lent this book");
    }

    @PostMapping(value = "return/book")
    public String returnBook(@RequestParam(name = "bookId") Long bookId) throws AccessDeniedException {
        booksService.returnBook(securityService.findLoggedInUser(), bookId);
        return "redirect:/library?owner=" + securityService.findLoggedInUsername();
    }

    @PostMapping(value = "/add/book/to/library")
    public String addBookToLib(@RequestParam(name = "bookId") Long bookId,
                               @RequestParam(name = "username") String username,
                               @RequestParam(name = "outside", defaultValue = "false") boolean outside) {
        User user = userService.findByUsername(username);
        if (securityService.findLoggedInUser().equals(user) || securityService.findLoggedInUser().getAccessibleUsers().contains(user)) {
            booksService.addBookToLibrary(user, bookId, outside);
            return "redirect:/books";
        }
        throw new AccessDeniedException("You have no right to this library!");
    }


}
