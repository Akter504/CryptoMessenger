package ru.java.maryan.cryptomessenger.exceptions;

public class ParseTokenException extends RuntimeException {

    public ParseTokenException(String message) {
        super(message);
    }

    public ParseTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}