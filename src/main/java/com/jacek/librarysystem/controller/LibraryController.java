package com.jacek.librarysystem.controller;

import com.jacek.librarysystem.model.BookInLibrary;
import com.jacek.librarysystem.model.User;
import com.jacek.librarysystem.security.SecurityService;
import com.jacek.librarysystem.service.BooksService;
import com.jacek.librarysystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class LibraryController {

    private final SecurityService securityService;
    private final BooksService booksService;

    private final UserService userService;

    @GetMapping(value = "avail_libraries")
    public String availLibraries(Model model) {
        Set<User> availLibrariesOwners = userService.findUsersWhoGaveAccessTo(securityService.findLoggedInUser());
        model.addAttribute("users", availLibrariesOwners);
        return "avail_libraries";
    }

    @PostMapping(value = "grant_access")
    public String grantAccess(@RequestParam(name = "username") String email) {
        userService.grantAccess(securityService.findLoggedInUser(), email);
        return "redirect:/library?owner=" + securityService.findLoggedInUsername();
    }

    @GetMapping(value = "accept")
    public String acceptInvitation(@RequestParam(name = "token") String token) {
        userService.acceptInvitation(securityService.findLoggedInUser(), token);
        return "redirect:/avail_libraries";
    }

    @GetMapping(value = "/library")
    public String library(Model model, @RequestParam(name = "owner") String username) {
        User user = userService.findByUsername(username);
        if (securityService.findLoggedInUser().equals(user) ||
                securityService.findLoggedInUser().getAccessibleUsers().contains(user)) {
            model.addAttribute("books", booksService.getAllBooksInLibrary(user));
            model.addAttribute("owner", securityService.findLoggedInUsername().equals(username));
            return "library";
        } else {
            throw new AccessDeniedException("You have no access to this library");
        }
    }

    @GetMapping(value = "/book_in_lib")
    public String bookInLibrary(Model model, @RequestParam(name = "id") Long id) {
        BookInLibrary book = booksService.getBookInLibraryById(id);
        if (securityService.findLoggedInUser().equals(book.getBookOwner())
                || securityService.findLoggedInUser().getAccessibleUsers().contains(book.getBookOwner())) {
            model.addAttribute("bookInLibrary", book);
            model.addAttribute("owner", securityService.findLoggedInUser().equals(book.getBookOwner()));
            return "book_in_lib";
        } else {
            throw new AccessDeniedException("You have no access to this book");
        }
    }

}
