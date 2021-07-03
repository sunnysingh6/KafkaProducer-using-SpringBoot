package com.learnkafka.libraryeventsproducer.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.libraryeventsproducer.domain.LibraryEvent;
import com.learnkafka.libraryeventsproducer.domain.LibraryEventType;
import com.learnkafka.libraryeventsproducer.producer.LibraryEventProducer;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class LibraryEventController {

	@Autowired	
	LibraryEventProducer libraryEventProducer;
		
	
	@PostMapping({"/v1/libraryevent"})
	public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent ) throws JsonProcessingException{
		log.info("before sending librarry event");
		libraryEvent.setLibraryEventType(LibraryEventType.NEW);
		libraryEventProducer.sendLibraryEvent_approach2(libraryEvent);
		log.info("after sending library event");
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
		
	}
	
	@PutMapping({"/v1/libraryevent"})
	public ResponseEntity<?> updateLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent ) throws JsonProcessingException{
		
		if(libraryEvent.getLibraryEventId()==null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("please pass the Library Event Id");
		}
			
		libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
		libraryEventProducer.sendLibraryEvent_approach2(libraryEvent);
		log.info("after sending librarry event");
		return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
		
	}
}
