package com.dynamic.command.kafka.mapper;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dynamic.command.kafka.producer.dto.response.TweetTopicResponse;
import com.dynamic.command.mongo.TweetTopicModel;

@Component
public class TweetTopicMapper {

	private static Logger logger = LoggerFactory.getLogger(TweetTopicMapper.class.getName());

	private static ModelMapper MAPPER = new ModelMapper();

	public TweetTopicModel responseToModel(TweetTopicResponse playerRequest) {
		logger.debug("Converting: response object to entity object");
		return MAPPER.map(playerRequest, TweetTopicModel.class);
	}

	public TweetTopicResponse modelToResponse(TweetTopicModel entity) {
		logger.debug("Converting: entity object to response object");
		return MAPPER.map(entity, TweetTopicResponse.class);
	}
}
