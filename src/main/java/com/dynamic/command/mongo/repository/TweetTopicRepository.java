package com.dynamic.command.mongo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dynamic.command.mongo.TweetTopicModel;

@Repository
public interface TweetTopicRepository extends MongoRepository<TweetTopicModel, String>{
	
	@Query("{ 'active': ?0 }")
	List<TweetTopicModel> findActiveTopics(String active);

	@Query("{ 'topicName': ?0 }")
	TweetTopicModel findByTopicName(String topic);

}
