package com.dynamic.command.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "tweets-log")
public class TweetsLogModel {

	@Id
	private String id;
	private String log;
	private String dateLog;

	public TweetsLogModel() {
		super();
	}

	public TweetsLogModel( String log, String dateLog) {
		super();
		this.log = log;
		this.dateLog = dateLog;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getDateLog() {
		return dateLog;
	}

	public void setDateLog(String dateLog) {
		this.dateLog = dateLog;
	}

}
