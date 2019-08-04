package com.jacek.librarysystem.exception;

public class EmailTakenException extends RuntimeException {
    public EmailTakenException(){
        super("Email already in use!");
    }
}
