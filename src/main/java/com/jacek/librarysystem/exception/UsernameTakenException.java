package com.jacek.librarysystem.exception;

public class UsernameTakenException extends RuntimeException {
    public UsernameTakenException(){
        super("Username already taken");
    }
}
