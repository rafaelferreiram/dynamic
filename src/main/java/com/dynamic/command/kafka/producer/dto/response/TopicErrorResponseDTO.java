package com.dynamic.command.kafka.producer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicErrorResponseDTO {

	@JsonProperty(value = "topicName")
	private String topicName;

	@JsonProperty(value = "error")
	private String msg;

	public TopicErrorResponseDTO(String topicName, String msg) {
		super();
		this.topicName = topicName;
		this.msg = msg;
	}

	public TopicErrorResponseDTO(String error) {
		super();
		this.msg = error;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
