package com.dynamic.command.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "twitter")
public class TweetTopicModel {

	@Id
	private String id;
	private String topicName;
	private String searchDate;
	private String active;

	public TweetTopicModel(String topicName, String searchDate, String active) {
		super();
		this.topicName = topicName;
		this.searchDate = searchDate;
		this.active = active;
	}

	public TweetTopicModel() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(String searchDate) {
		this.searchDate = searchDate;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "TweetTopicModel [id=" + id + ", topicName=" + topicName + ", searchDate=" + searchDate + ", active="
				+ active + "]";
	}

}
