package dev.answer.alternative.utils;

public class AlternativeException extends Exception {
    public AlternativeException(String s) {
        super(s);
    }

    public AlternativeException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
