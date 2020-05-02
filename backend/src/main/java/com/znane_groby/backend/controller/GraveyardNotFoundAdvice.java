package com.znane_groby.backend.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GraveyardNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(GraveyardNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String GraveyardNotFoundHandler(GraveyardNotFoundException ex) {
        return ex.getMessage();
    }
}
