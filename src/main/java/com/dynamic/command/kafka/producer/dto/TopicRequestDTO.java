package com.dynamic.command.kafka.producer.dto;

import java.util.List;

public class TopicRequestDTO {
	
	private List<String> topics;

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}
	
	
}
