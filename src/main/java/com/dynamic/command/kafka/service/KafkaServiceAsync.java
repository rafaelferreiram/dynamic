package com.dynamic.command.kafka.service;

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
		if (topicFound == null) {
			mongoService.saveNewTopic(topic);
		} else {
			topicFound.toUpdateActive();
			service.setActive(true);
			mongoService.update(topicFound);
		}
		service.send(topic);
	}

	public boolean deactivate(String topic) {
		TweetTopicModel topicFound = mongoService.findByTopicName(topic);
		if (topicFound == null) {
			return false;
		} else if (topicFound.isDeactivated()) {
			return false;
		} else {
			topicFound.toUpdateDeactive();
			mongoService.update(topicFound);
			service.setActive(false);
			return true;
		}
	}

}