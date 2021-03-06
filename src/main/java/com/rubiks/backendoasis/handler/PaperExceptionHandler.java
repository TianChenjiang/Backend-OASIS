package com.rubiks.backendoasis.handler;

import com.rubiks.backendoasis.exception.FileFormatNotSupportException;
import com.rubiks.backendoasis.exception.NoSuchAuthorException;
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
        return new ResponseEntity<>(new WrongResponse(10001, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileFormatNotSupportException.class)
    public ResponseEntity<?> handleFileFormatNotSupport(RuntimeException e) {
        return new ResponseEntity<>(new WrongResponse(10002, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchAuthorException.class)
    public ResponseEntity<?> handleNoSuchAuthor(RuntimeException e) {
        return new ResponseEntity<>(new WrongResponse(10003, e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
