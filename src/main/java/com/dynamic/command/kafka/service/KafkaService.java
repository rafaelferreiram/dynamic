package com.dynamic.command.kafka.service;

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
import com.dynamic.command.twitter.TwitterClient;
import com.twitter.hbc.core.Client;

@Component
public class KafkaService {
	
	private Logger logger = LoggerFactory.getLogger(KafkaService.class.getName());
	
	@Autowired
	private TwitterClient twitterClient;
	
	@Autowired
	private KafkaProducerConfig kafkaProducerConfig;
	
	@Value("${kafka.topic}")
	private String kafkaTopic;
	
	private boolean active = true;

	public void send(String topic) {
		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
		Client client = twitterClient.createTwitterClient(msgQueue, topic);
		client.connect();
		logger.info("Connected to Twitter client.");

		KafkaProducer<String, String> producer = kafkaProducerConfig.createKafkaProducer();

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
				producer.send(new ProducerRecord<String, String>(kafkaTopic, null, msg), new Callback() {

					public void onCompletion(RecordMetadata metadata, Exception exception) {
						if (exception != null) {
							logger.error("Error while sendind data.", exception);
						}
					}
				});
			}
		}
		logger.info("End of application.");
		
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}


}
