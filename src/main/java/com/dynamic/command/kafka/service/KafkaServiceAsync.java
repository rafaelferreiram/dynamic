package com.dynamic.command.kafka.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.dynamic.command.kafka.producer.dto.TopicRequestDTO;
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
			mongoService.update(topicFound);
		}
		service.setActive(true);
		service.send(topic);
	}

	@Async
	public void send(List<TopicRequestDTO> topics) {
		List<String> topicNames =  new ArrayList<String>();
		for (TopicRequestDTO topic : topics) {
			TweetTopicModel topicFound = mongoService.findByTopicName(topic.getTopicName());
			if (topicFound == null) {
				mongoService.saveNewTopic(topic.getTopicName());
			} else {
				topicFound.toUpdateActive();
				mongoService.update(topicFound);
			}
			topicNames.add(topic.getTopicName());
		}
		service.setActive(true);
		service.send(topicNames);
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
			return true;
		}
	}

	@Async
	public void closeConnectionClient(String topic) {
		service.deactivate(topic);
	}

}
