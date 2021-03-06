package com.dynamic.command.twitter;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

	private BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);

	@Value("${twitter.consumerKey}")
	String consumerKey;

	@Value("${twitter.consumerSecret}")
	String consumerSecret;

	@Value("${twitter.token}")
	String token;

	@Value("${twitter.secret}")
	String secret;

	public Client createTwitterClient(List<String> topic) {
		/**
		 * Declare the host you want to connect to, the endpoint, and authentication
		 * (basic auth or oauth)
		 */
		logger.info("Creating Twitter Client.");
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();

		// Optional: set up some followings and track terms
		List<String> terms = topic;
		hosebirdEndpoint.trackTerms(terms);

		Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);

		ClientBuilder builder = new ClientBuilder().hosts(hosebirdHosts).authentication(hosebirdAuth)
				.name("dynamic-twitter-command").endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue));

		Client hosebirdClient = builder.build();
		logger.info("Twitter Client created successfully.");
		return hosebirdClient;
	}

	public BlockingQueue<String> getMsgQueue() {
		return msgQueue;
	}

}
