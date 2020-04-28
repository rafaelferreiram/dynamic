package com.dynamic.command.kafka.service;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.kafka.clients.producer.KafkaProducer;

import com.twitter.hbc.core.Client;

public interface KafkaService {

	public void send(String topic);

	public void send(List<String> topics);

	public void produceTweetsToKafka(BlockingQueue<String> msgQueue, Client client,
			KafkaProducer<String, String> producer, List<String> topics);

	public void deactivate(String topic);

	public void deactivateAll();

	public boolean isActive();

	public void setActive(boolean active);
}
