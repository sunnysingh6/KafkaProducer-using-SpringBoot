package com.learnkafka.libraryeventsproducer.domain;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
	
	@NotNull
	Integer bookId;
	@NotNull
	String bookAuthor;
	@NotNull
	String bookName;
	


}
