package com.jacek.librarysystem.exception;

public class BookDoesNotExistException extends RuntimeException {
    public BookDoesNotExistException(){
        super("Such a book does not exist");
    }
}
