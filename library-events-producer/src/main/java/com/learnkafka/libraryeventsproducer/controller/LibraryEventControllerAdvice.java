package com.learnkafka.libraryeventsproducer.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class LibraryEventControllerAdvice<T> {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleRequestBody(MethodArgumentNotValidException ex){
		
		List<FieldError> list=(List<FieldError>) ex.getBindingResult().getFieldError();
		String errorMessage=list.stream().map(FieldError->FieldError.getField()+" "+FieldError.getDefaultMessage()).sorted()
		.collect(Collectors.joining(","));
		log.error("error mesg{}",errorMessage);
		return new ResponseEntity<>(errorMessage,HttpStatus.BAD_REQUEST);
	}

}
