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

	List<String> topics = new ArrayList<String>();

	Client client;

	public void send(String topic) {
		KafkaProducer<String, String> producer = kafkaProducerConfig.createKafkaProducer();
		topics.add(topic);
		client = twitterClient.createTwitterClient(msgQueue, topics);
		client.connect();
		logger.info("Connected to Twitter client.");

		produceTweetsToKafka(msgQueue, client, producer, topics);
	}

	public void send(List<String> topics) {
		if(!topics.isEmpty()) {
			Client client = twitterClient.createTwitterClient(msgQueue, topics);
			client.connect();
			logger.info("Connected to Twitter client.");
			
			KafkaProducer<String, String> producer = kafkaProducerConfig.createKafkaProducer();
			produceTweetsToKafka(msgQueue, client, producer, topics);
		}

	}

	private void produceTweetsToKafka(BlockingQueue<String> msgQueue, Client client,
			KafkaProducer<String, String> producer, List<String> topics) {
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
			}

		}

	}

	public void deactivate(String topic) {
		if (client != null) {
			if (topics.contains(topic)) {
				this.active = false;
				topics.remove(topic);
				client.stop();
				client = null;
				this.active = true;
				send(topics);
			}
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
			topics = new ArrayList<String>();
			client.stop();
			client = null;
		}

	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
