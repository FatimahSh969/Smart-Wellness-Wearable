package com._wellness.wellness.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, String>> invalid() {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid request"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> generic() {
        return ResponseEntity.status(500).body(Map.of("error", "Internal error"));
    }
}