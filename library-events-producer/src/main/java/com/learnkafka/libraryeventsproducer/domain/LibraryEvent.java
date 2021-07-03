package com.learnkafka.libraryeventsproducer.domain;


import javax.validation.Valid;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibraryEvent {

	public Integer libraryEventId;
	@NotNull
	@Valid
	public Book book;
	public LibraryEventType libraryEventType;
}
