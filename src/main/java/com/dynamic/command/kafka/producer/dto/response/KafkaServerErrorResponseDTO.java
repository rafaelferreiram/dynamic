package com.dynamic.command.kafka.producer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KafkaServerErrorResponseDTO {

	@JsonProperty(value = "ServerUp")
	private boolean isUp;
	@JsonProperty(value = "Message")
	private String message;

	public KafkaServerErrorResponseDTO(boolean isUp, String message) {
		super();
		this.isUp = isUp;
		this.message = message;
	}

	public boolean isUp() {
		return isUp;
	}

	public void setUp(boolean isUp) {
		this.isUp = isUp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
