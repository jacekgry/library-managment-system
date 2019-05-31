package com.jacek.librarysystem.controller;

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
        Set<User> availLibrariesOwners = userService.findUsersWhoGaveAccessToTheirLibrariesAndConfirmed(securityService.findLoggedInUser());
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
    public String library(Model model, @RequestParam(name = "owner") String username,
                          @RequestParam(name = "title", defaultValue = "") String title,
                          @RequestParam(name = "author", defaultValue = "") String author) {
        User user = userService.findByUsername(username);
        if (securityService.findLoggedInUser().equals(user) ||
                securityService.findLoggedInUser().getAccessibleUsers().contains(user)) {
            model.addAttribute("books", booksService.getAllBooksInLibrary(user, title, author));
            model.addAttribute("title", title);
            model.addAttribute("author", author);
            model.addAttribute("owner", securityService.findLoggedInUsername().equals(username));
            model.addAttribute("invitations", userService.findSentInvitations(user));
            model.addAttribute("borrowed", booksService.getBorrowedBooks(user));
            model.addAttribute("users", userService.findAllByUsernameIsNotAndConfirmed(securityService.findLoggedInUsername(), true));
            return "library";
        } else {
            throw new AccessDeniedException("You have no access to this library");
        }
    }
}
