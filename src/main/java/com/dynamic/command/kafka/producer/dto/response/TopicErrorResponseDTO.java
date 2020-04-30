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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((error == null) ? 0 : error.hashCode());
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
		TopicErrorResponseDTO other = (TopicErrorResponseDTO) obj;
		if (error == null) {
			if (other.error != null)
				return false;
		} else if (!error.equals(other.error))
			return false;
		if (topicName == null) {
			if (other.topicName != null)
				return false;
		} else if (!topicName.equals(other.topicName))
			return false;
		return true;
	}

}
