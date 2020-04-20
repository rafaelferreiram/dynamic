package com.dynamic.command.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dynamic.command.kafka.service.KafkaService;

@Component
public class ShutdownConfig {

	@Autowired
	private KafkaService kafkaService;
	
	public void shutdown() {
		kafkaService.deactivateAll();
		System.exit(0);	
	}
}
