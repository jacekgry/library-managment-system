package com.jacek.librarysystem.controller;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ErrorController {
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleDataIntegrityException(AccessDeniedException e) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", e.getMessage());
        e.printStackTrace();
        return modelAndView;
    }
}