package com.dynamic.command.kafka.producer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicResponseDTO {

	@JsonProperty(value = "topicName")
	private String topicName;
	
	@JsonProperty(value = "active")
	private String active;
	
	@JsonProperty(value = "msg")
	private String msg;

	public TopicResponseDTO(String topicName, String active, String msg) {
		super();
		this.topicName = topicName;
		this.active = active;
		this.msg = msg;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
