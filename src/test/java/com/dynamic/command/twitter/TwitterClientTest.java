package com.dynamic.command.twitter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import com.twitter.hbc.core.Client;

@RunWith(SpringRunner.class)
public class TwitterClientTest {

	@InjectMocks
	private TwitterClient client;

	@Before
	public void onInit() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldCreateClient() {
		client.consumerKey = "keyTest";
		client.consumerSecret = "consumerSecretTest";
		client.token = "tokenTest";
		client.secret = "secretTest";

		List<String> listOfTopics = populateTopics();

		Client twitterClient = client.createTwitterClient(listOfTopics);

		assertNotNull(twitterClient);

	}

	@Test
	public void shouldCreateClientWithTheTopicList() {
		client.consumerKey = "keyTest";
		client.consumerSecret = "consumerSecretTest";
		client.token = "tokenTest";
		client.secret = "secretTest";

		List<String> listOfTopics = populateTopics();
		String expectedParamString = populatePostParamString(listOfTopics);

		Client twitterClient = client.createTwitterClient(listOfTopics);

		String postParamString = twitterClient.getEndpoint().getPostParamString();

		assertEquals(expectedParamString, postParamString);
	}

	private String populatePostParamString(List<String> topics) {
		return "track=" + topics.get(0) + "%2C" + topics.get(1) + "%2C" + topics.get(2);
	}

	private List<String> populateTopics() {
		List<String> topics = new ArrayList<String>();
		topics.add("One");
		topics.add("Two");
		topics.add("Three");
		return topics;
	}

}
