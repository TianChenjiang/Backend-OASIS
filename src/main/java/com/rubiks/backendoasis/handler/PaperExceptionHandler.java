package com.rubiks.backendoasis.handler;

import com.rubiks.backendoasis.exception.NoSuchYearException;
import com.rubiks.backendoasis.response.WrongResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PaperExceptionHandler {
    @ExceptionHandler(NoSuchYearException.class)
    public ResponseEntity<?> handleNoSuchYear(RuntimeException e) {
        return new ResponseEntity<>(new WrongResponse(10000, e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
