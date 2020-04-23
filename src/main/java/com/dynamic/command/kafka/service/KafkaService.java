package com.dynamic.command.kafka.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dynamic.command.kafka.producer.KafkaProducerConfig;
import com.dynamic.command.mongo.TweetTopicModel;
import com.dynamic.command.mongo.service.MongoService;
import com.dynamic.command.twitter.TwitterClient;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.twitter.hbc.core.Client;

@Component
public class KafkaService {

	private Logger logger = LoggerFactory.getLogger(KafkaService.class.getName());

	BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);

	@Autowired
	private KafkaProducerConfig kafkaProducerConfig;

	@Autowired
	private TwitterClient twitterClient;

	@Autowired
	private MongoService mongoService;

	@Value("${kafka.topic}")
	private String kafkaTopic;

	private boolean active;

	Client client;

	List<String> topics = new ArrayList<String>();

	public void send(String topic) {
		KafkaProducer<String, String> producer = kafkaProducerConfig.createKafkaProducer();
		topics.add(topic);
		client = twitterClient.createTwitterClient(msgQueue, topics);
		client.connect();
		logger.info("Connected to Twitter client.");

		produceTweetsToKafka(msgQueue, client, producer, topics);
	}

	public void send(List<String> topics) {
		Client client = twitterClient.createTwitterClient(msgQueue, topics);
		client.connect();
		logger.info("Connected to Twitter client.");

		KafkaProducer<String, String> producer = kafkaProducerConfig.createKafkaProducer();
		produceTweetsToKafka(msgQueue, client, producer, topics);

		client.stop(5);
		logger.info("End of application for the topic: " + topics);

	}

	@SuppressWarnings("deprecation")
	private void produceTweetsToKafka(BlockingQueue<String> msgQueue, Client client,
			KafkaProducer<String, String> producer, List<String> topics) {
		int numberOfDataProduced = 0;
		JsonParser jsonParser = new JsonParser();
		String content = topics.get(0);
		while (!client.isDone()) {
			String msg = null;
			try {
				msg = msgQueue.poll(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
				client.stop();
			}

			if (msg != null) {
				logger.info(msg);
				mongoService.saveLog(msg);
				JsonObject asJsonObject = jsonParser.parse(msg).getAsJsonObject();
				content = asJsonObject.toString();
				numberOfDataProduced++;
				producer.send(new ProducerRecord<String, String>(kafkaTopic, null, msg), new Callback() {

					public void onCompletion(RecordMetadata metadata, Exception exception) {
						if (exception != null) {
							logger.error("Error while sendind data.", exception);
						}
					}
				});
			}

		}
		logger.info("Total of data produced into Kafka: " + client.getStatsTracker().getNumMessages()
				+ " on the twitter topic: " + topics.toString());
	}

	private void close(Client client, String topics) {

		String postParamString = client.getEndpoint().getPostParamString();
		postParamString = postParamString.replace(topics, "");
		postParamString = postParamString.replace("track=", "");
		client.stop();
		send(postParamString);

	}

	public void deactivate(String topic) {
		close(client, topic);
	}

	public void deactivateAll() {
		List<TweetTopicModel> activeTopics = mongoService.findActiveTopics();

		if (!activeTopics.isEmpty()) {
			for (TweetTopicModel topic : activeTopics) {
				topic.toUpdateDeactive();
				mongoService.update(topic);
			}
			this.setActive(false);
		}

	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
