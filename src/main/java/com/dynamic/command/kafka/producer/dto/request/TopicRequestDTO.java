package com.dynamic.command.kafka.producer.dto.request;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicRequestDTO {

	@NotNull( message = "Topic name is required" )
	@JsonProperty(value = "topicName")	
	private String topicName;

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	@Override
	public String toString() {
		return "'" + topicName + "'";
	}
	
}
