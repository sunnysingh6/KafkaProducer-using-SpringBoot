package com.learnkafka.libraryeventsproducer.unit.controller.producer;

import static org.hamcrest.CoreMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.libraryeventsproducer.controller.LibraryEventController;
import com.learnkafka.libraryeventsproducer.domain.Book;
import com.learnkafka.libraryeventsproducer.domain.LibraryEvent;
import com.learnkafka.libraryeventsproducer.domain.LibraryEventType;
import com.learnkafka.libraryeventsproducer.producer.LibraryEventProducer;

@WebMvcTest(LibraryEventController.class)
@AutoConfigureMockMvc
public class LibraryControllerUnitTest {
	
	@Autowired
	MockMvc mockMvc;
	
	ObjectMapper objectMapper=new ObjectMapper();
	
	@MockBean
	LibraryEventProducer libraryEventProducer;
	
	@Test
	void postLibraryEvent() throws Exception {
		LibraryEvent libraryEvent=new LibraryEvent(null,new Book(123,"Dilip","Kafka using SpringBoot test"),LibraryEventType.NEW);
		
		String json=objectMapper.writeValueAsString(libraryEvent);
		when(libraryEventProducer.sendLibraryEvent_approach2((LibraryEvent) isA(LibraryEvent.class))).thenReturn(null);
		mockMvc.perform(post("/v1/libraryevent")
		.content(json)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated());
		
		
	}
	@Test
	void postLibraryEvent_4xx() throws Exception {
		Book book=new Book(null,null,"Kafka using SpringBoot test");
		LibraryEvent libraryEvent=new LibraryEvent(null,null,LibraryEventType.NEW);
		
		String json=objectMapper.writeValueAsString(libraryEvent);
		when(libraryEventProducer.sendLibraryEvent_approach2((LibraryEvent) isA(LibraryEvent.class))).thenReturn(null);
		mockMvc.perform(post("/v1/libraryevent")
		.content(json)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is4xxClientError())
		.andExpect((ResultMatcher) content().string("bookId is marked @NonNull but is null"));
		
		
	}
	@Test
	void updateLibraryEvent() throws Exception {

	    //given
		LibraryEvent libraryEvent=new LibraryEvent(null,new Book(123,"Dilip","Kafka using SpringBoot test"),LibraryEventType.NEW);
		
	    String json = objectMapper.writeValueAsString(libraryEvent);
	    when(libraryEventProducer.sendLibraryEvent_approach2((LibraryEvent) isA(LibraryEvent.class))).thenReturn(null);

	    //expect
	    mockMvc.perform(
	            put("/v1/libraryevent")
	                    .content(json)
	                    .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk());

	}
	
	@Test
	void updateLibraryEvent_withNullLibraryEventId() throws Exception {

	    //given
		LibraryEvent libraryEvent=new LibraryEvent(null,new Book(123,"Dilip","Kafka using SpringBoot test"),LibraryEventType.NEW);
		
	    String json = objectMapper.writeValueAsString(libraryEvent);
	    when(libraryEventProducer.sendLibraryEvent_approach2((LibraryEvent) isA(LibraryEvent.class))).thenReturn(null);

	    //expect
	    mockMvc.perform(
	            put("/v1/libraryevent")
	                    .content(json)
	                    .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().is4xxClientError())
	    .andExpect((ResultMatcher) content().string("Please pass the LibraryEventId"));

	}
	

}
