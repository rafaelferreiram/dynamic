package com.dynamic.command.kafka.producer.dto.request;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((topics == null) ? 0 : topics.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TopicsListRequestDTO other = (TopicsListRequestDTO) obj;
		if (topics == null) {
			if (other.topics != null)
				return false;
		} else if (!topics.equals(other.topics))
			return false;
		return true;
	}
	
	
}
