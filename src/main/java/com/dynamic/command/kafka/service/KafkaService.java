package com.dynamic.command.kafka.service;

import java.util.List;

import org.apache.kafka.clients.producer.KafkaProducer;

public interface KafkaService {

	public void send(String topic);

	public void send(List<String> topics);

	public void produceTweetsToKafka(KafkaProducer<String, String> producer);

	public void deactivate(String topic);

	public void deactivateAll();

	public void removeDuplicatesFromTopics(List<String> topics);

	public boolean isActive();

	public void setActive(boolean active);

	public boolean isKafkaIsOn();
}
