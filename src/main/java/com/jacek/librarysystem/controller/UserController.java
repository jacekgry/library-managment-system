package com.jacek.librarysystem.controller;

import com.jacek.librarysystem.dto.ReadingStats;
import com.jacek.librarysystem.model.Book;
import com.jacek.librarysystem.model.User;
import com.jacek.librarysystem.security.SecurityService;
import com.jacek.librarysystem.service.BooksService;
import com.jacek.librarysystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;
    private final BooksService booksService;


    @RequestMapping(value = "signup", method = RequestMethod.GET)
    public String register(Model model){
        if(SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated()&&
                !(SecurityContextHolder.getContext().getAuthentication()
                        instanceof AnonymousAuthenticationToken) ){
            return "redirect:/home";
        }
        model.addAttribute("user", new User());
        return "signup";
    }

    @RequestMapping(value = "signup", method = RequestMethod.POST)
    public String register(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "signup";
        }
        user.setConfirmed(false);
        userService.save(user);

        return "redirect:/signin";
    }

    @RequestMapping(value = "signin", method = RequestMethod.GET)
    public String login(Model model){
        model.addAttribute("user", new User());
        if(SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !(SecurityContextHolder.getContext().getAuthentication()
                instanceof AnonymousAuthenticationToken) ){
            return "redirect:/home";
        }
        return "signin";
    }

    @RequestMapping(value={"/", "/home"}, method = RequestMethod.GET)
    public String home(Model model){
        ReadingStats stats = booksService.calcStats(securityService.findLoggedInUser());
        Book lastBook = booksService.getLastReadBookOfUser(securityService.findLoggedInUser());
        model.addAttribute("stats", stats);
        model.addAttribute("lastBook", lastBook);
        return "home";
    }

    @GetMapping(value="/confirm")
    public String confirm(@RequestParam(name = "token") String token){
        userService.confirmEmail(token);
        return "redirect:/signin";
    }

}
