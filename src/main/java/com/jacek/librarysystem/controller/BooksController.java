package com.jacek.librarysystem.controller;

import com.jacek.librarysystem.model.Book;
import com.jacek.librarysystem.model.BookInLibrary;
import com.jacek.librarysystem.model.Hire;
import com.jacek.librarysystem.model.User;
import com.jacek.librarysystem.security.SecurityHandler;
import com.jacek.librarysystem.security.SecurityService;
import com.jacek.librarysystem.service.BooksService;
import com.jacek.librarysystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BooksController {

    private final BooksService booksService;

    private final UserService userService;

    private final SecurityService securityService;

    @GetMapping(value = "/books")
    public String books(Model model) {
        model.addAttribute("books", booksService.getAllBooks());
        return "books";
    }


    @PostMapping(value = "add_to_my_lib")
    public String addToMyLibrary(@RequestParam(name = "book") Long bookId) {
        booksService.addBookToLibrary(securityService.findLoggedInUser(), bookId);
        return "redirect:/library?owner=" + securityService.findLoggedInUser().getUsername();
    }

    @GetMapping(value = "add_book")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "add_book";
    }

    @GetMapping(value = "hiring")
    public String hiringHistory(Model model, @RequestParam(name = "id") Long id) {
        BookInLibrary book = booksService.getBookInLibraryById(id);
        if (securityService.findLoggedInUser().equals(book.getBookOwner())
                || book.getBookOwner().getAccessibleUsers().contains(securityService.findLoggedInUser())) {
            List<Hire> hirings = booksService.getHiringHistory(book);
            model.addAttribute("hires", hirings);
            model.addAttribute("book", book);
            return "hiring_hist";
        }
        throw new AccessDeniedException("You have to right to this book");
    }
}
