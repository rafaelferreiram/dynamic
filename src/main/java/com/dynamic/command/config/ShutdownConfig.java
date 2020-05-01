package com.dynamic.command.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dynamic.command.kafka.service.impl.KafkaServiceImpl;

@Component
public class ShutdownConfig {

	@Autowired
	private KafkaServiceImpl kafkaService;
	
	public void shutdown() {
		try {
			kafkaService.setKafkaIsOn(Boolean.FALSE);
		} catch (Exception e) {

		}
	}
}
