package com.jacek.librarysystem.exception;

public class BookAlreadyLentException extends RuntimeException {
    public BookAlreadyLentException(String username, boolean outside){
        super("Book is already lent" + (outside ? "outside" : "to ") + username);
    }
}
