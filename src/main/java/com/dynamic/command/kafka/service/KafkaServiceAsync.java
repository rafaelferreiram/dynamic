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
		TweetTopicModel topicFound = mongoService.findByTopicName(topic);
		if(topicFound == null) {
			mongoService.saveNewTopic(topic);
		}else {
			topicFound.setSearchDate(new Date().toString());
			mongoService.update(topicFound);
		}
		service.send(topic);
	}

	public void deactivate(String topic) {
		service.setActive(false);
	}

	
}
