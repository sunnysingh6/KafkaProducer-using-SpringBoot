package com.learnkafka.libraryeventsproducer.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import com.learnkafka.libraryeventsproducer.domain.Book;
import com.learnkafka.libraryeventsproducer.domain.LibraryEvent;
import com.learnkafka.libraryeventsproducer.domain.LibraryEventType;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"},partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
		"spring.kafka.admin.properties.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@Slf4j
public class LibraryEventControllerIntegrationTest {
	
	@Autowired	
	private TestRestTemplate template;
	
	private Consumer<Integer, String> consumer;
	
	@Autowired
	EmbeddedKafkaBroker embeddedKafkaBroker;
	
	@BeforeEach
	public void setUp() {
		Map<String,Object> configs=new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
		
			consumer=(Consumer<Integer, String>) new DefaultKafkaConsumerFactory(configs,new IntegerDeserializer(),new StringDeserializer()).createConsumer();
			embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
	}
	@AfterEach
	public void tearDown() {
		consumer.close();
	}
	
	
	@Test
	@Timeout(5)
	void postLibraryEvent() throws InterruptedException {
		
		LibraryEvent libraryEvent=new LibraryEvent(null,new Book(123,"Dilip","Kafka using SpringBoot test"),LibraryEventType.NEW);
		HttpHeaders header=new HttpHeaders();
		header.set("content-type",MediaType.APPLICATION_JSON.toString());
		HttpEntity<LibraryEvent> request=new HttpEntity<LibraryEvent>(libraryEvent,header);
		
		ResponseEntity<LibraryEvent> responseEntity=template.exchange("/v1/libraryevent", HttpMethod.POST,request,LibraryEvent.class);
		
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		ConsumerRecord<Integer, String> consumerRecord=KafkaTestUtils.getSingleRecord((Consumer<Integer, String>) consumer, "library-events");
		//Thread.sleep(3000);
		String expectedRecord="{\"bookId\":\"123\",\"bookName\":\"Dilip\",\"bookAuthor\":\"Kafka using SpringBoot test\"}";
		String value=consumerRecord.value();
		assertEquals(expectedRecord, value);
		
	}
	@Test
	   @Timeout(5)
	   void putLibraryEvent() throws InterruptedException {
	       //given
		LibraryEvent libraryEvent=new LibraryEvent(null,new Book(123,"Dilip","Kafka using SpringBoot test"),LibraryEventType.NEW);
		
	       HttpHeaders headers = new HttpHeaders();
	       headers.set("content-type", MediaType.APPLICATION_JSON.toString());
	       HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

	       //when
	       ResponseEntity<LibraryEvent> responseEntity = template.exchange("/v1/libraryevent", HttpMethod.PUT, request, LibraryEvent.class);

	       //then
	       assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

	       ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");
	       //Thread.sleep(3000);
	       String expectedRecord = "{\"libraryEventId\":123,\"libraryEventType\":\"UPDATE\",\"book\":{\"bookId\":456,\"bookName\":\"Kafka using Spring Boot\",\"bookAuthor\":\"Dilip\"}}";
	       String value = consumerRecord.value();
	       assertEquals(expectedRecord, value);

	   }
	
	
	
	

}
