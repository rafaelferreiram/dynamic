package com.dynamic.command.kafka.producer.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicsListRequestDTO {
	 
	@NotNull( message = "List of topics is requires" )
	@JsonProperty(value = "topics")	
	private List<TopicRequestDTO> topics;

	public List<TopicRequestDTO> getTopics() {
		return topics;
	}

	public void setTopics(List<TopicRequestDTO> topics) {
		this.topics = topics;
	}
	
	
}
