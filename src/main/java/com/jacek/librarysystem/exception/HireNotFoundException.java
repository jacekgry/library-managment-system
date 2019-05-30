package com.jacek.librarysystem.exception;

public class HireNotFoundException extends RuntimeException {
    public HireNotFoundException(){
        super("Hire not found");
    }
}
