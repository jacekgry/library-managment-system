package com.jacek.librarysystem.controller;

import com.jacek.librarysystem.model.User;
import com.jacek.librarysystem.security.SecurityService;
import com.jacek.librarysystem.service.UserService;
import lombok.RequiredArgsConstructor;
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

    private final UserService userService;

    @GetMapping(value = "avail_libraries")
    public String availLibraries(Model model) {
        Set<User> availLibrariesOwners = securityService.findLoggedInUser()
                .getUsersWhoGaveAccessToTheirLibrary();
        model.addAttribute("users", availLibrariesOwners);
        return "avail_libraries";
    }

    @PostMapping(value = "grant_access")
    public String grantAccess(@RequestParam(name="username") String username){
        User userToBeGranted = userService.findByUsername(username);
        userService.grantAccess(securityService.findLoggedInUser(), userToBeGranted);
        return "redirect:/library/" + securityService.findLoggedInUsername();
    }

}
