package com.bgarage.ims.scheduler.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventPublisher {

	private KafkaTemplate<String, String> kafkaTemplate;

	public EventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void publishEvent(String topic, String event) {
		kafkaTemplate.send(topic, event);
		System.out.println("Published event to Kafka topic '" + topic + "': " + event);

	}

}