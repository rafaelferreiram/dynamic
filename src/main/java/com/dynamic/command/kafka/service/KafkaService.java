package com.dynamic.command.kafka.service;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dynamic.command.twitter.TwitterClient;
import com.twitter.hbc.core.Client;

@Component
public class KafkaService {
	
	private Logger logger = LoggerFactory.getLogger(KafkaService.class.getName());
	
	@Autowired
	private TwitterClient twitterClient;
	
	@Value("${kafka.bootstrapServer}")
	private String bootstrapServer;
	
	@Value("${kafka.compressionType}")
	private String compressionType;

	public void send(String topic) {
		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
		Client client = twitterClient.createTwitterClient(msgQueue, topic);
		
		client.connect();

		// kafka producer
		KafkaProducer<String, String> producer = createKafkaProducer();

		// send tweets to kafka
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
				producer.send(new ProducerRecord<String, String>("twitter_tweets", null, msg), new Callback() {

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

	private KafkaProducer<String, String> createKafkaProducer() {
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		// create safe
		properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
		properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
		properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
		properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

		// high throughput producer
		properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
		properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "15");
		properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024)); //32 BK batch size

		// create Producer
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
		return producer;
	}

}
