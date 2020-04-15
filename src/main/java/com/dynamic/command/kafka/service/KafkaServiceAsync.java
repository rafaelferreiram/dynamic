package com.dynamic.command.kafka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class KafkaServiceAsync {

	@Autowired
	private KafkaService service;
	
	@Async
	public void send(String topic) {
		service.send(topic);
	}
}
