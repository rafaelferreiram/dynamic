package com.dynamic.command.mongo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dynamic.command.mongo.TweetTopicModel;
import com.dynamic.command.mongo.repository.TweetTopicRepository;

@Component
public class MongoService {
	
	private Logger logger = LoggerFactory.getLogger(MongoService.class.getName());

	@Autowired
	private TweetTopicRepository repository;
	
	public void save(TweetTopicModel tweetTopic) {
		try {
			repository.save(tweetTopic);
			logger.info("Tweet Topic savend on MongoDB successfully!");
		} catch (Exception e) {
			logger.error("Error when saving topic on MongoDB.",e.getMessage());
		}
	}
	
	public List<TweetTopicModel> findAllTopics(){
		return repository.findAll();
	}
	
	public List<TweetTopicModel> findActiveTopics(){
		String active = "yes";
		return repository.findActiveTopics(active);
	}
	
}
