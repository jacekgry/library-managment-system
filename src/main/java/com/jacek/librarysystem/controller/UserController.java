package com.jacek.librarysystem.controller;

import com.jacek.librarysystem.model.User;
import com.jacek.librarysystem.security.SecurityService;
import com.jacek.librarysystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;


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
    public String register(@ModelAttribute("user") User user, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "signup";
        }
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
//
//    @RequestMapping(value="signin", method = RequestMethod.POST)
//    public String login(@ModelAttribute("user") User user, BindingResult bindingResult, Model model){
//        securityService.autologin(user.getUsername(), user.getPassword());
//        return "redirect:/home";
//    }

    @RequestMapping(value={"/", "/home"}, method = RequestMethod.GET)
    public String home(Model model){
        return "home";
    }

}
