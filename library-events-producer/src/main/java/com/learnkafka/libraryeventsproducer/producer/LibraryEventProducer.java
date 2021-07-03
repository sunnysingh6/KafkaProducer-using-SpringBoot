package com.learnkafka.libraryeventsproducer.producer;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.libraryeventsproducer.domain.LibraryEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LibraryEventProducer {
	@Autowired
	KafkaTemplate kafkaTemplate;
	@Autowired
	ObjectMapper objectMapper;
	
	String topic="library-events";
	
	public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
		Integer key=libraryEvent.getLibraryEventId();
		String value=objectMapper.writeValueAsString(libraryEvent.getBook());
		ListenableFuture<SendResult<Integer, String>> listenablefuture= kafkaTemplate.sendDefault(key, value);
		
		listenablefuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {

			@Override
			public void onSuccess(SendResult<Integer, String> result) {
				// TODO Auto-generated method stub
				handleSuccess(key,value,result);
				
			}

			@Override
			public void onFailure(Throwable ex) {
				try {
					handleFailure(key,value,ex);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		
		
	}
	
	public SendResult<Integer, String> sendLibraryEventSynchronous(LibraryEvent libraryEvent) throws JsonProcessingException {
		Integer key=libraryEvent.getLibraryEventId();
		String value=objectMapper.writeValueAsString(libraryEvent.getBook());
		SendResult<Integer,String> result=null;
		try {
			result=(SendResult<Integer, String>) kafkaTemplate.sendDefault(key, value).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		
	}
	
	public ListenableFuture<SendResult<Integer, String>> sendLibraryEvent_approach2(LibraryEvent libraryEvent) throws JsonProcessingException {
		Integer key=libraryEvent.getLibraryEventId();
		String value=objectMapper.writeValueAsString(libraryEvent);
	
		 
		ProducerRecord<Integer,String> producerRecord=buildPrducerRecord(key,value,topic);
		ListenableFuture<SendResult<Integer, String>> listenablefuture= kafkaTemplate.send(topic,key, value);
		
		listenablefuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {

			@Override
			public void onSuccess(SendResult<Integer, String> result) {
				// TODO Auto-generated method stub
				handleSuccess(key,value,result);
				
			}

			@Override
			public void onFailure(Throwable ex) {
				try {
					handleFailure(key,value,ex);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			
		});
		return listenablefuture;
		
		
	}

	
	private ProducerRecord<Integer, String> buildPrducerRecord(Integer key, String value, String topic) {
		// TODO Auto-generated method stub

		List<Header> recordHeaders=Arrays.asList((new RecordHeader("event-source", "scanner".getBytes())));
		return new ProducerRecord<Integer, String>(topic,null, key, value,recordHeaders);
	}

	protected void handleFailure(Integer key, String value, Throwable ex) throws Throwable {
			log.info("Error sending the message and the message {}",ex.getMessage());
		    throw ex;
				
		
		
	}

	protected void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
		// TODO Auto-generated method stub
		log.info("msg sent successfully for he key: {}and the value is {} partititon is {}",key,value,result.getRecordMetadata().partition());
		
	}

}
