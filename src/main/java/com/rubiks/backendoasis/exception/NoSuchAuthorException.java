package com.rubiks.backendoasis.exception;


import lombok.Data;

@Data
public class NoSuchAuthorException extends RuntimeException {
    private int code = 10003;
    public NoSuchAuthorException() {
        super("no such author");
    }

}
