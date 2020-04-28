package com.dynamic.command.kafka.producer.dto.response;

import java.util.List;

import com.dynamic.command.kafka.producer.dto.request.TopicRequestDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicsListResponseDTO {

	@JsonProperty(value = "topics")
	List<TopicRequestDTO> topics;

	@JsonProperty(value = "active")
	private String active;

	@JsonProperty(value = "msg")
	private String msg;

	public TopicsListResponseDTO(List<TopicRequestDTO> topics, String active, String msg) {
		super();
		this.topics = topics;
		this.active = active;
		this.msg = msg;
	}

	public List<TopicRequestDTO> getTopics() {
		return topics;
	}

	public void setTopics(List<TopicRequestDTO> topics) {
		this.topics = topics;
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
