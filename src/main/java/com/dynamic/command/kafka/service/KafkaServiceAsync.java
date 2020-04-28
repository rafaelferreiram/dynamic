package com.dynamic.command.kafka.service;

import java.util.List;

import org.springframework.scheduling.annotation.Async;

import com.dynamic.command.kafka.producer.dto.TopicRequestDTO;

public interface KafkaServiceAsync {

	@Async
	public void send(String topic) ;

	@Async
	public void send(List<TopicRequestDTO> topics) ;

	@Async
	public void closeConnectionClient(String topic) ;

	public boolean deactivate(String topic) ;

}
