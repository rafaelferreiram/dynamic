package com.dynamic.command.kafka.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dynamic.command.kafka.producer.dto.request.TopicRequestDTO;
import com.dynamic.command.kafka.service.KafkaService;
import com.dynamic.command.kafka.service.KafkaServiceAsync;
import com.dynamic.command.mongo.TweetTopicModel;
import com.dynamic.command.mongo.service.MongoService;

@Component
public class KafkaServiceAsyncImpl implements KafkaServiceAsync {

	@Autowired
	private KafkaService service;

	@Autowired
	private MongoService mongoService;

	public void send(String topic) {
		mongoService.registryTopic(topic);
		service.setActive(true);
		service.send(topic);
	}

	public void send(List<TopicRequestDTO> topics) {
		List<String> topicNames = new ArrayList<String>();
		for (TopicRequestDTO topic : topics) {
			mongoService.registryTopic(topic.getTopicName());
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

	public void closeConnectionClient(String topic) {
		service.deactivate(topic);
	}


}
