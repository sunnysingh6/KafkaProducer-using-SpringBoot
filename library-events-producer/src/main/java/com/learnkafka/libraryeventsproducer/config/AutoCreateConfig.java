package com.learnkafka.libraryeventsproducer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Profile("local")
@Slf4j
public class AutoCreateConfig {

	@Bean
	public NewTopic libraryEvents() {
		
		log.info("Topic initialized");
		return TopicBuilder.name("library-events")
					.partitions(1)
					.replicas(1)
					.build();
	}
}
