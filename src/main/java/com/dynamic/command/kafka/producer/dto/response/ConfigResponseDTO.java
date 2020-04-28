package com.dynamic.command.kafka.producer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigResponseDTO {
	
	@JsonProperty(value = "applicationStatus")	
	private String applicationStatus;
	
	@JsonProperty(value = "topicsStatus")	
	private String topicsStatus;

	public String getStatus() {
		return applicationStatus;
	}

	public void setStatus(String status) {
		this.applicationStatus = status;
	}

	public String getTopicsStatus() {
		return topicsStatus;
	}

	public void setTopicsStatus(String topicsStatus) {
		this.topicsStatus = topicsStatus;
	}
	
	


}
