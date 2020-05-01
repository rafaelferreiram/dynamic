package com.dynamic.command.mongo;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "twitter")
public class TweetTopicModel {

	@Id
	private String id;
	private String topicName;
	private String searchDate;
	private String active;

	public TweetTopicModel(String id, String topicName, String searchDate, String active) {
		super();
		this.id = id;
		this.topicName = topicName;
		this.searchDate = searchDate;
		this.active = active;
	}

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

	public void toUpdateActive() {
		this.searchDate = new Date().toString();
		this.active = "yes";
	}

	public void toUpdateDeactive() {
		this.searchDate = new Date().toString();
		this.active = "no";
	}

	public boolean isDeactivated() {
		return "no".equals(this.active) ? true : false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((searchDate == null) ? 0 : searchDate.hashCode());
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
		TweetTopicModel other = (TweetTopicModel) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (searchDate == null) {
			if (other.searchDate != null)
				return false;
		} else if (!searchDate.equals(other.searchDate))
			return false;
		if (topicName == null) {
			if (other.topicName != null)
				return false;
		} else if (!topicName.equals(other.topicName))
			return false;
		return true;
	}

}
