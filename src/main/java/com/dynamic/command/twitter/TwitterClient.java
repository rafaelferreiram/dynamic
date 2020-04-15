package com.dynamic.command.twitter;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

@Component
public class TwitterClient {
	
	private Logger logger = LoggerFactory.getLogger(TwitterClient.class.getName());
	
	@Value("${twitter.consumerKey}")
	private String consumerKey;
	
	@Value("${twitter.consumerSecret}")
	private String consumerSecret;
	
	@Value("${twitter.token}")
	private String token;
	
	@Value("${twitter.secret}")
	private String secret;
	
	public Client createTwitterClient(BlockingQueue<String> msgQueue, String topic) {
		/**
		 * Declare the host you want to connect to, the endpoint, and authentication
		 * (basic auth or oauth)
		 */
		logger.info("Creating Twitter Client.");
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		// Optional: set up some followings and track terms
		List<String> terms = Lists.newArrayList(topic);
		hosebirdEndpoint.trackTerms(terms);

		Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret,
				token, secret);

		ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01") 
				.hosts(hosebirdHosts).authentication(hosebirdAuth).endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue));

		Client hosebirdClient = builder.build();
		return hosebirdClient;
	}

}
