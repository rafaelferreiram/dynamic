package com.dynamic.command.kafka.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class KafkaProducerConfig {
	
	private Logger logger = LoggerFactory.getLogger(KafkaProducerConfig.class.getName());
	
	@Value("${kafka.bootstrapServer}")
	private String bootstrapServer;

	@Value("${kafka.compressionType}")
	private String compressionType;
	
	@Value("${kafka.acks}")
	private String acks;

	public KafkaProducer<String, String> createKafkaProducer() {
		logger.info("Setting Kafka Producer Properties.");
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		// create safe
		properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
		properties.setProperty(ProducerConfig.ACKS_CONFIG, acks);
		properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
		properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

		// high throughput producer
		properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
		properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "15");
		properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024)); //32 BK batch size

		// create Producer
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
		logger.info("Setting Kafka Producer created.");
		return producer;
	}
}
