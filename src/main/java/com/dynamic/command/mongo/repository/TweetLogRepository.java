package com.dynamic.command.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dynamic.command.mongo.TweetsLogModel;

@Repository
public interface TweetLogRepository extends MongoRepository<TweetsLogModel, String> {

}
