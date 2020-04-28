package com.dynamic.command.mongo.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dynamic.command.mongo.TweetTopicModel;
import com.dynamic.command.mongo.TweetsLogModel;
import com.dynamic.command.mongo.repository.TweetLogRepository;
import com.dynamic.command.mongo.repository.TweetTopicRepository;
import com.dynamic.command.mongo.service.MongoService;

@Component
public class MongoServiceImpl implements MongoService{

	private Logger logger = LoggerFactory.getLogger(MongoService.class.getName());

	@Autowired
	private TweetTopicRepository repository;

	@Autowired
	private TweetLogRepository logRepository;

	@Value("${twitter.topic.active}")
	private String active;
	
	public List<TweetTopicModel> findAllTopics() {
		List<TweetTopicModel> allTopics = repository.findAll();
		logger.info("Total of " + allTopics.size() + " topics.");
		return allTopics;
	}

	public List<TweetTopicModel> findActiveTopics() {
		List<TweetTopicModel> activeTopics = repository.findActiveTopics(active);
		logger.info("Total of active topics " + activeTopics.size());
		return activeTopics;
	}

	public TweetTopicModel findByTopicName(String topic) {
		return repository.findByTopicName(topic);
	}

	public void update(TweetTopicModel tweetTopic) {
		try {
			repository.save(tweetTopic);
			logger.info("Tweet Topic updated on MongoDB successfully!");
		} catch (Exception e) {
			logger.error("Error when updating topic on MongoDB.", e.getMessage());
		}
	}

	public void saveNewTopic(String topic) {
		try {
			TweetTopicModel tweetTopic = new TweetTopicModel(topic, new Date().toString(), active);
			repository.save(tweetTopic);
			logger.info("Tweet Topic saved on MongoDB successfully!\n" + tweetTopic.toString());
		} catch (Exception e) {
			logger.error("Error when saving topic on MongoDB.", e.getMessage());
		}
	}

	public void saveLog(String log) {
		TweetsLogModel logModel = new TweetsLogModel(log, new Date().toString());
		logRepository.save(logModel);
	}
	
	public void registryTopic(String topic) {
		TweetTopicModel topicFound = findByTopicName(topic);
		if (topicFound == null) {
			saveNewTopic(topic);
		} else {
			topicFound.toUpdateActive();
			update(topicFound);
		}
	}
}
