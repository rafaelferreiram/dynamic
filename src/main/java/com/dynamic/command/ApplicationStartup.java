package com.dynamic.command;

import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoClient;

@Component
public class ApplicationStartup extends AbstractMongoClientConfiguration{

	@Override
	public MongoClient mongoClient() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getDatabaseName() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
