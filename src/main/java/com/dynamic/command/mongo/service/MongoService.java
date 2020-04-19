package com.dynamic.command.mongo.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dynamic.command.mongo.TweetTopicModel;
import com.dynamic.command.mongo.TweetsLogModel;
import com.dynamic.command.mongo.repository.TweetLogRepository;
import com.dynamic.command.mongo.repository.TweetTopicRepository;

@Component
public class MongoService {

	private Logger logger = LoggerFactory.getLogger(MongoService.class.getName());

	@Autowired
	private TweetTopicRepository repository;

	@Autowired
	private TweetLogRepository logRepository;

	public List<TweetTopicModel> findAllTopics() {
		return repository.findAll();
	}

	public List<TweetTopicModel> findActiveTopics() {
		String active = "yes";
		return repository.findActiveTopics(active);
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
			TweetTopicModel tweetTopic = new TweetTopicModel(topic, new Date().toString(), "yes");
			repository.save(tweetTopic);
			logger.info("Tweet Topic saved on MongoDB successfully!");
		} catch (Exception e) {
			logger.error("Error when saving topic on MongoDB.", e.getMessage());
		}
	}

	public void saveLog(String log) {
		TweetsLogModel logModel = new TweetsLogModel(log, new Date().toString());
		logRepository.save(logModel);
	}
}
