package com.jacek.librarysystem.controller;

import com.jacek.librarysystem.dto.UserDto;
import com.jacek.librarysystem.exception.BookAlreadyLentException;
import com.jacek.librarysystem.exception.InvitationNotFoundException;
import com.jacek.librarysystem.exception.TokenNotFoundException;
import com.jacek.librarysystem.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ErrorController {
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException e) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", e.getMessage());
        e.printStackTrace();
        return modelAndView;
    }

    @ExceptionHandler(InvitationNotFoundException.class)
    public ModelAndView invitationNotFound(InvitationNotFoundException e){
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", e.getMessage());
        e.printStackTrace();
        return modelAndView;
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ModelAndView handleInvalidToken(TokenNotFoundException e){
        ModelAndView modelAndView = new ModelAndView("signin");
        modelAndView.addObject("user", new UserDto());
        modelAndView.addObject("error", e.getMessage());
        e.printStackTrace();
        return modelAndView;
    }

    @ExceptionHandler(BookAlreadyLentException.class)
    public ModelAndView badReq(BookAlreadyLentException e){
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", e.getMessage());
        e.printStackTrace();
        return modelAndView;
    }

}