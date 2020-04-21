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

	public void send(String topic) {
		int numberOfDataProduced = 0;
		List<String> topics = new ArrayList<String>();
		topics.add(topic);
		Client client = twitterClient.createTwitterClient(msgQueue, topics);
		client.connect();
		logger.info("Connected to Twitter client.");

		KafkaProducer<String, String> producer = kafkaProducerConfig.createKafkaProducer();

		produceTweetsToKafka(msgQueue, client, producer, numberOfDataProduced);

		client.stop(5);
		logger.info("End of application for the topic: " + topic);
		logger.info("Total of data produced into Kafka: " + numberOfDataProduced);

	}

	public void send(List<String> topics) {
		int numberOfDataProduced = 0;
		Client client = twitterClient.createTwitterClient(msgQueue, topics);
		client.connect();
		logger.info("Connected to Twitter client.");

		KafkaProducer<String, String> producer = kafkaProducerConfig.createKafkaProducer();
		produceTweetsToKafka(msgQueue, client, producer, numberOfDataProduced);

		client.stop(5);
		logger.info("End of application for the topic: " + topics);
		logger.info("Total of data produced into Kafka: " + numberOfDataProduced);

	}

	private void produceTweetsToKafka(BlockingQueue<String> msgQueue, Client client,
			KafkaProducer<String, String> producer, int numberOfDataProduced) {
		while (!client.isDone() && isActive()) {
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
				producer.send(new ProducerRecord<String, String>(kafkaTopic, null, msg), new Callback() {

					public void onCompletion(RecordMetadata metadata, Exception exception) {
						if (exception != null) {
							logger.error("Error while sendind data.", exception);
						}
					}
				});
				numberOfDataProduced++;
			}
		}
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
