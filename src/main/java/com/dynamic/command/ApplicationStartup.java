package com.dynamic.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dynamic.command.kafka.service.KafkaService;
import com.dynamic.command.mongo.TweetTopicModel;
import com.dynamic.command.mongo.repository.TweetTopicRepository;

@Component
public class ApplicationStartup {
	
	private Logger logger = LoggerFactory.getLogger(ApplicationStartup.class.getName());
	
	@Autowired
	private TweetTopicRepository repository;
	
	@Autowired
	private KafkaService kafkaService;
	
	public void onStartup() {
		logger.info("Checking for active tops on intialization");
		List<TweetTopicModel> activeTopics = repository.findActiveTopics("yes");
		List<String> topics = new ArrayList<String>();
		for(TweetTopicModel tweetTopic : activeTopics) {
			logger.info("Adding active tweet topics to Kafka Service");
			topics.add(tweetTopic.getTopicName());
		}
		kafkaService.send(topics);
	}

}
