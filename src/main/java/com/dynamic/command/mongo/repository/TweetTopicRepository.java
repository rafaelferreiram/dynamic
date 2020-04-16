package com.dynamic.command.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dynamic.command.mongo.TweetTopicModel;

public interface TweetTopicRepository extends MongoRepository<TweetTopicModel, String>{

}
