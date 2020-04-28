package com.dynamic.command.mongo.service;

import java.util.List;

import com.dynamic.command.mongo.TweetTopicModel;

public interface MongoService {

	public List<TweetTopicModel> findAllTopics() ;

	public List<TweetTopicModel> findActiveTopics() ;

	public TweetTopicModel findByTopicName(String topic) ;

	public void update(TweetTopicModel tweetTopic) ;

	public void saveNewTopic(String topic) ;

	public void saveLog(String log) ;

	public void registryTopic(String topic);
	
}
