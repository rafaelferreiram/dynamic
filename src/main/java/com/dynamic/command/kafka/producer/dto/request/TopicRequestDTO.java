package com.dynamic.command.kafka.producer.dto.request;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicRequestDTO {

	@NotNull(message = "Topic name is required")
	@JsonProperty(value = "topicName")
	private String topicName;

	public TopicRequestDTO() {
		super();
	}

	public TopicRequestDTO(@NotNull(message = "Topic name is required") String topicName) {
		super();
		this.topicName = topicName;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	@Override
	public String toString() {
		return topicName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((topicName == null) ? 0 : topicName.hashCode());
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
		TopicRequestDTO other = (TopicRequestDTO) obj;
		if (topicName == null) {
			if (other.topicName != null)
				return false;
		} else if (!topicName.equals(other.topicName))
			return false;
		return true;
	}

}
