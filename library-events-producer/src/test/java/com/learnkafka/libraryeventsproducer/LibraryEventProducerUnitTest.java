package com.learnkafka.libraryeventsproducer;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;

import javax.management.RuntimeErrorException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.libraryeventsproducer.domain.Book;
import com.learnkafka.libraryeventsproducer.domain.LibraryEvent;
import com.learnkafka.libraryeventsproducer.domain.LibraryEventType;
import com.learnkafka.libraryeventsproducer.producer.LibraryEventProducer;

@ExtendWith(MockitoExtension.class)
public class LibraryEventProducerUnitTest {
	
	
	@Mock
	KafkaTemplate<Integer, String> kafkaTemplate;
	
	@Spy
	ObjectMapper objectMapper=new ObjectMapper();
	
	@InjectMocks
	LibraryEventProducer libraryEventProducer;
	
	@Test
	void sendLibraryEvent_Approach2_failure() throws JsonProcessingException {
		LibraryEvent libraryEvent=new LibraryEvent(null,new Book(123,"Dilip","Kafka using SpringBoot test"),LibraryEventType.NEW);
		SettableListenableFuture future=new SettableListenableFuture();
		future.setException(new RuntimeErrorException(null, "Exception calling Kafka"));
		when(kafkaTemplate.send((ProducerRecord<Integer, String>) isA(ProducerRecord.class))).thenReturn(future);
		assertThrows(Exception.class,()->libraryEventProducer.sendLibraryEvent_approach2(libraryEvent).get());
		
		
	}
	@Test
	void sendLibraryEvent_Approach2_success() throws JsonProcessingException, InterruptedException, ExecutionException {
		LibraryEvent libraryEvent=new LibraryEvent(null,new Book(123,"Dilip","Kafka using SpringBoot test"),LibraryEventType.NEW);
		String record=objectMapper.writeValueAsString(libraryEvent);
		SettableListenableFuture future=new SettableListenableFuture();
		ProducerRecord<Integer, String> producerRecord=new ProducerRecord<Integer, String>("library-events", libraryEvent.getLibraryEventId(),record); 
		RecordMetadata	recordMetaData=new RecordMetadata(new TopicPartition("ibrary-events", 1),1,1,342,System.currentTimeMillis(),1,200);
		SendResult<Integer, String> result=new SendResult<Integer, String>(producerRecord, recordMetaData) ;
		future.set(result);
		when(kafkaTemplate.send((ProducerRecord<Integer, String>) isA(ProducerRecord.class))).thenReturn(future);
		
		ListenableFuture<SendResult<Integer, String>> listenableFuture= libraryEventProducer.sendLibraryEvent_approach2(libraryEvent);
		
		SendResult<Integer, String> sendResult=listenableFuture.get();
		assert(sendResult.getRecordMetadata().partition()==1);	
		
		
	}

}
