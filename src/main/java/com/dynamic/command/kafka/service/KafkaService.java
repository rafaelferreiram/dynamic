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

	List<String> topics = new ArrayList<String>();

	public void send(String topic) {
		topics.add(topic);
		Client client = twitterClient.createTwitterClient(msgQueue, topics);
		client.connect();
		logger.info("Connected to Twitter client.");
		KafkaProducer<String, String> producer = kafkaProducerConfig.createKafkaProducer();

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
			if (!isActive() && tweetContainsTopic(topics, content.toUpperCase())) {
				close(client, topics);
			}
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
		logger.info("Total of data produced into Kafka: " + numberOfDataProduced + " on the twitter topic: "
				+ topics.toString());
	}

	private void close(Client client, List<String> topics) {
		client.stop(5);
		logger.info("End of application for the topic: " + topics);

	}

	private boolean tweetContainsTopic(List<String> topics, String content) {
		for (String topic : topics) {
			if (content.contains(topic.toUpperCase())) {
				topics.remove(topic);
				this.active = true;
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean deactivate(String topic) {
		TweetTopicModel topicFound = mongoService.findByTopicName(topic);
		if (topicFound == null) {
			return false;
		} else if (topicFound.isDeactivated()) {
			return false;
		} else {
			topicFound.toUpdateDeactive();
			mongoService.update(topicFound);
			this.setActive(false);
			return true;
		}
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
