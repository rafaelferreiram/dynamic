package com.dynamic.command.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dynamic.command.mongo.TweetsLogModel;

public interface TweetLogRepository extends MongoRepository<TweetsLogModel, String> {

}
