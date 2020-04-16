package com.dynamic.command.kafka.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.dynamic.command.mongo.TweetTopicModel;
import com.dynamic.command.mongo.service.MongoService;

@Component
public class KafkaServiceAsync {

	@Autowired
	private KafkaService service;
	
	@Autowired
	private MongoService mongoService;
	
	@Async
	public void send(String topic) {
		TweetTopicModel tweetTopic = new TweetTopicModel(topic, new Date().toString(), "yes");
		mongoService.save(tweetTopic);
		service.send(topic);
	}
}
