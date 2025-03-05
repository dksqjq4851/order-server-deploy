package com.example.ordersystem.common.controller;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;



//controller딴에서 발생하는 모든 예외를 모니터링하여 발생하는 예외를 intercepting
@ControllerAdvice
public class CommonExceptionHandler {
//    EntityNotFound예외가 발생할경우
    @ExceptionHandler(EntityNotFoundException.class)
//    매개변수로 예외가 주입
    public ResponseEntity<?> entityNotFound(EntityNotFoundException e){
        e.printStackTrace();
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> illegal(IllegalArgumentException e){
        e.printStackTrace();
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public  ResponseEntity<?> method(MethodArgumentNotValidException e){
        e.printStackTrace();
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessDenyException(AccessDeniedException e){
        e.printStackTrace();
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

}
