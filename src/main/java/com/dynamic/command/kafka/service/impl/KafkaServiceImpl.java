package com.dynamic.command.kafka.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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

import com.dynamic.command.kafka.mapper.TweetTopicMapper;
import com.dynamic.command.kafka.producer.KafkaProducerConfig;
import com.dynamic.command.kafka.producer.dto.response.TweetTopicResponse;
import com.dynamic.command.kafka.service.KafkaService;
import com.dynamic.command.mongo.TweetTopicModel;
import com.dynamic.command.mongo.service.MongoService;
import com.dynamic.command.twitter.TwitterClient;
import com.twitter.hbc.core.Client;

@Component
public class KafkaServiceImpl implements KafkaService {

	private Logger logger = LoggerFactory.getLogger(KafkaService.class.getName());

	List<String> listOfTopics = new ArrayList<String>();

	@Autowired
	private KafkaProducerConfig kafkaProducerConfig;

	@Autowired
	private TwitterClient twitterClient;

	@Autowired
	private TweetTopicMapper mapper;

	@Autowired
	private MongoService mongoService;

	@Value("${kafka.topic}")
	private String kafkaTopic;

	private boolean active;

	Client client;

	public void send(String topic) {
		listOfTopics.add(topic);

		client = twitterClient.createTwitterClient(listOfTopics);
		client.connect();
		logger.info("Connected to Twitter client.");

		KafkaProducer<String, String> producer = kafkaProducerConfig.createKafkaProducer();
		produceTweetsToKafka(producer, listOfTopics);
	}

	public void send(List<String> topics) {
		if (!topics.isEmpty()) {
			listOfTopics.addAll(topics);
			removeDuplicatesFromTopics(listOfTopics);

			client = twitterClient.createTwitterClient(listOfTopics);
			client.connect();
			logger.info("Connected to Twitter client.");

			KafkaProducer<String, String> producer = kafkaProducerConfig.createKafkaProducer();
			produceTweetsToKafka(producer, listOfTopics);
		}

	}

	public void produceTweetsToKafka(KafkaProducer<String, String> producer, List<String> topics) {
		while (!client.isDone() && isActive()) {
			String msg = null;
			try {
				msg = twitterClient.getMsgQueue().poll(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
				client.stop();
			}

			if (msg != null) {
				logger.info(msg);
				mongoService.saveLog(msg);
				producer.send(new ProducerRecord<String, String>(kafkaTopic, null, msg), new Callback() {

					public void onCompletion(RecordMetadata metadata, Exception exception) {
						if (exception != null) {
							logger.error("Error while sendind data.", exception);
						}
					}
				});
			}

		}

	}

	public void deactivate(String topic) {
		if (client != null) {
			removeDuplicatesFromTopics(listOfTopics);
			if (listOfTopics.contains(topic)) {
				this.active = false;
				listOfTopics.remove(topic);
				client.stop();
				client = null;
				this.active = true;
				send(listOfTopics);
			}
		}
	}

	public void deactivateAll() {
		List<TweetTopicResponse> activeTopics = mongoService.findActiveTopics();

		if (!activeTopics.isEmpty()) {
			for (TweetTopicResponse topic : activeTopics) {
				TweetTopicModel topicModel = mapper.responseToModel(topic);
				topicModel.toUpdateDeactive();
				mongoService.update(topicModel);
			}
			this.setActive(false);
			listOfTopics = new ArrayList<String>();
			client.stop();
			client = null;
		}

	}

	public void removeDuplicatesFromTopics(List<String> listOfTopics) {
		LinkedHashSet<String> hashSet = new LinkedHashSet<>(listOfTopics);
		listOfTopics = new ArrayList<String>(hashSet);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
