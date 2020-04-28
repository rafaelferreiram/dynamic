package com.dynamic.command.kafka.producer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicErrorResponseDTO {

	@JsonProperty(value = "topicName")
	private String topicName;

	@JsonProperty(value = "error")
	private String error;

	public TopicErrorResponseDTO(String topicName, String error) {
		super();
		this.topicName = topicName;
		this.error = error;
	}

	public TopicErrorResponseDTO(String error) {
		super();
		this.error = error;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
