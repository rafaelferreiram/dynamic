package com.dynamic.command.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dynamic.command.kafka.producer.dto.request.TopicsListRequestDTO;
import com.dynamic.command.kafka.producer.dto.response.KafkaServerErrorResponseDTO;
import com.dynamic.command.kafka.producer.dto.response.TopicErrorResponseDTO;
import com.dynamic.command.kafka.producer.dto.response.TopicResponseDTO;
import com.dynamic.command.kafka.producer.dto.response.TopicsListResponseDTO;
import com.dynamic.command.kafka.producer.dto.response.TweetTopicResponse;
import com.dynamic.command.kafka.service.KafkaService;
import com.dynamic.command.kafka.service.KafkaServiceAsync;
import com.dynamic.command.mongo.service.MongoService;

@RestController
@RequestMapping("/twitter")
@CrossOrigin(origins = "*")
public class TwitterController {

	private KafkaServiceAsync kafkaServiceAsync;

	private KafkaService kafkaService;

	private MongoService mongoService;

	@Value("${twitter.topic.deactive}")
	String inactive;

	@Value("${twitter.topic.active}")
	String active;

	@GetMapping(value = "/status")
	public ResponseEntity<String> isWorking() {
		return ResponseEntity.ok("Is Working...");
	}

	@GetMapping(value = "/tweets/{topic}")
	public ResponseEntity<Object> sendTopicToKafka(@PathVariable(required = true) final String topic) {
		try {
			if (kafkaService.isKafkaIsOn()) {
				kafkaServiceAsync.send(topic);
				return ResponseEntity.ok().body(new TopicResponseDTO(topic, active, String.format("Topic %s sent will be consumed from tweets on real time", topic.toUpperCase())));
			}
			return ResponseEntity.badRequest().body(new KafkaServerErrorResponseDTO(Boolean.FALSE,"Kafka server is OFFLINE."));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new TopicErrorResponseDTO(topic, String.format("Error while sending topic to kafka [%]", e.getMessage())));
		}
	}

	@PostMapping(value = "/tweets/")
	public ResponseEntity<Object> sendListOfTopicsToKafka(@RequestBody(required = true) TopicsListRequestDTO topic) {
		try {
			if (topic.getTopics().isEmpty()) {
				return ResponseEntity.badRequest().body(new TopicErrorResponseDTO("List of topics cannot be empty."));
			}
			if (kafkaService.isKafkaIsOn()) {
				kafkaServiceAsync.send(topic.getTopics());
				return ResponseEntity.ok().body(new TopicsListResponseDTO(topic.getTopics(), active, String.format("Topics ' %s ' sent will be consumed from tweets on real time", topic.getTopics().toString().toUpperCase())));
			}
			return ResponseEntity.badRequest().body(new KafkaServerErrorResponseDTO(Boolean.FALSE,"Kafka server is OFFLINE."));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new TopicErrorResponseDTO("Error while sending topic to kafka"));
		}
	}

	@GetMapping(value = "/tweets/deactivate/{topic}")
	public ResponseEntity<Object> deactivateTopic(@PathVariable(required = true) final String topic) {
		try {
			if (!kafkaService.isKafkaIsOn()) {
				return ResponseEntity.badRequest().body(new KafkaServerErrorResponseDTO(Boolean.FALSE,"Kafka server is OFFLINE."));
			}
			if (kafkaServiceAsync.deactivate(topic)) {
				kafkaServiceAsync.closeConnectionClient(topic);
				return ResponseEntity.ok().body(new TopicResponseDTO(topic, inactive, String.format("Topic ' %s ' sent will be deactivade from Tweets Kafka Producer.",  topic.toUpperCase())));
			}
			return ResponseEntity.badRequest().body(new TopicResponseDTO(topic, inactive, String.format("Topic ' %s ' is not active on Kafka Producer.",  topic.toUpperCase())));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new TopicErrorResponseDTO("Error while deactivating."));
		}
	}

	@GetMapping(value = "/tweets/")
	public ResponseEntity<Object> getTweetTopics() {
		if (!kafkaService.isKafkaIsOn()) {
			return ResponseEntity.badRequest().body(new KafkaServerErrorResponseDTO(Boolean.FALSE,"Kafka server is OFFLINE."));
		}
		List<TweetTopicResponse> allTopics = mongoService.findAllTopics();
		if (allTopics.isEmpty()) {
			return ResponseEntity.badRequest().body(new TopicErrorResponseDTO("No Tweet Topics found."));
		}
		return ResponseEntity.ok().body(allTopics);
	}

	@GetMapping(value = "/tweets/actives")
	public ResponseEntity<Object> getActivesTweetTopics() {
		if (!kafkaService.isKafkaIsOn()) {
			return ResponseEntity.badRequest().body(new KafkaServerErrorResponseDTO(Boolean.FALSE,"Kafka server is OFFLINE."));
		}
		List<TweetTopicResponse> activeTopics = mongoService.findActiveTopics();
		if (activeTopics.isEmpty()) {
			return ResponseEntity.badRequest().body(new TopicErrorResponseDTO("No Active Tweet Topics found."));
		}
		return ResponseEntity.ok().body(activeTopics);
	}

	@Autowired
	public TwitterController(KafkaServiceAsync kafkaServiceAsync, KafkaService kafkaService, MongoService mongoService) {
		this.kafkaServiceAsync = kafkaServiceAsync;
		this.kafkaService = kafkaService;
		this.mongoService = mongoService;
	}

}
