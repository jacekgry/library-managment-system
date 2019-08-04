package com.jacek.librarysystem.controller;

import com.jacek.librarysystem.dto.ReadingStats;
import com.jacek.librarysystem.dto.UserDto;
import com.jacek.librarysystem.exception.EmailTakenException;
import com.jacek.librarysystem.exception.UsernameTakenException;
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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
        model.addAttribute("user", new UserDto());
        return "signup";
    }

    @RequestMapping(value = "signup", method = RequestMethod.POST)
    public String register(@ModelAttribute("user") @Valid UserDto user, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "signup";
        }
        try {
            userService.save(user);
            return "redirect:/signup?sent";
        }
        catch(UsernameTakenException e){
            bindingResult.rejectValue("username","error.user", "Username already in use");
            return "signup";
        }
        catch (EmailTakenException e){
            bindingResult.rejectValue("email","error.user", "Email already in use");
            return "signup";
        }
    }

    @RequestMapping(value = "/signin", method = RequestMethod.GET)
    public String login(Model model){
        model.addAttribute("user", new UserDto());
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
        model.addAttribute("borrowed", booksService.getBorrowedBooks(securityService.findLoggedInUser()));
        model.addAttribute("reads", booksService.getCurrentReads(securityService.findLoggedInUser()));
        model.addAttribute("lent", booksService.getLentBooks(securityService.findLoggedInUser()));
        return "home";
    }

    @GetMapping(value="/confirm")
    public String confirm(@RequestParam(name = "token") String token){
        userService.confirmEmail(token);
        return "redirect:/signin";
    }

    @PostMapping(value="/cancel/access")
    public String cancelString(@RequestParam(name="id") Long invId){
        userService.cancelInvitation(securityService.findLoggedInUser(), invId);
        return "redirect:/library?owner=" + securityService.findLoggedInUsername();
    }

}
